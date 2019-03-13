/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Daniel Naber (http://www.danielnaber.de)
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
import org.languagetool.Languages;
import org.languagetool.TestTools;


public class CompoundCoherencyRuleTest {
    private final CompoundCoherencyRule rule = new CompoundCoherencyRule(TestTools.getEnglishMessages());

    private final JLanguageTool lt = new JLanguageTool(Languages.getLanguageForShortCode("de"));

    @Test
    public void testRule() throws IOException {
        assertOkay("Ein Jugendfoto.", "Und ein Jugendfoto.");
        assertOkay("Ein Jugendfoto.", "Der Rahmen eines Jugendfotos.");
        assertOkay("Der Rahmen eines Jugendfotos.", "Ein Jugendfoto.");
        assertOkay("Der Zahn-?rzte-Verband.", "Der Zahn-?rzte-Verband.");
        assertOkay("Der Zahn-?rzte-Verband.", "Des Zahn-?rzte-Verbands.");
        assertOkay("Der Zahn-?rzte-Verband.", "Des Zahn-?rzte-Verbandes.");
        assertOkay("Es gibt E-Mail.", "Und es gibt E-Mails.");
        assertOkay("Es gibt E-Mails.", "Und es gibt E-Mail.");
        assertOkay("Ein Jugend-Foto.", "Der Rahmen eines Jugend-Fotos.");
        assertError("Ein Jugendfoto.", "Und ein Jugend-Foto.", 23, 34, "Jugendfoto");
        assertError("Ein Jugend-Foto.", "Und ein Jugendfoto.", 24, 34, "Jugend-Foto");
        assertError("Viele Zahn-?rzte.", "Oder Zahn?rzte.", 22, 31, null);
        assertError("Viele Zahn-?rzte.", "Oder Zahn?rzte.", 22, 31, null);
        assertError("Viele Zahn-?rzte.", "Oder Zahn?rzten.", 22, 32, null);
        assertError("Der Zahn-?rzte-Verband.", "Der Zahn-?rzteverband.", 27, 44, "Zahn-?rzte-Verband");
        assertError("Der Zahn-?rzte-Verband.", "Der Zahn?rzte-Verband.", 27, 44, "Zahn-?rzte-Verband");
        assertError("Der Zahn-?rzte-Verband.", "Der Zahn?rzteverband.", 27, 43, "Zahn-?rzte-Verband");
        assertError("Der Zahn-?rzteverband.", "Der Zahn-?rzte-Verband.", 26, 44, "Zahn-?rzteverband");
        assertError("Der Zahn?rzte-Verband.", "Der Zahn-?rzte-Verband.", 26, 44, "Zahn?rzte-Verband");
        assertError("Der Zahn?rzteverband.", "Der Zahn-?rzte-Verband.", 25, 43, "Zahn?rzteverband");
        assertError("Der Zahn-?rzte-Verband.", "Des Zahn-?rzteverbandes.", 27, 46, null);
        assertError("Der Zahn-?rzte-Verband.", "Des Zahn?rzte-Verbandes.", 27, 46, null);
        assertError("Der Zahn-?rzte-Verband.", "Des Zahn?rzteverbandes.", 27, 45, null);
    }
}

