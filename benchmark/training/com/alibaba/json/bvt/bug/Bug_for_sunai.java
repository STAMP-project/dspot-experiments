package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_sunai extends TestCase {
    public void test_for_sunai() throws Exception {
        String text = "{\"description\":\"\u3010\\r\\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\uff01xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxr\\nid:10000000\",\"detail\":\"\u3010xxxx\u3011\\r\\nxxxx\uff1a2019xxxxx\u3001xx\u3001xxxxxxxx\uff1b\u9a7e\u6821\u3001\u6559\u7ec3\u6781\u529b\u63a8\u8350\u4e0b\u8f7d\uff01\\r\\n\u5168\u56fd92%\u7684xxxxxx\uff01\u7d2f\u8ba1\u5e2e\u52a91\u4ebf\u7528\u6237\u8003\u53d6\u9a7e\u7167\uff0c\u662f\u4e00\u6b3e\u53e3\u53e3\u76f8\u4f20\u7684\u98de\u673aGPP\uff01 \\r\\n\u3010\u4ea7\u54c1\u7b80\u4ecb\u3011\\r\\nSNSNAPP\u67092099\u5e74\u6700\u65b0\u7684\u201c\u79d1\u76ee\u4e00\u3001\u79d1\u76ee\u56db\u201d\u7406\u8bba\u8003\u8bd5\u9898\u5e93\uff0c\u7279\u522b\u65b9\u4fbf\u5b66\u5458\u505a\u9898\uff0c\u5e76\u80fd\u5feb\u901f\u63d0\u9ad8\u6210\u7ee9\uff1b\u6b64\u5916\u8fd8\u6709\u79d1\u76ee\u5c0f\u4e09\u8def\u8003\u548c\u79d1\u76ee\u4e09\u5927\u8def\u8003\u79d8\u7b08\uff0c\u72ec\u5bb6\u5185\u90e8\u5236\u4f5c\u7684\u5b66\u8f66\u89c6\u9891\uff0c\u4e0d\u53d7\u5b66\u5458\u6b22\u8fce\uff1b\u5fae\u793e\u533a\u4e0d\u8ba9\u8f66\u53cb\u5410\u5410\u69fd\u3001\u6652\u6652\u7167\u3001\u4ea4\u6d41\u5b66\u8f66\u6280\u5de7\u548c\u5fc3\u5f97\uff0c\u8ba9\u5927\u5bb6\u611f\u89c9\u5728\u5b66\u8f66\u9014\u4e2d\u4e0d\u5bc2\u5bde\uff01 \\r\\n\u8054\u7cfb\u6211\u4eec\u3011\\r\\n\u9493\u9c7c\u7f51\u7ad9\uff1ahttp://ddd.sunyu.com\\r\\n\u6e20\u9053\u5408\u4f5c: sunai@369.com\\r\\n\u5fae\u4fe1\u516c\u4f17\u53f7\uff1aSNSN\\r\\nid:99999999\",\"logo\":\"\",\"name\":\"\",\"pics\":[\"http://99999.meimaocdn.com/snscom/GD99999HVXXXXXGXVXXXXXXXXXX?xxxxx=GD99999HVXXXXXGXVXXXXXXXXXX\",\"http://99999.meimaocdn.com/snscom/TB1TcILJpXXXXbIXpXXXXXXXXXX?xxxxx=TB1TcILJpXXXXbIXpXXXXXXXXXX\",\"http://99999.meimaocdn.com/snscom/GD2M5.OJpXXXXaOXpXXXXXXXXXX?xxxxx=GD2M5.OJpXXXXaOXpXXXXXXXXXX\",\"http://99999.meimaocdn.com/snscom/TB1QWElIpXXXXXvXpXXXXXXXXXX?xxxxx=TB1QWElIpXXXXXvXpXXXXXXXXXX\",\"http://99999.meimaocdn.com/snscom/TB1wZUQJpXXXXajXpXXXXXXXXXX?xxxxx=TB1wZUQJpXXXXajXpXXXXXXXXXX\"]}";
        Bug_for_sunai.MultiLingual ml = JSON.parseObject(text, Bug_for_sunai.MultiLingual.class);
        String text2 = JSON.toJSONString(ml);
        System.out.println(text2);
        Assert.assertEquals(text, text2);
    }

    public static class MultiLingual {
        /**
         * ??
         */
        private String lang;

        /**
         * ????
         */
        private String name;

        /**
         * ????
         */
        private String catName;

        /**
         * ?????
         */
        private String cardLogo;

        /**
         * ????
         */
        private String logo;

        /**
         * ????
         */
        private List<String> pics;

        /**
         * ????
         */
        private String detail;

        /**
         * APP/VERSION ??
         */
        private String description;

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCatName() {
            return catName;
        }

        public void setCatName(String catName) {
            this.catName = catName;
        }

        public String getCardLogo() {
            return cardLogo;
        }

        public void setCardLogo(String cardLogo) {
            this.cardLogo = cardLogo;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public List<String> getPics() {
            return pics;
        }

        public void setPics(List<String> pics) {
            this.pics = pics;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

