/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.http.rest.json.ValidationErrors;


public class ValidatorTest {
    @Test
    public void test_validateNotNullOrEmpty_valid() throws ValidationException {
        Validator.validateNotNullOrEmpty("name", "the name");
    }

    @Test
    public void test_validateNotNullOrEmpty_empty_invalid() throws ValidationException {
        try {
            Validator.validateNotNullOrEmpty("name", "");
            Assert.fail("Expected ValidationException");
        } catch (ValidationException e) {
            Assert.assertThat(e.getMessage(), IsEqual.equalTo("name may not be empty."));
        }
    }

    @Test
    public void test_validateNotNullOrEmpty_null_invalid() throws ValidationException {
        try {
            Validator.validateNotNullOrEmpty("name", null);
            Assert.fail("Expected ValidationException");
        } catch (ValidationException e) {
            Assert.assertThat(e.getMessage(), IsEqual.equalTo("name may not be null."));
        }
    }

    @Test
    public void test_validateMin_valid() throws ValidationException {
        Validator.validateMin("name", 2, 1);
    }

    @Test
    public void test_validateMin_invalid() throws ValidationException {
        try {
            Validator.validateMin("name", 10, 11);
            Assert.fail("Expected ValidationException");
        } catch (ValidationException e) {
            Assert.assertThat(e.getMessage(), IsEqual.equalTo("name must be greater than or equal to 11."));
        }
    }

    @Test
    public void test_isNotNullOrEmpty_string_valid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "name", "the name"), IsEqual.equalTo(true));
    }

    @Test
    public void test_isNotNullOrEmpty_string_invalid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "name", ((String) (null))), IsEqual.equalTo(false));
        Assert.assertThat(errors.getErrors(), CoreMatchers.hasItem("name may not be null."));
    }

    @Test
    public void test_isNotNullOrEmpty_JsonElement_null_value_invalid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "value", ((JsonElement) (null))), IsEqual.equalTo(false));
        Assert.assertThat(errors.getErrors(), CoreMatchers.hasItem("value may not be null."));
    }

    @Test
    public void test_isNotNullOrEmpty_JsonElement_isJsonNull_value_invalid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "value", JsonNull.INSTANCE), IsEqual.equalTo(false));
        Assert.assertThat(errors.getErrors(), CoreMatchers.hasItem("value may not be empty."));
    }

    @Test
    public void test_isNotNullOrEmpty_JsonPrimitive_empty_value_invalid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "value", new JsonPrimitive("")), IsEqual.equalTo(false));
        Assert.assertThat(errors.getErrors(), CoreMatchers.hasItem("value may not be empty."));
    }

    @Test
    public void test_isNotNullOrEmpty_JsonArray_empty_value_invalid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "value", new JsonArray()), IsEqual.equalTo(false));
        Assert.assertThat(errors.getErrors(), CoreMatchers.hasItem("value may not be an empty array."));
    }

    @Test
    public void test_isNotNullOrEmpty_JsonObject_empty_value_valid() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isNotNullOrEmpty(errors, "value", new JsonObject()), IsEqual.equalTo(true));
        Assert.assertThat(errors.getErrors().size(), IsEqual.equalTo(0));
    }

    @Test
    public void test_isGreaterThanOrEqualTo_valid() throws ValidationException {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isGreaterThanOrEqualTo(errors, "name", 2, 1), IsEqual.equalTo(true));
    }

    @Test
    public void test_isGreaterThanOrEqualTo_invalid() throws ValidationException {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertThat(Validator.isGreaterThanOrEqualTo(errors, "name", 10, 11), IsEqual.equalTo(false));
        Assert.assertThat(errors.getErrors(), CoreMatchers.hasItem("name must be greater than or equal to 11."));
    }
}

