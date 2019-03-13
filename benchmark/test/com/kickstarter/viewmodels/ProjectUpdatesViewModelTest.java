package com.kickstarter.viewmodels;


import IntentKey.PROJECT;
import KoalaEvent.OPENED_EXTERNAL_LINK;
import KoalaEvent.VIEWED_UPDATE;
import KoalaEvent.VIEWED_UPDATES;
import ProjectUpdatesViewModel.ViewModel;
import android.content.Intent;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.models.Project;
import com.kickstarter.models.Update;
import okhttp3.Request;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class ProjectUpdatesViewModelTest extends KSRobolectricTestCase {
    @Test
    public void testProjectUpdatesViewModel_ExternalLinkActivated() {
        final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
        final Project project = ProjectFactory.project();
        final TestSubscriber<String> webViewUrl = new TestSubscriber();
        vm.outputs.webViewUrl().subscribe(webViewUrl);
        vm.intent(new Intent().putExtra(PROJECT, project));
        // Initial updates index url is loaded.
        webViewUrl.assertValueCount(1);
        // Activate an external link.
        vm.inputs.externalLinkActivated();
        // External url is not loaded in our web view.
        webViewUrl.assertValueCount(1);
        koalaTest.assertValues(VIEWED_UPDATES, OPENED_EXTERNAL_LINK);
    }

    @Test
    public void testProjectUpdatesViewModel_LoadsWebViewUrl() {
        final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
        final Project project = ProjectFactory.project();
        final String anotherIndexUrl = "https://kck.str/projects/param/param/posts?page=another";
        final Request anotherIndexRequest = new Request.Builder().url(anotherIndexUrl).build();
        final TestSubscriber<String> webViewUrl = new TestSubscriber();
        vm.outputs.webViewUrl().subscribe(webViewUrl);
        // Start the intent with a project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        // Initial project updates index emits.
        webViewUrl.assertValues(project.updatesUrl());
        koalaTest.assertValues(VIEWED_UPDATES);
        // Make a request for another update index.
        vm.inputs.goToUpdatesRequest(anotherIndexRequest);
        // New updates index url emits. Event is not tracked again.
        webViewUrl.assertValues(project.updatesUrl(), anotherIndexUrl);
        koalaTest.assertValues(VIEWED_UPDATES);
    }

    @Test
    public void testProjectUpdatesViewModel_StartCommentsActivity() {
        final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
        final Project project = ProjectFactory.project();
        final Request commentsRequest = new Request.Builder().url("https://kck.str/projects/param/param/posts/id/comments").build();
        final TestSubscriber<Update> startCommentsActivity = new TestSubscriber();
        vm.outputs.startCommentsActivity().subscribe(startCommentsActivity);
        vm.intent(new Intent().putExtra(PROJECT, project));
        vm.inputs.goToCommentsRequest(commentsRequest);
        startCommentsActivity.assertValueCount(1);
    }

    @Test
    public void testProjectUpdatesViewModel_StartUpdateActivity() {
        final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
        final Project project = ProjectFactory.project();
        final Request updateRequest = new Request.Builder().url("https://kck.str/projects/param/param/posts/id").build();
        final TestSubscriber<Pair<Project, Update>> startUpdateActivity = new TestSubscriber();
        vm.outputs.startUpdateActivity().subscribe(startUpdateActivity);
        vm.intent(new Intent().putExtra(PROJECT, project));
        vm.inputs.goToUpdateRequest(updateRequest);
        startUpdateActivity.assertValueCount(1);
        koalaTest.assertValues(VIEWED_UPDATES, VIEWED_UPDATE);
    }
}

