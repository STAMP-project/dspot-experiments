package org.telegram.telegrambots.starter;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;


public class TestTelegramBotStarterConfiguration {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(TestTelegramBotStarterConfiguration.MockTelegramBotsApi.class, TelegramBotStarterConfiguration.class));

    @Test
    public void createMockTelegramBotsApiWithDefaultSettings() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            verifyNoMoreInteractions(context.getBean(.class));
        });
    }

    @Test
    public void createOnlyLongPollingBot() {
        this.contextRunner.withUserConfiguration(TestTelegramBotStarterConfiguration.LongPollingBotConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            TelegramBotsApi telegramBotsApi = context.getBean(.class);
            verify(telegramBotsApi, times(1)).registerBot(context.getBean(.class));
            verifyNoMoreInteractions(telegramBotsApi);
        });
    }

    @Test
    public void createOnlyWebhookBot() {
        this.contextRunner.withUserConfiguration(TestTelegramBotStarterConfiguration.WebhookBotConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            TelegramBotsApi telegramBotsApi = context.getBean(.class);
            verify(telegramBotsApi, times(1)).registerBot(context.getBean(.class));
            verifyNoMoreInteractions(telegramBotsApi);
        });
    }

    @Test
    public void createLongPoolingBotAndWebhookBot() {
        this.contextRunner.withUserConfiguration(TestTelegramBotStarterConfiguration.LongPollingBotConfig.class, TestTelegramBotStarterConfiguration.WebhookBotConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            TelegramBotsApi telegramBotsApi = context.getBean(.class);
            verify(telegramBotsApi, times(1)).registerBot(context.getBean(.class));
            verify(telegramBotsApi, times(1)).registerBot(context.getBean(.class));
            // verifyNoMoreInteractions(telegramBotsApi);
        });
    }

    @Configuration
    static class MockTelegramBotsApi {
        @Bean
        public TelegramBotsApi telegramBotsApi() {
            return Mockito.mock(TelegramBotsApi.class);
        }
    }

    @Configuration
    static class LongPollingBotConfig {
        @Bean
        public LongPollingBot longPollingBot() {
            return Mockito.mock(LongPollingBot.class);
        }
    }

    @Configuration
    static class WebhookBotConfig {
        @Bean
        public WebhookBot webhookBot() {
            return Mockito.mock(WebhookBot.class);
        }
    }
}

