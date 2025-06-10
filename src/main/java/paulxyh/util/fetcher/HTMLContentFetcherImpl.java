package paulxyh.util.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import paulxyh.util.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class HTMLContentFetcherImpl implements HTMLContentFetcher {
    private final JsoupWrapper wrapper;
    private final String CHARSET = "UTF-8";

    public HTMLContentFetcherImpl(JsoupWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public Optional<Document> fetch(String url) {
        try {
            if (url.startsWith("file:")) {
                Logger.info("Fetching from local file: " + url);
                File file = new File(URI.create(url));
                return Optional.of(wrapper.parse(file, CHARSET, url));
            } else if (url.startsWith("https://") || url.startsWith("http://")) {
                return Optional.of(wrapper.connect(url));
            }
            return Optional.empty();
        } catch (IOException e) {
            Logger.error("Error while fetching HTML content: " + e.getMessage());
            return Optional.empty();
        }
    }
}
