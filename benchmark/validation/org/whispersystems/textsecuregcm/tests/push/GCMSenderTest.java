package org.whispersystems.textsecuregcm.tests.push;


import com.google.common.util.concurrent.SettableFuture;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.whispersystems.gcm.server.Message;
import org.whispersystems.gcm.server.Result;
import org.whispersystems.gcm.server.Sender;
import org.whispersystems.textsecuregcm.push.GCMSender;
import org.whispersystems.textsecuregcm.push.GcmMessage;
import org.whispersystems.textsecuregcm.sqs.DirectoryQueue;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.tests.util.SynchronousExecutorService;


public class GCMSenderTest {
    @Test
    public void testSendMessage() {
        AccountsManager accountsManager = Mockito.mock(AccountsManager.class);
        Sender sender = Mockito.mock(Sender.class);
        Result successResult = Mockito.mock(Result.class);
        DirectoryQueue directoryQueue = Mockito.mock(DirectoryQueue.class);
        SynchronousExecutorService executorService = new SynchronousExecutorService();
        Mockito.when(successResult.isInvalidRegistrationId()).thenReturn(false);
        Mockito.when(successResult.isUnregistered()).thenReturn(false);
        Mockito.when(successResult.hasCanonicalRegistrationId()).thenReturn(false);
        Mockito.when(successResult.isSuccess()).thenReturn(true);
        GcmMessage message = new GcmMessage("foo", "+12223334444", 1, false);
        GCMSender gcmSender = new GCMSender(accountsManager, sender, directoryQueue, executorService);
        SettableFuture<Result> successFuture = SettableFuture.create();
        successFuture.set(successResult);
        Mockito.when(sender.send(ArgumentMatchers.any(Message.class), Matchers.anyObject())).thenReturn(successFuture);
        Mockito.when(successResult.getContext()).thenReturn(message);
        gcmSender.sendMessage(message);
        Mockito.verify(sender, Mockito.times(1)).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(message));
    }

    @Test
    public void testSendError() {
        String destinationNumber = "+12223334444";
        String gcmId = "foo";
        AccountsManager accountsManager = Mockito.mock(AccountsManager.class);
        Sender sender = Mockito.mock(Sender.class);
        Result invalidResult = Mockito.mock(Result.class);
        DirectoryQueue directoryQueue = Mockito.mock(DirectoryQueue.class);
        SynchronousExecutorService executorService = new SynchronousExecutorService();
        Account destinationAccount = Mockito.mock(Account.class);
        Device destinationDevice = Mockito.mock(Device.class);
        Mockito.when(destinationAccount.getDevice(1)).thenReturn(Optional.of(destinationDevice));
        Mockito.when(accountsManager.get(destinationNumber)).thenReturn(Optional.of(destinationAccount));
        Mockito.when(destinationDevice.getGcmId()).thenReturn(gcmId);
        Mockito.when(invalidResult.isInvalidRegistrationId()).thenReturn(true);
        Mockito.when(invalidResult.isUnregistered()).thenReturn(false);
        Mockito.when(invalidResult.hasCanonicalRegistrationId()).thenReturn(false);
        Mockito.when(invalidResult.isSuccess()).thenReturn(true);
        GcmMessage message = new GcmMessage(gcmId, destinationNumber, 1, false);
        GCMSender gcmSender = new GCMSender(accountsManager, sender, directoryQueue, executorService);
        SettableFuture<Result> invalidFuture = SettableFuture.create();
        invalidFuture.set(invalidResult);
        Mockito.when(sender.send(ArgumentMatchers.any(Message.class), Matchers.anyObject())).thenReturn(invalidFuture);
        Mockito.when(invalidResult.getContext()).thenReturn(message);
        gcmSender.sendMessage(message);
        Mockito.verify(sender, Mockito.times(1)).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(message));
        Mockito.verify(accountsManager, Mockito.times(1)).get(ArgumentMatchers.eq(destinationNumber));
        Mockito.verify(accountsManager, Mockito.times(1)).update(ArgumentMatchers.eq(destinationAccount));
        Mockito.verify(destinationDevice, Mockito.times(1)).setGcmId(ArgumentMatchers.eq(((String) (null))));
    }

    @Test
    public void testCanonicalId() {
        String destinationNumber = "+12223334444";
        String gcmId = "foo";
        String canonicalId = "bar";
        AccountsManager accountsManager = Mockito.mock(AccountsManager.class);
        Sender sender = Mockito.mock(Sender.class);
        Result canonicalResult = Mockito.mock(Result.class);
        SynchronousExecutorService executorService = new SynchronousExecutorService();
        Account destinationAccount = Mockito.mock(Account.class);
        Device destinationDevice = Mockito.mock(Device.class);
        DirectoryQueue directoryQueue = Mockito.mock(DirectoryQueue.class);
        Mockito.when(destinationAccount.getDevice(1)).thenReturn(Optional.of(destinationDevice));
        Mockito.when(accountsManager.get(destinationNumber)).thenReturn(Optional.of(destinationAccount));
        Mockito.when(destinationDevice.getGcmId()).thenReturn(gcmId);
        Mockito.when(canonicalResult.isInvalidRegistrationId()).thenReturn(false);
        Mockito.when(canonicalResult.isUnregistered()).thenReturn(false);
        Mockito.when(canonicalResult.hasCanonicalRegistrationId()).thenReturn(true);
        Mockito.when(canonicalResult.isSuccess()).thenReturn(false);
        Mockito.when(canonicalResult.getCanonicalRegistrationId()).thenReturn(canonicalId);
        GcmMessage message = new GcmMessage(gcmId, destinationNumber, 1, false);
        GCMSender gcmSender = new GCMSender(accountsManager, sender, directoryQueue, executorService);
        SettableFuture<Result> invalidFuture = SettableFuture.create();
        invalidFuture.set(canonicalResult);
        Mockito.when(sender.send(ArgumentMatchers.any(Message.class), Matchers.anyObject())).thenReturn(invalidFuture);
        Mockito.when(canonicalResult.getContext()).thenReturn(message);
        gcmSender.sendMessage(message);
        Mockito.verify(sender, Mockito.times(1)).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(message));
        Mockito.verify(accountsManager, Mockito.times(1)).get(ArgumentMatchers.eq(destinationNumber));
        Mockito.verify(accountsManager, Mockito.times(1)).update(ArgumentMatchers.eq(destinationAccount));
        Mockito.verify(destinationDevice, Mockito.times(1)).setGcmId(ArgumentMatchers.eq(canonicalId));
    }
}

