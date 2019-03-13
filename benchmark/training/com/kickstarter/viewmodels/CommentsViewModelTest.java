package com.kickstarter.viewmodels;


import CommentsViewModel.ViewModel;
import IntentKey.PROJECT;
import IntentKey.UPDATE;
import KoalaEvent.LOADED_OLDER_COMMENTS;
import KoalaEvent.POSTED_COMMENT;
import KoalaEvent.PROJECT_COMMENT_CREATE;
import KoalaEvent.PROJECT_COMMENT_LOAD_OLDER;
import KoalaEvent.PROJECT_COMMENT_VIEW;
import KoalaEvent.VIEWED_COMMENTS;
import android.content.Intent;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.CurrentUserType;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.MockCurrentUser;
import com.kickstarter.mock.factories.ApiExceptionFactory;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UpdateFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.models.Comment;
import com.kickstarter.models.Project;
import com.kickstarter.models.Update;
import com.kickstarter.models.User;
import com.kickstarter.services.ApiClientType;
import com.kickstarter.services.apiresponses.CommentsEnvelope;
import com.kickstarter.ui.adapters.data.CommentsData;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class CommentsViewModelTest extends KSRobolectricTestCase {
    @Test
    public void testCommentsViewModel_EmptyState() {
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<CommentsEnvelope> fetchComments(@NonNull
            final Update update) {
                return Observable.empty();
            }
        };
        final Environment env = environment().toBuilder().apiClient(apiClient).build();
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(env);
        final TestSubscriber<CommentsData> commentsData = new TestSubscriber();
        vm.outputs.commentsData().subscribe(commentsData);
        // Start the view model with an update.
        vm.intent(new Intent().putExtra(UPDATE, UpdateFactory.update()));
        // Only Viewed Comments event should fire.
        koalaTest.assertValues(VIEWED_COMMENTS);
        commentsData.assertNoValues();
    }

    @Test
    public void testCommentsViewModel_LoadMoreProjectComments() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()));
        koalaTest.assertValues(VIEWED_COMMENTS, PROJECT_COMMENT_VIEW);
        // Paginate for older comments.
        vm.inputs.nextPage();
        // Modern and deprecated events emit for loading older project comments.
        koalaTest.assertValues(VIEWED_COMMENTS, PROJECT_COMMENT_VIEW, LOADED_OLDER_COMMENTS, PROJECT_COMMENT_LOAD_OLDER);
    }

    @Test
    public void testCommentsViewModel_LoadMoreUpdateComments() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        // Start the view model with an update.
        vm.intent(new Intent().putExtra(UPDATE, UpdateFactory.update()));
        // Load older comments event should not fire yet.
        koalaTest.assertValues(VIEWED_COMMENTS);
        // Paginate for older comments.
        vm.inputs.nextPage();
        koalaTest.assertValues(VIEWED_COMMENTS, LOADED_OLDER_COMMENTS);
    }

    @Test
    public void testCommentsViewModel_ProjectCommentsEmit() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        final TestSubscriber<List<Comment>> comments = new TestSubscriber();
        vm.outputs.commentsData().map(CommentsData::comments).subscribe(comments);
        final TestSubscriber<Boolean> isFetchingComments = new TestSubscriber();
        vm.outputs.isFetchingComments().subscribe(isFetchingComments);
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.project()));
        koalaTest.assertValues(VIEWED_COMMENTS, PROJECT_COMMENT_VIEW);
        // Comments should emit.
        comments.assertValueCount(1);
        isFetchingComments.assertValues(true, false);
    }

    @Test
    public void testCommentsViewModel_postCommentError() {
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Comment> postComment(@NonNull
            final Project project, @NonNull
            final String body) {
                return Observable.error(ApiExceptionFactory.badRequestException());
            }
        };
        final Environment env = environment().toBuilder().apiClient(apiClient).build();
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(env);
        final TestSubscriber<String> showPostCommentErrorToast = new TestSubscriber();
        vm.outputs.showPostCommentErrorToast().subscribe(showPostCommentErrorToast);
        final TestSubscriber<Void> showCommentPostedToast = new TestSubscriber();
        vm.outputs.showCommentPostedToast().subscribe(showCommentPostedToast);
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.backedProject()));
        // Click the comment button and write a comment.
        vm.inputs.commentButtonClicked();
        vm.inputs.commentBodyChanged("Mic check mic check.");
        // Post comment. Error should be shown. Comment posted toast should not be shown.
        vm.inputs.postCommentClicked();
        showPostCommentErrorToast.assertValueCount(1);
        showCommentPostedToast.assertNoValues();
    }

    @Test
    public void testCommentsViewModel_postCommentFlow() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        final Project project = ProjectFactory.backedProject();
        final TestSubscriber<Void> showCommentPostedToastTest = new TestSubscriber();
        vm.outputs.showCommentPostedToast().subscribe(showCommentPostedToastTest);
        final TestSubscriber<Void> dismissCommentDialogTest = new TestSubscriber();
        vm.outputs.dismissCommentDialog().subscribe(dismissCommentDialogTest);
        final TestSubscriber<Boolean> postButtonIsEnabledTest = new TestSubscriber();
        vm.outputs.enablePostButton().subscribe(postButtonIsEnabledTest);
        final TestSubscriber<Boolean> commentButtonHidden = new TestSubscriber();
        vm.outputs.commentButtonHidden().subscribe(commentButtonHidden);
        final TestSubscriber<Pair<Project, Boolean>> showCommentDialogTest = new TestSubscriber();
        vm.outputs.showCommentDialog().subscribe(showCommentDialogTest);
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        koalaTest.assertValues(VIEWED_COMMENTS, PROJECT_COMMENT_VIEW);
        // Comment button should be shown.
        commentButtonHidden.assertValue(false);
        // Click comment button. Comment dialog should be shown.
        vm.inputs.commentButtonClicked();
        showCommentDialogTest.assertValue(Pair.create(project, true));
        // Write a comment. The post button should be enabled with valid comment body.
        vm.inputs.commentBodyChanged("");
        postButtonIsEnabledTest.assertValues(false);
        vm.inputs.commentBodyChanged("Some comment");
        postButtonIsEnabledTest.assertValues(false, true);
        // Post comment. Dialog should be dismissed.
        vm.inputs.postCommentClicked();
        dismissCommentDialogTest.assertValueCount(1);
        // Comment posted toast should be shown.
        showCommentPostedToastTest.assertValueCount(1);
        // A koala event for commenting should be tracked.
        koalaTest.assertValues(VIEWED_COMMENTS, PROJECT_COMMENT_VIEW, POSTED_COMMENT, PROJECT_COMMENT_CREATE);
    }

    @Test
    public void testCommentsViewModel_loggedOutShowDialogFlow() {
        final CurrentUserType currentUser = new MockCurrentUser(UserFactory.user());
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment().toBuilder().currentUser(currentUser).build());
        final Project project = ProjectFactory.backedProject();
        final TestSubscriber<Pair<Project, Boolean>> showCommentDialogTest = new TestSubscriber();
        vm.outputs.showCommentDialog().subscribe(showCommentDialogTest);
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        // The comment dialog should be hidden from logged out user.
        showCommentDialogTest.assertNoValues();
        // Login.
        currentUser.refresh(UserFactory.user());
        vm.inputs.loginSuccess();
        // The comment dialog should be shown to backer.
        showCommentDialogTest.assertValue(Pair.create(project, true));
    }

    @Test
    public void testCommentsViewModel_showCommentButton_isBacking() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        final TestSubscriber<Boolean> commentButtonHidden = new TestSubscriber();
        vm.outputs.commentButtonHidden().subscribe(commentButtonHidden);
        // Start the view model with a backed project.
        vm.intent(new Intent().putExtra(PROJECT, ProjectFactory.backedProject()));
        // The comment button should be shown to backer.
        commentButtonHidden.assertValue(false);
    }

    @Test
    public void testCommentsViewModel_commentButtonShown_isCreator() {
        final User currentUser = UserFactory.user().toBuilder().id(1234).build();
        final Project project = ProjectFactory.project().toBuilder().creator(currentUser).isBacking(false).build();
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment().toBuilder().currentUser(new MockCurrentUser(currentUser)).build());
        final TestSubscriber<Boolean> commentButtonHidden = new TestSubscriber();
        vm.outputs.commentButtonHidden().subscribe(commentButtonHidden);
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        // Comment button is shown for the creator.
        commentButtonHidden.assertValues(false);
    }

    @Test
    public void testCommentsViewModel_commentButtonHidden_notBackingNotCreator() {
        final User creator = UserFactory.creator().toBuilder().id(222).build();
        final User currentUser = UserFactory.user().toBuilder().id(111).build();
        final Project project = ProjectFactory.project().toBuilder().creator(creator).isBacking(false).build();
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment().toBuilder().currentUser(new MockCurrentUser(currentUser)).build());
        final TestSubscriber<Boolean> commentButtonHidden = new TestSubscriber();
        vm.outputs.commentButtonHidden().subscribe(commentButtonHidden);
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        // Comment button should be hidden if not backing and not creator.
        commentButtonHidden.assertValue(true);
    }

    @Test
    public void testCommentsViewModel_dismissCommentDialog() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        final TestSubscriber<Void> dismissCommentDialogTest = new TestSubscriber();
        vm.outputs.dismissCommentDialog().subscribe(dismissCommentDialogTest);
        final TestSubscriber<Pair<Project, Boolean>> showCommentDialogTest = new TestSubscriber();
        vm.outputs.showCommentDialog().subscribe(showCommentDialogTest);
        final Project project = ProjectFactory.backedProject();
        // Start the view model with a project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        // The comment dialog should not be shown.
        showCommentDialogTest.assertNoValues();
        dismissCommentDialogTest.assertNoValues();
        // Dismiss the comment dialog.
        vm.inputs.commentDialogDismissed();
        // The comment dialog should be dismissed.
        dismissCommentDialogTest.assertValueCount(1);
        showCommentDialogTest.assertValue(null);
    }

    @Test
    public void testCommentsViewModel_currentCommentBody() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        final TestSubscriber<String> currentCommentBodyTest = new TestSubscriber();
        vm.outputs.currentCommentBody().subscribe(currentCommentBodyTest);
        currentCommentBodyTest.assertNoValues();
        vm.inputs.commentBodyChanged("Hello");
        currentCommentBodyTest.assertValues("Hello");
    }

    @Test
    public void testCommentsViewModel_showCommentDialog() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        final TestSubscriber<Pair<Project, Boolean>> showCommentDialogTest = new TestSubscriber();
        vm.outputs.showCommentDialog().subscribe(showCommentDialogTest);
        final Project project = ProjectFactory.backedProject();
        // Start the view model with a backed project.
        vm.intent(new Intent().putExtra(PROJECT, project));
        showCommentDialogTest.assertNoValues();
        // Click the comment button.
        vm.inputs.commentButtonClicked();
        // The comment dialog should be shown.
        showCommentDialogTest.assertValue(Pair.create(project, true));
    }

    @Test
    public void testCommentsViewModel_UpdateCommentsEmit() {
        final CommentsViewModel.ViewModel vm = new CommentsViewModel.ViewModel(environment());
        final TestSubscriber<CommentsData> commentsData = new TestSubscriber();
        vm.outputs.commentsData().subscribe(commentsData);
        // Start the view model with an update.
        vm.intent(new Intent().putExtra(UPDATE, UpdateFactory.update()));
        koalaTest.assertValues(VIEWED_COMMENTS);
        // Comments should emit.
        commentsData.assertValueCount(1);
    }
}

