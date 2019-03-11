/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.net;


import ch.qos.logback.access.dummy.DummyRequest;
import ch.qos.logback.access.dummy.DummyResponse;
import ch.qos.logback.access.dummy.DummyServerAdapter;
import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import org.junit.Assert;
import org.junit.Test;


public class URLEvaluatorTest {
    final String expectedURL1 = "testUrl1";

    final String expectedURL2 = "testUrl2";

    AccessContext accessContext = new AccessContext();

    URLEvaluator evaluator;

    DummyRequest request;

    DummyResponse response;

    DummyServerAdapter serverAdapter;

    @Test
    public void testExpectFalse() throws EvaluationException {
        request.setRequestUri("test");
        IAccessEvent ae = new ch.qos.logback.access.spi.AccessEvent(accessContext, request, response, serverAdapter);
        Assert.assertFalse(evaluator.evaluate(ae));
    }

    @Test
    public void testExpectTrue() throws EvaluationException {
        request.setRequestUri(expectedURL1);
        IAccessEvent ae = new ch.qos.logback.access.spi.AccessEvent(accessContext, request, response, serverAdapter);
        Assert.assertTrue(evaluator.evaluate(ae));
    }

    @Test
    public void testExpectTrueMultiple() throws EvaluationException {
        evaluator.addURL(expectedURL2);
        request.setRequestUri(expectedURL2);
        IAccessEvent ae = new ch.qos.logback.access.spi.AccessEvent(accessContext, request, response, serverAdapter);
        Assert.assertTrue(evaluator.evaluate(ae));
    }
}

