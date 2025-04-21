package paulxyh.util.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import paulxyh.util.logger.Logger;

import java.io.IOException;

public class HTMLContentFetcherImpl implements HTMLContentFetcher {
    @Override
    public Document fetch(String url) {
        try {
            return Jsoup.connect(url).get();
        }
        catch (IOException e) {
            Logger.error("Error while fetching HTML content: " + e.getMessage());
            return null;
        }
    }
}
