package paulxyh;

import paulxyh.controller.CrawlerController;
import paulxyh.exception.IncorrectInputException;
import paulxyh.util.LinkUtils;
import paulxyh.util.logger.Logger;

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
        } catch (IncorrectInputException i){
            Logger.error("Input URL must contain 'https://' or 'http://'!");
            return;
        } catch (NumberFormatException e) {
            Logger.error("Depth must be an integer.");
            return;
        }

        List<String> allowedDomains = Arrays.asList(args[2].split(","));

        Logger.info("Initializing Crawler with url: " + startUrl);
        CrawlerController crawler = new CrawlerController();
        Logger.info("Starting Crawler");
        crawler.run(startUrl, maxDepth, allowedDomains);
    }
}