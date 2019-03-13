package com.kickstarter.viewmodels;


import Project.STATE_CANCELED;
import Project.STATE_LIVE;
import Project.STATE_SUCCESSFUL;
import Project.STATE_SUSPENDED;
import ProjectHolderViewModel.ViewModel;
import R.color.green_alpha_50;
import R.color.ksr_grey_400;
import R.drawable.rect_green_grey_stroke;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.Config;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.libs.utils.ProgressBarUtils;
import com.kickstarter.libs.utils.ProjectUtils;
import com.kickstarter.mock.MockCurrentConfig;
import com.kickstarter.mock.factories.CategoryFactory;
import com.kickstarter.mock.factories.ConfigFactory;
import com.kickstarter.mock.factories.LocationFactory;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.mock.factories.VideoFactory;
import com.kickstarter.models.Category;
import com.kickstarter.models.Location;
import com.kickstarter.models.Photo;
import com.kickstarter.models.Project;
import com.kickstarter.models.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class ProjectHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<String> avatarPhotoUrl = new TestSubscriber();

    private final TestSubscriber<String> backersCountTextViewText = new TestSubscriber();

    private final TestSubscriber<Boolean> backingViewGroupIsGone = new TestSubscriber();

    private final TestSubscriber<String> blurbTextViewText = new TestSubscriber();

    private final TestSubscriber<String> categoryTextViewText = new TestSubscriber();

    private final TestSubscriber<String> commentsCountTextViewText = new TestSubscriber();

    private final TestSubscriber<String> creatorNameTextViewText = new TestSubscriber();

    private final TestSubscriber<String> deadlineCountdownTextViewText = new TestSubscriber();

    private final TestSubscriber<String> featuredTextViewRootCategory = new TestSubscriber();

    private final TestSubscriber<Boolean> featuredViewGroupIsGone = new TestSubscriber();

    private final TestSubscriber<String> goalStringForTextView = new TestSubscriber();

    private final TestSubscriber<String> locationTextViewText = new TestSubscriber();

    private final TestSubscriber<Integer> percentageFundedProgress = new TestSubscriber();

    private final TestSubscriber<Boolean> percentageFundedProgressBarIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> playButtonIsGone = new TestSubscriber();

    private final TestSubscriber<String> pledgedTextViewText = new TestSubscriber();

    private final TestSubscriber<DateTime> projectDisclaimerGoalReachedDateTime = new TestSubscriber();

    private final TestSubscriber<Pair<String, DateTime>> projectDisclaimerGoalNotReachedString = new TestSubscriber();

    private final TestSubscriber<Boolean> projectDisclaimerTextViewIsGone = new TestSubscriber();

    private final TestSubscriber<Integer> projectMetadataViewGroupBackgroundDrawableInt = new TestSubscriber();

    private final TestSubscriber<Boolean> projectMetadataViewGroupIsGone = new TestSubscriber();

    private final TestSubscriber<String> projectNameTextViewText = new TestSubscriber();

    private final TestSubscriber<Project> projectOutput = new TestSubscriber();

    private final TestSubscriber<Photo> projectPhoto = new TestSubscriber();

    private final TestSubscriber<Boolean> projectSocialImageViewIsGone = new TestSubscriber();

    private final TestSubscriber<String> projectSocialImageViewUrl = new TestSubscriber();

    private final TestSubscriber<List<User>> projectSocialTextViewFriends = new TestSubscriber();

    private final TestSubscriber<Boolean> projectSocialViewGroupIsGone = new TestSubscriber();

    private final TestSubscriber<Integer> projectStateViewGroupBackgroundColorInt = new TestSubscriber();

    private final TestSubscriber<Boolean> projectStateViewGroupIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> shouldSetDefaultStatsMargins = new TestSubscriber();

    private final TestSubscriber<Void> setCanceledProjectStateView = new TestSubscriber();

    private final TestSubscriber<Void> setProjectSocialClickListener = new TestSubscriber();

    private final TestSubscriber<DateTime> setSuccessfulProjectStateView = new TestSubscriber();

    private final TestSubscriber<Void> setSuspendedProjectStateView = new TestSubscriber();

    private final TestSubscriber<DateTime> setUnsuccessfulProjectStateView = new TestSubscriber();

    private final TestSubscriber<Project> startProjectSocialActivity = new TestSubscriber();

    private final TestSubscriber<String> updatesCountTextViewText = new TestSubscriber();

    private final TestSubscriber<Pair<String, String>> usdConversionPledgedAndGoalText = new TestSubscriber();

    private final TestSubscriber<Boolean> usdConversionTextViewIsGone = new TestSubscriber();

    @Test
    public void testCreatorDataEmits() {
        final Project project = ProjectFactory.project();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "CA"));
        this.avatarPhotoUrl.assertValues(project.creator().avatar().medium());
        this.creatorNameTextViewText.assertValues(project.creator().name());
    }

    @Test
    public void testMetadata_Backing() {
        final Project project = ProjectFactory.project().toBuilder().isBacking(true).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.backingViewGroupIsGone.assertValues(false);
        this.featuredViewGroupIsGone.assertValues(true);
        this.projectMetadataViewGroupBackgroundDrawableInt.assertValues(rect_green_grey_stroke);
    }

    @Test
    public void testMetadata_Backing_Featured() {
        final Project project = ProjectFactory.project().toBuilder().isBacking(true).featuredAt(DateTime.now()).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.backingViewGroupIsGone.assertValues(false);
        this.featuredTextViewRootCategory.assertNoValues();
        this.featuredViewGroupIsGone.assertValues(true);
        this.projectMetadataViewGroupBackgroundDrawableInt.assertValues(rect_green_grey_stroke);
    }

    @Test
    public void testMetadata_Featured() {
        final Category category = CategoryFactory.textilesCategory();
        final Project project = ProjectFactory.project().toBuilder().category(category).featuredAt(DateTime.now()).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.backingViewGroupIsGone.assertValues(true);
        this.featuredTextViewRootCategory.assertValues(category.root().name());
        this.featuredViewGroupIsGone.assertValues(false);
        this.projectMetadataViewGroupBackgroundDrawableInt.assertNoValues();
    }

    @Test
    public void testMetadata_NoMetadata() {
        final Project project = ProjectFactory.project().toBuilder().featuredAt(null).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.backingViewGroupIsGone.assertValues(true);
        this.featuredTextViewRootCategory.assertNoValues();
        this.featuredViewGroupIsGone.assertValues(true);
        this.projectMetadataViewGroupBackgroundDrawableInt.assertNoValues();
        this.projectMetadataViewGroupIsGone.assertValues(true);
    }

    @Test
    public void testPlayButton_Gone() {
        final Project project = ProjectFactory.project().toBuilder().video(null).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.playButtonIsGone.assertValues(true);
    }

    @Test
    public void testPlayButton_Visible() {
        final Project project = ProjectFactory.project().toBuilder().video(VideoFactory.video()).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.playButtonIsGone.assertValues(false);
    }

    @Test
    public void testProgressBar_Visible() {
        final Project project = ProjectFactory.project().toBuilder().state(STATE_LIVE).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.percentageFundedProgress.assertValues(ProgressBarUtils.progress(project.percentageFunded()));
        this.percentageFundedProgressBarIsGone.assertValues(false);
    }

    @Test
    public void testProgressBar_Gone() {
        final Project project = ProjectFactory.project().toBuilder().state(STATE_SUCCESSFUL).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.percentageFundedProgressBarIsGone.assertValues(true);
    }

    @Test
    public void testProjectDataEmits() {
        // Set user's country to US.
        final Config config = ConfigFactory.configForUSUser();
        final Environment environment = environment();
        environment.currentConfig().config(config);
        setUpEnvironment(environment);
        final Category category = CategoryFactory.tabletopGamesCategory();
        final Location location = LocationFactory.unitedStates();
        final Project project = ProjectFactory.project().toBuilder().commentsCount(5000).category(category).location(location).updatesCount(10).build();
        this.vm.inputs.configureWith(Pair.create(project, config.countryCode()));
        this.blurbTextViewText.assertValues(project.blurb());
        this.categoryTextViewText.assertValues(category.name());
        this.commentsCountTextViewText.assertValues("5,000");
        this.goalStringForTextView.assertValueCount(1);// todo: flaky tests

        this.locationTextViewText.assertValues(location.displayableName());
        this.pledgedTextViewText.assertValueCount(1);
        this.projectNameTextViewText.assertValues(project.name());
        this.projectOutput.assertValues(project);
        this.projectPhoto.assertValues(project.photo());
        this.updatesCountTextViewText.assertValues("10");
    }

    @Test
    public void testProjectDisclaimer_GoalReached() {
        final Project project = ProjectFactory.project().toBuilder().state(STATE_LIVE).goal(100.0F).pledged(500.0F).build();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        this.projectDisclaimerGoalReachedDateTime.assertValueCount(1);
        this.projectDisclaimerTextViewIsGone.assertValues(false);
    }

    @Test
    public void testProjectDisclaimer_GoalNotReached() {
        final Project project = ProjectFactory.project().toBuilder().deadline(DateTime.now()).state(STATE_LIVE).goal(100.0F).pledged(50.0F).build();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        this.projectDisclaimerGoalNotReachedString.assertValueCount(1);
        this.projectDisclaimerTextViewIsGone.assertValues(false);
    }

    @Test
    public void testProjectDisclaimer_NoDisclaimer() {
        final Project project = ProjectFactory.successfulProject();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        // Disclaimer is not shown for completed projects.
        this.projectDisclaimerTextViewIsGone.assertValues(true);
    }

    @Test
    public void testProjectSocialView_Clickable() {
        final User myFriend = UserFactory.germanUser();
        final Project project = ProjectFactory.project().toBuilder().friends(Arrays.asList(myFriend, myFriend, myFriend)).build();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        // On click listener should be set for view with > 2 friends.
        this.setProjectSocialClickListener.assertValueCount(1);
        this.projectSocialImageViewIsGone.assertValues(false);
        this.projectSocialImageViewUrl.assertValueCount(1);
        this.projectSocialTextViewFriends.assertValueCount(1);
        this.projectSocialViewGroupIsGone.assertValues(false);
        this.shouldSetDefaultStatsMargins.assertValues(false);
        this.vm.inputs.projectSocialViewGroupClicked();
        this.startProjectSocialActivity.assertValues(project);
    }

    @Test
    public void testProjectSocialView_NoSocial_LoggedIn() {
        final Project project = ProjectFactory.project().toBuilder().friends(Collections.emptyList()).build();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        this.projectSocialImageViewIsGone.assertValues(true);
        this.projectSocialImageViewUrl.assertNoValues();
        this.projectSocialTextViewFriends.assertNoValues();
        this.projectSocialViewGroupIsGone.assertValues(true);
        this.shouldSetDefaultStatsMargins.assertValues(true);
        this.setProjectSocialClickListener.assertNoValues();
    }

    @Test
    public void testProjectSocialView_NoSocial_LoggedOut() {
        final Project project = ProjectFactory.project().toBuilder().friends(null).build();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        this.projectSocialImageViewIsGone.assertValues(true);
        this.projectSocialImageViewUrl.assertNoValues();
        this.projectSocialTextViewFriends.assertNoValues();
        this.projectSocialViewGroupIsGone.assertValues(true);
        this.shouldSetDefaultStatsMargins.assertValues(true);
        this.setProjectSocialClickListener.assertNoValues();
    }

    @Test
    public void testProjectSocialView_NotClickable() {
        final User myFriend = UserFactory.germanUser();
        final Project project = ProjectFactory.project().toBuilder().friends(Collections.singletonList(myFriend)).build();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "US"));
        // On click listener should be not set for view with < 2 friends.
        this.setProjectSocialClickListener.assertNoValues();
        this.projectSocialImageViewIsGone.assertValues(false);
        this.projectSocialImageViewUrl.assertValueCount(1);
        this.projectSocialTextViewFriends.assertValueCount(1);
        this.projectSocialViewGroupIsGone.assertValues(false);
        this.shouldSetDefaultStatsMargins.assertValues(false);
    }

    @Test
    public void testProjectState_Canceled() {
        final Project project = ProjectFactory.project().toBuilder().state(STATE_CANCELED).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.projectStateViewGroupBackgroundColorInt.assertValues(ksr_grey_400);
        this.projectStateViewGroupIsGone.assertValues(false);
        this.setCanceledProjectStateView.assertValueCount(1);
    }

    @Test
    public void testProjectState_Live() {
        final Project project = ProjectFactory.project().toBuilder().state(STATE_LIVE).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.projectStateViewGroupBackgroundColorInt.assertNoValues();
        this.projectStateViewGroupIsGone.assertValues(true);
    }

    @Test
    public void testProjectState_Successful() {
        final DateTime stateChangedAt = DateTime.now();
        final Project project = ProjectFactory.project().toBuilder().state(STATE_SUCCESSFUL).stateChangedAt(stateChangedAt).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.projectStateViewGroupBackgroundColorInt.assertValues(green_alpha_50);
        this.projectStateViewGroupIsGone.assertValues(false);
        this.setSuccessfulProjectStateView.assertValues(stateChangedAt);
    }

    @Test
    public void testProjectState_Suspended() {
        final Project project = ProjectFactory.project().toBuilder().state(STATE_SUSPENDED).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.projectStateViewGroupBackgroundColorInt.assertValues(ksr_grey_400);
        this.projectStateViewGroupIsGone.assertValues(false);
        this.setSuspendedProjectStateView.assertValueCount(1);
    }

    @Test
    public void testProjectState_Unsuccessful() {
        final DateTime stateChangedAt = DateTime.now();
        final Project project = ProjectFactory.project().toBuilder().state(Project.STATE_FAILED).stateChangedAt(stateChangedAt).build();
        setUpEnvironment(environment());
        this.vm.configureWith(Pair.create(project, "US"));
        this.projectStateViewGroupBackgroundColorInt.assertValues(ksr_grey_400);
        this.projectStateViewGroupIsGone.assertValues(false);
        this.setUnsuccessfulProjectStateView.assertValues(stateChangedAt);
    }

    @Test
    public void testProjectStatsEmit() {
        final Project project = ProjectFactory.project();
        setUpEnvironment(environment());
        this.vm.inputs.configureWith(Pair.create(project, "MX"));
        this.backersCountTextViewText.assertValues(NumberUtils.format(project.backersCount()));
        this.deadlineCountdownTextViewText.assertValues(NumberUtils.format(ProjectUtils.deadlineCountdownValue(project)));
    }

    @Test
    public void testUsdConversionForNonUSProject() {
        // Use a CA project with a MX$ currency
        final Project project = ProjectFactory.mxCurrencyCAProject();
        final Config config = ConfigFactory.configForUSUser();
        final MockCurrentConfig currentConfig = new MockCurrentConfig();
        currentConfig.config(config);
        // Set the current config for a US user. KSCurrency needs this config for conversions.
        setUpEnvironment(environment().toBuilder().ksCurrency(new com.kickstarter.libs.KSCurrency(currentConfig)).build());
        this.vm.inputs.configureWith(Pair.create(project, config.countryCode()));
        // USD conversion shown for non US project.
        this.usdConversionPledgedAndGoalText.assertValueCount(1);
        this.usdConversionTextViewIsGone.assertValue(false);
    }

    @Test
    public void testUsdConversionNotShownForUSProject() {
        final Project project = ProjectFactory.project().toBuilder().country("US").build();
        final Config config = ConfigFactory.configForUSUser();
        final MockCurrentConfig currentConfig = new MockCurrentConfig();
        currentConfig.config(config);
        setUpEnvironment(environment().toBuilder().ksCurrency(new com.kickstarter.libs.KSCurrency(currentConfig)).build());
        this.vm.inputs.configureWith(Pair.create(project, config.countryCode()));
        // USD conversion not shown for US project.
        this.usdConversionTextViewIsGone.assertValue(true);
    }
}

