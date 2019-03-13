package com.kickstarter.viewmodels;


import KoalaEvent.SENT_MESSAGE;
import KoalaEvent.VIEWED_MESSAGE_THREAD;
import MessagesViewModel.ViewModel;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.CurrentUserType;
import com.kickstarter.libs.MockCurrentUser;
import com.kickstarter.mock.factories.ApiExceptionFactory;
import com.kickstarter.mock.factories.BackingFactory;
import com.kickstarter.mock.factories.MessageFactory;
import com.kickstarter.mock.factories.MessageThreadEnvelopeFactory;
import com.kickstarter.mock.factories.MessageThreadFactory;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.models.Backing;
import com.kickstarter.models.Message;
import com.kickstarter.models.MessageThread;
import com.kickstarter.models.Project;
import com.kickstarter.models.User;
import com.kickstarter.services.apiresponses.MessageThreadEnvelope;
import com.kickstarter.ui.data.MessageSubject;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public final class MessagesViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Boolean> backButtonIsGone = new TestSubscriber();

    private final TestSubscriber<Pair<Backing, Project>> backingAndProject = new TestSubscriber();

    private final TestSubscriber<Boolean> backingInfoViewIsGone = new TestSubscriber();

    private final TestSubscriber<Boolean> closeButtonIsGone = new TestSubscriber();

    private final TestSubscriber<String> creatorNameTextViewText = new TestSubscriber();

    private final TestSubscriber<Void> goBack = new TestSubscriber();

    private final TestSubscriber<String> messageEditTextHint = new TestSubscriber();

    private final TestSubscriber<Void> messageEditTextShouldRequestFocus = new TestSubscriber();

    private final TestSubscriber<List<Message>> messageList = new TestSubscriber();

    private final TestSubscriber<String> projectNameTextViewText = new TestSubscriber();

    private final TestSubscriber<String> projectNameToolbarTextViewText = new TestSubscriber();

    private final TestSubscriber<Void> recyclerViewDefaultBottomPadding = new TestSubscriber();

    private final TestSubscriber<Integer> recyclerViewInitialBottomPadding = new TestSubscriber();

    private final TestSubscriber<Void> scrollRecyclerViewToBottom = new TestSubscriber();

    private final TestSubscriber<Boolean> sendMessageButtonIsEnabled = new TestSubscriber();

    private final TestSubscriber<String> setMessageEditText = new TestSubscriber();

    private final TestSubscriber<String> showMessageErrorToast = new TestSubscriber();

    private final TestSubscriber<Pair<Project, User>> startBackingActivity = new TestSubscriber();

    private final TestSubscriber<Void> successfullyMarkedAsRead = new TestSubscriber();

    private final TestSubscriber<Boolean> toolbarIsExpanded = new TestSubscriber();

    private final TestSubscriber<Boolean> viewPledgeButtonIsGone = new TestSubscriber();

    @Test
    public void testBackButton_IsGone() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        // Back button is gone if navigating from non-backer modal view.
        this.backButtonIsGone.assertValues(true);
        this.closeButtonIsGone.assertValues(false);
    }

    @Test
    public void testBackButton_IsVisible() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(BackingFactory.backing(), ProjectFactory.project()));
        // Back button is visible if navigating from backer modal view.
        this.backButtonIsGone.assertValues(false);
        this.closeButtonIsGone.assertValues(true);
    }

    @Test
    public void testBackingAndProject_Participant() {
        final Project project = ProjectFactory.project().toBuilder().isBacking(false).build();
        final Backing backing = BackingFactory.backing().toBuilder().project(project).build();
        final MessageThread messageThread = MessageThreadFactory.messageThread().toBuilder().project(project).backing(backing).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForThread(@NonNull
            final MessageThread messageThread) {
                return Observable.just(MessageThreadEnvelopeFactory.messageThreadEnvelope());
            }

            @Override
            @NonNull
            public Observable<Backing> fetchProjectBacking(@NonNull
            final Project project, @NonNull
            final User user) {
                return Observable.just(backing);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.backingAndProject.assertValues(Pair.create(backing, backing.project()));
        this.backingInfoViewIsGone.assertValues(false);
    }

    @Test
    public void testBackingInfo_NoBacking() {
        final Project project = ProjectFactory.project().toBuilder().isBacking(false).build();
        final MessageThread messageThread = MessageThreadFactory.messageThread().toBuilder().project(project).backing(null).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Backing> fetchProjectBacking(@NonNull
            final Project project, @NonNull
            final User user) {
                return Observable.error(ApiExceptionFactory.badRequestException());
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.backingAndProject.assertNoValues();
        this.backingInfoViewIsGone.assertValues(true);
    }

    @Test
    public void testConfiguredWithThread() {
        final MessageThread messageThread = MessageThreadFactory.messageThread();
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.backingAndProject.assertValueCount(1);
        this.messageList.assertValueCount(1);
        this.koalaTest.assertValues(VIEWED_MESSAGE_THREAD);
    }

    @Test
    public void testConfiguredWithProject_AndBacking() {
        final Backing backing = BackingFactory.backing();
        final Project project = ProjectFactory.project();
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a backing and a project.
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(backing, project));
        this.backingAndProject.assertValueCount(1);
        this.messageList.assertValueCount(1);
        this.koalaTest.assertValues(VIEWED_MESSAGE_THREAD);
    }

    @Test
    public void testCreatorViewingProjectMessages() {
        final User creator = UserFactory.creator().toBuilder().name("Sharon").build();
        final User participant = UserFactory.user().toBuilder().name("Timothy").build();
        final CurrentUserType currentUser = new MockCurrentUser(creator);
        final MessageThread messageThread = MessageThreadFactory.messageThread().toBuilder().project(ProjectFactory.project().toBuilder().creator(creator).build()).participant(participant).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForThread(@NonNull
            final MessageThread thread) {
                return Observable.just(MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messageThread(messageThread).build());
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(currentUser).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        // Creator name is the project creator, edit text hint is always the participant.
        this.creatorNameTextViewText.assertValues(creator.name());
        this.messageEditTextHint.assertValues(participant.name());
    }

    @Test
    public void testGoBack() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        this.vm.inputs.backOrCloseButtonClicked();
        this.goBack.assertValueCount(1);
    }

    @Test
    public void testProjectData_ExistingMessages() {
        final MessageThread messageThread = MessageThreadFactory.messageThread();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForThread(@NonNull
            final MessageThread thread) {
                return Observable.just(MessageThreadEnvelopeFactory.messageThreadEnvelope());
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.creatorNameTextViewText.assertValues(messageThread.project().creator().name());
        this.projectNameTextViewText.assertValues(messageThread.project().name());
        this.projectNameToolbarTextViewText.assertValues(messageThread.project().name());
    }

    @Test
    public void testMessageEditTextHint() {
        final MessageThread messageThread = MessageThreadFactory.messageThread();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForThread(@NonNull
            final MessageThread thread) {
                return Observable.just(MessageThreadEnvelopeFactory.messageThreadEnvelope());
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.messageEditTextHint.assertValues(messageThread.project().creator().name());
    }

    @Test
    public void testMessagesEmit() {
        final MessageThreadEnvelope envelope = MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messages(Collections.singletonList(MessageFactory.message())).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForThread(@NonNull
            final MessageThread messageThread) {
                return Observable.just(envelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        // Messages emit, keyboard not shown.
        this.messageList.assertValueCount(1);
        this.messageEditTextShouldRequestFocus.assertNoValues();
    }

    @Test
    public void testNoMessages() {
        final Backing backing = BackingFactory.backing();
        final Project project = ProjectFactory.project();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForBacking(@NonNull
            final Backing backing) {
                return Observable.just(MessageThreadEnvelopeFactory.empty());
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a backing and a project.
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(backing, project));
        // All data except for messages should emit.
        this.messageList.assertNoValues();
        this.creatorNameTextViewText.assertValues(project.creator().name());
        this.backingAndProject.assertValues(Pair.create(backing, project));
    }

    @Test
    public void testRecyclerViewBottomPadding() {
        final int appBarTotalScrolLRange = 327;
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        // View initially loaded with a 0 (expanded) offset.
        this.vm.inputs.appBarOffset(0);
        this.vm.inputs.appBarTotalScrollRange(appBarTotalScrolLRange);
        // Only initial bottom padding emits.
        this.recyclerViewDefaultBottomPadding.assertNoValues();
        this.recyclerViewInitialBottomPadding.assertValues(appBarTotalScrolLRange);
        // User scrolls.
        this.vm.inputs.appBarOffset((-30));
        this.vm.inputs.appBarTotalScrollRange(appBarTotalScrolLRange);
        // Default padding emits, initial padding does not emit again.
        this.recyclerViewDefaultBottomPadding.assertValueCount(1);
        this.recyclerViewInitialBottomPadding.assertValues(appBarTotalScrolLRange);
        // User scrolls.
        this.vm.inputs.appBarOffset(20);
        this.vm.inputs.appBarTotalScrollRange(appBarTotalScrolLRange);
        // Padding does not change.
        this.recyclerViewDefaultBottomPadding.assertValueCount(1);
        this.recyclerViewInitialBottomPadding.assertValues(appBarTotalScrolLRange);
    }

    @Test
    public void testSendMessage_Error() {
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Message> sendMessage(@NonNull
            final MessageSubject messageSubject, @NonNull
            final String body) {
                return Observable.error(ApiExceptionFactory.badRequestException());
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        // Send a message unsuccessfully.
        this.vm.inputs.messageEditTextChanged("Hello there");
        this.vm.inputs.sendMessageButtonClicked();
        // Error toast is displayed, errored message body remains in edit text, no new message is emitted.
        this.showMessageErrorToast.assertValueCount(1);
        this.setMessageEditText.assertNoValues();
        // No sent message event tracked.
        this.koalaTest.assertValues(VIEWED_MESSAGE_THREAD);
    }

    @Test
    public void testSendMessage_Success() {
        final Message sentMessage = MessageFactory.message();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<Message> sendMessage(@NonNull
            final MessageSubject messageSubject, @NonNull
            final String body) {
                return Observable.just(sentMessage);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a message thread.
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        // Initial messages emit.
        this.messageList.assertValueCount(1);
        // Send a message successfully.
        this.vm.inputs.messageEditTextChanged("Salutations friend!");
        this.vm.inputs.sendMessageButtonClicked();
        // New message list emits.
        this.messageList.assertValueCount(2);
        // Reply edit text should be cleared and view should be scrolled to new message.
        this.setMessageEditText.assertValues("");
        this.scrollRecyclerViewToBottom.assertValueCount(1);
        this.koalaTest.assertValues(VIEWED_MESSAGE_THREAD, SENT_MESSAGE);
    }

    @Test
    public void testSendMessageButtonIsEnabled() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        this.sendMessageButtonIsEnabled.assertNoValues();
        this.vm.inputs.messageEditTextChanged("hello");
        this.sendMessageButtonIsEnabled.assertValues(true);
        this.vm.inputs.messageEditTextChanged("");
        this.sendMessageButtonIsEnabled.assertValues(true, false);
    }

    @Test
    public void testShouldRequestFocus() {
        final Backing backing = BackingFactory.backing();
        final MessageThreadEnvelope envelope = MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messages(null).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForBacking(@NonNull
            final Backing backing) {
                return Observable.just(envelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a backing and project.
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(backing, ProjectFactory.project()));
        this.messageEditTextShouldRequestFocus.assertValueCount(1);
    }

    @Test
    public void testStartBackingActivity_AsBacker() {
        final User user = UserFactory.user();
        final Project project = ProjectFactory.project().toBuilder().isBacking(true).build();
        final MessageThread messageThread = MessageThreadFactory.messageThread().toBuilder().project(project).build();
        final MessageThreadEnvelope messageThreadEnvelope = MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messageThread(messageThread).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForBacking(@NonNull
            final Backing backing) {
                return Observable.just(messageThreadEnvelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(user)).build());
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(BackingFactory.backing(), project));
        this.vm.inputs.viewPledgeButtonClicked();
        this.startBackingActivity.assertValues(Pair.create(project, user));
    }

    @Test
    public void testStartBackingActivity_AsCreator() {
        final User backer = UserFactory.user().toBuilder().name("Vanessa").build();
        final User creator = UserFactory.user().toBuilder().name("Jessica").build();
        final Project project = ProjectFactory.project().toBuilder().creator(creator).build();
        final MessageThread messageThread = MessageThreadFactory.messageThread().toBuilder().participant(backer).project(project).build();
        final MessageThreadEnvelope messageThreadEnvelope = MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messageThread(messageThread).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForThread(@NonNull
            final MessageThread messageThread) {
                return Observable.just(messageThreadEnvelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(creator)).build());
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.vm.inputs.viewPledgeButtonClicked();
        this.startBackingActivity.assertValues(Pair.create(project, backer));
    }

    @Test
    public void testSuccessfullyMarkedAsRead() {
        final MessageThread messageThread = MessageThreadFactory.messageThread();
        final MockApiClient apiClient = new MockApiClient() {
            @NonNull
            @Override
            public Observable<MessageThread> markAsRead(@NonNull
            final MessageThread thread) {
                return Observable.just(messageThread);
            }
        };
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).apiClient(apiClient).build());
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(messageThread));
        this.successfullyMarkedAsRead.assertValueCount(1);
    }

    @Test
    public void testToolbarIsExpanded_NoMessages() {
        final Backing backing = BackingFactory.backing();
        final MessageThreadEnvelope envelope = MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messages(null).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForBacking(@NonNull
            final Backing backing) {
                return Observable.just(envelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a backing and project.
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(backing, ProjectFactory.project()));
        this.vm.inputs.messageEditTextIsFocused(true);
        // Toolbar stays expanded when keyboard opens and no messages.
        this.toolbarIsExpanded.assertNoValues();
    }

    @Test
    public void testToolbarIsExpanded_WithMessages() {
        final Backing backing = BackingFactory.backing();
        final MessageThreadEnvelope envelope = MessageThreadEnvelopeFactory.messageThreadEnvelope().toBuilder().messages(Collections.singletonList(MessageFactory.message())).build();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<MessageThreadEnvelope> fetchMessagesForBacking(@NonNull
            final Backing backing) {
                return Observable.just(envelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).currentUser(new MockCurrentUser(UserFactory.user())).build());
        // Start the view model with a backing and project.
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(backing, ProjectFactory.project()));
        this.vm.inputs.messageEditTextIsFocused(true);
        // Toolbar collapsed when keyboard opens and there are messages.
        this.toolbarIsExpanded.assertValues(false);
    }

    @Test
    public void testViewMessages_FromPush() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.pushContextIntent());
        this.backButtonIsGone.assertValues(true);
        this.closeButtonIsGone.assertValues(false);
        this.viewPledgeButtonIsGone.assertValues(false);
    }

    @Test
    public void testViewPledgeButton_IsGone() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.backerModalContextIntent(BackingFactory.backing(), ProjectFactory.project()));
        // View pledge button is hidden when context is from the backer modal.
        this.viewPledgeButtonIsGone.assertValues(true);
    }

    @Test
    public void testViewPledgeButton_IsVisible() {
        setUpEnvironment(environment().toBuilder().currentUser(new MockCurrentUser(UserFactory.user())).build());
        this.vm.intent(MessagesViewModelTest.messagesContextIntent(MessageThreadFactory.messageThread()));
        // View pledge button is shown when context is from anywhere but the backer modal.
        this.viewPledgeButtonIsGone.assertValues(false);
    }
}

