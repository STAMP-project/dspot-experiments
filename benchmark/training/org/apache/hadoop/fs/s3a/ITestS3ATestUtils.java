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
package org.apache.hadoop.fs.s3a;


import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the test utils. Why an integration test? it's needed to
 * verify property pushdown.
 */
public class ITestS3ATestUtils extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(ITestS3ATestUtils.class);

    public static final String KEY = "undefined.property";

    @Test
    public void testGetTestProperty() throws Throwable {
        Configuration conf = new Configuration(false);
        Assert.assertEquals("a", S3ATestUtils.getTestProperty(conf, ITestS3ATestUtils.KEY, "a"));
        conf.set(ITestS3ATestUtils.KEY, "\t b \n");
        Assert.assertEquals("b", S3ATestUtils.getTestProperty(conf, ITestS3ATestUtils.KEY, "a"));
        System.setProperty(ITestS3ATestUtils.KEY, "c");
        Assert.assertEquals("c", S3ATestUtils.getTestProperty(conf, ITestS3ATestUtils.KEY, "a"));
        unsetSysprop();
        Assert.assertEquals("b", S3ATestUtils.getTestProperty(conf, ITestS3ATestUtils.KEY, "a"));
    }

    @Test
    public void testGetTestPropertyLong() throws Throwable {
        Configuration conf = new Configuration(false);
        Assert.assertEquals(1, S3ATestUtils.getTestPropertyLong(conf, ITestS3ATestUtils.KEY, 1));
        conf.setInt(ITestS3ATestUtils.KEY, 2);
        Assert.assertEquals(2, S3ATestUtils.getTestPropertyLong(conf, ITestS3ATestUtils.KEY, 1));
        System.setProperty(ITestS3ATestUtils.KEY, "3");
        Assert.assertEquals(3, S3ATestUtils.getTestPropertyLong(conf, ITestS3ATestUtils.KEY, 1));
    }

    @Test
    public void testGetTestPropertyInt() throws Throwable {
        Configuration conf = new Configuration(false);
        Assert.assertEquals(1, S3ATestUtils.getTestPropertyInt(conf, ITestS3ATestUtils.KEY, 1));
        conf.setInt(ITestS3ATestUtils.KEY, 2);
        Assert.assertEquals(2, S3ATestUtils.getTestPropertyInt(conf, ITestS3ATestUtils.KEY, 1));
        System.setProperty(ITestS3ATestUtils.KEY, "3");
        Assert.assertEquals(3, S3ATestUtils.getTestPropertyInt(conf, ITestS3ATestUtils.KEY, 1));
        conf.unset(ITestS3ATestUtils.KEY);
        Assert.assertEquals(3, S3ATestUtils.getTestPropertyInt(conf, ITestS3ATestUtils.KEY, 1));
        unsetSysprop();
        Assert.assertEquals(5, S3ATestUtils.getTestPropertyInt(conf, ITestS3ATestUtils.KEY, 5));
    }

    @Test
    public void testGetTestPropertyBool() throws Throwable {
        Configuration conf = new Configuration(false);
        Assert.assertTrue(S3ATestUtils.getTestPropertyBool(conf, ITestS3ATestUtils.KEY, true));
        conf.set(ITestS3ATestUtils.KEY, "\tfalse \n");
        Assert.assertFalse(S3ATestUtils.getTestPropertyBool(conf, ITestS3ATestUtils.KEY, true));
        System.setProperty(ITestS3ATestUtils.KEY, "true");
        Assert.assertTrue(S3ATestUtils.getTestPropertyBool(conf, ITestS3ATestUtils.KEY, true));
        unsetSysprop();
        Assert.assertEquals("false", S3ATestUtils.getTestProperty(conf, ITestS3ATestUtils.KEY, "true"));
        conf.unset(ITestS3ATestUtils.KEY);
        Assert.assertTrue(S3ATestUtils.getTestPropertyBool(conf, ITestS3ATestUtils.KEY, true));
    }
}

