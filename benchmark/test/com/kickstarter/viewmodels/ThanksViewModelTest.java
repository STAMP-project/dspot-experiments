package com.kickstarter.viewmodels;


import IntentKey.PROJECT;
import ThanksViewModel.ViewModel;
import android.content.Intent;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.CurrentUserType;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.MockCurrentUser;
import com.kickstarter.libs.RefTag;
import com.kickstarter.libs.preferences.MockBooleanPreference;
import com.kickstarter.mock.factories.CategoryFactory;
import com.kickstarter.mock.factories.LocationFactory;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.models.Category;
import com.kickstarter.models.Project;
import com.kickstarter.models.User;
import com.kickstarter.services.DiscoveryParams;
import com.kickstarter.ui.adapters.data.ThanksData;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class ThanksViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<ThanksData> adapterData = new TestSubscriber();

    private final TestSubscriber<Void> showGamesNewsletterDialogTest = new TestSubscriber();

    private final TestSubscriber<Void> showRatingDialogTest = new TestSubscriber();

    private final TestSubscriber<Void> showConfirmGamesNewsletterDialogTest = TestSubscriber.create();

    private final TestSubscriber<DiscoveryParams> startDiscoveryTest = new TestSubscriber();

    private final TestSubscriber<Pair<Project, RefTag>> startProjectTest = new TestSubscriber();

    @Test
    public void testThanksViewModel_adapterData() {
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.artCategory()).build();
        setUpEnvironment(environment());
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.adapterData.assertValueCount(1);
    }

    @Test
    public void testThanksViewModel_showRatingDialog() {
        final MockBooleanPreference hasSeenAppRatingPreference = new MockBooleanPreference(false);
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(true);
        final Environment environment = environment().toBuilder().hasSeenAppRatingPreference(hasSeenAppRatingPreference).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        this.vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()));
        this.showRatingDialogTest.assertValueCount(1);
    }

    @Test
    public void testThanksViewModel_dontShowRatingDialogIfAlreadySeen() {
        final MockBooleanPreference hasSeenAppRatingPreference = new MockBooleanPreference(true);
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(true);
        final Environment environment = environment().toBuilder().hasSeenAppRatingPreference(hasSeenAppRatingPreference).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        this.vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()));
        this.showRatingDialogTest.assertValueCount(0);
    }

    @Test
    public void testThanksViewModel_dontShowRatingDialogIfGamesNewsletterWillDisplay() {
        final MockBooleanPreference hasSeenAppRatingPreference = new MockBooleanPreference(false);
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(false);
        final User user = UserFactory.user().toBuilder().gamesNewsletter(false).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.tabletopGamesCategory()).build();
        final Environment environment = environment().toBuilder().currentUser(currentUser).hasSeenAppRatingPreference(hasSeenAppRatingPreference).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.showRatingDialogTest.assertValueCount(0);
    }

    @Test
    public void testThanksViewModel_showGamesNewsletterDialog() {
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(false);
        final User user = UserFactory.user().toBuilder().gamesNewsletter(false).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Environment environment = environment().toBuilder().currentUser(currentUser).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.tabletopGamesCategory()).build();
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.showGamesNewsletterDialogTest.assertValueCount(1);
        TestCase.assertEquals(Arrays.asList(false, true), hasSeenGamesNewsletterPreference.values());
        this.koalaTest.assertValueCount(0);
    }

    @Test
    public void testThanksViewModel_dontShowGamesNewsletterDialogIfRootCategoryIsNotGames() {
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(false);
        final User user = UserFactory.user().toBuilder().gamesNewsletter(false).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Environment environment = environment().toBuilder().currentUser(currentUser).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.ceramicsCategory()).build();
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.showGamesNewsletterDialogTest.assertValueCount(0);
    }

    @Test
    public void testThanksViewModel_dontShowGamesNewsletterDialogIfUserHasAlreadySeen() {
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(true);
        final User user = UserFactory.user().toBuilder().gamesNewsletter(false).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Environment environment = environment().toBuilder().currentUser(currentUser).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.tabletopGamesCategory()).build();
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.showGamesNewsletterDialogTest.assertValueCount(0);
    }

    @Test
    public void testThanksViewModel_dontShowGamesNewsletterDialogIfUserHasAlreadySignedUp() {
        final MockBooleanPreference hasSeenGamesNewsletterPreference = new MockBooleanPreference(false);
        final User user = UserFactory.user().toBuilder().gamesNewsletter(true).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Environment environment = environment().toBuilder().currentUser(currentUser).hasSeenGamesNewsletterPreference(hasSeenGamesNewsletterPreference).build();
        setUpEnvironment(environment);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.tabletopGamesCategory()).build();
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.showGamesNewsletterDialogTest.assertValueCount(0);
    }

    @Test
    public void testThanksViewModel_signupToGamesNewsletterOnClick() {
        final User user = UserFactory.user().toBuilder().gamesNewsletter(false).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Environment environment = environment().toBuilder().currentUser(currentUser).build();
        setUpEnvironment(environment);
        final TestSubscriber<User> updateUserSettingsTest = new TestSubscriber();
        observable().filter(( e) -> "update_user_settings".equals(e.first)).map(( e) -> ((User) (e.second.get("user")))).subscribe(updateUserSettingsTest);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.tabletopGamesCategory()).build();
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.vm.signupToGamesNewsletterClick();
        updateUserSettingsTest.assertValues(user.toBuilder().gamesNewsletter(true).build());
        this.showConfirmGamesNewsletterDialogTest.assertValueCount(0);
        this.koalaTest.assertValues("Newsletter Subscribe");
    }

    @Test
    public void testThanksViewModel_showNewsletterConfirmationPromptAfterSignupForGermanUser() {
        final User user = UserFactory.user().toBuilder().gamesNewsletter(false).location(LocationFactory.germany()).build();
        final CurrentUserType currentUser = new MockCurrentUser(user);
        final Environment environment = environment().toBuilder().currentUser(currentUser).build();
        setUpEnvironment(environment);
        final Project project = ProjectFactory.project().toBuilder().category(CategoryFactory.tabletopGamesCategory()).build();
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.vm.signupToGamesNewsletterClick();
        this.showConfirmGamesNewsletterDialogTest.assertValueCount(1);
        this.koalaTest.assertValues("Newsletter Subscribe");
    }

    @Test
    public void testThanksViewModel_startDiscovery() {
        setUpEnvironment(environment());
        final Category category = CategoryFactory.category();
        this.vm.inputs.categoryViewHolderClicked(category);
        this.startDiscoveryTest.assertValues(DiscoveryParams.builder().category(category).build());
        this.koalaTest.assertValue("Checkout Finished Discover More");
    }

    @Test
    public void testThanksViewModel_startProject() {
        setUpEnvironment(environment());
        final Project project = ProjectFactory.project();
        this.vm.inputs.projectCardViewHolderClicked(project);
        this.startProjectTest.assertValues(Pair.create(project, RefTag.thanks()));
        this.koalaTest.assertValue("Checkout Finished Discover Open Project");
    }
}

