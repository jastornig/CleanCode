package paulxyh.controller;

import paulxyh.core.CrawlerEngine;
import paulxyh.model.PageResult;
import paulxyh.util.LinkUtils;
import paulxyh.util.logger.Logger;
import paulxyh.util.writer.MarkdownWriter;

import java.util.List;
import java.util.Optional;

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
        Optional<PageResult> result = engine.crawl(url);
        if (engine.isFailure()) {
            Logger.error("Crawling failed");
        } else {
            if (result.isEmpty()) {
                Logger.warn("Empty result!");
                return;
            }
            Logger.info("Writing results to " + filename);
            writer.write(result.get(), filename);
        }
    }
}
