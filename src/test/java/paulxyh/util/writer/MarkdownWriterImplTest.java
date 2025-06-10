package paulxyh.util.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import paulxyh.exception.WriterNotInitializedException;
import paulxyh.model.Heading;
import paulxyh.model.Link;
import paulxyh.model.PageResult;
import paulxyh.util.ReportBuilder;

import java.io.BufferedWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("MarkdownWriterImpl Tests")
public class MarkdownWriterImplTest {
    @Mock
    private BufferedWriter bufferedWriter;
    @Mock
    private ReportBuilder builder;
    private MarkdownWriterImpl writer;

    @BeforeEach
    void init() throws IOException {
        MockitoAnnotations.openMocks(this);
        writer = new MarkdownWriterImpl();
        writer.setWriter(bufferedWriter);
        writer.setBuilder(builder);
        doNothing().when(bufferedWriter).write(anyString());
    }

    @Test
    @DisplayName("write() should write a heading")
    void testWriteWithHeading() {
        Heading heading = new Heading(1, "test");
        PageResult result = new PageResult("paulxyh.test", 1);
        result.addElement(heading);
        when(builder.buildHeadingText(heading, 1)).thenReturn("");
        mockInitialReportMethods();
        writer.write(result, "test.md");
        verify(builder, times(1)).buildHeadingText(heading, 1);
    }

    @Test
    @DisplayName("write() should write a link")
    void testWriteWithLink() {
        String url = "http://paulxyh.test.url";
        Link link = new Link(url, true);
        PageResult result = new PageResult(url, 1);
        result.addElement(link);
        when(builder.buildLinkText(url, 1)).thenReturn("");
        mockInitialReportMethods();
        writer.write(result, "test.md");
        verify(builder, times(1)).buildLinkText(url, 1);
    }

    @Test
    @DisplayName("write() should write a broken link")
    void testWriteWithBrokenLink() {
        String url = "http://paulxyh.test.broken.url";
        Link link = new Link(url, false);
        PageResult result = new PageResult(url, 1);
        result.addElement(link);
        when(builder.buildBrokenLinkText(url, 1)).thenReturn("");
        mockInitialReportMethods();
        writer.write(result, "test.md");
        verify(builder, times(1)).buildBrokenLinkText(url, 1);
    }

    @Test
    @DisplayName("writeRecursively() should skip writing initial details on depth 2")
    void testWriteRecursivelySkipsInitialDetailsOnDepthTwo() throws WriterNotInitializedException {
        Link link = new Link("http://test.url", true);
        PageResult result = new PageResult("http://paulxyh.test.url", 2);
        result.addElement(link);
        when(builder.buildLinkText("http://test.url", 2)).thenReturn("");
        when(builder.buildDepthText(anyInt())).thenReturn("");
        writer.writeRecursively(result, 2);
        verify(builder, times(0)).buildInputUrlText(anyString());
        verify(builder, times(1)).buildDepthText(anyInt());
    }

    private void mockInitialReportMethods() {
        when(builder.buildInputUrlText(anyString())).thenReturn("");
        when(builder.buildDepthText(anyInt())).thenReturn("");
    }
}
