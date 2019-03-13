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
package org.languagetool.rules.en;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DateCheckFilterTest {
    @Test
    public void testGetDayOfWeek() throws Exception {
        DateCheckFilter filter = new DateCheckFilter();
        Assert.assertThat(filter.getDayOfWeek("Sun"), CoreMatchers.is(1));
        Assert.assertThat(filter.getDayOfWeek("Mon"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("mon"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("Mon."), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("Monday"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("monday"), CoreMatchers.is(2));
        Assert.assertThat(filter.getDayOfWeek("Tue"), CoreMatchers.is(3));
        Assert.assertThat(filter.getDayOfWeek("Fri"), CoreMatchers.is(6));
        Assert.assertThat(filter.getDayOfWeek("Fr"), CoreMatchers.is(6));
        Assert.assertThat(filter.getDayOfWeek("Saturday"), CoreMatchers.is(7));
    }

    @Test
    public void testMonth() throws Exception {
        DateCheckFilter filter = new DateCheckFilter();
        Assert.assertThat(filter.getMonth("jan"), CoreMatchers.is(1));
        Assert.assertThat(filter.getMonth("dec"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("december"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("December"), CoreMatchers.is(12));
        Assert.assertThat(filter.getMonth("DECEMBER"), CoreMatchers.is(12));
    }
}

