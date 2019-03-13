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


import EmailValidator.EMAIL_ERROR_MESSAGE;
import Validator.EMAIL;
import com.thoughtworks.go.domain.materials.ValidationBean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EmailValidatorTest {
    @Test
    public void shouldCheckValidationForEmailAddress() throws Exception {
        Assert.assertThat(validate("some@here.com"), Matchers.is(ValidationBean.valid()));
    }

    @Test
    public void shouldReturnInvalidForInvalidEmailAddress() throws Exception {
        Assert.assertThat(validate("invalid").isValid(), Matchers.is(false));
    }

    @Test
    public void shouldExplainThatEmailAddressIsInvalid() throws Exception {
        Assert.assertThat(validate("invalid").getError(), Matchers.containsString(EMAIL_ERROR_MESSAGE));
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsInvalid() throws Exception {
        try {
            EMAIL.assertValid("dklaf;jds;l");
            Assert.fail("Expected to throw exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(EMAIL_ERROR_MESSAGE));
        }
    }
}

