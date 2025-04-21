package paulxyh.util.writer;

import paulxyh.model.PageResult;

public interface MarkdownWriter {
    void write(PageResult result, String filename);
}
