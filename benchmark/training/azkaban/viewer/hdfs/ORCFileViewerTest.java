package azkaban.viewer.hdfs;


import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * <pre>
 * Test cases for ORCFileViewer
 *
 * Validate capability for ORC files Validate false capability for avro, text
 * and parquet schema for ORC files verify raw records from orc files
 * </pre>
 */
@Ignore
public class ORCFileViewerTest {
    ORCFileViewer viewer;

    Set<Capability> supportedCapabilities;

    Set<Capability> unSupportedCapabilities;

    FileSystem fs;

    /* verify capability for empty orc files */
    @Test
    public void orcEmptyFileCapability() throws IOException {
        Assert.assertEquals(this.supportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestOrcFile.emptyFile.orc")));
    }

    /* verify capability for generic orc files */
    @Test
    public void genericORCFileCapability() throws IOException {
        Assert.assertEquals(this.supportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestOrcFile.testPredicatePushdown.orc")));
    }

    /* verify capability for orc files with binary type */
    @Test
    public void binaryTypeORCFileCapability() throws IOException {
        Assert.assertEquals(this.supportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestOrcFile.testStringAndBinaryStatistics.orc")));
    }

    /* verify capability for snappy compressed orc file */
    @Test
    public void snappyCompressedORCFileCapability() throws IOException {
        Assert.assertEquals(this.supportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestOrcFile.testSnappy.orc")));
    }

    /* verify capability for union type orc file */
    @Test
    public void unionTypeORCFileCapability() throws IOException {
        Assert.assertEquals(this.supportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestOrcFile.testUnionAndTimestamp.orc")));
    }

    /* verify capability for avro files */
    @Test
    public void noAvroCapability() throws IOException {
        Assert.assertEquals(this.unSupportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestAvro.avro")));
    }

    /* verify capability for text files */
    @Test
    public void noTextCapability() throws IOException {
        Assert.assertEquals(this.unSupportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestTextFile.txt")));
    }

    /* verify capability for parquet files */
    @Test
    public void noParquetCapability() throws IOException {
        Assert.assertEquals(this.unSupportedCapabilities, this.viewer.getCapabilities(this.fs, getResourcePath("TestParquetFile.parquet")));
    }

    /* verify schema for empty orc files */
    @Test
    public void emptyORCFileSchema() throws IOException {
        final String schema = "struct<boolean1:boolean,byte1:tinyint,short1:smallint,int1:int,long1:bigint," + (("float1:float,double1:double,bytes1:binary,string1:string," + "middle:struct<list:array<struct<int1:int,string1:string>>>,") + "list:array<struct<int1:int,string1:string>>,map:map<string,struct<int1:int,string1:string>>>");
        Assert.assertEquals(schema, this.viewer.getSchema(this.fs, getResourcePath("TestOrcFile.emptyFile.orc")));
    }

    /* verify schema for generic orc files */
    @Test
    public void genericORCFileSchema() throws IOException {
        Assert.assertEquals("struct<int1:int,string1:string>", this.viewer.getSchema(this.fs, getResourcePath("TestOrcFile.testPredicatePushdown.orc")));
    }

    /* verify schema for orc files with binary type */
    @Test
    public void binaryTypeFileSchema() throws IOException {
        Assert.assertEquals("struct<bytes1:binary,string1:string>", this.viewer.getSchema(this.fs, getResourcePath("TestOrcFile.testStringAndBinaryStatistics.orc")));
    }

    /* verify schema for snappy compressed orc file */
    @Test
    public void snappyCompressedFileSchema() throws IOException {
        Assert.assertEquals("struct<int1:int,string1:string>", this.viewer.getSchema(this.fs, getResourcePath("TestOrcFile.testSnappy.orc")));
    }

    /* verify schema for union type orc file */
    @Test
    public void unionTypeFileSchema() throws IOException {
        Assert.assertEquals("struct<time:timestamp,union:uniontype<int,string>,decimal:decimal>", this.viewer.getSchema(this.fs, getResourcePath("TestOrcFile.testUnionAndTimestamp.orc")));
    }

    /* verify record display for empty orc files */
    @Test
    public void emptyORCFileDisplay() throws IOException {
        final String actual = displayRecordWrapper("TestOrcFile.emptyFile.orc", 1, 1);
        Assert.assertEquals("", actual);
    }

    /* verify record display for generic orc files */
    @Test
    public void genericORCFileDisplay() throws IOException {
        final String actual = displayRecordWrapper("TestOrcFile.testPredicatePushdown.orc", 2, 2);
        Assert.assertEquals("{\"int1\":300,\"string1\":\"a\"}", actual);
    }

    /* verify record display for orc files with binary type */
    @Test
    public void binaryTypeFileDisplay() throws IOException {
        final String actual = displayRecordWrapper("TestOrcFile.testStringAndBinaryStatistics.orc", 4, 4);
        Assert.assertEquals("{\"bytes1\":null,\"string1\":\"hi\"}", actual);
    }

    /* verify record display for snappy compressed orc file */
    @Test
    public void snappyCompressedFileDisplay() throws IOException {
        final String actual = displayRecordWrapper("TestOrcFile.testSnappy.orc", 2, 2);
        Assert.assertEquals("{\"int1\":1181413113,\"string1\":\"382fdaaa\"}", actual);
    }

    /* verify record display for union type orc file */
    @Test
    public void unionTypeFileDisplay() throws IOException {
        final String actual = displayRecordWrapper("TestOrcFile.testUnionAndTimestamp.orc", 5, 5);
        Assert.assertEquals("{\"decimal\":null,\"time\":null,\"union\":{\"1\":null}}", actual);
    }
}

