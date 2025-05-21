package paulxyh.model;

import java.util.*;

public class PageResult {
    private final String url;
    private final int depth;
    private final List<PageElement> elements = new ArrayList<>();
    private final Map<String,PageResult> children = new HashMap<>();

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
    public List<Link> getLinks(){
        List<Link> links = new ArrayList<>();
        for (PageElement element : elements) {
            if (element instanceof Link link) {
                links.add(link);
            }
        }
        return links;
    }

    public List<PageResult> getChildren() {
        return this.children.values().stream().toList();
    }

    public PageResult getChildByUrl(String url){
        return children.get(url);
    }

    public synchronized void addElement(PageElement element) {
        this.elements.add(element);
    }

    public synchronized void addChild(PageResult child) {
        this.children.put(child.getUrl(), child);
    }
}
