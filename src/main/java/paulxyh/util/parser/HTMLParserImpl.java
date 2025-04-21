package paulxyh.util.parser;

import org.jsoup.nodes.Document;
import paulxyh.model.PageElement;

import java.util.List;

public class HTMLParserImpl implements HTMLParser{
    @Override
    public List<PageElement> parse(String url, Document content) {
        return List.of();
    }
}
