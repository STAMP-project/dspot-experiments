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
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.GermanyGerman;


public class UppercaseSentenceStartRuleTest {
    @Test
    public void testRule() throws IOException {
        JLanguageTool lt = new JLanguageTool(new GermanyGerman());
        TestTools.disableAllRulesExcept(lt, "UPPERCASE_SENTENCE_START");
        Assert.assertEquals(2, lt.check("etwas beginnen. und der auch nicht").size());
        Assert.assertEquals(0, lt.check("sch?n").size());// not a real sentence

        Assert.assertEquals(0, lt.check("Satz").size());
        Assert.assertEquals(0, lt.check("Dies ist ein Satz. Und hier kommt noch einer").size());
        Assert.assertEquals(0, lt.check("Dies ist ein Satz. ?tsch, noch einer mit Umlaut.").size());
        Assert.assertEquals(0, lt.check("Dieser Satz ist bspw. okay so.").size());
        Assert.assertEquals(0, lt.check("Dieser Satz ist z.B. okay so.").size());
        Assert.assertEquals(0, lt.check("Dies ist ein Satz. \"Aber der hier auch!\".").size());
        Assert.assertEquals(0, lt.check("\"Dies ist ein Satz!\"").size());
        Assert.assertEquals(0, lt.check("'Dies ist ein Satz!'").size());
        Assert.assertEquals(0, lt.check("Sehr geehrte Frau Merkel,\nwie wir Ihnen schon fr\u00fcher mitgeteilt haben...").size());
        // assertEquals(0, lt.check("Dies ist ein Satz. aber das hier noch nicht").size());
        Assert.assertEquals(1, lt.check("sch?n!").size());
        Assert.assertEquals(1, lt.check("Dies ist ein Satz. ?tsch, noch einer mit Umlaut.").size());
        Assert.assertEquals(1, lt.check("Dies ist ein Satz. \"aber der hier auch!\"").size());
        Assert.assertEquals(1, lt.check("Dies ist ein Satz. ?aber der hier auch!?").size());
        Assert.assertEquals(1, lt.check("\"dies ist ein Satz!\"").size());
        Assert.assertEquals(1, lt.check("'dies ist ein Satz!'").size());
    }
}

