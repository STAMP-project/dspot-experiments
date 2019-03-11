/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.trevni;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static ValueType.FIXED32;
import static ValueType.INT;
import static ValueType.LONG;
import static ValueType.STRING;


@RunWith(Parameterized.class)
public class TestColumnFile {
    private static final File FILE = new File("target", "test.trv");

    private static final int COUNT = 1024 * 64;

    private String codec;

    private String checksum;

    public TestColumnFile(String codec, String checksum) {
        this.codec = codec;
        this.checksum = checksum;
    }

    @Test
    public void testEmptyFile() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta());
        out.writeTo(TestColumnFile.FILE);
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        Assert.assertEquals(0, in.getRowCount());
        Assert.assertEquals(0, in.getColumnCount());
        in.close();
    }

    @Test
    public void testEmptyColumn() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), new ColumnMetaData("test", INT));
        out.writeTo(TestColumnFile.FILE);
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        Assert.assertEquals(0, in.getRowCount());
        Assert.assertEquals(1, in.getColumnCount());
        ColumnValues<Integer> values = in.getValues("test");
        for (int i : values)
            throw new Exception("no value should be found");

        in.close();
    }

    @Test
    public void testInts() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), new ColumnMetaData("test", INT));
        Random random = TestUtil.createRandom();
        for (int i = 0; i < (TestColumnFile.COUNT); i++)
            out.writeRow(TestUtil.randomLength(random));

        out.writeTo(TestColumnFile.FILE);
        random = TestUtil.createRandom();
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        Assert.assertEquals(TestColumnFile.COUNT, in.getRowCount());
        Assert.assertEquals(1, in.getColumnCount());
        Iterator<Integer> i = in.getValues("test");
        int count = 0;
        while (i.hasNext()) {
            Assert.assertEquals(TestUtil.randomLength(random), ((int) (i.next())));
            count++;
        } 
        Assert.assertEquals(TestColumnFile.COUNT, count);
    }

    @Test
    public void testLongs() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), new ColumnMetaData("test", LONG));
        Random random = TestUtil.createRandom();
        for (int i = 0; i < (TestColumnFile.COUNT); i++)
            out.writeRow(random.nextLong());

        out.writeTo(TestColumnFile.FILE);
        random = TestUtil.createRandom();
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        Assert.assertEquals(TestColumnFile.COUNT, in.getRowCount());
        Assert.assertEquals(1, in.getColumnCount());
        Iterator<Long> i = in.getValues("test");
        int count = 0;
        while (i.hasNext()) {
            Assert.assertEquals(random.nextLong(), ((long) (i.next())));
            count++;
        } 
        Assert.assertEquals(TestColumnFile.COUNT, count);
    }

    @Test
    public void testStrings() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), new ColumnMetaData("test", STRING));
        Random random = TestUtil.createRandom();
        for (int i = 0; i < (TestColumnFile.COUNT); i++)
            out.writeRow(TestUtil.randomString(random));

        out.writeTo(TestColumnFile.FILE);
        random = TestUtil.createRandom();
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        Assert.assertEquals(TestColumnFile.COUNT, in.getRowCount());
        Assert.assertEquals(1, in.getColumnCount());
        Iterator<String> i = in.getValues("test");
        int count = 0;
        while (i.hasNext()) {
            Assert.assertEquals(TestUtil.randomString(random), i.next());
            count++;
        } 
        Assert.assertEquals(TestColumnFile.COUNT, count);
    }

    @Test
    public void testTwoColumn() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), new ColumnMetaData("a", FIXED32), new ColumnMetaData("b", STRING));
        Random random = TestUtil.createRandom();
        for (int i = 0; i < (TestColumnFile.COUNT); i++)
            out.writeRow(random.nextInt(), TestUtil.randomString(random));

        out.writeTo(TestColumnFile.FILE);
        random = TestUtil.createRandom();
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        Assert.assertEquals(TestColumnFile.COUNT, in.getRowCount());
        Assert.assertEquals(2, in.getColumnCount());
        Iterator<String> i = in.getValues("a");
        Iterator<String> j = in.getValues("b");
        int count = 0;
        while ((i.hasNext()) && (j.hasNext())) {
            Assert.assertEquals(random.nextInt(), i.next());
            Assert.assertEquals(TestUtil.randomString(random), j.next());
            count++;
        } 
        Assert.assertEquals(TestColumnFile.COUNT, count);
    }

    @Test
    public void testSeekLongs() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), new ColumnMetaData("test", LONG));
        Random random = TestUtil.createRandom();
        int seekCount = (TestColumnFile.COUNT) / 1024;
        int[] seekRows = new int[seekCount];
        Map<Integer, Integer> seekRowMap = new HashMap<>(seekCount);
        while ((seekRowMap.size()) < seekCount) {
            int row = random.nextInt(TestColumnFile.COUNT);
            if (!(seekRowMap.containsKey(row))) {
                seekRows[seekRowMap.size()] = row;
                seekRowMap.put(row, seekRowMap.size());
            }
        } 
        Long[] seekValues = new Long[seekCount];
        for (int i = 0; i < (TestColumnFile.COUNT); i++) {
            long l = random.nextLong();
            out.writeRow(l);
            if (seekRowMap.containsKey(i))
                seekValues[seekRowMap.get(i)] = l;

        }
        out.writeTo(TestColumnFile.FILE);
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        ColumnValues<Long> v = in.getValues("test");
        for (int i = 0; i < seekCount; i++) {
            v.seek(seekRows[i]);
            Assert.assertEquals(seekValues[i], v.next());
        }
    }

    @Test
    public void testSeekStrings() throws Exception {
        TestColumnFile.FILE.delete();
        ColumnFileWriter out = new ColumnFileWriter(createFileMeta(), hasIndexValues(true));
        Random random = TestUtil.createRandom();
        int seekCount = (TestColumnFile.COUNT) / 1024;
        Map<Integer, Integer> seekRowMap = new HashMap<>(seekCount);
        while ((seekRowMap.size()) < seekCount) {
            int row = random.nextInt(TestColumnFile.COUNT);
            if (!(seekRowMap.containsKey(row)))
                seekRowMap.put(row, seekRowMap.size());

        } 
        String[] values = new String[TestColumnFile.COUNT];
        for (int i = 0; i < (TestColumnFile.COUNT); i++)
            values[i] = TestUtil.randomString(random);

        Arrays.sort(values);
        String[] seekValues = new String[seekCount];
        for (int i = 0; i < (TestColumnFile.COUNT); i++) {
            out.writeRow(values[i]);
            if (seekRowMap.containsKey(i))
                seekValues[seekRowMap.get(i)] = values[i];

        }
        out.writeTo(TestColumnFile.FILE);
        ColumnFileReader in = new ColumnFileReader(TestColumnFile.FILE);
        ColumnValues<String> v = in.getValues("test");
        for (int i = 0; i < seekCount; i++) {
            v.seek(seekValues[i]);
            Assert.assertEquals(seekValues[i], v.next());
        }
    }
}

