package paulxyh.controller;

import java.util.List;

public class CrawlerController {
    private String url;
    private int maxDepth;
    private List<String> allowedDomains;

    public CrawlerController(String url, int depth, List<String> allowedDomains){
        this.url = url;
        this.maxDepth = depth;
        this.allowedDomains = allowedDomains;
    }

//     TODO: Implement
//        - Call the engine
//        - Call the MarkdownWriter with the results
    public void run(){

    }
}
