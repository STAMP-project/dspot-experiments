/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.de;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.GermanyGerman;


public class DashRuleTest {
    private final DashRule rule = new DashRule(TestTools.getMessages("de"));

    @Test
    public void testRule() throws IOException {
        JLanguageTool lt = new JLanguageTool(new GermanyGerman());
        // correct sentences:
        assertGood("Die gro?e Di?ten-Erh?hung kam dann doch.", lt);
        assertGood("Die gro?e Di?tenerh?hung kam dann doch.", lt);
        assertGood("Die gro?e Di?ten-Erh?hungs-Manie kam dann doch.", lt);
        assertGood("Die gro?e Di?ten- und Gehaltserh?hung kam dann doch.", lt);
        assertGood("Die gro?e Di?ten- sowie Gehaltserh?hung kam dann doch.", lt);
        assertGood("Die gro?e Di?ten- oder Gehaltserh?hung kam dann doch.", lt);
        assertGood("Erst so - Karl-Heinz dann blah.", lt);
        assertGood("Erst so -- Karl-Heinz aber...", lt);
        assertGood("Nord- und S?dkorea", lt);
        assertGood("NORD- UND S?DKOREA", lt);
        assertGood("NORD- BZW. S?DKOREA", lt);
        // incorrect sentences:
        assertBad("Die gro?e Di?ten- Erh?hung kam dann doch.", lt);
        assertBad("Die gro?e Di?ten-  Erh?hung kam dann doch.", lt);
        assertBad("Die gro?e Di?ten-Erh?hungs- Manie kam dann doch.", lt);
        assertBad("Die gro?e Di?ten- Erh?hungs-Manie kam dann doch.", lt);
        assertBad("MAZEDONIEN- SKOPJE Str.", lt);
    }
}

