/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config;


import com.thoughtworks.go.util.TestUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ParamsConfigTest {
    private ParamsConfig paramsConfig;

    private ValidationContext context = Mockito.mock(ValidationContext.class);

    @Test
    public void shouldPopulateParamFromMapIgnoringEmptyPairs() {
        paramsConfig = new ParamsConfig();
        List<Map> paramsMap = new ArrayList<>();
        paramsMap.add(createParamMap("param-name", "param-value"));
        paramsMap.add(createParamMap("", ""));
        paramsMap.add(createParamMap("", "bar"));
        paramsConfig.setConfigAttributes(paramsMap);
        Assert.assertThat(paramsConfig.size(), Matchers.is(2));
        Assert.assertThat(paramsConfig.getParamNamed("param-name").getValue(), Matchers.is("param-value"));
    }

    @Test
    public void getIndex() {
        paramsConfig = new ParamsConfig();
        ParamConfig one = new ParamConfig("name", "value");
        paramsConfig.add(one);
        ParamConfig two = new ParamConfig("other", "other-value");
        paramsConfig.add(two);
        Assert.assertThat(paramsConfig.getIndex("other"), Matchers.is(1));
        Assert.assertThat(paramsConfig.getIndex("name"), Matchers.is(0));
    }

    @Test
    public void getIndex_shouldThrowExceptionIfNameNotFound() {
        try {
            new ParamsConfig().getIndex(null);
            Assert.fail("should throw exception if param not found");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void shouldThrowAnErrorWhenDuplicateNameIsInserted() {
        paramsConfig = new ParamsConfig();
        ParamConfig one = new ParamConfig("name", "value");
        paramsConfig.add(one);
        ParamConfig two = new ParamConfig("name", "other-value");
        paramsConfig.add(two);
        paramsConfig.validate(context);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().firstError(), TestUtils.contains("Param name 'name' is not unique for pipeline 'some-pipeline'."));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(two.errors().firstError(), TestUtils.contains("Param name 'name' is not unique for pipeline 'some-pipeline'."));
    }

    @Test
    public void shouldThrowAnErrorWhenNameIsEmpty() {
        paramsConfig = new ParamsConfig();
        ParamConfig empty = new ParamConfig("", "value");
        paramsConfig.add(empty);
        paramsConfig.validate(context);
        Assert.assertThat(empty.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(empty.errors().firstError(), TestUtils.contains("Parameter cannot have an empty name for pipeline 'some-pipeline'."));
    }
}

