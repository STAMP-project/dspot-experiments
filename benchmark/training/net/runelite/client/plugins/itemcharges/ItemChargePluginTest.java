/**
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.itemcharges;


import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.Notifier;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ItemChargePluginTest {
    private static final String CHECK = "Your dodgy necklace has 10 charges left.";

    private static final String PROTECT = "Your dodgy necklace protects you. It has 9 charges left.";

    private static final String PROTECT_1 = "Your dodgy necklace protects you. <col=ff0000>It has 1 charge left.</col>";

    private static final String BREAK = "Your dodgy necklace protects you. <col=ff0000>It then crumbles to dust.</col>";

    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    private ScheduledExecutorService scheduledExecutorService;

    @Mock
    @Bind
    private RuneLiteConfig runeLiteConfig;

    @Mock
    @Bind
    private OverlayManager overlayManager;

    @Mock
    @Bind
    private Notifier notifier;

    @Mock
    @Bind
    private ItemChargeConfig config;

    @Inject
    private ItemChargePlugin itemChargePlugin;

    @Test
    public void testOnChatMessage() {
        ChatMessage chatMessage = new ChatMessage(null, ChatMessageType.SERVER, "", ItemChargePluginTest.CHECK, "", 0);
        itemChargePlugin.onChatMessage(chatMessage);
        Mockito.verify(config).dodgyNecklace(ArgumentMatchers.eq(10));
        Mockito.reset(config);
        chatMessage = new ChatMessage(null, ChatMessageType.SERVER, "", ItemChargePluginTest.PROTECT, "", 0);
        itemChargePlugin.onChatMessage(chatMessage);
        Mockito.verify(config).dodgyNecklace(ArgumentMatchers.eq(9));
        Mockito.reset(config);
        chatMessage = new ChatMessage(null, ChatMessageType.SERVER, "", ItemChargePluginTest.PROTECT_1, "", 0);
        itemChargePlugin.onChatMessage(chatMessage);
        Mockito.verify(config).dodgyNecklace(ArgumentMatchers.eq(1));
        Mockito.reset(config);
        chatMessage = new ChatMessage(null, ChatMessageType.SERVER, "", ItemChargePluginTest.BREAK, "", 0);
        itemChargePlugin.onChatMessage(chatMessage);
        Mockito.verify(config).dodgyNecklace(ArgumentMatchers.eq(10));
        Mockito.reset(config);
    }
}

