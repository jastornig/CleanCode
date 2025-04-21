package paulxyh.core;

import org.jsoup.nodes.Document;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.Link;
import paulxyh.model.PageElement;
import paulxyh.model.PageResult;
import paulxyh.util.fetcher.HTMLContentFetcher;
import paulxyh.util.fetcher.HTMLContentFetcherImpl;
import paulxyh.util.logger.Logger;
import paulxyh.util.parser.HTMLParser;

import java.util.*;

public class CrawlerEngine {
    private final HTMLParser parser;
    private final HTMLContentFetcher fetcher;
    private final Set<String> visitedUrls = new HashSet<>();

    public CrawlerEngine(HTMLParser parser) {
        this.parser = parser;
        this.fetcher = new HTMLContentFetcherImpl();
    }

    public PageResult crawl(String url, int maxDepth) {
        return crawlRecursive(url, 1, maxDepth);
    }

    private PageResult crawlRecursive(String url, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth) {
            Logger.warn("Maximal Depth was reached! Stepping over!");
            return null;
        }
        if(visitedUrls.contains(url)){
            Logger.warn("URL " + url + " already visited! Stepping over!");
            return null;
        }
        visitedUrls.add(url);

        Logger.info("Fetching HTML content");
        Document htmlContent = fetcher.fetch(url);
        if (htmlContent == null) {
            Logger.warn("No HTML Content fetched! Exiting...");
            return null;
        }

        PageResult result = new PageResult(url, currentDepth);

        try {
            List<PageElement> elements = parser.parse(url, htmlContent);
            for (PageElement element : elements) {
                result.addElement(element);
                if (element instanceof Link link && !visitedUrls.contains(link.url())) {
                    String linkUrl = link.url();
                    if (link.isValid() && currentDepth < maxDepth) {
                            PageResult child = crawlRecursive(linkUrl, currentDepth + 1, maxDepth);
                            if (child != null) {
                                result.addChild(child);
                            }
                    }
                }
            }
        } catch (ElementNotRecognizedException e) {
            Logger.error("Error while parsing: " + e.getMessage());
        }

        return result;
    }
}
