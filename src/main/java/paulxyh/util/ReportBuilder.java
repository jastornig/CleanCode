package paulxyh.util;

import paulxyh.model.Heading;

public class ReportBuilder {
    public String buildInputUrlText(String url) {
        return "input: <a>" + url + "</a>\n";
    }

    public String buildDepthText(int depth) {
        return "<br>depth: " + depth + "\n";
    }

    public String buildHeadingText(Heading heading, int depth) {
        return addHeadingPrefix(heading.level(), depth-1) + heading.title() + "\n";
    }

    public String buildLinkText(String link, int depth) {
        return addLinkPrefix(depth) + "link to <a>" + link + "</a>\n";
    }

    public String buildBrokenLinkText(String brokenLink, int depth) {
        return addLinkPrefix(depth) + "broken link <a>" + brokenLink + "</a>\n";
    }

    private String addHeadingPrefix(int level, int depth) {
        return "#".repeat(level) + " " + addDepthIntent(depth);
    }

    private String addLinkPrefix(int depth) {
        return "<br>" + addDepthIntent(depth);
    }

    private String addDepthIntent(int depth) {
        if (depth > 0) return "--".repeat(depth) + "> ";
        return "";
    }
}
