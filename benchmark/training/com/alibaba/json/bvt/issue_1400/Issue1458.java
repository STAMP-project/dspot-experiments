package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import junit.framework.TestCase;


public class Issue1458 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1458.HostPoint hostPoint = new Issue1458.HostPoint(new Issue1458.HostAddress("192.168.10.101"));
        hostPoint.setFingerprint(new Issue1458.Fingerprint("abc"));
        String json = JSON.toJSONString(hostPoint);
        Issue1458.HostPoint hostPoint1 = JSON.parseObject(json, Issue1458.HostPoint.class);
        String json1 = JSON.toJSONString(hostPoint1);
        TestCase.assertEquals(json, json1);
    }

    public static class HostPoint implements Serializable {
        private final Issue1458.HostAddress address;

        @JSONField(name = "fingerprint")
        private Issue1458.Fingerprint fingerprint;

        @JSONField(name = "unkown")
        private boolean unkown;

        // ------------------------------------------------------------------------
        @JSONCreator
        public HostPoint(@JSONField(name = "address")
        Issue1458.HostAddress addr) {
            this.address = addr;
        }

        public boolean isChanged() {
            return false;
        }

        public boolean isMatched() {
            return false;
        }

        public Issue1458.HostAddress getAddress() {
            return address;
        }

        public Issue1458.Fingerprint getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(Issue1458.Fingerprint fingerprint) {
            this.fingerprint = fingerprint;
        }

        public boolean isUnkown() {
            return unkown;
        }

        public void setUnkown(boolean unkown) {
            this.unkown = unkown;
        }
    }

    public static class Fingerprint implements Serializable {
        private final String source;

        private ImmutableMap<String, String> probes;

        @JSONCreator
        public Fingerprint(@JSONField(name = "source")
        String fingerprint) {
            this.source = fingerprint;
        }

        public String getSource() {
            return source;
        }
    }

    public static class HostAddress {
        public final String hostAddress;

        public HostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
        }
    }
}

