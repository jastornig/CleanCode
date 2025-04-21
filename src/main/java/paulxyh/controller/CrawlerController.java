package paulxyh.controller;

import paulxyh.core.CrawlerEngine;
import paulxyh.model.PageResult;
import paulxyh.util.LinkUtils;
import paulxyh.util.logger.Logger;
import paulxyh.util.writer.MarkdownWriterImpl;
import paulxyh.util.parser.HTMLParserImpl;

import java.util.List;

public class CrawlerController {
    private final String filename;
    private final CrawlerEngine engine;

    public CrawlerController(){
        this.filename = "crawler_report.md";
        this.engine = new CrawlerEngine(new HTMLParserImpl());
    }

    public void run(String url, int depth, List<String> allowedDomains){
        Logger.info("Crawler started");
        LinkUtils.setAllowedDomains(allowedDomains);
        PageResult result = engine.crawl(url, depth);

        Logger.info("Writing results to " + filename);
        MarkdownWriterImpl writer = new MarkdownWriterImpl();
        writer.write(result, filename);
    }
}
