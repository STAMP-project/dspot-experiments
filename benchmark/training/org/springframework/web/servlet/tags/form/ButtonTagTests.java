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


import Tag.EVAL_BODY_INCLUDE;
import Tag.EVAL_PAGE;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rossen Stoyanchev
 */
public class ButtonTagTests extends AbstractFormTagTests {
    private ButtonTag tag;

    @Test
    public void buttonTag() throws Exception {
        Assert.assertEquals(EVAL_BODY_INCLUDE, this.tag.doStartTag());
        Assert.assertEquals(EVAL_PAGE, this.tag.doEndTag());
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "id", "My Id");
        assertContainsAttribute(output, "name", "My Name");
        assertContainsAttribute(output, "type", "submit");
        assertContainsAttribute(output, "value", "My Button");
        assertAttributeNotPresent(output, "disabled");
    }

    @Test
    public void disabled() throws Exception {
        this.tag.setDisabled(true);
        this.tag.doStartTag();
        this.tag.doEndTag();
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "disabled", "disabled");
    }
}

