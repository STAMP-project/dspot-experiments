package org.telegram.abilitybots.api.bot;


import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.User;


public class AbilityBotI18nTest {
    private static final User NO_LANGUAGE_USER = new User(1, "first", false, "last", "username", null);

    private static final User ITALIAN_USER = new User(2, "first", false, "last", "username", "it-IT");

    private DBContext db;

    private AbilityBotI18nTest.NoPublicCommandsBot bot;

    private MessageSender sender;

    private SilentSender silent;

    @Test
    public void missingPublicCommandsLocalizedInEnglishByDefault() {
        MessageContext context = AbilityBotTest.mockContext(AbilityBotI18nTest.NO_LANGUAGE_USER);
        reportCommands().action().accept(context);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send("No available commands found.", AbilityBotI18nTest.NO_LANGUAGE_USER.getId());
    }

    @Test
    public void missingPublicCommandsLocalizedInItalian() {
        MessageContext context = AbilityBotTest.mockContext(AbilityBotI18nTest.ITALIAN_USER);
        reportCommands().action().accept(context);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send("Non sono presenti comandi disponibile.", AbilityBotI18nTest.ITALIAN_USER.getId());
    }

    public static class NoPublicCommandsBot extends AbilityBot {
        protected NoPublicCommandsBot(String botToken, String botUsername, DBContext db) {
            super(botToken, botUsername, db);
        }

        @Override
        public int creatorId() {
            return 1;
        }
    }
}

