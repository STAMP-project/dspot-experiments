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
package com.iluwatar.abstractfactory;


import ElfKing.DESCRIPTION;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test for abstract factory
 */
public class AbstractFactoryTest {
    private App app = new App();

    private KingdomFactory elfFactory;

    private KingdomFactory orcFactory;

    @Test
    public void king() {
        final King elfKing = app.getKing(elfFactory);
        Assertions.assertTrue((elfKing instanceof ElfKing));
        Assertions.assertEquals(DESCRIPTION, elfKing.getDescription());
        final King orcKing = app.getKing(orcFactory);
        Assertions.assertTrue((orcKing instanceof OrcKing));
        Assertions.assertEquals(OrcKing.DESCRIPTION, orcKing.getDescription());
    }

    @Test
    public void castle() {
        final Castle elfCastle = app.getCastle(elfFactory);
        Assertions.assertTrue((elfCastle instanceof ElfCastle));
        Assertions.assertEquals(ElfCastle.DESCRIPTION, elfCastle.getDescription());
        final Castle orcCastle = app.getCastle(orcFactory);
        Assertions.assertTrue((orcCastle instanceof OrcCastle));
        Assertions.assertEquals(OrcCastle.DESCRIPTION, orcCastle.getDescription());
    }

    @Test
    public void army() {
        final Army elfArmy = app.getArmy(elfFactory);
        Assertions.assertTrue((elfArmy instanceof ElfArmy));
        Assertions.assertEquals(ElfArmy.DESCRIPTION, elfArmy.getDescription());
        final Army orcArmy = app.getArmy(orcFactory);
        Assertions.assertTrue((orcArmy instanceof OrcArmy));
        Assertions.assertEquals(OrcArmy.DESCRIPTION, orcArmy.getDescription());
    }

    @Test
    public void createElfKingdom() {
        app.createKingdom(elfFactory);
        final King king = app.getKing();
        final Castle castle = app.getCastle();
        final Army army = app.getArmy();
        Assertions.assertTrue((king instanceof ElfKing));
        Assertions.assertEquals(DESCRIPTION, king.getDescription());
        Assertions.assertTrue((castle instanceof ElfCastle));
        Assertions.assertEquals(ElfCastle.DESCRIPTION, castle.getDescription());
        Assertions.assertTrue((army instanceof ElfArmy));
        Assertions.assertEquals(ElfArmy.DESCRIPTION, army.getDescription());
    }

    @Test
    public void createOrcKingdom() {
        app.createKingdom(orcFactory);
        final King king = app.getKing();
        final Castle castle = app.getCastle();
        final Army army = app.getArmy();
        Assertions.assertTrue((king instanceof OrcKing));
        Assertions.assertEquals(OrcKing.DESCRIPTION, king.getDescription());
        Assertions.assertTrue((castle instanceof OrcCastle));
        Assertions.assertEquals(OrcCastle.DESCRIPTION, castle.getDescription());
        Assertions.assertTrue((army instanceof OrcArmy));
        Assertions.assertEquals(OrcArmy.DESCRIPTION, army.getDescription());
    }
}

