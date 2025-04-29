package paulxyh.util.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLConnectionWrapperImpl implements URLConnectionWrapper {
    @Override
    public HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
