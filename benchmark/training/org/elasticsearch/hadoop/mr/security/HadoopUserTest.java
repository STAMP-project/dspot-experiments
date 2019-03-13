/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.mr.security;


import ClusterName.UNNAMED_CLUSTER_NAME;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.elasticsearch.hadoop.EsHadoopException;
import org.elasticsearch.hadoop.security.EsToken;
import org.elasticsearch.hadoop.security.User;
import org.elasticsearch.hadoop.util.ClusterName;
import org.elasticsearch.hadoop.util.EsMajorVersion;
import org.elasticsearch.hadoop.util.TestSettings;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HadoopUserTest {
    @Test
    public void getEsToken() throws IOException {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        String testClusterName = "getEsTokenTest";
        User hadoopUser = new HadoopUser(ugi, new TestSettings());
        Assert.assertThat(hadoopUser.getEsToken(null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(UNNAMED_CLUSTER_NAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(testClusterName), Matchers.is(Matchers.nullValue()));
        EsToken testToken = new EsToken("gmarx", "swordfish", "mary", ((System.currentTimeMillis()) + 100000L), testClusterName, EsMajorVersion.LATEST);
        EsToken unnamedToken = new EsToken("luggage", "12345", "12345", ((System.currentTimeMillis()) + 100000L), ClusterName.UNNAMED_CLUSTER_NAME, EsMajorVersion.LATEST);
        EsTokenIdentifier identifier = new EsTokenIdentifier();
        byte[] id = identifier.getBytes();
        Text kind = identifier.getKind();
        for (EsToken token : new EsToken[]{ testToken, unnamedToken }) {
            Text service = new Text(token.getClusterName());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                token.writeOut(new DataOutputStream(buffer));
            } catch (IOException e) {
                throw new EsHadoopException("Could not serialize token information", e);
            }
            byte[] pw = buffer.toByteArray();
            ugi.addToken(new org.apache.hadoop.security.token.Token<EsTokenIdentifier>(id, pw, kind, service));
        }
        Assert.assertThat(hadoopUser.getEsToken(null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(UNNAMED_CLUSTER_NAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(testClusterName), Matchers.is(Matchers.equalTo(testToken)));
    }

    @Test
    public void addEsToken() throws IOException {
        String testClusterName = "addEsTokenTest";
        User hadoopUser = new HadoopUser(UserGroupInformation.getCurrentUser(), new TestSettings());
        Assert.assertThat(hadoopUser.getEsToken(null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(UNNAMED_CLUSTER_NAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(testClusterName), Matchers.is(Matchers.nullValue()));
        EsToken testToken = new EsToken("gmarx", "swordfish", "mary", ((System.currentTimeMillis()) + 100000L), testClusterName, EsMajorVersion.LATEST);
        EsToken testToken2 = new EsToken("zmarx", "pantomime", "pantomime", ((System.currentTimeMillis()) + 100000L), testClusterName, EsMajorVersion.LATEST);
        EsToken unnamedToken = new EsToken("luggage", "12345", "12345", ((System.currentTimeMillis()) + 100000L), ClusterName.UNNAMED_CLUSTER_NAME, EsMajorVersion.LATEST);
        hadoopUser.addEsToken(testToken);
        hadoopUser.addEsToken(unnamedToken);
        Assert.assertThat(hadoopUser.getEsToken(null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(UNNAMED_CLUSTER_NAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(testClusterName), Matchers.is(Matchers.equalTo(testToken)));
        hadoopUser.addEsToken(testToken2);
        Assert.assertThat(hadoopUser.getEsToken(null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(UNNAMED_CLUSTER_NAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(hadoopUser.getEsToken(testClusterName), Matchers.is(Matchers.equalTo(testToken2)));
    }

    @Test
    public void getKerberosPrincipal() throws IOException {
        User jdkUser = new HadoopUser(UserGroupInformation.getCurrentUser(), new TestSettings());
        // This should always be null - We aren't running with Kerberos enabled in this test.
        // See HadoopUserKerberosTest for that.
        Assert.assertThat(jdkUser.getKerberosPrincipal(), Matchers.is(Matchers.nullValue()));
    }
}

