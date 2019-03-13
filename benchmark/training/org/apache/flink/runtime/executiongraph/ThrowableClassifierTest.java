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
package org.apache.flink.runtime.executiongraph;


import ThrowableType.EnvironmentError;
import ThrowableType.NonRecoverableError;
import ThrowableType.PartitionDataMissingError;
import ThrowableType.RecoverableError;
import org.apache.flink.runtime.execution.SuppressRestartsException;
import org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException;
import org.apache.flink.runtime.throwable.ThrowableAnnotation;
import org.apache.flink.runtime.throwable.ThrowableClassifier;
import org.apache.flink.runtime.throwable.ThrowableType;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test throwable classifier
 */
public class ThrowableClassifierTest extends TestLogger {
    @Test
    public void testThrowableType_NonRecoverable() {
        Assert.assertEquals(NonRecoverableError, ThrowableClassifier.getThrowableType(new SuppressRestartsException(new Exception(""))));
        Assert.assertEquals(NonRecoverableError, ThrowableClassifier.getThrowableType(new NoResourceAvailableException()));
    }

    @Test
    public void testThrowableType_Recoverable() {
        Assert.assertEquals(RecoverableError, ThrowableClassifier.getThrowableType(new Exception("")));
        Assert.assertEquals(RecoverableError, ThrowableClassifier.getThrowableType(new ThrowableClassifierTest.ThrowableType_RecoverableFailure_Exception()));
    }

    @Test
    public void testThrowableType_EnvironmentError() {
        Assert.assertEquals(EnvironmentError, ThrowableClassifier.getThrowableType(new ThrowableClassifierTest.ThrowableType_EnvironmentError_Exception()));
    }

    @Test
    public void testThrowableType_PartitionDataMissingError() {
        Assert.assertEquals(PartitionDataMissingError, ThrowableClassifier.getThrowableType(new ThrowableClassifierTest.ThrowableType_PartitionDataMissingError_Exception()));
    }

    @Test
    public void testThrowableType_InheritError() {
        Assert.assertEquals(PartitionDataMissingError, ThrowableClassifier.getThrowableType(new ThrowableClassifierTest.Sub_ThrowableType_PartitionDataMissingError_Exception()));
    }

    @ThrowableAnnotation(ThrowableType.PartitionDataMissingError)
    private class ThrowableType_PartitionDataMissingError_Exception extends Exception {}

    @ThrowableAnnotation(ThrowableType.EnvironmentError)
    private class ThrowableType_EnvironmentError_Exception extends Exception {}

    @ThrowableAnnotation(ThrowableType.RecoverableError)
    private class ThrowableType_RecoverableFailure_Exception extends Exception {}

    private class Sub_ThrowableType_PartitionDataMissingError_Exception extends ThrowableClassifierTest.ThrowableType_PartitionDataMissingError_Exception {}
}

