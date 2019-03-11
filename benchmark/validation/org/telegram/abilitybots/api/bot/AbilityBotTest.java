package org.telegram.abilitybots.api.bot;


import Flag.TEXT;
import Locality.USER;
import Privacy.CREATOR;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.abilitybots.api.util.Trio;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.telegram.abilitybots.api.objects.MessageContext.newContext;


public class AbilityBotTest {
    // Messages
    private static final String RECOVERY_MESSAGE = "I am ready to receive the backup file. Please reply to this message with the backup file attached.";

    private static final String RECOVER_SUCCESS = "I have successfully recovered.";

    private static final String[] EMPTY_ARRAY = new String[]{  };

    private static final long GROUP_ID = 10L;

    private static final String TEST = "test";

    private static final String[] TEXT = new String[]{ AbilityBotTest.TEST };

    public static final User USER = new User(1, "first", false, "last", "username", null);

    public static final User CREATOR = new User(1337, "creatorFirst", false, "creatorLast", "creatorUsername", null);

    private DefaultBot bot;

    private DBContext db;

    private MessageSender sender;

    private SilentSender silent;

    @Test
    public void sendsPrivacyViolation() {
        Update update = mockFullUpdate(AbilityBotTest.USER, "/admin");
        bot.onUpdateReceived(update);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send("Sorry, you don't have the required access level to do that.", AbilityBotTest.USER.getId());
    }

    @Test
    public void sendsLocalityViolation() {
        Update update = mockFullUpdate(AbilityBotTest.USER, "/group");
        bot.onUpdateReceived(update);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send(String.format("Sorry, %s-only feature.", "group"), AbilityBotTest.USER.getId());
    }

    @Test
    public void sendsInputArgsViolation() {
        Update update = mockFullUpdate(AbilityBotTest.USER, "/count 1 2 3");
        bot.onUpdateReceived(update);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send(String.format("Sorry, this feature requires %d additional inputs.", 4), AbilityBotTest.USER.getId());
    }

    @Test
    public void canProcessRepliesIfSatisfyRequirements() {
        Update update = mockFullUpdate(AbilityBotTest.USER, "must reply");
        // False means the update was not pushed down the stream since it has been consumed by the reply
        Assert.assertFalse(bot.filterReply(update));
        Mockito.verify(silent, VerificationModeFactory.times(1)).send("reply", AbilityBotTest.USER.getId());
    }

    @Test
    public void canBackupDB() throws TelegramApiException {
        MessageContext context = defaultContext();
        backupDB().action().accept(context);
        FileUtils.deleteQuietly(new File("backup.json"));
        Mockito.verify(sender, VerificationModeFactory.times(1)).sendDocument(ArgumentMatchers.any());
    }

    @Test
    public void canRecoverDB() throws IOException, TelegramApiException {
        Update update = mockBackupUpdate();
        Object backup = getDbBackup();
        File backupFile = createBackupFile(backup);
        Mockito.when(sender.downloadFile(ArgumentMatchers.any(File.class))).thenReturn(backupFile);
        recoverDB().replies().get(0).actOn(update);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send(AbilityBotTest.RECOVER_SUCCESS, AbilityBotTest.GROUP_ID);
        Assert.assertEquals("Bot recovered but the DB is still not in sync", db.getSet(AbilityBotTest.TEST), Sets.newHashSet(AbilityBotTest.TEST));
        Assert.assertTrue("Could not delete backup file", backupFile.delete());
    }

    @Test
    public void canFilterOutReplies() {
        Update update = Mockito.mock(Update.class);
        Mockito.when(update.hasMessage()).thenReturn(false);
        Assert.assertTrue(bot.filterReply(update));
    }

    @Test
    public void canDemote() {
        addUsers(AbilityBotTest.USER);
        admins().add(AbilityBotTest.USER.getId());
        MessageContext context = defaultContext();
        demoteAdmin().action().accept(context);
        Set<Integer> actual = bot.admins();
        Set<Integer> expected = Collections.emptySet();
        Assert.assertEquals("Could not sudont super-admin", expected, actual);
    }

    @Test
    public void canPromote() {
        addUsers(AbilityBotTest.USER);
        MessageContext context = defaultContext();
        promoteAdmin().action().accept(context);
        Set<Integer> actual = bot.admins();
        Set<Integer> expected = Sets.newHashSet(AbilityBotTest.USER.getId());
        Assert.assertEquals("Could not sudo user", expected, actual);
    }

    @Test
    public void canBanUser() {
        addUsers(AbilityBotTest.USER);
        MessageContext context = defaultContext();
        banUser().action().accept(context);
        Set<Integer> actual = blacklist();
        Set<Integer> expected = Sets.newHashSet(AbilityBotTest.USER.getId());
        Assert.assertEquals("The ban was not emplaced", expected, actual);
    }

    @Test
    public void canUnbanUser() {
        addUsers(AbilityBotTest.USER);
        bot.blacklist().add(AbilityBotTest.USER.getId());
        MessageContext context = defaultContext();
        unbanUser().action().accept(context);
        Set<Integer> actual = blacklist();
        Set<Integer> expected = Sets.newHashSet();
        Assert.assertEquals("The ban was not lifted", expected, actual);
    }

    @Test
    public void cannotBanCreator() {
        addUsers(AbilityBotTest.USER, AbilityBotTest.CREATOR);
        MessageContext context = AbilityBotTest.mockContext(AbilityBotTest.USER, AbilityBotTest.GROUP_ID, AbilityBotTest.CREATOR.getUserName());
        banUser().action().accept(context);
        Set<Integer> actual = blacklist();
        Set<Integer> expected = Sets.newHashSet(AbilityBotTest.USER.getId());
        Assert.assertEquals("Impostor was not added to the blacklist", expected, actual);
    }

    @Test
    public void creatorCanClaimBot() {
        MessageContext context = AbilityBotTest.mockContext(AbilityBotTest.CREATOR, AbilityBotTest.GROUP_ID);
        claimCreator().action().accept(context);
        Set<Integer> actual = bot.admins();
        Set<Integer> expected = Sets.newHashSet(AbilityBotTest.CREATOR.getId());
        Assert.assertEquals("Creator was not properly added to the super admins set", expected, actual);
    }

    @Test
    public void bannedCreatorPassesBlacklistCheck() {
        bot.blacklist().add(AbilityBotTest.CREATOR.getId());
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        mockUser(update, message, user);
        boolean notBanned = bot.checkBlacklist(update);
        Assert.assertTrue("Creator is banned", notBanned);
    }

    @Test
    public void canAddUser() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        mockAlternateUser(update, message, AbilityBotTest.USER);
        bot.addUser(update);
        Map<String, Integer> expectedUserIds = ImmutableMap.of(AbilityBotTest.USER.getUserName(), AbilityBotTest.USER.getId());
        Map<Integer, User> expectedUsers = ImmutableMap.of(AbilityBotTest.USER.getId(), AbilityBotTest.USER);
        Assert.assertEquals("User was not added", expectedUserIds, userIds());
        Assert.assertEquals("User was not added", expectedUsers, users());
    }

    @Test
    public void canEditUser() {
        addUsers(AbilityBotTest.USER);
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        String newUsername = (AbilityBotTest.USER.getUserName()) + "-test";
        String newFirstName = (AbilityBotTest.USER.getFirstName()) + "-test";
        String newLastName = (AbilityBotTest.USER.getLastName()) + "-test";
        int sameId = AbilityBotTest.USER.getId();
        User changedUser = new User(sameId, newFirstName, false, newLastName, newUsername, null);
        mockAlternateUser(update, message, changedUser);
        bot.addUser(update);
        Map<String, Integer> expectedUserIds = ImmutableMap.of(changedUser.getUserName(), changedUser.getId());
        Map<Integer, User> expectedUsers = ImmutableMap.of(changedUser.getId(), changedUser);
        Assert.assertEquals("User was not properly edited", userIds(), expectedUserIds);
        Assert.assertEquals("User was not properly edited", expectedUsers, expectedUsers);
    }

    @Test
    public void canValidateAbility() {
        Trio<Update, Ability, String[]> invalidPair = Trio.of(null, null, null);
        Ability validAbility = DefaultBot.getDefaultBuilder().build();
        Trio<Update, Ability, String[]> validPair = Trio.of(null, validAbility, null);
        Assert.assertFalse("Bot can't validate ability properly", bot.validateAbility(invalidPair));
        Assert.assertTrue("Bot can't validate ability properly", bot.validateAbility(validPair));
    }

    @Test
    public void canCheckInput() {
        Update update = mockFullUpdate(AbilityBotTest.USER, "/something");
        Ability abilityWithOneInput = DefaultBot.getDefaultBuilder().build();
        Ability abilityWithZeroInput = DefaultBot.getDefaultBuilder().input(0).build();
        Trio<Update, Ability, String[]> trioOneArg = Trio.of(update, abilityWithOneInput, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> trioZeroArg = Trio.of(update, abilityWithZeroInput, AbilityBotTest.TEXT);
        Assert.assertTrue("Unexpected result when applying token filter", bot.checkInput(trioOneArg));
        trioOneArg = Trio.of(update, abilityWithOneInput, ArrayUtils.addAll(AbilityBotTest.TEXT, AbilityBotTest.TEXT));
        Assert.assertFalse("Unexpected result when applying token filter", bot.checkInput(trioOneArg));
        Assert.assertTrue("Unexpected result  when applying token filter", bot.checkInput(trioZeroArg));
        trioZeroArg = Trio.of(update, abilityWithZeroInput, AbilityBotTest.EMPTY_ARRAY);
        Assert.assertTrue("Unexpected result when applying token filter", bot.checkInput(trioZeroArg));
    }

    @Test
    public void canCheckPrivacy() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        Ability publicAbility = DefaultBot.getDefaultBuilder().privacy(PUBLIC).build();
        Ability groupAdminAbility = DefaultBot.getDefaultBuilder().privacy(GROUP_ADMIN).build();
        Ability adminAbility = DefaultBot.getDefaultBuilder().privacy(ADMIN).build();
        Ability creatorAbility = DefaultBot.getDefaultBuilder().privacy(Privacy.CREATOR).build();
        Trio<Update, Ability, String[]> publicTrio = Trio.of(update, publicAbility, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> groupAdminTrio = Trio.of(update, groupAdminAbility, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> adminTrio = Trio.of(update, adminAbility, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> creatorTrio = Trio.of(update, creatorAbility, AbilityBotTest.TEXT);
        mockUser(update, message, user);
        Assert.assertTrue("Unexpected result when checking for privacy", bot.checkPrivacy(publicTrio));
        Assert.assertFalse("Unexpected result when checking for privacy", bot.checkPrivacy(groupAdminTrio));
        Assert.assertFalse("Unexpected result when checking for privacy", bot.checkPrivacy(adminTrio));
        Assert.assertFalse("Unexpected result when checking for privacy", bot.checkPrivacy(creatorTrio));
    }

    @Test
    public void canValidateGroupAdminPrivacy() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        Ability groupAdminAbility = DefaultBot.getDefaultBuilder().privacy(GROUP_ADMIN).build();
        Trio<Update, Ability, String[]> groupAdminTrio = Trio.of(update, groupAdminAbility, AbilityBotTest.TEXT);
        mockUser(update, message, user);
        Mockito.when(message.isGroupMessage()).thenReturn(true);
        ChatMember member = Mockito.mock(ChatMember.class);
        Mockito.when(member.getUser()).thenReturn(user);
        Mockito.when(member.getUser()).thenReturn(user);
        Mockito.when(silent.execute(ArgumentMatchers.any(GetChatAdministrators.class))).thenReturn(Optional.of(Lists.newArrayList(member)));
        Assert.assertTrue("Unexpected result when checking for privacy", bot.checkPrivacy(groupAdminTrio));
    }

    @Test
    public void canRestrictNormalUsersFromGroupAdminAbilities() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        Ability groupAdminAbility = DefaultBot.getDefaultBuilder().privacy(GROUP_ADMIN).build();
        Trio<Update, Ability, String[]> groupAdminTrio = Trio.of(update, groupAdminAbility, AbilityBotTest.TEXT);
        mockUser(update, message, user);
        Mockito.when(message.isGroupMessage()).thenReturn(true);
        Mockito.when(silent.execute(ArgumentMatchers.any(GetChatAdministrators.class))).thenReturn(Optional.empty());
        Assert.assertFalse("Unexpected result when checking for privacy", bot.checkPrivacy(groupAdminTrio));
    }

    @Test
    public void canBlockAdminsFromCreatorAbilities() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        Ability creatorAbility = DefaultBot.getDefaultBuilder().privacy(Privacy.CREATOR).build();
        Trio<Update, Ability, String[]> creatorTrio = Trio.of(update, creatorAbility, AbilityBotTest.TEXT);
        admins().add(AbilityBotTest.USER.getId());
        mockUser(update, message, user);
        Assert.assertFalse("Unexpected result when checking for privacy", bot.checkPrivacy(creatorTrio));
    }

    @Test
    public void canCheckLocality() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        Ability allAbility = DefaultBot.getDefaultBuilder().locality(Locality.ALL).build();
        Ability userAbility = DefaultBot.getDefaultBuilder().locality(Locality.USER).build();
        Ability groupAbility = DefaultBot.getDefaultBuilder().locality(Locality.GROUP).build();
        Trio<Update, Ability, String[]> publicTrio = Trio.of(update, allAbility, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> userTrio = Trio.of(update, userAbility, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> groupTrio = Trio.of(update, groupAbility, AbilityBotTest.TEXT);
        mockUser(update, message, user);
        Mockito.when(message.isUserMessage()).thenReturn(true);
        Assert.assertTrue("Unexpected result when checking for locality", bot.checkLocality(publicTrio));
        Assert.assertTrue("Unexpected result when checking for locality", bot.checkLocality(userTrio));
        Assert.assertFalse("Unexpected result when checking for locality", bot.checkLocality(groupTrio));
    }

    @Test
    public void canRetrieveContext() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Ability ability = DefaultBot.getDefaultBuilder().build();
        Trio<Update, Ability, String[]> trio = Trio.of(update, ability, AbilityBotTest.TEXT);
        Mockito.when(message.getChatId()).thenReturn(AbilityBotTest.GROUP_ID);
        mockUser(update, message, AbilityBotTest.USER);
        Pair<MessageContext, Ability> actualPair = bot.getContext(trio);
        Pair<MessageContext, Ability> expectedPair = Pair.of(org.telegram.abilitybots.api.objects.MessageContext.newContext(update, AbilityBotTest.USER, AbilityBotTest.GROUP_ID, AbilityBotTest.TEXT), ability);
        Assert.assertEquals("Unexpected result when fetching for context", expectedPair, actualPair);
    }

    @Test
    public void defaultGlobalFlagIsTrue() {
        Update update = Mockito.mock(Update.class);
        Assert.assertTrue("Unexpected result when checking for the default global flags", bot.checkGlobalFlags(update));
    }

    @Test(expected = ArithmeticException.class)
    public void canConsumeUpdate() {
        Ability ability = DefaultBot.getDefaultBuilder().action(( context) -> {
            int x = 1 / 0;
        }).build();
        MessageContext context = Mockito.mock(MessageContext.class);
        Pair<MessageContext, Ability> pair = Pair.of(context, ability);
        bot.consumeUpdate(pair);
    }

    @Test
    public void canFetchAbility() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        String text = "/test";
        Mockito.when(update.hasMessage()).thenReturn(true);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(update.getMessage().hasText()).thenReturn(true);
        Mockito.when(message.getText()).thenReturn(text);
        Trio<Update, Ability, String[]> trio = bot.getAbility(update);
        Ability expected = bot.testAbility();
        Ability actual = trio.b();
        Assert.assertEquals("Wrong ability was fetched", expected, actual);
    }

    @Test
    public void canFetchAbilityCaseInsensitive() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        String text = "/tESt";
        Mockito.when(update.hasMessage()).thenReturn(true);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(update.getMessage().hasText()).thenReturn(true);
        Mockito.when(message.getText()).thenReturn(text);
        Trio<Update, Ability, String[]> trio = bot.getAbility(update);
        Ability expected = bot.testAbility();
        Ability actual = trio.b();
        Assert.assertEquals("Wrong ability was fetched", expected, actual);
    }

    @Test
    public void canFetchDefaultAbility() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        String text = "test tags";
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
        Trio<Update, Ability, String[]> trio = bot.getAbility(update);
        Ability expected = bot.defaultAbility();
        Ability actual = trio.b();
        Assert.assertEquals("Wrong ability was fetched", expected, actual);
    }

    @Test
    public void canCheckAbilityFlags() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.hasMessage()).thenReturn(true);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasDocument()).thenReturn(false);
        Mockito.when(message.hasText()).thenReturn(true);
        Ability documentAbility = DefaultBot.getDefaultBuilder().flag(Flag.DOCUMENT, Flag.MESSAGE).build();
        Ability textAbility = DefaultBot.getDefaultBuilder().flag(Flag.TEXT, Flag.MESSAGE).build();
        Trio<Update, Ability, String[]> docTrio = Trio.of(update, documentAbility, AbilityBotTest.TEXT);
        Trio<Update, Ability, String[]> textTrio = Trio.of(update, textAbility, AbilityBotTest.TEXT);
        Assert.assertFalse("Unexpected result when checking for message flags", bot.checkMessageFlags(docTrio));
        Assert.assertTrue("Unexpected result when checking for message flags", bot.checkMessageFlags(textTrio));
    }

    @Test
    public void canReportCommands() {
        MessageContext context = AbilityBotTest.mockContext(AbilityBotTest.USER, AbilityBotTest.GROUP_ID);
        reportCommands().action().accept(context);
        Mockito.verify(silent, VerificationModeFactory.times(1)).send("default - dis iz default command", AbilityBotTest.GROUP_ID);
    }

    @Test
    public void canPrintCommandsBasedOnPrivacy() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.hasMessage()).thenReturn(true);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasText()).thenReturn(true);
        MessageContext creatorCtx = newContext(update, AbilityBotTest.CREATOR, AbilityBotTest.GROUP_ID);
        commands().action().accept(creatorCtx);
        String expected = "PUBLIC\n/commands\n/count\n/default - dis iz default command\n/group\n/test\nADMIN\n/admin\n/ban\n/demote\n/promote\n/unban\nCREATOR\n/backup\n/claim\n/recover\n/report";
        Mockito.verify(silent, VerificationModeFactory.times(1)).send(expected, AbilityBotTest.GROUP_ID);
    }

    @Test
    public void printsOnlyPublicCommandsForNormalUser() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.hasMessage()).thenReturn(true);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasText()).thenReturn(true);
        MessageContext userCtx = newContext(update, AbilityBotTest.USER, AbilityBotTest.GROUP_ID);
        commands().action().accept(userCtx);
        String expected = "PUBLIC\n/commands\n/count\n/default - dis iz default command\n/group\n/test";
        Mockito.verify(silent, VerificationModeFactory.times(1)).send(expected, AbilityBotTest.GROUP_ID);
    }
}

