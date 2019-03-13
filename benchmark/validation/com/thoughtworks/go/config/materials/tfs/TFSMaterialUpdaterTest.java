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
package com.thoughtworks.go.config.materials.tfs;


import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.materials.RevisionContext;
import com.thoughtworks.go.domain.materials.tfs.TfsMaterialUpdater;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TFSMaterialUpdaterTest {
    TfsMaterialUpdater tfsMaterialUpdater;

    RevisionContext revisionContext;

    TfsMaterial tfsMaterial;

    @Test
    public void shouldCreateBuildCommandUpdateToSpecificRevision() throws Exception {
        String expectedCommand = "compose\n    secret \"value:password\"\n    plugin \"password:password\" " + ("\"projectPath:projectpath\" \"domain:domain\" \"type:tfs\" \"url:url\" \"username:username\" " + "\"revision:11111\"");
        BuildCommand buildCommand = tfsMaterialUpdater.updateTo("baseDir", revisionContext);
        Assert.assertThat(buildCommand.dump(), Matchers.is(expectedCommand));
    }
}

