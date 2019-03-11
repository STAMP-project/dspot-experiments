package net.openhft.chronicle.queue.impl;


import Metadata.NoMeta.INSTANCE;
import java.io.File;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.queue.impl.table.SingleTableBuilder;
import org.junit.Assert;
import org.junit.Test;


public class TableStoreTest {
    @Test
    public void acquireValueFor() {
        String file = (((OS.TARGET) + "/table-") + (System.nanoTime())) + ".cq4t";
        new File(file).deleteOnExit();
        try (TableStore table = SingleTableBuilder.binary(file, INSTANCE).build()) {
            LongValue a = table.acquireValueFor("a");
            LongValue b = table.acquireValueFor("b");
            Assert.assertEquals(Long.MIN_VALUE, a.getVolatileValue());
            Assert.assertTrue(a.compareAndSwapValue(Long.MIN_VALUE, 1));
            Assert.assertEquals(Long.MIN_VALUE, b.getVolatileValue());
            Assert.assertTrue(b.compareAndSwapValue(Long.MIN_VALUE, 2));
            Assert.assertEquals(("--- !!meta-data #binary\n" + (((((((((("header: !STStore {\n" + "  wireType: !WireType BINARY_LIGHT\n") + "}\n") + "# position: 60, header: 0\n") + "--- !!data #binary\n") + "a: 1\n") + "# position: 80, header: 1\n") + "--- !!data #binary\n") + "b: 2\n") + "...\n") + "# 65436 bytes remaining\n")), table.dump());
        }
        try (TableStore table = SingleTableBuilder.binary(file, INSTANCE).build()) {
            LongValue c = table.acquireValueFor("c");
            LongValue b = table.acquireValueFor("b");
            Assert.assertEquals(Long.MIN_VALUE, c.getVolatileValue());
            Assert.assertTrue(c.compareAndSwapValue(Long.MIN_VALUE, 3));
            Assert.assertEquals(2, b.getVolatileValue());
            Assert.assertTrue(b.compareAndSwapValue(2, 22));
            Assert.assertEquals(("--- !!meta-data #binary\n" + ((((((((((((("header: !STStore {\n" + "  wireType: !WireType BINARY_LIGHT\n") + "}\n") + "# position: 60, header: 0\n") + "--- !!data #binary\n") + "a: 1\n") + "# position: 80, header: 1\n") + "--- !!data #binary\n") + "b: 22\n") + "# position: 96, header: 2\n") + "--- !!data #binary\n") + "c: 3\n") + "...\n") + "# 65420 bytes remaining\n")), table.dump());
            System.out.println(table.dump());
        }
    }
}

