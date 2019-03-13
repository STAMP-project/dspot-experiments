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
package org.apache.tomcat.jdbc.pool;


import org.junit.Assert;
import org.junit.Test;


public class ShouldForceReconnectTest {
    private ConnectionPool pool;

    private PoolProperties properties;

    private static final String DEFAULT_USER = "username_def";

    private static final String DEFAULT_PASSWD = "password_def";

    private static final String ALT_USER = "username_alt";

    private static final String ALT_PASSWD = "password_alt";

    @Test
    public void testShouldForceReconnect() throws Exception {
        PooledConnection con = new PooledConnection(properties, pool);
        // connection previously connect with default
        configureDefault(con);
        Assert.assertFalse(con.shouldForceReconnect(null, null));
        configureDefault(con);
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
        configureDefault(con);
        Assert.assertFalse(con.shouldForceReconnect(null, ShouldForceReconnectTest.DEFAULT_PASSWD));
        configureDefault(con);
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, null));
        configureDefault(con);
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        configureDefault(con);
        Assert.assertTrue(con.shouldForceReconnect(null, ShouldForceReconnectTest.ALT_PASSWD));
        configureDefault(con);
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, null));
        // connection previously connect with alternate
        configureAlt(con);
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(null, null));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(null, ShouldForceReconnectTest.DEFAULT_PASSWD));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, null));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(null, ShouldForceReconnectTest.ALT_PASSWD));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, null));
        // test changes in username password
        configureDefault(con);
        Assert.assertFalse(con.shouldForceReconnect(null, null));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertTrue(con.shouldForceReconnect(null, null));
        configureDefault(con);
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
        configureAlt(con);
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
        Assert.assertFalse(con.shouldForceReconnect(null, null));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        configureAlt(con);
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertFalse(con.shouldForceReconnect(ShouldForceReconnectTest.ALT_USER, ShouldForceReconnectTest.ALT_PASSWD));
        Assert.assertTrue(con.shouldForceReconnect(ShouldForceReconnectTest.DEFAULT_USER, ShouldForceReconnectTest.DEFAULT_PASSWD));
    }
}

