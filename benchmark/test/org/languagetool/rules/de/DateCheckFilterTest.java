/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
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


import java.util.Calendar;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.rules.FakeRule;
import org.languagetool.rules.RuleMatch;


public class DateCheckFilterTest {
    private final RuleMatch match = new RuleMatch(new FakeRule(), null, 0, 10, "message");

    private final DateCheckFilter filter = new DateCheckFilter();

    @Test
    public void testAccept() throws Exception {
        Assert.assertNull(filter.acceptRuleMatch(match, makeMap("2014", "8", "23", "Samstag"), null));// correct date

        Assert.assertNotNull(filter.acceptRuleMatch(match, makeMap("2014", "8", "23", "Sonntag"), null));// incorrect date

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptIncompleteArgs() throws Exception {
        Map<String, String> map = makeMap("2014", "8", "23", "Samstag");
        map.remove("weekDay");
        filter.acceptRuleMatch(match, map, null);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidDay() throws Exception {
        filter.acceptRuleMatch(match, makeMap("2014", "8", "23", "invalid"), null);
    }

    @Test
    public void testGetDayOfWeek1() throws Exception {
        Assert.assertThat(filter.getDayOfWeek("So"), CoreMatchers.is(1));
        Assert.assertThat(filter.getDayOfWeek("Mo"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("mo"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("Mon."), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("Montag"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("montag"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("Di"), CoreMatchers.is(3));
        Assert.assertThat(filter.getDayOfWeek("Fr"), CoreMatchers.is(6));
        Assert.assertThat(filter.getDayOfWeek("Samstag"), CoreMatchers.is(7));
        Assert.assertThat(filter.getDayOfWeek("Sonnabend"), CoreMatchers.is(7));
    }

    @Test
    public void testGetDayOfWeek2() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, (8 - 1), 29);
        Assert.assertThat(filter.getDayOfWeek(calendar), CoreMatchers.is("Freitag"));
        calendar.set(2014, (8 - 1), 30);
        Assert.assertThat(filter.getDayOfWeek(calendar), CoreMatchers.is("Samstag"));
    }

    @Test
    public void testGetMonth() throws Exception {
        Assert.assertThat(filter.getMonth("Januar"), CoreMatchers.is(1));
        Assert.assertThat(filter.getMonth("Jan"), CoreMatchers.is(1));
        Assert.assertThat(filter.getMonth("Jan."), CoreMatchers.is(1));
        Assert.assertThat(filter.getMonth("Dezember"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("Dez"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("dez"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("DEZEMBER"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("dezember"), CoreMatchers.is(12));
    }
}

