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
package org.languagetool.rules.pl;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;


public class DashRuleTest {
    private JLanguageTool langTool;

    private Rule rule;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        check(0, "Nie r?bmy nic na ?apu-capu.");
        check(0, "Jedzmy kogel-mogel.");
        check(0, "To jest ?adna nota ? bene, bene ? odpowiedzia? J?zek.");// not really a real mistake

        // incorrect sentences:
        check(1, "bim ? bom", new String[]{ "bim-bom" });
        check(1, "Papua?Nowa Gwinea", new String[]{ "Papua-Nowa" });
        check(1, "Papua ? Nowa Gwinea", new String[]{ "Papua-Nowa" });
        check(1, "Aix ? en ? Provence", new String[]{ "Aix-en-Provence" });
    }
}

