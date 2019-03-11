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
package com.thoughtworks.go.domain.materials.svn;


import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.SvnTestRepoWithExternal;
import java.io.File;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SvnExternalTest {
    @ClassRule
    public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static SvnTestRepoWithExternal svnRepo;

    public static File workingDir;

    @Test
    public void shouldGetAllExternalURLSByPropgetOnMainURL() throws Exception {
        String url = SvnExternalTest.svnRepo.projectRepositoryUrl();
        SvnCommand svn = new SvnCommand(null, url, "user", "pass", false);
        List<SvnExternal> urls = svn.getAllExternalURLs();
        Assert.assertThat(urls.size(), Matchers.is(1));
    }

    @Test
    public void shouldGetLatestRevisionFromExpandedSvnExternalRepository() {
        MaterialRevisions materialRevisions = new MaterialRevisions();
        Material svnExt = MaterialsMother.svnMaterial(SvnExternalTest.svnRepo.externalRepositoryUrl(), "end2end");
        List<Modification> modifications = ((SvnMaterial) (svnExt)).latestModification(SvnExternalTest.svnRepo.workingFolder(), new TestSubprocessExecutionContext());
        materialRevisions.addRevision(svnExt, modifications);
        Assert.assertThat(materialRevisions.numberOfRevisions(), Matchers.is(1));
        MaterialRevision materialRevision = materialRevisions.getRevisions().get(0);
        Assert.assertThat(materialRevision.getMaterial(), Matchers.is(svnExt));
        Assert.assertThat(materialRevision.getRevision().getRevision(), Matchers.is("4"));
    }

    @Test
    public void shouldGetLatestRevision() {
        SvnMaterial svn = MaterialsMother.svnMaterial(SvnExternalTest.svnRepo.projectRepositoryUrl(), null);
        SvnMaterial svnExt = MaterialsMother.svnMaterial(SvnExternalTest.svnRepo.externalRepositoryUrl(), "end2end");
        final Materials materials = new Materials(svn, svnExt);
        final MaterialRevisions materialRevisions = materials.latestModification(SvnExternalTest.svnRepo.workingFolder(), new TestSubprocessExecutionContext());
        Assert.assertThat(materialRevisions.numberOfRevisions(), Matchers.is(2));
        MaterialRevision main = materialRevisions.getRevisions().get(0);
        Assert.assertThat(main.getMaterial(), Matchers.is(svn));
        Assert.assertThat(main.getModifications().size(), Matchers.is(1));
        Assert.assertThat(main.getRevision().getRevision(), Matchers.is("5"));
        MaterialRevision external = materialRevisions.getRevisions().get(1);
        Assert.assertThat(external.getMaterial(), Matchers.is(svnExt));
        Assert.assertThat(external.getRevision().getRevision(), Matchers.is("4"));
        Assert.assertThat(external.getModifications().size(), Matchers.is(1));
    }
}

