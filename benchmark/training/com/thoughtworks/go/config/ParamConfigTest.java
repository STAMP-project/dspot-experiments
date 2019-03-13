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


import ParamConfig.NAME;
import com.thoughtworks.go.domain.ConfigErrors;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ParamConfigTest {
    @Test
    public void validate_shouldMakeSureParamNameIsOfNameType() {
        Assert.assertThat(createAndValidate("name").errors().isEmpty(), Matchers.is(true));
        ConfigErrors errors = createAndValidate(".name").errors();
        Assert.assertThat(errors.isEmpty(), Matchers.is(false));
        Assert.assertThat(errors.on(NAME), Matchers.is("Invalid parameter name '.name'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldReturnValueForDisplay() {
        ParamConfig paramConfig = new ParamConfig("foo", "bar");
        Assert.assertThat(paramConfig.getValueForDisplay(), Matchers.is("bar"));
    }

    @Test
    public void shouldValidateName() {
        ParamConfig paramConfig = new ParamConfig();
        ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        Mockito.when(validationContext.getPipeline()).thenReturn(new PipelineConfig(new CaseInsensitiveString("p"), null));
        paramConfig.validateName(new HashMap(), validationContext);
        Assert.assertThat(paramConfig.errors().on(NAME), Matchers.is("Parameter cannot have an empty name for pipeline 'p'."));
    }
}

