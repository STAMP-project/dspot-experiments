/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.rest;


import Action.READ;
import MediaType.APPLICATION_JSON_TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.rest.model.CellModel;
import org.apache.hadoop.hbase.rest.model.CellSetModel;
import org.apache.hadoop.hbase.rest.model.RowModel;
import org.apache.hadoop.hbase.security.access.AccessControlClient;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.http.HttpEntity;
import org.apache.http.auth.Credentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for SPNEGO authentication on the HttpServer. Uses Kerby's MiniKDC and Apache
 * HttpComponents to verify that a simple Servlet is reachable via SPNEGO and unreachable w/o.
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestSecureRESTServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSecureRESTServer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSecureRESTServer.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST = new HBaseRESTTestingUtility();

    private static MiniHBaseCluster CLUSTER;

    private static final String HOSTNAME = "localhost";

    private static final String CLIENT_PRINCIPAL = "client";

    // The principal for accepting SPNEGO authn'ed requests (*must* be HTTP/fqdn)
    private static final String SPNEGO_SERVICE_PRINCIPAL = "HTTP/" + (TestSecureRESTServer.HOSTNAME);

    // The principal we use to connect to HBase
    private static final String REST_SERVER_PRINCIPAL = "rest";

    private static final String SERVICE_PRINCIPAL = "hbase/" + (TestSecureRESTServer.HOSTNAME);

    private static URL baseUrl;

    private static MiniKdc KDC;

    private static RESTServer server;

    private static File restServerKeytab;

    private static File clientKeytab;

    private static File serviceKeytab;

    @Test
    public void testPositiveAuthorization() throws Exception {
        // Create a table, write a row to it, grant read perms to the client
        UserGroupInformation superuser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(TestSecureRESTServer.SERVICE_PRINCIPAL, TestSecureRESTServer.serviceKeytab.getAbsolutePath());
        final TableName table = TableName.valueOf("publicTable");
        superuser.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestSecureRESTServer.TEST_UTIL.getConfiguration())) {
                    TableDescriptor desc = TableDescriptorBuilder.newBuilder(table).setColumnFamily(ColumnFamilyDescriptorBuilder.of("f1")).build();
                    conn.getAdmin().createTable(desc);
                    try (Table t = conn.getTable(table)) {
                        Put p = new Put(Bytes.toBytes("a"));
                        p.addColumn(Bytes.toBytes("f1"), new byte[0], Bytes.toBytes("1"));
                        t.put(p);
                    }
                    AccessControlClient.grant(conn, TestSecureRESTServer.CLIENT_PRINCIPAL, READ);
                } catch (Throwable e) {
                    if (e instanceof Exception) {
                        throw ((Exception) (e));
                    } else {
                        throw new Exception(e);
                    }
                }
                return null;
            }
        });
        // Read that row as the client
        Pair<CloseableHttpClient, HttpClientContext> pair = getClient();
        CloseableHttpClient client = pair.getFirst();
        HttpClientContext context = pair.getSecond();
        HttpGet get = new HttpGet(((((new URL(("http://localhost:" + (TestSecureRESTServer.REST_TEST.getServletPort()))).toURI()) + "/") + table) + "/a"));
        get.addHeader("Accept", "application/json");
        UserGroupInformation user = UserGroupInformation.loginUserFromKeytabAndReturnUGI(TestSecureRESTServer.CLIENT_PRINCIPAL, TestSecureRESTServer.clientKeytab.getAbsolutePath());
        String jsonResponse = user.doAs(new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                try (CloseableHttpResponse response = client.execute(get, context)) {
                    final int statusCode = response.getStatusLine().getStatusCode();
                    Assert.assertEquals(response.getStatusLine().toString(), HttpURLConnection.HTTP_OK, statusCode);
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity);
                }
            }
        });
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel model = mapper.readValue(jsonResponse, CellSetModel.class);
        Assert.assertEquals(1, model.getRows().size());
        RowModel row = model.getRows().get(0);
        Assert.assertEquals("a", Bytes.toString(row.getKey()));
        Assert.assertEquals(1, row.getCells().size());
        CellModel cell = row.getCells().get(0);
        Assert.assertEquals("1", Bytes.toString(cell.getValue()));
    }

    @Test
    public void testNegativeAuthorization() throws Exception {
        Pair<CloseableHttpClient, HttpClientContext> pair = getClient();
        CloseableHttpClient client = pair.getFirst();
        HttpClientContext context = pair.getSecond();
        StringEntity entity = new StringEntity("{\"name\":\"test\", \"ColumnSchema\":[{\"name\":\"f\"}]}", ContentType.APPLICATION_JSON);
        HttpPut put = new HttpPut((("http://localhost:" + (TestSecureRESTServer.REST_TEST.getServletPort())) + "/test/schema"));
        put.setEntity(entity);
        UserGroupInformation unprivileged = UserGroupInformation.loginUserFromKeytabAndReturnUGI(TestSecureRESTServer.CLIENT_PRINCIPAL, TestSecureRESTServer.clientKeytab.getAbsolutePath());
        unprivileged.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (CloseableHttpResponse response = client.execute(put, context)) {
                    final int statusCode = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();
                    Assert.assertEquals(("Got response: " + (EntityUtils.toString(entity))), HttpURLConnection.HTTP_FORBIDDEN, statusCode);
                }
                return null;
            }
        });
    }

    private static class EmptyCredentials implements Credentials {
        public static final TestSecureRESTServer.EmptyCredentials INSTANCE = new TestSecureRESTServer.EmptyCredentials();

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }
    }
}

