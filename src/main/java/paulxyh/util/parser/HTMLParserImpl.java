package paulxyh.util.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.Heading;
import paulxyh.model.Link;
import paulxyh.model.PageElement;
import paulxyh.util.LinkUtils;

import java.util.*;

public class HTMLParserImpl implements HTMLParser{
    private List<PageElement> htmlElements;
    private final Map<String, Boolean> checkedUrls = new HashMap<>();

    @Override
    public List<PageElement> parse(String url, Document content) throws ElementNotRecognizedException{
        htmlElements = new ArrayList<>();

        Elements elements = content.body().select("h1, h2, h3, h4, h5, h6, a[href]");

        for(Element element : elements){
            parseToCorrectType(element);
        }

        return htmlElements;
    }

    private void parseToCorrectType(Element element) throws ElementNotRecognizedException {
        if(element.tagName().matches("h[1-6]")) {
            parseHeading(element);
        }
        else if (element.tagName().equals("a")) {
            parseLink(element);
        }
        else {
            throw new ElementNotRecognizedException();
        }
    }

    private void parseHeading(Element element) {
        String tag = element.tagName();
        int level = Integer.parseInt(tag.substring(1));
        this.htmlElements.add(new Heading(level, element.text()));
    }

    private void parseLink(Element element) {
        String link = element.absUrl("href");
        boolean isLinkValid;
        if (checkedUrls.containsKey(link)) {
            isLinkValid = checkedUrls.get(link);
        } else {
            isLinkValid = LinkUtils.isLinkValid(link);
            checkedUrls.put(link, isLinkValid);
        }
        htmlElements.add(new Link(link, isLinkValid));
    }
}
