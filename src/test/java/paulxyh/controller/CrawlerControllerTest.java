package paulxyh.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paulxyh.core.CrawlerEngine;
import paulxyh.model.PageResult;
import paulxyh.util.writer.MarkdownWriterImpl;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrawlerController Tests")
public class CrawlerControllerTest {
    private final String testUrl = "https://paulxyh.test.url";
    @Mock
    CrawlerEngine engine;
    @Mock
    MarkdownWriterImpl writer;

    @InjectMocks
    CrawlerController controller;

    @BeforeEach
    void init(){
        controller = new CrawlerController(engine, writer);
    }
    @Test
    @DisplayName("Verify engine.start() and writer.write() are called")
    void testRunCallsEngineAndWriter(){
        PageResult mockResult = mock(PageResult.class);
        when(engine.crawl(testUrl, 1)).thenReturn(mockResult);

        controller.run(testUrl, 1, List.of("paulxyh", "test", "url"));

        verify(engine).crawl(testUrl, 1);
        verify(writer).write(mockResult, "crawler_report.md");
    }
}
