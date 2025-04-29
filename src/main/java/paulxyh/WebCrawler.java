package paulxyh;

import paulxyh.controller.CrawlerController;
import paulxyh.core.CrawlerEngine;
import paulxyh.exception.IncorrectInputException;
import paulxyh.util.LinkUtils;
import paulxyh.util.fetcher.HTMLContentFetcher;
import paulxyh.util.fetcher.HTMLContentFetcherImpl;
import paulxyh.util.fetcher.JsoupWrapper;
import paulxyh.util.fetcher.JsoupWrapperImpl;
import paulxyh.util.logger.Logger;
import paulxyh.util.parser.HTMLParser;
import paulxyh.util.parser.HTMLParserImpl;
import paulxyh.util.writer.MarkdownWriter;
import paulxyh.util.writer.MarkdownWriterImpl;

import java.util.Arrays;
import java.util.List;

public class WebCrawler {
    public static void main(String[] args) {
        if (args.length < 3) {
            Logger.error("Usage: java WebCrawler <startURL> <depth> <domains>");
            System.exit(1);
        }

        String startUrl = args[0];
        int maxDepth;

        try {
            LinkUtils.checkUrlFormatting(startUrl);
            maxDepth = Integer.parseInt(args[1]);
        } catch (IncorrectInputException i) {
            Logger.error("Input URL must contain 'https://' or 'http://'!");
            return;
        } catch (NumberFormatException e) {
            Logger.error("Depth must be an integer.");
            return;
        }

        List<String> allowedDomains = Arrays.asList(args[2].split(","));

        Logger.info("Initializing Crawler with url: " + startUrl);
        HTMLParser parser = new HTMLParserImpl();
        JsoupWrapper wrapper = new JsoupWrapperImpl();
        HTMLContentFetcher fetcher = new HTMLContentFetcherImpl(wrapper);
        CrawlerEngine engine = new CrawlerEngine(parser, fetcher);
        MarkdownWriter writer = new MarkdownWriterImpl();
        CrawlerController crawler = new CrawlerController(engine, writer);

        Logger.info("Starting Crawler");
        crawler.run(startUrl, maxDepth, allowedDomains);
    }
}