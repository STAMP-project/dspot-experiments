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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Unit tests for {@link ArgumentTag}
 *
 * @author Nicholas Williams
 */
public class ArgumentTagTests extends AbstractTagTests {
    private ArgumentTag tag;

    private ArgumentTagTests.MockArgumentSupportTag parent;

    @Test
    public void argumentWithStringValue() throws JspException {
        tag.setValue("value1");
        int action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("value1", parent.getArgument());
    }

    @Test
    public void argumentWithImplicitNullValue() throws JspException {
        int action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertNull(parent.getArgument());
    }

    @Test
    public void argumentWithExplicitNullValue() throws JspException {
        tag.setValue(null);
        int action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertNull(parent.getArgument());
    }

    @Test
    public void argumentWithBodyValue() throws JspException {
        tag.setBodyContent(new org.springframework.mock.web.test.MockBodyContent("value2", new MockHttpServletResponse()));
        int action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("value2", parent.getArgument());
    }

    @Test
    public void argumentWithValueThenReleaseThenBodyValue() throws JspException {
        tag.setValue("value3");
        int action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("value3", parent.getArgument());
        tag.release();
        parent = new ArgumentTagTests.MockArgumentSupportTag();
        tag.setPageContext(createPageContext());
        tag.setParent(parent);
        tag.setBodyContent(new org.springframework.mock.web.test.MockBodyContent("value4", new MockHttpServletResponse()));
        action = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, action);
        Assert.assertEquals("value4", parent.getArgument());
    }

    @SuppressWarnings("serial")
    private class MockArgumentSupportTag extends TagSupport implements ArgumentAware {
        Object argument;

        @Override
        public void addArgument(Object argument) {
            this.argument = argument;
        }

        private Object getArgument() {
            return argument;
        }
    }
}

