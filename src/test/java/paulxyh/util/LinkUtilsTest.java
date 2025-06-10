package paulxyh.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import paulxyh.exception.IncorrectInputException;
import paulxyh.util.url.URLConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static paulxyh.util.LinkUtils.isLinkValid;

@DisplayName("LinkUtils Tests")
public class LinkUtilsTest {
    @Mock
    private URLConnectionWrapper urlConnectionWrapperMock;

    @Mock
    private HttpURLConnection httpURLConnectionMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        LinkUtils.setAllowedDomains(Arrays.asList("paulxyh.test", "test.url"));
        LinkUtils.setConnectionWrapper(urlConnectionWrapperMock);
    }

    @Test
    @DisplayName("isLinkValid() checks links only once")
    public void testIsLinkValidAlreadyCheckedLink() throws IOException {
        String link = "https://paulxyh.test.url";
        when(urlConnectionWrapperMock.openConnection(any())).thenReturn(httpURLConnectionMock);
        when(httpURLConnectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        LinkUtils.isLinkValid(link);
        boolean result = LinkUtils.isLinkValid(link);

        assertTrue(result);
        verify(urlConnectionWrapperMock, times(1)).openConnection(any());
    }

    @Test
    @DisplayName("isLinkValid() returns true for valid http link")
    public void testIsLinkValidValidHttpLink() throws IOException {
        String link = "http://paulxyh.test.url";
        when(urlConnectionWrapperMock.openConnection(any())).thenReturn(httpURLConnectionMock);
        when(httpURLConnectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        boolean result = LinkUtils.isLinkValid(link);

        assertTrue(result);
        verify(urlConnectionWrapperMock, times(1)).openConnection(any());
    }

    @Test
    @DisplayName("isLinkValid() returns false for invalid http link")
    public void testIsLinkValidInvalidHttpLink() throws IOException {
        String link = "http://paulxyh.test.invalid.url";

        when(urlConnectionWrapperMock.openConnection(any())).thenReturn(httpURLConnectionMock);
        when(httpURLConnectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        boolean result = LinkUtils.isLinkValid(link);

        assertFalse(result);
        verify(urlConnectionWrapperMock, times(1)).openConnection(any());
    }

    @Test
    @DisplayName("isLinkValid() returns true for mailto link")
    public void testIsLinkValidMailto() {
        String link = "mailto:examplemail@test.com";
        boolean result = LinkUtils.isLinkValid(link);
        assertTrue(result);
    }

    @Test
    @DisplayName("isLinkValid() returns true for sms link")
    public void testIsLinkValidSMS() {
        String link = "sms:exampleNumber";
        boolean result = LinkUtils.isLinkValid(link);
        assertTrue(result);
    }

    @Test
    @DisplayName("isLinkValid() returns true for tel link")
    public void testIsLinkValidTel() {
        String link = "tel:exampleNumber";
        boolean result = LinkUtils.isLinkValid(link);
        assertTrue(result);
    }

    @Test
    @DisplayName("isLinkValid() returns true for existing file")
    public void testIsLinkValidFileScheme() {
        URI uri = Paths.get("src/test/resources/testpage/index.html").toAbsolutePath().toUri();
        String startUrl = uri.toString();
        LinkUtils.setAllowedDomains(List.of("file:"));
        boolean result = isLinkValid(startUrl);
        assertTrue(result);
    }

    @Test
    @DisplayName("isLinkValid() returns false for non existing file")
    public void testIsLinkValidInvalidFileScheme() {
        String invalidUrl = "file:///wrong/path";
        LinkUtils.setAllowedDomains(List.of("file:"));
        boolean result = isLinkValid(invalidUrl);
        assertFalse(result);
    }

    @Test
    @DisplayName("isLinkValid() returns false for url with no valid domain")
    public void testCheckDomainInvalidDomain() {
        String link = "https://wrong.domain.url";
        boolean result = isLinkValid(link);
        assertFalse(result);
    }

    @Test
    public void testCheckUrlFormatting_validUrl() {
        String url = "https://paulxyh.test.url";
        assertDoesNotThrow(() -> LinkUtils.checkUrlFormatting(url));
    }

    @Test
    @DisplayName("checkUrlFormatting() returns false for non http* links")
    public void testCheckUrlFormatting_invalidUrl() {
        String invalidUrl = "ftp://example.com";
        assertThrows(IncorrectInputException.class, () -> LinkUtils.checkUrlFormatting(invalidUrl));
    }

    @Test
    @DisplayName("normalizeLinkAndRemoveFragment() should return normalized URL without fragment")
    public void testNormalizeLinkAndRemoveFragment() {
        String urlWithFragment = "https://paulxyh.test.url#section1";
        String normalizedUrl = LinkUtils.normalizeLinkAndRemoveFragment(urlWithFragment);
        assertEquals("https://paulxyh.test.url", normalizedUrl);
    }
}
