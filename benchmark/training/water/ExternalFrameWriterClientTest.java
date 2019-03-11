package water;


import Vec.T_NUM;
import Vec.T_STR;
import Vec.T_TIME;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.parser.BufferedString;
import water.util.ArrayUtils;

import static ExternalFrameUtils.EXPECTED_BOOL;
import static ExternalFrameUtils.EXPECTED_INT;
import static ExternalFrameUtils.EXPECTED_STRING;
import static ExternalFrameUtils.EXPECTED_TIMESTAMP;
import static ExternalFrameUtils.EXPECTED_VECTOR;


/**
 * Test external frame writer test
 */
public class ExternalFrameWriterClientTest extends TestUtil {
    @Test
    public void testWriting() {
        // The api expects that empty frame has to be in the DKV before we start working with it
        final Timestamp time = new Timestamp(Calendar.getInstance().getTime().getTime());
        WriteOperation testOp = new WriteOperation() {
            @Override
            public void doWrite(ExternalFrameWriterClient writer) throws IOException {
                for (int i = 0; i < 997; i++) {
                    writer.sendInt(i);
                    writer.sendBoolean(true);
                    writer.sendString(("str_" + i));
                    writer.sendTimestamp(time);
                }
                writer.sendInt(0);
                writer.sendBoolean(false);
                writer.sendString(null);
                writer.sendTimestamp(time);
                writer.sendInt(1);
                writer.sendBoolean(true);
                writer.sendString("\u0080");
                writer.sendTimestamp(time);
                // send NA for all columns
                writer.sendNA();
                writer.sendNA();
                writer.sendNA();
                writer.sendNA();
            }

            @Override
            public int nrows() {
                return 1000;
            }

            @Override
            public String[] colNames() {
                return new String[]{ "NUM", "BOOL", "STR", "TIMESTAMP" };
            }

            @Override
            public byte[] colTypes() {
                return new byte[]{ EXPECTED_INT, EXPECTED_BOOL, EXPECTED_STRING, EXPECTED_TIMESTAMP };
            }
        };
        final String[] nodes = ExternalFrameWriterClientTest.getH2ONodes();
        // we will open 2 connection per h2o node
        final String[] connStrings = ArrayUtils.join(nodes, nodes);
        Frame frame = ExternalFrameWriterClientTest.createFrame(testOp, connStrings);
        try {
            Assert.assertEquals(frame.anyVec().nChunks(), connStrings.length);
            Assert.assertEquals(frame._names.length, 4);
            Assert.assertEquals(frame.numCols(), 4);
            Assert.assertEquals(frame._names[0], "NUM");
            Assert.assertEquals(frame._names[1], "BOOL");
            Assert.assertEquals(frame._names[2], "STR");
            Assert.assertEquals(frame._names[3], "TIMESTAMP");
            Assert.assertEquals(frame.vec(0).get_type(), T_NUM);
            Assert.assertEquals(frame.vec(1).get_type(), T_NUM);
            Assert.assertEquals(frame.vec(2).get_type(), T_STR);
            Assert.assertEquals(frame.vec(3).get_type(), T_TIME);
            Assert.assertEquals(frame.numRows(), (1000 * (connStrings.length)));
            BufferedString buff = new BufferedString();
            for (int i = 0; i < (connStrings.length); i++) {
                // Writer segment (=chunk)
                for (int localRow = 0; localRow < 997; localRow++) {
                    // Local row in segment
                    Assert.assertEquals(localRow, frame.vec(0).at8((localRow + ((testOp.nrows()) * i))));
                    Assert.assertEquals(1, frame.vec(1).at8((localRow + ((testOp.nrows()) * i))));
                    Assert.assertEquals(("str_" + localRow), frame.vec(2).atStr(buff, (localRow + ((testOp.nrows()) * i))).toString());
                    Assert.assertEquals(time.getTime(), frame.vec(3).at8((localRow + ((testOp.nrows()) * i))));
                }
                // Row 997
                int row = 997;
                Assert.assertEquals(0, frame.vec(0).at8((row + ((testOp.nrows()) * i))));
                Assert.assertEquals(0, frame.vec(1).at8((row + ((testOp.nrows()) * i))));
                Assert.assertTrue(frame.vec(2).isNA((row + ((testOp.nrows()) * i))));
                Assert.assertEquals(time.getTime(), frame.vec(3).at8((row + ((testOp.nrows()) * i))));
                // Row 998
                row = 998;
                Assert.assertEquals(1, frame.vec(0).at8((row + ((testOp.nrows()) * i))));
                Assert.assertEquals(1, frame.vec(1).at8((row + ((testOp.nrows()) * i))));
                Assert.assertEquals("\u0080", frame.vec(2).atStr(buff, (row + ((testOp.nrows()) * i))).toString());
                Assert.assertEquals(time.getTime(), frame.vec(3).at8((row + ((testOp.nrows()) * i))));
                // Row 999
                row = 999;
                for (int c = 0; c < 4; c++) {
                    Assert.assertTrue(frame.vec(c).isNA((row + ((testOp.nrows()) * i))));
                }
            }
        } finally {
            frame.remove();
        }
    }

    @Test
    public void testDenseVectorWrite() throws IOException {
        WriteOperation testOp = new WriteOperation() {
            private static final int VEC_LEN = 100;

            @Override
            public void doWrite(ExternalFrameWriterClient writer) throws IOException {
                for (int i = 0; i < (nrows()); i++) {
                    writer.sendDenseVector(ExternalFrameWriterClientTest.vector(i, i, VEC_LEN));
                }
            }

            @Override
            public int nrows() {
                return 10;
            }

            @Override
            public String[] colNames() {
                return ExternalFrameWriterClientTest.names("DV", VEC_LEN);
            }

            @Override
            public byte[] colTypes() {
                return new byte[]{ EXPECTED_VECTOR };
            }

            @Override
            public int[] maxVecSizes() {
                return new int[]{ VEC_LEN };
            }
        };
        ExternalFrameWriterClientTest.assertVectorWrite(testOp);
    }

    @Test
    public void testSparseVectorWrite() throws IOException {
        WriteOperation testOp = new WriteOperation() {
            private static final int VEC_LEN = 100;

            @Override
            public void doWrite(ExternalFrameWriterClient writer) throws IOException {
                for (int i = 0; i < (nrows()); i++) {
                    writer.sendSparseVector(new int[]{ i }, new double[]{ i });
                }
            }

            @Override
            public int nrows() {
                return 10;
            }

            @Override
            public String[] colNames() {
                return ExternalFrameWriterClientTest.names("SV", VEC_LEN);
            }

            @Override
            public byte[] colTypes() {
                return new byte[]{ ExternalFrameUtils.EXPECTED_VECTOR };
            }

            @Override
            public int[] maxVecSizes() {
                return new int[]{ VEC_LEN };
            }
        };
        ExternalFrameWriterClientTest.assertVectorWrite(testOp);
    }

    @Test
    public void testMixedVectorWrite() throws IOException {
        WriteOperation testOp = new WriteOperation() {
            private static final int VEC_LEN = 100;

            @Override
            public void doWrite(ExternalFrameWriterClient writer) throws IOException {
                for (int i = 0; i < (nrows()); i++) {
                    writer.sendSparseVector(new int[]{ i }, new double[]{ i });
                    writer.sendInt(i);
                    writer.sendDenseVector(ExternalFrameWriterClientTest.vector(i, i, VEC_LEN));
                }
            }

            @Override
            public int nrows() {
                return 10;
            }

            @Override
            public String[] colNames() {
                return ArrayUtils.join(ArrayUtils.join(ExternalFrameWriterClientTest.names("SV", VEC_LEN), new String[]{ "ROW_ID" }), ExternalFrameWriterClientTest.names("DV", VEC_LEN));
            }

            @Override
            public byte[] colTypes() {
                return new byte[]{ ExternalFrameUtils.EXPECTED_VECTOR, ExternalFrameUtils.EXPECTED_INT, ExternalFrameUtils.EXPECTED_VECTOR };
            }

            @Override
            public int[] maxVecSizes() {
                return new int[]{ VEC_LEN, VEC_LEN };
            }
        };
        final String[] nodes = ExternalFrameWriterClientTest.getH2ONodes();
        // we will open 2 connection per h2o node
        final String[] connStrings = ArrayUtils.join(nodes, nodes);
        Frame frame = ExternalFrameWriterClientTest.createFrame(testOp, connStrings);
        try {
            Assert.assertEquals("Number of columns", testOp.colNames().length, frame.numCols());
            Assert.assertEquals("Number of rows", ((testOp.nrows()) * (connStrings.length)), frame.numRows());
        } finally {
            frame.delete();
        }
    }
}

