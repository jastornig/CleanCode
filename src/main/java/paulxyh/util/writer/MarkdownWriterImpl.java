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
import java.util.HashSet;
import java.util.Set;

public class MarkdownWriterImpl implements MarkdownWriter {
    private BufferedWriter writer;
    private ReportBuilder builder;

    @Override
    public void write(PageResult result, String filename) {
        this.builder = (this.builder != null) ? this.builder : new ReportBuilder(); // testing purposes
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            this.writer = (this.writer != null) ? this.writer : writer; // testing purposes
            writeRecursively(result, 1);
            this.writer.close();
        } catch (WriterNotInitializedException mwe) {
            Logger.error("Error while initializing writer: " + mwe.getMessage());
        } catch (IOException e) {
            Logger.error("Error while writing the report: " + e.getMessage());
        }
    }


    // testing purposes
    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    // testing purposes
    public void setBuilder(ReportBuilder builder) {
        this.builder = builder;
    }

    private void writeRecursively(PageResult page, int depth) throws WriterNotInitializedException {
        if (!checkWriterExists()) throw new WriterNotInitializedException();
        try {
            if (depth == 1) {
                writeInitialDetails(page);
            }
            writeStartOfPage(page.getUrl());
            Set<String> handledLinks = new HashSet<>();
            for (PageElement element : page.getElements()) {
                if (element instanceof Heading heading) writeHeading(heading, depth);
                else if (element instanceof Link link) {
                     boolean recursive = !handledLinks.contains(link.url());
                     handledLinks.add(link.url());
                     handleLink(page, depth, link, recursive);
                }
            }
            writeEndOfPage(page.getUrl());
        } catch (WriteExecutionException e) {
            Logger.error("Error while writing the report: " + e.getMessage());
        }
    }

    private void handleLink(PageResult page, int depth, Link link, boolean recursive) throws WriteExecutionException, WriterNotInitializedException {
        if (link.isValid()) {
            handleValidLink(page, depth, link, recursive);
        } else {
            writeBrokenLink(link.url(), depth);
        }
    }

    private void handleValidLink(PageResult page, int depth, Link link, boolean recursive) throws WriteExecutionException, WriterNotInitializedException {
        writeLink(link.url(), depth);
        writeDepth(depth + 1);
        PageResult child = page.getChildByUrl(link.url());
        if(child != null && recursive){
            writeRecursively(child, depth + 1);
        }
    }

    private void writeInitialDetails(PageResult page) throws WriteExecutionException {
        writeInputUrl(page.getUrl());
        writeDepth(page.getDepth());
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

    private void writeStartOfPage(String url) throws WriteExecutionException {
        try {
            String text = this.builder.buildStartOfPageText(url);
            this.writer.write(text);
        } catch (IOException e) {
            throw new WriteExecutionException();
        }
    }

    private void writeEndOfPage(String url) throws WriteExecutionException {
        try {
            String text = this.builder.buildEndOfPageText(url);
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
