package com.kickstarter.viewmodels;


import IntentKey.PROJECT;
import IntentKey.PROJECT_PARAM;
import IntentKey.UPDATE;
import KoalaEvent.OPENED_EXTERNAL_LINK;
import UpdateViewModel.ViewModel;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UpdateFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.models.Project;
import com.kickstarter.models.Update;
import com.kickstarter.services.ApiClientType;
import okhttp3.Request;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public final class UpdateViewModelTest extends KSRobolectricTestCase {
    private final Intent defaultIntent = new Intent().putExtra(PROJECT, ProjectFactory.project()).putExtra(UPDATE, UpdateFactory.update());

    @Test
    public void testUpdateViewModel_ExternalLinkActivated() {
        final Project project = ProjectFactory.project().toBuilder().slug("meatballs").build();
        final MockApiClient client = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Project> fetchProject(@NonNull
            final String param) {
                return Observable.just(project);
            }
        };
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment().toBuilder().apiClient(client).build());
        // Start the intent with a project param and update.
        vm.intent(new Intent().putExtra(PROJECT_PARAM, "meatballs").putExtra(UPDATE, UpdateFactory.update()));
        vm.inputs.externalLinkActivated();
        this.koalaTest.assertValues(OPENED_EXTERNAL_LINK);
    }

    @Test
    public void testUpdateViewModel_LoadsWebViewUrl() {
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment());
        final Update update = UpdateFactory.update();
        final String anotherUpdateUrl = "https://kck.str/projects/param/param/posts/next-id";
        final Request anotherUpdateRequest = new Request.Builder().url(anotherUpdateUrl).build();
        final TestSubscriber<String> webViewUrl = new TestSubscriber();
        vm.outputs.webViewUrl().subscribe(webViewUrl);
        // Start the intent with a project and update.
        vm.intent(this.defaultIntent);
        // Initial update's url emits.
        webViewUrl.assertValues(update.urls().web().update());
        // Make a request for another update.
        vm.inputs.goToUpdateRequest(anotherUpdateRequest);
        // New update url emits.
        webViewUrl.assertValues(update.urls().web().update(), anotherUpdateUrl);
    }

    @Test
    public void testUpdateViewModel_StartCommentsActivity() {
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment());
        final Update update = UpdateFactory.update();
        final Request commentsRequest = new Request.Builder().url("https://kck.str/projects/param/param/posts/id/comments").build();
        final TestSubscriber<Update> startCommentsActivity = new TestSubscriber();
        vm.outputs.startCommentsActivity().subscribe(startCommentsActivity);
        // Start the intent with a project and update.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()).putExtra(UPDATE, update));
        vm.inputs.goToCommentsRequest(commentsRequest);
        startCommentsActivity.assertValues(update);
    }

    @Test
    public void testUpdateViewModel_StartProjectActivity() {
        final Update update = UpdateFactory.update().toBuilder().projectId(1234).build();
        final Project project = ProjectFactory.project().toBuilder().id(update.projectId()).build();
        final Request projectRequest = new Request.Builder().url("https://kck.str/projects/param/param").build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Project> fetchProject(@NonNull
            final String param) {
                return Observable.just(project);
            }
        };
        final Environment environment = environment().toBuilder().apiClient(apiClient).build();
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment);
        final TestSubscriber<Project> startProjectActivity = new TestSubscriber();
        vm.outputs.startProjectActivity().map(( pr) -> pr.first).subscribe(startProjectActivity);
        // Start the intent with a project and update.
        vm.intent(new Intent().putExtra(PROJECT, project).putExtra(UPDATE, update));
        vm.inputs.goToProjectRequest(projectRequest);
        startProjectActivity.assertValues(project);
    }

    @Test
    public void testUpdateViewModel_StartShareIntent() {
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment());
        final Update update = UpdateFactory.update();
        final TestSubscriber<Update> startShareIntent = new TestSubscriber();
        vm.outputs.startShareIntent().subscribe(startShareIntent);
        // Start the intent with a project and update.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()).putExtra(UPDATE, update));
        vm.inputs.shareIconButtonClicked();
        startShareIntent.assertValues(update);
    }

    @Test
    public void testUpdateViewModel_UpdateSequence() {
        final Update initialUpdate = UpdateFactory.update().toBuilder().sequence(1).build();
        final Update anotherUpdate = UpdateFactory.update().toBuilder().sequence(2).build();
        final Request anotherUpdateRequest = new Request.Builder().url("https://kck.str/projects/param/param/posts/id").build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Update> fetchUpdate(@NonNull
            final String projectParam, @NonNull
            final String updateParam) {
                return Observable.just(anotherUpdate);
            }
        };
        final Environment environment = environment().toBuilder().apiClient(apiClient).build();
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment);
        final TestSubscriber<String> updateSequence = new TestSubscriber();
        vm.outputs.updateSequence().subscribe(updateSequence);
        // Start the intent with a project and update.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()).putExtra(UPDATE, initialUpdate));
        // Initial update's sequence number emits.
        updateSequence.assertValues(NumberUtils.format(initialUpdate.sequence()));
        vm.inputs.goToUpdateRequest(anotherUpdateRequest);
        // New sequence should emit for new update page.
        updateSequence.assertValues(NumberUtils.format(initialUpdate.sequence()), NumberUtils.format(anotherUpdate.sequence()));
    }

    @Test
    public void testUpdateViewModel_WebViewUrl() {
        final UpdateViewModel.ViewModel vm = new UpdateViewModel.ViewModel(environment());
        final Update update = UpdateFactory.update();
        final TestSubscriber<String> webViewUrl = new TestSubscriber();
        vm.outputs.webViewUrl().subscribe(webViewUrl);
        // Start the intent with a project and update.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()).putExtra(UPDATE, update));
        // Initial update index url emits.
        webViewUrl.assertValues(update.urls().web().update());
    }
}

