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
package org.eclipse.jetty;


import HttpMethod.POST;
import HttpMethod.PUT;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import HttpStatus.OK_200;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.util.IO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JdbcLoginServiceTest {
    private static String _content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In quis felis nunc. " + (((((((((("Quisque suscipit mauris et ante auctor ornare rhoncus lacus aliquet. Pellentesque " + "habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. ") + "Vestibulum sit amet felis augue, vel convallis dolor. Cras accumsan vehicula diam ") + "at faucibus. Etiam in urna turpis, sed congue mi. Morbi et lorem eros. Donec vulputate ") + "velit in risus suscipit lobortis. Aliquam id urna orci, nec sollicitudin ipsum. ") + "Cras a orci turpis. Donec suscipit vulputate cursus. Mauris nunc tellus, fermentum ") + "eu auctor ut, mollis at diam. Quisque porttitor ultrices metus, vitae tincidunt massa ") + "sollicitudin a. Vivamus porttitor libero eget purus hendrerit cursus. Integer aliquam ") + "consequat mauris quis luctus. Cras enim nibh, dignissim eu faucibus ac, mollis nec neque. ") + "Aliquam purus mauris, consectetur nec convallis lacinia, porta sed ante. Suspendisse ") + "et cursus magna. Donec orci enim, molestie a lobortis eu, imperdiet vitae neque.");

    private static File _docRoot;

    private static HttpClient _client;

    private static String __realm = "JdbcRealm";

    private static URI _baseUri;

    private static DatabaseLoginServiceTestServer _testServer;

    @Test
    public void testPut() throws Exception {
        try {
            startClient();
            Request request = JdbcLoginServiceTest._client.newRequest(JdbcLoginServiceTest._baseUri.resolve("output.txt"));
            request.method(PUT);
            request.content(new BytesContentProvider(JdbcLoginServiceTest._content.getBytes()));
            ContentResponse response = request.send();
            int responseStatus = response.getStatus();
            boolean statusOk = (responseStatus == 200) || (responseStatus == 201);
            Assertions.assertTrue(statusOk);
            String content = IO.toString(new FileInputStream(new File(JdbcLoginServiceTest._docRoot, "output.txt")));
            Assertions.assertEquals(JdbcLoginServiceTest._content, content);
        } finally {
            stopClient();
        }
    }

    @Test
    public void testGet() throws Exception {
        try {
            startClient();
            ContentResponse response = JdbcLoginServiceTest._client.GET(JdbcLoginServiceTest._baseUri.resolve("input.txt"));
            Assertions.assertEquals(SC_OK, response.getStatus());
            Assertions.assertEquals(JdbcLoginServiceTest._content, response.getContentAsString());
        } finally {
            stopClient();
        }
    }

    @Test
    public void testGetNonExistantUser() throws Exception {
        try {
            startClient("foo", "bar");
            ContentResponse response = JdbcLoginServiceTest._client.GET(JdbcLoginServiceTest._baseUri.resolve("input.txt"));
            Assertions.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        } finally {
            stopClient();
        }
    }

    @Test
    public void testPost() throws Exception {
        try {
            startClient();
            Request request = JdbcLoginServiceTest._client.newRequest(JdbcLoginServiceTest._baseUri.resolve("test"));
            request.method(POST);
            request.content(new BytesContentProvider(JdbcLoginServiceTest._content.getBytes()));
            ContentResponse response = request.send();
            Assertions.assertEquals(OK_200, response.getStatus());
            Assertions.assertEquals(JdbcLoginServiceTest._content, JdbcLoginServiceTest._testServer.getTestHandler().getRequestContent());
        } finally {
            stopClient();
        }
    }
}

