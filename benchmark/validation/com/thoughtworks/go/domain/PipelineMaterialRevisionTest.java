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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.domain.materials.Modification;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineMaterialRevisionTest {
    @Test
    public void shouldSetFROMRevisionSameAsTORevisionWhenDependencyMaterial() {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("pipeline_name"), new CaseInsensitiveString("stage_name"));
        Modification latestModification = modification(new Date(), "pipeline_name/4/stage_name/1", "4", null);
        MaterialRevision revision = new MaterialRevision(material, latestModification, new Modification(new Date(), "pipeline_name/2/stage_name/1", "2", null));
        PipelineMaterialRevision pmr = new PipelineMaterialRevision(9, revision, null);
        Assert.assertThat(pmr.getToModification(), Matchers.is(latestModification));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(latestModification));
    }

    @Test
    public void shouldNotSetFROMRevisionSameAsTORevisionWhenSCMMaterial() {
        Material material = new HgMaterial("http://some_server/repo", null);
        Modification latestModification = modification(new Date(), "123", null, null);
        Modification earlierModification = modification(new Date(), "23", null, null);
        MaterialRevision revision = new MaterialRevision(material, latestModification, earlierModification);
        PipelineMaterialRevision pmr = new PipelineMaterialRevision(9, revision, null);
        Assert.assertThat(pmr.getToModification(), Matchers.is(latestModification));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(earlierModification));
    }

    @Test
    public void shouldUpdateFROMRevisionSameAsTORevisionWhenDependencyMaterial() {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("pipeline_name"), new CaseInsensitiveString("stage_name"));
        Modification latestModification = modification(new Date(), "pipeline_name/4/stage_name/1", "4", null);
        MaterialRevision revision = new MaterialRevision(material, latestModification, modification(new Date(), "pipeline_name/2/stage_name/1", "2", null));
        PipelineMaterialRevision pmr = new PipelineMaterialRevision(9, revision, null);
        Modification differentFrom = modification(new Date(), "pipeline_name/3/stage_name/1", "3", null);
        pmr.useMaterialRevision(new MaterialRevision(material, latestModification, differentFrom));
        Assert.assertThat(pmr.getToModification(), Matchers.is(latestModification));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(latestModification));
        Modification laterThanTheLatest = modification(new Date(), "pipeline_name/5/stage_name/1", "5", null);
        pmr.useMaterialRevision(new MaterialRevision(material, laterThanTheLatest, differentFrom));
        Assert.assertThat(pmr.getToModification(), Matchers.is(laterThanTheLatest));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(laterThanTheLatest));
        pmr.useMaterialRevision(new MaterialRevision(material, laterThanTheLatest, modification(new Date(), "pipeline_name/3/stage_name/2", "3", null)));
        Assert.assertThat(pmr.getToModification(), Matchers.is(laterThanTheLatest));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(laterThanTheLatest));
    }

    @Test
    public void shouldUsePassedInFROMRevisionWhenSCMMaterial() {
        Material material = new HgMaterial("http://some_server/repo", null);
        Modification latestModification = modification(new Date(), "123", null, null);
        Modification earlierModification = modification(new Date(), "23", null, null);
        PipelineMaterialRevision pmr = new PipelineMaterialRevision(9, new MaterialRevision(material, latestModification, earlierModification), null);
        Modification earlierThatEarlyModification = modification(new Date(), "13", null, null);
        pmr.useMaterialRevision(new MaterialRevision(material, latestModification, earlierThatEarlyModification));
        Assert.assertThat(pmr.getToModification(), Matchers.is(latestModification));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(earlierThatEarlyModification));
        Modification laterThanLatestModification = modification(new Date(), "1234", null, null);
        pmr.useMaterialRevision(new MaterialRevision(material, laterThanLatestModification, earlierThatEarlyModification));
        Assert.assertThat(pmr.getToModification(), Matchers.is(laterThanLatestModification));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(earlierThatEarlyModification));
        pmr.useMaterialRevision(new MaterialRevision(material, laterThanLatestModification, earlierModification));
        Assert.assertThat(pmr.getToModification(), Matchers.is(laterThanLatestModification));
        Assert.assertThat(pmr.getFromModification(), Matchers.is(earlierModification));
    }
}

