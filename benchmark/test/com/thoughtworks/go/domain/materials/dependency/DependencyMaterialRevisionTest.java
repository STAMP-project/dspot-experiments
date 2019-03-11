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
package com.thoughtworks.go.domain.materials.dependency;


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.domain.MaterialRevision;
import java.util.Date;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DependencyMaterialRevisionTest {
    @Test
    public void shouldRenderPipelineLabelToRenderer() {
        DependencyMaterialRevision revision = DependencyMaterialRevision.create("pipeline", 50, "1.0.123", "stage", 1);
        Assert.assertThat(revision.getRevisionUrl(), Matchers.is("pipelines/pipeline/50/stage/1"));
    }

    @Test
    public void shouldReturnRevisionForSavingIntoDatabase() {
        DependencyMaterialRevision revision = DependencyMaterialRevision.create("pipeline", 2, "1.0.123", "stage", 1);
        Assert.assertThat(revision.getRevision(), Matchers.is("pipeline/2/stage/1"));
        Assert.assertThat(revision.getPipelineLabel(), Matchers.is("1.0.123"));
    }

    @Test
    public void shouldUseLabelIfCounterIsNotPresent() {
        try {
            DependencyMaterialRevision.create("pipeline", null, "1.0.123", "stage", 1);
            Assert.fail("creation without pipeline counter must not be allowed");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Dependency material revision can not be created without pipeline counter."));
        }
    }

    @Test
    public void shouldConvertToTheCounterBasedRevision() {
        DependencyMaterialRevision materialRevision = DependencyMaterialRevision.create("pipeline", 10, "1.2.3", "stage", 4);
        MaterialRevision withRevision = materialRevision.convert(new com.thoughtworks.go.config.materials.dependency.DependencyMaterial(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage")), new Date());
        DependencyMaterialRevision revision = ((DependencyMaterialRevision) (withRevision.getRevision()));
        Assert.assertThat(revision.getRevision(), Matchers.is("pipeline/10/stage/4"));
        Assert.assertThat(revision.getPipelineLabel(), Matchers.is("1.2.3"));
    }

    @Test
    public void shouldAddPipelineLabelAsRevisionForMaterial() {
        DependencyMaterialRevision materialRevision = DependencyMaterialRevision.create("pipeline", 10, "foo-1.2.3", "stage", 4);
        HashMap<String, String> revMap = new HashMap<>();
        materialRevision.putRevision(revMap);
        Assert.assertThat(revMap.get("pipeline"), Matchers.is("foo-1.2.3"));
    }
}

