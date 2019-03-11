/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.arrow;


import ColumnType.Double;
import ColumnType.NDArray;
import Schema.Builder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import junit.framework.TestCase;
import lombok.val;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.TimeStampMilliVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.VectorUnloader;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.FloatingPointPrecision;
import org.apache.arrow.vector.types.pojo.Field;
import org.datavec.api.records.Record;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.metadata.RecordMetaDataIndex;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.schema.Schema;
import org.datavec.arrow.recordreader.ArrowRecordReader;
import org.datavec.arrow.recordreader.ArrowWritableRecordBatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


public class ArrowConverterTest {
    private static BufferAllocator bufferAllocator = new RootAllocator(Long.MAX_VALUE);

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testToArrayFromINDArray() {
        Schema.Builder schemaBuilder = new Schema.Builder();
        schemaBuilder.addColumnNDArray("outputArray", new long[]{ 1, 4 });
        Schema schema = schemaBuilder.build();
        int numRows = 4;
        List<List<Writable>> ret = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; i++) {
            ret.add(Arrays.<Writable>asList(new NDArrayWritable(Nd4j.linspace(1, 4, 4).reshape(1, 4))));
        }
        List<FieldVector> fieldVectors = ArrowConverter.toArrowColumns(ArrowConverterTest.bufferAllocator, schema, ret);
        ArrowWritableRecordBatch arrowWritableRecordBatch = new ArrowWritableRecordBatch(fieldVectors, schema);
        INDArray array = ArrowConverter.toArray(arrowWritableRecordBatch);
        Assert.assertArrayEquals(new long[]{ 4, 4 }, array.shape());
        INDArray assertion = Nd4j.repeat(Nd4j.linspace(1, 4, 4), 4).reshape(4, 4);
        Assert.assertEquals(assertion, array);
    }

    @Test
    public void testArrowColumnINDArray() {
        Schema.Builder schema = new Schema.Builder();
        List<String> single = new ArrayList<>();
        int numCols = 2;
        INDArray arr = Nd4j.linspace(1, 4, 4);
        for (int i = 0; i < numCols; i++) {
            schema.addColumnNDArray(String.valueOf(i), new long[]{ 1, 4 });
            single.add(String.valueOf(i));
        }
        Schema buildSchema = schema.build();
        List<List<Writable>> list = new ArrayList<>();
        List<Writable> firstRow = new ArrayList<>();
        for (int i = 0; i < numCols; i++) {
            firstRow.add(new NDArrayWritable(arr));
        }
        list.add(firstRow);
        List<FieldVector> fieldVectors = ArrowConverter.toArrowColumns(ArrowConverterTest.bufferAllocator, buildSchema, list);
        Assert.assertEquals(numCols, fieldVectors.size());
        Assert.assertEquals(1, fieldVectors.get(0).getValueCount());
        Assert.assertFalse(fieldVectors.get(0).isNull(0));
        ArrowWritableRecordBatch arrowWritableRecordBatch = ArrowConverter.toArrowWritables(fieldVectors, buildSchema);
        Assert.assertEquals(1, arrowWritableRecordBatch.size());
        Writable writable = arrowWritableRecordBatch.get(0).get(0);
        TestCase.assertTrue((writable instanceof NDArrayWritable));
        NDArrayWritable ndArrayWritable = ((NDArrayWritable) (writable));
        Assert.assertEquals(arr, ndArrayWritable.get());
        Writable writable1 = ArrowConverter.fromEntry(0, fieldVectors.get(0), NDArray);
        NDArrayWritable ndArrayWritablewritable1 = ((NDArrayWritable) (writable1));
        System.out.println(ndArrayWritablewritable1.get());
    }

    @Test
    public void testArrowColumnString() {
        Schema.Builder schema = new Schema.Builder();
        List<String> single = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            schema.addColumnInteger(String.valueOf(i));
            single.add(String.valueOf(i));
        }
        List<FieldVector> fieldVectors = ArrowConverter.toArrowColumnsStringSingle(ArrowConverterTest.bufferAllocator, schema.build(), single);
        List<List<Writable>> records = ArrowConverter.toArrowWritables(fieldVectors, schema.build());
        List<List<Writable>> assertion = new ArrayList<>();
        assertion.add(Arrays.<Writable>asList(new IntWritable(0), new IntWritable(1)));
        Assert.assertEquals(assertion, records);
        List<List<String>> batch = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            batch.add(Arrays.asList(String.valueOf(i), String.valueOf(i)));
        }
        List<FieldVector> fieldVectorsBatch = ArrowConverter.toArrowColumnsString(ArrowConverterTest.bufferAllocator, schema.build(), batch);
        List<List<Writable>> batchRecords = ArrowConverter.toArrowWritables(fieldVectorsBatch, schema.build());
        List<List<Writable>> assertionBatch = new ArrayList<>();
        assertionBatch.add(Arrays.<Writable>asList(new IntWritable(0), new IntWritable(0)));
        assertionBatch.add(Arrays.<Writable>asList(new IntWritable(1), new IntWritable(1)));
        Assert.assertEquals(assertionBatch, batchRecords);
    }

    @Test
    public void testArrowBatchSetTime() {
        Schema.Builder schema = new Schema.Builder();
        List<String> single = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            schema.addColumnTime(String.valueOf(i), TimeZone.getDefault());
            single.add(String.valueOf(i));
        }
        List<List<Writable>> input = Arrays.asList(Arrays.<Writable>asList(new LongWritable(0), new LongWritable(1)), Arrays.<Writable>asList(new LongWritable(2), new LongWritable(3)));
        List<FieldVector> fieldVector = ArrowConverter.toArrowColumns(ArrowConverterTest.bufferAllocator, schema.build(), input);
        ArrowWritableRecordBatch writableRecordBatch = new ArrowWritableRecordBatch(fieldVector, schema.build());
        List<Writable> assertion = Arrays.<Writable>asList(new LongWritable(4), new LongWritable(5));
        writableRecordBatch.set(1, Arrays.<Writable>asList(new LongWritable(4), new LongWritable(5)));
        List<Writable> recordTest = writableRecordBatch.get(1);
        Assert.assertEquals(assertion, recordTest);
    }

    @Test
    public void testArrowBatchSet() {
        Schema.Builder schema = new Schema.Builder();
        List<String> single = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            schema.addColumnInteger(String.valueOf(i));
            single.add(String.valueOf(i));
        }
        List<List<Writable>> input = Arrays.asList(Arrays.<Writable>asList(new IntWritable(0), new IntWritable(1)), Arrays.<Writable>asList(new IntWritable(2), new IntWritable(3)));
        List<FieldVector> fieldVector = ArrowConverter.toArrowColumns(ArrowConverterTest.bufferAllocator, schema.build(), input);
        ArrowWritableRecordBatch writableRecordBatch = new ArrowWritableRecordBatch(fieldVector, schema.build());
        List<Writable> assertion = Arrays.<Writable>asList(new IntWritable(4), new IntWritable(5));
        writableRecordBatch.set(1, Arrays.<Writable>asList(new IntWritable(4), new IntWritable(5)));
        List<Writable> recordTest = writableRecordBatch.get(1);
        Assert.assertEquals(assertion, recordTest);
    }

    @Test
    public void testArrowColumnsStringTimeSeries() {
        Schema.Builder schema = new Schema.Builder();
        List<List<List<String>>> entries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            schema.addColumnInteger(String.valueOf(i));
        }
        for (int i = 0; i < 5; i++) {
            List<List<String>> arr = Arrays.asList(Arrays.asList(String.valueOf(i), String.valueOf(i), String.valueOf(i)));
            entries.add(arr);
        }
        List<FieldVector> fieldVectors = ArrowConverter.toArrowColumnsStringTimeSeries(ArrowConverterTest.bufferAllocator, schema.build(), entries);
        Assert.assertEquals(3, fieldVectors.size());
        Assert.assertEquals(5, fieldVectors.get(0).getValueCount());
        INDArray exp = Nd4j.create(5, 3);
        for (int i = 0; i < 5; i++) {
            exp.getRow(i).assign(i);
        }
        // Convert to ArrowWritableRecordBatch - note we can't do this in general with time series...
        ArrowWritableRecordBatch wri = ArrowConverter.toArrowWritables(fieldVectors, schema.build());
        INDArray arr = ArrowConverter.toArray(wri);
        Assert.assertArrayEquals(new long[]{ 5, 3 }, arr.shape());
        Assert.assertEquals(exp, arr);
    }

    @Test
    public void testConvertVector() {
        Schema.Builder schema = new Schema.Builder();
        List<List<List<String>>> entries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            schema.addColumnInteger(String.valueOf(i));
        }
        for (int i = 0; i < 5; i++) {
            List<List<String>> arr = Arrays.asList(Arrays.asList(String.valueOf(i), String.valueOf(i), String.valueOf(i)));
            entries.add(arr);
        }
        List<FieldVector> fieldVectors = ArrowConverter.toArrowColumnsStringTimeSeries(ArrowConverterTest.bufferAllocator, schema.build(), entries);
        INDArray arr = ArrowConverter.convertArrowVector(fieldVectors.get(0), schema.build().getType(0));
        Assert.assertEquals(5, arr.length());
    }

    @Test
    public void testCreateNDArray() throws Exception {
        val recordsToWrite = recordToWrite();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArrowConverter.writeRecordBatchTo(recordsToWrite.getRight(), recordsToWrite.getFirst(), byteArrayOutputStream);
        File f = testDir.newFolder();
        File tmpFile = new File(f, (("tmp-arrow-file-" + (UUID.randomUUID().toString())) + ".arrorw"));
        FileOutputStream outputStream = new FileOutputStream(tmpFile);
        tmpFile.deleteOnExit();
        ArrowConverter.writeRecordBatchTo(recordsToWrite.getRight(), recordsToWrite.getFirst(), outputStream);
        outputStream.flush();
        outputStream.close();
        Pair<Schema, ArrowWritableRecordBatch> schemaArrowWritableRecordBatchPair = ArrowConverter.readFromFile(tmpFile);
        Assert.assertEquals(recordsToWrite.getFirst(), schemaArrowWritableRecordBatchPair.getFirst());
        Assert.assertEquals(recordsToWrite.getRight(), schemaArrowWritableRecordBatchPair.getRight().toArrayList());
        byte[] arr = byteArrayOutputStream.toByteArray();
        val read = ArrowConverter.readFromBytes(arr);
        Assert.assertEquals(recordsToWrite, read);
        // send file
        File tmp = tmpDataFile(recordsToWrite);
        ArrowRecordReader recordReader = new ArrowRecordReader();
        recordReader.initialize(new FileSplit(tmp));
        recordReader.next();
        ArrowWritableRecordBatch currentBatch = recordReader.getCurrentBatch();
        INDArray arr2 = ArrowConverter.toArray(currentBatch);
        Assert.assertEquals(2, arr2.rows());
        Assert.assertEquals(2, arr2.columns());
    }

    @Test
    public void testConvertToArrowVectors() {
        INDArray matrix = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        val vectors = ArrowConverter.convertToArrowVector(matrix, Arrays.asList("test", "test2"), Double, ArrowConverterTest.bufferAllocator);
        Assert.assertEquals(matrix.rows(), vectors.size());
        INDArray vector = Nd4j.linspace(1, 4, 4);
        val vectors2 = ArrowConverter.convertToArrowVector(vector, Arrays.asList("test"), Double, ArrowConverterTest.bufferAllocator);
        Assert.assertEquals(1, vectors2.size());
        Assert.assertEquals(matrix.length(), vectors2.get(0).getValueCount());
    }

    @Test
    public void testSchemaConversionBasic() {
        Schema.Builder schemaBuilder = new Schema.Builder();
        for (int i = 0; i < 2; i++) {
            schemaBuilder.addColumnDouble(("test-" + i));
            schemaBuilder.addColumnInteger(("testi-" + i));
            schemaBuilder.addColumnLong(("testl-" + i));
            schemaBuilder.addColumnFloat(("testf-" + i));
        }
        Schema schema = schemaBuilder.build();
        val schema2 = ArrowConverter.toArrowSchema(schema);
        Assert.assertEquals(8, schema2.getFields().size());
        val convertedSchema = ArrowConverter.toDatavecSchema(schema2);
        Assert.assertEquals(schema, convertedSchema);
    }

    @Test
    public void testReadSchemaAndRecordsFromByteArray() throws Exception {
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
        int valueCount = 3;
        List<Field> fields = new ArrayList<>();
        fields.add(ArrowConverter.field("field1", new org.apache.arrow.vector.types.pojo.ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE)));
        fields.add(ArrowConverter.intField("field2"));
        List<FieldVector> fieldVectors = new ArrayList<>();
        fieldVectors.add(ArrowConverter.vectorFor(allocator, "field1", new float[]{ 1, 2, 3 }));
        fieldVectors.add(ArrowConverter.vectorFor(allocator, "field2", new int[]{ 1, 2, 3 }));
        org.apache.arrow.vector.types.pojo.Schema schema = new org.apache.arrow.vector.types.pojo.Schema(fields);
        VectorSchemaRoot schemaRoot1 = new VectorSchemaRoot(schema, fieldVectors, valueCount);
        VectorUnloader vectorUnloader = new VectorUnloader(schemaRoot1);
        vectorUnloader.getRecordBatch();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ArrowFileWriter arrowFileWriter = new ArrowFileWriter(schemaRoot1, null, Channels.newChannel(byteArrayOutputStream))) {
            arrowFileWriter.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] arr = byteArrayOutputStream.toByteArray();
        val arr2 = ArrowConverter.readFromBytes(arr);
        Assert.assertEquals(2, arr2.getFirst().numColumns());
        Assert.assertEquals(3, arr2.getRight().size());
        val arrowCols = ArrowConverter.toArrowColumns(allocator, arr2.getFirst(), arr2.getRight());
        Assert.assertEquals(2, arrowCols.size());
        Assert.assertEquals(valueCount, arrowCols.get(0).getValueCount());
    }

    @Test
    public void testVectorForEdgeCases() {
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
        val vector = ArrowConverter.vectorFor(allocator, "field1", new float[]{ Float.MIN_VALUE, Float.MAX_VALUE });
        Assert.assertEquals(Float.MIN_VALUE, vector.get(0), 0.01);
        Assert.assertEquals(Float.MAX_VALUE, vector.get(1), 0.01);
        val vectorInt = ArrowConverter.vectorFor(allocator, "field1", new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE });
        Assert.assertEquals(Integer.MIN_VALUE, vectorInt.get(0), 0.01);
        Assert.assertEquals(Integer.MAX_VALUE, vectorInt.get(1), 0.01);
    }

    @Test
    public void testVectorFor() {
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
        val vector = ArrowConverter.vectorFor(allocator, "field1", new float[]{ 1, 2, 3 });
        Assert.assertEquals(3, vector.getValueCount());
        Assert.assertEquals(1, vector.get(0), 0.01);
        Assert.assertEquals(2, vector.get(1), 0.01);
        Assert.assertEquals(3, vector.get(2), 0.01);
        val vectorLong = ArrowConverter.vectorFor(allocator, "field1", new long[]{ 1, 2, 3 });
        Assert.assertEquals(3, vectorLong.getValueCount());
        Assert.assertEquals(1, vectorLong.get(0), 0.01);
        Assert.assertEquals(2, vectorLong.get(1), 0.01);
        Assert.assertEquals(3, vectorLong.get(2), 0.01);
        val vectorInt = ArrowConverter.vectorFor(allocator, "field1", new int[]{ 1, 2, 3 });
        Assert.assertEquals(3, vectorInt.getValueCount());
        Assert.assertEquals(1, vectorInt.get(0), 0.01);
        Assert.assertEquals(2, vectorInt.get(1), 0.01);
        Assert.assertEquals(3, vectorInt.get(2), 0.01);
        val vectorDouble = ArrowConverter.vectorFor(allocator, "field1", new double[]{ 1, 2, 3 });
        Assert.assertEquals(3, vectorDouble.getValueCount());
        Assert.assertEquals(1, vectorDouble.get(0), 0.01);
        Assert.assertEquals(2, vectorDouble.get(1), 0.01);
        Assert.assertEquals(3, vectorDouble.get(2), 0.01);
        val vectorBool = ArrowConverter.vectorFor(allocator, "field1", new boolean[]{ true, true, false });
        Assert.assertEquals(3, vectorBool.getValueCount());
        Assert.assertEquals(1, vectorBool.get(0), 0.01);
        Assert.assertEquals(1, vectorBool.get(1), 0.01);
        Assert.assertEquals(0, vectorBool.get(2), 0.01);
    }

    @Test
    public void testRecordReaderAndWriteFile() throws Exception {
        val recordsToWrite = recordToWrite();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArrowConverter.writeRecordBatchTo(recordsToWrite.getRight(), recordsToWrite.getFirst(), byteArrayOutputStream);
        byte[] arr = byteArrayOutputStream.toByteArray();
        val read = ArrowConverter.readFromBytes(arr);
        Assert.assertEquals(recordsToWrite, read);
        // send file
        File tmp = tmpDataFile(recordsToWrite);
        RecordReader recordReader = new ArrowRecordReader();
        recordReader.initialize(new FileSplit(tmp));
        List<Writable> record = recordReader.next();
        Assert.assertEquals(2, record.size());
    }

    @Test
    public void testRecordReaderMetaDataList() throws Exception {
        val recordsToWrite = recordToWrite();
        // send file
        File tmp = tmpDataFile(recordsToWrite);
        RecordReader recordReader = new ArrowRecordReader();
        RecordMetaDataIndex recordMetaDataIndex = new RecordMetaDataIndex(0, tmp.toURI(), ArrowRecordReader.class);
        recordReader.loadFromMetaData(Arrays.<RecordMetaData>asList(recordMetaDataIndex));
        Record record = recordReader.nextRecord();
        Assert.assertEquals(2, record.getRecord().size());
    }

    @Test
    public void testDates() {
        Date now = new Date();
        BufferAllocator bufferAllocator = new RootAllocator(Long.MAX_VALUE);
        TimeStampMilliVector timeStampMilliVector = ArrowConverter.vectorFor(bufferAllocator, "col1", new Date[]{ now });
        Assert.assertEquals(now.getTime(), timeStampMilliVector.get(0));
    }

    @Test
    public void testRecordReaderMetaData() throws Exception {
        val recordsToWrite = recordToWrite();
        // send file
        File tmp = tmpDataFile(recordsToWrite);
        RecordReader recordReader = new ArrowRecordReader();
        RecordMetaDataIndex recordMetaDataIndex = new RecordMetaDataIndex(0, tmp.toURI(), ArrowRecordReader.class);
        recordReader.loadFromMetaData(recordMetaDataIndex);
        Record record = recordReader.nextRecord();
        Assert.assertEquals(2, record.getRecord().size());
    }
}

