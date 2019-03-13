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
package com.thoughtworks.go.config;


import GoConstants.CONFIG_SCHEMA_VERSION;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoConfigSchemaTest {
    @Test
    public void shouldResolveToCorrectSchemaFile() {
        Assert.assertThat(GoConfigSchema.resolveSchemaFile(CONFIG_SCHEMA_VERSION), Matchers.is("/cruise-config.xsd"));
        Assert.assertThat(GoConfigSchema.resolveSchemaFile(1), Matchers.is("/schemas/1_cruise-config.xsd"));
    }
}

