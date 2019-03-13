package org.netbeans.lib.profiler.heap;


import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;


/**
 * Resolves offset by instance ID.
 *
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class LayoutPrinter {
    HprofHeap heap;

    HprofByteBuffer dumpBuffer;

    long[] pointer = new long[1];

    @Test
    public void test() throws FileNotFoundException, IOException {
        scan("file path");
    }
}

