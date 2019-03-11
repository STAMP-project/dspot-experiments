package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_569 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_569.LoginResponse loginResp = new Bug_for_issue_569.LoginResponse();
        loginResp.response = new Bug_for_issue_569.Response<Bug_for_issue_569.LoginResponse.Body>();
        loginResp.response.content = new Bug_for_issue_569.LoginResponse.Body();
        loginResp.response.content.setMemberinfo(new Bug_for_issue_569.LoginResponse.MemberInfo());
        loginResp.response.content.getMemberinfo().name = "ding102992";
        loginResp.response.content.getMemberinfo().email = "ding102992@github.com";
        String text = JSON.toJSONString(loginResp);
        Bug_for_issue_569.LoginResponse loginResp2 = JSON.parseObject(text, Bug_for_issue_569.LoginResponse.class);
        // 
        Assert.assertEquals(// 
        // 
        loginResp.response.getContent().getMemberinfo().name, // 
        // 
        loginResp2.response.getContent().getMemberinfo().name);
        // 
        Assert.assertEquals(// 
        loginResp.response.getContent().getMemberinfo().email, loginResp2.response.getContent().getMemberinfo().email);
    }

    public static class BaseResponse<T> {
        public Bug_for_issue_569.Response<T> response;
    }

    public static class Response<T> {
        private T content;

        public T getContent() {
            return content;
        }

        public void setContent(T content) {
            this.content = content;
        }
    }

    public static class LoginResponse extends Bug_for_issue_569.BaseResponse<Bug_for_issue_569.LoginResponse.Body> {
        public static class Body {
            private Bug_for_issue_569.LoginResponse.MemberInfo memberinfo;

            public Bug_for_issue_569.LoginResponse.MemberInfo getMemberinfo() {
                return memberinfo;
            }

            public void setMemberinfo(Bug_for_issue_569.LoginResponse.MemberInfo memberinfo) {
                this.memberinfo = memberinfo;
            }
        }

        /* ??Getter,Setter */
        public static class MemberInfo {
            public String name;

            public String email;
        }
    }
}

