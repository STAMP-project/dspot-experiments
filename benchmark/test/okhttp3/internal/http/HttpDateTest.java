/**
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.internal.http;


import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class HttpDateTest {
    private TimeZone originalDefault;

    @Test
    public void parseStandardFormats() throws Exception {
        // RFC 822, updated by RFC 1123 with GMT.
        Assert.assertEquals(0L, HttpDate.parse("Thu, 01 Jan 1970 00:00:00 GMT").getTime());
        Assert.assertEquals(1402057830000L, HttpDate.parse("Fri, 06 Jun 2014 12:30:30 GMT").getTime());
        // RFC 850, obsoleted by RFC 1036 with GMT.
        Assert.assertEquals(0L, HttpDate.parse("Thursday, 01-Jan-70 00:00:00 GMT").getTime());
        Assert.assertEquals(1402057830000L, HttpDate.parse("Friday, 06-Jun-14 12:30:30 GMT").getTime());
        // ANSI C's asctime(): should use GMT, not platform default.
        Assert.assertEquals(0L, HttpDate.parse("Thu Jan 1 00:00:00 1970").getTime());
        Assert.assertEquals(1402057830000L, HttpDate.parse("Fri Jun 6 12:30:30 2014").getTime());
    }

    @Test
    public void format() throws Exception {
        Assert.assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", HttpDate.format(new Date(0)));
        Assert.assertEquals("Fri, 06 Jun 2014 12:30:30 GMT", HttpDate.format(new Date(1402057830000L)));
    }

    @Test
    public void parseNonStandardStrings() throws Exception {
        // RFC 822, updated by RFC 1123 with any TZ
        Assert.assertEquals(3600000L, HttpDate.parse("Thu, 01 Jan 1970 00:00:00 GMT-01:00").getTime());
        Assert.assertEquals(28800000L, HttpDate.parse("Thu, 01 Jan 1970 00:00:00 PST").getTime());
        // Ignore trailing junk
        Assert.assertEquals(0L, HttpDate.parse("Thu, 01 Jan 1970 00:00:00 GMT JUNK").getTime());
        // Missing timezones treated as bad.
        Assert.assertNull(HttpDate.parse("Thu, 01 Jan 1970 00:00:00"));
        // Missing seconds treated as bad.
        Assert.assertNull(HttpDate.parse("Thu, 01 Jan 1970 00:00 GMT"));
        // Extra spaces treated as bad.
        Assert.assertNull(HttpDate.parse("Thu,  01 Jan 1970 00:00 GMT"));
        // Missing leading zero treated as bad.
        Assert.assertNull(HttpDate.parse("Thu, 1 Jan 1970 00:00 GMT"));
        // RFC 850, obsoleted by RFC 1036 with any TZ.
        Assert.assertEquals(3600000L, HttpDate.parse("Thursday, 01-Jan-1970 00:00:00 GMT-01:00").getTime());
        Assert.assertEquals(28800000L, HttpDate.parse("Thursday, 01-Jan-1970 00:00:00 PST").getTime());
        // Ignore trailing junk
        Assert.assertEquals(28800000L, HttpDate.parse("Thursday, 01-Jan-1970 00:00:00 PST JUNK").getTime());
        // ANSI C's asctime() format
        // This format ignores the timezone entirely even if it is present and uses GMT.
        Assert.assertEquals(1402057830000L, HttpDate.parse("Fri Jun 6 12:30:30 2014 PST").getTime());
        // Ignore trailing junk.
        Assert.assertEquals(1402057830000L, HttpDate.parse("Fri Jun 6 12:30:30 2014 JUNK").getTime());
    }
}

