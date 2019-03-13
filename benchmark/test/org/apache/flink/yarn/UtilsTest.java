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
package org.apache.flink.yarn;


import ConfigConstants.YARN_HEAP_CUTOFF_MIN;
import ConfigConstants.YARN_HEAP_CUTOFF_RATIO;
import ResourceManagerOptions.CONTAINERIZED_HEAP_CUTOFF_MIN;
import ResourceManagerOptions.CONTAINERIZED_HEAP_CUTOFF_RATIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for various utilities.
 */
public class UtilsTest extends TestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(UtilsTest.class);

    @Test
    public void testUberjarLocator() {
        File dir = YarnTestBase.findFile("..", new YarnTestBase.RootDirFilenameFilter());
        Assert.assertNotNull(dir);
        Assert.assertTrue(dir.getName().endsWith(".jar"));
        dir = dir.getParentFile().getParentFile();// from uberjar to lib to root

        Assert.assertTrue(dir.exists());
        Assert.assertTrue(dir.isDirectory());
        List<String> files = Arrays.asList(dir.list());
        Assert.assertTrue(files.contains("lib"));
        Assert.assertTrue(files.contains("bin"));
        Assert.assertTrue(files.contains("conf"));
    }

    /**
     * Remove 15% of the heap, at least 384MB.
     */
    @Test
    public void testHeapCutoff() {
        Configuration conf = new Configuration();
        conf.setFloat(CONTAINERIZED_HEAP_CUTOFF_RATIO, 0.15F);
        conf.setInteger(CONTAINERIZED_HEAP_CUTOFF_MIN, 384);
        Assert.assertEquals(616, Utils.calculateHeapSize(1000, conf));
        Assert.assertEquals(8500, Utils.calculateHeapSize(10000, conf));
        // test different configuration
        Assert.assertEquals(3400, Utils.calculateHeapSize(4000, conf));
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_MIN.key(), "1000");
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_RATIO.key(), "0.1");
        Assert.assertEquals(3000, Utils.calculateHeapSize(4000, conf));
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_RATIO.key(), "0.5");
        Assert.assertEquals(2000, Utils.calculateHeapSize(4000, conf));
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_RATIO.key(), "1");
        Assert.assertEquals(0, Utils.calculateHeapSize(4000, conf));
        // test also deprecated keys
        conf = new Configuration();
        conf.setDouble(YARN_HEAP_CUTOFF_RATIO, 0.15);
        conf.setInteger(YARN_HEAP_CUTOFF_MIN, 384);
        Assert.assertEquals(616, Utils.calculateHeapSize(1000, conf));
        Assert.assertEquals(8500, Utils.calculateHeapSize(10000, conf));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument() {
        Configuration conf = new Configuration();
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_RATIO.key(), "1.1");
        Assert.assertEquals(0, Utils.calculateHeapSize(4000, conf));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentNegative() {
        Configuration conf = new Configuration();
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_RATIO.key(), "-0.01");
        Assert.assertEquals(0, Utils.calculateHeapSize(4000, conf));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooMuchCutoff() {
        Configuration conf = new Configuration();
        conf.setString(CONTAINERIZED_HEAP_CUTOFF_RATIO.key(), "6000");
        Assert.assertEquals(0, Utils.calculateHeapSize(4000, conf));
    }

    @Test
    public void testGetEnvironmentVariables() {
        Configuration testConf = new Configuration();
        testConf.setString("yarn.application-master.env.LD_LIBRARY_PATH", "/usr/lib/native");
        Map<String, String> res = Utils.getEnvironmentVariables("yarn.application-master.env.", testConf);
        Assert.assertEquals(1, res.size());
        Map.Entry<String, String> entry = res.entrySet().iterator().next();
        Assert.assertEquals("LD_LIBRARY_PATH", entry.getKey());
        Assert.assertEquals("/usr/lib/native", entry.getValue());
    }

    @Test
    public void testGetEnvironmentVariablesErroneous() {
        Configuration testConf = new Configuration();
        testConf.setString("yarn.application-master.env.", "/usr/lib/native");
        Map<String, String> res = Utils.getEnvironmentVariables("yarn.application-master.env.", testConf);
        Assert.assertEquals(0, res.size());
    }

    // 
    // --------------- Tools to test if a certain string has been logged with Log4j. -------------
    // See :  http://stackoverflow.com/questions/3717402/how-to-test-w-junit-that-warning-was-logged-w-log4j
    // 
    private static UtilsTest.TestAppender testAppender;

    private static class TestAppender extends AppenderSkeleton {
        public final List<LoggingEvent> events = new ArrayList<>();

        public void close() {
        }

        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(LoggingEvent event) {
            synchronized(events) {
                events.add(event);
            }
        }
    }
}

