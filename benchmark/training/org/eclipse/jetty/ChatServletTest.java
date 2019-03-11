/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty;


import org.eclipse.jetty.servlet.ServletTester;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ChatServletTest {
    private final ServletTester tester = new ServletTester();

    @Test
    public void testLogin() throws Exception {
        assertResponse("user=test&join=true&message=has%20joined!", "{\"from\":\"test\",\"chat\":\"has joined!\"}");
    }

    @Test
    public void testChat() throws Exception {
        assertResponse("user=test&join=true&message=has%20joined!", "{\"from\":\"test\",\"chat\":\"has joined!\"}");
        String response = tester.getResponses(createRequestString("user=test&message=message"));
        MatcherAssert.assertThat(response.contains("{"), Matchers.is(false));// make sure we didn't get a json body

    }

    @Test
    public void testPoll() throws Exception {
        assertResponse("user=test", "{action:\"poll\"}");
    }
}

