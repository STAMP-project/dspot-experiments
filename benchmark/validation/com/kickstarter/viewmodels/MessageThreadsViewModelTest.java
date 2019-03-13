package com.kickstarter.viewmodels;


import Empty.INSTANCE;
import IntentKey.PROJECT;
import KoalaEvent.VIEWED_MESSAGE_INBOX;
import MessageThreadsViewModel.ViewModel;
import R.color.ksr_dark_grey_400;
import R.color.ksr_text_green_700;
import Typeface.BOLD;
import Typeface.NORMAL;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.CurrentUserType;
import com.kickstarter.libs.MockCurrentUser;
import com.kickstarter.mock.factories.MessageThreadFactory;
import com.kickstarter.mock.factories.MessageThreadsEnvelopeFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.models.MessageThread;
import com.kickstarter.models.Project;
import com.kickstarter.models.User;
import com.kickstarter.services.ApiClientType;
import com.kickstarter.services.apiresponses.MessageThreadsEnvelope;
import com.kickstarter.ui.data.Mailbox;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class MessageThreadsViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Boolean> hasNoMessages = new TestSubscriber();

    private final TestSubscriber<Boolean> hasNoUnreadMessages = new TestSubscriber();

    private final TestSubscriber<List<MessageThread>> messageThreadList = new TestSubscriber();

    private final TestSubscriber<Integer> unreadCountTextViewColorInt = new TestSubscriber();

    private final TestSubscriber<Integer> unreadCountTextViewTypefaceInt = new TestSubscriber();

    private final TestSubscriber<Boolean> unreadCountToolbarTextViewIsGone = new TestSubscriber();

    private final TestSubscriber<Integer> unreadMessagesCount = new TestSubscriber();

    @Test
    public void testMessageThreadsEmit_NoProjectIntent() {
        final CurrentUserType currentUser = new MockCurrentUser();
        currentUser.login(com.kickstarter.mock.factories.UserFactory.user().toBuilder().unreadMessagesCount(0).build(), "beefbod5");
        final MessageThreadsEnvelope envelope = MessageThreadsEnvelopeFactory.messageThreadsEnvelope().toBuilder().messageThreads(Collections.singletonList(MessageThreadFactory.messageThread())).build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadsEnvelope> fetchMessageThreads(@Nullable
            final Project project, @NonNull
            final Mailbox mailbox) {
                return Observable.just(envelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(currentUser).build());
        this.vm.intent(new Intent().putExtra(PROJECT, INSTANCE));
        this.messageThreadList.assertValueCount(1);
        // Same message threads should not emit again.
        this.vm.inputs.onResume();
        this.messageThreadList.assertValueCount(1);
        this.koalaTest.assertValues(VIEWED_MESSAGE_INBOX);
    }

    @Test
    public void testMessageThreadsEmit_WithProjectIntent() {
        final CurrentUserType currentUser = new MockCurrentUser();
        currentUser.login(com.kickstarter.mock.factories.UserFactory.user().toBuilder().unreadMessagesCount(0).build(), "beefbod5");
        final MessageThreadsEnvelope envelope = MessageThreadsEnvelopeFactory.messageThreadsEnvelope().toBuilder().messageThreads(Collections.singletonList(MessageThreadFactory.messageThread())).build();
        final Project project = com.kickstarter.mock.factories.ProjectFactory.project().toBuilder().unreadMessagesCount(5).build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadsEnvelope> fetchMessageThreads(@Nullable
            final Project project, @NonNull
            final Mailbox mailbox) {
                return Observable.just(envelope);
            }

            @Override
            @NonNull
            public Observable<Project> fetchProject(@NonNull
            final String param) {
                return Observable.just(project);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(currentUser).build());
        this.vm.intent(new Intent().putExtra(PROJECT, project));
        this.messageThreadList.assertValueCount(1);
        // Same message threads should not emit again.
        this.vm.inputs.onResume();
        this.messageThreadList.assertValueCount(1);
        this.koalaTest.assertValues(VIEWED_MESSAGE_INBOX);
    }

    @Test
    public void testHasUnreadMessages() {
        final User user = com.kickstarter.mock.factories.UserFactory.user().toBuilder().unreadMessagesCount(3).build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<User> fetchCurrentUser() {
                return Observable.just(user);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).build());
        this.vm.intent(new Intent());
        this.vm.inputs.onResume();
        // Unread count text view is shown.
        this.unreadMessagesCount.assertValues(user.unreadMessagesCount());
        this.hasNoUnreadMessages.assertValues(false);
        this.unreadCountTextViewColorInt.assertValues(ksr_text_green_700);
        this.unreadCountTextViewTypefaceInt.assertValues(BOLD);
        this.unreadCountToolbarTextViewIsGone.assertValues(false);
    }

    @Test
    public void testNoMessages() {
        final User user = com.kickstarter.mock.factories.UserFactory.user().toBuilder().unreadMessagesCount(null).build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<User> fetchCurrentUser() {
                return Observable.just(user);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).build());
        this.vm.intent(new Intent());
        this.vm.inputs.onResume();
        this.hasNoMessages.assertValues(true);
        this.unreadMessagesCount.assertNoValues();
        this.unreadCountTextViewColorInt.assertValues(ksr_dark_grey_400);
        this.unreadCountTextViewTypefaceInt.assertValues(NORMAL);
        this.unreadCountToolbarTextViewIsGone.assertValues(true);
    }

    @Test
    public void testNoUnreadMessages() {
        final User user = com.kickstarter.mock.factories.UserFactory.user().toBuilder().unreadMessagesCount(0).build();
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<User> fetchCurrentUser() {
                return Observable.just(user);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).build());
        this.vm.intent(new Intent());
        this.vm.inputs.onResume();
        this.hasNoUnreadMessages.assertValues(true);
        this.unreadMessagesCount.assertNoValues();
        this.unreadCountTextViewColorInt.assertValues(ksr_dark_grey_400);
        this.unreadCountTextViewTypefaceInt.assertValues(NORMAL);
        this.unreadCountToolbarTextViewIsGone.assertValues(true);
    }
}

