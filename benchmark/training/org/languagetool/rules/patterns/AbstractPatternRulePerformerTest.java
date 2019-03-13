/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.chunking.ChunkTag;
import org.languagetool.language.Demo;


public class AbstractPatternRulePerformerTest {
    @Test
    public void testTestAllReadings() throws Exception {
        PatternToken patternToken1 = new PatternToken("foo", false, false, false);
        PatternRule simpleRule = new PatternRule("FAKE", new Demo(), Collections.singletonList(patternToken1), "descr", "message", "short");
        PatternTokenMatcher elemMatcher = new PatternTokenMatcher(patternToken1);
        AbstractPatternRulePerformer p = new AbstractPatternRulePerformerTest.MockAbstractPatternRulePerformer(simpleRule, new Unifier(null, null));
        Assert.assertTrue(p.testAllReadings(tokenReadings("foo", null), elemMatcher, null, 0, 0, 0));
        Assert.assertFalse(p.testAllReadings(tokenReadings("bar", null), elemMatcher, null, 0, 0, 0));
        Assert.assertTrue(p.testAllReadings(tokenReadings("foo", "myChunk"), elemMatcher, null, 0, 0, 0));
        Assert.assertTrue(p.testAllReadings(tokenReadings("foo", "otherChunk"), elemMatcher, null, 0, 0, 0));
    }

    @Test
    public void testTestAllReadingsWithChunks() throws Exception {
        PatternToken chunkPatternToken = new PatternToken(null, false, false, false);
        chunkPatternToken.setChunkTag(new ChunkTag("myChunk"));
        PatternRule simpleRule = new PatternRule("FAKE", new Demo(), Collections.singletonList(chunkPatternToken), "descr", "message", "short");
        PatternTokenMatcher elemMatcher = new PatternTokenMatcher(chunkPatternToken);
        AbstractPatternRulePerformer p = new AbstractPatternRulePerformerTest.MockAbstractPatternRulePerformer(simpleRule, new Unifier(null, null));
        Assert.assertFalse(p.testAllReadings(tokenReadings("bar", null), elemMatcher, null, 0, 0, 0));
        Assert.assertTrue(p.testAllReadings(tokenReadings("bar", "myChunk"), elemMatcher, null, 0, 0, 0));
        Assert.assertFalse(p.testAllReadings(tokenReadings("bar", "otherChunk"), elemMatcher, null, 0, 0, 0));
    }

    static class MockAbstractPatternRulePerformer extends AbstractPatternRulePerformer {
        protected MockAbstractPatternRulePerformer(AbstractPatternRule rule, Unifier unifier) {
            super(rule, unifier);
        }
    }
}

