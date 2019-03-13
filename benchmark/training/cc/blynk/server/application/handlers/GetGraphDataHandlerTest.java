package cc.blynk.server.application.handlers;


import cc.blynk.server.core.model.graph.GraphKey;
import cc.blynk.utils.ByteUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.07.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetGraphDataHandlerTest {
    @Test
    public void testCompressAndDecompress() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate((1000 * (ByteUtils.REPORTING_RECORD_SIZE_BYTES)));
        int dataLength = 0;
        for (int i = 0; i < 1000; i++) {
            long ts = System.currentTimeMillis();
            GraphKey mes = new GraphKey(1, ("aw 1 " + i).split(" "), ts);
            bb.put(GetGraphDataHandlerTest.toByteArray(mes));
            dataLength += ByteUtils.REPORTING_RECORD_SIZE_BYTES;
        }
        System.out.println(("Size before compression : " + dataLength));
        byte[][] data = new byte[1][];
        data[0] = bb.array();
        byte[] compressedData = ByteUtils.compress(data);
        System.out.println(((("Size after compression : " + (compressedData.length)) + ". Compress rate ") + (((double) (dataLength)) / (compressedData.length))));
        Assert.assertNotNull(compressedData);
        ByteBuffer result = ByteBuffer.wrap(GetGraphDataHandlerTest.decompress(compressedData));
        Assert.assertEquals(((1000 * (ByteUtils.REPORTING_RECORD_SIZE_BYTES)) + 4), result.capacity());
        int size = result.getInt();
        Assert.assertEquals(1000, size);
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(((double) (i)), result.getDouble(), 0.001);
            result.getLong();
        }
        // System.out.println(result);
    }
}

