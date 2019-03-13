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
package org.eclipse.jetty.client;


import org.junit.jupiter.api.Test;


public class ServerConnectionCloseTest {
    private HttpClient client;

    @Test
    public void testServerSendsConnectionCloseWithoutContent() throws Exception {
        testServerSendsConnectionClose(true, false, "");
    }

    @Test
    public void testServerSendsConnectionCloseWithContent() throws Exception {
        testServerSendsConnectionClose(true, false, "data");
    }

    @Test
    public void testServerSendsConnectionCloseWithChunkedContent() throws Exception {
        testServerSendsConnectionClose(true, true, "data");
    }

    @Test
    public void testServerSendsConnectionCloseWithoutContentButDoesNotClose() throws Exception {
        testServerSendsConnectionClose(false, false, "");
    }

    @Test
    public void testServerSendsConnectionCloseWithContentButDoesNotClose() throws Exception {
        testServerSendsConnectionClose(false, false, "data");
    }

    @Test
    public void testServerSendsConnectionCloseWithChunkedContentButDoesNotClose() throws Exception {
        testServerSendsConnectionClose(false, true, "data");
    }
}

