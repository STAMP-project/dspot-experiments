/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.stepbuilder;


import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/29/15 - 9:21 PM
 *
 * @author Jeroen Meulemeester
 */
public class CharacterStepBuilderTest {
    /**
     * Build a new wizard {@link Character} and verify if it has the expected attributes
     */
    @Test
    public void testBuildWizard() {
        final Character character = CharacterStepBuilder.newBuilder().name("Merlin").wizardClass("alchemist").withSpell("poison").withAbility("invisibility").withAbility("wisdom").noMoreAbilities().build();
        Assertions.assertEquals("Merlin", character.getName());
        Assertions.assertEquals("alchemist", getWizardClass());
        Assertions.assertEquals("poison", getSpell());
        Assertions.assertNotNull(character.toString());
        final List<String> abilities = getAbilities();
        Assertions.assertNotNull(abilities);
        Assertions.assertEquals(2, abilities.size());
        Assertions.assertTrue(abilities.contains("invisibility"));
        Assertions.assertTrue(abilities.contains("wisdom"));
    }

    /**
     * Build a new wizard {@link Character} without spell or abilities and verify if it has the
     * expected attributes
     */
    @Test
    public void testBuildPoorWizard() {
        final Character character = CharacterStepBuilder.newBuilder().name("Merlin").wizardClass("alchemist").noSpell().build();
        Assertions.assertEquals("Merlin", character.getName());
        Assertions.assertEquals("alchemist", getWizardClass());
        Assertions.assertNull(getSpell());
        Assertions.assertNull(character.getAbilities());
        Assertions.assertNotNull(character.toString());
    }

    /**
     * Build a new wizard {@link Character} and verify if it has the expected attributes
     */
    @Test
    public void testBuildWeakWizard() {
        final Character character = CharacterStepBuilder.newBuilder().name("Merlin").wizardClass("alchemist").withSpell("poison").noAbilities().build();
        Assertions.assertEquals("Merlin", character.getName());
        Assertions.assertEquals("alchemist", getWizardClass());
        Assertions.assertEquals("poison", getSpell());
        Assertions.assertNull(character.getAbilities());
        Assertions.assertNotNull(character.toString());
    }

    /**
     * Build a new warrior {@link Character} and verify if it has the expected attributes
     */
    @Test
    public void testBuildWarrior() {
        final Character character = CharacterStepBuilder.newBuilder().name("Cuauhtemoc").fighterClass("aztec").withWeapon("spear").withAbility("speed").withAbility("strength").noMoreAbilities().build();
        Assertions.assertEquals("Cuauhtemoc", character.getName());
        Assertions.assertEquals("aztec", getFighterClass());
        Assertions.assertEquals("spear", getWeapon());
        Assertions.assertNotNull(character.toString());
        final List<String> abilities = getAbilities();
        Assertions.assertNotNull(abilities);
        Assertions.assertEquals(2, abilities.size());
        Assertions.assertTrue(abilities.contains("speed"));
        Assertions.assertTrue(abilities.contains("strength"));
    }

    /**
     * Build a new wizard {@link Character} without weapon and abilities and verify if it has the
     * expected attributes
     */
    @Test
    public void testBuildPoorWarrior() {
        final Character character = CharacterStepBuilder.newBuilder().name("Poor warrior").fighterClass("none").noWeapon().build();
        Assertions.assertEquals("Poor warrior", character.getName());
        Assertions.assertEquals("none", getFighterClass());
        Assertions.assertNull(getWeapon());
        Assertions.assertNull(character.getAbilities());
        Assertions.assertNotNull(character.toString());
    }

    /**
     * Build a new warrior {@link Character} without any abilities, but with a weapon and verify if it
     * has the expected attributes
     */
    @Test
    public void testBuildWeakWarrior() {
        final Character character = CharacterStepBuilder.newBuilder().name("Weak warrior").fighterClass("none").withWeapon("Slingshot").noAbilities().build();
        Assertions.assertEquals("Weak warrior", character.getName());
        Assertions.assertEquals("none", getFighterClass());
        Assertions.assertEquals("Slingshot", getWeapon());
        Assertions.assertNull(character.getAbilities());
        Assertions.assertNotNull(character.toString());
    }
}

