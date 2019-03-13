package org.telegram.telegrambots.starter;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class TestTelegramBotStarterRegistrationHooks {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(TestTelegramBotStarterRegistrationHooks.MockTelegramBotsApi.class, TelegramBotStarterConfiguration.class));

    // Terrible workaround for mockito loosing annotations on methods
    private static boolean hookCalled = false;

    private static boolean hookCalledWithSession = false;

    private static final DefaultBotSession someBotSession = new DefaultBotSession();

    private static final TelegramBotsApi mockTelegramBotsApi = Mockito.mock(TelegramBotsApi.class);

    @Test
    public void longPollingBotWithAnnotatedMethodshouldBeCalled() throws TelegramApiRequestException {
        Mockito.when(TestTelegramBotStarterRegistrationHooks.mockTelegramBotsApi.registerBot(ArgumentMatchers.any(LongPollingBot.class))).thenReturn(TestTelegramBotStarterRegistrationHooks.someBotSession);
        this.contextRunner.withUserConfiguration(TestTelegramBotStarterRegistrationHooks.LongPollingBotConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            final LongPollingBot bot = context.getBean(.class);
            final TelegramBotsApi telegramBotsApi = context.getBean(.class);
            assertThat(TestTelegramBotStarterRegistrationHooks.hookCalled).isTrue();
            assertThat(TestTelegramBotStarterRegistrationHooks.hookCalledWithSession).isTrue();
            verify(telegramBotsApi, times(1)).registerBot(bot);
            verifyNoMoreInteractions(telegramBotsApi);
        });
    }

    @Configuration
    static class MockTelegramBotsApi {
        @Bean
        public TelegramBotsApi telegramBotsApi() {
            return TestTelegramBotStarterRegistrationHooks.mockTelegramBotsApi;
        }
    }

    @Configuration
    static class LongPollingBotConfig {
        @Bean
        public LongPollingBot longPollingBot() {
            return new TestTelegramBotStarterRegistrationHooks.AnnotatedLongPollingBot();
        }
    }

    static class AnnotatedLongPollingBot extends TelegramLongPollingBot {
        @Override
        public void onUpdateReceived(final Update update) {
        }

        @Override
        public String getBotUsername() {
            return null;
        }

        @Override
        public String getBotToken() {
            return null;
        }

        @AfterBotRegistration
        public void afterBotHook() {
            TestTelegramBotStarterRegistrationHooks.hookCalled = true;
        }

        @AfterBotRegistration
        public void afterBotHookWithSession(BotSession session) {
            TestTelegramBotStarterRegistrationHooks.hookCalledWithSession = session.equals(TestTelegramBotStarterRegistrationHooks.someBotSession);
        }
    }
}

