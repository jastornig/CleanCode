package paulxyh.util.parser;

import org.jsoup.nodes.Document;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.PageElement;

import java.util.List;

public interface HTMLParser {
    List<PageElement> parse(String url, Document content) throws ElementNotRecognizedException;
}
