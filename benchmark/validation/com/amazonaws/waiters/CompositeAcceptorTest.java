/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.waiters;


import WaiterState.RETRY;
import WaiterState.SUCCESS;
import com.amazonaws.AmazonServiceException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static WaiterState.RETRY;
import static WaiterState.SUCCESS;


public class CompositeAcceptorTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullWaiterAcceptorList() {
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyWaiterAcceptorList() {
        List<WaiterAcceptor> waiterAcceptorsList = new ArrayList<WaiterAcceptor>();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(waiterAcceptorsList);
    }

    @Test
    public void responseMatchExpected() throws Exception {
        List<WaiterAcceptor> waiterAcceptorsList = new ArrayList<WaiterAcceptor>();
        waiterAcceptorsList.add(new CompositeAcceptorTest.TestExceptionAcceptor());
        waiterAcceptorsList.add(new CompositeAcceptorTest.TestResultAcceptor());
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(waiterAcceptorsList);
        Assert.assertEquals("Response output doesn't match expected output.", SUCCESS, compositeAcceptor.accepts(new CompositeAcceptorTest.DescribeTableResult()));
    }

    @Test
    public void responseNotMatchExpected() throws Exception {
        List<WaiterAcceptor> waiterAcceptorsList = new ArrayList<WaiterAcceptor>();
        waiterAcceptorsList.add(new CompositeAcceptorTest.TestExceptionAcceptor());
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(waiterAcceptorsList);
        Assert.assertEquals("Response output doesn't match expected output.", RETRY, compositeAcceptor.accepts(new CompositeAcceptorTest.DescribeTableResult()));
    }

    @Test(expected = AmazonServiceException.class)
    public void exceptionNotMatchExpected() throws Exception {
        List<WaiterAcceptor> waiterAcceptorsList = new ArrayList<WaiterAcceptor>();
        waiterAcceptorsList.add(new CompositeAcceptorTest.TestResultAcceptor());
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(waiterAcceptorsList);
        Assert.assertEquals("Exception thrown doesn't match expected exception.", RETRY, compositeAcceptor.accepts(new AmazonServiceException("")));
    }

    @Test
    public void exceptionMatchExpected() throws Exception {
        List<WaiterAcceptor> waiterAcceptorsList = new ArrayList<WaiterAcceptor>();
        waiterAcceptorsList.add(new CompositeAcceptorTest.TestResultAcceptor());
        waiterAcceptorsList.add(new CompositeAcceptorTest.TestExceptionAcceptor());
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(waiterAcceptorsList);
        Assert.assertEquals("Exception thrown doesn't match expected exception.", RETRY, compositeAcceptor.accepts(new CompositeAcceptorTest.ResourceNotFoundException("")));
    }

    class DescribeTableResult {
        private String tableName;
    }

    class TestResultAcceptor extends WaiterAcceptor<CompositeAcceptorTest.DescribeTableResult> {
        public boolean matches(CompositeAcceptorTest.DescribeTableResult result) {
            return true;
        }

        public WaiterState getState() {
            return SUCCESS;
        }
    }

    class TestExceptionAcceptor extends WaiterAcceptor<CompositeAcceptorTest.DescribeTableResult> {
        public boolean matches(AmazonServiceException e) {
            return e instanceof CompositeAcceptorTest.ResourceNotFoundException;
        }

        public WaiterState getState() {
            return RETRY;
        }
    }

    static class ResourceNotFoundException extends AmazonServiceException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}

