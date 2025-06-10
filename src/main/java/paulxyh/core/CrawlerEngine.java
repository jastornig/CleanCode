package paulxyh.core;

import org.jsoup.nodes.Document;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.Link;
import paulxyh.model.PageElement;
import paulxyh.model.PageResult;
import paulxyh.util.fetcher.HTMLContentFetcher;
import paulxyh.util.logger.Logger;
import paulxyh.util.parser.HTMLParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CrawlerEngine {
    private final HTMLContentFetcher fetcher;
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final CrawlTaskExecutor crawlTaskExecutor;
    private final int maxDepth;
    private Optional<PageResult> finalResult;
    private final HTMLParser htmlParser;
    private boolean crawlingFailed = false;

    public CrawlerEngine(HTMLContentFetcher fetcher, HTMLParser parser, CrawlTaskExecutor executor, int maxDepth) {
        this.fetcher = fetcher;
        this.maxDepth = maxDepth;
        this.crawlTaskExecutor = executor;
        this.htmlParser = parser;
    }

    public boolean isFailure() {
        return crawlingFailed;
    }

    public Optional<PageResult> crawl(String url) {
        if (maxDepth == 0)
            return Optional.empty();
        submitTask(() -> processInitialPage(url));
        crawlTaskExecutor.waitForAllTasksToFinish();
        crawlTaskExecutor.shutdown();
        return finalResult;
    }

    private synchronized void submitTask(Runnable task) {
        crawlTaskExecutor.submitTask(task);
    }

    private boolean shouldCrawl(Link link, int currentDepth) {
        synchronized (visitedUrls) {
            if (visitedUrls.contains(link.url())) {
                Logger.debug("URL " + link.url() + " already visited! Stepping over!");
                return false;
            }
        }
        if (currentDepth >= maxDepth) {
            Logger.debug("URL " + link.url() + " exceeds max depth! (" + currentDepth + " >= " + maxDepth + ")");
            return false;
        }
        return link.isValid();
    }

    private void processInitialPage(String url) {
        visitedUrls.add(url);
        Optional<PageResult> initialPage = crawlSinglePage(url, 1);
        this.finalResult = initialPage;
        if (initialPage.isEmpty()) {
            Logger.error("Initial page could not be crawled!");
            crawlingFailed = true;
            return;
        }
        processPageContentInternal(initialPage.get());
    }

    private void processPageContentInternal(PageResult initialPage) {
        List<Link> links = initialPage.getLinks();
        for (Link link : links) {
            if (shouldCrawl(link, initialPage.getDepth())) {
                visitedUrls.add(link.url());
                submitTask(() -> processChildPage(link.url(), initialPage));
            }
        }
    }

    private void processChildPage(String url, PageResult parentPage) {
        Optional<PageResult> page = crawlSinglePage(url, parentPage.getDepth() + 1);
        if (page.isEmpty()) {
            Logger.warn("Child page " + url + " could not be crawled!");
            return;
        }
        parentPage.addChild(page.get());
        processPageContentInternal(page.get());
    }

    private Optional<PageResult> crawlSinglePage(String url, int currentDepth) {
        Logger.info("Fetching HTML content for URL: " + url);
        Optional<Document> htmlContent = fetcher.fetch(url);
        if (htmlContent.isEmpty()) {
            Logger.warn("No HTML Content for " + url + " fetched! Exiting path...");
            return Optional.empty();
        }
        PageResult result = new PageResult(url, currentDepth);
        try {
            List<PageElement> elements = htmlParser.parse(url, htmlContent.get());
            for (PageElement element : elements) {
                result.addElement(element);
            }
        } catch (ElementNotRecognizedException e) {
            Logger.error("Error while parsing: " + e.getMessage());
        }
        return Optional.of(result);
    }
}
