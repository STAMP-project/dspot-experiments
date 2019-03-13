/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestGetSpaceUsed {
    private static final File DIR = GenericTestUtils.getTestDir("TestGetSpaceUsed");

    /**
     * Test that the builder can create a class specified through the class.
     */
    @Test
    public void testBuilderConf() throws Exception {
        File file = new File(TestGetSpaceUsed.DIR, "testBuilderConf");
        Assert.assertTrue(file.createNewFile());
        Configuration conf = new Configuration();
        conf.set("fs.getspaceused.classname", TestGetSpaceUsed.DummyDU.class.getName());
        CachingGetSpaceUsed instance = ((CachingGetSpaceUsed) (new CachingGetSpaceUsed.Builder().setPath(file).setInterval(0).setConf(conf).build()));
        Assert.assertNotNull(instance);
        Assert.assertTrue((instance instanceof TestGetSpaceUsed.DummyDU));
        Assert.assertFalse(instance.running());
        instance.close();
    }

    @Test
    public void testBuildInitial() throws Exception {
        File file = new File(TestGetSpaceUsed.DIR, "testBuildInitial");
        Assert.assertTrue(file.createNewFile());
        CachingGetSpaceUsed instance = ((CachingGetSpaceUsed) (new CachingGetSpaceUsed.Builder().setPath(file).setInitialUsed(90210).setKlass(TestGetSpaceUsed.DummyDU.class).build()));
        Assert.assertEquals(90210, instance.getUsed());
        instance.close();
    }

    @Test
    public void testBuildInterval() throws Exception {
        File file = new File(TestGetSpaceUsed.DIR, "testBuildInitial");
        Assert.assertTrue(file.createNewFile());
        CachingGetSpaceUsed instance = ((CachingGetSpaceUsed) (new CachingGetSpaceUsed.Builder().setPath(file).setInitialUsed(90210).setInterval(50060).setKlass(TestGetSpaceUsed.DummyDU.class).build()));
        Assert.assertEquals(50060, instance.getRefreshInterval());
        instance.close();
    }

    @Test
    public void testBuildNonCaching() throws Exception {
        File file = new File(TestGetSpaceUsed.DIR, "testBuildNonCaching");
        Assert.assertTrue(file.createNewFile());
        GetSpaceUsed instance = new CachingGetSpaceUsed.Builder().setPath(file).setInitialUsed(90210).setInterval(50060).setKlass(TestGetSpaceUsed.DummyGetSpaceUsed.class).build();
        Assert.assertEquals(300, instance.getUsed());
        Assert.assertTrue((instance instanceof TestGetSpaceUsed.DummyGetSpaceUsed));
    }

    private static class DummyDU extends CachingGetSpaceUsed {
        public DummyDU(Builder builder) throws IOException {
            // Push to the base class.
            // Most times that's all that will need to be done.
            super(builder);
        }

        @Override
        protected void refresh() {
            // This is a test so don't du anything.
        }
    }

    private static class DummyGetSpaceUsed implements GetSpaceUsed {
        public DummyGetSpaceUsed(GetSpaceUsed.Builder builder) {
        }

        @Override
        public long getUsed() throws IOException {
            return 300;
        }
    }
}

