package paulxyh.util.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class JsoupWrapperImpl implements JsoupWrapper {

    @Override
    public Document connect(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    @Override
    public Document parse(File input, String charset, String url) throws IOException {
        return Jsoup.parse(input, charset, url);
    }
}
