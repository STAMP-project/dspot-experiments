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
package org.apache.hadoop.fs.shell;


import java.io.File;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the logic for displaying the binary formats supported
 * by the Text command.
 */
public class TestTextCommand {
    private static final File TEST_ROOT_DIR = GenericTestUtils.getTestDir("testText");

    private static final String AVRO_FILENAME = new File(TestTextCommand.TEST_ROOT_DIR, "weather.avro").toURI().getPath();

    private static final String TEXT_FILENAME = new File(TestTextCommand.TEST_ROOT_DIR, "testtextfile.txt").toURI().getPath();

    /**
     * Tests whether binary Avro data files are displayed correctly.
     */
    @Test(timeout = 30000)
    public void testDisplayForAvroFiles() throws Exception {
        String expectedOutput = (((((((("{\"station\":\"011990-99999\",\"time\":-619524000000,\"temp\":0}" + (System.getProperty("line.separator"))) + "{\"station\":\"011990-99999\",\"time\":-619506000000,\"temp\":22}") + (System.getProperty("line.separator"))) + "{\"station\":\"011990-99999\",\"time\":-619484400000,\"temp\":-11}") + (System.getProperty("line.separator"))) + "{\"station\":\"012650-99999\",\"time\":-655531200000,\"temp\":111}") + (System.getProperty("line.separator"))) + "{\"station\":\"012650-99999\",\"time\":-655509600000,\"temp\":78}") + (System.getProperty("line.separator"));
        String output = readUsingTextCommand(TestTextCommand.AVRO_FILENAME, generateWeatherAvroBinaryData());
        Assert.assertEquals(expectedOutput, output);
    }

    /**
     * Tests that a zero-length file is displayed correctly.
     */
    @Test(timeout = 30000)
    public void testEmptyTextFil() throws Exception {
        byte[] emptyContents = new byte[]{  };
        String output = readUsingTextCommand(TestTextCommand.TEXT_FILENAME, emptyContents);
        Assert.assertTrue("".equals(output));
    }

    /**
     * Tests that a one-byte file is displayed correctly.
     */
    @Test(timeout = 30000)
    public void testOneByteTextFil() throws Exception {
        byte[] oneByteContents = new byte[]{ 'x' };
        String output = readUsingTextCommand(TestTextCommand.TEXT_FILENAME, oneByteContents);
        Assert.assertTrue(new String(oneByteContents).equals(output));
    }

    /**
     * Tests that a one-byte file is displayed correctly.
     */
    @Test(timeout = 30000)
    public void testTwoByteTextFil() throws Exception {
        byte[] twoByteContents = new byte[]{ 'x', 'y' };
        String output = readUsingTextCommand(TestTextCommand.TEXT_FILENAME, twoByteContents);
        Assert.assertTrue(new String(twoByteContents).equals(output));
    }
}

