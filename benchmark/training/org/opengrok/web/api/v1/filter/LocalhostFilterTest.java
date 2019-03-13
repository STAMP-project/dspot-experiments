/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.web.api.v1.filter;


import Response.Status.UNAUTHORIZED;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class LocalhostFilterTest {
    @Test
    public void nonLocalhostTest() throws Exception {
        LocalhostFilter filter = mockWithRemoteAddress("192.168.1.1");
        ContainerRequestContext context = mockContainerRequestContext("test");
        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        filter.filter(context);
        Mockito.verify(context).abortWith(captor.capture());
        Assert.assertEquals(UNAUTHORIZED.getStatusCode(), captor.getValue().getStatus());
    }

    @Test
    public void localhostTest() throws Exception {
        assertFilterDoesNotBlockAddress("127.0.0.1");
    }

    @Test
    public void localhostIPv6Test() throws Exception {
        assertFilterDoesNotBlockAddress("0:0:0:0:0:0:0:1");
    }

    @Test
    public void searchTest() throws Exception {
        LocalhostFilter filter = mockWithRemoteAddress("10.0.0.1");
        ContainerRequestContext context = mockContainerRequestContext("search");
        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        filter.filter(context);
        Mockito.verify(context, Mockito.never()).abortWith(captor.capture());
    }
}

