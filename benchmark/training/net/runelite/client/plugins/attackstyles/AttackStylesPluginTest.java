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
package net.runelite.client.plugins.attackstyles;


import AttackStyle.ACCURATE;
import AttackStyle.AGGRESSIVE;
import Skill.ATTACK;
import VarPlayer.ATTACK_STYLE;
import Varbits.EQUIPPED_WEAPON_TYPE;
import WeaponType.TYPE_4;
import WidgetInfo.COMBAT_STYLE_ONE;
import WidgetInfo.COMBAT_STYLE_THREE;
import WidgetInfo.COMBAT_STYLE_TWO;
import com.google.inject.testing.fieldbinder.Bind;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.ConfigChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AttackStylesPluginTest {
    @Mock
    @Bind
    Client client;

    @Mock
    @Bind
    OverlayManager overlayManager;

    @Mock
    @Bind
    AttackStylesConfig attackConfig;

    @Inject
    AttackStylesPlugin attackPlugin;

    /* Verify that red text is displayed when attacking with a style that gains experience
    in one of the unwanted skills.
     */
    @Test
    public void testWarning() {
        ConfigChanged warnForAttackEvent = new ConfigChanged();
        warnForAttackEvent.setGroup("attackIndicator");
        warnForAttackEvent.setKey("warnForAttack");
        warnForAttackEvent.setNewValue("true");
        attackPlugin.onConfigChanged(warnForAttackEvent);
        // Verify there is a warned skill
        Set<Skill> warnedSkills = attackPlugin.getWarnedSkills();
        Assert.assertTrue(warnedSkills.contains(ATTACK));
        // Set mock client to attack in style that gives attack xp
        Mockito.when(client.getVar(ATTACK_STYLE)).thenReturn(ACCURATE.ordinal());
        // verify that earning xp in a warned skill will display red text on the widget
        attackPlugin.onVarbitChanged(new VarbitChanged());
        Assert.assertTrue(attackPlugin.isWarnedSkillSelected());
        // Switch to attack style that doesn't give attack xp
        Mockito.when(client.getVar(ATTACK_STYLE)).thenReturn(AGGRESSIVE.ordinal());
        // Verify the widget will now display white text
        attackPlugin.onVarbitChanged(new VarbitChanged());
        warnedSkills = attackPlugin.getWarnedSkills();
        Assert.assertTrue(warnedSkills.contains(ATTACK));
        Assert.assertFalse(attackPlugin.isWarnedSkillSelected());
    }

    /* Verify that attack style widgets are hidden when filtered with the AttackStylesPlugin. */
    @Test
    public void testHiddenWidget() {
        ConfigChanged warnForAttackEvent = new ConfigChanged();
        warnForAttackEvent.setGroup("attackIndicator");
        warnForAttackEvent.setKey("warnForAttack");
        warnForAttackEvent.setNewValue("true");
        attackPlugin.onConfigChanged(warnForAttackEvent);
        // Set up mock widgets for atk and str attack styles
        Widget atkWidget = Mockito.mock(Widget.class);
        Widget strWidget = Mockito.mock(Widget.class);
        Mockito.when(client.getWidget(COMBAT_STYLE_ONE)).thenReturn(atkWidget);
        Mockito.when(client.getWidget(COMBAT_STYLE_TWO)).thenReturn(strWidget);
        // Set widgets to return their hidden value in widgetsToHide when isHidden() is called
        Mockito.when(atkWidget.isHidden()).thenAnswer(( x) -> isAtkHidden());
        Mockito.when(strWidget.isHidden()).thenAnswer(( x) -> isStrHidden());
        // equip type_4 weapon type on player
        Mockito.when(client.getVar(EQUIPPED_WEAPON_TYPE)).thenReturn(TYPE_4.ordinal());
        attackPlugin.onVarbitChanged(new VarbitChanged());
        // Verify there is a warned skill
        Set<Skill> warnedSkills = attackPlugin.getWarnedSkills();
        Assert.assertTrue(warnedSkills.contains(ATTACK));
        // Enable hiding widgets
        ConfigChanged hideWidgetEvent = new ConfigChanged();
        hideWidgetEvent.setGroup("attackIndicator");
        hideWidgetEvent.setKey("removeWarnedStyles");
        hideWidgetEvent.setNewValue("true");
        attackPlugin.onConfigChanged(hideWidgetEvent);
        Mockito.when(attackConfig.removeWarnedStyles()).thenReturn(true);
        // verify that the accurate attack style widget is hidden
        Assert.assertTrue(atkWidget.isHidden());
        // add another warned skill
        ConfigChanged warnForStrengthEvent = new ConfigChanged();
        warnForStrengthEvent.setGroup("attackIndicator");
        warnForStrengthEvent.setKey("warnForStrength");
        warnForStrengthEvent.setNewValue("true");
        attackPlugin.onConfigChanged(warnForStrengthEvent);
        // verify that the aggressive attack style widget is now hidden
        Assert.assertTrue(strWidget.isHidden());
        // disable hiding attack style widgets
        hideWidgetEvent.setGroup("attackIndicator");
        hideWidgetEvent.setKey("removeWarnedStyles");
        hideWidgetEvent.setNewValue("false");
        attackPlugin.onConfigChanged(hideWidgetEvent);
        Mockito.when(attackConfig.removeWarnedStyles()).thenReturn(false);
        // verify that the aggressive and accurate attack style widgets are no longer hidden
        Assert.assertFalse(attackPlugin.getHiddenWidgets().get(TYPE_4, COMBAT_STYLE_ONE));
        Assert.assertFalse(attackPlugin.getHiddenWidgets().get(TYPE_4, COMBAT_STYLE_THREE));
    }
}

