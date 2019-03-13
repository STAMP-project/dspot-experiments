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


import Display.Text;
import java.io.InputStream;
import java.lang.reflect.Method;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the logic for displaying the binary formats supported
 * by the Text command.
 */
public class TestHdfsTextCommand {
    private static final String TEST_ROOT_DIR = "/test/data/testText";

    private static final Path AVRO_FILENAME = new Path(TestHdfsTextCommand.TEST_ROOT_DIR, "weather.avro");

    private static MiniDFSCluster cluster;

    private static FileSystem fs;

    /**
     * Tests whether binary Avro data files are displayed correctly.
     */
    @Test
    public void testDisplayForAvroFiles() throws Exception {
        // Create a small Avro data file on the HDFS.
        createAvroFile(generateWeatherAvroBinaryData());
        // Prepare and call the Text command's protected getInputStream method
        // using reflection.
        Configuration conf = TestHdfsTextCommand.fs.getConf();
        PathData pathData = new PathData(TestHdfsTextCommand.AVRO_FILENAME.toString(), conf);
        Display.Text text = new Display.Text();
        text.setConf(conf);
        Method method = text.getClass().getDeclaredMethod("getInputStream", PathData.class);
        method.setAccessible(true);
        InputStream stream = ((InputStream) (method.invoke(text, pathData)));
        String output = inputStreamToString(stream);
        // Check the output.
        String expectedOutput = (((((((("{\"station\":\"011990-99999\",\"time\":-619524000000,\"temp\":0}" + (System.getProperty("line.separator"))) + "{\"station\":\"011990-99999\",\"time\":-619506000000,\"temp\":22}") + (System.getProperty("line.separator"))) + "{\"station\":\"011990-99999\",\"time\":-619484400000,\"temp\":-11}") + (System.getProperty("line.separator"))) + "{\"station\":\"012650-99999\",\"time\":-655531200000,\"temp\":111}") + (System.getProperty("line.separator"))) + "{\"station\":\"012650-99999\",\"time\":-655509600000,\"temp\":78}") + (System.getProperty("line.separator"));
        Assert.assertEquals(expectedOutput, output);
    }
}

