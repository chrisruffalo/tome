package io.github.chrisruffalo.tome.core.reader;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A reader that is capable of buffering input line-by-line and
 * transforming it as implemented by the transformer.
 *
 * Originally from: https://stackoverflow.com/questions/50799710/java-dynamic-string-replacement-inside-a-reader-stream
 *
 * @param <CONTEXT> a wrapper class that will be passed to each call of transform
 */
public abstract class TransformingReader<CONTEXT extends TransformContext> extends Reader {

    private BufferedReader input;
    private StringReader buffer;

    private TransformingReader() {
        buffer = new StringReader("");
    }

    /**
     * Build a reader that transforms the input reader on a line-by-line basis
     * as implemented by children classes. The input does not need to be a buffered
     * reader but it will be wrapped if one is not provided.
     *
     * @param in the source reader
     */
    public TransformingReader(Reader in) {
        this();

        // only wrap if necessary
        if (in instanceof BufferedReader) {
            this.input = (BufferedReader)in;
        } else {
            this.input = new BufferedReader(in);
        }
    }

    /**
     * This is the method to implement that performs line-by-line translation
     * while also providing a context that allows the reader to pass configuration
     * and other details to the translation without needing to copy/reimplement
     * too much of this class.
     *
     * @param line the text of the line to transform
     * @param context the current transformation context
     *
     * @return the transformed line
     */
    protected abstract String transform(String line, final CONTEXT context) throws IOException, TransformException;

    /**
     * Returns the current context of the transform. Can allow settings to be passed between modules
     * and submodules as well as mutating or keeping the same configuration throughout the transform.
     *
     * @return the transforming context for the current line
     */
    protected abstract CONTEXT getCurrentContext();

    /**
     * Implement the core method of the reader in a way that uses an underlying
     * (buffered) reader to read lines one at a time which allows them to be
     * transformed on a line-by-line basis. When the buffer cannot support a read
     * that matches the length of the read requested then another line is read and
     * used to refill the buffer. This continues until the read is complete or
     * the buffer is exhausted.
     *
     * @param cbuf the requested array (buffer) to fill
     * @param off the offset to fill starting at in the buffer
     * @param len how far in the stream to read
     * @return the number of characters read
     * @throws IOException when something goes wrong reading from the stream
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        // if requested to read 0 bytes then immediately return having
        // read 0 bytes
        if (len == 0) {
            return 0;
        }

        int read = 0;

        // create context to start transform
        final CONTEXT context = this.getCurrentContext();

        while (len > 0) {
            int nchars = buffer.read(cbuf, off, len);
            if (nchars == -1) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }

                // implement line-by-line transform
                try {
                    line = transform(line, context);
                } catch (TransformException tex) {
                    throw new IOException(tex);
                }

                // this is used to test and see if the next line in the stream is ready
                // (which in most JVMs means that it has content and is not empty)
                // so that we append the line separator if there will be content read
                if(input.ready()) {
                    line += System.lineSeparator(); // Add the system line separator which _might_ be an issue
                }

                // update the output buffer with the new line
                buffer = new StringReader(line);
            } else {
                read += nchars;
                off += nchars;
                len -= nchars;
            }
        }

        // no bytes were read meaning that the end of the file has been reached
        if (read == 0) {
            read = -1;
        }

        return read;
    }

    @Override
    public void close() throws IOException {
        input.close();
        buffer.close();
    }
}
