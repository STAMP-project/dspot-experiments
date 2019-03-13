/**
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config.validation;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class NameTypeValidatorTest {
    @Test
    public void shouldValidateNameBasedOnLength() {
        Assert.assertThat(new NameTypeValidator().isNameValid("name"), Matchers.is(true));
        Assert.assertThat(new NameTypeValidator().isNameValid(nameOfLength(255)), Matchers.is(true));
        Assert.assertThat(new NameTypeValidator().isNameValid(nameOfLength(256)), Matchers.is(false));
    }

    @Test
    public void shouldValidateNameBasedOnCharacterType() {
        // [a-zA-Z0-9_\-]{1}[a-zA-Z0-9_\-.]*
        Assert.assertThat(new NameTypeValidator().isNameValid(""), Matchers.is(false));
        Assert.assertThat(new NameTypeValidator().isNameValid("name"), Matchers.is(true));
        Assert.assertThat(new NameTypeValidator().isNameValid("!"), Matchers.is(false));
        Assert.assertThat(new NameTypeValidator().isNameValid("name!"), Matchers.is(false));
        Assert.assertThat(new NameTypeValidator().isNameValid("name_123"), Matchers.is(true));
        Assert.assertThat(new NameTypeValidator().isNameValid("1"), Matchers.is(true));
        Assert.assertThat(new NameTypeValidator().isNameValid("."), Matchers.is(false));
        Assert.assertThat(new NameTypeValidator().isNameValid("1."), Matchers.is(true));
    }
}

