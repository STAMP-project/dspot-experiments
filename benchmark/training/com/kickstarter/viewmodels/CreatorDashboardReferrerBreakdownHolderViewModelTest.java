package com.kickstarter.viewmodels;


import CreatorDashboardReferrerBreakdownHolderViewModel.ViewModel;
import ProjectStatsEnvelope.CumulativeStats;
import ProjectStatsEnvelope.ReferralAggregateStats;
import ProjectStatsEnvelopeFactory.CumulativeStatsFactory;
import ProjectStatsEnvelopeFactory.ReferralAggregateStatsFactory;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.ProjectStatsEnvelopeFactory;
import com.kickstarter.models.Project;
import com.kickstarter.services.apiresponses.ProjectStatsEnvelope;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class CreatorDashboardReferrerBreakdownHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Boolean> breakdownViewIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> emptyViewIsGone = new TestSubscriber();

    private final TestSubscriber<Float> customReferrerPercent = new TestSubscriber();

    private final TestSubscriber<Float> externalReferrerPercent = new TestSubscriber();

    private final TestSubscriber<Float> kickstarterReferrerPercent = new TestSubscriber();

    private final TestSubscriber<String> customReferrerPercentText = new TestSubscriber();

    private final TestSubscriber<String> externalReferrerPercentText = new TestSubscriber();

    private final TestSubscriber<String> kickstarterReferrerPercentText = new TestSubscriber();

    private final TestSubscriber<Boolean> pledgedViaCustomLayoutIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> pledgedViaExternalLayoutIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> pledgedViaKickstarterLayoutIsGone = new TestSubscriber();

    private final TestSubscriber<Pair<Project, Integer>> projectAndAveragePledge = new TestSubscriber();

    private final TestSubscriber<Pair<Project, Float>> projectAndCustomReferrerPledgedAmount = new TestSubscriber();

    private final TestSubscriber<Pair<Project, Float>> projectAndExternalReferrerPledgedAmount = new TestSubscriber();

    private final TestSubscriber<Pair<Project, Float>> projectAndKickstarterReferrerPledgedAmount = new TestSubscriber();

    @Test
    public void testBreakdownViewIsGone_isTrue_whenStatsEmpty() {
        setUpEnvironmentAndInputProjectAndEmptyStats();
        this.breakdownViewIsGone.assertValues(true);
    }

    @Test
    public void testBreakdownViewIsGone_isFalse_whenStatsNotEmpty() {
        setUpEnvironmentAndInputProjectAndStats();
        this.breakdownViewIsGone.assertValues(false);
    }

    @Test
    public void testEmptyViewIsGone_isFalse_whenStatsEmpty() {
        setUpEnvironmentAndInputProjectAndEmptyStats();
        this.emptyViewIsGone.assertValues(false);
    }

    @Test
    public void testEmptyViewIsGone_isTrue_whenStatsNotEmpty() {
        setUpEnvironmentAndInputProjectAndStats();
        this.emptyViewIsGone.assertValues(true);
    }

    @Test
    public void testCustomReferrerPercent() {
        setUpEnvironmentAndInputProjectAndStats();
        this.customReferrerPercent.assertValues(0.5F);
    }

    @Test
    public void testCustomReferrerPercentText() {
        setUpEnvironmentAndInputProjectAndStats();
        this.customReferrerPercentText.assertValues(NumberUtils.flooredPercentage((0.5F * 100.0F)));
    }

    @Test
    public void testExternalReferrerPercent() {
        setUpEnvironmentAndInputProjectAndStats();
        this.externalReferrerPercent.assertValues(0.25F);
    }

    @Test
    public void testExternalReferrerPercentText() {
        setUpEnvironmentAndInputProjectAndStats();
        this.externalReferrerPercentText.assertValues(NumberUtils.flooredPercentage((0.25F * 100.0F)));
    }

    @Test
    public void testKickstarterReferrerPercent() {
        setUpEnvironmentAndInputProjectAndStats();
        this.kickstarterReferrerPercent.assertValues(0.25F);
    }

    @Test
    public void testKickstarterReferrerPercentText() {
        setUpEnvironmentAndInputProjectAndStats();
        this.kickstarterReferrerPercentText.assertValues(NumberUtils.flooredPercentage((0.25F * 100.0F)));
    }

    @Test
    public void testPledgedViaCustomLayoutIsGone_isTrue_WhenStatsEmpty() {
        setUpEnvironmentAndInputProjectAndEmptyStats();
        this.pledgedViaCustomLayoutIsGone.assertValues(true);
    }

    @Test
    public void testPledgedViaCustomLayoutIsGone_isFalse_WhenStatsNotEmpty() {
        setUpEnvironmentAndInputProjectAndStats();
        this.pledgedViaCustomLayoutIsGone.assertValues(false);
    }

    @Test
    public void testPledgedViaExternalLayoutIsGone_isTrue_WhenStatsEmpty() {
        setUpEnvironmentAndInputProjectAndEmptyStats();
        this.pledgedViaExternalLayoutIsGone.assertValues(true);
    }

    @Test
    public void testPledgedViaExternalLayoutIsGone_isFalse_WhenStatsNotEmpty() {
        setUpEnvironmentAndInputProjectAndStats();
        this.pledgedViaExternalLayoutIsGone.assertValues(false);
    }

    @Test
    public void testPledgedViaKickstarterLayoutIsGone_isTrue_WhenStatsEmpty() {
        setUpEnvironmentAndInputProjectAndEmptyStats();
        this.pledgedViaKickstarterLayoutIsGone.assertValues(true);
    }

    @Test
    public void testPledgedViaKickstarterLayoutIsGone_isFalse_WhenStatsNotEmpty() {
        setUpEnvironmentAndInputProjectAndStats();
        this.pledgedViaKickstarterLayoutIsGone.assertValues(false);
    }

    @Test
    public void testProjectAndAveragePledge() {
        final Project project = ProjectFactory.project();
        final ProjectStatsEnvelope.CumulativeStats cumulativeStats = CumulativeStatsFactory.cumulativeStats().toBuilder().averagePledge(10.0F).build();
        final ProjectStatsEnvelope statsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope().toBuilder().cumulative(cumulativeStats).build();
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStatsInput(Pair.create(project, statsEnvelope));
        this.projectAndAveragePledge.assertValue(Pair.create(project, 10));
    }

    @Test
    public void testProjectAndCustomReferrerPledgedAmount() {
        final Project project = setUpEnvironmentAndInputProjectAndStats();
        this.projectAndCustomReferrerPledgedAmount.assertValue(Pair.create(project, 100.0F));
    }

    @Test
    public void testProjectAndExternalReferrerPledgedAmount() {
        final Project project = setUpEnvironmentAndInputProjectAndStats();
        this.projectAndExternalReferrerPledgedAmount.assertValue(Pair.create(project, 50.0F));
    }

    @Test
    public void testProjectAndKickstarterReferrerPledgedAmount() {
        final Project project = setUpEnvironmentAndInputProjectAndStats();
        this.projectAndKickstarterReferrerPledgedAmount.assertValue(Pair.create(project, 50.0F));
    }

    @Test
    public void testReferrerPercents() {
        final Project project = ProjectFactory.project();
        final ProjectStatsEnvelope.ReferralAggregateStats referralAggregateStats = ReferralAggregateStatsFactory.referralAggregates().toBuilder().custom(100).internal(50).external(50).build();
        final ProjectStatsEnvelope.CumulativeStats cumulativeStats = CumulativeStatsFactory.cumulativeStats().toBuilder().pledged(200).build();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope().toBuilder().referralAggregates(referralAggregateStats).cumulative(cumulativeStats).build();
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStatsInput(Pair.create(project, projectStatsEnvelope));
        this.customReferrerPercent.assertValues(0.5F);
        this.externalReferrerPercent.assertValues(0.25F);
        this.kickstarterReferrerPercent.assertValues(0.25F);
    }
}

