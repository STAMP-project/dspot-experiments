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


import ArtifactPropertyConfig.NAME;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ArtifactPropertyConfigTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File workspace;

    @Test
    public void shouldAddErrorToErrorCollection() throws IOException {
        String multipleMatchXPATH = "//artifact/@src";
        ArtifactPropertyConfig generator = new ArtifactPropertyConfig("test_property", createSrcFile().getName(), multipleMatchXPATH);
        generator.addError("src", "Source invalid");
        Assert.assertThat(generator.errors().on("src"), Matchers.is("Source invalid"));
    }

    @Test
    public void shouldValidateThatNameIsMandatory() {
        ArtifactPropertyConfig generator = new ArtifactPropertyConfig(null, "props.xml", "//some_xpath");
        generator.validateTree(null);
        Assert.assertThat(generator.errors().on(NAME), Matchers.containsString("Invalid property name 'null'."));
    }
}

