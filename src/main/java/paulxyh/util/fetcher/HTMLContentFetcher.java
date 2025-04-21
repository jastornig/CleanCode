package paulxyh.util.fetcher;
import org.jsoup.nodes.Document;
public interface HTMLContentFetcher {
    Document fetch(String url);
}
