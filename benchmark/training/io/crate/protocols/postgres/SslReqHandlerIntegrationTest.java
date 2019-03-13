/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.protocols.postgres;


import ESIntegTestCase.ClusterScope;
import io.crate.integrationtests.SQLTransportIntegrationTest;
import io.crate.testing.SQLResponse;
import io.crate.testing.UseJdbc;
import java.io.File;
import org.junit.Test;


@UseJdbc(1)
@ClusterScope(numDataNodes = 1, numClientNodes = 0, supportsDedicatedMasters = false)
public class SslReqHandlerIntegrationTest extends SQLTransportIntegrationTest {
    private static File trustStoreFile;

    private static File keyStoreFile;

    public SslReqHandlerIntegrationTest() {
        super(true);
    }

    @Test
    public void testCheckEncryptedConnection() throws Throwable {
        SQLResponse response = execute("select name from sys.nodes");
        assertEquals(1, response.rowCount());
    }
}

