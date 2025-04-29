package paulxyh.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PageResult Tests")
public class PageResultTest {
    private final String testUrl = "https://paulxyh.test.url";
    private final int testDepth = 1;
    private PageResult result;

    @BeforeEach
    void init() {
        this.result = new PageResult(this.testUrl, this.testDepth);
    }

    @Test
    @DisplayName("getUrl() should return the correct URL")
    void testGetUrlReturnsCorrectUrl() {
        assertEquals(this.testUrl, this.result.getUrl());
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

        element = new Link(testUrl, true);
        this.result.addElement(element);
        assertEquals(element, this.result.getElements().get(1));
    }

    @Test
    @DisplayName("getElements() returns list with correct size")
    void testGetElementsReturnsListWithCorrectSize() {
        this.result.addElement(new Heading(1, "test"));
        assertEquals(1, this.result.getElements().size());

        this.result.addElement(new Link(testUrl, true));
        assertEquals(2, this.result.getElements().size());
    }

    @Test
    @DisplayName("getChildren() should return an empty list initially")
    void testGetChildrenIsEmptyInitially() {

    }

    @Test
    @DisplayName("addChild() adds child PageResult correctly")
    void testAddChildren() {
        PageResult child = new PageResult(this.testUrl, this.testDepth);
        this.result.addChild(child);
        assertEquals(child, this.result.getChildren().getFirst());

        PageResult child2 = new PageResult(this.testUrl, this.testDepth);
        this.result.addChild(child2);
        assertEquals(child2, this.result.getChildren().get(1));
    }

    @Test
    @DisplayName("getChildren() returns list with correct size")
    void testGetChildrenReturnsListWithCorrectSize() {
        this.result.addChild(new PageResult(this.testUrl, this.testDepth));
        assertEquals(1, this.result.getChildren().size());

        this.result.addChild(new PageResult(this.testUrl, this.testDepth));
        assertEquals(2, this.result.getChildren().size());
    }
}
