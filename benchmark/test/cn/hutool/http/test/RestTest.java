package cn.hutool.http.test;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Rest????????
 *
 * @author looly
 */
public class RestTest {
    @Test
    public void contentTypeTest() {
        HttpRequest request = // 
        HttpRequest.post("http://localhost:8090/rest/restTest/").body(JSONUtil.createObj().put("aaa", "aaaValue").put("?2", "?2"));
        Assert.assertEquals("application/json;charset=UTF-8", request.header("Content-Type"));
    }
}

