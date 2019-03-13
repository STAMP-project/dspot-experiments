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
package org.apache.camel.processor.exceptionpolicy;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.HashMap;
import org.apache.camel.AlreadyStoppedException;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.ValidationException;
import org.apache.camel.model.OnExceptionDefinition;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for DefaultExceptionPolicy
 */
public class DefaultExceptionPolicyStrategyTest extends Assert {
    private DefaultExceptionPolicyStrategy strategy;

    private HashMap<ExceptionPolicyKey, OnExceptionDefinition> policies;

    private OnExceptionDefinition type1;

    private OnExceptionDefinition type2;

    private OnExceptionDefinition type3;

    @Test
    public void testDirectMatch1() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new CamelExchangeException("", null));
        Assert.assertEquals(type1, result);
    }

    @Test
    public void testDirectMatch2() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new Exception(""));
        Assert.assertEquals(type2, result);
    }

    @Test
    public void testDirectMatch3() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new IOException(""));
        Assert.assertEquals(type3, result);
    }

    @Test
    public void testClosetMatch3() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new ConnectException(""));
        Assert.assertEquals(type3, result);
        result = strategy.getExceptionPolicy(policies, null, new SocketException(""));
        Assert.assertEquals(type3, result);
        result = strategy.getExceptionPolicy(policies, null, new FileNotFoundException());
        Assert.assertEquals(type3, result);
    }

    @Test
    public void testClosetMatch2() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new ClassCastException(""));
        Assert.assertEquals(type2, result);
        result = strategy.getExceptionPolicy(policies, null, new NumberFormatException(""));
        Assert.assertEquals(type2, result);
        result = strategy.getExceptionPolicy(policies, null, new NullPointerException());
        Assert.assertEquals(type2, result);
    }

    @Test
    public void testClosetMatch1() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new ValidationException(null, ""));
        Assert.assertEquals(type1, result);
        result = strategy.getExceptionPolicy(policies, null, new ExchangeTimedOutException(null, 0));
        Assert.assertEquals(type1, result);
    }

    @Test
    public void testNoMatch1ThenMatchingJustException() {
        setupPolicies();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new AlreadyStoppedException());
        Assert.assertEquals(type2, result);
    }

    @Test
    public void testNoMatch1ThenNull() {
        setupPoliciesNoTopLevelException();
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new AlreadyStoppedException());
        Assert.assertNull("Should not find an exception policy to use", result);
    }

    @Test
    public void testCausedBy() {
        setupPoliciesCausedBy();
        IOException ioe = new IOException("Damm");
        ioe.initCause(new FileNotFoundException("Somefile not found"));
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, ioe);
        Assert.assertEquals(type1, result);
    }

    @Test
    public void testCausedByWrapped() {
        setupPoliciesCausedBy();
        IOException ioe = new IOException("Damm");
        ioe.initCause(new FileNotFoundException("Somefile not found"));
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, new RuntimeCamelException(ioe));
        Assert.assertEquals(type1, result);
    }

    @Test
    public void testCausedByNotConnected() {
        setupPoliciesCausedBy();
        IOException ioe = new IOException("Damm");
        ioe.initCause(new ConnectException("Not connected"));
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, ioe);
        Assert.assertEquals(type3, result);
    }

    @Test
    public void testCausedByOtherIO() {
        setupPoliciesCausedBy();
        IOException ioe = new IOException("Damm");
        ioe.initCause(new MalformedURLException("Bad url"));
        OnExceptionDefinition result = strategy.getExceptionPolicy(policies, null, ioe);
        Assert.assertEquals(type2, result);
    }
}

