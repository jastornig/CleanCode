package paulxyh.util.fetcher;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HTMLContentFetcher Tests")
public class HTMLContentFetcherTest {
    private final String TEST_URL_SAFE = "https://paulxyh.test.url";
    private final String TEST_URL_UNSAFE = "http://paulxyh.test.url";
    private Document mockDocument;
    @Mock
    private JsoupWrapper wrapper;
    @InjectMocks
    private HTMLContentFetcherImpl fetcher;

    @BeforeEach
    void init() {
        fetcher = new HTMLContentFetcherImpl(wrapper);
        mockDocument = mock(Document.class);
    }

    @Test
    @DisplayName("fetch() should return a document from https://paulxyh.test.url")
    void testFetchReturnsDocumentFromSafeWebsite() throws IOException {
        when(wrapper.connect(anyString())).thenReturn(mockDocument);
        assertFalse(this.fetcher.fetch(TEST_URL_SAFE).isEmpty());
        verify(wrapper, times(1)).connect(TEST_URL_SAFE);
    }

    @Test
    @DisplayName("fetch() should return a document from http://paulxyh.test.url")
    void testFetchReturnsDocumentFromUnsafeWebsite() throws IOException {
        when(wrapper.connect(anyString())).thenReturn(mockDocument);
        assertFalse(fetcher.fetch(TEST_URL_UNSAFE).isEmpty());
        verify(wrapper, times(1)).connect(TEST_URL_UNSAFE);
    }

    @Test
    @DisplayName("fetch() should return a document from file-url")
    void testFetchReturnsDocumentFromFileUrl() throws IOException {
        URI uri = Paths.get("src/test/resources/testpage/index.html").toAbsolutePath().toUri();
        String startUrl = uri.toString();
        when(wrapper.parse(any(), anyString(), anyString())).thenReturn(mockDocument);
        assertFalse(fetcher.fetch(startUrl).isEmpty());
        verify(wrapper, times(1)).parse(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("fetch() should return Optional.empty() for ftp:// url")
    void testFetchReturnsNullForFTPUrl() {
        assertTrue(fetcher.fetch("ftp://test.url.return.null").isEmpty());
    }

    @Test
    @DisplayName("fetch() should return Optional.empty() for mailto: url")
    void testFetchReturnsNullForMailtoUrl() {
        assertTrue(fetcher.fetch("mailto:k.paulxyh@gmail.com").isEmpty());
    }

}
