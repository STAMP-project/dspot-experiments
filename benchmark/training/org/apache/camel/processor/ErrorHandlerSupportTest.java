/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.OnExceptionDefinition;
import org.junit.Assert;
import org.junit.Test;


public class ErrorHandlerSupportTest extends Assert {
    @Test
    public void testOnePolicyChildFirst() {
        List<Class<? extends Throwable>> exceptions = new ArrayList<>();
        exceptions.add(ErrorHandlerSupportTest.ChildException.class);
        exceptions.add(ErrorHandlerSupportTest.ParentException.class);
        ErrorHandlerSupport support = new ErrorHandlerSupportTest.ShuntErrorHandlerSupport();
        support.addExceptionPolicy(null, new OnExceptionDefinition(exceptions));
        Assert.assertEquals(ErrorHandlerSupportTest.ChildException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ChildException(), 0));
        Assert.assertEquals(ErrorHandlerSupportTest.ParentException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ParentException(), 1));
    }

    @Test
    public void testOnePolicyChildLast() {
        List<Class<? extends Throwable>> exceptions = new ArrayList<>();
        exceptions.add(ErrorHandlerSupportTest.ParentException.class);
        exceptions.add(ErrorHandlerSupportTest.ChildException.class);
        ErrorHandlerSupport support = new ErrorHandlerSupportTest.ShuntErrorHandlerSupport();
        support.addExceptionPolicy(null, new OnExceptionDefinition(exceptions));
        Assert.assertEquals(ErrorHandlerSupportTest.ChildException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ChildException(), 1));
        Assert.assertEquals(ErrorHandlerSupportTest.ParentException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ParentException(), 0));
    }

    @Test
    public void testTwoPolicyChildFirst() {
        ErrorHandlerSupport support = new ErrorHandlerSupportTest.ShuntErrorHandlerSupport();
        support.addExceptionPolicy(null, new OnExceptionDefinition(ErrorHandlerSupportTest.ChildException.class));
        support.addExceptionPolicy(null, new OnExceptionDefinition(ErrorHandlerSupportTest.ParentException.class));
        Assert.assertEquals(ErrorHandlerSupportTest.ChildException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ChildException(), 0));
        Assert.assertEquals(ErrorHandlerSupportTest.ParentException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ParentException(), 0));
    }

    @Test
    public void testTwoPolicyChildLast() {
        ErrorHandlerSupport support = new ErrorHandlerSupportTest.ShuntErrorHandlerSupport();
        support.addExceptionPolicy(null, new OnExceptionDefinition(ErrorHandlerSupportTest.ParentException.class));
        support.addExceptionPolicy(null, new OnExceptionDefinition(ErrorHandlerSupportTest.ChildException.class));
        Assert.assertEquals(ErrorHandlerSupportTest.ChildException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ChildException(), 0));
        Assert.assertEquals(ErrorHandlerSupportTest.ParentException.class, ErrorHandlerSupportTest.getExceptionPolicyFor(support, new ErrorHandlerSupportTest.ParentException(), 0));
    }

    private static class ParentException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    private static class ChildException extends ErrorHandlerSupportTest.ParentException {
        private static final long serialVersionUID = 1L;
    }

    private static class ShuntErrorHandlerSupport extends ErrorHandlerSupport {
        protected void doStart() throws Exception {
        }

        protected void doStop() throws Exception {
        }

        public boolean supportTransacted() {
            return false;
        }

        public Processor getOutput() {
            return null;
        }

        public void process(Exchange exchange) throws Exception {
        }
    }
}

