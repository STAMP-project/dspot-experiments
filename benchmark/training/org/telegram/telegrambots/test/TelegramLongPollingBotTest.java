package org.telegram.telegrambots.test;


import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class TelegramLongPollingBotTest {
    @Test
    public void testOnUpdateReceived() throws Exception {
        TelegramLongPollingBot bot = Mockito.mock(TelegramLongPollingBot.class);
        Mockito.doCallRealMethod().when(bot).onUpdatesReceived(ArgumentMatchers.any());
        Update update1 = new Update();
        Update update2 = new Update();
        bot.onUpdatesReceived(Arrays.asList(update1, update2));
        Mockito.verify(bot).onUpdateReceived(update1);
        Mockito.verify(bot).onUpdateReceived(update2);
    }

    @Test
    public void testExecutorShutdown() throws Exception {
        TelegramLongPollingBotTest.TestBot bot = Mockito.spy(new TelegramLongPollingBotTest.TestBot());
        DefaultBotSession session = new DefaultBotSession();
        session.setCallback(bot);
        session.setOptions(new DefaultBotOptions());
        session.start();
        session.stop();
        onClosing();
        ExecutorService executor = bot.getExecutor();
        Assert.assertThat("Executor was not shut down", executor.isShutdown(), CoreMatchers.is(true));
        executor.awaitTermination(1, TimeUnit.SECONDS);
        Assert.assertThat("Executor could not terminate", executor.isTerminated(), CoreMatchers.is(true));
    }

    private static class TestBot extends TelegramLongPollingBot {
        @Override
        public void onUpdateReceived(Update update) {
        }

        ExecutorService getExecutor() {
            return exe;
        }

        @Override
        public String getBotUsername() {
            return "";
        }

        @Override
        public String getBotToken() {
            return "";
        }
    }
}

