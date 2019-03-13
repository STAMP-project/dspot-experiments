/**
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2018 Martin Berglund
 */
package com.googlecode.lanterna.terminal;


import KeyType.Character;
import KeyType.Enter;
import KeyType.PageUp;
import KeyType.ReverseTab;
import com.googlecode.lanterna.input.KeyStroke;
import org.junit.Assert;
import org.junit.Test;


public class KeyTest {
    @Test
    public void testFromVim() {
        {
            KeyStroke k = KeyStroke.fromString("a");
            Assert.assertEquals(Character, k.getKeyType());
            Assert.assertEquals(new Character('a'), k.getCharacter());
            Assert.assertEquals(false, k.isCtrlDown());
            Assert.assertEquals(false, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<c-a>");
            Assert.assertEquals(Character, k.getKeyType());
            Assert.assertEquals(new Character('a'), k.getCharacter());
            Assert.assertEquals(true, k.isCtrlDown());
            Assert.assertEquals(false, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<a-a>");
            Assert.assertEquals(Character, k.getKeyType());
            Assert.assertEquals(new Character('a'), k.getCharacter());
            Assert.assertEquals(false, k.isCtrlDown());
            Assert.assertEquals(true, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<c-a-a>");
            Assert.assertEquals(k.getKeyType(), Character);
            Assert.assertEquals(new Character('a'), k.getCharacter());
            Assert.assertEquals(true, k.isCtrlDown());
            Assert.assertEquals(true, k.isAltDown());
        }
        Assert.assertEquals(ReverseTab, KeyStroke.fromString("<s-tab>").getKeyType());
        Assert.assertEquals(ReverseTab, KeyStroke.fromString("<S-tab>").getKeyType());
        Assert.assertEquals(ReverseTab, KeyStroke.fromString("<S-Tab>").getKeyType());
        Assert.assertEquals(Enter, KeyStroke.fromString("<cr>").getKeyType());
        Assert.assertEquals(PageUp, KeyStroke.fromString("<PageUp>").getKeyType());
    }
}

