package paulxyh;

import paulxyh.controller.CrawlerController;

import java.util.Arrays;
import java.util.List;

public class WebCrawler {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java WebCrawler <startURL> <depth> <domains>");
            System.exit(1);
        }

        String startUrl = args[0];
        int maxDepth;
        try {
            maxDepth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Depth must be an integer.");
            return;
        }

        List<String> allowedDomains = Arrays.asList(args[2].split(","));

        CrawlerController crawler = new CrawlerController(startUrl, maxDepth, allowedDomains);
        crawler.run();
    }
}