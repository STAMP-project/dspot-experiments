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


import Tag.SKIP_BODY;
import javax.servlet.jsp.JspException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Jeremy Grelle
 */
public class PasswordInputTagTests extends InputTagTests {
    /**
     * https://jira.spring.io/browse/SPR-2866
     */
    @Test
    public void passwordValueIsNotRenderedByDefault() throws Exception {
        this.getTag().setPath("name");
        Assert.assertEquals(SKIP_BODY, this.getTag().doStartTag());
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "type", getType());
        assertValueAttribute(output, "");
    }

    /**
     * https://jira.spring.io/browse/SPR-2866
     */
    @Test
    public void passwordValueIsRenderedIfShowPasswordAttributeIsSetToTrue() throws Exception {
        this.getTag().setPath("name");
        this.getPasswordTag().setShowPassword(true);
        Assert.assertEquals(SKIP_BODY, this.getTag().doStartTag());
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "type", getType());
        assertValueAttribute(output, "Rob");
    }

    /**
     * https://jira.spring.io/browse/SPR-2866
     */
    @Test
    public void passwordValueIsNotRenderedIfShowPasswordAttributeIsSetToFalse() throws Exception {
        this.getTag().setPath("name");
        this.getPasswordTag().setShowPassword(false);
        Assert.assertEquals(SKIP_BODY, this.getTag().doStartTag());
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "type", getType());
        assertValueAttribute(output, "");
    }

    @Test
    @Override
    public void dynamicTypeAttribute() throws JspException {
        try {
            this.getTag().setDynamicAttribute(null, "type", "email");
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Attribute type=\"email\" is not allowed", e.getMessage());
        }
    }
}

