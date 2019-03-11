package net.openhft.chronicle.map;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.values.IntValue;
import net.openhft.chronicle.values.Values;
import org.junit.Assert;
import org.junit.Test;


public class DemoChronicleMapTest {
    @Test
    public void testMap() throws IOException {
        File file = File.createTempFile(("DummyOrders" + (System.currentTimeMillis())), ".test");
        file.deleteOnExit();
        int maxEntries = 1000;
        try (ChronicleMap<IntValue, DemoOrderVOInterface> map = ChronicleMapBuilder.of(IntValue.class, DemoOrderVOInterface.class).putReturnsNull(true).removeReturnsNull(true).entries(maxEntries).entryAndValueOffsetAlignment(8).createPersistedTo(file)) {
            IntValue key = Values.newHeapInstance(IntValue.class);
            DemoOrderVOInterface value = Values.newNativeReference(DemoOrderVOInterface.class);
            DemoOrderVOInterface value2 = Values.newNativeReference(DemoOrderVOInterface.class);
            // Initially populate the map
            for (int i = 0; i < maxEntries; i++) {
                key.setValue(i);
                map.acquireUsing(key, value);
                value.setSymbol(("IBM-" + i));
                value.addAtomicOrderQty(1000);
                map.getUsing(key, value2);
                Assert.assertEquals(("IBM-" + i), value.getSymbol().toString());
                Assert.assertEquals(1000, value.getOrderQty(), 0.0);
            }
            for (Map.Entry<IntValue, DemoOrderVOInterface> entry : map.entrySet()) {
                IntValue k = entry.getKey();
                DemoOrderVOInterface v = entry.getValue();
                // System.out.println(String.format("Key %d %s", k.getValue(), v == null ? "<null>" : v.getSymbol()));
                Assert.assertNotNull(v);
            }
        }
        file.delete();
    }

    @Test
    public void testMapLocked() throws IOException {
        File file = File.createTempFile(("DummyOrders-" + (System.currentTimeMillis())), ".test");
        file.deleteOnExit();
        int maxEntries = 1000;
        try (ChronicleMap<IntValue, DemoOrderVOInterface> map = ChronicleMapBuilder.of(IntValue.class, DemoOrderVOInterface.class).putReturnsNull(true).removeReturnsNull(true).entries(maxEntries).entryAndValueOffsetAlignment(8).createPersistedTo(file)) {
            IntValue key = Values.newHeapInstance(IntValue.class);
            DemoOrderVOInterface value = Values.newNativeReference(DemoOrderVOInterface.class);
            DemoOrderVOInterface value2 = Values.newNativeReference(DemoOrderVOInterface.class);
            // Initially populate the map
            for (int i = 0; i < maxEntries; i++) {
                key.setValue(i);
                try (Closeable c = map.acquireContext(key, value)) {
                    value.setSymbol(("IBM-" + i));
                    value.addAtomicOrderQty(1000);
                }
                // TODO suspicious -- getUsing `value2`, working with `value` then
                // try (ReadContext rc = map.getUsingLocked(key, value2)) {
                // assertTrue(rc.present());
                // assertEquals("IBM-" + i, value.getSymbol());
                // assertEquals(1000, value.getOrderQty(), 0.0);
                // }
            }
            for (Map.Entry<IntValue, DemoOrderVOInterface> entry : map.entrySet()) {
                IntValue k = entry.getKey();
                DemoOrderVOInterface v = entry.getValue();
                // System.out.println(String.format("Key %d %s", k.getValue(), v == null ? "<null>" : v.getSymbol()));
                Assert.assertNotNull(v);
            }
        }
        file.delete();
    }
}

