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
package org.apache.ignite.internal.websession;


import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.marshaller.Marshaller;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.eclipse.jetty.server.Server;
import org.junit.Test;


/**
 * Tests the correctness of web sessions caching functionality.
 */
public class WebSessionSelfTest extends GridCommonAbstractTest {
    /**
     * Port for test Jetty server.
     */
    private static final int TEST_JETTY_PORT = 49090;

    /**
     * Servers count in load test.
     */
    private static final int SRV_CNT = 3;

    /**
     * Session invalidated marker. Added to HTTP response to indicate that session invalidation was successful.
     */
    public static final String INVALIDATED = "invalidated";

    /**
     * Session invalidated failed marker fot HTTP reponse.
     */
    public static final String FAILED = "failed";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleRequest() throws Exception {
        testSingleRequest("/modules/core/src/test/config/websession/example-cache.xml");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleRequestMetaInf() throws Exception {
        testSingleRequest("ignite-webapp-config.xml");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testImplicitlyAttributeModification() throws Exception {
        testImplicitlyModification("ignite-webapp-config.xml");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectRequest() throws Exception {
        testClientReconnectRequest("/modules/core/src/test/config/websession/example-cache.xml", "/modules/core/src/test/config/websession/example-cache2.xml", "/modules/core/src/test/config/websession/example-cache-client.xml");
    }

    /**
     * Tests invalidated sessions.
     *
     * @throws Exception
     * 		Exception If failed.
     */
    @Test
    public void testInvalidatedSession() throws Exception {
        String invalidatedSesId;
        Server srv = null;
        try {
            srv = startServer(WebSessionSelfTest.TEST_JETTY_PORT, "/modules/core/src/test/config/websession/example-cache.xml", null, new WebSessionSelfTest.InvalidatedSessionServlet());
            Ignite ignite = G.ignite();
            URLConnection conn = new URL((("http://localhost:" + (WebSessionSelfTest.TEST_JETTY_PORT)) + "/ignitetest/invalidated")).openConnection();
            conn.connect();
            try (BufferedReader rdr = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                // checks if the old session object is invalidated.
                invalidatedSesId = rdr.readLine();
                assertNotNull(invalidatedSesId);
                if (!(keepBinary())) {
                    IgniteCache<String, HttpSession> cache = ignite.cache(getCacheName());
                    assertNotNull(cache);
                    HttpSession invalidatedSes = cache.get(invalidatedSesId);
                    assertNull(invalidatedSes);
                    // requests to subsequent getSession() returns null.
                    String ses = rdr.readLine();
                    assertEquals("null", ses);
                } else {
                    IgniteCache<String, WebSessionEntity> cache = ignite.cache(getCacheName());
                    assertNotNull(cache);
                    WebSessionEntity invalidatedSes = cache.get(invalidatedSesId);
                    assertNull(invalidatedSes);
                    // requests to subsequent getSession() returns null.
                    String ses = rdr.readLine();
                    assertEquals("null", ses);
                }
            }
            // put and update.
            final CountDownLatch latch = new CountDownLatch(2);
            final IgnitePredicate<Event> putLsnr = new IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert evt != null;
                    latch.countDown();
                    return true;
                }
            };
            ignite.events().localListen(putLsnr, EVT_CACHE_OBJECT_PUT);
            // new request that creates a new session.
            conn = new URL((("http://localhost:" + (WebSessionSelfTest.TEST_JETTY_PORT)) + "/ignitetest/valid")).openConnection();
            conn.addRequestProperty("Cookie", ("JSESSIONID=" + invalidatedSesId));
            conn.connect();
            try (BufferedReader rdr = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String sesId = rdr.readLine();
                assertFalse(sesId.equals("null"));
                assertTrue(latch.await(10, TimeUnit.SECONDS));
                if (!(keepBinary())) {
                    IgniteCache<String, HttpSession> cache = ignite.cache(getCacheName());
                    assertNotNull(cache);
                    HttpSession ses = cache.get(sesId);
                    assertNotNull(ses);
                    assertEquals("val10", ses.getAttribute("key10"));
                } else {
                    IgniteCache<String, WebSessionEntity> cache = ignite.cache(getCacheName());
                    assertNotNull(cache);
                    WebSessionEntity entity = cache.get(sesId);
                    assertNotNull(entity);
                    final Marshaller marshaller = ignite.configuration().getMarshaller();
                    assertEquals("val10", marshaller.unmarshal(entity.attributes().get("key10"), getClass().getClassLoader()));
                }
            }
        } finally {
            stopServer(srv);
        }
    }

    /**
     * Tests session id change.
     *
     * @throws Exception
     * 		Exception If failed.
     */
    @Test
    public void testChangeSessionId() throws Exception {
        String newWebSesId;
        Server srv = null;
        try {
            srv = startServer(WebSessionSelfTest.TEST_JETTY_PORT, "/modules/core/src/test/config/websession/example-cache.xml", null, new WebSessionSelfTest.SessionIdChangeServlet());
            Ignite ignite = G.ignite();
            URLConnection conn = new URL((("http://localhost:" + (WebSessionSelfTest.TEST_JETTY_PORT)) + "/ignitetest/chngsesid")).openConnection();
            conn.connect();
            try (BufferedReader rdr = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                // checks if the old session object is invalidated.
                String oldId = rdr.readLine();
                assertNotNull(oldId);
                // id from genuine session
                String newGenSesId = rdr.readLine();
                assertNotNull(newGenSesId);
                assertFalse(newGenSesId.equals(oldId));
                // id from replicated session
                newWebSesId = rdr.readLine();
                assertNotNull(newWebSesId);
                assertTrue(newGenSesId.equals(newWebSesId));
                if (!(keepBinary())) {
                    IgniteCache<String, HttpSession> cache = ignite.cache(getCacheName());
                    assertNotNull(cache);
                    Thread.sleep(1000);
                    HttpSession ses = cache.get(newWebSesId);
                    assertNotNull(ses);
                    assertEquals("val1", ses.getAttribute("key1"));
                } else {
                    IgniteCache<String, WebSessionEntity> cache = ignite.cache(getCacheName());
                    assertNotNull(cache);
                    Thread.sleep(1000);
                    WebSessionEntity ses = cache.get(newWebSesId);
                    assertNotNull(ses);
                    final Marshaller marshaller = ignite.configuration().getMarshaller();
                    assertEquals("val1", marshaller.<String>unmarshal(ses.attributes().get("key1"), getClass().getClassLoader()));
                }
            }
            conn = new URL((("http://localhost:" + (WebSessionSelfTest.TEST_JETTY_PORT)) + "/ignitetest/simple")).openConnection();
            conn.addRequestProperty("Cookie", ("JSESSIONID=" + newWebSesId));
            conn.connect();
            try (BufferedReader rdr = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                // checks if it can be handled with the subsequent request.
                String sesId = rdr.readLine();
                assertTrue(newWebSesId.equals(sesId));
                String attr = rdr.readLine();
                assertEquals("val1", attr);
                String reqSesValid = rdr.readLine();
                assertEquals("true", reqSesValid);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rdr.readLine()) != null)
                    sb.append(line);

                assertTrue(sb.toString().contains(WebSessionSelfTest.INVALIDATED));
            }
        } finally {
            stopServer(srv);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRestarts() throws Exception {
        final AtomicReference<String> sesIdRef = new AtomicReference<>();
        final AtomicReferenceArray<Server> srvs = new AtomicReferenceArray<>(WebSessionSelfTest.SRV_CNT);
        for (int idx = 0; idx < (WebSessionSelfTest.SRV_CNT); idx++) {
            String cfg = ("/modules/core/src/test/config/websession/spring-cache-" + (idx + 1)) + ".xml";
            srvs.set(idx, startServer(((WebSessionSelfTest.TEST_JETTY_PORT) + idx), cfg, ("grid-" + (idx + 1)), new WebSessionSelfTest.RestartsTestServlet(sesIdRef)));
        }
        final AtomicBoolean stop = new AtomicBoolean();
        IgniteInternalFuture<?> restarterFut = GridTestUtils.runMultiThreadedAsync(new Callable<Object>() {
            @SuppressWarnings("BusyWait")
            @Override
            public Object call() throws Exception {
                Random rnd = new Random();
                for (int i = 0; i < 10; i++) {
                    int idx = -1;
                    Server srv = null;
                    while (srv == null) {
                        idx = rnd.nextInt(WebSessionSelfTest.SRV_CNT);
                        srv = srvs.getAndSet(idx, null);
                    } 
                    assert idx != (-1);
                    stopServer(srv);
                    String cfg = ("/modules/core/src/test/config/websession/spring-cache-" + (idx + 1)) + ".xml";
                    srv = startServer(((WebSessionSelfTest.TEST_JETTY_PORT) + idx), cfg, ("grid-" + (idx + 1)), new WebSessionSelfTest.RestartsTestServlet(sesIdRef));
                    assert srvs.compareAndSet(idx, null, srv);
                    Thread.sleep(100);
                }
                X.println("Stopping...");
                stop.set(true);
                return null;
            }
        }, 1, "restarter");
        Server srv = null;
        try {
            Random rnd = new Random();
            int n = 0;
            while (!(stop.get())) {
                int idx = -1;
                while (srv == null) {
                    idx = rnd.nextInt(WebSessionSelfTest.SRV_CNT);
                    srv = srvs.getAndSet(idx, null);
                } 
                assert idx != (-1);
                int port = (WebSessionSelfTest.TEST_JETTY_PORT) + idx;
                URLConnection conn = new URL((("http://localhost:" + port) + "/ignitetest/test")).openConnection();
                String sesId = sesIdRef.get();
                if (sesId != null)
                    conn.addRequestProperty("Cookie", ("JSESSIONID=" + sesId));

                conn.connect();
                String str;
                try (BufferedReader rdr = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    str = rdr.readLine();
                }
                assertEquals(n, Integer.parseInt(str));
                n++;
                assert srvs.compareAndSet(idx, null, srv);
                srv = null;
            } 
            X.println(((">>> Made " + n) + " requests."));
        } finally {
            restarterFut.get();
            if (srv != null)
                stopServer(srv);

            for (int i = 0; i < (srvs.length()); i++)
                stopServer(srvs.get(i));

        }
    }

    /**
     * Test servlet.
     */
    private static class SessionCreateServlet extends HttpServlet {
        /**
         * Keep binary flag.
         */
        private final boolean keepBinaryFlag;

        /**
         *
         *
         * @param keepBinaryFlag
         * 		Keep binary flag.
         */
        private SessionCreateServlet(final boolean keepBinaryFlag) {
            this.keepBinaryFlag = keepBinaryFlag;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            HttpSession ses = req.getSession(true);
            ses.setAttribute("checkCnt", 0);
            ses.setAttribute("key1", "val1");
            ses.setAttribute("key2", "val2");
            ses.setAttribute("mkey", new WebSessionSelfTest.TestObj("mval", keepBinaryFlag));
            WebSessionSelfTest.Profile p = ((WebSessionSelfTest.Profile) (ses.getAttribute("profile")));
            if (p == null) {
                p = new WebSessionSelfTest.Profile();
                ses.setAttribute("profile", p);
            }
            p.setMarker(req.getParameter("marker"));
            X.println(">>>", ("Created session: " + (ses.getId())), ">>>");
            res.getWriter().write(ses.getId());
            res.getWriter().flush();
        }
    }

    /**
     * Complex class for stored in session.
     */
    private static class Profile implements Serializable {
        /**
         * Marker string.
         */
        String marker;

        /**
         *
         *
         * @return marker
         */
        public String getMarker() {
            return marker;
        }

        /**
         *
         *
         * @param marker
         * 		
         */
        public void setMarker(String marker) {
            this.marker = marker;
        }
    }

    /**
     * Test for invalidated sessions.
     */
    private static class InvalidatedSessionServlet extends HttpServlet {
        /**
         * {@inheritDoc }
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            HttpSession ses = req.getSession();
            assert ses != null;
            final String sesId = ses.getId();
            if (req.getPathInfo().equals("/invalidated")) {
                X.println(">>>", ("Session to invalidate with id: " + sesId), ">>>");
                ses.invalidate();
                res.getWriter().println(sesId);
                // invalidates again.
                req.getSession().invalidate();
            } else
                if (req.getPathInfo().equals("/valid")) {
                    X.println(">>>", ("Created session: " + sesId), ">>>");
                    ses.setAttribute("key10", "val10");
                }

            res.getWriter().println(((req.getSession(false)) == null ? "null" : sesId));
            res.getWriter().flush();
        }
    }

    /**
     * Test session behavior on id change.
     */
    private static class SessionIdChangeServlet extends HttpServlet {
        /**
         * {@inheritDoc }
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            HttpSession ses = req.getSession();
            assertNotNull(ses);
            if (req.getPathInfo().equals("/chngsesid")) {
                ses.setAttribute("key1", "val1");
                X.println(">>>", ("Created session: " + (ses.getId())), ">>>");
                res.getWriter().println(req.getSession().getId());
                String newId = req.changeSessionId();
                // new id from genuine session.
                res.getWriter().println(newId);
                // new id from WebSession.
                res.getWriter().println(req.getSession().getId());
                res.getWriter().flush();
            } else
                if (req.getPathInfo().equals("/simple")) {
                    res.getWriter().println(req.getSession().getId());
                    res.getWriter().println(req.getSession().getAttribute("key1"));
                    res.getWriter().println(req.isRequestedSessionIdValid());
                    try {
                        req.getSession().invalidate();
                        res.getWriter().println(WebSessionSelfTest.INVALIDATED);
                    } catch (Exception ignored) {
                        res.getWriter().println(WebSessionSelfTest.FAILED);
                    }
                    res.getWriter().flush();
                } else
                    throw new ServletException(("Nonexisting path: " + (req.getPathInfo())));


        }
    }

    /**
     * Test session behavior on id change.
     */
    private static class SessionLoginServlet extends HttpServlet {
        /**
         * {@inheritDoc }
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            if (req.getPathInfo().equals("/test")) {
                HttpSession ses = req.getSession(true);
                assertNotNull(ses);
                ses.setAttribute("checkCnt", 0);
                ses.setAttribute("key1", "val1");
                ses.setAttribute("key2", "val2");
                ses.setAttribute("mkey", new WebSessionSelfTest.TestObj());
                WebSessionSelfTest.Profile p = ((WebSessionSelfTest.Profile) (ses.getAttribute("profile")));
                if (p == null) {
                    p = new WebSessionSelfTest.Profile();
                    ses.setAttribute("profile", p);
                }
                p.setMarker(req.getParameter("marker"));
                X.println(">>>", ("Request session test: " + (ses.getId())), ">>>");
                res.getWriter().write(ses.getId());
                res.getWriter().flush();
            } else
                if (req.getPathInfo().equals("/simple")) {
                    HttpSession ses = req.getSession();
                    X.println(">>>", ("Request session simple: " + (ses.getId())), ">>>");
                    res.getWriter().write(ses.getId());
                    res.getWriter().flush();
                }

        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            if (req.getPathInfo().equals("/login")) {
                try {
                    req.login("admin", "admin");
                } catch (Exception e) {
                    X.printerrln("Login failed due to exception.", e);
                }
                HttpSession ses = req.getSession();
                X.println(">>>", ("Logged In session: " + (ses.getId())), ">>>");
                res.getWriter().write(ses.getId());
                res.getWriter().flush();
            }
        }
    }

    /**
     * Servlet for restarts test.
     */
    private static class RestartsTestServlet extends HttpServlet {
        /**
         * Session ID.
         */
        private final AtomicReference<String> sesId;

        /**
         *
         *
         * @param sesId
         * 		Session ID.
         */
        RestartsTestServlet(AtomicReference<String> sesId) {
            this.sesId = sesId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            HttpSession ses = req.getSession(true);
            assertTrue(req.isRequestedSessionIdValid());
            sesId.compareAndSet(null, ses.getId());
            Integer attr = ((Integer) (ses.getAttribute("attr")));
            if (attr == null)
                attr = 0;
            else
                attr++;

            ses.setAttribute("attr", attr);
            res.getWriter().write(attr.toString());
            res.getWriter().flush();
        }
    }

    /**
     *
     */
    private static class TestObj implements Externalizable {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        private String val;

        /**
         *
         */
        private boolean keepBinaryFlag;

        /**
         *
         */
        public TestObj() {
        }

        /**
         *
         *
         * @param val
         * 		Value.
         * @param keepBinaryFlag
         * 		Keep binary flag.
         */
        public TestObj(final String val, final boolean keepBinaryFlag) {
            this.val = val;
            this.keepBinaryFlag = keepBinaryFlag;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            U.writeString(out, val);
            out.writeBoolean(keepBinaryFlag);
            System.out.println("TestObj marshalled");
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            val = U.readString(in);
            keepBinaryFlag = in.readBoolean();
            // It must be unmarshalled only on client side.
            if (keepBinaryFlag)
                fail("Should not be unmarshalled");

            System.out.println("TestObj unmarshalled");
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(final Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            final WebSessionSelfTest.TestObj testObj = ((WebSessionSelfTest.TestObj) (o));
            if ((keepBinaryFlag) != (testObj.keepBinaryFlag))
                return false;

            return (val) != null ? val.equals(testObj.val) : (testObj.val) == null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            int res = ((val) != null) ? val.hashCode() : 0;
            res = (31 * res) + (keepBinaryFlag ? 1 : 0);
            return res;
        }
    }
}

