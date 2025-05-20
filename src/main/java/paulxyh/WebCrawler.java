package paulxyh;

import paulxyh.args.ArgParser;
import paulxyh.controller.CrawlerController;
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

import java.util.Arrays;
import java.util.List;

public class WebCrawler {
    public static void main(String[] args) {

        ArgParser argParser = new ArgParser();
        argParser.addArgument("url", "The starting URL for the crawler", true);
        argParser.addArgument("depth", "The maximum depth to crawl", true);
        argParser.addArgument("verbose", "If additional information should be logged", false);
        argParser.addTrailingArgs("domain","The domains to crawl", true);

        try {
            argParser.parse(args);
        } catch (ArgParsingException e) {
            System.err.println(e.getMessage());
            System.out.println(argParser.getSynopsis());
            System.exit(1);
        }

        if(argParser.has("verbose")){
            Logger.setLogLevel(Logger.Level.DEBUG);
        }

        String startUrl = argParser.get("url");
        int maxDepth;

        try {
            LinkUtils.checkUrlFormatting(startUrl);
            maxDepth = Integer.parseInt(argParser.get("depth"));
        } catch (IncorrectInputException i) {
            Logger.error("Input URL must contain 'https://' or 'http://'!");
            return;
        } catch (NumberFormatException e) {
            Logger.error("Depth must be an integer.");
            return;
        }

        List<String> allowedDomains = argParser.getTrailingArgs();

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