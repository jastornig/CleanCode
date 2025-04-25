package paulxyh.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paulxyh.model.Heading;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ReportBuilder Tests")
public class ReportBuilderTest {
    private final String testUrl = "https://paulxyh.test.url";
    private ReportBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ReportBuilder();
    }

    @Test
    @DisplayName("buildInputUrlText() should wrap URL in <a> tags")
    void buildInputUrlTextReturnsFormattedText() {
        String result = builder.buildInputUrlText(this.testUrl);
        String expected = "input: <a>" + this.testUrl + "</a>\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("buildDepthText() should return formatted depth line with <br>")
    void buildDepthTextReturnsFormattedDepth() {
        String result = builder.buildDepthText(2);
        String expected = "<br>depth: 2\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("buildHeadingText() should return properly prefixed heading line")
    void buildHeadingTextReturnsFormattedHeading() {
        Heading heading = new Heading(2, "Chapter");
        String result = builder.buildHeadingText(heading, 1);
        String expected = "## Chapter\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("buildLinkText() should return a formatted link line")
    void buildLinkTextReturnsFormattedLink() {
        String result = builder.buildLinkText(this.testUrl, 2);
        String expected = "<br>----> link to <a>" + this.testUrl + "</a>\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("buildBrokenLinkText() should return a formatted broken link line")
    void buildBrokenLinkTextReturnsFormattedBrokenLink() {
        String result = builder.buildBrokenLinkText(this.testUrl, 1);
        String expected = "<br>--> broken link <a>" + this.testUrl + "</a>\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("addDepthIntent() should return empty string for depth 0")
    void addDepthIntentReturnsEmptyForZeroDepth() {
        String result = invokeAddDepthIntent(0);
        assertEquals("", result);
    }

    // see Visibility and Testing Disclaimer in README
    private String invokeAddDepthIntent(int depth) {
        try {
            var method = ReportBuilder.class.getDeclaredMethod("addDepthIntent", int.class);
            method.setAccessible(true);
            return (String) method.invoke(builder, depth);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
