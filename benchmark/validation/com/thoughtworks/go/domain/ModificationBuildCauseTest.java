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


import MaterialRevisions.EMPTY;
import ModificationsMother.MOD_COMMENT_3;
import ModificationsMother.MOD_USER_WITH_HTML_CHAR;
import Username.ANONYMOUS;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ModificationBuildCauseTest {
    private BuildCause buildCause;

    @Test
    public void shouldAggreateUserNameFromModifications() throws Exception {
        String message = String.format("modified by %s", MOD_USER_WITH_HTML_CHAR);
        Assert.assertThat(buildCause.getBuildCauseMessage(), Matchers.is(message));
    }

    @Test
    public void shouldReturnBuildCauseMessageForLegacyDependencyRevision() {
        MaterialRevisions revisions = new MaterialRevisions();
        Modification modification = new Modification(new Date(), "pipelineName/10/stageName/1", "MOCK_LABEL-12", null);
        revisions.addRevision(new com.thoughtworks.go.config.materials.dependency.DependencyMaterial(new CaseInsensitiveString("cruise"), new CaseInsensitiveString("dev")), modification);
        BuildCause modificationBuildCause = BuildCause.createWithModifications(revisions, "");
        String message = modificationBuildCause.getBuildCauseMessage();
        Assert.assertThat(message, Matchers.containsString("triggered by pipelineName/10/stageName/1"));
    }

    @Test
    public void shouldReturnBuildCauseMessage() {
        MaterialRevisions revisions = new MaterialRevisions();
        Modification modification = new Modification(new Date(), "pipelineName/123/stageName/1", "MOCK_LABEL-12", null);
        revisions.addRevision(new com.thoughtworks.go.config.materials.dependency.DependencyMaterial(new CaseInsensitiveString("cruise"), new CaseInsensitiveString("dev")), modification);
        BuildCause modificationBuildCause = BuildCause.createWithModifications(revisions, "");
        String message = modificationBuildCause.getBuildCauseMessage();
        Assert.assertThat(message, Matchers.containsString("triggered by pipelineName/123/stageName/1"));
    }

    @Test
    public void shouldAggreateUserCommentFromModifications() throws Exception {
        ModificationSummaries summaries = buildCause.toModificationSummaries();
        String message = summaries.getModification(0).getComment();
        String user = summaries.getModification(0).getUserName();
        Assert.assertThat(user, Matchers.is(MOD_USER_WITH_HTML_CHAR));
        Assert.assertThat(message, Matchers.is(MOD_COMMENT_3));
    }

    @Test
    public void shouldDisplayNoModifications() throws Exception {
        buildCause = BuildCause.createWithModifications(new MaterialRevisions(), "");
        Assert.assertThat(buildCause.getBuildCauseMessage(), Matchers.is("No modifications"));
    }

    @Test
    public void shouldSafelyGetBuildCausedBy() throws Exception {
        Assert.assertThat(BuildCause.createWithEmptyModifications().getBuildCauseMessage(), Matchers.is("No modifications"));
    }

    @Test
    public void shouldGetBuildCausedByIfIsDenpendencyMaterial() throws Exception {
        MaterialRevisions revisions = new MaterialRevisions();
        Modification modification = new Modification(new Date(), "pipelineName/10/stageName/1", "MOCK_LABEL-12", null);
        revisions.addRevision(new com.thoughtworks.go.config.materials.dependency.DependencyMaterial(new CaseInsensitiveString("cruise"), new CaseInsensitiveString("dev")), modification);
        Assert.assertThat(BuildCause.createWithModifications(revisions, "").getBuildCauseMessage(), Matchers.is("triggered by pipelineName/10/stageName/1"));
    }

    @Test
    public void shouldBeInvalidWhenMaterialsFromBuildCauseAreDifferentFromConfigFile() {
        try {
            buildCause.assertMaterialsMatch(new com.thoughtworks.go.config.materials.MaterialConfigs(MaterialConfigsMother.hgMaterialConfig()));
            Assert.fail("The material from build cause was different from the one in the config");
        } catch (RuntimeException expected) {
        }
    }

    @Test
    public void shouldIncludeUserWhoForcedBuildInManualBuildCause() {
        BuildCause cause = BuildCause.createManualForced(null, new com.thoughtworks.go.server.domain.Username(new CaseInsensitiveString("Joe Bloggs")));
        Assert.assertThat(cause.getBuildCauseMessage(), Matchers.containsString("Forced by Joe Bloggs"));
    }

    @Test
    public void shouldNotAllowNullUsername() {
        BuildCause cause = BuildCause.createManualForced(EMPTY, ANONYMOUS);
        Assert.assertThat(cause.getBuildCauseMessage(), Matchers.containsString("Forced by anonymous"));
    }

    @Test
    public void shouldNotAllowCreationWithANullUsername() {
        try {
            BuildCause.createManualForced(null, null);
            Assert.fail("Expected NullPointerException to be thrown");
        } catch (IllegalArgumentException ignore) {
            Assert.assertThat(ignore.getMessage(), Matchers.containsString("Username cannot be null"));
        }
    }

    @Test
    public void shouldBeValidWithExternalMaterials() {
        SvnMaterial mainRepo = MaterialsMother.svnMaterial("mainRepo");
        MaterialRevisions revisions = new MaterialRevisions();
        revisions.addRevision(mainRepo, ModificationsMother.multipleModificationList());
        revisions.addRevision(MaterialsMother.svnMaterial("externalRepo"), ModificationsMother.multipleModificationList());
        buildCause = BuildCause.createWithModifications(revisions, "");
        buildCause.assertMaterialsMatch(new com.thoughtworks.go.config.materials.MaterialConfigs(mainRepo.config()));
    }

    @Test
    public void shouldBeInvalidWhenMaterialsFromConfigAreNotInBuildCause() {
        SvnMaterial mainRepo = MaterialsMother.svnMaterial("mainRepo");
        SvnMaterial extRepo = MaterialsMother.svnMaterial("externalRepo");
        MaterialRevisions revisions = new MaterialRevisions();
        revisions.addRevision(mainRepo, ModificationsMother.multipleModificationList());
        buildCause = BuildCause.createWithModifications(revisions, "");
        try {
            buildCause.assertMaterialsMatch(new com.thoughtworks.go.config.materials.MaterialConfigs(mainRepo.config(), extRepo.config()));
            Assert.fail("All the materials from config file should be in build cause");
        } catch (Exception expected) {
        }
    }
}

