/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.broker.security;


import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBAuthenticatorTest {
    private static final Logger LOG = LoggerFactory.getLogger(DBAuthenticatorTest.class);

    public static final String ORG_H2_DRIVER = "org.h2.Driver";

    public static final String JDBC_H2_MEM_TEST = "jdbc:h2:mem:test";

    public static final String SHA_256 = "SHA-256";

    private Connection connection;

    @Test
    public void Db_verifyValid() {
        final DBAuthenticator dbAuthenticator = new DBAuthenticator(DBAuthenticatorTest.ORG_H2_DRIVER, DBAuthenticatorTest.JDBC_H2_MEM_TEST, "SELECT PASSWORD FROM ACCOUNT WHERE LOGIN=?", DBAuthenticatorTest.SHA_256);
        Assert.assertTrue(dbAuthenticator.checkValid(null, "dbuser", "password".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void Db_verifyInvalidLogin() {
        final DBAuthenticator dbAuthenticator = new DBAuthenticator(DBAuthenticatorTest.ORG_H2_DRIVER, DBAuthenticatorTest.JDBC_H2_MEM_TEST, "SELECT PASSWORD FROM ACCOUNT WHERE LOGIN=?", DBAuthenticatorTest.SHA_256);
        Assert.assertFalse(dbAuthenticator.checkValid(null, "dbuser2", "password".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void Db_verifyInvalidPassword() {
        final DBAuthenticator dbAuthenticator = new DBAuthenticator(DBAuthenticatorTest.ORG_H2_DRIVER, DBAuthenticatorTest.JDBC_H2_MEM_TEST, "SELECT PASSWORD FROM ACCOUNT WHERE LOGIN=?", DBAuthenticatorTest.SHA_256);
        Assert.assertFalse(dbAuthenticator.checkValid(null, "dbuser", "wrongPassword".getBytes(StandardCharsets.UTF_8)));
    }
}

