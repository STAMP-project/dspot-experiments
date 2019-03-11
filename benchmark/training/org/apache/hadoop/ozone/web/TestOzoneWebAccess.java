/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.web;


import Header.OZONE_USER;
import Header.OZONE_V1_VERSION_HEADER;
import Header.OZONE_VERSION_HEADER;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.DATE;
import OzoneConsts.OZONE_SIMPLE_HDFS_USER;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.ozone.client.rest.headers.Header;
import org.apache.hadoop.util.Time;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test Ozone Access through REST protocol.
 */
public class TestOzoneWebAccess {
    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static MiniOzoneCluster cluster;

    private static int port;

    /**
     * Send a vaild Ozone Request.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testOzoneRequest() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.US);
        CloseableHttpClient client = HttpClients.createDefault();
        String volumeName = getRequestID().toLowerCase(Locale.US);
        try {
            HttpPost httppost = new HttpPost(String.format("http://localhost:%d/%s", TestOzoneWebAccess.port, volumeName));
            httppost.addHeader(OZONE_VERSION_HEADER, OZONE_V1_VERSION_HEADER);
            httppost.addHeader(DATE, format.format(new Date(Time.now())));
            httppost.addHeader(AUTHORIZATION, (((Header.OZONE_SIMPLE_AUTHENTICATION_SCHEME) + " ") + (OzoneConsts.OZONE_SIMPLE_HDFS_USER)));
            httppost.addHeader(OZONE_USER, OZONE_SIMPLE_HDFS_USER);
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(response.toString(), HttpURLConnection.HTTP_CREATED, response.getStatusLine().getStatusCode());
        } finally {
            client.close();
        }
    }
}

