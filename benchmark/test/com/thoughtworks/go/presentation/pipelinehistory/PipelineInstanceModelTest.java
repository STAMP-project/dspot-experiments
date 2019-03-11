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
package com.thoughtworks.go.presentation.pipelinehistory;


import JobResult.Failed;
import JobResult.Passed;
import JobState.Completed;
import MaterialRevisions.EMPTY;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.JobResult;
import com.thoughtworks.go.domain.JobState;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.domain.StageIdentifier;
import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.helper.PipelineHistoryMother;
import java.util.Date;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class PipelineInstanceModelTest {
    private static final Modification HG_MATERIAL_MODIFICATION = new Modification("user", "Comment", "email", new Date(), "a087402bd2a7828a130c1bdf43f2d9ef8f48fd46");

    @Test
    public void shouldUnderstandActiveStage() {
        StageInstanceModels stages = new StageInstanceModels();
        stages.addStage("unit1", JobHistory.withJob("test", Completed, Passed, new Date()));
        JobHistory history = new JobHistory();
        history.add(new JobHistoryItem("test-1", JobState.Completed, JobResult.Failed, new Date()));
        history.add(new JobHistoryItem("test-2", JobState.Building, JobResult.Unknown, new Date()));
        StageInstanceModel activeStage = new StageInstanceModel("unit2", "1", history);
        stages.add(activeStage);
        stages.addFutureStage("unit3", false);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createManualForced(), stages);
        Assert.assertThat(model.activeStage(), Matchers.is(activeStage));
    }

    @Test
    public void shouldReturnNullWhenNoActiveStage() {
        StageInstanceModels stages = new StageInstanceModels();
        stages.addStage("unit1", JobHistory.withJob("test", Completed, Passed, new Date()));
        JobHistory history = new JobHistory();
        history.add(new JobHistoryItem("test-1", JobState.Completed, JobResult.Failed, new Date()));
        history.add(new JobHistoryItem("test-2", JobState.Completed, JobResult.Passed, new Date()));
        stages.add(new StageInstanceModel("unit2", "1", history));
        stages.addFutureStage("unit3", false);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createManualForced(), stages);
        Assert.assertThat(model.activeStage(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldUnderstandPipelineStatusMessage() throws Exception {
        MaterialRevisions revisions = ModificationsMother.modifyOneFile(MaterialsMother.hgMaterials("url"), "revision");
        StageInstanceModels stages = new StageInstanceModels();
        stages.addStage("unit1", JobHistory.withJob("test", Completed, Passed, new Date()));
        stages.addFutureStage("unit2", false);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createWithModifications(revisions, ""), stages);
        Assert.assertThat(model.getPipelineStatusMessage(), Matchers.is("Passed: unit1"));
    }

    @Test
    public void shouldGetCurrentRevisionForMaterial() {
        MaterialRevisions revisions = new MaterialRevisions();
        HgMaterial material = MaterialsMother.hgMaterial();
        revisions.addRevision(material, PipelineInstanceModelTest.HG_MATERIAL_MODIFICATION);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createWithModifications(revisions, ""), new StageInstanceModels());
        Assert.assertThat(model.getCurrentRevision(material.config()).getRevision(), Matchers.is("a087402bd2a7828a130c1bdf43f2d9ef8f48fd46"));
    }

    @Test
    public void shouldGetCurrentMaterialRevisionForMaterial() {
        MaterialRevisions revisions = new MaterialRevisions();
        HgMaterial material = MaterialsMother.hgMaterial();
        revisions.addRevision(material, PipelineInstanceModelTest.HG_MATERIAL_MODIFICATION);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createWithModifications(revisions, ""), new StageInstanceModels());
        Assert.assertThat(model.findCurrentMaterialRevisionForUI(material.config()), Matchers.is(revisions.getMaterialRevision(0)));
    }

    @Test
    public void shouldFallbackToPipelineFingerpringWhenGettingCurrentMaterialRevisionForMaterialIsNull() {
        MaterialRevisions revisions = new MaterialRevisions();
        HgMaterial material = MaterialsMother.hgMaterial();
        HgMaterial materialWithDifferentDest = MaterialsMother.hgMaterial();
        materialWithDifferentDest.setFolder("otherFolder");
        revisions.addRevision(material, PipelineInstanceModelTest.HG_MATERIAL_MODIFICATION);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createWithModifications(revisions, ""), new StageInstanceModels());
        Assert.assertThat(model.findCurrentMaterialRevisionForUI(materialWithDifferentDest.config()), Matchers.is(revisions.getMaterialRevision(0)));
    }

    @Test
    public void shouldGetCurrentRevisionForMaterialByName() {
        MaterialRevisions revisions = new MaterialRevisions();
        HgMaterial material = MaterialsMother.hgMaterial();
        SvnMaterial svnMaterial = MaterialsMother.svnMaterial();
        material.setName(new CaseInsensitiveString("hg_material"));
        revisions.addRevision(svnMaterial, new Modification(new Date(), "1024", "MOCK_LABEL-12", null));
        revisions.addRevision(material, PipelineInstanceModelTest.HG_MATERIAL_MODIFICATION);
        BuildCause buildCause = BuildCause.createWithModifications(revisions, "");
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", buildCause, new StageInstanceModels());
        Assert.assertThat(model.getCurrentRevision("hg_material").getRevision(), Matchers.is("a087402bd2a7828a130c1bdf43f2d9ef8f48fd46"));
    }

    @Test
    public void shouldGetLatestMaterialRevisionForMaterial() {
        HgMaterial material = MaterialsMother.hgMaterial();
        Assert.assertThat(setUpModificationForHgMaterial().getLatestMaterialRevision(material.config()), Matchers.is(new com.thoughtworks.go.domain.MaterialRevision(material, PipelineInstanceModelTest.HG_MATERIAL_MODIFICATION)));
    }

    @Test
    public void shouldGetLatestRevisionForMaterial() {
        HgMaterialConfig hgMaterialConfig = MaterialConfigsMother.hgMaterialConfig();
        Assert.assertThat(setUpModificationForHgMaterial().getLatestRevision(hgMaterialConfig).getRevision(), Matchers.is("a087402bd2a7828a130c1bdf43f2d9ef8f48fd46"));
    }

    @Test
    public void shouldGetLatestRevisionForMaterialWithNoModifications() {
        Assert.assertThat(hgMaterialWithNoModifications().getLatestRevision(MaterialConfigsMother.hgMaterialConfig()).getRevision(), Matchers.is("No historical data"));
    }

    @Test
    public void shouldGetCurrentRevisionForMaterialName() {
        HgMaterial material = MaterialsMother.hgMaterial();
        material.setName(new CaseInsensitiveString("foo"));
        Assert.assertThat(setUpModificationFor(material).getCurrentRevision(CaseInsensitiveString.str(material.getName())).getRevision(), Matchers.is("a087402bd2a7828a130c1bdf43f2d9ef8f48fd46"));
    }

    @Test
    public void shouldThrowExceptionWhenCurrentRevisionForUnknownMaterialNameRequested() {
        HgMaterial material = MaterialsMother.hgMaterial();
        material.setName(new CaseInsensitiveString("foo"));
        try {
            Assert.assertThat(setUpModificationFor(material).getCurrentRevision("blah").getRevision(), Matchers.is("a087402bd2a7828a130c1bdf43f2d9ef8f48fd46"));
            Assert.fail("should have raised an exeption for unknown material name");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void shouldKnowIfLatestRevisionIsReal() throws Exception {
        Assert.assertThat(setUpModificationForHgMaterial().hasModificationsFor(MaterialConfigsMother.hgMaterialConfig()), Matchers.is(true));
    }

    @Test
    public void shouldKnowApproverAsApproverForTheFirstStage() {
        MaterialRevisions revisions = new MaterialRevisions();
        StageInstanceModels models = new StageInstanceModels();
        StageInstanceModel firstStage = new StageInstanceModel("dev", "1", new JobHistory());
        firstStage.setApprovedBy("some_user");
        models.add(firstStage);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createWithModifications(revisions, ""), models);
        Assert.assertThat(model.getApprovedBy(), Matchers.is("some_user"));
        Assert.assertThat(model.getApprovedByForDisplay(), Matchers.is("Triggered by some_user"));
    }

    @Test
    public void shouldUnderstandIfReal() {
        Assert.assertThat(PipelineInstanceModel.createEmptyModel().hasHistoricalData(), Matchers.is(true));
        Assert.assertThat(PipelineInstanceModel.createEmptyPipelineInstanceModel("pipeline", createWithEmptyModifications(), new StageInstanceModels()).hasHistoricalData(), Matchers.is(false));
    }

    // Pipeline: Red -> Green -> Has_Not_Run_Yet
    @Test
    public void shouldBeSucessfulOnAForceContinuedPass_Red_AND_Green_AND_Has_Not_Run_Yet() {
        Date occuredFirst = new Date(2008, 12, 13);
        Date occuredSecond = new Date(2008, 12, 14);
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Failed, occuredFirst), PipelineHistoryMother.job(Passed, occuredSecond));
        stageInstanceModels.add(new NullStageHistoryItem("stage-3", false));
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        Assert.assertThat(instanceModel.isLatestStageUnsuccessful(), Matchers.is(false));
        Assert.assertThat(instanceModel.isLatestStageSuccessful(), Matchers.is(true));
        Assert.assertThat(instanceModel.isRunning(), Matchers.is(true));
    }

    // Pipeline: Red(Rerun after second stage passed i.e. latest stage) -> Green -> Has_Not_Run_Yet
    @Test
    public void shouldReturnStatusOfAfailedRerunAndIncompleteStage() {
        Date occuredFirst = new Date(2008, 12, 13);
        Date occuredSecond = new Date(2008, 12, 14);
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Failed, occuredSecond), PipelineHistoryMother.job(Passed, occuredFirst));
        stageInstanceModels.add(new NullStageHistoryItem("stage-3", false));
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        Assert.assertThat(instanceModel.isLatestStageUnsuccessful(), Matchers.is(true));
        Assert.assertThat(instanceModel.isLatestStageSuccessful(), Matchers.is(false));
        Assert.assertThat(instanceModel.isRunning(), Matchers.is(true));
    }

    @Test
    public void shouldReturnStatusOfAFailedRerun() {
        Date occuredFirst = new Date(2008, 12, 13);
        Date occuredSecond = new Date(2008, 12, 14);
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Failed, occuredSecond), PipelineHistoryMother.job(Passed, occuredFirst));
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        Assert.assertThat(instanceModel.isLatestStageUnsuccessful(), Matchers.is(true));
        Assert.assertThat(instanceModel.isLatestStageSuccessful(), Matchers.is(false));
        Assert.assertThat(instanceModel.isRunning(), Matchers.is(false));
    }

    @Test
    public void shouldReturnStatusOfAPassedForceThrough() {
        Date occuredFirst = new Date(2008, 12, 13);
        Date occuredSecond = new Date(2008, 12, 14);
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Failed, occuredFirst), PipelineHistoryMother.job(Passed, occuredSecond));
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        Assert.assertThat(instanceModel.isLatestStageUnsuccessful(), Matchers.is(false));
        Assert.assertThat(instanceModel.isLatestStageSuccessful(), Matchers.is(true));
        Assert.assertThat(instanceModel.isRunning(), Matchers.is(false));
    }

    @Test
    public void shouldReturnPipelineStatusAsPassedWhenAllTheStagesPass() {
        Date occuredFirst = new Date(2008, 12, 13);
        Date occuredSecond = new Date(2008, 12, 14);
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Passed, occuredSecond), PipelineHistoryMother.job(Passed, occuredFirst));
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        Assert.assertThat(instanceModel.isLatestStageUnsuccessful(), Matchers.is(false));
        Assert.assertThat(instanceModel.isLatestStageSuccessful(), Matchers.is(true));
        Assert.assertThat(instanceModel.isRunning(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTheLatestStageEvenWhenThereIsANullStage() {
        Date occuredFirst = new DateTime().minusDays(1).toDate();
        Date occuredSecond = new DateTime().toDate();
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Passed, occuredSecond), PipelineHistoryMother.job(Passed, occuredFirst));
        NullStageHistoryItem stageHistoryItem = new NullStageHistoryItem("not_yet_run", false);
        stageInstanceModels.add(stageHistoryItem);
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        StageInstanceModel value = stageInstanceModels.get(0);
        Assert.assertThat(instanceModel.latestStage(), Matchers.is(value));
    }

    @Test
    public void shouldReturnIfAStageIsLatest() {
        Date occuredFirst = new DateTime().minusDays(1).toDate();
        Date occuredSecond = new DateTime().toDate();
        StageInstanceModels stageInstanceModels = PipelineHistoryMother.stagePerJob("stage", PipelineHistoryMother.job(Passed, occuredSecond), PipelineHistoryMother.job(Passed, occuredFirst));
        NullStageHistoryItem stageHistoryItem = new NullStageHistoryItem("not_yet_run", false);
        stageInstanceModels.add(stageHistoryItem);
        PipelineInstanceModel instanceModel = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", createWithEmptyModifications(), stageInstanceModels);
        Assert.assertThat(instanceModel.isLatestStage(stageInstanceModels.get(0)), Matchers.is(true));
        Assert.assertThat(instanceModel.isLatestStage(stageInstanceModels.get(1)), Matchers.is(false));
    }

    @Test
    public void shouldReturnIfAnyMaterialHasModifications() {
        final SvnMaterial svnMaterial = MaterialsMother.svnMaterial("http://svnurl");
        final HgMaterial hgMaterial = MaterialsMother.hgMaterial("http://hgurl", "hgdir");
        MaterialRevisions currentRevisions = ModificationsMother.getMaterialRevisions(new HashMap<Material, String>() {
            {
                put(svnMaterial, "1");
                put(hgMaterial, "a");
            }
        });
        MaterialRevisions latestRevisions = ModificationsMother.getMaterialRevisions(new HashMap<Material, String>() {
            {
                put(svnMaterial, "1");
                put(hgMaterial, "b");
            }
        });
        MaterialConfigs materialConfigs = new MaterialConfigs();
        materialConfigs.add(svnMaterial.config());
        materialConfigs.add(hgMaterial.config());
        StageInstanceModels stages = new StageInstanceModels();
        stages.addStage("unit1", JobHistory.withJob("test", Completed, Passed, new Date()));
        stages.addFutureStage("unit2", false);
        PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createWithModifications(currentRevisions, ""), stages);
        model.setLatestRevisions(latestRevisions);
        model.setMaterialConfigs(materialConfigs);
        Assert.assertThat("svnMaterial hasNewRevisions", model.hasNewRevisions(svnMaterial.config()), Matchers.is(false));
        Assert.assertThat("hgMaterial hasNewRevisions", model.hasNewRevisions(hgMaterial.config()), Matchers.is(true));
        Assert.assertThat("all materials hasNewRevisions", model.hasNewRevisions(), Matchers.is(true));
    }

    @Test
    public void shouldUnderstandIfItHasNeverCheckedForRevisions() {
        StageInstanceModels stages = new StageInstanceModels();
        stages.addStage("unit1", JobHistory.withJob("test", Completed, Passed, new Date()));
        stages.addFutureStage("unit2", false);
        PipelineInstanceModel pim = PipelineInstanceModel.createPipeline("pipeline", (-1), "label", BuildCause.createNeverRun(), stages);
        pim.setLatestRevisions(EMPTY);
        Assert.assertThat("pim.hasNeverCheckedForRevisions()", pim.hasNeverCheckedForRevisions(), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfThePipelineHasStage() {
        PipelineInstanceModel pim = PipelineHistoryMother.pipelineHistoryItemWithOneStage("pipeline", "stage", new Date());
        Assert.assertThat(pim.hasStage(pim.getStageHistory().first().getIdentifier()), Matchers.is(true));
        Assert.assertThat(pim.hasStage(new StageIdentifier("pipeline", 1, "1", "stagex", "2")), Matchers.is(false));
    }

    @Test
    public void shouldSetAndGetComment() {
        PipelineInstanceModel pim = PipelineInstanceModel.createEmptyModel();
        pim.setComment("test comment");
        Assert.assertThat("PipelineInstanceModel.getComment()", pim.getComment(), Matchers.is("test comment"));
    }
}

