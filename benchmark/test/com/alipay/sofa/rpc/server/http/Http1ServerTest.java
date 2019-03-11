/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.server.http;


import RemotingConstants.HEAD_SERIALIZE_TYPE;
import RpcConstants.PROTOCOL_TYPE_HTTP;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.server.bolt.pb.EchoRequest;
import com.alipay.sofa.rpc.server.bolt.pb.EchoResponse;
import com.alipay.sofa.rpc.server.bolt.pb.Group;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class Http1ServerTest extends ActivelyDestroyTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testHttp1General() throws Exception {
        // ??1??? ??
        ServerConfig serverConfig = new ServerConfig().setStopTimeout(60000).setPort(12300).setProtocol(PROTOCOL_TYPE_HTTP).setDaemon(true);
        // ??????????????1?
        ProviderConfig<HttpService> providerConfig = new ProviderConfig<HttpService>().setInterfaceId(HttpService.class.getName()).setRef(new HttpServiceImpl()).setApplication(new ApplicationConfig().setAppName("serverApp")).setServer(serverConfig).setUniqueId("uuu").setRegister(false);
        providerConfig.export();
        HttpClient httpclient = HttpClientBuilder.create().build();
        {
            // GET ??
            String url = "http://127.0.0.1:12300/favicon.ico";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            Assert.assertTrue(StringUtils.isEmpty(getStringContent(httpResponse)));
        }
        {
            // ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService/add";
            HttpOptions httpGet = new HttpOptions(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(400, httpResponse.getStatusLine().getStatusCode());
            Assert.assertTrue(StringUtils.isNotEmpty(getStringContent(httpResponse)));
        }
        {
            // HEAD ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService/add";
            HttpHead httpHead = new HttpHead(url);
            HttpResponse httpResponse = httpclient.execute(httpHead);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
        }
        {
            // HEAD ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/xasdasdasd";
            HttpHead httpHead = new HttpHead(url);
            HttpResponse httpResponse = httpclient.execute(httpHead);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
        }
        {
            // HEAD ?????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/add";
            HttpHead httpHead = new HttpHead(url);
            HttpResponse httpResponse = httpclient.execute(httpHead);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        }
        {
            // GET ???????
            String url = "http://127.0.0.1:12300/com.alipay.sofa";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(400, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // GET ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService/asdasdas?code=xxx";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
            Assert.assertTrue(getStringContent(httpResponse).contains("asdasdas"));
        }
        {
            // GET ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/asdasdas?code=xxx";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
            Assert.assertTrue(getStringContent(httpResponse).contains("asdasdas"));
        }
        {
            // GET ?????????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/query";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(400, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // GET ?????????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/add?code=1";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(400, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // GET ?????????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/query?code=xxx";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(400, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // GET ??
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/add?code=1&name=22";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            Assert.assertEquals("221", getStringContent(httpResponse));
        }
        {
            // POST ?????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService/adasdad";
            HttpPost httpPost = new HttpPost(url);
            EchoRequest request = EchoRequest.newBuilder().setGroup(Group.A).setName("xxx").build();
            ByteArrayEntity entity = new ByteArrayEntity(toByteArray(), null);
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(400, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
    }

    @Test
    public void testHttp1Protobuf() throws Exception {
        // ??1??? ??
        ServerConfig serverConfig = new ServerConfig().setStopTimeout(60000).setPort(12300).setProtocol(PROTOCOL_TYPE_HTTP).setDaemon(true);
        // ??????????????1?
        ProviderConfig<HttpService> providerConfig = new ProviderConfig<HttpService>().setInterfaceId(HttpService.class.getName()).setRef(new HttpServiceImpl()).setApplication(new ApplicationConfig().setAppName("serverApp")).setServer(serverConfig).setUniqueId("uuu").setRegister(false);
        providerConfig.export();
        HttpClient httpclient = HttpClientBuilder.create().build();
        {
            // POST ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService/adasdad";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HEAD_SERIALIZE_TYPE, "protobuf");
            EchoRequest request = EchoRequest.newBuilder().setGroup(Group.A).setName("xxx").build();
            ByteArrayEntity entity = new ByteArrayEntity(toByteArray(), ContentType.create("application/protobuf"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // POST ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/adasdad";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HEAD_SERIALIZE_TYPE, "protobuf");
            EchoRequest request = EchoRequest.newBuilder().setGroup(Group.A).setName("xxx").build();
            ByteArrayEntity entity = new ByteArrayEntity(toByteArray(), ContentType.create("application/protobuf"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // POST ?? HEAD_SERIALIZE_TYPE
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/echoPb";
            HttpPost httpPost = new HttpPost(url);
            EchoRequest request = EchoRequest.newBuilder().setGroup(Group.A).setName("xxx").build();
            ByteArrayEntity entity = new ByteArrayEntity(toByteArray(), ContentType.create("application/protobuf"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
            EchoResponse response = EchoResponse.parseFrom(data);
            Assert.assertEquals("helloxxx", response.getMessage());
        }
        {
            // POST ????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/echoPb";
            HttpPost httpPost = new HttpPost(url);
            EchoRequest request = EchoRequest.newBuilder().setGroup(Group.A).setName("xxx").build();
            httpPost.setHeader(HEAD_SERIALIZE_TYPE, "protobuf");
            ByteArrayEntity entity = new ByteArrayEntity(toByteArray(), ContentType.create("application/protobuf"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
            EchoResponse response = EchoResponse.parseFrom(data);
            Assert.assertEquals("helloxxx", response.getMessage());
        }
    }

    @Test
    public void testHttp1Json() throws Exception {
        // ??1??? ??
        ServerConfig serverConfig = new ServerConfig().setStopTimeout(60000).setPort(12300).setProtocol(PROTOCOL_TYPE_HTTP).setDaemon(true);
        // ??????????????1?
        ProviderConfig<HttpService> providerConfig = new ProviderConfig<HttpService>().setInterfaceId(HttpService.class.getName()).setRef(new HttpServiceImpl()).setApplication(new ApplicationConfig().setAppName("serverApp")).setServer(serverConfig).setUniqueId("uuu").setRegister(false);
        providerConfig.export();
        HttpClient httpclient = HttpClientBuilder.create().build();
        {
            // POST jackson??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService/adasdad";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HEAD_SERIALIZE_TYPE, "json");
            ExampleObj obj = new ExampleObj();
            obj.setId(1);
            obj.setName("xxx");
            byte[] bytes = mapper.writeValueAsBytes(obj);
            ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.create("application/json"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // POST ??????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/adasdad";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HEAD_SERIALIZE_TYPE, "json");
            ExampleObj obj = new ExampleObj();
            obj.setId(1);
            obj.setName("xxx");
            byte[] bytes = mapper.writeValueAsBytes(obj);
            ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.create("application/json"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
            Assert.assertNotNull(getStringContent(httpResponse));
        }
        {
            // POST ?? HEAD_SERIALIZE_TYPE
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/object";
            HttpPost httpPost = new HttpPost(url);
            ExampleObj obj = new ExampleObj();
            obj.setId(1);
            obj.setName("xxx");
            byte[] bytes = mapper.writeValueAsBytes(obj);
            ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.create("application/json"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
            ExampleObj result = mapper.readValue(data, ExampleObj.class);
            Assert.assertEquals("xxxxx", result.getName());
        }
        {
            // POST ????
            String url = "http://127.0.0.1:12300/com.alipay.sofa.rpc.server.http.HttpService:uuu/object";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HEAD_SERIALIZE_TYPE, "json");
            ExampleObj obj = new ExampleObj();
            obj.setId(1);
            obj.setName("xxx");
            byte[] bytes = mapper.writeValueAsBytes(obj);
            ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.create("application/json"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
            ExampleObj result = mapper.readValue(data, ExampleObj.class);
            Assert.assertEquals("xxxxx", result.getName());
        }
    }
}

