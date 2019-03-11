package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by kimmking on 11/08/2017.
 */
public class Issue1400 extends TestCase {
    public void test_for_issue() throws Exception {
        TypeReference tr = new TypeReference<Issue1400.Resource<java.util.ArrayList<Issue1400.App>>>() {};
        Issue1400.Test test = new Issue1400.Test(tr);
        Issue1400.Resource resource = test.resource;
        Assert.assertEquals(1, resource.ret);
        Assert.assertEquals("ok", resource.message);
        List<Issue1400.App> data = ((List<Issue1400.App>) (resource.data));
        Assert.assertEquals(2, data.size());
        Issue1400.App app1 = data.get(0);
        Assert.assertEquals("11c53f541dee4f5bbc4f75f99002278c", app1.appId);
    }

    public static class Resource<T> {
        public int ret;

        public String message;

        public T data;
    }

    public static class App {
        public String appId;
    }

    public static class Test {
        String str = "{\"ret\":1,\"message\":\"ok\",\"data\":[{\"appId\":\"11c53f541dee4f5bbc4f75f99002278c\"},{\"appId\":\"c6102275ce5540a59424defa1cccb8ed\"}]}";

        public Issue1400.Resource resource;

        Test(TypeReference tr) {
            resource = ((Issue1400.Resource) (JSON.parseObject(str, tr)));
        }
    }
}

