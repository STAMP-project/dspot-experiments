package net.openhft.chronicle.queue;


import ClassAliasPool.CLASS_ALIASES;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.AbstractMarshallable;
import org.junit.Assert;
import org.junit.Test;


/* Created by Peter Lawrey on 08/05/2017. */
@RequiredForClient
public class MethodReaderObjectReuseTest {
    @Test
    public void testOneOne() {
        CLASS_ALIASES.addAlias(MethodReaderObjectReuseTest.PingDTO.class);
        try (ChronicleQueue cq = SingleChronicleQueueBuilder.single((((OS.TARGET) + "/MethodReaderObjectReuseTest-") + (System.nanoTime()))).build()) {
            (MethodReaderObjectReuseTest.PingDTO.constructionExpected)++;
            MethodReaderObjectReuseTest.PingDTO pdtio = new MethodReaderObjectReuseTest.PingDTO();
            (MethodReaderObjectReuseTest.PingDTO.constructionExpected)++;
            MethodReaderObjectReuseTest.Pinger pinger = cq.acquireAppender().methodWriter(MethodReaderObjectReuseTest.Pinger.class);
            for (int i = 0; i < 5; i++) {
                pinger.ping(pdtio);
                Assert.assertEquals(MethodReaderObjectReuseTest.PingDTO.constructionExpected, MethodReaderObjectReuseTest.PingDTO.constructionCounter);
                pdtio.bytes.append("hi");
            }
            StringBuilder sb = new StringBuilder();
            (MethodReaderObjectReuseTest.PingDTO.constructionExpected)++;
            MethodReader reader = cq.createTailer().methodReader(((MethodReaderObjectReuseTest.Pinger) (( pingDTO) -> sb.append("ping ").append(pingDTO))));
            Assert.assertEquals(MethodReaderObjectReuseTest.PingDTO.constructionExpected, MethodReaderObjectReuseTest.PingDTO.constructionCounter);
            while (reader.readOne());
            Assert.assertEquals(("ping !PingDTO {\n" + ((((((((((((("  bytes: \"\"\n" + "}\n") + "ping !PingDTO {\n") + "  bytes: hi\n") + "}\n") + "ping !PingDTO {\n") + "  bytes: hihi\n") + "}\n") + "ping !PingDTO {\n") + "  bytes: hihihi\n") + "}\n") + "ping !PingDTO {\n") + "  bytes: hihihihi\n") + "}\n")), sb.toString());
        }
    }

    @FunctionalInterface
    interface Pinger {
        void ping(MethodReaderObjectReuseTest.PingDTO pingDTO);
    }

    static class PingDTO extends AbstractMarshallable {
        static int constructionCounter;

        static int constructionExpected;

        final Bytes bytes = Bytes.allocateElasticDirect();

        PingDTO() {
            if ((++(MethodReaderObjectReuseTest.PingDTO.constructionCounter)) > (MethodReaderObjectReuseTest.PingDTO.constructionExpected))
                throw new AssertionError();

        }
    }
}

