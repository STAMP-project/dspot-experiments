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
package com.iluwatar.builder;


import Armor.CHAIN_MAIL;
import HairColor.BLOND;
import HairType.LONG_CURLY;
import Profession.WARRIOR;
import Weapon.SWORD;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static Profession.THIEF;
import static Profession.WARRIOR;


/**
 * Date: 12/6/15 - 11:01 PM
 *
 * @author Jeroen Meulemeester
 */
public class HeroTest {
    /**
     * Test if we get the expected exception when trying to create a hero without a profession
     */
    @Test
    public void testMissingProfession() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Hero.Builder(null, "Sir without a job"));
    }

    /**
     * Test if we get the expected exception when trying to create a hero without a name
     */
    @Test
    public void testMissingName() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Hero.Builder(THIEF, null));
    }

    /**
     * Test if the hero build by the builder has the correct attributes, as requested
     */
    @Test
    public void testBuildHero() throws Exception {
        final String heroName = "Sir Lancelot";
        final Hero hero = new Hero.Builder(WARRIOR, heroName).withArmor(CHAIN_MAIL).withWeapon(SWORD).withHairType(LONG_CURLY).withHairColor(BLOND).build();
        Assertions.assertNotNull(hero);
        Assertions.assertNotNull(hero.toString());
        Assertions.assertEquals(WARRIOR, hero.getProfession());
        Assertions.assertEquals(heroName, hero.getName());
        Assertions.assertEquals(CHAIN_MAIL, hero.getArmor());
        Assertions.assertEquals(SWORD, hero.getWeapon());
        Assertions.assertEquals(LONG_CURLY, hero.getHairType());
        Assertions.assertEquals(BLOND, hero.getHairColor());
    }
}

