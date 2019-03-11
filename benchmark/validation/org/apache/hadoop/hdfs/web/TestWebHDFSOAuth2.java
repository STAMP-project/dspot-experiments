/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hdfs.web;


import CredentialBasedAccessTokenProvider.OAUTH_CREDENTIAL_KEY;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.apache.hadoop.hdfs.web.oauth2.OAuth2ConnectionConfigurator;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static WebHdfsFileSystem.PATH_PREFIX;


public class TestWebHDFSOAuth2 {
    public static final Logger LOG = LoggerFactory.getLogger(TestWebHDFSOAuth2.class);

    private ClientAndServer mockWebHDFS;

    private ClientAndServer mockOAuthServer;

    public static final int WEBHDFS_PORT = 7552;

    public static final int OAUTH_PORT = 7553;

    public static final Header CONTENT_TYPE_APPLICATION_JSON = new Header("Content-Type", "application/json");

    public static final String AUTH_TOKEN = "0123456789abcdef";

    public static final Header AUTH_TOKEN_HEADER = new Header("AUTHORIZATION", ((OAuth2ConnectionConfigurator.HEADER) + (TestWebHDFSOAuth2.AUTH_TOKEN)));

    @Test
    public void listStatusReturnsAsExpected() throws IOException, URISyntaxException {
        MockServerClient mockWebHDFSServerClient = new MockServerClient("localhost", TestWebHDFSOAuth2.WEBHDFS_PORT);
        MockServerClient mockOAuthServerClient = new MockServerClient("localhost", TestWebHDFSOAuth2.OAUTH_PORT);
        HttpRequest oauthServerRequest = getOAuthServerMockRequest(mockOAuthServerClient);
        HttpRequest fileSystemRequest = request().withMethod("GET").withPath(((PATH_PREFIX) + "/test1/test2")).withHeader(TestWebHDFSOAuth2.AUTH_TOKEN_HEADER);
        try {
            mockWebHDFSServerClient.when(fileSystemRequest, exactly(1)).respond(response().withStatusCode(HttpStatus.SC_OK).withHeaders(TestWebHDFSOAuth2.CONTENT_TYPE_APPLICATION_JSON).withBody(("{\n" + (((((((((((((((((((((((((((((("  \"FileStatuses\":\n" + "  {\n") + "    \"FileStatus\":\n") + "    [\n") + "      {\n") + "        \"accessTime\"      : 1320171722771,\n") + "        \"blockSize\"       : 33554432,\n") + "        \"group\"           : \"supergroup\",\n") + "        \"length\"          : 24930,\n") + "        \"modificationTime\": 1320171722771,\n") + "        \"owner\"           : \"webuser\",\n") + "        \"pathSuffix\"      : \"a.patch\",\n") + "        \"permission\"      : \"644\",\n") + "        \"replication\"     : 1,\n") + "        \"type\"            : \"FILE\"\n") + "      },\n") + "      {\n") + "        \"accessTime\"      : 0,\n") + "        \"blockSize\"       : 0,\n") + "        \"group\"           : \"supergroup\",\n") + "        \"length\"          : 0,\n") + "        \"modificationTime\": 1320895981256,\n") + "        \"owner\"           : \"szetszwo\",\n") + "        \"pathSuffix\"      : \"bar\",\n") + "        \"permission\"      : \"711\",\n") + "        \"replication\"     : 0,\n") + "        \"type\"            : \"DIRECTORY\"\n") + "      }\n") + "    ]\n") + "  }\n") + "}\n"))));
            FileSystem fs = new WebHdfsFileSystem();
            Configuration conf = getConfiguration();
            conf.set(HdfsClientConfigKeys.OAUTH_REFRESH_URL_KEY, (("http://localhost:" + (TestWebHDFSOAuth2.OAUTH_PORT)) + "/refresh"));
            conf.set(OAUTH_CREDENTIAL_KEY, "credential");
            URI uri = new URI(("webhdfs://localhost:" + (TestWebHDFSOAuth2.WEBHDFS_PORT)));
            fs.initialize(uri, conf);
            FileStatus[] ls = fs.listStatus(new Path("/test1/test2"));
            mockOAuthServer.verify(oauthServerRequest);
            mockWebHDFSServerClient.verify(fileSystemRequest);
            Assert.assertEquals(2, ls.length);
            Assert.assertEquals("a.patch", ls[0].getPath().getName());
            Assert.assertEquals("bar", ls[1].getPath().getName());
            fs.close();
        } finally {
            mockWebHDFSServerClient.clear(fileSystemRequest);
            mockOAuthServerClient.clear(oauthServerRequest);
        }
    }
}

