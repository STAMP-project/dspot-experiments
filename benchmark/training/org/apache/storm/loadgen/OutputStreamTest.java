/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.loadgen;


import org.junit.Assert;
import org.junit.Test;


public class OutputStreamTest {
    @Test
    public void scaleThroughput() throws Exception {
        OutputStream orig = new OutputStream("ID", new NormalDistStats(100.0, 1.0, 99.0, 101.0), false);
        OutputStream scaled = orig.scaleThroughput(2.0);
        Assert.assertEquals(orig.id, scaled.id);
        Assert.assertEquals(orig.areKeysSkewed, scaled.areKeysSkewed);
        Assert.assertEquals(scaled.rate.mean, 200.0, 1.0E-4);
        Assert.assertEquals(scaled.rate.stddev, 1.0, 1.0E-4);
        Assert.assertEquals(scaled.rate.min, 199.0, 1.0E-4);
        Assert.assertEquals(scaled.rate.max, 201.0, 1.0E-4);
    }
}

