/**
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.idlenotifier;


import AnimationID.IDLE;
import AnimationID.LOOKING_INTO;
import AnimationID.WOODCUTTING_BRONZE;
import GameState.LOGGED_IN;
import GameState.LOGIN_SCREEN;
import Hitsplat.HitsplatType;
import VarPlayer.SPECIAL_ATTACK_PERCENT;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.Notifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class IdleNotifierPluginTest {
    private static final String PLAYER_NAME = "Deathbeam";

    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    private IdleNotifierConfig config;

    @Mock
    @Bind
    private Notifier notifier;

    @Inject
    private IdleNotifierPlugin plugin;

    @Mock
    private NPC monster;

    @Mock
    private NPC randomEvent;

    @Mock
    private Player player;

    @Test
    public void checkAnimationIdle() {
        Mockito.when(player.getAnimation()).thenReturn(WOODCUTTING_BRONZE);
        AnimationChanged animationChanged = new AnimationChanged();
        animationChanged.setActor(player);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        Mockito.when(player.getAnimation()).thenReturn(IDLE);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier).notify((("[" + (IdleNotifierPluginTest.PLAYER_NAME)) + "] is now idle!"));
    }

    @Test
    public void checkAnimationReset() {
        Mockito.when(player.getAnimation()).thenReturn(WOODCUTTING_BRONZE);
        AnimationChanged animationChanged = new AnimationChanged();
        animationChanged.setActor(player);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        Mockito.when(player.getAnimation()).thenReturn(LOOKING_INTO);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        Mockito.when(player.getAnimation()).thenReturn(IDLE);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier, Mockito.times(0)).notify(ArgumentMatchers.any());
    }

    @Test
    public void checkAnimationLogout() {
        Mockito.when(player.getAnimation()).thenReturn(WOODCUTTING_BRONZE);
        AnimationChanged animationChanged = new AnimationChanged();
        animationChanged.setActor(player);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        // Logout
        Mockito.when(client.getGameState()).thenReturn(LOGIN_SCREEN);
        GameStateChanged gameStateChanged = new GameStateChanged();
        gameStateChanged.setGameState(LOGIN_SCREEN);
        plugin.onGameStateChanged(gameStateChanged);
        // Log back in
        Mockito.when(client.getGameState()).thenReturn(LOGGED_IN);
        gameStateChanged.setGameState(LOGGED_IN);
        plugin.onGameStateChanged(gameStateChanged);
        // Tick
        Mockito.when(player.getAnimation()).thenReturn(IDLE);
        plugin.onAnimationChanged(animationChanged);
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier, Mockito.times(0)).notify(ArgumentMatchers.any());
    }

    @Test
    public void checkCombatIdle() {
        Mockito.when(player.getInteracting()).thenReturn(monster);
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, monster));
        plugin.onGameTick(new GameTick());
        Mockito.when(player.getInteracting()).thenReturn(null);
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, null));
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier).notify((("[" + (IdleNotifierPluginTest.PLAYER_NAME)) + "] is now out of combat!"));
    }

    @Test
    public void checkCombatReset() {
        Mockito.when(player.getInteracting()).thenReturn(monster);
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, monster));
        plugin.onGameTick(new GameTick());
        Mockito.when(player.getInteracting()).thenReturn(randomEvent);
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, randomEvent));
        plugin.onGameTick(new GameTick());
        Mockito.when(player.getInteracting()).thenReturn(null);
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, null));
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier, Mockito.times(0)).notify(ArgumentMatchers.any());
    }

    @Test
    public void checkCombatLogout() {
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, monster));
        Mockito.when(player.getInteracting()).thenReturn(monster);
        plugin.onGameTick(new GameTick());
        // Logout
        Mockito.when(client.getGameState()).thenReturn(LOGIN_SCREEN);
        GameStateChanged gameStateChanged = new GameStateChanged();
        gameStateChanged.setGameState(LOGIN_SCREEN);
        plugin.onGameStateChanged(gameStateChanged);
        // Log back in
        Mockito.when(client.getGameState()).thenReturn(LOGGED_IN);
        gameStateChanged.setGameState(LOGGED_IN);
        plugin.onGameStateChanged(gameStateChanged);
        // Tick
        Mockito.when(player.getInteracting()).thenReturn(null);
        plugin.onInteractingChanged(new net.runelite.api.events.InteractingChanged(player, null));
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier, Mockito.times(0)).notify(ArgumentMatchers.any());
    }

    @Test
    public void checkCombatLogoutIdle() {
        // Player is idle
        Mockito.when(client.getMouseIdleTicks()).thenReturn(80000);
        // But player is being damaged (is in combat)
        final HitsplatApplied hitsplatApplied = new HitsplatApplied();
        hitsplatApplied.setActor(player);
        hitsplatApplied.setHitsplat(new net.runelite.api.Hitsplat(HitsplatType.DAMAGE, 0, 0));
        plugin.onHitsplatApplied(hitsplatApplied);
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier, Mockito.times(0)).notify(ArgumentMatchers.any());
    }

    @Test
    public void doubleNotifyOnMouseReset() {
        // Player is idle, but in combat so the idle packet is getting set repeatedly
        // make sure we are not notifying
        Mockito.when(client.getKeyboardIdleTicks()).thenReturn(80000);
        Mockito.when(client.getMouseIdleTicks()).thenReturn(14500);
        plugin.onGameTick(new GameTick());
        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier, Mockito.times(1)).notify(ArgumentMatchers.any());
    }

    @Test
    public void testSpecRegen() {
        Mockito.when(config.getSpecEnergyThreshold()).thenReturn(50);
        Mockito.when(client.getVar(Matchers.eq(SPECIAL_ATTACK_PERCENT))).thenReturn(400);// 40%

        plugin.onGameTick(new GameTick());// once to set lastSpecEnergy to 400

        Mockito.verify(notifier, Mockito.never()).notify(ArgumentMatchers.any());
        Mockito.when(client.getVar(Matchers.eq(SPECIAL_ATTACK_PERCENT))).thenReturn(500);// 50%

        plugin.onGameTick(new GameTick());
        Mockito.verify(notifier).notify(Matchers.eq((("[" + (IdleNotifierPluginTest.PLAYER_NAME)) + "] has restored spec energy!")));
    }
}

