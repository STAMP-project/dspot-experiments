/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class QueryInterruptedExceptionTest {
    private static final ObjectMapper MAPPER = TestHelper.makeJsonMapper();

    @Test
    public void testErrorCode() {
        Assert.assertEquals("Query cancelled", getErrorCode());
        Assert.assertEquals("Query cancelled", new QueryInterruptedException(new CancellationException()).getErrorCode());
        Assert.assertEquals("Query interrupted", new QueryInterruptedException(new InterruptedException()).getErrorCode());
        Assert.assertEquals("Query timeout", new QueryInterruptedException(new TimeoutException()).getErrorCode());
        Assert.assertEquals("Unknown exception", new QueryInterruptedException(null).getErrorCode());
        Assert.assertEquals("Unknown exception", getErrorCode());
        Assert.assertEquals("Resource limit exceeded", getErrorCode());
        Assert.assertEquals("Unknown exception", new QueryInterruptedException(new QueryInterruptedException(new ISE("Something bad!"))).getErrorCode());
    }

    @Test
    public void testErrorMessage() {
        Assert.assertEquals(null, getMessage());
        Assert.assertEquals(null, new QueryInterruptedException(new CancellationException()).getMessage());
        Assert.assertEquals(null, new QueryInterruptedException(new InterruptedException()).getMessage());
        Assert.assertEquals(null, new QueryInterruptedException(new TimeoutException()).getMessage());
        Assert.assertEquals(null, new QueryInterruptedException(null).getMessage());
        Assert.assertEquals("too many!", getMessage());
        Assert.assertEquals("Something bad!", getMessage());
        Assert.assertEquals("Something bad!", new QueryInterruptedException(new QueryInterruptedException(new ISE("Something bad!"))).getMessage());
    }

    @Test
    public void testErrorClass() {
        Assert.assertEquals("java.util.concurrent.CancellationException", getErrorClass());
        Assert.assertEquals("java.util.concurrent.CancellationException", new QueryInterruptedException(new CancellationException()).getErrorClass());
        Assert.assertEquals("java.lang.InterruptedException", new QueryInterruptedException(new InterruptedException()).getErrorClass());
        Assert.assertEquals("java.util.concurrent.TimeoutException", new QueryInterruptedException(new TimeoutException()).getErrorClass());
        Assert.assertEquals("org.apache.druid.query.ResourceLimitExceededException", getErrorClass());
        Assert.assertEquals(null, new QueryInterruptedException(null).getErrorClass());
        Assert.assertEquals("org.apache.druid.java.util.common.ISE", getErrorClass());
        Assert.assertEquals("org.apache.druid.java.util.common.ISE", new QueryInterruptedException(new QueryInterruptedException(new ISE("Something bad!"))).getErrorClass());
    }

    @Test
    public void testHost() {
        Assert.assertEquals("myhost", getHost());
    }

    @Test
    public void testSerde() {
        Assert.assertEquals("Query cancelled", getErrorCode());
        Assert.assertEquals("java.util.concurrent.CancellationException", getErrorClass());
        Assert.assertEquals(null, getMessage());
        Assert.assertEquals("java.util.concurrent.CancellationException", getErrorClass());
        Assert.assertEquals("java.lang.InterruptedException", getErrorClass());
        Assert.assertEquals("java.util.concurrent.TimeoutException", getErrorClass());
        Assert.assertEquals(null, getErrorClass());
        Assert.assertEquals("org.apache.druid.java.util.common.ISE", getErrorClass());
        Assert.assertEquals("org.apache.druid.java.util.common.ISE", getErrorClass());
        Assert.assertEquals("Something bad!", getMessage());
        Assert.assertEquals("Something bad!", getMessage());
        Assert.assertEquals("Unknown exception", getErrorCode());
        Assert.assertEquals("Unknown exception", getErrorCode());
    }
}

