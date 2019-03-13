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
package org.languagetool.dev.wikipedia.atom;


import java.sql.SQLException;
import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.CategoryId;
import org.languagetool.rules.RuleMatch;


public class MatchDatabaseTest {
    @Test
    public void test() throws ClassNotFoundException, SQLException {
        Language language = Languages.getLanguageForShortCode("de");
        MatchDatabase database = new MatchDatabase("jdbc:derby:atomFeedChecksDB;create=true", "user", "pass");
        database.dropTables();
        database.createTables();
        Assert.assertThat(database.getLatestDate(language), CoreMatchers.is(new Date(0)));
        Assert.assertThat(database.list().size(), CoreMatchers.is(0));
        Assert.assertThat(database.getCheckDates().size(), CoreMatchers.is(0));
        FakeRule rule1 = new FakeRule(1);
        setCategory(new org.languagetool.rules.Category(new CategoryId("TEST_ID"), "My Category"));
        RuleMatch ruleMatch = new RuleMatch(rule1, null, 5, 10, "my message");
        AtomFeedItem feedItem1 = new AtomFeedItem("//id1?diff=123", "title", "summary1", new Date(10000));
        WikipediaRuleMatch wikiRuleMatch1 = new WikipediaRuleMatch(language, ruleMatch, "my context", feedItem1);
        database.add(wikiRuleMatch1);
        Assert.assertThat(database.list().size(), CoreMatchers.is(1));
        Assert.assertThat(database.list().get(0).getRuleId(), CoreMatchers.is("ID_1"));
        Assert.assertThat(database.list().get(0).getRuleDescription(), CoreMatchers.is("A fake rule"));
        Assert.assertThat(database.list().get(0).getRuleMessage(), CoreMatchers.is("my message"));
        Assert.assertThat(database.list().get(0).getTitle(), CoreMatchers.is("title"));
        Assert.assertThat(database.list().get(0).getErrorContext(), CoreMatchers.is("my context"));
        Assert.assertThat(database.list().get(0).getDiffId(), CoreMatchers.is(123L));
        Assert.assertThat(database.list().get(0).getFixDiffId(), CoreMatchers.is(0L));
        Assert.assertThat(database.list().get(0).getEditDate(), CoreMatchers.is(new Date(10000)));
        Assert.assertThat(database.getLatestDate(language), CoreMatchers.is(new Date(0)));
        Assert.assertNull(database.list().get(0).getRuleSubId());
        Assert.assertNull(database.list().get(0).getFixDate());
        Assert.assertThat(database.getCheckDates().size(), CoreMatchers.is(0));
        RuleMatch ruleMatch2 = new RuleMatch(new FakeRule(1), null, 9, 11, "my message");// same ID, different character positions

        AtomFeedItem feedItem2 = new AtomFeedItem("//id2?diff=124", "title", "summary2", new Date(9000000000L));
        WikipediaRuleMatch wikiRuleMatch2 = new WikipediaRuleMatch(language, ruleMatch2, "my context", feedItem2);
        int affected = database.markedFixed(wikiRuleMatch2);
        Assert.assertThat(affected, CoreMatchers.is(1));
        Assert.assertThat(database.list().get(0).getFixDate(), CoreMatchers.is(new Date(9000000000L)));
        Assert.assertThat(database.list().get(0).getDiffId(), CoreMatchers.is(123L));
        Assert.assertThat(database.list().get(0).getFixDiffId(), CoreMatchers.is(124L));
        Assert.assertThat(database.getLatestDate(language), CoreMatchers.is(new Date(0)));
        Assert.assertThat(database.getCheckDates().size(), CoreMatchers.is(0));
    }
}

