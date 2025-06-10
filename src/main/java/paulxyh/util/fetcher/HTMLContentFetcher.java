package paulxyh.util.fetcher;

import org.jsoup.nodes.Document;

import java.util.Optional;

public interface HTMLContentFetcher {
    Optional<Document> fetch(String url);
}
