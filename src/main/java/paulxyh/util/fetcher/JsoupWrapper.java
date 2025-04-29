package paulxyh.util.fetcher;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public interface JsoupWrapper {
    Document connect(String url) throws IOException;

    Document parse(File input, String charset, String url) throws IOException;
}
