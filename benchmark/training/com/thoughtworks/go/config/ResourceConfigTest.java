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
package com.thoughtworks.go.config;


import JobConfig.RESOURCES;
import ResourceConfig.VALID_REGEX;
import com.thoughtworks.go.domain.ConfigErrors;
import java.lang.reflect.Field;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ResourceConfigTest {
    @Test
    public void shouldAllowValidResourceNameForAgentResources() throws Exception {
        ResourceConfig resourceConfig = resource("- foo|bar baz.quux");
        resourceConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(resourceConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldAllowParamsInsideResourceNameWhenInsideTemplates() throws Exception {
        ResourceConfig resourceConfig = resource("#{PARAMS}");
        ValidationContext context = ConfigSaveValidationContext.forChain(new BasicCruiseConfig(), new TemplatesConfig());
        resourceConfig.validate(context);
        Assert.assertThat(resourceConfig.errors().isEmpty(), Matchers.is(true));
    }

    // Note : At the Resource class level there is no way of accurately validating Parameters. This will only be invalidated when template gets used.
    @Test
    public void validate_shouldAllowAnyCombinationOfHashesAndCurlyBraces() throws Exception {
        ResourceConfig resourceConfig = resource("}#PARAMS{");
        ValidationContext context = ConfigSaveValidationContext.forChain(new BasicCruiseConfig(), new TemplatesConfig());
        resourceConfig.validate(context);
        Assert.assertThat(resourceConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldNotAllowInvalidResourceNamesWhenInsideTemplates() throws Exception {
        ResourceConfig resourceConfig = resource("#?{45}");
        ValidationContext context = ConfigSaveValidationContext.forChain(new BasicCruiseConfig(), new TemplatesConfig());
        resourceConfig.validate(context);
        Assert.assertThat(resourceConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(resourceConfig.errors().on(RESOURCES), Matchers.is("Resource name '#?{45}' is not valid. Valid names can contain valid parameter syntax or valid alphanumeric with hyphens,dots or pipes"));
    }

    @Test
    public void shouldNotAllowParamsInsideResourceNameWhenOutsideTemplates() throws Exception {
        ResourceConfig resourceConfig = resource("#{PARAMS}");
        ValidationContext context = ConfigSaveValidationContext.forChain(new BasicCruiseConfig(), new PipelineConfig());
        resourceConfig.validate(context);
        Assert.assertThat(resourceConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(resourceConfig.errors().on(RESOURCES), Matchers.is(String.format("Resource name '#{PARAMS}' is not valid. Valid names much match '%s'", VALID_REGEX)));
    }

    @Test
    public void shouldNotAllowInvalidResourceNameForAgentResources() throws Exception {
        ResourceConfig resourceConfig = resource("foo$bar");
        resourceConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        ConfigErrors configErrors = resourceConfig.errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(RESOURCES), Matchers.is(String.format("Resource name 'foo$bar' is not valid. Valid names much match '%s'", VALID_REGEX)));
    }

    // This is to work around a bug caused by MagicalCruiseConfigLoader,
    // since it uses direct field access
    @Test
    public void shouldUseTrimmedNameInEquals() throws IllegalAccessException, NoSuchFieldException {
        ResourceConfig resourceConfig = new ResourceConfig();
        Field field = resourceConfig.getClass().getDeclaredField("name");
        field.setAccessible(true);
        field.set(resourceConfig, "resource1   ");
        Assert.assertThat(new ResourceConfig("resource1"), Matchers.is(resourceConfig));
    }

    @Test
    public void shouldCompareBasedOnName() {
        ResourceConfig resourceConfigA = new ResourceConfig("aaa");
        ResourceConfig resourceConfigB = new ResourceConfig("bbb");
        Assert.assertThat(resourceConfigA.compareTo(resourceConfigB), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(resourceConfigB.compareTo(resourceConfigA), Matchers.is(Matchers.greaterThan(0)));
        Assert.assertThat(resourceConfigA.compareTo(resourceConfigA), Matchers.is(0));
    }
}

