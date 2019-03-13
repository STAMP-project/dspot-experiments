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
package org.languagetool;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.RejectedExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.language.Demo;
import org.languagetool.rules.MultipleWhitespaceRule;
import org.languagetool.rules.Rule;
import org.languagetool.rules.UppercaseSentenceStartRule;
import org.languagetool.rules.patterns.AbstractPatternRule;


@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class MultiThreadedJLanguageToolTest {
    @Test
    public void testCheck() throws IOException {
        MultiThreadedJLanguageTool lt1 = new MultiThreadedJLanguageTool(new Demo());
        lt1.setCleanOverlappingMatches(false);
        List<String> ruleMatchIds1 = getRuleMatchIds(lt1);
        Assert.assertEquals(9, ruleMatchIds1.size());
        lt1.shutdown();
        JLanguageTool lt2 = new JLanguageTool(new Demo());
        lt2.setCleanOverlappingMatches(false);
        List<String> ruleMatchIds2 = getRuleMatchIds(lt2);
        Assert.assertEquals(ruleMatchIds1, ruleMatchIds2);
    }

    @Test
    public void testShutdownException() throws IOException {
        MultiThreadedJLanguageTool tool = new MultiThreadedJLanguageTool(new Demo());
        getRuleMatchIds(tool);
        tool.shutdown();
        try {
            getRuleMatchIds(tool);
            Assert.fail("should have been rejected as the thread pool has been shut down");
        } catch (RejectedExecutionException ignore) {
        }
    }

    @Test
    public void testTextAnalysis() throws IOException {
        MultiThreadedJLanguageTool lt = new MultiThreadedJLanguageTool(new Demo());
        List<AnalyzedSentence> analyzedSentences = lt.analyzeText("This is a sentence. And another one.");
        MatcherAssert.assertThat(analyzedSentences.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(analyzedSentences.get(0).getTokens().length, CoreMatchers.is(10));
        MatcherAssert.assertThat(analyzedSentences.get(0).getTokensWithoutWhitespace().length, CoreMatchers.is(6));// sentence start has its own token

        MatcherAssert.assertThat(analyzedSentences.get(1).getTokens().length, CoreMatchers.is(7));
        MatcherAssert.assertThat(analyzedSentences.get(1).getTokensWithoutWhitespace().length, CoreMatchers.is(5));
        lt.shutdown();
    }

    @Test
    public void testConfigurableThreadPoolSize() throws IOException {
        MultiThreadedJLanguageTool lt = new MultiThreadedJLanguageTool(new Demo());
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), lt.getThreadPoolSize());
        lt.shutdown();
    }

    @Test
    public void testTwoRulesOnly() throws IOException {
        MultiThreadedJLanguageTool lt = new MultiThreadedJLanguageTool(new FakeLanguage() {
            @Override
            protected synchronized List<AbstractPatternRule> getPatternRules() {
                return Collections.emptyList();
            }

            @Override
            public List<Rule> getRelevantRules(ResourceBundle messages, UserConfig userConfig, List<Language> altLanguages) {
                // less rules than processors (depending on the machine), should at least not crash
                return Arrays.asList(new UppercaseSentenceStartRule(messages, this), new MultipleWhitespaceRule(messages, this));
            }
        });
        MatcherAssert.assertThat(lt.check("my test  text").size(), CoreMatchers.is(2));
        lt.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalThreadPoolSize1() {
        new MultiThreadedJLanguageTool(new Demo(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalThreadPoolSize2() {
        new MultiThreadedJLanguageTool(new Demo(), null, 0, null);
    }
}

