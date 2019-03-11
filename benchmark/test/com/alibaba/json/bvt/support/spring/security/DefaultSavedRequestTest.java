package com.alibaba.json.bvt.support.spring.security;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedCookie;


/**
 * Created by wenshao on 11/08/2017.
 */
// 
public class DefaultSavedRequestTest extends TestCase {
    ParserConfig config;

    public void test_for_issue() throws Exception {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        DefaultSavedRequest request = new DefaultSavedRequest(mockReq, new PortResolver() {
            public int getServerPort(ServletRequest servletRequest) {
                return 0;
            }
        });
        String str = JSON.toJSONString(request, WriteClassName);
        // System.out.println(str);
        JSON.parseObject(str, Object.class, config);
        JSON.parseObject(str);
    }

    public void test_cookie() throws Exception {
        String json = "{\"name\":\"xx\",\"value\":\"xx\",\"comment\":\"xx\",\"domain\":\"xx\"}";
        SavedCookie cookie = JSON.parseObject(json, SavedCookie.class);
        TestCase.assertEquals("xx", cookie.getName());
        TestCase.assertEquals("{\"comment\":\"xx\",\"cookie\":{\"comment\":\"xx\",\"domain\":\"xx\",\"httpOnly\":false,\"maxAge\":0,\"name\":\"xx\",\"secure\":false,\"value\":\"xx\",\"version\":0},\"domain\":\"xx\",\"maxAge\":0,\"name\":\"xx\",\"secure\":false,\"value\":\"xx\",\"version\":0}", JSON.toJSONString(cookie));
    }

    public void test_0() throws Exception {
        DefaultCsrfToken token = JSON.parseObject("{\"token\":\"xxx\",\"parameterName\":\"222\",\"headerName\":\"hhh\"}", DefaultCsrfToken.class);
        TestCase.assertEquals("hhh", token.getHeaderName());
        TestCase.assertEquals("222", token.getParameterName());
        TestCase.assertEquals("xxx", token.getToken());
        TestCase.assertEquals("{\"headerName\":\"hhh\",\"parameterName\":\"222\",\"token\":\"xxx\"}", JSON.toJSONString(token));
    }

    public void test_http_cookie() throws Exception {
        Cookie cookie = new Cookie("cna", "h8a2EO57uEgCAXyg1TgBBFK");
        cookie.setMaxAge(10);
        String json = JSON.toJSONString(cookie);
        Cookie cookie1 = JSON.parseObject(json, Cookie.class);
        TestCase.assertEquals(cookie.getName(), cookie1.getName());
        TestCase.assertEquals(cookie.getValue(), cookie1.getValue());
        TestCase.assertEquals(cookie.getMaxAge(), cookie1.getMaxAge());
        // System.out.println(json);
    }

    public void test_PreAuthenticatedAuthenticationToken() throws Exception {
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken("ppp", "cccc");
        String json = JSON.toJSONString(token);
        System.out.println(json);
        PreAuthenticatedAuthenticationToken token1 = JSON.parseObject(json, PreAuthenticatedAuthenticationToken.class);
        TestCase.assertEquals("ppp", token1.getPrincipal());
        TestCase.assertEquals("cccc", token1.getCredentials());
    }

    public void test_WebAuthenticationDetails() throws Exception {
        WebAuthenticationDetails details = JSON.parseObject("{\"remoteAddress\":\"rrr\",\"sessionId\":\"ssss\"}", WebAuthenticationDetails.class);
        TestCase.assertEquals("rrr", details.getRemoteAddress());
        TestCase.assertEquals("ssss", details.getSessionId());
    }

    public void test_SecurityContextImpl() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.context.SecurityContextImpl\"}";
        JSON.parseObject(json, Object.class);
        JSON.parseObject(json, Object.class, config);
    }

    public void test_UsernamePasswordAuthenticationToken() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.authentication.UsernamePasswordAuthenticationToken\",\"principal\":\"pp\"}";
        UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) (JSON.parseObject(json, Object.class)));
        UsernamePasswordAuthenticationToken token1 = ((UsernamePasswordAuthenticationToken) (JSON.parseObject(json, Object.class, config)));
        TestCase.assertEquals("pp", token.getPrincipal());
        TestCase.assertEquals("pp", token1.getPrincipal());
    }

    public void test_SimpleGrantedAuthority() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"xx\"}";
        SimpleGrantedAuthority token = ((SimpleGrantedAuthority) (JSON.parseObject(json, Object.class)));
        SimpleGrantedAuthority token1 = ((SimpleGrantedAuthority) (JSON.parseObject(json, Object.class, config)));
        TestCase.assertEquals("xx", token.getAuthority());
        TestCase.assertEquals("xx", token1.getAuthority());
        TestCase.assertEquals("{\"authority\":\"xx\"}", JSON.toJSONString(token));
    }

    public void test_User() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.userdetails.User\",\"username\":\"xx\",\"authorities\":[]}";
        User token = ((User) (JSON.parseObject(json, Object.class)));
        User token1 = ((User) (JSON.parseObject(json, Object.class, config)));
        TestCase.assertEquals("xx", token.getUsername());
        TestCase.assertEquals("xx", token1.getUsername());
        TestCase.assertEquals("", token.getPassword());
        TestCase.assertEquals("", token1.getPassword());
    }

    public void test_SecurityContextImpl_x() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.context.SecurityContextImpl\",\"authentication\":{\"@type\":\"org.springframework.security.authentication.UsernamePasswordAuthenticationToken\",\"authenticated\":true,\"authorities\":[{\"@type\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ROLE_ADMIN\"}],\"details\":{\"@type\":\"org.springframework.security.web.authentication.WebAuthenticationDetails\",\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"sessionId\":\"35dbb2c4-971c-4624-bd89-2e002180a2ca\"},\"name\":\"admin\",\"principal\":{\"@type\":\"org.springframework.security.core.userdetails.User\",\"accountNonExpired\":true,\"accountNonLocked\":true,\"authorities\":[{\"$ref\":\"$.authentication.authorities[0]\"}],\"credentialsNonExpired\":true,\"enabled\":true,\"username\":\"admin\"}}}";
        SecurityContextImpl context = ((SecurityContextImpl) (JSON.parseObject(json, Object.class, config)));
    }
}

