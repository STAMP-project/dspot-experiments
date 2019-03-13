package water;


import H2O.CLOUD;
import H2O.CLOUD._memary;
import H2O.SELF;
import Vec.T_NUM;
import Vec.T_STR;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;

import static ExternalFrameUtils.EXPECTED_DOUBLE;
import static ExternalFrameUtils.EXPECTED_STRING;


public class ExternalFrameReaderClientTest extends TestUtil {
    // We keep this one for assertion errors occurred in different threads
    private volatile AssertionError exc;

    @Test
    public void testReading() throws IOException, InterruptedException, ExternalFrameConfirmationException {
        final String frameName = "testFrame";
        final long[] chunkLayout = new long[]{ 2, 2, 2, 1 };
        final Frame testFrame = new TestFrameBuilder().withName(frameName).withColNames("ColA", "ColB").withVecTypes(T_NUM, T_STR).withDataForCol(0, TestUtil.ard(Double.NaN, 1, 2, 3, 4, 5.6, 7, (-1), 3.14)).withDataForCol(1, TestUtil.ar("A", "B", "C", "E", "F", "I", "J", "\u0000", null)).withChunkLayout(chunkLayout).build();
        // create frame
        final String[] nodes = new String[_memary.length];
        // get ip and ports of h2o nodes
        for (int i = 0; i < (nodes.length); i++) {
            nodes[i] = CLOUD._memary[i].getIpPortString();
        }
        final int[] selectedColumnIndices = new int[]{ 0, 1 };
        // specify expected types for selected columns
        final byte[] expectedTypes = new byte[]{ EXPECTED_DOUBLE, EXPECTED_STRING };
        final int nChunks = testFrame.anyVec().nChunks();
        // we will read from all chunks at the same time
        Thread[] threads = new Thread[nChunks];
        try {
            // open all connections in connStrings array
            for (int idx = 0; idx < nChunks; idx++) {
                final int currentChunkIdx = idx;
                threads[idx] = new Thread() {
                    @Override
                    public void run() {
                        try {
                            ByteChannel sock = ExternalFrameUtils.getConnection(nodes[(currentChunkIdx % (nodes.length))], SELF.getTimestamp());
                            ExternalFrameReaderClient reader = new ExternalFrameReaderClient(sock, frameName, currentChunkIdx, selectedColumnIndices, expectedTypes);
                            int rowsRead = 0;
                            Assert.assertEquals(reader.getNumRows(), chunkLayout[currentChunkIdx]);
                            while (rowsRead < (reader.getNumRows())) {
                                if ((rowsRead == 0) & (currentChunkIdx == 0)) {
                                    reader.readDouble();
                                    Assert.assertTrue("[0,0] in chunk 0 should be NA", reader.isLastNA());
                                } else {
                                    reader.readDouble();
                                    Assert.assertFalse("Should not be NA", reader.isLastNA());
                                }
                                reader.readString();
                                Assert.assertFalse("Should not be NA", reader.isLastNA());
                                rowsRead++;
                            } 
                            Assert.assertEquals(((("Num or rows read was " + rowsRead) + ", expecting ") + (reader.getNumRows())), rowsRead, reader.getNumRows());
                            reader.waitUntilAllReceived(10);
                            sock.close();
                        } catch (AssertionError e) {
                            exc = e;
                        } catch (ExternalFrameConfirmationException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                threads[idx].start();
            }
            // wait for all writer thread to finish
            for (Thread t : threads) {
                t.join();
                if ((exc) != null) {
                    throw exc;
                }
            }
        } finally {
            testFrame.remove();
        }
    }
}

