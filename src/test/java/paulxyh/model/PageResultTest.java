package paulxyh.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PageResult Tests")
public class PageResultTest {
    private final String testUrl1 = "https://paulxyh.test.url";
    private final String testUrl2 = "https://jastornig.test.url/";
    private final int testDepth = 1;
    private PageResult result;

    @BeforeEach
    void init() {
        this.result = new PageResult(this.testUrl1, this.testDepth);
    }

    @Test
    @DisplayName("getUrl() should return the correct URL")
    void testGetUrlReturnsCorrectUrl() {
        assertEquals(this.testUrl1, this.result.getUrl());
    }

    @Test
    @DisplayName("getDepth() should return the correct depth")
    void testGetDepthReturnsCorrectDepth() {
        assertEquals(this.testDepth, this.result.getDepth());
    }

    @Test
    @DisplayName("getElements() should return an empty list initially")
    void testGetElementsIsEmptyInitially() {
        assertTrue(this.result.getElements().isEmpty());
    }

    @Test
    @DisplayName("addElements() adds elements correctly")
    void testAddElements() {
        PageElement element = new Heading(1, "test");
        this.result.addElement(element);
        assertEquals(element, this.result.getElements().getFirst());

        element = new Link(testUrl1, true);
        this.result.addElement(element);
        assertEquals(element, this.result.getElements().get(1));
    }

    @Test
    @DisplayName("getElements() returns list with correct size")
    void testGetElementsReturnsListWithCorrectSize() {
        this.result.addElement(new Heading(1, "test"));
        assertEquals(1, this.result.getElements().size());

        this.result.addElement(new Link(testUrl1, true));
        assertEquals(2, this.result.getElements().size());
    }

    @Test
    @DisplayName("getChildren() should return an empty list initially")
    void testGetChildrenIsEmptyInitially() {
        assertTrue(this.result.getChildren().isEmpty());
    }

    @Test
    @DisplayName("addChild() allows adding children for different URLs")
    void testAddChildren() {
        PageResult child = new PageResult(this.testUrl1, this.testDepth);
        this.result.addChild(child);
        assertEquals(child, this.result.getChildByUrl(testUrl1));

        PageResult child2 = new PageResult(this.testUrl2, this.testDepth);
        this.result.addChild(child2);
        assertEquals(child2, this.result.getChildByUrl(testUrl2));
    }

    @Test
    @DisplayName("addChild() has no effect when adding child with existing URL")
    void testAddChildWithExistingURL() {
        PageResult child = new PageResult(this.testUrl1, this.testDepth);
        this.result.addChild(child);
        assertEquals(1, this.result.getChildren().size());

        PageResult duplicate = new PageResult(this.testUrl1, this.testDepth);
        this.result.addChild(duplicate);
        // Adding a child with the same URL should not increase the size
        assertEquals(1, this.result.getChildren().size());
        assertEquals(child, this.result.getChildByUrl(testUrl1));
    }

    @Test
    @DisplayName("getChildren() returns list with correct size")
    void testGetChildrenReturnsListWithCorrectSize() {
        this.result.addChild(new PageResult(this.testUrl1, this.testDepth));
        assertEquals(1, this.result.getChildren().size());

        this.result.addChild(new PageResult(this.testUrl2, this.testDepth));
        assertEquals(2, this.result.getChildren().size());
    }
}
