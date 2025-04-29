package paulxyh.util.writer;

import paulxyh.model.PageResult;

import java.io.BufferedWriter;

public interface MarkdownWriter {
    void write(PageResult result, String filename);
}
