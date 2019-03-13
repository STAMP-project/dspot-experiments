/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
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
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.validation;


import PortValidator.ERROR_MESSAGE;
import Validator.PORT;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PortValidatorTest {
    @Test
    public void shouldCheckPortIsNumber() throws Exception {
        Assert.assertThat(PORT.validate("some@here.com").isValid(), Matchers.is(false));
        Assert.assertThat(PORT.validate("12345").isValid(), Matchers.is(true));
        Assert.assertThat(PORT.validate("foo").getError(), Matchers.is(ERROR_MESSAGE));
    }

    @Test
    public void shouldCheckPortIsPositiveNumber() throws Exception {
        Assert.assertThat(PORT.validate("-24").isValid(), Matchers.is(false));
        Assert.assertThat(PORT.validate("0").isValid(), Matchers.is(false));
    }
}

