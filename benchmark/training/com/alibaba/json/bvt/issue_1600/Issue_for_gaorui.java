package com.alibaba.json.bvt.issue_1600;


import Feature.SupportAutoType;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue_for_gaorui extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"@type\":\"java.util.HashMap\",\"COUPON\":[{\"@type\":\"com.alibaba.json.bvt.issue_1600.Issue_for_gaorui.PromotionTermDetail\",\"activityId\":\"1584034\",\"choose\":true,\"couponId\":1251068987,\"couponType\":\"limitp\",\"match\":true,\"realPrice\":{\"amount\":0.6,\"currency\":\"USD\"}}],\"grayTrade\":\"true\"}";
        JSON.parseObject(json, Object.class, SupportAutoType);
    }

    public static class PromotionTermDetail {
        /**
         * ??Id
         */
        private Long couponId;

        /**
         * ??Id
         */
        private String promotionId;

        /**
         * ????
         */
        private Issue_for_gaorui.Money realPrice;

        /**
         * ??Id
         */
        private String activityId;

        /**
         * ????
         */
        private String couponType;

        /**
         * ??????????
         */
        private boolean isMatch = false;

        /**
         * ????????
         */
        private boolean isChoose = false;

        /**
         * ?????????
         */
        private String reasonForLose;

        /**
         * ?????????
         */
        private String codeForLose;

        public Long getCouponId() {
            return couponId;
        }

        public void setCouponId(Long couponId) {
            this.couponId = couponId;
        }

        public String getPromotionId() {
            return promotionId;
        }

        public void setPromotionId(String promotionId) {
            this.promotionId = promotionId;
        }

        public Issue_for_gaorui.Money getRealPrice() {
            return realPrice;
        }

        public void setRealPrice(Issue_for_gaorui.Money realPrice) {
            this.realPrice = realPrice;
        }

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public String getCouponType() {
            return couponType;
        }

        public void setCouponType(String couponType) {
            this.couponType = couponType;
        }

        public boolean isMatch() {
            return isMatch;
        }

        public void setMatch(boolean match) {
            isMatch = match;
        }

        public boolean isChoose() {
            return isChoose;
        }

        public void setChoose(boolean choose) {
            isChoose = choose;
        }

        public String getReasonForLose() {
            return reasonForLose;
        }

        public void setReasonForLose(String reasonForLose) {
            this.reasonForLose = reasonForLose;
        }

        public String getCodeForLose() {
            return codeForLose;
        }

        public void setCodeForLose(String codeForLose) {
            this.codeForLose = codeForLose;
        }
    }

    public static class Money {}
}

