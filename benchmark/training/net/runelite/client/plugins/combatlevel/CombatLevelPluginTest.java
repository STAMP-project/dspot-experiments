/**
 * Copyright (c) 2018, Brett Middle <https://github.com/bmiddle>
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
package net.runelite.client.plugins.combatlevel;


import Skill.ATTACK;
import Skill.DEFENCE;
import Skill.HITPOINTS;
import Skill.MAGIC;
import Skill.PRAYER;
import Skill.RANGED;
import Skill.STRENGTH;
import com.google.inject.testing.fieldbinder.Bind;
import java.util.HashMap;
import net.runelite.api.Client;
import net.runelite.api.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CombatLevelPluginTest {
    @Mock
    @Bind
    private Client client;

    @Mock
    private Player player;

    @Test
    public void testNewPlayer() {
        Mockito.when(player.getCombatLevel()).thenReturn(3);
        Mockito.when(client.getRealSkillLevel(ATTACK)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(STRENGTH)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(DEFENCE)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(PRAYER)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(RANGED)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(MAGIC)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(HITPOINTS)).thenReturn(10);
        HashMap<String, Double> baseValues = getBaseValues();
        // test attack/strength
        Assert.assertEquals(2, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("melee"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.ATT_STR_MULT));
        // test defence/hitpoints
        Assert.assertEquals(3, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.DEF_HP_MULT));
        // test prayer
        Assert.assertEquals(5, CombatLevelOverlay.calcLevelsPray(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), client.getRealSkillLevel(PRAYER)));
        // test ranged
        Assert.assertEquals(2, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(RANGED), ((player.getCombatLevel()) + 1), baseValues.get("base")));
        // test magic
        Assert.assertEquals(2, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(MAGIC), ((player.getCombatLevel()) + 1), baseValues.get("base")));
    }

    @Test
    public void testAll10() {
        Mockito.when(player.getCombatLevel()).thenReturn(12);
        Mockito.when(client.getRealSkillLevel(ATTACK)).thenReturn(10);
        Mockito.when(client.getRealSkillLevel(STRENGTH)).thenReturn(10);
        Mockito.when(client.getRealSkillLevel(DEFENCE)).thenReturn(10);
        Mockito.when(client.getRealSkillLevel(PRAYER)).thenReturn(10);
        Mockito.when(client.getRealSkillLevel(RANGED)).thenReturn(10);
        Mockito.when(client.getRealSkillLevel(MAGIC)).thenReturn(10);
        Mockito.when(client.getRealSkillLevel(HITPOINTS)).thenReturn(10);
        HashMap<String, Double> baseValues = getBaseValues();
        // test attack/strength
        Assert.assertEquals(1, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("melee"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.ATT_STR_MULT));
        // test defence/hitpoints
        Assert.assertEquals(1, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.DEF_HP_MULT));
        // test prayer
        Assert.assertEquals(2, CombatLevelOverlay.calcLevelsPray(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), client.getRealSkillLevel(PRAYER)));
        // test ranged
        Assert.assertEquals(4, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(RANGED), ((player.getCombatLevel()) + 1), baseValues.get("base")));
        // test magic
        Assert.assertEquals(4, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(MAGIC), ((player.getCombatLevel()) + 1), baseValues.get("base")));
    }

    @Test
    public void testPlayerBmid() {
        // snapshot of current stats 2018-10-2
        Mockito.when(player.getCombatLevel()).thenReturn(83);
        Mockito.when(client.getRealSkillLevel(ATTACK)).thenReturn(65);
        Mockito.when(client.getRealSkillLevel(STRENGTH)).thenReturn(70);
        Mockito.when(client.getRealSkillLevel(DEFENCE)).thenReturn(60);
        Mockito.when(client.getRealSkillLevel(PRAYER)).thenReturn(56);
        Mockito.when(client.getRealSkillLevel(RANGED)).thenReturn(75);
        Mockito.when(client.getRealSkillLevel(MAGIC)).thenReturn(73);
        Mockito.when(client.getRealSkillLevel(HITPOINTS)).thenReturn(71);
        HashMap<String, Double> baseValues = getBaseValues();
        // test attack/strength
        Assert.assertEquals(2, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("melee"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.ATT_STR_MULT));
        // test defence/hitpoints
        Assert.assertEquals(2, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.DEF_HP_MULT));
        // test prayer
        Assert.assertEquals(4, CombatLevelOverlay.calcLevelsPray(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), client.getRealSkillLevel(PRAYER)));
        // test ranged
        Assert.assertEquals(17, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(RANGED), ((player.getCombatLevel()) + 1), baseValues.get("base")));
        // test magic
        Assert.assertEquals(19, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(MAGIC), ((player.getCombatLevel()) + 1), baseValues.get("base")));
    }

    @Test
    public void testPlayerRunelite() {
        // snapshot of current stats 2018-10-2
        Mockito.when(player.getCombatLevel()).thenReturn(43);
        Mockito.when(client.getRealSkillLevel(ATTACK)).thenReturn(43);
        Mockito.when(client.getRealSkillLevel(STRENGTH)).thenReturn(36);
        Mockito.when(client.getRealSkillLevel(DEFENCE)).thenReturn(1);
        Mockito.when(client.getRealSkillLevel(PRAYER)).thenReturn(15);
        Mockito.when(client.getRealSkillLevel(RANGED)).thenReturn(51);
        Mockito.when(client.getRealSkillLevel(MAGIC)).thenReturn(64);
        Mockito.when(client.getRealSkillLevel(HITPOINTS)).thenReturn(42);
        HashMap<String, Double> baseValues = getBaseValues();
        // test attack/strength
        Assert.assertEquals(18, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("melee"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.ATT_STR_MULT));
        // test defence/hitpoints
        Assert.assertEquals(2, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.DEF_HP_MULT));
        // test prayer
        Assert.assertEquals(3, CombatLevelOverlay.calcLevelsPray(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), client.getRealSkillLevel(PRAYER)));
        // test ranged
        Assert.assertEquals(14, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(RANGED), ((player.getCombatLevel()) + 1), baseValues.get("base")));
        // test magic
        Assert.assertEquals(1, CombatLevelOverlay.calcLevelsRM(client.getRealSkillLevel(MAGIC), ((player.getCombatLevel()) + 1), baseValues.get("base")));
    }

    @Test
    public void testPlayerZezima() {
        // snapshot of current stats 2018-10-3
        // Zezima cannot earn a combat level from ranged/magic anymore, so it won't show as the result is too high
        Mockito.when(player.getCombatLevel()).thenReturn(90);
        Mockito.when(client.getRealSkillLevel(ATTACK)).thenReturn(74);
        Mockito.when(client.getRealSkillLevel(STRENGTH)).thenReturn(74);
        Mockito.when(client.getRealSkillLevel(DEFENCE)).thenReturn(72);
        Mockito.when(client.getRealSkillLevel(PRAYER)).thenReturn(52);
        Mockito.when(client.getRealSkillLevel(RANGED)).thenReturn(44);
        Mockito.when(client.getRealSkillLevel(MAGIC)).thenReturn(60);
        Mockito.when(client.getRealSkillLevel(HITPOINTS)).thenReturn(72);
        HashMap<String, Double> baseValues = getBaseValues();
        // test attack/strength
        Assert.assertEquals(2, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("melee"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.ATT_STR_MULT));
        // test defence/hitpoints
        Assert.assertEquals(2, CombatLevelOverlay.calcLevels(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), CombatLevelOverlay.DEF_HP_MULT));
        // test prayer
        Assert.assertEquals(4, CombatLevelOverlay.calcLevelsPray(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), client.getRealSkillLevel(PRAYER)));
    }

    @Test
    public void testPrayerLevelsNeeded() {
        Mockito.when(player.getCombatLevel()).thenReturn(124);
        Mockito.when(client.getRealSkillLevel(ATTACK)).thenReturn(99);
        Mockito.when(client.getRealSkillLevel(STRENGTH)).thenReturn(99);
        Mockito.when(client.getRealSkillLevel(DEFENCE)).thenReturn(99);
        Mockito.when(client.getRealSkillLevel(PRAYER)).thenReturn(89);
        Mockito.when(client.getRealSkillLevel(RANGED)).thenReturn(99);
        Mockito.when(client.getRealSkillLevel(MAGIC)).thenReturn(99);
        Mockito.when(client.getRealSkillLevel(HITPOINTS)).thenReturn(99);
        HashMap<String, Double> baseValues = getBaseValues();
        // test prayer
        Assert.assertEquals(1, CombatLevelOverlay.calcLevelsPray(((baseValues.get("base")) + (baseValues.get("max"))), ((player.getCombatLevel()) + 1), client.getRealSkillLevel(PRAYER)));
    }
}

