/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.protocols.http;


import ESIntegTestCase.ClusterScope;
import io.crate.integrationtests.SQLHttpIntegrationTest;
import io.crate.testing.UseJdbc;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Test;


@UseJdbc(0)
@ClusterScope(numDataNodes = 1, numClientNodes = 0, supportsDedicatedMasters = false)
public class CrateHttpsTransportIntegrationTest extends SQLHttpIntegrationTest {
    private static File trustStoreFile;

    private static File keyStoreFile;

    public CrateHttpsTransportIntegrationTest() {
        super(true);
    }

    @Test
    public void testCheckEncryptedConnection() throws Throwable {
        CloseableHttpResponse response = post("{\"stmt\": \"select \'sslWorks\'\"}");
        assertThat(response, Matchers.not(Matchers.nullValue()));
        assertEquals(200, response.getStatusLine().getStatusCode());
        String result = EntityUtils.toString(response.getEntity());
        assertThat(result, Matchers.containsString("\"rowcount\":1"));
        assertThat(result, Matchers.containsString("sslWorks"));
    }

    @Test
    public void testBlobLayer() throws IOException {
        try {
            execute("create blob table test");
            final String blob = StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", (1024 * 600));
            String blobUrl = upload("test", blob);
            assertThat(blobUrl, Matchers.not(Matchers.isEmptyOrNullString()));
            HttpGet httpGet = new HttpGet(blobUrl);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertThat(response.getEntity().getContentLength(), Matchers.is(((long) (blob.length()))));
        } finally {
            execute("drop table if exists test");
        }
    }
}

