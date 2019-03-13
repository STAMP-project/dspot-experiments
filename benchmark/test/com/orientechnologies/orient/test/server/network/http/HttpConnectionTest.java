package com.orientechnologies.orient.test.server.network.http;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests HTTP "connect" command.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com) (l.garulli--at-orientdb.com)
 */
public class HttpConnectionTest extends BaseHttpDatabaseTest {
    @Test
    public void testConnect() throws Exception {
        Assert.assertEquals(get(("connect/" + (getDatabaseName()))).getResponse().getStatusLine().getStatusCode(), 204);
    }
}

