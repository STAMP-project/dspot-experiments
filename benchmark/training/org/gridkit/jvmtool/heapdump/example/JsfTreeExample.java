package org.gridkit.jvmtool.heapdump.example;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;


/**
 * This example demonstrates extracting JSF component trees
 * from heap dump.
 *
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class JsfTreeExample {
    /**
     * This entry point for this example.
     */
    @Test
    public void check() throws FileNotFoundException, IOException {
        String dumppath = "";// path to dump of JEE server

        Heap heap = HeapFactory.createFastHeap(new File(dumppath));
        dumpComponentTree(heap);
    }
}

