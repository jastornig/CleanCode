package paulxyh.controller;

import paulxyh.core.CrawlerEngine;
import paulxyh.model.PageResult;
import paulxyh.util.LinkUtils;
import paulxyh.util.logger.Logger;
import paulxyh.util.writer.MarkdownWriter;

import java.util.List;

public class CrawlerController {
    private final String filename;
    private final CrawlerEngine engine;
    private final MarkdownWriter writer;

    public CrawlerController(CrawlerEngine engine, MarkdownWriter writer) {
        this.filename = "crawler_report.md";
        this.engine = engine;
        this.writer = writer;
    }

    public void run(String url, List<String> allowedDomains) {
        Logger.info("Crawler started");
        LinkUtils.setAllowedDomains(allowedDomains);
        PageResult result = engine.crawl(url);

        Logger.info("Writing results to " + filename);
        writer.write(result, filename);
    }
}
