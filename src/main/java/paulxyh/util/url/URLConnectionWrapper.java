package paulxyh.util.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public interface URLConnectionWrapper {
    HttpURLConnection openConnection(URL url) throws IOException;
}
