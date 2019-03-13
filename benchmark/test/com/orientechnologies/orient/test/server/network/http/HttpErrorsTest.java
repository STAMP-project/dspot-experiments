package com.orientechnologies.orient.test.server.network.http;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests HTTP errors command.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com) (l.garulli--at-orientdb.com)
 */
public class HttpErrorsTest extends BaseHttpTest {
    @Test
    public void testCommandNotFound() throws Exception {
        Assert.assertEquals(setUserName("root").setUserPassword("root").get("commandNotfound").getResponse().getStatusLine().getStatusCode(), 405);
    }

    @Test
    public void testCommandWrongMethod() throws Exception {
        Assert.assertEquals(setUserName("root").setUserPassword("root").post("listDatabases").getResponse().getStatusLine().getStatusCode(), 405);
    }
}

