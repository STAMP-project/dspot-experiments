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


import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.language.German;


public class AtomFeedCheckerTest {
    private static final String DB_URL = "jdbc:derby:atomFeedChecksDB;create=true";

    @Test
    public void testCheck() throws IOException {
        AtomFeedChecker atomFeedChecker = new AtomFeedChecker(new German());
        CheckResult checkResult = atomFeedChecker.checkChanges(getStream());
        List<ChangeAnalysis> changeAnalysis = checkResult.getCheckResults();
        Assert.assertThat(changeAnalysis.size(), CoreMatchers.is(3));
        Assert.assertThat(changeAnalysis.get(0).getAddedMatches().size(), CoreMatchers.is(1));
        Assert.assertThat(changeAnalysis.get(0).getAddedMatches().get(0).getRule().getId(), CoreMatchers.is("DE_AGREEMENT"));
        Assert.assertTrue(changeAnalysis.get(0).getAddedMatches().get(0).getErrorContext().contains("Fehler: <err>der Haus</err>"));
        Assert.assertThat(changeAnalysis.get(0).getRemovedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(1).getAddedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(1).getRemovedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(2).getAddedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(2).getRemovedMatches().size(), CoreMatchers.is(0));
        CheckResult checkResult2 = atomFeedChecker.checkChanges(getStream());
        List<ChangeAnalysis> changeAnalysis2 = checkResult2.getCheckResults();
        Assert.assertThat(changeAnalysis2.size(), CoreMatchers.is(3));// not skipped because no database is used

    }

    @Test
    public void testCheckToDatabase() throws IOException, SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        initDatabase();
        DatabaseConfig databaseConfig = new DatabaseConfig(AtomFeedCheckerTest.DB_URL, "user", "pass");
        AtomFeedChecker atomFeedChecker1 = new AtomFeedChecker(new German(), databaseConfig);
        CheckResult checkResult = atomFeedChecker1.runCheck(getStream());
        List<ChangeAnalysis> changeAnalysis = checkResult.getCheckResults();
        Assert.assertThat(changeAnalysis.size(), CoreMatchers.is(3));
        Assert.assertThat(changeAnalysis.get(0).getAddedMatches().size(), CoreMatchers.is(1));
        Assert.assertThat(changeAnalysis.get(0).getAddedMatches().get(0).getRule().getId(), CoreMatchers.is("DE_AGREEMENT"));
        Assert.assertTrue(changeAnalysis.get(0).getAddedMatches().get(0).getErrorContext().contains("Fehler: <err>der Haus</err>"));
        Assert.assertThat(changeAnalysis.get(0).getRemovedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(1).getAddedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(1).getRemovedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(2).getAddedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(changeAnalysis.get(2).getRemovedMatches().size(), CoreMatchers.is(0));
        Date latestCheckDate1 = atomFeedChecker1.getDatabase().getCheckDates().get("de");
        Assert.assertThat(dateFormat.format(latestCheckDate1), CoreMatchers.is("2013-12-03 10:48"));
        AtomFeedChecker atomFeedChecker2 = new AtomFeedChecker(new German(), databaseConfig);
        CheckResult checkResult2 = atomFeedChecker2.runCheck(getStream());
        List<ChangeAnalysis> changeAnalysis2 = checkResult2.getCheckResults();
        // All articles could be skipped as they have been checked in the previous run:
        Assert.assertThat(changeAnalysis2.size(), CoreMatchers.is(0));
        Assert.assertThat(atomFeedChecker2.getDatabase().getCheckDates().size(), CoreMatchers.is(1));
        Date latestCheckDate2 = atomFeedChecker2.getDatabase().getCheckDates().get("de");
        Assert.assertThat(dateFormat.format(latestCheckDate2), CoreMatchers.is("2013-12-03 10:48"));
    }
}

