package com.kickstarter.viewmodels;


import CreatorDashboardHeaderHolderViewModel.ViewModel;
import Project.STATE_CANCELED;
import Project.STATE_FAILED;
import Project.STATE_LIVE;
import Project.STATE_STARTED;
import Project.STATE_SUBMITTED;
import Project.STATE_SUCCESSFUL;
import Project.STATE_SUSPENDED;
import R.drawable.progress_bar_green_horizontal;
import R.drawable.progress_bar_grey_horizontal;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.CurrentUserType;
import com.kickstarter.libs.MockCurrentUser;
import com.kickstarter.libs.RefTag;
import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.libs.utils.ProgressBarUtils;
import com.kickstarter.libs.utils.ProjectUtils;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.ProjectStatsEnvelopeFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.models.Project;
import com.kickstarter.models.User;
import com.kickstarter.services.apiresponses.ProjectStatsEnvelope;
import org.joda.time.DateTime;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class CreatorDashboardHeaderHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Boolean> messagesButtonIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> otherProjectsButtonIsGone = new TestSubscriber();

    private final TestSubscriber<String> percentageFunded = new TestSubscriber();

    private final TestSubscriber<Integer> percentageFundedProgress = new TestSubscriber();

    private final TestSubscriber<String> projectBackersCountText = new TestSubscriber();

    private final TestSubscriber<String> projectNameTextViewText = new TestSubscriber();

    private final TestSubscriber<Integer> progressBarBackground = new TestSubscriber();

    private final TestSubscriber<String> timeRemainingText = new TestSubscriber();

    private final TestSubscriber<Pair<Project, RefTag>> startMessageThreadsActivity = new TestSubscriber();

    private final TestSubscriber<Pair<Project, RefTag>> startProjectActivity = new TestSubscriber();

    @Test
    public void testMessagesButtonIsGone() {
        final User creator = UserFactory.creator();
        final CurrentUserType currentUser = new MockCurrentUser(UserFactory.collaborator());
        final Project project = ProjectFactory.project().toBuilder().creator(creator).build();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        setUpEnvironment(environment().toBuilder().currentUser(currentUser).build());
        this.vm.inputs.projectAndStats(Pair.create(project, projectStatsEnvelope));
        // Messages button is gone if current user is not the project creator (e.g. a collaborator).
        this.messagesButtonIsGone.assertValue(true);
    }

    @Test
    public void testOtherProjectsButtonIsGone_isTrue_WhenCollaboratorHas1Project() {
        final User collaboratorWith1Project = UserFactory.collaborator().toBuilder().memberProjectsCount(1).build();
        final CurrentUserType collaborator = new MockCurrentUser(collaboratorWith1Project);
        setUpEnvironment(environment().toBuilder().currentUser(collaborator).build());
        this.vm.inputs.projectAndStats(Pair.create(ProjectFactory.project(), ProjectStatsEnvelopeFactory.projectStatsEnvelope()));
        this.otherProjectsButtonIsGone.assertValue(true);
    }

    @Test
    public void testOtherProjectsButtonIsGone_isFalse_WhenCollaboratorHasManyProjects() {
        final CurrentUserType collaborator = new MockCurrentUser(UserFactory.collaborator());
        setUpEnvironment(environment().toBuilder().currentUser(collaborator).build());
        this.vm.inputs.projectAndStats(Pair.create(ProjectFactory.project(), ProjectStatsEnvelopeFactory.projectStatsEnvelope()));
        this.otherProjectsButtonIsGone.assertValue(false);
    }

    @Test
    public void testProjectBackersCountText() {
        final Project project = ProjectFactory.project().toBuilder().backersCount(10).build();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(Pair.create(project, projectStatsEnvelope));
        this.projectBackersCountText.assertValue("10");
    }

    @Test
    public void testProjectNameTextViewText() {
        final Project project = ProjectFactory.project().toBuilder().name("somebody once told me").build();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(Pair.create(project, projectStatsEnvelope));
        this.projectNameTextViewText.assertValue("somebody once told me");
    }

    @Test
    public void testPercentageFunded() {
        setUpEnvironment(environment());
        final Project project = ProjectFactory.project();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        this.vm.inputs.projectAndStats(Pair.create(project, projectStatsEnvelope));
        final String percentageFundedOutput = NumberUtils.flooredPercentage(project.percentageFunded());
        this.percentageFunded.assertValues(percentageFundedOutput);
        final int percentageFundedProgressOutput = ProgressBarUtils.progress(project.percentageFunded());
        this.percentageFundedProgress.assertValue(percentageFundedProgressOutput);
    }

    @Test
    public void testProgressBarBackground_LiveProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_LIVE));
        this.progressBarBackground.assertValue(progress_bar_green_horizontal);
    }

    @Test
    public void testProgressBarBackground_SubmittedProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_SUBMITTED));
        this.progressBarBackground.assertValue(progress_bar_green_horizontal);
    }

    @Test
    public void testProgressBarBackground_StartedProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_STARTED));
        this.progressBarBackground.assertValue(progress_bar_green_horizontal);
    }

    @Test
    public void testProgressBarBackground_SuccessfulProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_SUCCESSFUL));
        this.progressBarBackground.assertValue(progress_bar_green_horizontal);
    }

    @Test
    public void testProgressBarBackground_FailedProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_FAILED));
        this.progressBarBackground.assertValue(progress_bar_grey_horizontal);
    }

    @Test
    public void testProgressBarBackground_CanceledProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_CANCELED));
        this.progressBarBackground.assertValue(progress_bar_grey_horizontal);
    }

    @Test
    public void testProgressBarBackground_SuspendedProject() {
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(getProjectAndStats(STATE_SUSPENDED));
        this.progressBarBackground.assertValue(progress_bar_grey_horizontal);
    }

    @Test
    public void testStartMessagesActivity() {
        final User creator = UserFactory.creator();
        final CurrentUserType currentUser = new MockCurrentUser(creator);
        final Project project = ProjectFactory.project().toBuilder().creator(creator).build();
        setUpEnvironment(environment().toBuilder().currentUser(currentUser).build());
        this.vm.inputs.projectAndStats(Pair.create(project, ProjectStatsEnvelopeFactory.projectStatsEnvelope()));
        this.vm.inputs.messagesButtonClicked();
        // Messages button is shown to project creator, messages activity starts.
        this.messagesButtonIsGone.assertValues(false);
        this.startMessageThreadsActivity.assertValue(Pair.create(project, RefTag.dashboard()));
    }

    @Test
    public void testStartProjectActivity() {
        final Project project = ProjectFactory.project();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        setUpEnvironment(environment());
        this.vm.inputs.projectAndStats(Pair.create(project, projectStatsEnvelope));
        this.vm.inputs.projectButtonClicked();
        this.startProjectActivity.assertValue(Pair.create(project, RefTag.dashboard()));
    }

    @Test
    public void testTimeRemainingText() {
        setUpEnvironment(environment());
        final Project project = ProjectFactory.project().toBuilder().deadline(new DateTime().plusDays(10)).build();
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        final int deadlineVal = ProjectUtils.deadlineCountdownValue(project);
        this.vm.inputs.projectAndStats(Pair.create(project, projectStatsEnvelope));
        this.timeRemainingText.assertValue(NumberUtils.format(deadlineVal));
    }
}

