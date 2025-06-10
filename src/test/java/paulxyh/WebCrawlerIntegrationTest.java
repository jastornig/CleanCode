package paulxyh;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paulxyh.controller.CrawlerController;
import paulxyh.core.CrawlTaskExecutor;
import paulxyh.core.CrawlerEngine;
import paulxyh.util.fetcher.HTMLContentFetcher;
import paulxyh.util.fetcher.HTMLContentFetcherImpl;
import paulxyh.util.fetcher.JsoupWrapper;
import paulxyh.util.fetcher.JsoupWrapperImpl;
import paulxyh.util.logger.Logger;
import paulxyh.util.parser.HTMLParser;
import paulxyh.util.parser.HTMLParserImpl;
import paulxyh.util.writer.MarkdownWriter;
import paulxyh.util.writer.MarkdownWriterImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("WebCrawler Integration Tests")
public class WebCrawlerIntegrationTest {
    private static final Path OUTPUT_PATH = Paths.get("crawler_report.md");
    private static final Path EXPECTED_CONTENT_PATH = Paths.get("src/test/resources/expected_output.md");

    @BeforeEach
    void cleanOutput() throws IOException {
        Files.deleteIfExists(OUTPUT_PATH);
        generateExpectedOutput();
    }

    @AfterEach
    void deleteExpected() throws IOException {
        deleteExpectedOutput();
    }

    @Test
    @DisplayName("Checks if the complete process works")
    void testCrawlerCreatesExpectedOutput() throws Exception {
        URI uri = Paths.get("src/test/resources/testpage/index.html").toAbsolutePath().toUri();
        String startUrl = uri.toString();
        List<String> allowedDomains = List.of("file:/");
        Logger.setLogLevel(Logger.Level.DEBUG);
        JsoupWrapper wrapper = new JsoupWrapperImpl();
        HTMLContentFetcher fetcher = new HTMLContentFetcherImpl(wrapper);
        CrawlTaskExecutor executor = new CrawlTaskExecutor(10);
        HTMLParser parser = new HTMLParserImpl();
        CrawlerEngine engine = new CrawlerEngine(fetcher, parser, executor, 3);
        MarkdownWriter writer = new MarkdownWriterImpl();
        CrawlerController crawler = new CrawlerController(engine, writer);
        crawler.run(startUrl, allowedDomains);

        // to make the test independent of local file paths
        String prefix = "input: <a>" + uri.toString() + "</a>\n";

        assertTrue(Files.exists(OUTPUT_PATH), "Report file should be generated.");
        String expectedContent = prefix + Files.readString(EXPECTED_CONTENT_PATH);
        String content = Files.readString(OUTPUT_PATH);
        assertEquals(expectedContent.trim(), content.trim());
    }

    void generateExpectedOutput() throws IOException {
        File expectedFile = EXPECTED_CONTENT_PATH.toFile();
        String indexHtml = "file://" + Paths.get("src/test/resources/testpage/index.html").toAbsolutePath().toString();
        String loop1Html = "file:" + Paths.get("src/test/resources/testpage/loop1.html").toAbsolutePath().toString();
        String loop2Html = "file:" + Paths.get("src/test/resources/testpage/loop2.html").toAbsolutePath().toString();
        String page2Html = "file:" + Paths.get("src/test/resources/testpage/page2.html").toAbsolutePath().toString();
        String page3Html = "file:" + Paths.get("src/test/resources/testpage/page3.html").toAbsolutePath().toString();
        String page4Html = "file:" + Paths.get("src/test/resources/testpage/page4.html").toAbsolutePath().toString();
        String deadendHtml = "file:" + Paths.get("src/test/resources/testpage/deadend.html").toAbsolutePath().toString();

        try(BufferedWriter writer = Files.newBufferedWriter(expectedFile.toPath())) {
            writer.write("<br>depth: 1\n");
            writer.write("<br>-----start of page <a>%s</a>%n".formatted(indexHtml));
            writer.write("# Heading 1\n");
            writer.write("<br>--> link to <a>%s</a>%n".formatted(page2Html));
            writer.write("<br>depth: 2\n");
            writer.write("<br>-----start of page <a>%s</a>%n".formatted(page2Html));
            writer.write("## --> Heading 2\n");
            writer.write("<br>----> link to <a>%s</a>%n".formatted(page3Html));
            writer.write("<br>depth: 3\n");
            writer.write("<br>-----start of page <a>%s</a>%n".formatted(page3Html));
            writer.write("### ----> Heading 3\n");
            writer.write("<br>------> link to <a>%s</a>%n".formatted(page4Html));
            writer.write("<br>depth: 4\n");
            writer.write("#### ----> Heading 3.1\n");
            writer.write("<br>------> broken link <a>file:/Users/test/document.txt</a>\n");
            writer.write("<br>-----end of page <a>%s</a>%n".formatted(page3Html));
            writer.write("<br>----> link to <a>mailto:k.paulxyh@gmail.com</a>\n");
            writer.write("<br>depth: 3\n");
            writer.write("<br>-----end of page <a>%s</a>%n".formatted(page2Html));
            writer.write("<br>--> link to <a>%s</a>%n".formatted(loop1Html));
            writer.write("<br>depth: 2\n");
            writer.write("<br>-----start of page <a>%s</a>%n".formatted(loop1Html));
            writer.write("# --> Loop Back\n");
            writer.write("<br>----> link to <a>%s</a>%n".formatted(loop2Html));
            writer.write("<br>depth: 3\n");
            writer.write("<br>-----start of page <a>%s</a>%n".formatted(loop2Html));
            writer.write("# ----> Loop Start\n");
            writer.write("<br>------> link to <a>%s</a>%n".formatted(loop1Html));
            writer.write("<br>depth: 4\n");
            writer.write("<br>-----end of page <a>%s</a>%n".formatted(loop2Html));
            writer.write("<br>-----end of page <a>%s</a>%n".formatted(loop1Html));
            writer.write("<br>--> link to <a>%s</a>%n".formatted(deadendHtml));
            writer.write("<br>depth: 2\n");
            writer.write("<br>-----start of page <a>%s</a>%n".formatted(deadendHtml));
            writer.write("# --> Ende\n");
            writer.write("<br>-----end of page <a>%s</a>%n".formatted(deadendHtml));
            writer.write("###### Abschluss Heading\n");
            writer.write("<br>-----end of page <a>%s</a>%n".formatted(indexHtml));
        }catch (IOException e){
            throw new IOException("Error while generating expected output file: " + e.getMessage(), e);
        }
    }

    void deleteExpectedOutput() throws IOException {
        Files.deleteIfExists(EXPECTED_CONTENT_PATH);
    }
}
