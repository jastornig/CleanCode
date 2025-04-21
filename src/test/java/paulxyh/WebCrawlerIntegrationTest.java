package paulxyh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paulxyh.controller.CrawlerController;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebCrawlerIntegrationTest {
    private static final Path OUTPUT_PATH = Paths.get("crawler_report.md");
    private static final Path EXPECTED_CONTENT_PATH = Paths.get("src/test/resources/expected_output.md");
    @BeforeEach
    void cleanOutput() throws IOException {
        Files.deleteIfExists(OUTPUT_PATH);
    }

    @Test
    void testCrawlerCreatesExpectedOutput() throws Exception {
        // Pfad zur lokalen Testdatei
        URI uri = Paths.get("src/test/resources/testpage/index.html").toAbsolutePath().toUri();
        String startUrl = uri.toString(); // file:///...
        // Expected: file:// als erlaubte "Domain"
        List<String> allowedDomains = List.of("file:/");

        // Starte den Crawler (je nach Signatur deiner Methode)
        CrawlerController controller = new CrawlerController();
        controller.run(startUrl, 3, allowedDomains);

        // Überprüfe, dass die Datei erzeugt wurde
        assertTrue(Files.exists(OUTPUT_PATH), "Report file should be generated.");

        // Lies den Inhalt und überprüfe auf erwarteten Text
        String content = Files.readString(OUTPUT_PATH);

        String expectedContent = Files.readString(EXPECTED_CONTENT_PATH);

        assertEquals(expectedContent.trim(), content.trim());
    }
}
