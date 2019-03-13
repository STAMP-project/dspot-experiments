/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.gradle.tasks.bundling;


import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link LaunchScriptConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class LaunchScriptConfigurationTests {
    private final AbstractArchiveTask task = Mockito.mock(AbstractArchiveTask.class);

    private final Project project = Mockito.mock(Project.class);

    @Test
    public void initInfoProvidesUsesArchiveBaseNameByDefault() {
        BDDMockito.given(this.task.getBaseName()).willReturn("base-name");
        assertThat(getProperties()).containsEntry("initInfoProvides", "base-name");
    }

    @Test
    public void initInfoShortDescriptionUsesDescriptionByDefault() {
        BDDMockito.given(this.project.getDescription()).willReturn("Project description");
        assertThat(getProperties()).containsEntry("initInfoShortDescription", "Project description");
    }

    @Test
    public void initInfoShortDescriptionUsesArchiveBaseNameWhenDescriptionIsNull() {
        BDDMockito.given(this.task.getBaseName()).willReturn("base-name");
        assertThat(getProperties()).containsEntry("initInfoShortDescription", "base-name");
    }

    @Test
    public void initInfoShortDescriptionUsesSingleLineVersionOfMultiLineProjectDescription() {
        BDDMockito.given(this.project.getDescription()).willReturn("Project\ndescription");
        assertThat(getProperties()).containsEntry("initInfoShortDescription", "Project description");
    }

    @Test
    public void initInfoDescriptionUsesArchiveBaseNameWhenDescriptionIsNull() {
        BDDMockito.given(this.task.getBaseName()).willReturn("base-name");
        assertThat(getProperties()).containsEntry("initInfoDescription", "base-name");
    }

    @Test
    public void initInfoDescriptionUsesProjectDescriptionByDefault() {
        BDDMockito.given(this.project.getDescription()).willReturn("Project description");
        assertThat(getProperties()).containsEntry("initInfoDescription", "Project description");
    }

    @Test
    public void initInfoDescriptionUsesCorrectlyFormattedMultiLineProjectDescription() {
        BDDMockito.given(this.project.getDescription()).willReturn("The\nproject\ndescription");
        assertThat(getProperties()).containsEntry("initInfoDescription", "The\n#  project\n#  description");
    }
}

