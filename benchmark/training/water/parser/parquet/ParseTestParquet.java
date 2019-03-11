package water.parser.parquet;


import ParseSetup.GUESS_HEADER;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.Vec;
import water.parser.BufferedString;
import water.parser.ParseDataset;
import water.parser.ParseSetup;
import water.util.IcedInt;
import water.util.PrettyPrint;


/**
 * Test suite for Parquet parser.
 */
@RunWith(Parameterized.class)
public class ParseTestParquet extends TestUtil {
    private static double EPSILON = 1.0E-9;

    @Parameterized.Parameter
    public boolean disableParallelParse;

    public ParseSetupTransformer psTransformer;

    @Test
    public void testParseSimple() {
        Frame expected = null;
        Frame actual = null;
        try {
            expected = parse_test_file("smalldata/airlines/AirlinesTrain.csv.zip");
            actual = parse_parquet("smalldata/parser/parquet/airlines-simple.snappy.parquet");
            Assert.assertEquals(Arrays.asList(expected._names), Arrays.asList(actual._names));
            Assert.assertEquals(Arrays.asList(expected.typesStr()), Arrays.asList(actual.typesStr()));
            Assert.assertEquals(expected.numRows(), actual.numRows());
            Assert.assertTrue(isBitIdentical(expected, actual));
        } finally {
            if (expected != null)
                expected.delete();

            if (actual != null)
                actual.delete();

        }
    }

    @Test
    public void testParseWithTypeOverride() {
        Frame expected = null;
        Frame actual = null;
        try {
            NFSFileVec nfs = makeNfsFileVec("smalldata/parser/parquet/airlines-simple.snappy.parquet");
            Key[] keys = new Key[]{ nfs._key };
            ParseSetup guessedSetup = ParseSetup.guessSetup(keys, false, GUESS_HEADER);
            // attempt to override a Enum type to String
            byte[] types = guessedSetup.getColumnTypes();
            types[1] = Vec.T_STR;
            guessedSetup.setColumnTypes(types);
            guessedSetup.disableParallelParse = disableParallelParse;
            // parse the file with the modified setup
            ParseDataset pd = ParseDataset.forkParseDataset(Key.<Frame>make(), keys, guessedSetup, true);
            actual = pd._job.get();
            expected = parse_test_file("smalldata/airlines/AirlinesTrain.csv.zip");
            expected.replace(1, expected.vec(1).toStringVec()).remove();
            // type is String instead of Enum
            Assert.assertEquals("String", actual.typesStr()[1]);
            Assert.assertEquals(Arrays.asList(expected._names), Arrays.asList(actual._names));
            Assert.assertEquals(Arrays.asList(expected.typesStr()), Arrays.asList(actual.typesStr()));
            Assert.assertTrue(isBitIdentical(expected, actual));
            // no warnings were generated
            Assert.assertNull(pd._job.warns());
        } finally {
            if (expected != null)
                expected.delete();

            if (actual != null)
                actual.delete();

        }
    }

    @Test
    public void testParseWithInvalidTypeOverride() {
        Frame expected = null;
        Frame actual = null;
        try {
            NFSFileVec nfs = makeNfsFileVec("smalldata/parser/parquet/airlines-simple.snappy.parquet");
            Key[] keys = new Key[]{ nfs._key };
            ParseSetup guessedSetup = ParseSetup.guessSetup(keys, false, GUESS_HEADER);
            // attempt to override a Numeric type to String
            byte[] types = guessedSetup.getColumnTypes();
            types[9] = Vec.T_STR;
            guessedSetup.setColumnTypes(types);
            guessedSetup.disableParallelParse = disableParallelParse;
            // parse the file with the modified setup
            ParseDataset pd = ParseDataset.forkParseDataset(Key.<Frame>make(), keys, guessedSetup, true);
            actual = pd._job.get();
            // type stayed the same
            Assert.assertEquals("Numeric", actual.typesStr()[9]);
            expected = parse_test_file("smalldata/airlines/AirlinesTrain.csv.zip");
            Assert.assertEquals(Arrays.asList(expected._names), Arrays.asList(actual._names));
            Assert.assertEquals(Arrays.asList(expected.typesStr()), Arrays.asList(actual.typesStr()));
            Assert.assertTrue(isBitIdentical(expected, actual));
            // proper warnings were generated
            Assert.assertEquals(1, pd._job.warns().length);
            Assert.assertTrue(pd._job.warns()[0].endsWith("error = 'Unsupported type override (Numeric -> String). Column Distance will be parsed as Numeric'"));
        } finally {
            if (expected != null)
                expected.delete();

            if (actual != null)
                actual.delete();

        }
    }

    @Test
    public void testParseMulti() {
        final int nFiles = 10;
        FrameAssertion assertion = new GenFrameAssertion("testParseMulti-$.parquet", TestUtil.ari(9, 100), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                File dir = Files.createTempDir();
                for (int i = 0; i < nFiles; i++) {
                    String fName = file.replace("$", String.valueOf(i));
                    File f = ParquetFileGenerator.generateAvroPrimitiveTypes(dir, fName, ((nrows()) / nFiles), new Date());
                    File crcF = new File(f.getCanonicalPath().replace(fName, (("." + fName) + ".crc")));
                    if ((crcF.exists()) && (!(crcF.delete())))
                        throw new IllegalStateException(("Unable to delete Parquet CRC for file: " + f));

                }
                return dir;
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("myboolean", "myint", "mylong", "myfloat", "mydouble", "mydate", "myuuid", "mystring", "myenum"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_TIME, Vec.T_UUID, Vec.T_STR, Vec.T_CAT), f.types());
            }
        };
        assertFrameAssertion(assertion);
    }

    /**
     * Test parsing of Parquet file originally made from Avro records (avro < 1.8, before introduction of logical types)
     */
    @Test
    public void testParseAvroPrimitiveTypes() {
        FrameAssertion assertion = new GenFrameAssertion("avroPrimitiveTypes.parquet", TestUtil.ari(9, 100), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateAvroPrimitiveTypes(Files.createTempDir(), file, nrows(), new Date());
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("myboolean", "myint", "mylong", "myfloat", "mydouble", "mydate", "myuuid", "mystring", "myenum"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_TIME, Vec.T_UUID, Vec.T_STR, Vec.T_CAT), f.types());
                BufferedString bs = new BufferedString();
                for (int row = 0; row < (nrows()); row++) {
                    Assert.assertEquals("Value in column myboolean", (1 - (row % 2)), f.vec(0).at8(row));
                    Assert.assertEquals("Value in column myint", (1 + row), f.vec(1).at8(row));
                    Assert.assertEquals("Value in column mylong", (2 + row), f.vec(2).at8(row));
                    Assert.assertEquals("Value in column myfloat", (3.1F + row), f.vec(3).at(row), ParseTestParquet.EPSILON);
                    Assert.assertEquals("Value in column myfloat", (4.1 + row), f.vec(4).at(row), ParseTestParquet.EPSILON);
                    Assert.assertEquals("Value in column mystring", ("hello world: " + row), f.vec(7).atStr(bs, row).toSanitizedString());
                    Assert.assertEquals("Value in column myenum", ((row % 2) == 0 ? "a" : "b"), f.vec(8).factor(f.vec(8).at8(row)));
                }
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testParseTimestamps() {
        final Date date = new Date();
        FrameAssertion assertion = new GenFrameAssertion("avroPrimitiveTypes.parquet", TestUtil.ari(5, 100), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateParquetFile(Files.createTempDir(), file, nrows(), date);
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("int32_field", "int64_field", "float_field", "double_field", "timestamp_field"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_TIME), f.types());
                for (int row = 0; row < (nrows()); row++) {
                    Assert.assertEquals("Value in column int32_field", (32 + row), f.vec(0).at8(row));
                    Assert.assertEquals("Value in column timestamp_field", ((date.getTime()) + (row * 117)), f.vec(4).at8(row));
                }
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testParseSingleEmpty() {
        FrameAssertion assertion = new GenFrameAssertion("empty.parquet", TestUtil.ari(5, 0), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateEmptyWithSchema(Files.createTempDir(), file);
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("int32_field", "int64_field", "float_field", "double_field", "timestamp_field"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_TIME), f.types());
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testParseStringOverflow() {
        FrameAssertion assertion = new GenFrameAssertion("large.parquet", TestUtil.ari(1, 1), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateStringParquet(Files.createTempDir(), file);
            }

            @Override
            public Frame prepare() {
                try {
                    File f = super.generatedFile = prepareFile();
                    System.out.println(("File generated into: " + (f.getCanonicalPath())));
                    return parse_test_file(f.getCanonicalPath(), null, ParseSetup.HAS_HEADER, new byte[]{ Vec.T_STR }, psTransformer);
                } catch (IOException e) {
                    throw new RuntimeException(("Cannot prepare test frame from file: " + (file)), e);
                }
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("string_field"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_STR), f.types());
                Assert.assertEquals(1, f.naCount());
                Assert.assertEquals(1, f.numCols());
                Assert.assertEquals(1, f.numRows());
            }
        };
        Key<?> cfgKey = Key.make(((WriterDelegate.class.getCanonicalName()) + "_maxStringSize"));
        try {
            DKV.put(cfgKey, new IcedInt(6));
            assertFrameAssertion(assertion);
        } finally {
            DKV.remove(cfgKey);
        }
    }

    @Test
    public void testParseMultiWithEmpty() {
        final int nFiles = 10;
        FrameAssertion assertion = new GenFrameAssertion("testParseMultiEmpty-$.parquet", TestUtil.ari(5, 90), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                File dir = Files.createTempDir();
                for (int i = 0; i < (nFiles - 1); i++) {
                    final String fName = file.replace("$", String.valueOf(i));
                    final File f = ParquetFileGenerator.generateParquetFile(dir, fName, ((nrows()) / (nFiles - 1)), new Date());
                    final File crcF = new File(f.getCanonicalPath().replace(fName, (("." + fName) + ".crc")));
                    if ((crcF.exists()) && (!(crcF.delete())))
                        throw new IllegalStateException(("Unable to delete Parquet CRC for file: " + f));

                }
                final String emptyFileName = file.replace("$", String.valueOf((nFiles - 1)));
                File emptyFile = ParquetFileGenerator.generateEmptyWithSchema(dir, emptyFileName);
                final File crcEmptyFile = new File(emptyFile.getCanonicalPath().replace(emptyFileName, (("." + emptyFileName) + ".crc")));
                if ((crcEmptyFile.exists()) && (!(crcEmptyFile.delete())))
                    throw new IllegalStateException(("Unable to delete Parquet CRC for file: " + emptyFileName));

                return dir;
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("int32_field", "int64_field", "float_field", "double_field", "timestamp_field"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_TIME), f.types());
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testParseSparseColumns() {
        FrameAssertion assertion = new GenFrameAssertion("sparseColumns.parquet", TestUtil.ari(4, 100), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateSparseParquetFile(Files.createTempDir(), file, nrows());
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("int32_field", "string_field", "row", "int32_field2"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_CAT, Vec.T_NUM, Vec.T_NUM), f.types());
                for (int row = 0; row < (nrows()); row++) {
                    if ((row % 10) == 0) {
                        Assert.assertEquals("Value in column int32_field", row, f.vec(0).at8(row));
                        Assert.assertEquals("Value in column string_field", ("CAT_" + (row % 10)), f.vec(1).factor(f.vec(1).at8(row)));
                        Assert.assertEquals("Value in column int32_field2", row, f.vec(3).at8(row));
                    } else {
                        Assert.assertTrue(f.vec(0).isNA(row));
                        Assert.assertTrue(f.vec(1).isNA(row));
                        Assert.assertTrue(f.vec(3).isNA(row));
                    }
                    Assert.assertEquals("Value in column row", row, f.vec(2).at8(row));
                }
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testParseCategoricalsWithZeroCharacters() {
        FrameAssertion assertion = new GenFrameAssertion("nullCharacters.parquet", TestUtil.ari(1, 100), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateParquetFileWithNullCharacters(Files.createTempDir(), file, nrows());
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("cat_field"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_CAT), f.types());
                for (int row = 0; row < (nrows()); row++) {
                    String catValue = (row == 66) ? "CAT_0_weird\u0000" : "CAT_" + (row % 10);
                    Assert.assertEquals("Value in column string_field", catValue, f.vec(0).factor(f.vec(0).at8(row)));
                }
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testParseDecimals() {
        FrameAssertion assertion = new GenFrameAssertion("decimals.parquet", TestUtil.ari(2, 18), psTransformer) {
            @Override
            protected File prepareFile() throws IOException {
                return ParquetFileGenerator.generateParquetFileDecimals(Files.createTempDir(), file, nrows());
            }

            @Override
            public void check(Frame f) {
                Assert.assertArrayEquals("Column names need to match!", ar("decimal32", "decimal64"), f.names());
                Assert.assertArrayEquals("Column types need to match!", ar(Vec.T_NUM, Vec.T_NUM), f.types());
                for (int row = 0; row < (nrows()); row++) {
                    double expected32 = (1 + (PrettyPrint.pow10(1, (row % 9)))) / 100000.0;
                    Assert.assertEquals("Value in column decimal32", expected32, f.vec(0).at(row), 0);
                    double expected64 = (1 + (PrettyPrint.pow10(1, (row % 18)))) / 1.0E10;
                    Assert.assertEquals("Value in column decimal64", expected64, f.vec(1).at(row), 0);
                }
            }
        };
        assertFrameAssertion(assertion);
    }

    @Test
    public void testPubdev5673() {
        Frame actual = null;
        try {
            actual = parse_parquet("smalldata/jira/pubdev-5673.parquet");
            double actualVal = actual.vec(0).at(0);
            Assert.assertEquals(9.877654321199876E10, actualVal, 0);
        } finally {
            if (actual != null)
                actual.delete();

        }
    }
}

