/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for EnumSetWritable
 */
public class TestEnumSetWritable {
    enum TestEnumSet {

        CREATE,
        OVERWRITE,
        APPEND;}

    EnumSet<TestEnumSetWritable.TestEnumSet> nonEmptyFlag = EnumSet.of(TestEnumSetWritable.TestEnumSet.APPEND);

    EnumSetWritable<TestEnumSetWritable.TestEnumSet> nonEmptyFlagWritable = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(nonEmptyFlag);

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializeAndDeserializeNonEmpty() throws IOException {
        DataOutputBuffer out = new DataOutputBuffer();
        ObjectWritable.writeObject(out, nonEmptyFlagWritable, nonEmptyFlagWritable.getClass(), null);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), out.getLength());
        EnumSet<TestEnumSetWritable.TestEnumSet> read = ((EnumSetWritable<TestEnumSetWritable.TestEnumSet>) (ObjectWritable.readObject(in, null))).get();
        Assert.assertEquals(read, nonEmptyFlag);
    }

    EnumSet<TestEnumSetWritable.TestEnumSet> emptyFlag = EnumSet.noneOf(TestEnumSetWritable.TestEnumSet.class);

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializeAndDeserializeEmpty() throws IOException {
        boolean gotException = false;
        try {
            new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(emptyFlag);
        } catch (RuntimeException e) {
            gotException = true;
        }
        Assert.assertTrue(("Instantiation of empty EnumSetWritable with no element type class " + "provided should throw exception."), gotException);
        EnumSetWritable<TestEnumSetWritable.TestEnumSet> emptyFlagWritable = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(emptyFlag, TestEnumSetWritable.TestEnumSet.class);
        DataOutputBuffer out = new DataOutputBuffer();
        ObjectWritable.writeObject(out, emptyFlagWritable, emptyFlagWritable.getClass(), null);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), out.getLength());
        EnumSet<TestEnumSetWritable.TestEnumSet> read = ((EnumSetWritable<TestEnumSetWritable.TestEnumSet>) (ObjectWritable.readObject(in, null))).get();
        Assert.assertEquals(read, emptyFlag);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializeAndDeserializeNull() throws IOException {
        boolean gotException = false;
        try {
            new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(null);
        } catch (RuntimeException e) {
            gotException = true;
        }
        Assert.assertTrue(("Instantiation of empty EnumSetWritable with no element type class " + "provided should throw exception"), gotException);
        EnumSetWritable<TestEnumSetWritable.TestEnumSet> nullFlagWritable = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(null, TestEnumSetWritable.TestEnumSet.class);
        DataOutputBuffer out = new DataOutputBuffer();
        ObjectWritable.writeObject(out, nullFlagWritable, nullFlagWritable.getClass(), null);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), out.getLength());
        EnumSet<TestEnumSetWritable.TestEnumSet> read = ((EnumSetWritable<TestEnumSetWritable.TestEnumSet>) (ObjectWritable.readObject(in, null))).get();
        Assert.assertEquals(read, null);
    }

    public EnumSetWritable<TestEnumSetWritable.TestEnumSet> testField;

    @Test
    public void testAvroReflect() throws Exception {
        String schema = "{\"type\":\"array\",\"items\":{\"type\":\"enum\"," + ((("\"name\":\"TestEnumSet\"," + "\"namespace\":\"org.apache.hadoop.io.TestEnumSetWritable$\",") + "\"symbols\":[\"CREATE\",\"OVERWRITE\",\"APPEND\"]},") + "\"java-class\":\"org.apache.hadoop.io.EnumSetWritable\"}");
        Type type = TestEnumSetWritable.class.getField("testField").getGenericType();
        AvroTestUtil.testReflect(nonEmptyFlagWritable, type, schema);
    }

    /**
     * test {@link EnumSetWritable} equals() method
     */
    @Test
    public void testEnumSetWritableEquals() {
        EnumSetWritable<TestEnumSetWritable.TestEnumSet> eset1 = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(EnumSet.of(TestEnumSetWritable.TestEnumSet.APPEND, TestEnumSetWritable.TestEnumSet.CREATE), TestEnumSetWritable.TestEnumSet.class);
        EnumSetWritable<TestEnumSetWritable.TestEnumSet> eset2 = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(EnumSet.of(TestEnumSetWritable.TestEnumSet.APPEND, TestEnumSetWritable.TestEnumSet.CREATE), TestEnumSetWritable.TestEnumSet.class);
        Assert.assertTrue("testEnumSetWritableEquals error !!!", eset1.equals(eset2));
        Assert.assertFalse("testEnumSetWritableEquals error !!!", eset1.equals(new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(EnumSet.of(TestEnumSetWritable.TestEnumSet.APPEND, TestEnumSetWritable.TestEnumSet.CREATE, TestEnumSetWritable.TestEnumSet.OVERWRITE), TestEnumSetWritable.TestEnumSet.class)));
        Assert.assertTrue("testEnumSetWritableEquals getElementType error !!!", eset1.getElementType().equals(TestEnumSetWritable.TestEnumSet.class));
    }

    /**
     * test {@code EnumSetWritable.write(DataOutputBuffer out)}
     *  and iteration by TestEnumSet through iterator().
     */
    @Test
    public void testEnumSetWritableWriteRead() throws Exception {
        EnumSetWritable<TestEnumSetWritable.TestEnumSet> srcSet = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>(EnumSet.of(TestEnumSetWritable.TestEnumSet.APPEND, TestEnumSetWritable.TestEnumSet.CREATE), TestEnumSetWritable.TestEnumSet.class);
        DataOutputBuffer out = new DataOutputBuffer();
        srcSet.write(out);
        EnumSetWritable<TestEnumSetWritable.TestEnumSet> dstSet = new EnumSetWritable<TestEnumSetWritable.TestEnumSet>();
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), out.getLength());
        dstSet.readFields(in);
        EnumSet<TestEnumSetWritable.TestEnumSet> result = dstSet.get();
        Iterator<TestEnumSetWritable.TestEnumSet> dstIter = result.iterator();
        Iterator<TestEnumSetWritable.TestEnumSet> srcIter = srcSet.iterator();
        while ((dstIter.hasNext()) && (srcIter.hasNext())) {
            Assert.assertEquals("testEnumSetWritableWriteRead error !!!", dstIter.next(), srcIter.next());
        } 
    }
}

