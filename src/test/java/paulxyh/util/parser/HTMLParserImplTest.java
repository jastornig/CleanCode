package paulxyh.util.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.Heading;
import paulxyh.model.Link;
import paulxyh.model.PageElement;
import paulxyh.util.LinkUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("HTMLParserImpl Tests")
public class HTMLParserImplTest {
    private String parseUrl = "http://paulxyh.test.url";
    private HTMLParserImpl parser;
    private Document document;

    @BeforeEach
    void init() {
        parser = new HTMLParserImpl();
        document = mock(Document.class);
    }

    @Test
    void testParseShouldParseHeadingCorrectly() throws Exception, ElementNotRecognizedException {
        Element heading = new Element("h2");
        heading.text("Test");
        Elements elements = new Elements(heading);
        mockDocumentBodyCalls(elements);

        List<PageElement> result = parser.parse(parseUrl, document);

        assertEquals(1, result.size());
        assertInstanceOf(Heading.class, result.getFirst());

        Heading parsedHeading = (Heading) result.getFirst();
        assertEquals(2, parsedHeading.level());
        assertEquals("Test", parsedHeading.title());
    }

    @Test
    void testParseShouldParseLinkCorrectly() throws ElementNotRecognizedException {
        String validUrl = "http://paulxyh.test.valid.url";
        Element link = new Element("a");
        link.attr("href", validUrl);
        Elements elements = new Elements(link);

        mockDocumentBodyCalls(elements);

        try (MockedStatic<LinkUtils> utilities = mockStatic(LinkUtils.class)) {
            utilities.when(() -> LinkUtils.isLinkValid(validUrl)).thenReturn(true);

            List<PageElement> result = parser.parse(parseUrl, document);

            assertEquals(1, result.size());
            assertInstanceOf(Link.class, result.getFirst());

            Link parsedLink = (Link) result.getFirst();
            assertEquals(validUrl, parsedLink.url());
            assertTrue(parsedLink.isValid());
        }
    }

    @Test
    void testParseShouldParseBrokenLinkCorrectly() throws ElementNotRecognizedException {
        String brokenUrl = "http://paulxyh.test.broken.url";
        Element link = new Element("a");
        link.attr("href", brokenUrl);
        Elements elements = new Elements(link);

        mockDocumentBodyCalls(elements);

        try (MockedStatic<LinkUtils> utilities = mockStatic(LinkUtils.class)) {
            utilities.when(() -> LinkUtils.isLinkValid(brokenUrl)).thenReturn(false);

            List<PageElement> result = parser.parse(parseUrl, document);

            assertEquals(1, result.size());
            assertInstanceOf(Link.class, result.getFirst());

            Link parsedLink = (Link) result.getFirst();
            assertEquals(brokenUrl, parsedLink.url());
            assertFalse(parsedLink.isValid());
        }
    }

    @Test
    void testParseCachesLinkValidation() throws ElementNotRecognizedException {
        String url = "http://paulxyh.test.cache.url";
        Element link1 = new Element("a");
        link1.attr("href", url);

        Element link2 = new Element("a");
        link2.attr("href", url);

        Elements elements = new Elements(link1, link2);

        mockDocumentBodyCalls(elements);

        try (MockedStatic<LinkUtils> utilities = mockStatic(LinkUtils.class)) {
            utilities.when(() -> LinkUtils.isLinkValid(url)).thenReturn(true);

            parser.parse(parseUrl, document);

            // verify static method called only once
            utilities.verify(() -> LinkUtils.isLinkValid(url), times(1));
        }
    }

    @Test
    void testParseThrowsElementNotRecognizedException() {
        Element unknown = new Element("div", "Some Div");
        Elements elements = new Elements(unknown);

        mockDocumentBodyCalls(elements);

        assertThrows(ElementNotRecognizedException.class, () -> {
            parser.parse(parseUrl, document);
        });
    }

    private void mockDocumentBodyCalls(Elements elements) {
        Element mockBody = mock(Element.class);
        when(document.body()).thenReturn(mockBody);
        when(document.body().select("h1, h2, h3, h4, h5, h6, a[href]")).thenReturn(elements);
    }
}
