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
package org.languagetool.rules.patterns;


import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.FakeLanguage;


/**
 *
 *
 * @author Daniel Naber
 */
public class PatternRuleTest extends AbstractPatternRuleTest {
    // A test sentence should only be a single sentence - if that's not the case it can
    // happen that rules are checked as being correct that in reality will never match.
    // This check prints a warning for affected rules, but it's disabled by default because
    // it makes the tests very slow:
    private static final boolean CHECK_WITH_SENTENCE_SPLITTING = false;

    private static final Pattern PATTERN_MARKER_START = Pattern.compile(".*<pattern[^>]*>\\s*<marker>.*", Pattern.DOTALL);

    private static final Pattern PATTERN_MARKER_END = Pattern.compile(".*</marker>\\s*</pattern>.*", Pattern.DOTALL);

    private static final Comparator<Match> MATCH_COMPARATOR = ( m1, m2) -> Integer.compare(m1.getTokenRef(), m2.getTokenRef());

    @Test
    public void testSupportsLanguage() {
        FakeLanguage fakeLanguage1 = new FakeLanguage("yy");
        FakeLanguage fakeLanguage2 = new FakeLanguage("zz");
        PatternRule patternRule1 = new PatternRule("ID", fakeLanguage1, Collections.<PatternToken>emptyList(), "", "", "");
        Assert.assertTrue(patternRule1.supportsLanguage(fakeLanguage1));
        Assert.assertFalse(patternRule1.supportsLanguage(fakeLanguage2));
        FakeLanguage fakeLanguage1WithVariant1 = new FakeLanguage("zz", "VAR1");
        FakeLanguage fakeLanguage1WithVariant2 = new FakeLanguage("zz", "VAR2");
        PatternRule patternRuleVariant1 = new PatternRule("ID", fakeLanguage1WithVariant1, Collections.<PatternToken>emptyList(), "", "", "");
        Assert.assertTrue(patternRuleVariant1.supportsLanguage(fakeLanguage1WithVariant1));
        Assert.assertFalse(patternRuleVariant1.supportsLanguage(fakeLanguage1));
        Assert.assertFalse(patternRuleVariant1.supportsLanguage(fakeLanguage2));
        Assert.assertFalse(patternRuleVariant1.supportsLanguage(fakeLanguage1WithVariant2));
    }
}

