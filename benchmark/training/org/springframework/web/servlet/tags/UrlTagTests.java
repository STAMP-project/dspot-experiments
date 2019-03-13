/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.tags;


import PageContext.PAGE_SCOPE;
import PageContext.REQUEST_SCOPE;
import Tag.EVAL_BODY_INCLUDE;
import Tag.EVAL_PAGE;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.jsp.JspException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockPageContext;


/**
 *
 *
 * @author Scott Andrews
 */
public class UrlTagTests extends AbstractTagTests {
    private UrlTag tag;

    private MockPageContext context;

    @Test
    public void paramSupport() {
        Assert.assertThat(tag, instanceOf(ParamAware.class));
    }

    @Test
    public void doStartTag() throws JspException {
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
    }

    @Test
    public void doEndTag() throws JspException {
        tag.setValue("url/path");
        tag.doStartTag();
        int action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
    }

    @Test
    public void varDefaultScope() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.doStartTag();
        tag.doEndTag();
        Assert.assertEquals("url/path", context.getAttribute("var", PAGE_SCOPE));
    }

    @Test
    public void varExplicitScope() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.setScope("request");
        tag.doStartTag();
        tag.doEndTag();
        Assert.assertEquals("url/path", context.getAttribute("var", REQUEST_SCOPE));
    }

    @Test
    public void setHtmlEscapeDefault() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.doStartTag();
        Param param = new Param();
        param.setName("n me");
        param.setValue("v&l=e");
        tag.addParam(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        tag.addParam(param);
        tag.doEndTag();
        Assert.assertEquals("url/path?n%20me=v%26l%3De&name=value2", context.getAttribute("var"));
    }

    @Test
    public void setHtmlEscapeFalse() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.setHtmlEscape(false);
        tag.doStartTag();
        Param param = new Param();
        param.setName("n me");
        param.setValue("v&l=e");
        tag.addParam(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        tag.addParam(param);
        tag.doEndTag();
        Assert.assertEquals("url/path?n%20me=v%26l%3De&name=value2", context.getAttribute("var"));
    }

    @Test
    public void setHtmlEscapeTrue() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.setHtmlEscape(true);
        tag.doStartTag();
        Param param = new Param();
        param.setName("n me");
        param.setValue("v&l=e");
        tag.addParam(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        tag.addParam(param);
        tag.doEndTag();
        Assert.assertEquals("url/path?n%20me=v%26l%3De&amp;name=value2", context.getAttribute("var"));
    }

    @Test
    public void setJavaScriptEscapeTrue() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.setJavaScriptEscape(true);
        tag.doStartTag();
        Param param = new Param();
        param.setName("n me");
        param.setValue("v&l=e");
        tag.addParam(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        tag.addParam(param);
        tag.doEndTag();
        Assert.assertEquals("url\\/path?n%20me=v%26l%3De&name=value2", context.getAttribute("var"));
    }

    @Test
    public void setHtmlAndJavaScriptEscapeTrue() throws JspException {
        tag.setValue("url/path");
        tag.setVar("var");
        tag.setHtmlEscape(true);
        tag.setJavaScriptEscape(true);
        tag.doStartTag();
        Param param = new Param();
        param.setName("n me");
        param.setValue("v&l=e");
        tag.addParam(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        tag.addParam(param);
        tag.doEndTag();
        Assert.assertEquals("url\\/path?n%20me=v%26l%3De&amp;name=value2", context.getAttribute("var"));
    }

    @Test
    public void createQueryStringNoParams() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("", queryString);
    }

    @Test
    public void createQueryStringOneParam() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("?name=value", queryString);
    }

    @Test
    public void createQueryStringOneParamForExsistingQueryString() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, false);
        Assert.assertEquals("&name=value", queryString);
    }

    @Test
    public void createQueryStringOneParamEmptyValue() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("?name=", queryString);
    }

    @Test
    public void createQueryStringOneParamNullValue() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue(null);
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("?name", queryString);
    }

    @Test
    public void createQueryStringOneParamAlreadyUsed() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        params.add(param);
        usedParams.add("name");
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("", queryString);
    }

    @Test
    public void createQueryStringTwoParams() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        params.add(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("?name=value&name=value2", queryString);
    }

    @Test
    public void createQueryStringUrlEncoding() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("n me");
        param.setValue("v&l=e");
        params.add(param);
        param = new Param();
        param.setName("name");
        param.setValue("value2");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("?n%20me=v%26l%3De&name=value2", queryString);
    }

    @Test
    public void createQueryStringParamNullName() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName(null);
        param.setValue("value");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("", queryString);
    }

    @Test
    public void createQueryStringParamEmptyName() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("");
        param.setValue("value");
        params.add(param);
        String queryString = tag.createQueryString(params, usedParams, true);
        Assert.assertEquals("", queryString);
    }

    @Test
    public void replaceUriTemplateParamsNoParams() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        String uri = tag.replaceUriTemplateParams("url/path", params, usedParams);
        Assert.assertEquals("url/path", uri);
        Assert.assertEquals(0, usedParams.size());
    }

    @Test
    public void replaceUriTemplateParamsTemplateWithoutParamMatch() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        String uri = tag.replaceUriTemplateParams("url/{path}", params, usedParams);
        Assert.assertEquals("url/{path}", uri);
        Assert.assertEquals(0, usedParams.size());
    }

    @Test
    public void replaceUriTemplateParamsTemplateWithParamMatch() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        params.add(param);
        String uri = tag.replaceUriTemplateParams("url/{name}", params, usedParams);
        Assert.assertEquals("url/value", uri);
        Assert.assertEquals(1, usedParams.size());
        Assert.assertTrue(usedParams.contains("name"));
    }

    @Test
    public void replaceUriTemplateParamsTemplateWithParamMatchNamePreEncoding() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("n me");
        param.setValue("value");
        params.add(param);
        String uri = tag.replaceUriTemplateParams("url/{n me}", params, usedParams);
        Assert.assertEquals("url/value", uri);
        Assert.assertEquals(1, usedParams.size());
        Assert.assertTrue(usedParams.contains("n me"));
    }

    @Test
    public void replaceUriTemplateParamsTemplateWithParamMatchValueEncoded() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("v lue");
        params.add(param);
        String uri = tag.replaceUriTemplateParams("url/{name}", params, usedParams);
        Assert.assertEquals("url/v%20lue", uri);
        Assert.assertEquals(1, usedParams.size());
        Assert.assertTrue(usedParams.contains("name"));
    }

    // SPR-11401
    @Test
    public void replaceUriTemplateParamsTemplateWithPathSegment() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("my/Id");
        params.add(param);
        String uri = tag.replaceUriTemplateParams("url/{/name}", params, usedParams);
        Assert.assertEquals("url/my%2FId", uri);
        Assert.assertEquals(1, usedParams.size());
        Assert.assertTrue(usedParams.contains("name"));
    }

    @Test
    public void replaceUriTemplateParamsTemplateWithPath() throws JspException {
        List<Param> params = new LinkedList<>();
        Set<String> usedParams = new HashSet<>();
        Param param = new Param();
        param.setName("name");
        param.setValue("my/Id");
        params.add(param);
        String uri = tag.replaceUriTemplateParams("url/{name}", params, usedParams);
        Assert.assertEquals("url/my/Id", uri);
        Assert.assertEquals(1, usedParams.size());
        Assert.assertTrue(usedParams.contains("name"));
    }

    @Test
    public void createUrlRemoteServer() throws JspException {
        tag.setValue("http://www.springframework.org/");
        tag.doStartTag();
        String uri = tag.createUrl();
        Assert.assertEquals("http://www.springframework.org/", uri);
    }

    @Test
    public void createUrlRelative() throws JspException {
        tag.setValue("url/path");
        tag.doStartTag();
        String uri = tag.createUrl();
        Assert.assertEquals("url/path", uri);
    }

    @Test
    public void createUrlLocalContext() throws JspException {
        setContextPath("/app-context");
        tag.setValue("/url/path");
        tag.doStartTag();
        String uri = tag.createUrl();
        Assert.assertEquals("/app-context/url/path", uri);
    }

    @Test
    public void createUrlRemoteContext() throws JspException {
        setContextPath("/app-context");
        tag.setValue("/url/path");
        tag.setContext("some-other-context");
        tag.doStartTag();
        String uri = tag.createUrl();
        Assert.assertEquals("/some-other-context/url/path", uri);
    }

    @Test
    public void createUrlRemoteContextWithSlash() throws JspException {
        setContextPath("/app-context");
        tag.setValue("/url/path");
        tag.setContext("/some-other-context");
        tag.doStartTag();
        String uri = tag.createUrl();
        Assert.assertEquals("/some-other-context/url/path", uri);
    }

    @Test
    public void createUrlRemoteContextSingleSlash() throws JspException {
        setContextPath("/app-context");
        tag.setValue("/url/path");
        tag.setContext("/");
        tag.doStartTag();
        String uri = tag.createUrl();
        Assert.assertEquals("/url/path", uri);
    }

    @Test
    public void createUrlWithParams() throws JspException {
        tag.setValue("url/path");
        tag.doStartTag();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        tag.addParam(param);
        param = new Param();
        param.setName("n me");
        param.setValue("v lue");
        tag.addParam(param);
        String uri = tag.createUrl();
        Assert.assertEquals("url/path?name=value&n%20me=v%20lue", uri);
    }

    @Test
    public void createUrlWithTemplateParams() throws JspException {
        tag.setValue("url/{name}");
        tag.doStartTag();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        tag.addParam(param);
        param = new Param();
        param.setName("n me");
        param.setValue("v lue");
        tag.addParam(param);
        String uri = tag.createUrl();
        Assert.assertEquals("url/value?n%20me=v%20lue", uri);
    }

    @Test
    public void createUrlWithParamAndExistingQueryString() throws JspException {
        tag.setValue("url/path?foo=bar");
        tag.doStartTag();
        Param param = new Param();
        param.setName("name");
        param.setValue("value");
        tag.addParam(param);
        String uri = tag.createUrl();
        Assert.assertEquals("url/path?foo=bar&name=value", uri);
    }
}

