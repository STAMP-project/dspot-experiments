package com.orientechnologies.orient.test.server.network.http;


import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test HTTP "gephi" command.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com) (l.garulli--at-orientdb.com)
 */
public class HttpGephiTest extends BaseHttpDatabaseTest {
    @Test
    public void commandRootCredentials() throws IOException {
        HttpResponse response = get((("gephi/" + (getDatabaseName())) + "/sql/select%20from%20V")).setUserName("root").setUserPassword("root").getResponse();
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void commandDatabaseCredentials() throws IOException {
        HttpResponse response = get((("gephi/" + (getDatabaseName())) + "/sql/select%20from%20V")).setUserName("admin").setUserPassword("admin").getResponse();
        response.getEntity().writeTo(System.out);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
}

