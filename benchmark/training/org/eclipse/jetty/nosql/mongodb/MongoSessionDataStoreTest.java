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
package org.eclipse.jetty.nosql.mongodb;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.server.session.AbstractSessionDataStoreFactory;
import org.eclipse.jetty.server.session.AbstractSessionDataStoreTest;
import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * MongoSessionDataStoreTest
 */
public class MongoSessionDataStoreTest extends AbstractSessionDataStoreTest {
    /**
     * Test that a session stored in the legacy attribute
     * format can be read.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadLegacySession() throws Exception {
        // create the SessionDataStore
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/legacy");
        SessionDataStoreFactory factory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (factory)).setGracePeriodSec(GRACE_PERIOD_SEC);
        SessionDataStore store = factory.getSessionDataStore(context.getSessionHandler());
        SessionContext sessionContext = new SessionContext("foo", context.getServletContext());
        store.initialize(sessionContext);
        // persist an old-style session
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attribute1", "attribute1value");
        attributes.put("attribute2", new ArrayList<String>(Arrays.asList("1", "2", "3")));
        MongoTestHelper.createLegacySession("1234", sessionContext.getCanonicalContextPath(), sessionContext.getVhost(), "foo", 1000L, ((System.currentTimeMillis()) - 1000L), ((System.currentTimeMillis()) - 2000L), (-1), (-1), attributes);
        store.start();
        // test that we can retrieve it
        SessionData loaded = store.load("1234");
        Assertions.assertNotNull(loaded);
        Assertions.assertEquals("1234", loaded.getId());
        Assertions.assertEquals(1000L, loaded.getCreated());
        Assertions.assertEquals("attribute1value", loaded.getAttribute("attribute1"));
        Assertions.assertNotNull(loaded.getAttribute("attribute2"));
        // test that we can write it
        store.store("1234", loaded);
        // and that it has now been written out with the new format
        MongoTestHelper.checkSessionPersisted(loaded);
    }
}

