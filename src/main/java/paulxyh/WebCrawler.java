package paulxyh;

import paulxyh.args.ArgParser;
import paulxyh.controller.CrawlerController;
import paulxyh.core.CrawlTaskExecutor;
import paulxyh.core.CrawlerEngine;
import paulxyh.exception.ArgParsingException;
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

import java.net.HttpURLConnection;
import java.util.List;

public class WebCrawler {
    public static void main(String[] args) {

        ArgParser argParser = new ArgParser();
        argParser.addArgument("url", "The starting URL for the crawler", true);
        argParser.addArgument("depth", "The maximum depth to crawl", true);
        argParser.addArgument("threads", "The number of threads to use. Default is 3", false);
        argParser.addArgument("verbose", "If additional information should be logged", false);
        argParser.addTrailingArgs("domain", "The domains to crawl", true);

        try {
            argParser.parse(args);
        } catch (ArgParsingException e) {
            System.err.println(e.getMessage());
            System.out.println(argParser.getSynopsis());
            System.exit(1);
        }

        if (argParser.has("verbose")) {
            Logger.setLogLevel(Logger.Level.DEBUG);
        }
        int numThreads = 3;
        if (argParser.has("threads")) {
            try {
                if (argParser.get("threads").isEmpty()) {
                    throw new NumberFormatException("No value for threads!");
                }
                numThreads = Integer.parseInt(argParser.get("threads").get());
            } catch (NumberFormatException e) {
                Logger.error("Number of threads must be an integer.");
                Logger.error(e.getMessage());
                return;
            }
        }

        if (argParser.get("url").isEmpty()) {
            Logger.error("Starting url must be given!");
            return;
        }
        String startUrl = argParser.get("url").get();
        int maxDepth;
        try {
            LinkUtils.checkUrlFormatting(startUrl);
            if (argParser.get("depth").isEmpty()) {
                throw new NumberFormatException("No value for depth!");
            }
            maxDepth = Integer.parseInt(argParser.get("depth").get());
        } catch (IncorrectInputException i) {
            Logger.error("Input URL must contain 'https://' or 'http://'!");
            return;
        } catch (NumberFormatException e) {
            Logger.error("Depth must be an integer.");
            return;
        }

        List<String> allowedDomains = argParser.getTrailingArgs();
        HttpURLConnection.setFollowRedirects(true);
        Logger.info("Initializing Crawler with url: " + startUrl + " and " + numThreads + " threads");
        JsoupWrapper wrapper = new JsoupWrapperImpl();
        HTMLContentFetcher fetcher = new HTMLContentFetcherImpl(wrapper);
        CrawlTaskExecutor executor = new CrawlTaskExecutor(numThreads);
        HTMLParser parser = new HTMLParserImpl();
        CrawlerEngine engine = new CrawlerEngine(fetcher, parser, executor, maxDepth);
        MarkdownWriter writer = new MarkdownWriterImpl();
        CrawlerController crawler = new CrawlerController(engine, writer);

        Logger.info("Starting Crawler");
        crawler.run(startUrl, allowedDomains);
    }
}