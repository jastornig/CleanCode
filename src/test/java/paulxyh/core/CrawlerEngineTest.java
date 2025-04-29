package paulxyh.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.Heading;
import paulxyh.model.Link;
import paulxyh.model.PageResult;
import paulxyh.util.fetcher.HTMLContentFetcher;
import paulxyh.util.parser.HTMLParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@DisplayName("CrawlerEngine Tests")
@ExtendWith(MockitoExtension.class)
public class CrawlerEngineTest {

    @InjectMocks
    private CrawlerEngine engine;
    @Mock
    private HTMLParser parser;
    @Mock
    private HTMLContentFetcher fetcher;

    @BeforeEach
    void setup() {
        engine = new CrawlerEngine(parser, fetcher);
    }

    @Test
    @DisplayName("crawl() should return empty PageResult for depth 0")
    void testCrawlReturnsEmptyPageResult() {
        PageResult result = engine.crawl("testUrl", 0);
        assertNull(result);
    }

    @Test
    @DisplayName("crawl() should return null for url with no content")
    void testCrawlReturnsEmptyPageResultForEmptyWebsite() {
        String url = "https://paulxyh.test.url";
        when(fetcher.fetch(url)).thenReturn(null);
        PageResult result = engine.crawl(url, 1);
        assertNull(result);
    }

    @Test
    @DisplayName("crawl() should return correct PageResult for HTML with 1 Heading and 1 Link")
    void testCrawlReturnsCorrectPageResult() throws ElementNotRecognizedException {
        String url = "https://paulxyh.test.url";
        String html = """
                <h1>TestHeading</h1>
                <a>https://paulxyh.test.url</a>
                """;
        Document mockedDocument = Jsoup.parse(html);
        Heading mockedHeading = new Heading(1, "TestHeading");
        Link mockedLink = new Link(url, true);
        when(fetcher.fetch(url)).thenReturn(mockedDocument);
        when(parser.parse(url, mockedDocument)).thenReturn(List.of(mockedHeading, mockedLink));
        PageResult result = engine.crawl(url, 1);
        assertEquals(url, result.getUrl());
        assertEquals(1, result.getDepth());
        assertEquals(2, result.getElements().size());
        assertEquals(0, result.getChildren().size());
        assertEquals(mockedHeading, result.getElements().getFirst());
        assertEquals(mockedLink, result.getElements().get(1));
    }

    @Test
    @DisplayName("crawl() should return correct PageResult for depth 2")
    void testCrawlReturnsCorrectPageResultForDepthTwo() throws ElementNotRecognizedException {
        String url = "https://paulxyh.test.url";
        String html = """
                <h1>TestHeading</h1>
                <a>https://paulxyh.test.url.deeper</a>
                """;
        String urlDeeper = "https://paulxyh.test.url.deeper";
        String htmlDeeper = """
                <h1>TestHeadingDeeper</h1>
                <a>https://paulxyh.test.url</a>
                """;

        Document mockedDocument = Jsoup.parse(html);
        Heading mockedHeading = new Heading(1, "TestHeading");
        Link mockedLink = new Link(urlDeeper, true);
        when(fetcher.fetch(url)).thenReturn(mockedDocument);
        when(parser.parse(url, mockedDocument)).thenReturn(List.of(mockedHeading, mockedLink));


        Document mockedDocumentDeeper = Jsoup.parse(htmlDeeper);
        Heading mockedHeadingDeeper = new Heading(1, "TestHeadingDeeper");
        Link mockedLinkDeeper = new Link(url, true);
        when(fetcher.fetch(urlDeeper)).thenReturn(mockedDocumentDeeper);
        when(parser.parse(urlDeeper, mockedDocumentDeeper)).thenReturn(List.of(mockedHeadingDeeper, mockedLinkDeeper));

        PageResult result = engine.crawl(url, 2);
        assertEquals(url, result.getUrl());
        assertEquals(1, result.getDepth());
        assertEquals(2, result.getElements().size());
        assertEquals(1, result.getChildren().size());
        assertEquals(mockedHeading, result.getElements().getFirst());
        assertEquals(mockedLink, result.getElements().get(1));
        assertEquals(2, result.getChildren().getFirst().getElements().size());
        assertEquals(urlDeeper, result.getChildren().getFirst().getUrl());
    }
}
