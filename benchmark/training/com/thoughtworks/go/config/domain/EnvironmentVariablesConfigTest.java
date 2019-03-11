/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.domain;


import EnvironmentVariableConfig.ISCHANGED;
import EnvironmentVariableConfig.NAME;
import EnvironmentVariableConfig.SECURE;
import EnvironmentVariableConfig.VALUE;
import com.thoughtworks.go.config.EnvironmentVariablesConfig;
import com.thoughtworks.go.config.ValidationContext;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EnvironmentVariablesConfigTest {
    private EnvironmentVariablesConfig environmentVariablesConfig;

    private ValidationContext context = Mockito.mock(ValidationContext.class);

    private GoCipher goCipher = Mockito.mock(GoCipher.class);

    @Test
    public void shouldPopulateErrorWhenDuplicateEnvironmentVariableNameIsPresent() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig("FOO", "BAR");
        EnvironmentVariableConfig two = new EnvironmentVariableConfig("FOO", "bAZ");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.add(two);
        environmentVariablesConfig.validate(context);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().firstError(), TestUtils.contains("Environment Variable name 'FOO' is not unique for pipeline 'some-pipeline'"));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(two.errors().firstError(), TestUtils.contains("Environment Variable name 'FOO' is not unique for pipeline 'some-pipeline'"));
    }

    @Test
    public void shouldValidateTree() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig("FOO", "BAR");
        EnvironmentVariableConfig two = new EnvironmentVariableConfig("FOO", "bAZ");
        EnvironmentVariableConfig three = new EnvironmentVariableConfig("", "bAZ");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.add(two);
        environmentVariablesConfig.add(three);
        environmentVariablesConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(new CaseInsensitiveString("p1"), null)));
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().firstError(), TestUtils.contains("Environment Variable name 'FOO' is not unique for pipeline 'p1'"));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(two.errors().firstError(), TestUtils.contains("Environment Variable name 'FOO' is not unique for pipeline 'p1'"));
        Assert.assertThat(three.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(three.errors().firstError(), TestUtils.contains("Environment Variable cannot have an empty name for pipeline 'p1'."));
    }

    @Test
    public void shouldPopulateErrorWhenVariableNameIsEmpty() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig("", "BAR");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.validate(context);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().on(NAME), TestUtils.contains("Environment Variable cannot have an empty name for pipeline 'some-pipeline'."));
    }

    @Test
    public void shouldPopulateErrorWhenVariableNameStartsWithSpace() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig(" foo", "BAR");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.validate(context);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().on(NAME), TestUtils.contains("Environment Variable cannot start or end with spaces for pipeline 'some-pipeline'."));
    }

    @Test
    public void shouldPopulateErrorWhenVariableNameEndsWithSpace() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig("FOO ", "BAR");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.validate(context);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().on(NAME), TestUtils.contains("Environment Variable cannot start or end with spaces for pipeline 'some-pipeline'."));
    }

    @Test
    public void shouldPopulateErrorWhenVariableNameContainsLeadingAndTrailingSpaces() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig("     FOO   ", "BAR");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.validate(context);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().on(NAME), TestUtils.contains("Environment Variable cannot start or end with spaces for pipeline 'some-pipeline'."));
    }

    @Test
    public void shouldClearEnvironmentVariablesWhenTheMapIsNull() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig one = new EnvironmentVariableConfig("FOO", "BAR");
        environmentVariablesConfig.add(one);
        environmentVariablesConfig.setConfigAttributes(null);
        Assert.assertThat(environmentVariablesConfig.size(), Matchers.is(0));
    }

    @Test
    public void shouldSetConfigAttributesSecurely() {
        environmentVariablesConfig = new EnvironmentVariablesConfig();
        ArrayList<Map<String, String>> attribs = new ArrayList<>();
        Map<String, String> var1 = new HashMap<>();
        var1.put(NAME, "name-var1");
        var1.put(VALUE, "val-var1");
        attribs.add(var1);
        Map<String, String> var2 = new HashMap<>();
        var2.put(NAME, "name-var2");
        var2.put(VALUE, "val-var2");
        var2.put(SECURE, "true");
        var2.put(ISCHANGED, "true");
        attribs.add(var2);
        Assert.assertThat(environmentVariablesConfig.size(), Matchers.is(0));
        environmentVariablesConfig.setConfigAttributes(attribs);
        Assert.assertThat(environmentVariablesConfig.size(), Matchers.is(2));
        Assert.assertThat(environmentVariablesConfig, Matchers.hasItem(new EnvironmentVariableConfig(null, "name-var1", "val-var1", false)));
        Assert.assertThat(environmentVariablesConfig, Matchers.hasItem(new EnvironmentVariableConfig(new GoCipher(), "name-var2", "val-var2", true)));
    }

    @Test
    public void shouldGetSecureVariables() throws CryptoException {
        EnvironmentVariablesConfig environmentVariablesConfig = new EnvironmentVariablesConfig();
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        EnvironmentVariableConfig plainVar1 = new EnvironmentVariableConfig("var1", "var1_value");
        EnvironmentVariableConfig var1 = secureVariable(mockGoCipher, "foo1", "bar1", "encryptedBar1");
        EnvironmentVariableConfig var2 = secureVariable(mockGoCipher, "foo2", "bar2", "encryptedBar2");
        EnvironmentVariableConfig var3 = secureVariable(mockGoCipher, "foo3", "bar3", "encryptedBar3");
        environmentVariablesConfig.addAll(Arrays.asList(var1, var2, var3, plainVar1));
        List<EnvironmentVariableConfig> variables = environmentVariablesConfig.getSecureVariables();
        Assert.assertThat(variables.size(), Matchers.is(3));
        Assert.assertThat(variables, Matchers.hasItems(var1, var2, var3));
    }

    @Test
    public void shouldGetOnlyPlainTextVariables() throws CryptoException {
        EnvironmentVariablesConfig environmentVariablesConfig = new EnvironmentVariablesConfig();
        EnvironmentVariableConfig plainVar1 = new EnvironmentVariableConfig("var1", "var1_value");
        EnvironmentVariableConfig plainVar2 = new EnvironmentVariableConfig("var2", "var2_value");
        EnvironmentVariableConfig var1 = secureVariable(goCipher, "foo1", "bar1", "encryptedBar1");
        EnvironmentVariableConfig var2 = secureVariable(goCipher, "foo2", "bar2", "encryptedBar2");
        environmentVariablesConfig.addAll(Arrays.asList(var1, var2, plainVar1, plainVar2));
        List<EnvironmentVariableConfig> variables = environmentVariablesConfig.getPlainTextVariables();
        Assert.assertThat(variables.size(), Matchers.is(2));
        Assert.assertThat(variables, Matchers.hasItems(plainVar1, plainVar2));
    }
}

