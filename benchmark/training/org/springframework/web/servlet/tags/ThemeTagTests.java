/**
 * Copyright 2002-2015 the original author or authors.
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


import Tag.EVAL_PAGE;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.servlet.support.RequestContext;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Alef Arendsen
 */
public class ThemeTagTests extends AbstractTagTests {
    @Test
    @SuppressWarnings("serial")
    public void themeTag() throws JspException {
        PageContext pc = createPageContext();
        final StringBuffer message = new StringBuffer();
        ThemeTag tag = new ThemeTag() {
            @Override
            protected void writeMessage(String msg) {
                message.append(msg);
            }
        };
        tag.setPageContext(pc);
        tag.setCode("themetest");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        Assert.assertEquals("Correct doEndTag return value", EVAL_PAGE, tag.doEndTag());
        Assert.assertEquals("theme test message", message.toString());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void requestContext() throws ServletException {
        PageContext pc = createPageContext();
        RequestContext rc = new RequestContext(((HttpServletRequest) (pc.getRequest())));
        Assert.assertEquals("theme test message", rc.getThemeMessage("themetest"));
        Assert.assertEquals("theme test message", rc.getThemeMessage("themetest", ((String[]) (null))));
        Assert.assertEquals("theme test message", rc.getThemeMessage("themetest", "default"));
        Assert.assertEquals("theme test message", rc.getThemeMessage("themetest", ((Object[]) (null)), "default"));
        Assert.assertEquals("theme test message arg1", rc.getThemeMessage("themetestArgs", new String[]{ "arg1" }));
        Assert.assertEquals("theme test message arg1", rc.getThemeMessage("themetestArgs", Arrays.asList(new String[]{ "arg1" })));
        Assert.assertEquals("default", rc.getThemeMessage("themetesta", "default"));
        Assert.assertEquals("default", rc.getThemeMessage("themetesta", ((List) (null)), "default"));
        MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(new String[]{ "themetest" });
        Assert.assertEquals("theme test message", rc.getThemeMessage(resolvable));
    }
}

