package com.alibaba.json.bvt.parser.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_lingzhi extends TestCase {
    public void test_0() throws Exception {
        String str = "[\n" + (((((((((((((((((((((((((((((((((((("{\n" + "\"isDefault\":false,\n") + "\"msgId\": \"expireTransitionChange\",\n") + "\"msgText\": \"xxx\",\n") + "\"extMsgId\": \"promptInformation\",\n") + "\"extMsgText\": \"xxx\",\n") + "\"instChangeType\": 1,\n") + "\"rule\": {\n") + "\"aliUid\":[39314],\n") + "\"regionNo\":[]\n") + "}\n") + "},\n") + "{\n") + "\"isDefault\":true,\n") + "\"msgId\": \"expireTransitionUnChange\",\n") + "\"msgText\": \"xxx\",\n") + "\"extMsgId\": \"Prompt information\",\n") + "\"extMsgText\": \"xxx\",\n") + "\"instChangeType\": 0,\n") + "\"rule\": {\n") + "\"aliUid\":[],\n") + "\"regionNo\":[]\n") + "}\n") + "},\n") + "{\n") + "\"isDefault\":false,\n") + "\"msgId\": \"expireTransitionChange\",\n") + "\"msgText\": \"xxx\",\n") + "\"extMsgId\": \"Prompt information\",\n") + "\"extMsgText\": \"\u4f60\u597dB\",\n") + "\"instChangeType\": 1,\n") + "\"rule\": {\n") + "\"aliUid\":[111],\n") + "\"regionNo\":[]\n") + "}\n") + "}\n") + "]");
        // String pstr = JSON.toJSONString(JSON.parse(str), SerializerFeature.PrettyFormat);
        // System.out.println(pstr);
        JSON.parseObject(str, new com.alibaba.fastjson.TypeReference<java.util.List<Bug_for_lingzhi.EcsTransitionDisplayedMsgConfig>>() {});
    }

    public static class EcsTransitionDisplayedMsgConfig {
        /**
         * ??????
         */
        private Boolean isDefault;

        /**
         * ?????Id
         */
        private String msgId;

        /**
         * ???????
         */
        private String msgText;

        /**
         * ????Id
         */
        private String extMsgId;

        /**
         * ??????
         */
        private String extMsgText;

        private Integer instChangeType;

        /**
         * ???????
         */
        private Bug_for_lingzhi.EcsTransitionConfigRule rule;

        public String getMsgText() {
            return msgText;
        }

        public void setMsgText(String msgText) {
            this.msgText = msgText;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public Bug_for_lingzhi.EcsTransitionConfigRule getRule() {
            return rule;
        }

        public void setRule(Bug_for_lingzhi.EcsTransitionConfigRule rule) {
            this.rule = rule;
        }

        public Integer getInstChangeType() {
            return instChangeType;
        }

        public void setInstChangeType(Integer instChangeType) {
            this.instChangeType = instChangeType;
        }

        public Boolean getIsDefault() {
            return this.isDefault;
        }

        public void setIsDefault(Boolean isDefault) {
            this.isDefault = isDefault;
        }

        public String getExtMsgId() {
            return extMsgId;
        }

        public void setExtMsgId(String extMsgId) {
            this.extMsgId = extMsgId;
        }

        public String getExtMsgText() {
            return extMsgText;
        }

        public void setExtMsgText(String extMsgText) {
            this.extMsgText = extMsgText;
        }
    }

    public static class EcsTransitionConfigRule {
        /**
         * 0 ????, 1 ????? *
         */
        private java.util.List<Integer> transType;

        /**
         * ???cn-qingdao-cm5-a01 *
         */
        private java.util.List<String> regionNo;

        private java.util.List<Long> aliUid;

        private java.util.List<String> bid;

        /**
         * ecs,disk *
         */
        private java.util.List<String> resourceType;

        private java.util.List<Long> zoneId;

        private java.util.List<Long> targetZoneId;

        private java.util.List<Integer> networkTransType;

        /**
         * instance type ???? *
         */
        private java.util.List<String> instanceType;

        /**
         * ???? ioX *
         */
        private java.util.List<String> ioX;

        private java.util.List<String> instanceId;

        public java.util.List<Integer> getTransType() {
            return transType;
        }

        public void setTransType(java.util.List<Integer> transType) {
            this.transType = transType;
        }

        public java.util.List<String> getRegionNo() {
            return regionNo;
        }

        public void setRegionNo(java.util.List<String> regionNo) {
            this.regionNo = regionNo;
        }

        public java.util.List<Long> getAliUid() {
            return aliUid;
        }

        public void setAliUid(java.util.List<Long> aliUid) {
            this.aliUid = aliUid;
        }

        public java.util.List<String> getBid() {
            return bid;
        }

        public void setBid(java.util.List<String> bid) {
            this.bid = bid;
        }

        public java.util.List<String> getResourceType() {
            return resourceType;
        }

        public void setResourceType(java.util.List<String> resourceType) {
            this.resourceType = resourceType;
        }

        public java.util.List<Long> getZoneId() {
            return zoneId;
        }

        public void setZoneId(java.util.List<Long> zoneId) {
            this.zoneId = zoneId;
        }

        public java.util.List<Long> getTargetZoneId() {
            return targetZoneId;
        }

        public void setTargetZoneId(java.util.List<Long> targetZoneId) {
            this.targetZoneId = targetZoneId;
        }

        public java.util.List<Integer> getNetworkTransType() {
            return networkTransType;
        }

        public void setNetworkTransType(java.util.List<Integer> networkTransType) {
            this.networkTransType = networkTransType;
        }

        public java.util.List<String> getInstanceType() {
            return instanceType;
        }

        public void setInstanceType(java.util.List<String> instanceType) {
            this.instanceType = instanceType;
        }

        public java.util.List<String> getIoX() {
            return ioX;
        }

        public void setIoX(java.util.List<String> ioX) {
            this.ioX = ioX;
        }

        public java.util.List<String> getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(java.util.List<String> instanceId) {
            this.instanceId = instanceId;
        }
    }
}

