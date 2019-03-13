/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.gui;


import org.junit.Assert;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.language.English;


public class RuleLinkTest {
    @Test
    public void testBuildDeactivationLink() {
        Language language = new English();
        RuleLink ruleLink = RuleLink.buildDeactivationLink(new org.languagetool.rules.WordRepeatRule(TestTools.getMessages(language.getShortCode()), language));
        Assert.assertEquals("WORD_REPEAT_RULE", ruleLink.getId());
        Assert.assertEquals("http://languagetool.org/deactivate/WORD_REPEAT_RULE", ruleLink.toString());
    }

    @Test
    public void testBuildReactivationLink() {
        Language language = new English();
        RuleLink ruleLink = RuleLink.buildReactivationLink(new org.languagetool.rules.WordRepeatRule(TestTools.getMessages(language.getShortCode()), language));
        Assert.assertEquals("WORD_REPEAT_RULE", ruleLink.getId());
        Assert.assertEquals("http://languagetool.org/reactivate/WORD_REPEAT_RULE", ruleLink.toString());
    }

    @Test
    public void testGetFromString() {
        RuleLink ruleLink1 = RuleLink.getFromString("http://languagetool.org/reactivate/FOO_BAR_ID");
        Assert.assertEquals("FOO_BAR_ID", ruleLink1.getId());
        Assert.assertEquals("http://languagetool.org/reactivate/FOO_BAR_ID", ruleLink1.toString());
        RuleLink ruleLink2 = RuleLink.getFromString("http://languagetool.org/deactivate/FOO_BAR_ID2");
        Assert.assertEquals("FOO_BAR_ID2", ruleLink2.getId());
        Assert.assertEquals("http://languagetool.org/deactivate/FOO_BAR_ID2", ruleLink2.toString());
    }
}

