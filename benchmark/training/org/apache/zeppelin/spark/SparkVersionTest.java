/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.spark;


import SparkVersion.SPARK_2_0_0;
import SparkVersion.SPARK_2_3_0;
import SparkVersion.UNSUPPORTED_FUTURE_VERSION;
import org.junit.Assert;
import org.junit.Test;


public class SparkVersionTest {
    @Test
    public void testUnknownSparkVersion() {
        Assert.assertEquals(99999, SparkVersion.fromVersionString("DEV-10.10").toNumber());
    }

    @Test
    public void testUnsupportedVersion() {
        Assert.assertTrue(SparkVersion.fromVersionString("1.4.2").isUnsupportedVersion());
        Assert.assertFalse(SparkVersion.fromVersionString("2.3.0").isUnsupportedVersion());
        Assert.assertTrue(SparkVersion.fromVersionString("0.9.0").isUnsupportedVersion());
        Assert.assertTrue(UNSUPPORTED_FUTURE_VERSION.isUnsupportedVersion());
        // should support spark2 version of HDP 2.5
        Assert.assertFalse(SparkVersion.fromVersionString("2.0.0.2.5.0.0-1245").isUnsupportedVersion());
    }

    @Test
    public void testSparkVersion() {
        // test equals
        Assert.assertEquals(SPARK_2_0_0, SparkVersion.fromVersionString("2.0.0"));
        Assert.assertEquals(SPARK_2_0_0, SparkVersion.fromVersionString("2.0.0-SNAPSHOT"));
        // test spark2 version of HDP 2.5
        Assert.assertEquals(SPARK_2_0_0, SparkVersion.fromVersionString("2.0.0.2.5.0.0-1245"));
        // test newer than
        Assert.assertTrue(SPARK_2_3_0.newerThan(SPARK_2_0_0));
        Assert.assertTrue(SPARK_2_3_0.newerThanEquals(SPARK_2_3_0));
        Assert.assertFalse(SPARK_2_0_0.newerThan(SPARK_2_3_0));
        // test older than
        Assert.assertTrue(SPARK_2_0_0.olderThan(SPARK_2_3_0));
        Assert.assertTrue(SPARK_2_0_0.olderThanEquals(SPARK_2_0_0));
        Assert.assertFalse(SPARK_2_3_0.olderThan(SPARK_2_0_0));
        // test newerThanEqualsPatchVersion
        Assert.assertTrue(SparkVersion.fromVersionString("2.3.1").newerThanEqualsPatchVersion(SparkVersion.fromVersionString("2.3.0")));
        Assert.assertFalse(SparkVersion.fromVersionString("2.3.1").newerThanEqualsPatchVersion(SparkVersion.fromVersionString("2.3.2")));
        Assert.assertFalse(SparkVersion.fromVersionString("2.3.1").newerThanEqualsPatchVersion(SparkVersion.fromVersionString("2.2.0")));
        // conversion
        Assert.assertEquals(20300, SPARK_2_3_0.toNumber());
        Assert.assertEquals("2.3.0", SPARK_2_3_0.toString());
    }
}

