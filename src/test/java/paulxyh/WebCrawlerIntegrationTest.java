package paulxyh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paulxyh.controller.CrawlerController;
import paulxyh.core.CrawlerEngine;
import paulxyh.util.parser.HTMLParserImpl;
import paulxyh.util.writer.MarkdownWriterImpl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@DisplayName("WebCrawler Integration Tests")
public class WebCrawlerIntegrationTest {
    private static final Path OUTPUT_PATH = Paths.get("crawler_report.md");
    private static final Path EXPECTED_CONTENT_PATH = Paths.get("src/test/resources/expected_output.md");
    @BeforeEach
    void cleanOutput() throws IOException {
        Files.deleteIfExists(OUTPUT_PATH);
    }

    @Test
    @DisplayName("Checks if the complete process works")
    void testCrawlerCreatesExpectedOutput() throws Exception {
        URI uri = Paths.get("src/test/resources/testpage/index.html").toAbsolutePath().toUri();
        String startUrl = uri.toString();
        List<String> allowedDomains = List.of("file:/");

        CrawlerController controller = new CrawlerController(new CrawlerEngine(new HTMLParserImpl()), new MarkdownWriterImpl());
        controller.run(startUrl, 3, allowedDomains);

        assertTrue(Files.exists(OUTPUT_PATH), "Report file should be generated.");

        String content = Files.readString(OUTPUT_PATH);
        String expectedContent = Files.readString(EXPECTED_CONTENT_PATH);
        assertEquals(expectedContent.trim(), content.trim());
    }
}
