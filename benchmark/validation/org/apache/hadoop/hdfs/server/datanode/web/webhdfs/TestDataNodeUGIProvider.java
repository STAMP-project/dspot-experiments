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
package org.apache.hadoop.hdfs.server.datanode.web.webhdfs;


import com.google.common.collect.Lists;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.net.URI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.hdfs.web.WebHdfsConstants;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.hdfs.web.resources.LengthParam;
import org.apache.hadoop.hdfs.web.resources.NamenodeAddressParam;
import org.apache.hadoop.hdfs.web.resources.OffsetParam;
import org.apache.hadoop.hdfs.web.resources.Param;
import org.apache.hadoop.hdfs.web.resources.UserParam;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;


public class TestDataNodeUGIProvider {
    private final URI uri = URI.create((((WebHdfsConstants.WEBHDFS_SCHEME) + "://") + "127.0.0.1:0"));

    private final String PATH = "/foo";

    private final int OFFSET = 42;

    private final int LENGTH = 512;

    private static final int EXPIRE_AFTER_ACCESS = 5 * 1000;

    private Configuration conf;

    @Test
    public void testUGICacheSecure() throws Exception {
        // fake turning on security so api thinks it should use tokens
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, conf);
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        ugi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
        ugi = UserGroupInformation.createProxyUser("test-proxy-user", ugi);
        UserGroupInformation.setLoginUser(ugi);
        List<Token<DelegationTokenIdentifier>> tokens = Lists.newArrayList();
        getWebHdfsFileSystem(ugi, conf, tokens);
        String uri1 = (((WebHdfsFileSystem.PATH_PREFIX) + (PATH)) + "?op=OPEN") + (Param.toSortedString("&", new NamenodeAddressParam("127.0.0.1:1010"), new OffsetParam(((long) (OFFSET))), new LengthParam(((long) (LENGTH))), new org.apache.hadoop.hdfs.web.resources.DelegationParam(tokens.get(0).encodeToUrlString())));
        String uri2 = (((WebHdfsFileSystem.PATH_PREFIX) + (PATH)) + "?op=OPEN") + (Param.toSortedString("&", new NamenodeAddressParam("127.0.0.1:1010"), new OffsetParam(((long) (OFFSET))), new LengthParam(((long) (LENGTH))), new org.apache.hadoop.hdfs.web.resources.DelegationParam(tokens.get(1).encodeToUrlString())));
        DataNodeUGIProvider ugiProvider1 = new DataNodeUGIProvider(new ParameterParser(new QueryStringDecoder(URI.create(uri1)), conf));
        UserGroupInformation ugi11 = ugiProvider1.ugi();
        UserGroupInformation ugi12 = ugiProvider1.ugi();
        Assert.assertEquals("With UGI cache, two UGIs returned by the same token should be same", ugi11, ugi12);
        DataNodeUGIProvider ugiProvider2 = new DataNodeUGIProvider(new ParameterParser(new QueryStringDecoder(URI.create(uri2)), conf));
        UserGroupInformation url21 = ugiProvider2.ugi();
        UserGroupInformation url22 = ugiProvider2.ugi();
        Assert.assertEquals("With UGI cache, two UGIs returned by the same token should be same", url21, url22);
        Assert.assertNotEquals("With UGI cache, two UGIs for the different token should not be same", ugi11, url22);
        ugiProvider2.clearCache();
        awaitCacheEmptyDueToExpiration();
        ugi12 = ugiProvider1.ugi();
        url22 = ugiProvider2.ugi();
        String msg = "With cache eviction, two UGIs returned" + " by the same token should not be same";
        Assert.assertNotEquals(msg, ugi11, ugi12);
        Assert.assertNotEquals(msg, url21, url22);
        Assert.assertNotEquals("With UGI cache, two UGIs for the different token should not be same", ugi11, url22);
    }

    @Test
    public void testUGICacheInSecure() throws Exception {
        String uri1 = (((WebHdfsFileSystem.PATH_PREFIX) + (PATH)) + "?op=OPEN") + (Param.toSortedString("&", new OffsetParam(((long) (OFFSET))), new LengthParam(((long) (LENGTH))), new UserParam("root")));
        String uri2 = (((WebHdfsFileSystem.PATH_PREFIX) + (PATH)) + "?op=OPEN") + (Param.toSortedString("&", new OffsetParam(((long) (OFFSET))), new LengthParam(((long) (LENGTH))), new UserParam("hdfs")));
        DataNodeUGIProvider ugiProvider1 = new DataNodeUGIProvider(new ParameterParser(new QueryStringDecoder(URI.create(uri1)), conf));
        UserGroupInformation ugi11 = ugiProvider1.ugi();
        UserGroupInformation ugi12 = ugiProvider1.ugi();
        Assert.assertEquals("With UGI cache, two UGIs for the same user should be same", ugi11, ugi12);
        DataNodeUGIProvider ugiProvider2 = new DataNodeUGIProvider(new ParameterParser(new QueryStringDecoder(URI.create(uri2)), conf));
        UserGroupInformation url21 = ugiProvider2.ugi();
        UserGroupInformation url22 = ugiProvider2.ugi();
        Assert.assertEquals("With UGI cache, two UGIs for the same user should be same", url21, url22);
        Assert.assertNotEquals("With UGI cache, two UGIs for the different user should not be same", ugi11, url22);
        awaitCacheEmptyDueToExpiration();
        ugi12 = ugiProvider1.ugi();
        url22 = ugiProvider2.ugi();
        String msg = "With cache eviction, two UGIs returned by" + " the same user should not be same";
        Assert.assertNotEquals(msg, ugi11, ugi12);
        Assert.assertNotEquals(msg, url21, url22);
        Assert.assertNotEquals("With UGI cache, two UGIs for the different user should not be same", ugi11, url22);
    }
}

