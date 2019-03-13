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


import com.thoughtworks.go.domain.materials.ValidationBean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class LengthValidatorTest {
    @Test
    public void shouldReturnInvalidWhenLengthExceeds() {
        LengthValidator lengthValidator = new LengthValidator(2);
        ValidationBean validationBean = lengthValidator.validate("abc");
        Assert.assertThat(validationBean.isValid(), Matchers.is(false));
    }

    @Test
    public void shouldReturnValidWhenLengthDoesNotExceeds() {
        LengthValidator lengthValidator = new LengthValidator(2);
        ValidationBean validationBean = lengthValidator.validate("ab");
        Assert.assertThat(validationBean.isValid(), Matchers.is(true));
    }

    @Test
    public void shouldReturnValidWhenNoInput() {
        LengthValidator lengthValidator = new LengthValidator(2);
        ValidationBean validationBean = lengthValidator.validate(null);
        Assert.assertThat(validationBean.isValid(), Matchers.is(true));
    }

    @Test
    public void shouldReturnValidWhenEmptyInput() {
        LengthValidator lengthValidator = new LengthValidator(2);
        ValidationBean validationBean = lengthValidator.validate("");
        Assert.assertThat(validationBean.isValid(), Matchers.is(true));
    }
}

