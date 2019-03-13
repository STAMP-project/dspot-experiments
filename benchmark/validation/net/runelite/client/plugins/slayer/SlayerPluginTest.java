/**
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.slayer;


import ChatMessageType.PUBLIC;
import WidgetInfo.DIALOG_NPC_TEXT;
import WidgetInfo.DIALOG_SPRITE_TEXT;
import WidgetInfo.SLAYER_REWARDS_TOPBAR;
import com.google.inject.testing.fieldbinder.Bind;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.http.api.chat.ChatClient;
import net.runelite.http.api.chat.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SlayerPluginTest {
    private static final String TASK_NEW = "Your new task is to kill 231 Suqahs.";

    private static final String TASK_NEW_KONAR = "You are to bring balance to 147 Wyrms in the Karuulm Slayer Dungeon.";

    private static final String TASK_NEW_KONAR_2 = "You are to bring balance to 142 Hellhounds in Witchhaven Dungeon.";

    private static final String TASK_NEW_FIRST = "We'll start you off hunting goblins, you'll need to kill 17 of them.";

    private static final String TASK_NEW_NPC_CONTACT = "Excellent, you're doing great. Your new task is to kill<br>211 Suqahs.";

    private static final String TASK_NEW_FROM_PARTNER = "You have received a new Slayer assignment from breaklulz: Dust Devils (377)";

    private static final String TASK_CHECKSLAYERGEM = "You're assigned to kill Suqahs; only 211 more to go.";

    private static final String TASK_CHECKSLAYERGEM_WILDERNESS = "You're assigned to kill Suqahs in the Wilderness; only 211 more to go.";

    private static final String TASK_CHECKSLAYERGEM_KONAR = "You're assigned to kill Blue dragons in the Ogre Enclave; only 122 more to go.";

    private static final String TASK_UPDATE_COMBAT_BRACELET = "You still need to kill 30 monsters to complete your current Slayer assignment";

    private static final String TASK_BOSS_NEW = "Excellent. You're now assigned to kill Vet'ion 3 times.<br>Your reward point tally is 914.";

    private static final String TASK_BOSS_NEW_THE = "Excellent. You're now assigned to kill the Chaos <br>Elemental 3 times. Your reward point tally is 914.";

    private static final String TASK_EXISTING = "You're still hunting suqahs; you have 222 to go. Come<br>back when you've finished your task.";

    private static final String REWARD_POINTS = "Reward points: 17,566";

    private static final String TASK_ONE = "You've completed one task; return to a Slayer master.";

    private static final String TASK_COMPLETE_NO_POINTS = "<col=ef1020>You've completed 3 tasks; return to a Slayer master.</col>";

    private static final String TASK_POINTS = "You've completed 9 tasks and received 0 points, giving you a total of 18,000; return to a Slayer master.";

    private static final String TASK_LARGE_STREAK = "You've completed 2,465 tasks and received 15 points, giving you a total of 17,566,000; return to a Slayer master.";

    private static final String TASK_COMPLETE = "You need something new to hunt.";

    private static final String TASK_CANCELED = "Your task has been cancelled.";

    private static final String SUPERIOR_MESSAGE = "A superior foe has appeared...";

    private static final String BRACLET_SLAUGHTER = "Your bracelet of slaughter prevents your slayer count decreasing. It has 9 charges left.";

    private static final String BRACLET_EXPEDITIOUS = "Your expeditious bracelet helps you progress your slayer task faster. It has 9 charges left.";

    private static final String BRACLET_SLAUGHTER_V2 = "Your bracelet of slaughter prevents your slayer count decreasing. It has 1 charge left.";

    private static final String BRACLET_EXPEDITIOUS_V2 = "Your expeditious bracelet helps you progress your slayer faster. It has 1 charge left.";

    private static final String BRACLET_SLAUGHTER_V3 = "Your bracelet of slaughter prevents your slayer count decreasing. It then crumbles to dust.";

    private static final String BRACLET_EXPEDITIOUS_V3 = "Your expeditious bracelet helps you progress your slayer faster. It then crumbles to dust.";

    private static final String CHAT_BRACELET_SLAUGHTER_CHARGE = "Your bracelet of slaughter has 12 charges left.";

    private static final String CHAT_BRACELET_EXPEDITIOUS_CHARGE = "Your expeditious bracelet has 12 charges left.";

    private static final String CHAT_BRACELET_SLAUGHTER_CHARGE_ONE = "Your bracelet of slaughter has 1 charge left.";

    private static final String CHAT_BRACELET_EXPEDITIOUS_CHARGE_ONE = "Your expeditious bracelet has 1 charge left.";

    private static final String BREAK_SLAUGHTER = "The bracelet shatters. Your next bracelet of slaughter<br>will start afresh from 30 charges.";

    private static final String BREAK_EXPEDITIOUS = "The bracelet shatters. Your next expeditious bracelet<br>will start afresh from 30 charges.";

    @Mock
    @Bind
    Client client;

    @Mock
    @Bind
    SlayerConfig slayerConfig;

    @Mock
    @Bind
    OverlayManager overlayManager;

    @Mock
    @Bind
    SlayerOverlay overlay;

    @Mock
    @Bind
    InfoBoxManager infoBoxManager;

    @Mock
    @Bind
    ItemManager itemManager;

    @Mock
    @Bind
    Notifier notifier;

    @Mock
    @Bind
    ChatMessageManager chatMessageManager;

    @Mock
    @Bind
    ChatCommandManager chatCommandManager;

    @Mock
    @Bind
    ScheduledExecutorService executor;

    @Mock
    @Bind
    ChatClient chatClient;

    @Inject
    SlayerPlugin slayerPlugin;

    @Test
    public void testNewTask() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_NEW);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("Suqahs", slayerPlugin.getTaskName());
        Assert.assertEquals(231, slayerPlugin.getAmount());
    }

    @Test
    public void testNewKonarTask() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_NEW_KONAR);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("Wyrms", slayerPlugin.getTaskName());
        Assert.assertEquals(147, slayerPlugin.getAmount());
        Assert.assertEquals("Karuulm Slayer Dungeon", slayerPlugin.getTaskLocation());
    }

    @Test
    public void testNewKonarTask2() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_NEW_KONAR_2);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("Hellhounds", slayerPlugin.getTaskName());
        Assert.assertEquals(142, slayerPlugin.getAmount());
        Assert.assertEquals("Witchhaven Dungeon", slayerPlugin.getTaskLocation());
    }

    @Test
    public void testFirstTask() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_NEW_FIRST);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("goblins", slayerPlugin.getTaskName());
        Assert.assertEquals(17, slayerPlugin.getAmount());
    }

    @Test
    public void testNewNpcContactTask() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_NEW_NPC_CONTACT);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("Suqahs", slayerPlugin.getTaskName());
        Assert.assertEquals(211, slayerPlugin.getAmount());
    }

    @Test
    public void testBossTask() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_BOSS_NEW);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("Vet'ion", slayerPlugin.getTaskName());
        Assert.assertEquals(3, slayerPlugin.getAmount());
        Assert.assertEquals(914, slayerPlugin.getPoints());
    }

    @Test
    public void testBossTaskThe() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_BOSS_NEW_THE);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("Chaos Elemental", slayerPlugin.getTaskName());
        Assert.assertEquals(3, slayerPlugin.getAmount());
        Assert.assertEquals(914, slayerPlugin.getPoints());
    }

    @Test
    public void testPartnerTask() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.TASK_NEW_FROM_PARTNER, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals("Dust Devils", slayerPlugin.getTaskName());
        Assert.assertEquals(377, slayerPlugin.getAmount());
    }

    @Test
    public void testCheckSlayerGem() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.TASK_CHECKSLAYERGEM, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals("Suqahs", slayerPlugin.getTaskName());
        Assert.assertEquals(211, slayerPlugin.getAmount());
    }

    @Test
    public void testCheckSlayerGemWildernessTask() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.TASK_CHECKSLAYERGEM_WILDERNESS, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals("Suqahs", slayerPlugin.getTaskName());
        Assert.assertEquals(211, slayerPlugin.getAmount());
        Assert.assertEquals("Wilderness", slayerPlugin.getTaskLocation());
    }

    @Test
    public void testCheckSlayerGemKonarTask() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.TASK_CHECKSLAYERGEM_KONAR, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals("Blue dragons", slayerPlugin.getTaskName());
        Assert.assertEquals(122, slayerPlugin.getAmount());
        Assert.assertEquals("Ogre Enclave", slayerPlugin.getTaskLocation());
    }

    @Test
    public void testExistingTask() {
        Widget npcDialog = Mockito.mock(Widget.class);
        Mockito.when(npcDialog.getText()).thenReturn(SlayerPluginTest.TASK_EXISTING);
        Mockito.when(client.getWidget(DIALOG_NPC_TEXT)).thenReturn(npcDialog);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals("suqahs", slayerPlugin.getTaskName());
        Assert.assertEquals(222, slayerPlugin.getAmount());
    }

    @Test
    public void testRewardPointsWidget() {
        Widget rewardBar = Mockito.mock(Widget.class);
        Widget rewardBarText = Mockito.mock(Widget.class);
        Widget[] rewardBarChildren = new Widget[]{ rewardBarText };
        Mockito.when(rewardBar.getDynamicChildren()).thenReturn(rewardBarChildren);
        Mockito.when(rewardBarText.getText()).thenReturn(SlayerPluginTest.REWARD_POINTS);
        Mockito.when(client.getWidget(SLAYER_REWARDS_TOPBAR)).thenReturn(rewardBar);
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals(17566, slayerPlugin.getPoints());
    }

    @Test
    public void testOneTask() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Perterter", SlayerPluginTest.TASK_ONE, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(1, slayerPlugin.getStreak());
        Assert.assertEquals("", slayerPlugin.getTaskName());
        Assert.assertEquals(0, slayerPlugin.getAmount());
    }

    @Test
    public void testNoPoints() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Perterter", SlayerPluginTest.TASK_COMPLETE_NO_POINTS, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(3, slayerPlugin.getStreak());
        Assert.assertEquals("", slayerPlugin.getTaskName());
        Assert.assertEquals(0, slayerPlugin.getAmount());
    }

    @Test
    public void testPoints() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Perterter", SlayerPluginTest.TASK_POINTS, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(9, slayerPlugin.getStreak());
        Assert.assertEquals("", slayerPlugin.getTaskName());
        Assert.assertEquals(0, slayerPlugin.getAmount());
        Assert.assertEquals(18000, slayerPlugin.getPoints());
    }

    @Test
    public void testLargeStreak() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Perterter", SlayerPluginTest.TASK_LARGE_STREAK, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(2465, slayerPlugin.getStreak());
        Assert.assertEquals("", slayerPlugin.getTaskName());
        Assert.assertEquals(0, slayerPlugin.getAmount());
        Assert.assertEquals(17566000, slayerPlugin.getPoints());
    }

    @Test
    public void testComplete() {
        slayerPlugin.setTaskName("cows");
        slayerPlugin.setAmount(42);
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Perterter", SlayerPluginTest.TASK_COMPLETE, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals("", slayerPlugin.getTaskName());
        Assert.assertEquals(0, slayerPlugin.getAmount());
    }

    @Test
    public void testCancelled() {
        slayerPlugin.setTaskName("cows");
        slayerPlugin.setAmount(42);
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Perterter", SlayerPluginTest.TASK_CANCELED, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals("", slayerPlugin.getTaskName());
        Assert.assertEquals(0, slayerPlugin.getAmount());
    }

    @Test
    public void testSuperiorNotification() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "Superior", SlayerPluginTest.SUPERIOR_MESSAGE, null, 0);
        Mockito.when(slayerConfig.showSuperiorNotification()).thenReturn(true);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Mockito.verify(notifier).notify(SlayerPluginTest.SUPERIOR_MESSAGE);
        Mockito.when(slayerConfig.showSuperiorNotification()).thenReturn(false);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Mockito.verifyNoMoreInteractions(notifier);
    }

    @Test
    public void testBraceletSlaughter() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.BRACLET_SLAUGHTER, null, 0);
        slayerPlugin.setAmount(42);
        slayerPlugin.setSlaughterChargeCount(10);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(9, slayerPlugin.getSlaughterChargeCount());
        Assert.assertEquals(43, slayerPlugin.getAmount());
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.CHAT_BRACELET_SLAUGHTER_CHARGE, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(12, slayerPlugin.getSlaughterChargeCount());
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.CHAT_BRACELET_SLAUGHTER_CHARGE_ONE, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(1, slayerPlugin.getSlaughterChargeCount());
        slayerPlugin.setSlaughterChargeCount(1);
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.BRACLET_SLAUGHTER_V3, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(30, slayerPlugin.getSlaughterChargeCount());
        Widget braceletBreakWidget = Mockito.mock(Widget.class);
        Mockito.when(braceletBreakWidget.getText()).thenReturn(SlayerPluginTest.BREAK_SLAUGHTER);
        Mockito.when(client.getWidget(DIALOG_SPRITE_TEXT)).thenReturn(braceletBreakWidget);
        slayerPlugin.setSlaughterChargeCount((-1));
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals(30, slayerPlugin.getSlaughterChargeCount());
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.BRACLET_SLAUGHTER_V2, null, 0);
        slayerPlugin.setAmount(42);
        slayerPlugin.setSlaughterChargeCount(2);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(1, slayerPlugin.getSlaughterChargeCount());
        Assert.assertEquals(43, slayerPlugin.getAmount());
    }

    @Test
    public void testBraceletExpeditious() {
        ChatMessage chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.BRACLET_EXPEDITIOUS, null, 0);
        slayerPlugin.setAmount(42);
        slayerPlugin.setExpeditiousChargeCount(10);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(41, slayerPlugin.getAmount());
        Assert.assertEquals(9, slayerPlugin.getExpeditiousChargeCount());
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.CHAT_BRACELET_EXPEDITIOUS_CHARGE, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(12, slayerPlugin.getExpeditiousChargeCount());
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.CHAT_BRACELET_EXPEDITIOUS_CHARGE_ONE, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(1, slayerPlugin.getExpeditiousChargeCount());
        slayerPlugin.setExpeditiousChargeCount(1);
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.BRACLET_EXPEDITIOUS_V3, null, 0);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(30, slayerPlugin.getExpeditiousChargeCount());
        Widget braceletBreakWidget = Mockito.mock(Widget.class);
        Mockito.when(braceletBreakWidget.getText()).thenReturn(SlayerPluginTest.BREAK_EXPEDITIOUS);
        Mockito.when(client.getWidget(DIALOG_SPRITE_TEXT)).thenReturn(braceletBreakWidget);
        slayerPlugin.setExpeditiousChargeCount((-1));
        slayerPlugin.onGameTick(new GameTick());
        Assert.assertEquals(30, slayerPlugin.getExpeditiousChargeCount());
        chatMessageEvent = new ChatMessage(null, SERVER, "", SlayerPluginTest.BRACLET_EXPEDITIOUS_V2, null, 0);
        slayerPlugin.setAmount(42);
        slayerPlugin.setExpeditiousChargeCount(2);
        slayerPlugin.onChatMessage(chatMessageEvent);
        Assert.assertEquals(41, slayerPlugin.getAmount());
        Assert.assertEquals(1, slayerPlugin.getExpeditiousChargeCount());
    }

    @Test
    public void testCombatBraceletUpdate() {
        final Player player = Mockito.mock(Player.class);
        Mockito.when(player.getLocalLocation()).thenReturn(new LocalPoint(0, 0));
        Mockito.when(client.getLocalPlayer()).thenReturn(player);
        slayerPlugin.setTaskName("Suqahs");
        slayerPlugin.setAmount(231);
        ChatMessage chatMessage = new ChatMessage(null, SERVER, "", SlayerPluginTest.TASK_UPDATE_COMBAT_BRACELET, null, 0);
        slayerPlugin.onChatMessage(chatMessage);
        Assert.assertEquals("Suqahs", slayerPlugin.getTaskName());
        slayerPlugin.killedOne();
        Assert.assertEquals(30, slayerPlugin.getAmount());
    }

    @Test
    public void testTaskLookup() throws IOException {
        Task task = new Task();
        task.setTask("task");
        task.setLocation("loc");
        task.setAmount(42);
        task.setInitialAmount(42);
        Mockito.when(slayerConfig.taskCommand()).thenReturn(true);
        Mockito.when(chatClient.getTask(ArgumentMatchers.anyString())).thenReturn(task);
        ChatMessage setMessage = new ChatMessage();
        setMessage.setType(PUBLIC);
        setMessage.setName("Adam");
        setMessage.setMessageNode(Mockito.mock(MessageNode.class));
        slayerPlugin.taskLookup(setMessage, "!task");
        Mockito.verify(chatMessageManager).update(ArgumentMatchers.any(MessageNode.class));
    }

    @Test
    public void testTaskLookupInvalid() throws IOException {
        Task task = new Task();
        task.setTask("task<");
        task.setLocation("loc");
        task.setAmount(42);
        task.setInitialAmount(42);
        Mockito.when(slayerConfig.taskCommand()).thenReturn(true);
        Mockito.when(chatClient.getTask(ArgumentMatchers.anyString())).thenReturn(task);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(PUBLIC);
        chatMessage.setName("Adam");
        chatMessage.setMessageNode(Mockito.mock(MessageNode.class));
        slayerPlugin.taskLookup(chatMessage, "!task");
        Mockito.verify(chatMessageManager, Mockito.never()).update(ArgumentMatchers.any(MessageNode.class));
    }
}

