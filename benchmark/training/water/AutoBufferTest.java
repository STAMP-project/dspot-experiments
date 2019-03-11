package water;


import java.io.Serializable;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class AutoBufferTest extends TestUtil {
    @Test
    public void testOutputStreamBigDataBigChunks() {
        // don't run if the JVM doesn't have enough memory
        Assume.assumeTrue("testOutputStreamBigDataBigChunks: JVM has enough memory (~4GB)", ((Runtime.getRuntime().maxMemory()) > 4.0E9));
        /* =array header size */
        final int dataSize = ((Integer.MAX_VALUE) - 8)/* =max array size */
         - 5;
        byte[] data = new byte[dataSize - 1];
        AutoBuffer ab = new AutoBuffer(NullOutputStream.NULL_OUTPUT_STREAM, false);
        // make sure the buffer can take the full array
        ab.putA1(data);
        // now try to stream 1TB of data through the buffer
        for (int i = 0; i < 512; i++) {
            if ((i % 10) == 0)
                System.out.println(i);

            ab.putA1(data);
        }
        ab.close();
    }

    @Test
    public void testOutputStreamBigDataSmallChunks() {
        final int dataSize = 100 * 1024;
        byte[] data = new byte[dataSize - 1];
        AutoBuffer ab = new AutoBuffer(NullOutputStream.NULL_OUTPUT_STREAM, false);
        // make sure the buffer can take full array
        ab.putA1(data);
        // try to stream 1TB of data made of small chunks through AutoBuffer
        for (int i = 0; i < (1.0E12 / dataSize); i++)
            ab.putA1(data);

        ab.close();
    }

    @Test
    public void testOutputStreamSmallData() {
        final int dataSize = 100 * 1024;
        byte[] data = new byte[dataSize];
        AutoBuffer ab = new AutoBuffer(NullOutputStream.NULL_OUTPUT_STREAM, false);
        // stream bite-size data to AutoBuffer
        for (int i = 0; i < ((Integer.MAX_VALUE) / dataSize); i++)
            ab.putA1(data);

        ab.close();
    }

    static class XYZZY implements Serializable {
        int i = 1;

        String s = "hi";
    }

    @Test
    public void testNameOfClass() throws Exception {
        byte[] bytes = AutoBuffer.javaSerializeWritePojo(new AutoBufferTest.XYZZY());
        Assert.assertEquals("water.AutoBufferTest$XYZZY", AutoBuffer.nameOfClass(bytes));
        bytes[7] = 127;
        Assert.assertEquals("water.AutoBufferTest$XYZZY\ufffd\u0017b)`\ufffd\ufffd|\u0002\u0000\u0002I\u0000\u0001", AutoBuffer.nameOfClass(bytes));
        bytes[7] = 1;
        Assert.assertEquals("wat", AutoBuffer.nameOfClass(bytes));
        bytes[7] = -10;
        Assert.assertEquals("wat", AutoBuffer.nameOfClass(bytes));
        Assert.assertEquals("(null)", AutoBuffer.nameOfClass(null));
        Assert.assertEquals("(no name)", AutoBuffer.nameOfClass(new byte[]{ 0, 0, 0, 0, 0 }));
    }
}

