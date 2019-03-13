/**
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain.materials;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ValidationBeanTest {
    @Test
    public void shouldBeInvalidIfMessageIsEmptyOrNull() {
        Assert.assertThat(ValidationBean.notValid("").isValid(), Matchers.is(false));
        Assert.assertThat(ValidationBean.notValid(((String) (null))).isValid(), Matchers.is(false));
        Assert.assertThat(ValidationBean.notValid(new Exception()).isValid(), Matchers.is(false));
    }

    @Test
    public void shouldBeInvalidIfMessageIsNotEmpty() {
        Assert.assertThat(ValidationBean.notValid("With a message").isValid(), Matchers.is(false));
    }

    @Test
    public void shouldHaveBasicErrorIfMessageIsEmpty() {
        Assert.assertThat(ValidationBean.notValid("").getError(), Matchers.is(""));
        Assert.assertThat(ValidationBean.notValid(((String) (null))).isValid(), Matchers.is(false));
        Assert.assertThat(ValidationBean.notValid(new Exception()).isValid(), Matchers.is(false));
    }

    /**
     * This is a straight test of what is already there.
     * We are NOT sure that this is the correct behaviour.
     * We think it was there for SVNKIT and can now be removed.
     */
    @Test
    public void shouldStripOutExceptionText() {
        Assert.assertThat(ValidationBean.notValid(new Exception("SVNKITException:   The actual message")).getError(), Matchers.is("The actual message"));
    }

    @Test
    public void shouldSeeOriginalExceptionMessage() {
        String message = "A message.";
        Assert.assertThat(ValidationBean.notValid(new Exception(message)).getError(), Matchers.is(message));
        Assert.assertThat(ValidationBean.notValid(message).getError(), Matchers.is(message));
    }

    @Test
    public void shouldBeValid() {
        Assert.assertThat(ValidationBean.valid().isValid(), Matchers.is(true));
        Assert.assertThat(ValidationBean.valid().getError(), Matchers.is(""));
        ValidationBean bean = ValidationBean.valid();
        Assert.assertThat(bean.isValid(), Matchers.is(true));
        Assert.assertThat(bean.toJson().get("isValid"), Matchers.is("true"));
    }

    @Test
    public void shouldBeAbleToSerializeToJson() {
        ValidationBean bean = ValidationBean.notValid("ErrorMessage");
        String output = render(bean);
        assertThatJson(output).isEqualTo("{ \"isValid\": \"false\",\"error\": \"ErrorMessage\" }");
    }
}

