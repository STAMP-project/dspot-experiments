package com.alibaba.json.bvt.parser.creator;


import JSON.VERSION;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;


public class JSONCreatorTest11 extends TestCase {
    public void test_for_yk() throws Exception {
        String jsonString = "[{\"image\":\"https://gw.alicdn.com/tfs/TB1Dtk.ay6guuRjy1XdXXaAwpXa-204-154.png\"," + ("\"labelNot\":\"zh*179753,zh*179745,zh*179743,zh*178230,zh*180695\",\"link\":\"https://alimarket.m.taobao" + ".com/markets/alisports/3_1weeklist\",\"title\":\"\u70ed\u5356\u699c\u5355\",\"desc\":\"\u5927\u5bb6\u90fd\u5728\u4e70\"}]");
        JSONArray array = JSON.parseArray(jsonString);
        List<JSONCreatorTest11.RecommendDTO> dtoList = array.toJavaList(JSONCreatorTest11.RecommendDTO.class);
        TestCase.assertEquals("????", dtoList.get(0).title);
        System.out.println(VERSION);
    }

    public static class RecommendDTO {
        private String image;

        private String link;

        private String title;

        private String desc;

        private String labels;

        private String labelNot;

        @JSONCreator
        public RecommendDTO(@JSONField(name = "image")
        String image, @JSONField(name = "link")
        String link, @JSONField(name = "title")
        String title, @JSONField(name = "desc")
        String desc, @JSONField(name = "labels")
        String labels, @JSONField(name = "labelNot")
        String labelNot) {
            final String PREFIX = "//";
            this.desc = desc;
            this.title = title;
            this.labelNot = labelNot;
            this.labels = labels;
            if (image.startsWith(PREFIX)) {
                this.image = image.substring(2);
            }
            if (link.startsWith(PREFIX)) {
                this.link = link.substring(2);
            }
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getLabels() {
            return labels;
        }

        public void setLabels(String labels) {
            this.labels = labels;
        }

        public String getLabelNot() {
            return labelNot;
        }

        public void setLabelNot(String labelNot) {
            this.labelNot = labelNot;
        }
    }
}

