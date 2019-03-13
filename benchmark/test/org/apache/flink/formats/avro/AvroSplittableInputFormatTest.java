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
package org.apache.flink.formats.avro;


import java.io.File;
import java.io.IOException;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.generated.Colors;
import org.apache.flink.formats.avro.generated.User;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the avro input format.
 * (The testcase is mostly the getting started tutorial of avro)
 * http://avro.apache.org/docs/current/gettingstartedjava.html
 */
public class AvroSplittableInputFormatTest {
    private File testFile;

    static final String TEST_NAME = "Alyssa";

    static final String TEST_ARRAY_STRING_1 = "ELEMENT 1";

    static final String TEST_ARRAY_STRING_2 = "ELEMENT 2";

    static final boolean TEST_ARRAY_BOOLEAN_1 = true;

    static final boolean TEST_ARRAY_BOOLEAN_2 = false;

    static final Colors TEST_ENUM_COLOR = Colors.GREEN;

    static final String TEST_MAP_KEY1 = "KEY 1";

    static final long TEST_MAP_VALUE1 = 8546456L;

    static final String TEST_MAP_KEY2 = "KEY 2";

    static final long TEST_MAP_VALUE2 = 17554L;

    static final Integer TEST_NUM = 239;

    static final String TEST_STREET = "Baker Street";

    static final String TEST_CITY = "London";

    static final String TEST_STATE = "London";

    static final String TEST_ZIP = "NW1 6XE";

    static final int NUM_RECORDS = 5000;

    @Test
    public void testSplittedIF() throws IOException {
        Configuration parameters = new Configuration();
        AvroInputFormat<User> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
        format.configure(parameters);
        FileInputSplit[] splits = format.createInputSplits(4);
        Assert.assertEquals(splits.length, 4);
        int elements = 0;
        int[] elementsPerSplit = new int[4];
        for (int i = 0; i < (splits.length); i++) {
            format.open(splits[i]);
            while (!(format.reachedEnd())) {
                User u = format.nextRecord(null);
                Assert.assertTrue(u.getName().toString().startsWith(AvroSplittableInputFormatTest.TEST_NAME));
                elements++;
                (elementsPerSplit[i])++;
            } 
            format.close();
        }
        Assert.assertEquals(1604, elementsPerSplit[0]);
        Assert.assertEquals(1203, elementsPerSplit[1]);
        Assert.assertEquals(1203, elementsPerSplit[2]);
        Assert.assertEquals(990, elementsPerSplit[3]);
        Assert.assertEquals(AvroSplittableInputFormatTest.NUM_RECORDS, elements);
        format.close();
    }

    @Test
    public void testAvroRecoveryWithFailureAtStart() throws Exception {
        final int recordsUntilCheckpoint = 132;
        Configuration parameters = new Configuration();
        AvroInputFormat<User> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
        format.configure(parameters);
        FileInputSplit[] splits = format.createInputSplits(4);
        Assert.assertEquals(splits.length, 4);
        int elements = 0;
        int[] elementsPerSplit = new int[4];
        for (int i = 0; i < (splits.length); i++) {
            format.reopen(splits[i], format.getCurrentState());
            while (!(format.reachedEnd())) {
                User u = format.nextRecord(null);
                Assert.assertTrue(u.getName().toString().startsWith(AvroSplittableInputFormatTest.TEST_NAME));
                elements++;
                if ((format.getRecordsReadFromBlock()) == recordsUntilCheckpoint) {
                    // do the whole checkpoint-restore procedure and see if we pick up from where we left off.
                    Tuple2<Long, Long> state = format.getCurrentState();
                    // this is to make sure that nothing stays from the previous format
                    // (as it is going to be in the normal case)
                    format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
                    format.reopen(splits[i], state);
                    Assert.assertEquals(format.getRecordsReadFromBlock(), recordsUntilCheckpoint);
                }
                (elementsPerSplit[i])++;
            } 
            format.close();
        }
        Assert.assertEquals(1604, elementsPerSplit[0]);
        Assert.assertEquals(1203, elementsPerSplit[1]);
        Assert.assertEquals(1203, elementsPerSplit[2]);
        Assert.assertEquals(990, elementsPerSplit[3]);
        Assert.assertEquals(AvroSplittableInputFormatTest.NUM_RECORDS, elements);
        format.close();
    }

    @Test
    public void testAvroRecovery() throws Exception {
        final int recordsUntilCheckpoint = 132;
        Configuration parameters = new Configuration();
        AvroInputFormat<User> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
        format.configure(parameters);
        FileInputSplit[] splits = format.createInputSplits(4);
        Assert.assertEquals(splits.length, 4);
        int elements = 0;
        int[] elementsPerSplit = new int[4];
        for (int i = 0; i < (splits.length); i++) {
            format.open(splits[i]);
            while (!(format.reachedEnd())) {
                User u = format.nextRecord(null);
                Assert.assertTrue(u.getName().toString().startsWith(AvroSplittableInputFormatTest.TEST_NAME));
                elements++;
                if ((format.getRecordsReadFromBlock()) == recordsUntilCheckpoint) {
                    // do the whole checkpoint-restore procedure and see if we pick up from where we left off.
                    Tuple2<Long, Long> state = format.getCurrentState();
                    // this is to make sure that nothing stays from the previous format
                    // (as it is going to be in the normal case)
                    format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
                    format.reopen(splits[i], state);
                    Assert.assertEquals(format.getRecordsReadFromBlock(), recordsUntilCheckpoint);
                }
                (elementsPerSplit[i])++;
            } 
            format.close();
        }
        Assert.assertEquals(1604, elementsPerSplit[0]);
        Assert.assertEquals(1203, elementsPerSplit[1]);
        Assert.assertEquals(1203, elementsPerSplit[2]);
        Assert.assertEquals(990, elementsPerSplit[3]);
        Assert.assertEquals(AvroSplittableInputFormatTest.NUM_RECORDS, elements);
        format.close();
    }
}

