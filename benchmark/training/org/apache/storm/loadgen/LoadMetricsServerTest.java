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


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class LoadMetricsServerTest {
    @Test
    public void convertTest() throws Exception {
        for (TimeUnit from : TimeUnit.values()) {
            for (TimeUnit to : TimeUnit.values()) {
                Assert.assertEquals((((from + " to ") + to) + " and back"), 1.0, LoadMetricsServer.convert(LoadMetricsServer.convert(1.0, from, to), to, from), 1.0E-4);
            }
        }
    }
}

