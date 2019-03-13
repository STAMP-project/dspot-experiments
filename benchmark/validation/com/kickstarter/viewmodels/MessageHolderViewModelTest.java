package com.kickstarter.viewmodels;


import MessageHolderViewModel.ViewModel;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.MockCurrentUser;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.models.Message;
import com.kickstarter.models.User;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class MessageHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Boolean> messageBodyRecipientCardViewIsGone = new TestSubscriber();

    private final TestSubscriber<String> messageBodyRecipientTextViewText = new TestSubscriber();

    private final TestSubscriber<Boolean> messageBodySenderCardViewIsGone = new TestSubscriber();

    private final TestSubscriber<String> messageBodySenderTextViewText = new TestSubscriber();

    private final TestSubscriber<Boolean> participantAvatarImageHidden = new TestSubscriber();

    private final TestSubscriber<String> participantAvatarImageUrl = new TestSubscriber();

    @Test
    public void testMessageBodyTextViewFormatting_CurrentUserIsRecipient() {
        final User recipient = UserFactory.user().toBuilder().name("Ima Backer").id(123).build();
        final User sender = UserFactory.user().toBuilder().name("Ima Creator").id(456).build();
        final Message message = com.kickstarter.mock.factories.MessageFactory.message().toBuilder().recipient(recipient).sender(sender).build();
        final MockCurrentUser currentUser = new MockCurrentUser(recipient);
        setUpEnvironment(environment().toBuilder().currentUser(currentUser).build());
        this.vm.inputs.configureWith(message);
        this.messageBodyRecipientCardViewIsGone.assertValues(false);
        this.messageBodyRecipientTextViewText.assertValues(message.body());
        this.messageBodySenderCardViewIsGone.assertValues(true);
        this.messageBodySenderTextViewText.assertNoValues();
    }

    @Test
    public void testMessageBodyTextViewFormatting_CurrentUserIsSender() {
        final User recipient = UserFactory.user().toBuilder().name("Ima Creator").id(123).build();
        final User sender = UserFactory.user().toBuilder().name("Ima Backer").id(456).build();
        final Message message = com.kickstarter.mock.factories.MessageFactory.message().toBuilder().recipient(recipient).sender(sender).build();
        final MockCurrentUser currentUser = new MockCurrentUser(sender);
        setUpEnvironment(environment().toBuilder().currentUser(currentUser).build());
        this.vm.inputs.configureWith(message);
        this.messageBodyRecipientCardViewIsGone.assertValues(true);
        this.messageBodyRecipientTextViewText.assertNoValues();
        this.messageBodySenderCardViewIsGone.assertValues(false);
        this.messageBodySenderTextViewText.assertValues(message.body());
    }

    @Test
    public void testParticipantAvatarImage_CurrentUserIsRecipient() {
        final User recipient = UserFactory.user().toBuilder().name("Ima Backer").id(123).build();
        final User sender = UserFactory.user().toBuilder().name("Ima Creator").id(456).build();
        final Message message = com.kickstarter.mock.factories.MessageFactory.message().toBuilder().recipient(recipient).sender(sender).build();
        final MockCurrentUser currentUser = new MockCurrentUser(recipient);
        setUpEnvironment(environment().toBuilder().currentUser(currentUser).build());
        this.vm.inputs.configureWith(message);
        // Avatar shown for sender who is the creator.
        this.participantAvatarImageHidden.assertValues(false);
        this.participantAvatarImageUrl.assertValues(message.sender().avatar().medium());
    }

    @Test
    public void testParticipantAvatarImage_CurrentUserIsSender() {
        final User recipient = UserFactory.user().toBuilder().name("Ima Creator").id(123).build();
        final User sender = UserFactory.user().toBuilder().name("Ima Backer").id(456).build();
        final Message message = com.kickstarter.mock.factories.MessageFactory.message().toBuilder().recipient(recipient).sender(sender).build();
        final MockCurrentUser currentUser = new MockCurrentUser(sender);
        setUpEnvironment(environment().toBuilder().currentUser(currentUser).build());
        this.vm.inputs.configureWith(message);
        // Avatar hidden for sender who is the backer.
        this.participantAvatarImageHidden.assertValues(true);
        this.participantAvatarImageUrl.assertNoValues();
    }
}

