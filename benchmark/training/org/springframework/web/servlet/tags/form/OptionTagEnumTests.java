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
package org.springframework.web.servlet.tags.form;


import BodyTag.EVAL_BODY_BUFFERED;
import CustomEnum.VALUE_1;
import SelectTag.LIST_VALUE_PAGE_ATTRIBUTE;
import Tag.EVAL_PAGE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.GenericBean;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class OptionTagEnumTests extends AbstractHtmlElementTagTests {
    private OptionTag tag;

    private SelectTag parentTag;

    @Test
    @SuppressWarnings("rawtypes")
    public void withJavaEnum() throws Exception {
        GenericBean testBean = new GenericBean();
        testBean.setCustomEnum(VALUE_1);
        getPageContext().getRequest().setAttribute("testBean", testBean);
        String selectName = "testBean.customEnum";
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, new org.springframework.web.servlet.support.BindStatus(getRequestContext(), selectName, false));
        this.tag.setValue("VALUE_1");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getWriter().toString();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "VALUE_1");
        assertContainsAttribute(output, "selected", "selected");
    }
}

