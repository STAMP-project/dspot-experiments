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
package org.graylog.plugins.cef.parser;


import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CEFTimestampParserTest {
    private final String testString;

    private final DateTime expectedDateTime;

    private final DateTimeZone timeZone;

    private final Locale locale;

    public CEFTimestampParserTest(String testString, DateTime expectedDateTime, DateTimeZone timeZone, Locale locale) {
        this.testString = testString;
        this.expectedDateTime = expectedDateTime;
        this.timeZone = timeZone;
        this.locale = locale;
    }

    @Test
    public void parseWithTimeZoneAndLocale() throws Exception {
        Assert.assertEquals(testString, expectedDateTime, CEFTimestampParser.parse(testString, timeZone, locale));
    }
}

