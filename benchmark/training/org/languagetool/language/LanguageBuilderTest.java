/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2011 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.language;


import java.io.File;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;


public class LanguageBuilderTest {
    @Test
    public void testMakeAdditionalLanguage() throws Exception {
        Language language = LanguageBuilder.makeAdditionalLanguage(new File("rules-xy-Fakelanguage.xml"));
        Assert.assertEquals("Fakelanguage", language.getName());
        Assert.assertEquals("xy", language.getShortCode());
        Assert.assertEquals(0, language.getRelevantRules(JLanguageTool.getMessageBundle(), null, Collections.emptyList()).size());
        Assert.assertTrue(language.isExternal());
    }

    @Test
    public void testIllegalFileName() throws Exception {
        try {
            LanguageBuilder.makeAdditionalLanguage(new File("foo"));
            Assert.fail();
        } catch (RuleFilenameException ignored) {
        }
    }
}

