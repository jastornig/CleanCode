package paulxyh.core;

import org.jsoup.nodes.Document;
import paulxyh.exception.ElementNotRecognizedException;
import paulxyh.model.Link;
import paulxyh.model.PageElement;
import paulxyh.model.PageResult;
import paulxyh.util.fetcher.HTMLContentFetcher;
import paulxyh.util.logger.Logger;
import paulxyh.util.parser.HTMLParser;
import paulxyh.util.parser.HTMLParserImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlerEngine {
    private final HTMLContentFetcher fetcher;
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final CrawlTaskExecutor crawlTaskExecutor;
    private final int maxDepth;
    private PageResult finalResult;

    public CrawlerEngine(HTMLContentFetcher fetcher, int numThreads, int maxDepth) {
        this.fetcher = fetcher;
        this.maxDepth = maxDepth;
        this.crawlTaskExecutor = new CrawlTaskExecutor(numThreads);
    }


    public PageResult crawl(String url) {
        submitTask(() -> processInitialPage(url));
        crawlTaskExecutor.waitForAllTasksToFinish();
        crawlTaskExecutor.shutdown();
        return finalResult;
    }

    private synchronized void submitTask(Runnable task){
        crawlTaskExecutor.submitTask(task);
    }

    private boolean shouldCrawl(Link link, int currentDepth){
        synchronized (visitedUrls) {
            if(visitedUrls.contains(link.url())){
                Logger.debug("URL " + link.url() + " already visited! Stepping over!");
                return false;
            }
        }
        if(currentDepth >= maxDepth){
            Logger.debug("URL " + link.url() + " exceeds max depth! (" + currentDepth + " >= " + maxDepth + ")");
            return false;
        }
        return link.isValid();
    }

    private void processInitialPage(String url){
        visitedUrls.add(url);
        PageResult initialPage = crawlSinglePage(url, 1);
        this.finalResult = initialPage;
        if(initialPage == null){
            Logger.error("Initial page could not be crawled!");
            System.exit(1);
            return;
        }
        List<Link> links = initialPage.getLinks();
        for (Link link : links) {
            if(shouldCrawl(link, initialPage.getDepth())){
                visitedUrls.add(link.url());
                submitTask(()->processChildPage(link.url(), initialPage));
            }
        }
    }

    private void processChildPage(String url, PageResult parentPage){
        PageResult page = crawlSinglePage(url, parentPage.getDepth() + 1);
        if(page == null){
            Logger.warn("Child page " + url + " could not be crawled!");
            return;
        }
        parentPage.addChild(page);
        List<Link> links = page.getLinks();
        for (Link link : links) {
            if(shouldCrawl(link, page.getDepth())){
                visitedUrls.add(link.url());
                submitTask(()->processChildPage(link.url(), page));
            }
        }
    }

    private PageResult crawlSinglePage(String url, int currentDepth){
        Logger.info("Fetching HTML content for URL: " + url);
        Document htmlContent = fetcher.fetch(url);
        if(htmlContent == null){
            Logger.warn("No HTML Content for " + url + " fetched! Exiting path...");
            return null;
        }
        PageResult result = new PageResult(url, currentDepth);
        try {
            HTMLParser parser = new HTMLParserImpl();
            List<PageElement> elements = parser.parse(url, htmlContent);
            for (PageElement element : elements) {
                result.addElement(element);
            }
        } catch (ElementNotRecognizedException e) {
            Logger.error("Error while parsing: " + e.getMessage());
        }
        return result;
    }
}
