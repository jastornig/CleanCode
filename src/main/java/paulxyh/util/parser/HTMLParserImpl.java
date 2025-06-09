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
import java.util.concurrent.ConcurrentHashMap;

public class HTMLParserImpl implements HTMLParser {
    private static final Map<String, Boolean> checkedUrls = new ConcurrentHashMap<>();

    @Override
    public List<PageElement> parse(String url, Document content) throws ElementNotRecognizedException {
        List<PageElement> htmlElements = new ArrayList<>();

        Elements elements = content.body().select("h1, h2, h3, h4, h5, h6, a[href]");

        for (Element element : elements) {
            parseToCorrectType(element, htmlElements);
        }

        return htmlElements;
    }

    private void parseToCorrectType(Element element, List<PageElement> htmlElements) throws ElementNotRecognizedException {
        if (element.tagName().matches("h[1-6]")) {
            parseHeading(element, htmlElements);
        } else if (element.tagName().equals("a")) {
            parseLink(element, htmlElements);
        } else {
            throw new ElementNotRecognizedException();
        }
    }

    private void parseHeading(Element element, List<PageElement> htmlElements) {
        String tag = element.tagName();
        int level = Integer.parseInt(tag.substring(1));
        htmlElements.add(new Heading(level, element.text()));
    }

    private void parseLink(Element element, List<PageElement> htmlElements) {
        String unnormalizedLink = element.absUrl("href");
        String normalizedLink = LinkUtils.normalizeLinkAndRemoveFragment(unnormalizedLink);
        boolean isLinkValid = getIsLinkValid(normalizedLink);
        htmlElements.add(new Link(normalizedLink, isLinkValid));
    }

    private boolean getIsLinkValid(String link) {
        boolean isLinkValid;
        if (checkedUrls.containsKey(link)) {
            isLinkValid = checkedUrls.get(link);
        } else {
            isLinkValid = LinkUtils.isLinkValid(link);
            checkedUrls.put(link, isLinkValid);
        }
        return isLinkValid;
    }
}
