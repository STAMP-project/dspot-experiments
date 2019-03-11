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
package org.apache.hadoop.http;


import HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import IsActiveServlet.RESPONSE_ACTIVE;
import IsActiveServlet.RESPONSE_NOT_ACTIVE;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test if the {@link IsActiveServlet} returns the right answer if the
 * underlying service is active.
 */
public class TestIsActiveServlet {
    private IsActiveServlet servlet;

    private HttpServletRequest req;

    private HttpServletResponse resp;

    private ByteArrayOutputStream respOut;

    @Test
    public void testSucceedsOnActive() throws IOException {
        servlet = new IsActiveServlet() {
            @Override
            protected boolean isActive() {
                return true;
            }
        };
        String response = doGet();
        Mockito.verify(resp, Mockito.never()).sendError(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
        Assert.assertEquals(RESPONSE_ACTIVE, response);
    }

    @Test
    public void testFailsOnInactive() throws IOException {
        servlet = new IsActiveServlet() {
            @Override
            protected boolean isActive() {
                return false;
            }
        };
        doGet();
        Mockito.verify(resp, Mockito.atLeastOnce()).sendError(ArgumentMatchers.eq(SC_METHOD_NOT_ALLOWED), ArgumentMatchers.eq(RESPONSE_NOT_ACTIVE));
    }
}

