package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_236 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{1:{\"donateLevel\":0,\"goodsInfoInRoomMap\":{102:2160,103:0},\"goodsInfoMap\":null,\"headPhoto\":null,\"headPhotoId\":0,\"id\":-569,\"nickName\":\"\u5564\u9152\u5151\u5496\u5561\u7684\u82e6\u6da9\",\"sex\":1,\"vipLevel\":0},2:{\"donateLevel\":0,\"goodsInfoInRoomMap\":{102:11000,103:0},\"goodsInfoMap\":null,\"headPhoto\":null,\"headPhotoId\":1,\"id\":18315,\"nickName\":\"\u6e38\u5ba26083\",\"sex\":1,\"vipLevel\":0},3:{\"donateLevel\":0,\"goodsInfoInRoomMap\":{102:1940,103:0},\"goodsInfoMap\":null,\"headPhoto\":null,\"headPhotoId\":0,\"id\":-887,\"nickName\":\"\u50bb\u7b11\uff0c\u90a3\u6bb5\u60c5\",\"sex\":0,\"vipLevel\":0},5:{\"$ref\":\"$[2]\"}}";
        Map<Integer, Object> root = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<Integer, Object>>() {});
        Assert.assertNotNull(root.get(5));
    }

    public static class TestPara {
        public Object[] paras;
    }
}

