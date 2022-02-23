package io.github.chrisruffalo.tome.core.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Util {

    private static final int BUFFER_SIZE = 1024 * 8; // 8kb buffer

    public static int transfer(final Reader input, final Writer output) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
