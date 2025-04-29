package paulxyh.model;

import java.util.ArrayList;
import java.util.List;

public class PageResult {
    private final String url;
    private final int depth;
    private final List<PageElement> elements = new ArrayList<>();
    private final List<PageResult> children = new ArrayList<>();

    public PageResult(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public List<PageElement> getElements() {
        return this.elements;
    }

    public List<PageResult> getChildren() {
        return this.children;
    }

    public void addElement(PageElement element) {
        this.elements.add(element);
    }

    public void addChild(PageResult child) {
        this.children.add(child);
    }
}
