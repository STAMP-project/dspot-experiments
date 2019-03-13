/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.utilities.date;


import NaturalDateParser.DateNotParsableException;
import NaturalDateParser.Result;
import org.graylog2.plugin.utilities.date.NaturalDateParser;
import org.junit.Assert;
import org.junit.Test;


public class NaturalDateParserTest {
    private NaturalDateParser naturalDateParser;

    @Test
    public void testParse() throws Exception {
        NaturalDateParser.Result today = naturalDateParser.parse("today");
        Assert.assertNotNull(today.getFrom());
        Assert.assertNotNull(today.getTo());
        // It's enough if this does not throw exceptions because we are not testing the underlying library.
        naturalDateParser.parse("today");
        naturalDateParser.parse("last week to today");
    }

    @Test(expected = DateNotParsableException.class)
    public void testParseFailsOnUnparsableDate() throws Exception {
        naturalDateParser.parse("LOLWUT");
    }

    @Test(expected = DateNotParsableException.class)
    public void testParseFailsOnEmptyDate() throws Exception {
        naturalDateParser.parse("");
    }

    // https://github.com/Graylog2/graylog2-server/issues/1226
    @Test
    public void issue1226() throws Exception {
        NaturalDateParser.Result result99days = naturalDateParser.parse("last 99 days");
        assertThat(result99days.getFrom()).isEqualToIgnoringMillis(result99days.getTo().minusDays(99));
        NaturalDateParser.Result result100days = naturalDateParser.parse("last 100 days");
        assertThat(result100days.getFrom()).isEqualToIgnoringMillis(result100days.getTo().minusDays(100));
        NaturalDateParser.Result result101days = naturalDateParser.parse("last 101 days");
        assertThat(result101days.getFrom()).isEqualToIgnoringMillis(result101days.getTo().minusDays(101));
    }
}

