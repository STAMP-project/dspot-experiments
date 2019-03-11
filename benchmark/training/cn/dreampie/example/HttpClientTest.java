package cn.dreampie.example;


import cn.dreampie.client.HttpClient;
import cn.dreampie.client.HttpClientRequest;
import cn.dreampie.client.HttpClientResult;
import cn.dreampie.common.util.json.Jsoner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Created by ice on 15-1-4.
 */
@FixMethodOrder(MethodSorters.JVM)
public class HttpClientTest {
    private HttpClient httpClient;

    @Test
    public void testLogout() {
        HttpClientRequest request = new HttpClientRequest("/sessions");
        System.out.println(httpClient.build(request).delete());
    }

    @Test
    public void testGet() {
        HttpClientRequest request = new HttpClientRequest("/tests/??");
        request.setJsonParam("2013-03-23 00:00:00");
        // Jsoner.addDeserializer(User.class, ModelDeserializer.instance());
        // System.out.println(Jsoner.toObject(httpClient.build(request).get().getResult(),new TypeReference<List<User>>(){}));
        System.out.println(httpClient.build(request).get());
    }

    @Test
    public void testPost() {
        HttpClientRequest request = new HttpClientRequest("/tests/1");
        request.addParam("params", Jsoner.toJSON(new HashMap<String, String>() {
            {
                put("a", "??");
            }
        }));
        System.out.println(httpClient.build(request).post());
    }

    @Test
    public void testDelete() {
        HttpClientRequest request = new HttpClientRequest("/tests/1");
        System.out.println(httpClient.build(request).delete());
    }

    @Test
    public void testPut() {
        HttpClientRequest request = new HttpClientRequest("/tests/1");
        request.setJsonParam("{\"id\":\"1\",\"username\":\"\u54c8\u5e02\u5927\"}");
        System.out.println(httpClient.build(request).put());
    }

    // httpurlconnection patch
    // @Test
    // public void testPatch() {
    // HttpClientRequest request = new HttpClientRequest("/tests/1");
    // request.setJsonParam("{\"id\":\"1\",\"username\":\"k\"}");
    // System.out.println(httpClient.build(request).patch());
    // }
    @Test
    public void testUpload() throws FileNotFoundException {
        // upload
        HttpClientRequest uploadRequest = new HttpClientRequest("/tests/file");
        uploadRequest.addUploadFile("testfile", HttpClientTest.class.getResource("/resty.jar").getFile());
        uploadRequest.addParam("des", "test file  paras  ???");
        HttpClientResult uploadResult = httpClient.build(uploadRequest).post();
        System.out.println(uploadResult.getResult());
    }

    @Test
    public void testDownload() {
        // download  ??????
        HttpClientRequest downloadRequest = new HttpClientRequest("/tests/file");
        downloadRequest.setDownloadFile(HttpClientTest.class.getResource("/").getFile(), false);
        HttpClientResult downloadResult = httpClient.build(downloadRequest).get();
        System.out.println(downloadResult);
    }

    @Test
    public void testSave() {
        HttpClientRequest request = new HttpClientRequest("/users/1?x");
        String json = // new HashMap<String, Object>() {
        // {
        // put("users",
        Jsoner.toJSON(new ArrayList() {
            {
                add(new HashMap<String, String>() {
                    {
                        put("sid", "1");
                        put("username", "test1");
                        put("providername", "test1");
                        put("password", "123456");
                        put("created_at", "2014-10-11 10:09:12");
                    }
                });
                add(new HashMap<String, String>() {
                    {
                        put("sid", "2");
                        put("username", "test2");
                        put("providername", "tes2");
                        put("password", "123456");
                        put("created_at", "2014-10-12 10:09:12");
                    }
                });
            }
        });
        request.setJsonParam(json);
        System.out.println(httpClient.build(request).post());
    }
}

