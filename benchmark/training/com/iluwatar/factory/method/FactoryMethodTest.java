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
package com.iluwatar.factory.method;


import WeaponType.AXE;
import WeaponType.SHORT_SWORD;
import WeaponType.SPEAR;
import org.junit.jupiter.api.Test;


/**
 * The Factory Method is a creational design pattern which uses factory methods to deal with the
 * problem of creating objects without specifying the exact class of object that will be created.
 * This is done by creating objects via calling a factory method either specified in an interface
 * and implemented by child classes, or implemented in a base class and optionally overridden by
 * derived classes?rather than by calling a constructor.
 *
 * <p>Factory produces the object of its liking.
 * The weapon {@link Weapon} manufactured by the
 * blacksmith depends on the kind of factory implementation it is referring to.
 * </p>
 */
public class FactoryMethodTest {
    /**
     * Testing {@link OrcBlacksmith} to produce a SPEAR asserting that the Weapon is an instance
     * of {@link OrcWeapon}.
     */
    @Test
    public void testOrcBlacksmithWithSpear() {
        Blacksmith blacksmith = new OrcBlacksmith();
        Weapon weapon = blacksmith.manufactureWeapon(SPEAR);
        verifyWeapon(weapon, SPEAR, OrcWeapon.class);
    }

    /**
     * Testing {@link OrcBlacksmith} to produce an AXE asserting that the Weapon is an instance
     *  of {@link OrcWeapon}.
     */
    @Test
    public void testOrcBlacksmithWithAxe() {
        Blacksmith blacksmith = new OrcBlacksmith();
        Weapon weapon = blacksmith.manufactureWeapon(AXE);
        verifyWeapon(weapon, AXE, OrcWeapon.class);
    }

    /**
     * Testing {@link ElfBlacksmith} to produce a SHORT_SWORD asserting that the Weapon is an
     * instance of {@link ElfWeapon}.
     */
    @Test
    public void testElfBlacksmithWithShortSword() {
        Blacksmith blacksmith = new ElfBlacksmith();
        Weapon weapon = blacksmith.manufactureWeapon(SHORT_SWORD);
        verifyWeapon(weapon, SHORT_SWORD, ElfWeapon.class);
    }

    /**
     * Testing {@link ElfBlacksmith} to produce a SPEAR asserting that the Weapon is an instance
     * of {@link ElfWeapon}.
     */
    @Test
    public void testElfBlacksmithWithSpear() {
        Blacksmith blacksmith = new ElfBlacksmith();
        Weapon weapon = blacksmith.manufactureWeapon(SPEAR);
        verifyWeapon(weapon, SPEAR, ElfWeapon.class);
    }
}

