package paulxyh.util.writer;

import paulxyh.exception.WriteExecutionException;
import paulxyh.exception.WriterNotInitializedException;
import paulxyh.model.Heading;
import paulxyh.model.Link;
import paulxyh.model.PageElement;
import paulxyh.model.PageResult;
import paulxyh.util.LinkUtils;
import paulxyh.util.ReportBuilder;
import paulxyh.util.logger.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MarkdownWriterImpl implements MarkdownWriter {
    private BufferedWriter writer;
    private ReportBuilder builder;
    @Override
    public void write(PageResult result, String filename) {
        try {
            this.builder = new ReportBuilder();
            this.writer = new BufferedWriter(new FileWriter(filename));
            writeRecursively(result, 1);
            this.writer.close();
        } catch (WriterNotInitializedException mwe) {
            Logger.error("Error while initializing writer: " + mwe.getMessage());
        } catch (IOException e) {
            Logger.error("Error while writing the report: " + e.getMessage());
        }
    }

    private void writeRecursively(PageResult page, int depth) throws WriterNotInitializedException {
        if(!checkWriterExists()) throw new WriterNotInitializedException();
        int childIndex = 0;
        try {
            if(depth == 1) {
                writeInputUrl(page.getUrl());
                writeDepth(page.getDepth());
            }
            for(PageElement element : page.getElements()){
                if(element instanceof Heading heading) writeHeading(heading, depth);
                else if (element instanceof Link link) {
                    if(link.isValid()) {
                        writeLink(link.url(), depth);
                        writeDepth(depth + 1);
                        if(!page.getChildren().isEmpty() && childIndex < page.getChildren().size()) {
                            writeRecursively(page.getChildren().get(childIndex++), depth + 1);
                        }
                    }
                    else {
                        writeBrokenLink(link.url(), depth);
                    }
                }
            }
        } catch(WriteExecutionException e) {
            Logger.error("Error while writing the report: " + e.getMessage());
        }
    }

    private boolean checkWriterExists() {
        return writer != null;
    }
    private void writeInputUrl(String url) throws WriteExecutionException {
        try {
            String text = this.builder.buildInputUrlText(url);
            this.writer.write(text);
        } catch (IOException e) {
            throw new WriteExecutionException();
        }
    }
    private void writeDepth(int depth) throws WriteExecutionException {
        try {
            String text = this.builder.buildDepthText(depth);
            this.writer.write(text);
        } catch (IOException e) {
            throw new WriteExecutionException();
        }
    }
    private void writeHeading(Heading heading, int depth) throws WriteExecutionException {
        try {
            String text = this.builder.buildHeadingText(heading, depth);
            this.writer.write(text);
        } catch (IOException e) {
            throw new WriteExecutionException();
        }
    }
    private void writeLink(String link, int depth) throws WriteExecutionException {
        try {
            String text = this.builder.buildLinkText(link, depth);
            this.writer.write(text);
        } catch (IOException e) {
            throw new WriteExecutionException();
        }
    }
    private void writeBrokenLink(String brokenLink, int depth) throws WriteExecutionException {
        try {
            String text = this.builder.buildBrokenLinkText(brokenLink, depth);
            this.writer.write(text);
        } catch (IOException e) {
            throw new WriteExecutionException();
        }
    }


}
