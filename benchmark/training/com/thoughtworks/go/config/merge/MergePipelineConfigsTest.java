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
package com.thoughtworks.go.config.merge;


import BasicPipelineConfigs.DEFAULT_GROUP;
import BasicPipelineConfigs.GROUP;
import PipelineConfig.NAME;
import com.thoughtworks.go.config.PipelineConfigsTestBase;
import com.thoughtworks.go.config.remote.ConfigOrigin;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MergePipelineConfigsTest extends PipelineConfigsTestBase {
    @Test
    public void shouldReturnNullForGetLocalWhenOnlyRemoteParts() {
        BasicPipelineConfigs firstPart = new BasicPipelineConfigs();
        firstPart.setOrigin(new RepoConfigOrigin());
        BasicPipelineConfigs secondPart = new BasicPipelineConfigs();
        secondPart.setOrigin(new RepoConfigOrigin());
        MergePipelineConfigs merge = new MergePipelineConfigs(firstPart, secondPart);
        Assert.assertNull(merge.getLocal());
    }

    @Test
    public void shouldReturnFilePartForGetLocalWhenHasRemoteAndFilePart() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs();
        filePart.setOrigin(new FileConfigOrigin());
        BasicPipelineConfigs secondPart = new BasicPipelineConfigs();
        secondPart.setOrigin(new RepoConfigOrigin());
        MergePipelineConfigs merge = new MergePipelineConfigs(filePart, secondPart);
        Assert.assertThat(merge.getLocal(), Matchers.<PipelineConfigs>is(filePart));
    }

    @Test
    public void shouldSetAuthorizationInFile() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs();
        filePart.setOrigin(new FileConfigOrigin());
        MergePipelineConfigs merge = new MergePipelineConfigs(filePart, new BasicPipelineConfigs());
        Authorization auth = new Authorization(new AdminsConfig(new AdminUser(new CaseInsensitiveString("buddy"))));
        merge.setAuthorization(auth);
        Assert.assertThat(filePart.getAuthorization(), Matchers.is(auth));
    }

    @Test
    public void shouldAddToFirstEditableWhenAddToTop() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2"));
        filePart.setOrigin(new FileConfigOrigin());
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1")), filePart);
        group.addToTop(PipelineConfigMother.pipelineConfig("pipeline3"));
        Assert.assertThat(filePart.hasPipeline(new CaseInsensitiveString("pipeline3")), Matchers.is(true));
        Assert.assertThat(group.hasPipeline(new CaseInsensitiveString("pipeline3")), Matchers.is(true));
    }

    @Test
    public void shouldReturnIndexOfPipeline() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1"), PipelineConfigMother.pipelineConfig("pipeline2")));
        PipelineConfig pipelineConfig = group.findBy(new CaseInsensitiveString("pipeline2"));
        Assert.assertThat(group.indexOf(pipelineConfig), Matchers.is(1));
    }

    @Test
    public void shouldApplyChangesToPipelineWhenPartEditable() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1"));
        filePart.setOrigin(new FileConfigOrigin());
        PipelineConfigs group = new MergePipelineConfigs(filePart);
        PipelineConfig pipelineConfig = ((PipelineConfig) (group.get(0).clone()));
        pipelineConfig.setLabelTemplate("blah");
        group.update(group.getGroup(), pipelineConfig, "pipeline1");
        Assert.assertThat(group.get(0).getLabelTemplate(), Matchers.is("blah"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailToUpdateName() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1")), new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2")));
        group.setConfigAttributes(DataStructureUtils.m(GROUP, "my-new-group"));
        Assert.assertThat(group.getGroup(), Matchers.is("my-new-group"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldSetToDefaultGroupWithGroupNameIsEmptyString() {
        PipelineConfigs pipelineConfigs = new MergePipelineConfigs(new BasicPipelineConfigs());
        pipelineConfigs.setGroup("");
        Assert.assertThat(pipelineConfigs.getGroup(), Matchers.is(DEFAULT_GROUP));
    }

    // 2 parts and more cases
    @Test
    public void shouldReturnTrueIfPipelineExist_When2ConfigParts() {
        PipelineConfigs part1 = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1"));
        PipelineConfigs part2 = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2"));
        MergePipelineConfigs merge = new MergePipelineConfigs(part1, part2);
        Assert.assertThat("shouldReturnTrueIfPipelineExist", merge.hasPipeline(new CaseInsensitiveString("pipeline1")), Matchers.is(true));
        Assert.assertThat("shouldReturnTrueIfPipelineExist", merge.hasPipeline(new CaseInsensitiveString("pipeline2")), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfPipelineNotExist_When2ConfigParts() {
        PipelineConfigs part1 = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1"));
        PipelineConfigs part2 = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2"));
        MergePipelineConfigs merge = new MergePipelineConfigs(part2);
        Assert.assertThat("shouldReturnFalseIfPipelineNotExist", merge.hasPipeline(new CaseInsensitiveString("not-exist")), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfAuthorizationIsNotDefined_When2ConfigParts() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs();
        filePart.setOrigin(new FileConfigOrigin());
        MergePipelineConfigs merge = new MergePipelineConfigs(new BasicPipelineConfigs(), filePart);
        Assert.assertThat(merge.hasViewPermission(new CaseInsensitiveString("anyone"), null), Matchers.is(true));
    }

    @Test
    public void shouldReturnAuthorizationFromFileIfDefined_When2ConfigParts() {
        BasicPipelineConfigs part1 = new BasicPipelineConfigs();
        Authorization fileAuth = new Authorization();
        part1.setAuthorization(fileAuth);
        part1.setOrigin(new FileConfigOrigin());
        BasicPipelineConfigs part2 = new BasicPipelineConfigs();
        part2.setAuthorization(new Authorization());
        MergePipelineConfigs merge = new MergePipelineConfigs(part1, part2);
        Assert.assertThat(merge.getAuthorization(), Matchers.is(fileAuth));
    }

    @Test
    public void shouldReturnFalseIfViewPermissionIsNotDefined_When2ConfigParts() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline3"));
        filePart.setOrigin(new FileConfigOrigin());
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1")), new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2")), filePart);
        group.getAuthorization().getOperationConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasViewPermission(new CaseInsensitiveString("jez"), null), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueForOperatePermissionIfAuthorizationIsNotDefined_When2ConfigParts() {
        BasicPipelineConfigs filePart = new BasicPipelineConfigs();
        filePart.setOrigin(new FileConfigOrigin());
        Assert.assertThat(new MergePipelineConfigs(filePart, new BasicPipelineConfigs()).hasOperatePermission(new CaseInsensitiveString("anyone"), null), Matchers.is(true));
    }

    @Test
    public void validate_shouldMakeSureTheNameIsAppropriate_When2ConfigParts() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(), new BasicPipelineConfigs());
        group.validate(null);
        Assert.assertThat(group.errors().on(GROUP), Matchers.is("Invalid group name 'null'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnPartsWithDifferentGroupNames() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs("one", null), new BasicPipelineConfigs("two", null));
    }

    @Test
    public void shouldValidateThatPipelineNameIsUnique_When2ConfigParts() {
        PipelineConfig first = PipelineConfigMother.pipelineConfig("first");
        PipelineConfig duplicate = PipelineConfigMother.pipelineConfig("first");
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(first, PipelineConfigMother.pipelineConfig("second")), new BasicPipelineConfigs(duplicate, PipelineConfigMother.pipelineConfig("third")));
        group.validate(null);
        Assert.assertThat(duplicate.errors().on(NAME), Matchers.is("You have defined multiple pipelines called 'first'. Pipeline names are case-insensitive and must be unique."));
        Assert.assertThat(first.errors().on(NAME), Matchers.is("You have defined multiple pipelines called 'first'. Pipeline names are case-insensitive and must be unique."));
    }

    @Test
    public void shouldValidateNameUniqueness_When2ConfigParts() {
        PipelineConfig first = PipelineConfigMother.pipelineConfig("first");
        PipelineConfig duplicate = PipelineConfigMother.pipelineConfig("first");
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(first, PipelineConfigMother.pipelineConfig("second")), new BasicPipelineConfigs(duplicate, PipelineConfigMother.pipelineConfig("third")));
        Map<String, PipelineConfigs> nameToConfig = new HashMap<>();
        List<PipelineConfigs> visited = new ArrayList();
        group.validateNameUniqueness(nameToConfig);
    }

    @Test
    public void shouldReturnSizeSummedFrom2ConfigParts() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1")), new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2")));
        Assert.assertThat(group.size(), Matchers.is(2));
    }

    @Test
    public void shouldReturnTrueWhenAllPartsEmpty() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(), new BasicPipelineConfigs());
        Assert.assertThat(group.isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseSomePartIsNotEmpty() {
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1")), new BasicPipelineConfigs());
        Assert.assertThat(group.isEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueWhenContainsPipeline() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(pipe1), new BasicPipelineConfigs());
        Assert.assertThat(group.contains(pipe1), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseWhenDoesNotContainPipeline() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(pipe1), new BasicPipelineConfigs());
        Assert.assertThat(group.contains(PipelineConfigMother.pipelineConfig("pipeline2")), Matchers.is(false));
    }

    @Test
    public void shouldReturnPipelinesInOrder() {
        PipelineConfig pipeline1 = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfig pipeline3 = PipelineConfigMother.pipelineConfig("pipeline3");
        PipelineConfig pipeline5 = PipelineConfigMother.pipelineConfig("pipeline5");
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("pipeline2");
        PipelineConfig pipeline4 = PipelineConfigMother.pipelineConfig("pipeline4");
        PipelineConfigs group = new MergePipelineConfigs(new BasicPipelineConfigs(pipeline1, pipeline2), new BasicPipelineConfigs(pipeline3), new BasicPipelineConfigs(pipeline4, pipeline5));
        Assert.assertThat(group.get(0), Matchers.is(pipeline1));
        Assert.assertThat(group.get(1), Matchers.is(pipeline2));
        Assert.assertThat(group.get(2), Matchers.is(pipeline3));
        Assert.assertThat(group.get(3), Matchers.is(pipeline4));
        Assert.assertThat(group.get(4), Matchers.is(pipeline5));
    }

    @Test
    public void shouldReturnFirstEditablePartWhenExists() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        BasicPipelineConfigs part1 = new BasicPipelineConfigs(pipe1);
        part1.setOrigin(new FileConfigOrigin());
        MergePipelineConfigs group = new MergePipelineConfigs(part1, new BasicPipelineConfigs());
        Assert.assertThat(group.getFirstEditablePartOrNull(), Matchers.<PipelineConfigs>is(part1));
    }

    @Test
    public void shouldReturnNullWhenFirstEditablePartNotExists() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        BasicPipelineConfigs part1 = new BasicPipelineConfigs(pipe1);
        MergePipelineConfigs group = new MergePipelineConfigs(part1, new BasicPipelineConfigs());
        Assert.assertNull(group.getFirstEditablePartOrNull());
    }

    @Test
    public void shouldReturnPartWithPipelineWhenExists() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        BasicPipelineConfigs part1 = new BasicPipelineConfigs(pipe1);
        part1.setOrigin(new FileConfigOrigin());
        MergePipelineConfigs group = new MergePipelineConfigs(part1, new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2")));
        Assert.assertThat(group.getPartWithPipeline(new CaseInsensitiveString("pipeline1")), Matchers.<PipelineConfigs>is(part1));
    }

    @Test
    public void shouldReturnNullPartWithPipelineNotExists() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        BasicPipelineConfigs part1 = new BasicPipelineConfigs(pipe1);
        MergePipelineConfigs group = new MergePipelineConfigs(part1, new BasicPipelineConfigs());
        Assert.assertNull(group.getPartWithPipeline(new CaseInsensitiveString("pipelineX")));
    }

    @Test
    public void shouldAddPipelineToFirstEditablePartWhenExists() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        BasicPipelineConfigs part1 = new BasicPipelineConfigs(pipe1);
        part1.setOrigin(new FileConfigOrigin());
        MergePipelineConfigs group = new MergePipelineConfigs(part1, new BasicPipelineConfigs());
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("pipeline2");
        group.add(pipeline2);
        Assert.assertThat(group.contains(pipeline2), Matchers.is(true));
    }

    @Test
    public void shouldBombWhenAddPipelineAndNoEditablePartExists() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipeline1");
        BasicPipelineConfigs part1 = new BasicPipelineConfigs(pipe1);
        MergePipelineConfigs group = new MergePipelineConfigs(part1, new BasicPipelineConfigs());
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("pipeline2");
        try {
            group.add(pipeline2);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.is("No editable configuration sources"));
            return;
        }
        Assert.fail("exception not thrown");
    }

    @Test
    public void shouldFailToAddPipelineAtIndex_WhenWouldLandInNonEditablePart() {
        PipelineConfig pipeline0 = PipelineConfigMother.pipelineConfig("pipeline0");
        PipelineConfig pipeline1 = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfig pipeline3 = PipelineConfigMother.pipelineConfig("pipeline3");
        PipelineConfig pipeline5 = PipelineConfigMother.pipelineConfig("pipeline5");
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("pipeline2");
        PipelineConfig pipeline4 = PipelineConfigMother.pipelineConfig("pipeline4");
        BasicPipelineConfigs pipelineConfigsMiddle = new BasicPipelineConfigs(pipeline3);
        pipelineConfigsMiddle.setOrigin(new FileConfigOrigin());
        BasicPipelineConfigs bottom = new BasicPipelineConfigs(pipeline0, pipeline1, pipeline2);
        BasicPipelineConfigs top = new BasicPipelineConfigs(pipeline4, pipeline5);
        bottom.setOrigin(new RepoConfigOrigin());
        top.setOrigin(new RepoConfigOrigin());
        PipelineConfigs group = new MergePipelineConfigs(bottom, pipelineConfigsMiddle, top);
        PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipelineToInsert");
        tryAddAndAssertThatFailed(group, p1, 0);
        tryAddAndAssertThatFailed(group, p1, 1);
        tryAddAndAssertThatFailed(group, p1, 2);
        tryAddAndAssertThatFailed(group, p1, 5);
        tryAddAndAssertThatFailed(group, p1, 4);
    }

    @Test
    public void shouldReturnOriginAsASumOfAllOrigins() {
        BasicPipelineConfigs fileConfigs = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline1"));
        fileConfigs.setOrigin(new FileConfigOrigin());
        BasicPipelineConfigs remoteConfigs = new BasicPipelineConfigs(PipelineConfigMother.pipelineConfig("pipeline2"));
        remoteConfigs.setOrigin(new RepoConfigOrigin());
        PipelineConfigs group = new MergePipelineConfigs(fileConfigs, remoteConfigs);
        ConfigOrigin allOrigins = group.getOrigin();
        Assert.assertThat((allOrigins instanceof MergeConfigOrigin), Matchers.is(true));
        MergeConfigOrigin mergeConfigOrigin = ((MergeConfigOrigin) (allOrigins));
        Assert.assertThat(mergeConfigOrigin.size(), Matchers.is(2));
        Assert.assertThat(mergeConfigOrigin.contains(new FileConfigOrigin()), Matchers.is(true));
        Assert.assertThat(mergeConfigOrigin.contains(new RepoConfigOrigin()), Matchers.is(true));
    }

    @Test
    public void shouldAddPipelineAtIndex_WhenWouldLandInEditablePart() {
        PipelineConfig pipeline0 = PipelineConfigMother.pipelineConfig("pipeline0");
        PipelineConfig pipeline1 = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfig pipeline3 = PipelineConfigMother.pipelineConfig("pipeline3");
        PipelineConfig pipeline5 = PipelineConfigMother.pipelineConfig("pipeline5");
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("pipeline2");
        PipelineConfig pipeline4 = PipelineConfigMother.pipelineConfig("pipeline4");
        BasicPipelineConfigs pipelineConfigsMiddle = new BasicPipelineConfigs(pipeline3);
        pipelineConfigsMiddle.setOrigin(new FileConfigOrigin());
        BasicPipelineConfigs bottom = new BasicPipelineConfigs(pipeline0, pipeline1, pipeline2);
        BasicPipelineConfigs top = new BasicPipelineConfigs(pipeline4, pipeline5);
        bottom.setOrigin(new RepoConfigOrigin());
        top.setOrigin(new RepoConfigOrigin());
        PipelineConfigs group = new MergePipelineConfigs(bottom, pipelineConfigsMiddle, top);
        PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipelineToInsert");
        group.add(3, p1);
        Assert.assertThat(group, Matchers.hasItem(p1));
        Assert.assertThat(pipelineConfigsMiddle, Matchers.hasItem(p1));
    }
}

