/**
 * Copyright 2002-2017 the original author or authors.
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


import DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import Tag.EVAL_BODY_INCLUDE;
import Tag.EVAL_PAGE;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.format.number.PercentStyleFormatter;
import org.springframework.mock.web.test.MockPageContext;


/**
 *
 *
 * @author Keith Donald
 */
public class EvalTagTests extends AbstractTagTests {
    private EvalTag tag;

    private MockPageContext context;

    @Test
    public void printScopedAttributeResult() throws Exception {
        tag.setExpression("bean.method()");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("foo", getContentAsString());
    }

    @Test
    public void printNullAsEmptyString() throws Exception {
        tag.setExpression("bean.null");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("", getContentAsString());
    }

    @Test
    public void printFormattedScopedAttributeResult() throws Exception {
        PercentStyleFormatter formatter = new PercentStyleFormatter();
        tag.setExpression("bean.formattable");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals(formatter.print(new BigDecimal(".25"), Locale.getDefault()), getContentAsString());
    }

    @Test
    public void printHtmlEscapedAttributeResult() throws Exception {
        tag.setExpression("bean.html()");
        tag.setHtmlEscape(true);
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("&lt;p&gt;", getContentAsString());
    }

    @Test
    public void printJavaScriptEscapedAttributeResult() throws Exception {
        tag.setExpression("bean.js()");
        tag.setJavaScriptEscape(true);
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("function foo() { alert(\\\"hi\\\") }", getContentAsString());
    }

    @Test
    public void setFormattedScopedAttributeResult() throws Exception {
        tag.setExpression("bean.formattable");
        tag.setVar("foo");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals(new BigDecimal(".25"), context.getAttribute("foo"));
    }

    // SPR-6923
    @Test
    public void nestedPropertyWithAttributeName() throws Exception {
        tag.setExpression("bean.bean");
        tag.setVar("foo");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("not the bean object", context.getAttribute("foo"));
    }

    @Test
    public void accessUsingBeanSyntax() throws Exception {
        GenericApplicationContext wac = ((GenericApplicationContext) (context.getRequest().getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE)));
        wac.getDefaultListableBeanFactory().registerSingleton("bean2", context.getRequest().getAttribute("bean"));
        tag.setExpression("@bean2.bean");
        tag.setVar("foo");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("not the bean object", context.getAttribute("foo"));
    }

    @Test
    public void environmentAccess() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("key.foo", "value.foo");
        GenericApplicationContext wac = ((GenericApplicationContext) (context.getRequest().getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE)));
        wac.getEnvironment().getPropertySources().addFirst(new MapPropertySource("mapSource", map));
        wac.getDefaultListableBeanFactory().registerSingleton("bean2", context.getRequest().getAttribute("bean"));
        tag.setExpression("@environment['key.foo']");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("value.foo", getContentAsString());
    }

    @Test
    public void mapAccess() throws Exception {
        tag.setExpression("bean.map.key");
        int action = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, action);
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("value", getContentAsString());
    }

    public static class Bean {
        public String method() {
            return "foo";
        }

        @NumberFormat(style = Style.PERCENT)
        public BigDecimal getFormattable() {
            return new BigDecimal(".25");
        }

        public String html() {
            return "<p>";
        }

        public String getBean() {
            return "not the bean object";
        }

        public Object getNull() {
            return null;
        }

        public String js() {
            return "function foo() { alert(\"hi\") }";
        }

        public Map<String, Object> getMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("key", "value");
            return map;
        }
    }
}

