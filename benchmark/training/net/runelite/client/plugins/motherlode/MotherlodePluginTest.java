/**
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.motherlode;


import GameState.LOGGED_IN;
import InventoryID.INVENTORY;
import ItemID.ADAMANTITE_ORE;
import ItemID.COAL;
import ItemID.GOLDEN_NUGGET;
import ItemID.RUNITE_ORE;
import Varbits.SACK_NUMBER;
import com.google.inject.testing.fieldbinder.Bind;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MotherlodePluginTest {
    @Inject
    private MotherlodePlugin motherlodePlugin;

    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    MotherlodeSession motherlodeSession;

    @Mock
    @Bind
    private MotherlodeConfig motherlodeConfig;

    @Mock
    @Bind
    private MotherlodeGemOverlay motherlodeGemOverlay;

    @Mock
    @Bind
    private MotherlodeOreOverlay motherlodeOreOverlay;

    @Mock
    @Bind
    private MotherlodeRocksOverlay motherlodeRocksOverlay;

    @Mock
    @Bind
    private MotherlodeSackOverlay motherlodeSackOverlay;

    @Mock
    @Bind
    private ScheduledExecutorService scheduledExecutorService;

    @Test
    public void testOreCounter() {
        // set inMlm
        GameStateChanged gameStateChanged = new GameStateChanged();
        gameStateChanged.setGameState(LOGGED_IN);
        motherlodePlugin.onGameStateChanged(gameStateChanged);
        // Initial sack count
        Mockito.when(client.getVar(SACK_NUMBER)).thenReturn(42);
        motherlodePlugin.onVarbitChanged(new VarbitChanged());
        // Create before inventory
        ItemContainer inventory = Mockito.mock(ItemContainer.class);
        Item[] items = new Item[]{ MotherlodePluginTest.mockItem(RUNITE_ORE, 1), MotherlodePluginTest.mockItem(GOLDEN_NUGGET, 4), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1) };
        Mockito.when(inventory.getItems()).thenReturn(items);
        Mockito.when(client.getItemContainer(INVENTORY)).thenReturn(inventory);
        // Withdraw 20
        Mockito.when(client.getVar(SACK_NUMBER)).thenReturn(22);
        motherlodePlugin.onVarbitChanged(new VarbitChanged());
        inventory = Mockito.mock(ItemContainer.class);
        // +1 rune, +4 nugget, +2 coal, +1 addy
        items = new Item[]{ MotherlodePluginTest.mockItem(RUNITE_ORE, 1), MotherlodePluginTest.mockItem(RUNITE_ORE, 1), MotherlodePluginTest.mockItem(GOLDEN_NUGGET, 8), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(COAL, 1), MotherlodePluginTest.mockItem(ADAMANTITE_ORE, 1) };
        Mockito.when(inventory.getItems()).thenReturn(items);
        Mockito.when(client.getItemContainer(INVENTORY)).thenReturn(inventory);
        // Trigger comparison
        motherlodePlugin.onItemContainerChanged(new net.runelite.api.events.ItemContainerChanged(inventory));
        Mockito.verify(motherlodeSession).updateOreFound(RUNITE_ORE, 1);
        Mockito.verify(motherlodeSession).updateOreFound(GOLDEN_NUGGET, 4);
        Mockito.verify(motherlodeSession).updateOreFound(COAL, 2);
        Mockito.verify(motherlodeSession).updateOreFound(ADAMANTITE_ORE, 1);
        Mockito.verifyNoMoreInteractions(motherlodeSession);
    }
}

