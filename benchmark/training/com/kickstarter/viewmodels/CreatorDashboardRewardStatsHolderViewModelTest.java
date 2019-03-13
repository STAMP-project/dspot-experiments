package com.kickstarter.viewmodels;


import CreatorDashboardRewardStatsHolderViewModel.ViewModel;
import ProjectStatsEnvelope.RewardStats;
import ProjectStatsEnvelopeFactory.RewardStatsFactory;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.models.Project;
import com.kickstarter.services.apiresponses.ProjectStatsEnvelope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class CreatorDashboardRewardStatsHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Project> projectOutput = new TestSubscriber();

    private final TestSubscriber<List<ProjectStatsEnvelope.RewardStats>> rewardStatsOutput = new TestSubscriber();

    private final TestSubscriber<Boolean> rewardsStatsListIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> rewardsStatsTruncatedTextIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> rewardsTitleIsLimitedCopy = new TestSubscriber();

    @Test
    public void testProjectAndRewardStats() {
        final Project project = ProjectFactory.project();
        final ProjectStatsEnvelope.RewardStats rewardWith10Pledged = RewardStatsFactory.rewardStats().toBuilder().pledged(10).build();
        final ProjectStatsEnvelope.RewardStats rewardWith15Pledged = RewardStatsFactory.rewardStats().toBuilder().pledged(15).build();
        final ProjectStatsEnvelope.RewardStats rewardWith20Pledged = RewardStatsFactory.rewardStats().toBuilder().pledged(20).build();
        final List<ProjectStatsEnvelope.RewardStats> unsortedRewardStatsList = Arrays.asList(rewardWith15Pledged, rewardWith10Pledged, rewardWith20Pledged);
        final List<ProjectStatsEnvelope.RewardStats> sortedRewardStatsList = Arrays.asList(rewardWith20Pledged, rewardWith15Pledged, rewardWith10Pledged);
        setUpEnvironment(environment());
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, unsortedRewardStatsList));
        this.projectOutput.assertValue(project);
        this.rewardStatsOutput.assertValue(sortedRewardStatsList);
    }

    @Test
    public void testRewardsStatsListIsGone() {
        setUpEnvironment(environment());
        final Project project = ProjectFactory.project();
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, new ArrayList()));
        this.rewardsStatsListIsGone.assertValue(true);
        this.rewardsStatsTruncatedTextIsGone.assertValue(true);
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, Collections.singletonList(RewardStatsFactory.rewardStats())));
        this.rewardsStatsListIsGone.assertValues(true, false);
        this.rewardsStatsTruncatedTextIsGone.assertValue(true);
    }

    @Test
    public void testRewardsStatsTruncatedTextIsGone() {
        setUpEnvironment(environment());
        final Project project = ProjectFactory.project();
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, Collections.singletonList(RewardStatsFactory.rewardStats())));
        this.rewardsStatsTruncatedTextIsGone.assertValue(true);
        final List<ProjectStatsEnvelope.RewardStats> maxStats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            maxStats.add(RewardStatsFactory.rewardStats().toBuilder().pledged(i).build());
        }
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, maxStats));
        this.rewardsStatsTruncatedTextIsGone.assertValues(true);
        maxStats.add(RewardStatsFactory.rewardStats().toBuilder().pledged(11).build());
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, maxStats));
        this.rewardsStatsTruncatedTextIsGone.assertValues(true, false);
    }

    @Test
    public void rewardsTitleIsLimitedCopy() {
        setUpEnvironment(environment());
        final Project project = ProjectFactory.project();
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, Collections.singletonList(RewardStatsFactory.rewardStats())));
        this.rewardsTitleIsLimitedCopy.assertValue(false);
        final List<ProjectStatsEnvelope.RewardStats> maxStats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            maxStats.add(RewardStatsFactory.rewardStats().toBuilder().pledged(i).build());
        }
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, maxStats));
        this.rewardsTitleIsLimitedCopy.assertValues(false);
        maxStats.add(RewardStatsFactory.rewardStats().toBuilder().pledged(11).build());
        this.vm.inputs.projectAndRewardStatsInput(Pair.create(project, maxStats));
        this.rewardsTitleIsLimitedCopy.assertValues(false, true);
    }
}

