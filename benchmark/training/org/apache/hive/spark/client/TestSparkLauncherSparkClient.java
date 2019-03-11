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
package org.apache.hive.spark.client;


import SparkAppHandle.State.CONNECTED;
import SparkAppHandle.State.FAILED;
import SparkAppHandle.State.FINISHED;
import SparkAppHandle.State.KILLED;
import SparkAppHandle.State.LOST;
import SparkAppHandle.State.RUNNING;
import SparkAppHandle.State.SUBMITTED;
import org.junit.Test;


public class TestSparkLauncherSparkClient {
    @Test
    public void testSparkLauncherFutureGet() {
        testChainOfStates(CONNECTED, SUBMITTED, RUNNING);
        testChainOfStates(CONNECTED, SUBMITTED, FINISHED);
        testChainOfStates(CONNECTED, SUBMITTED, FAILED);
        testChainOfStates(CONNECTED, SUBMITTED, KILLED);
        testChainOfStates(LOST);
        testChainOfStates(CONNECTED, LOST);
        testChainOfStates(CONNECTED, SUBMITTED, LOST);
    }
}

