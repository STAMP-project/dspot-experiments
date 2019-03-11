package org.telegram.telegrambots.test;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.test.Fakes.FakeLongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


/**
 *
 *
 * @author Ruben Bermudez
 * @version 1.0
 * @unknown Test for DefaultBotSession
 */
public class TestDefaultBotSession {
    DefaultBotSession session;

    @Test
    public void TestDefaultBotSessionIsNotRunningWhenCreated() throws Exception {
        Assert.assertFalse(session.isRunning());
    }

    @Test
    public void TestDefaultBotSessionCanBeStartedAfterCreation() throws Exception {
        session = getDefaultBotSession();
        session.start();
        Assert.assertTrue(session.isRunning());
    }

    @Test(expected = IllegalStateException.class)
    public void TestDefaultBotSessionCanNotBeStoppedAfterCreation() throws Exception {
        session = getDefaultBotSession();
        session.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void TestDefaultBotSessionCanNotBeStartedIfAlreadyStarted() throws Exception {
        session = getDefaultBotSession();
        session.start();
        session.start();
    }

    @Test
    public void TestDefaultBotSessionCanBeStoppedIfStarted() throws Exception {
        session = getDefaultBotSession();
        session.start();
        session.stop();
        Assert.assertFalse(session.isRunning());
    }

    @Test(expected = IllegalStateException.class)
    public void TestDefaultBotSessionCanNotBeStoppedIfAlreadyStopped() throws Exception {
        session = getDefaultBotSession();
        session.start();
        session.stop();
        session.stop();
    }

    @Test
    public void testUpdates() throws Exception {
        LongPollingBot bot = Mockito.spy(new FakeLongPollingBot());
        session = getDefaultBotSession(bot);
        AtomicInteger flag = new AtomicInteger();
        Update[] updates = createFakeUpdates(9);
        session.setUpdatesSupplier(createFakeUpdatesSupplier(flag, updates));
        session.start();
        Thread.sleep(1000);
        Mockito.verify(bot, Mockito.never()).onUpdateReceived(Matchers.any());
        flag.compareAndSet(0, 1);
        Thread.sleep(1000);
        Mockito.verify(bot).onUpdateReceived(updates[0]);
        Mockito.verify(bot).onUpdateReceived(updates[1]);
        flag.compareAndSet(2, 3);
        Thread.sleep(1000);
        Mockito.verify(bot).onUpdateReceived(updates[2]);
        Mockito.verify(bot).onUpdateReceived(updates[3]);
        Mockito.verify(bot).onUpdateReceived(updates[4]);
        flag.compareAndSet(4, 5);
        Thread.sleep(1000);
        Mockito.verify(bot).onUpdateReceived(updates[5]);
        Mockito.verify(bot).onUpdateReceived(updates[6]);
        Mockito.verify(bot).onUpdateReceived(updates[7]);
        Mockito.verify(bot).onUpdateReceived(updates[8]);
        session.stop();
    }

    @Test
    public void testBatchUpdates() throws Exception {
        LongPollingBot bot = Mockito.spy(new FakeLongPollingBot());
        session = getDefaultBotSession(bot);
        AtomicInteger flag = new AtomicInteger();
        Update[] updates = createFakeUpdates(9);
        session.setUpdatesSupplier(createFakeUpdatesSupplier(flag, updates));
        session.start();
        Thread.sleep(1000);
        Mockito.verify(bot, Mockito.never()).onUpdateReceived(Matchers.any());
        flag.compareAndSet(0, 1);
        Thread.sleep(1000);
        Mockito.verify(bot).onUpdatesReceived(Arrays.asList(updates[0], updates[1]));
        flag.compareAndSet(2, 3);
        Thread.sleep(1000);
        Mockito.verify(bot).onUpdatesReceived(Arrays.asList(updates[2], updates[3], updates[4]));
        flag.compareAndSet(4, 5);
        Thread.sleep(1000);
        Mockito.verify(bot).onUpdatesReceived(Arrays.asList(updates[5], updates[6], updates[7], updates[8]));
        session.stop();
    }
}

