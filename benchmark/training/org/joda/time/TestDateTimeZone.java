/**
 * Copyright 2001-2014 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time;


import DateTimeZone.UTC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilePermission;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.tz.DefaultNameProvider;
import org.joda.time.tz.NameProvider;
import org.joda.time.tz.Provider;
import org.joda.time.tz.UTCProvider;
import org.joda.time.tz.ZoneInfoProvider;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;
import static DateTimeZone.UTC;


/**
 * This class is a JUnit test for DateTimeZone.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeZone extends TestCase {
    private static final boolean OLD_JDK;

    static {
        String str = System.getProperty("java.version");
        boolean old = true;
        if (((((str.length()) > 3) && ((str.charAt(0)) == '1')) && ((str.charAt(1)) == '.')) && ((((str.charAt(2)) == '4') || ((str.charAt(2)) == '5')) || ((str.charAt(2)) == '6'))) {
            old = false;
        }
        OLD_JDK = old;
    }

    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_SUMMER = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // 2002-01-09
    private long TEST_TIME_WINTER = (((y2002days) + 9L) - 1L) * (MILLIS_PER_DAY);

    // // 2002-04-05 Fri
    // private long TEST_TIME1 =
    // (y2002days + 31L + 28L + 31L + 5L -1L) * DateTimeConstants.MILLIS_PER_DAY
    // + 12L * DateTimeConstants.MILLIS_PER_HOUR
    // + 24L * DateTimeConstants.MILLIS_PER_MINUTE;
    // 
    // // 2003-05-06 Tue
    // private long TEST_TIME2 =
    // (y2003days + 31L + 28L + 31L + 30L + 6L -1L) * DateTimeConstants.MILLIS_PER_DAY
    // + 14L * DateTimeConstants.MILLIS_PER_HOUR
    // + 28L * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final Policy RESTRICT;

    private static final Policy ALLOW;

    static {
        // don't call Policy.getPolicy()
        RESTRICT = new Policy() {
            public PermissionCollection getPermissions(CodeSource codesource) {
                Permissions p = new Permissions();
                p.add(new AllPermission());// enable everything

                return p;
            }

            public void refresh() {
            }

            public boolean implies(ProtectionDomain domain, Permission permission) {
                if (permission instanceof JodaTimePermission) {
                    return false;
                }
                return true;
                // return super.implies(domain, permission);
            }
        };
        ALLOW = new Policy() {
            public PermissionCollection getPermissions(CodeSource codesource) {
                Permissions p = new Permissions();
                p.add(new AllPermission());// enable everything

                return p;
            }

            public void refresh() {
            }
        };
    }

    private DateTimeZone zone;

    private Locale locale;

    public TestDateTimeZone(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testDefault() {
        TestCase.assertNotNull(DateTimeZone.getDefault());
        DateTimeZone.setDefault(TestDateTimeZone.PARIS);
        TestCase.assertSame(TestDateTimeZone.PARIS, DateTimeZone.getDefault());
        try {
            DateTimeZone.setDefault(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testDefaultSecurity() {
        if (TestDateTimeZone.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestDateTimeZone.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            DateTimeZone.setDefault(TestDateTimeZone.PARIS);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestDateTimeZone.ALLOW);
        }
    }

    // -----------------------------------------------------------------------
    public void testForID_String() {
        TestCase.assertEquals(DateTimeZone.getDefault(), DateTimeZone.forID(((String) (null))));
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        TestCase.assertEquals("Europe/London", zone.getID());
        zone = DateTimeZone.forID("UTC");
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forID("+00:00");
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forID("+00");
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forID("+01:23");
        TestCase.assertEquals("+01:23", zone.getID());
        TestCase.assertEquals(((MILLIS_PER_HOUR) + (23L * (MILLIS_PER_MINUTE))), zone.getOffset(TEST_TIME_SUMMER));
        zone = DateTimeZone.forID("-02:00");
        TestCase.assertEquals("-02:00", zone.getID());
        TestCase.assertEquals(((-2L) * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_SUMMER));
        zone = DateTimeZone.forID("-07:05:34.0");
        TestCase.assertEquals("-07:05:34", zone.getID());
        TestCase.assertEquals(((((-7L) * (MILLIS_PER_HOUR)) + ((-5L) * (MILLIS_PER_MINUTE))) + ((-34L) * (MILLIS_PER_SECOND))), zone.getOffset(TEST_TIME_SUMMER));
        try {
            DateTimeZone.forID("SST");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forID("europe/london");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forID("Europe/UK");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forID("+");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forID("+0");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testForID_String_old() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("GMT", "UTC");
        map.put("WET", "WET");
        map.put("CET", "CET");
        map.put("MET", "CET");
        map.put("ECT", "CET");
        map.put("EET", "EET");
        map.put("MIT", "Pacific/Apia");
        map.put("HST", "Pacific/Honolulu");
        map.put("AST", "America/Anchorage");
        map.put("PST", "America/Los_Angeles");
        map.put("MST", "America/Denver");
        map.put("PNT", "America/Phoenix");
        map.put("CST", "America/Chicago");
        map.put("EST", "America/New_York");
        map.put("IET", "America/Indiana/Indianapolis");
        map.put("PRT", "America/Puerto_Rico");
        map.put("CNT", "America/St_Johns");
        map.put("AGT", "America/Argentina/Buenos_Aires");
        map.put("BET", "America/Sao_Paulo");
        map.put("ART", "Africa/Cairo");
        map.put("CAT", "Africa/Harare");
        map.put("EAT", "Africa/Addis_Ababa");
        map.put("NET", "Asia/Yerevan");
        map.put("PLT", "Asia/Karachi");
        map.put("IST", "Asia/Kolkata");
        map.put("BST", "Asia/Dhaka");
        map.put("VST", "Asia/Ho_Chi_Minh");
        map.put("CTT", "Asia/Shanghai");
        map.put("JST", "Asia/Tokyo");
        map.put("ACT", "Australia/Darwin");
        map.put("AET", "Australia/Sydney");
        map.put("SST", "Pacific/Guadalcanal");
        map.put("NST", "Pacific/Auckland");
        for (String key : map.keySet()) {
            String value = map.get(key);
            TimeZone juZone = TimeZone.getTimeZone(key);
            DateTimeZone zone = DateTimeZone.forTimeZone(juZone);
            TestCase.assertEquals(DateTimeZone.forID(value), zone);
            // System.out.println(juZone);
            // System.out.println(juZone.getDisplayName());
            // System.out.println(zone);
            // System.out.println("------");
        }
        // gee thanks time-zone db maintainer for damaging the database
        // and breaking the long-standing  association with CAT/EAT
    }

    // -----------------------------------------------------------------------
    public void testForOffsetHours_int() {
        TestCase.assertEquals(UTC, DateTimeZone.forOffsetHours(0));
        TestCase.assertEquals(DateTimeZone.forID("+03:00"), DateTimeZone.forOffsetHours(3));
        TestCase.assertEquals(DateTimeZone.forID("-02:00"), DateTimeZone.forOffsetHours((-2)));
        try {
            DateTimeZone.forOffsetHours(999999);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testForOffsetHoursMinutes_int_int() {
        TestCase.assertEquals(UTC, DateTimeZone.forOffsetHoursMinutes(0, 0));
        TestCase.assertEquals(DateTimeZone.forID("+23:59"), DateTimeZone.forOffsetHoursMinutes(23, 59));
        TestCase.assertEquals(DateTimeZone.forID("+02:15"), DateTimeZone.forOffsetHoursMinutes(2, 15));
        TestCase.assertEquals(DateTimeZone.forID("+02:00"), DateTimeZone.forOffsetHoursMinutes(2, 0));
        try {
            DateTimeZone.forOffsetHoursMinutes(2, (-15));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(DateTimeZone.forID("+00:15"), DateTimeZone.forOffsetHoursMinutes(0, 15));
        TestCase.assertEquals(DateTimeZone.forID("+00:00"), DateTimeZone.forOffsetHoursMinutes(0, 0));
        TestCase.assertEquals(DateTimeZone.forID("-00:15"), DateTimeZone.forOffsetHoursMinutes(0, (-15)));
        TestCase.assertEquals(DateTimeZone.forID("-02:00"), DateTimeZone.forOffsetHoursMinutes((-2), 0));
        TestCase.assertEquals(DateTimeZone.forID("-02:15"), DateTimeZone.forOffsetHoursMinutes((-2), (-15)));
        TestCase.assertEquals(DateTimeZone.forID("-02:15"), DateTimeZone.forOffsetHoursMinutes((-2), 15));
        TestCase.assertEquals(DateTimeZone.forID("-23:59"), DateTimeZone.forOffsetHoursMinutes((-23), 59));
        try {
            DateTimeZone.forOffsetHoursMinutes(2, 60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forOffsetHoursMinutes((-2), 60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forOffsetHoursMinutes(24, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeZone.forOffsetHoursMinutes((-24), 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testForOffsetMillis_int() {
        TestCase.assertSame(UTC, DateTimeZone.forOffsetMillis(0));
        TestCase.assertEquals(DateTimeZone.forID("+23:59:59.999"), DateTimeZone.forOffsetMillis(((((24 * 60) * 60) * 1000) - 1)));
        TestCase.assertEquals(DateTimeZone.forID("+03:00"), DateTimeZone.forOffsetMillis((((3 * 60) * 60) * 1000)));
        TestCase.assertEquals(DateTimeZone.forID("-02:00"), DateTimeZone.forOffsetMillis(((((-2) * 60) * 60) * 1000)));
        TestCase.assertEquals(DateTimeZone.forID("-23:59:59.999"), DateTimeZone.forOffsetMillis((((((-24) * 60) * 60) * 1000) + 1)));
        TestCase.assertEquals(DateTimeZone.forID("+04:45:17.045"), DateTimeZone.forOffsetMillis(((((((4 * 60) * 60) * 1000) + ((45 * 60) * 1000)) + (17 * 1000)) + 45)));
    }

    // -----------------------------------------------------------------------
    public void testForTimeZone_TimeZone() {
        TestCase.assertEquals(DateTimeZone.getDefault(), DateTimeZone.forTimeZone(((TimeZone) (null))));
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/London"));
        TestCase.assertEquals("Europe/London", zone.getID());
        TestCase.assertSame(UTC, DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("+00:00"));
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+00"));
        TestCase.assertSame(UTC, zone);
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+01:23"));
        TestCase.assertEquals("+01:23", zone.getID());
        TestCase.assertEquals(((MILLIS_PER_HOUR) + (23L * (MILLIS_PER_MINUTE))), zone.getOffset(TEST_TIME_SUMMER));
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+1:23"));
        TestCase.assertEquals("+01:23", zone.getID());
        TestCase.assertEquals(((MILLIS_PER_HOUR) + (23L * (MILLIS_PER_MINUTE))), zone.getOffset(TEST_TIME_SUMMER));
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT-02:00"));
        TestCase.assertEquals("-02:00", zone.getID());
        TestCase.assertEquals(((-2L) * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_SUMMER));
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+2"));
        TestCase.assertEquals("+02:00", zone.getID());
        TestCase.assertEquals((2L * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_SUMMER));
        zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST"));
        TestCase.assertEquals("America/New_York", zone.getID());
        TimeZone tz = TimeZone.getTimeZone("GMT-08:00");
        tz.setID("GMT-\u0660\u0668:\u0660\u0660");
        zone = DateTimeZone.forTimeZone(tz);
        TestCase.assertEquals("-08:00", zone.getID());
    }

    public void testFromTimeZoneInvalid() throws Exception {
        TimeZone jdkZone = new TimeZone() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getID() {
                return null;
            }

            @Override
            public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
                return 0;
            }

            @Override
            public void setRawOffset(int offsetMillis) {
            }

            @Override
            public int getRawOffset() {
                return 0;
            }

            @Override
            public boolean useDaylightTime() {
                return false;
            }

            @Override
            public boolean inDaylightTime(Date date) {
                return false;
            }
        };
        try {
            DateTimeZone.forTimeZone(jdkZone);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testTimeZoneConversion() {
        TimeZone jdkTimeZone = TimeZone.getTimeZone("GMT-10");
        TestCase.assertEquals("GMT-10:00", jdkTimeZone.getID());
        DateTimeZone jodaTimeZone = DateTimeZone.forTimeZone(jdkTimeZone);
        TestCase.assertEquals("-10:00", jodaTimeZone.getID());
        TestCase.assertEquals(jdkTimeZone.getRawOffset(), jodaTimeZone.getOffset(0L));
        TimeZone convertedTimeZone = jodaTimeZone.toTimeZone();
        TestCase.assertEquals("GMT-10:00", jdkTimeZone.getID());
        TestCase.assertEquals(jdkTimeZone.getID(), convertedTimeZone.getID());
        TestCase.assertEquals(jdkTimeZone.getRawOffset(), convertedTimeZone.getRawOffset());
    }

    // -----------------------------------------------------------------------
    public void testGetAvailableIDs() {
        TestCase.assertTrue(DateTimeZone.getAvailableIDs().contains("UTC"));
    }

    // -----------------------------------------------------------------------
    public void testProvider() {
        try {
            TestCase.assertNotNull(DateTimeZone.getProvider());
            Provider provider = DateTimeZone.getProvider();
            DateTimeZone.setProvider(null);
            TestCase.assertEquals(provider.getClass(), DateTimeZone.getProvider().getClass());
            try {
                DateTimeZone.setProvider(new TestDateTimeZone.MockNullIDSProvider());
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
            try {
                DateTimeZone.setProvider(new TestDateTimeZone.MockEmptyIDSProvider());
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
            try {
                DateTimeZone.setProvider(new TestDateTimeZone.MockNoUTCProvider());
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
            try {
                DateTimeZone.setProvider(new TestDateTimeZone.MockBadUTCProvider());
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
            Provider prov = new TestDateTimeZone.MockOKProvider();
            DateTimeZone.setProvider(prov);
            TestCase.assertSame(prov, DateTimeZone.getProvider());
            TestCase.assertEquals(2, DateTimeZone.getAvailableIDs().size());
            TestCase.assertTrue(DateTimeZone.getAvailableIDs().contains("UTC"));
            TestCase.assertTrue(DateTimeZone.getAvailableIDs().contains("Europe/London"));
        } finally {
            DateTimeZone.setProvider(null);
            TestCase.assertEquals(ZoneInfoProvider.class, DateTimeZone.getProvider().getClass());
        }
        try {
            System.setProperty("org.joda.time.DateTimeZone.Provider", "org.joda.time.tz.UTCProvider");
            DateTimeZone.setProvider(null);
            TestCase.assertEquals(UTCProvider.class, DateTimeZone.getProvider().getClass());
        } finally {
            System.getProperties().remove("org.joda.time.DateTimeZone.Provider");
            DateTimeZone.setProvider(null);
            TestCase.assertEquals(ZoneInfoProvider.class, DateTimeZone.getProvider().getClass());
        }
        try {
            System.setProperty("org.joda.time.DateTimeZone.Folder", "src/test/resources/tzdata");
            DateTimeZone.setProvider(null);
            TestCase.assertEquals(ZoneInfoProvider.class, DateTimeZone.getProvider().getClass());
            TestCase.assertEquals(2, DateTimeZone.getAvailableIDs().size());
            TestCase.assertEquals(true, DateTimeZone.getAvailableIDs().contains("UTC"));
            TestCase.assertEquals(true, DateTimeZone.getAvailableIDs().contains("CET"));
        } finally {
            System.getProperties().remove("org.joda.time.DateTimeZone.Folder");
            DateTimeZone.setProvider(null);
            TestCase.assertEquals(ZoneInfoProvider.class, DateTimeZone.getProvider().getClass());
            TestCase.assertEquals(true, ((DateTimeZone.getAvailableIDs().size()) > 2));
        }
    }

    public void testProvider_badClassName() {
        try {
            System.setProperty("org.joda.time.DateTimeZone.Provider", "xxx");
            DateTimeZone.setProvider(null);
        } catch (RuntimeException ex) {
            // expected
            TestCase.assertEquals(ZoneInfoProvider.class, DateTimeZone.getProvider().getClass());
        } finally {
            System.getProperties().remove("org.joda.time.DateTimeZone.Provider");
            DateTimeZone.setProvider(null);
        }
    }

    public void testProviderSecurity() {
        if (TestDateTimeZone.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestDateTimeZone.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            DateTimeZone.setProvider(new TestDateTimeZone.MockOKProvider());
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestDateTimeZone.ALLOW);
        }
    }

    public void testZoneInfoProviderResourceLoading() {
        final Set<String> ids = new HashSet<String>(DateTimeZone.getAvailableIDs());
        ids.remove(DateTimeZone.getDefault().getID());
        final String id = ids.toArray(new String[ids.size()])[new Random().nextInt(ids.size())];
        try {
            Policy.setPolicy(new Policy() {
                @Override
                public PermissionCollection getPermissions(CodeSource codesource) {
                    Permissions p = new Permissions();
                    p.add(new AllPermission());// enable everything

                    return p;
                }

                @Override
                public void refresh() {
                }

                @Override
                public boolean implies(ProtectionDomain domain, Permission permission) {
                    return (!(permission instanceof FilePermission)) && (!(permission.getName().contains(id)));
                }
            });
            System.setSecurityManager(new SecurityManager());
            // will throw IllegalArgumentException if the resource can
            // not be loaded
            final DateTimeZone zone = DateTimeZone.forID(id);
            TestCase.assertNotNull(zone);
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestDateTimeZone.ALLOW);
        }
    }

    static class MockNullIDSProvider implements Provider {
        public Set getAvailableIDs() {
            return null;
        }

        public DateTimeZone getZone(String id) {
            return null;
        }
    }

    static class MockEmptyIDSProvider implements Provider {
        public Set getAvailableIDs() {
            return new HashSet();
        }

        public DateTimeZone getZone(String id) {
            return null;
        }
    }

    static class MockNoUTCProvider implements Provider {
        public Set getAvailableIDs() {
            Set set = new HashSet();
            set.add("Europe/London");
            return set;
        }

        public DateTimeZone getZone(String id) {
            return null;
        }
    }

    static class MockBadUTCProvider implements Provider {
        public Set getAvailableIDs() {
            Set set = new HashSet();
            set.add("UTC");
            set.add("Europe/London");
            return set;
        }

        public DateTimeZone getZone(String id) {
            return null;
        }
    }

    static class MockOKProvider implements Provider {
        public Set getAvailableIDs() {
            Set set = new HashSet();
            set.add("UTC");
            set.add("Europe/London");
            return set;
        }

        public DateTimeZone getZone(String id) {
            return UTC;
        }
    }

    // -----------------------------------------------------------------------
    public void testNameProvider() {
        try {
            TestCase.assertNotNull(DateTimeZone.getNameProvider());
            NameProvider provider = DateTimeZone.getNameProvider();
            DateTimeZone.setNameProvider(null);
            TestCase.assertEquals(provider.getClass(), DateTimeZone.getNameProvider().getClass());
            provider = new TestDateTimeZone.MockOKButNullNameProvider();
            DateTimeZone.setNameProvider(provider);
            TestCase.assertSame(provider, DateTimeZone.getNameProvider());
            TestCase.assertEquals("+00:00", UTC.getShortName(TEST_TIME_SUMMER));
            TestCase.assertEquals("+00:00", UTC.getName(TEST_TIME_SUMMER));
        } finally {
            DateTimeZone.setNameProvider(null);
        }
        try {
            System.setProperty("org.joda.time.DateTimeZone.NameProvider", "org.joda.time.tz.DefaultNameProvider");
            DateTimeZone.setNameProvider(null);
            TestCase.assertEquals(DefaultNameProvider.class, DateTimeZone.getNameProvider().getClass());
        } finally {
            System.getProperties().remove("org.joda.time.DateTimeZone.NameProvider");
            DateTimeZone.setNameProvider(null);
            TestCase.assertEquals(DefaultNameProvider.class, DateTimeZone.getNameProvider().getClass());
        }
    }

    public void testNameProvider_badClassName() {
        try {
            System.setProperty("org.joda.time.DateTimeZone.NameProvider", "xxx");
            DateTimeZone.setProvider(null);
        } catch (RuntimeException ex) {
            // expected
            TestCase.assertEquals(DefaultNameProvider.class, DateTimeZone.getNameProvider().getClass());
        } finally {
            System.getProperties().remove("org.joda.time.DateTimeZone.NameProvider");
            DateTimeZone.setProvider(null);
        }
    }

    public void testNameProviderSecurity() {
        if (TestDateTimeZone.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestDateTimeZone.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            DateTimeZone.setNameProvider(new TestDateTimeZone.MockOKButNullNameProvider());
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestDateTimeZone.ALLOW);
        }
    }

    static class MockOKButNullNameProvider implements NameProvider {
        public String getShortName(Locale locale, String id, String nameKey) {
            return null;
        }

        public String getName(Locale locale, String id, String nameKey) {
            return null;
        }
    }

    // -----------------------------------------------------------------------
    public void testConstructor() {
        TestCase.assertEquals(1, DateTimeZone.class.getDeclaredConstructors().length);
        TestCase.assertTrue(Modifier.isProtected(DateTimeZone.class.getDeclaredConstructors()[0].getModifiers()));
        try {
            new DateTimeZone(null) {
                public String getNameKey(long instant) {
                    return null;
                }

                public int getOffset(long instant) {
                    return 0;
                }

                public int getStandardOffset(long instant) {
                    return 0;
                }

                public boolean isFixed() {
                    return false;
                }

                public long nextTransition(long instant) {
                    return 0;
                }

                public long previousTransition(long instant) {
                    return 0;
                }

                public boolean equals(Object object) {
                    return false;
                }
            };
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testGetID() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        TestCase.assertEquals("Europe/Paris", zone.getID());
    }

    public void testGetNameKey() {
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        TestCase.assertEquals("BST", zone.getNameKey(TEST_TIME_SUMMER));
        TestCase.assertEquals("GMT", zone.getNameKey(TEST_TIME_WINTER));
    }

    static final boolean JDK6PLUS;

    static {
        boolean jdk6 = true;
        try {
            DateFormatSymbols.class.getMethod("getInstance", new Class[]{ Locale.class });
        } catch (Exception ex) {
            jdk6 = false;
        }
        JDK6PLUS = jdk6;
    }

    static final boolean JDK9;

    static {
        boolean jdk9 = true;
        try {
            String str = System.getProperty("java.version");
            jdk9 = str.startsWith("9");
        } catch (Exception ex) {
            jdk9 = false;
        }
        JDK9 = jdk9;
    }

    public void testGetShortName() {
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        TestCase.assertEquals("BST", zone.getShortName(TEST_TIME_SUMMER));
        TestCase.assertEquals("GMT", zone.getShortName(TEST_TIME_WINTER));
        TestCase.assertEquals("BST", zone.getShortName(TEST_TIME_SUMMER, Locale.ENGLISH));
    }

    public void testGetShortName_berlin() {
        DateTimeZone berlin = DateTimeZone.forID("Europe/Berlin");
        TestCase.assertEquals("CET", berlin.getShortName(TEST_TIME_WINTER, Locale.ENGLISH));
        TestCase.assertEquals("CEST", berlin.getShortName(TEST_TIME_SUMMER, Locale.ENGLISH));
        if (TestDateTimeZone.JDK6PLUS) {
            TestCase.assertEquals("MEZ", berlin.getShortName(TEST_TIME_WINTER, Locale.GERMAN));
            TestCase.assertEquals("MESZ", berlin.getShortName(TEST_TIME_SUMMER, Locale.GERMAN));
        } else {
            TestCase.assertEquals("CET", berlin.getShortName(TEST_TIME_WINTER, Locale.GERMAN));
            TestCase.assertEquals("CEST", berlin.getShortName(TEST_TIME_SUMMER, Locale.GERMAN));
        }
    }

    public void testGetShortNameProviderName() {
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getShortName(null, "Europe/London", "BST"));
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getShortName(Locale.ENGLISH, null, "BST"));
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getShortName(Locale.ENGLISH, "Europe/London", null));
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getShortName(null, null, null));
    }

    public void testGetShortNameNullKey() {
        DateTimeZone zone = new TestDateTimeZone.MockDateTimeZone("Europe/London");
        TestCase.assertEquals("Europe/London", zone.getShortName(TEST_TIME_SUMMER, Locale.ENGLISH));
    }

    public void testGetName() {
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        TestCase.assertEquals("British Summer Time", zone.getName(TEST_TIME_SUMMER));
        TestCase.assertEquals("Greenwich Mean Time", zone.getName(TEST_TIME_WINTER));
        TestCase.assertEquals("British Summer Time", zone.getName(TEST_TIME_SUMMER, Locale.ENGLISH));
    }

    public void testGetName_berlin_english() {
        DateTimeZone berlin = DateTimeZone.forID("Europe/Berlin");
        if (TestDateTimeZone.JDK9) {
            TestCase.assertEquals("Central European Standard Time", berlin.getName(TEST_TIME_WINTER, Locale.ENGLISH));
        } else {
            TestCase.assertEquals("Central European Time", berlin.getName(TEST_TIME_WINTER, Locale.ENGLISH));
        }
        TestCase.assertEquals("Central European Summer Time", berlin.getName(TEST_TIME_SUMMER, Locale.ENGLISH));
    }

    public void testGetName_berlin_german() {
        DateTimeZone berlin = DateTimeZone.forID("Europe/Berlin");
        if (TestDateTimeZone.JDK9) {
            TestCase.assertEquals("Mitteleurop\u00e4ische Normalzeit", berlin.getName(TEST_TIME_WINTER, Locale.GERMAN));
            TestCase.assertEquals("Mitteleurop\u00e4ische Sommerzeit", berlin.getName(TEST_TIME_SUMMER, Locale.GERMAN));
        } else
            if (TestDateTimeZone.JDK6PLUS) {
                TestCase.assertEquals("Mitteleurop\u00e4ische Zeit", berlin.getName(TEST_TIME_WINTER, Locale.GERMAN));
                TestCase.assertEquals("Mitteleurop\u00e4ische Sommerzeit", berlin.getName(TEST_TIME_SUMMER, Locale.GERMAN));
            } else {
                TestCase.assertEquals("Zentraleurop\u00e4ische Zeit", berlin.getName(TEST_TIME_WINTER, Locale.GERMAN));
                TestCase.assertEquals("Zentraleurop\u00e4ische Sommerzeit", berlin.getName(TEST_TIME_SUMMER, Locale.GERMAN));
            }

    }

    public void testGetNameProviderName() {
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getName(null, "Europe/London", "BST"));
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getName(Locale.ENGLISH, null, "BST"));
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getName(Locale.ENGLISH, "Europe/London", null));
        TestCase.assertEquals(null, DateTimeZone.getNameProvider().getName(null, null, null));
    }

    public void testGetNameNullKey() {
        DateTimeZone zone = new TestDateTimeZone.MockDateTimeZone("Europe/London");
        TestCase.assertEquals("Europe/London", zone.getName(TEST_TIME_SUMMER, Locale.ENGLISH));
    }

    static class MockDateTimeZone extends DateTimeZone {
        public MockDateTimeZone(String id) {
            super(id);
        }

        public String getNameKey(long instant) {
            return null;// null

        }

        public int getOffset(long instant) {
            return 0;
        }

        public int getStandardOffset(long instant) {
            return 0;
        }

        public boolean isFixed() {
            return false;
        }

        public long nextTransition(long instant) {
            return 0;
        }

        public long previousTransition(long instant) {
            return 0;
        }

        public boolean equals(Object object) {
            return false;
        }
    }

    // -----------------------------------------------------------------------
    public void testGetOffset_long() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        TestCase.assertEquals((2L * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_SUMMER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_WINTER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getStandardOffset(TEST_TIME_SUMMER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getStandardOffset(TEST_TIME_WINTER));
        TestCase.assertEquals((2L * (MILLIS_PER_HOUR)), zone.getOffsetFromLocal(TEST_TIME_SUMMER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffsetFromLocal(TEST_TIME_WINTER));
        TestCase.assertEquals(false, zone.isStandardOffset(TEST_TIME_SUMMER));
        TestCase.assertEquals(true, zone.isStandardOffset(TEST_TIME_WINTER));
    }

    public void testGetOffset_RI() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        TestCase.assertEquals((2L * (MILLIS_PER_HOUR)), zone.getOffset(new Instant(TEST_TIME_SUMMER)));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffset(new Instant(TEST_TIME_WINTER)));
        TestCase.assertEquals(zone.getOffset(DateTimeUtils.currentTimeMillis()), zone.getOffset(null));
    }

    public void testGetOffsetFixed() {
        DateTimeZone zone = DateTimeZone.forID("+01:00");
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_SUMMER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffset(TEST_TIME_WINTER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getStandardOffset(TEST_TIME_SUMMER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getStandardOffset(TEST_TIME_WINTER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffsetFromLocal(TEST_TIME_SUMMER));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffsetFromLocal(TEST_TIME_WINTER));
        TestCase.assertEquals(true, zone.isStandardOffset(TEST_TIME_SUMMER));
        TestCase.assertEquals(true, zone.isStandardOffset(TEST_TIME_WINTER));
    }

    public void testGetOffsetFixed_RI() {
        DateTimeZone zone = DateTimeZone.forID("+01:00");
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffset(new Instant(TEST_TIME_SUMMER)));
        TestCase.assertEquals((1L * (MILLIS_PER_HOUR)), zone.getOffset(new Instant(TEST_TIME_WINTER)));
        TestCase.assertEquals(zone.getOffset(DateTimeUtils.currentTimeMillis()), zone.getOffset(null));
    }

    // -----------------------------------------------------------------------
    public void testGetMillisKeepLocal() {
        long millisLondon = TEST_TIME_SUMMER;
        long millisParis = (TEST_TIME_SUMMER) - (1L * (MILLIS_PER_HOUR));
        TestCase.assertEquals(millisLondon, TestDateTimeZone.LONDON.getMillisKeepLocal(TestDateTimeZone.LONDON, millisLondon));
        TestCase.assertEquals(millisParis, TestDateTimeZone.LONDON.getMillisKeepLocal(TestDateTimeZone.LONDON, millisParis));
        TestCase.assertEquals(millisLondon, TestDateTimeZone.PARIS.getMillisKeepLocal(TestDateTimeZone.PARIS, millisLondon));
        TestCase.assertEquals(millisParis, TestDateTimeZone.PARIS.getMillisKeepLocal(TestDateTimeZone.PARIS, millisParis));
        TestCase.assertEquals(millisParis, TestDateTimeZone.LONDON.getMillisKeepLocal(TestDateTimeZone.PARIS, millisLondon));
        TestCase.assertEquals(millisLondon, TestDateTimeZone.PARIS.getMillisKeepLocal(TestDateTimeZone.LONDON, millisParis));
        DateTimeZone zone = DateTimeZone.getDefault();
        try {
            DateTimeZone.setDefault(TestDateTimeZone.LONDON);
            TestCase.assertEquals(millisLondon, TestDateTimeZone.PARIS.getMillisKeepLocal(null, millisParis));
        } finally {
            DateTimeZone.setDefault(zone);
        }
    }

    // -----------------------------------------------------------------------
    public void testIsFixed() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        TestCase.assertEquals(false, zone.isFixed());
        TestCase.assertEquals(true, UTC.isFixed());
    }

    // -----------------------------------------------------------------------
    public void testTransitionFixed() {
        DateTimeZone zone = DateTimeZone.forID("+01:00");
        TestCase.assertEquals(TEST_TIME_SUMMER, zone.nextTransition(TEST_TIME_SUMMER));
        TestCase.assertEquals(TEST_TIME_WINTER, zone.nextTransition(TEST_TIME_WINTER));
        TestCase.assertEquals(TEST_TIME_SUMMER, zone.previousTransition(TEST_TIME_SUMMER));
        TestCase.assertEquals(TEST_TIME_WINTER, zone.previousTransition(TEST_TIME_WINTER));
    }

    // //-----------------------------------------------------------------------
    // public void testIsLocalDateTimeOverlap_Berlin() {
    // DateTimeZone zone = DateTimeZone.forID("Europe/Berlin");
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 1, 0)));
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 1, 59, 59, 99)));
    // assertEquals(true, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 2, 0)));
    // assertEquals(true, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 2, 30)));
    // assertEquals(true, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 2, 59, 59, 99)));
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 3, 0)));
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 10, 28, 4, 0)));
    // 
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 3, 25, 1, 30)));  // before gap
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 3, 25, 2, 30)));  // gap
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 3, 25, 3, 30)));  // after gap
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 12, 24, 12, 34)));
    // }
    // 
    // //-----------------------------------------------------------------------
    // public void testIsLocalDateTimeOverlap_NewYork() {
    // DateTimeZone zone = DateTimeZone.forID("America/New_York");
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 0, 0)));
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 0, 59, 59, 99)));
    // assertEquals(true, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 1, 0)));
    // assertEquals(true, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 1, 30)));
    // assertEquals(true, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 1, 59, 59, 99)));
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 2, 0)));
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 11, 4, 3, 0)));
    // 
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 3, 11, 1, 30)));  // before gap
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 3, 11, 2, 30)));  // gap
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 3, 11, 3, 30)));  // after gap
    // assertEquals(false, zone.isLocalDateTimeOverlap(new LocalDateTime(2007, 12, 24, 12, 34)));
    // }
    // -----------------------------------------------------------------------
    public void testIsLocalDateTimeGap_Berlin() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Berlin");
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 1, 0)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 1, 59, 59, 99)));
        TestCase.assertEquals(true, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 2, 0)));
        TestCase.assertEquals(true, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 2, 30)));
        TestCase.assertEquals(true, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 2, 59, 59, 99)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 3, 0)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 25, 4, 0)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 10, 28, 1, 30)));// before overlap

        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 10, 28, 2, 30)));// overlap

        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 10, 28, 3, 30)));// after overlap

        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 12, 24, 12, 34)));
    }

    // -----------------------------------------------------------------------
    public void testIsLocalDateTimeGap_NewYork() {
        DateTimeZone zone = DateTimeZone.forID("America/New_York");
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 1, 0)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 1, 59, 59, 99)));
        TestCase.assertEquals(true, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 2, 0)));
        TestCase.assertEquals(true, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 2, 30)));
        TestCase.assertEquals(true, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 2, 59, 59, 99)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 3, 0)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 3, 11, 4, 0)));
        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 11, 4, 0, 30)));// before overlap

        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 11, 4, 1, 30)));// overlap

        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 11, 4, 2, 30)));// after overlap

        TestCase.assertEquals(false, zone.isLocalDateTimeGap(new LocalDateTime(2007, 12, 24, 12, 34)));
    }

    // -----------------------------------------------------------------------
    public void testToTimeZone() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        TimeZone tz = zone.toTimeZone();
        TestCase.assertEquals("Europe/Paris", tz.getID());
    }

    // -----------------------------------------------------------------------
    public void testEqualsHashCode() {
        DateTimeZone zone1 = DateTimeZone.forID("Europe/Paris");
        DateTimeZone zone2 = DateTimeZone.forID("Europe/Paris");
        TestCase.assertEquals(true, zone1.equals(zone1));
        TestCase.assertEquals(true, zone1.equals(zone2));
        TestCase.assertEquals(true, zone2.equals(zone1));
        TestCase.assertEquals(true, zone2.equals(zone2));
        TestCase.assertEquals(true, ((zone1.hashCode()) == (zone2.hashCode())));
        DateTimeZone zone3 = DateTimeZone.forID("Europe/London");
        TestCase.assertEquals(true, zone3.equals(zone3));
        TestCase.assertEquals(false, zone1.equals(zone3));
        TestCase.assertEquals(false, zone2.equals(zone3));
        TestCase.assertEquals(false, zone3.equals(zone1));
        TestCase.assertEquals(false, zone3.equals(zone2));
        TestCase.assertEquals(false, ((zone1.hashCode()) == (zone3.hashCode())));
        TestCase.assertEquals(true, ((zone3.hashCode()) == (zone3.hashCode())));
        DateTimeZone zone4 = DateTimeZone.forID("+01:00");
        TestCase.assertEquals(true, zone4.equals(zone4));
        TestCase.assertEquals(false, zone1.equals(zone4));
        TestCase.assertEquals(false, zone2.equals(zone4));
        TestCase.assertEquals(false, zone3.equals(zone4));
        TestCase.assertEquals(false, zone4.equals(zone1));
        TestCase.assertEquals(false, zone4.equals(zone2));
        TestCase.assertEquals(false, zone4.equals(zone3));
        TestCase.assertEquals(false, ((zone1.hashCode()) == (zone4.hashCode())));
        TestCase.assertEquals(true, ((zone4.hashCode()) == (zone4.hashCode())));
        DateTimeZone zone5 = DateTimeZone.forID("+02:00");
        TestCase.assertEquals(true, zone5.equals(zone5));
        TestCase.assertEquals(false, zone1.equals(zone5));
        TestCase.assertEquals(false, zone2.equals(zone5));
        TestCase.assertEquals(false, zone3.equals(zone5));
        TestCase.assertEquals(false, zone4.equals(zone5));
        TestCase.assertEquals(false, zone5.equals(zone1));
        TestCase.assertEquals(false, zone5.equals(zone2));
        TestCase.assertEquals(false, zone5.equals(zone3));
        TestCase.assertEquals(false, zone5.equals(zone4));
        TestCase.assertEquals(false, ((zone1.hashCode()) == (zone5.hashCode())));
        TestCase.assertEquals(true, ((zone5.hashCode()) == (zone5.hashCode())));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        TestCase.assertEquals("Europe/Paris", zone.toString());
        TestCase.assertEquals("UTC", UTC.toString());
    }

    // -----------------------------------------------------------------------
    public void testDublin() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Dublin");
        DateTime winter = new DateTime(2018, 1, 1, 0, 0, 0, 0, zone);
        TestCase.assertEquals(0, zone.getStandardOffset(winter.getMillis()));
        TestCase.assertEquals(0, zone.getOffset(winter.getMillis()));
        TestCase.assertEquals(true, zone.isStandardOffset(winter.getMillis()));
        TestCase.assertEquals("Greenwich Mean Time", zone.getName(winter.getMillis()));
        TestCase.assertEquals("GMT", zone.getNameKey(winter.getMillis()));
        DateTime summer = winter.plusMonths(6);
        TestCase.assertEquals(0, zone.getStandardOffset(summer.getMillis()));
        TestCase.assertEquals(3600000, zone.getOffset(summer.getMillis()));
        TestCase.assertEquals(false, zone.isStandardOffset(summer.getMillis()));
        TestCase.assertEquals(true, zone.getName(summer.getMillis()).startsWith("Irish "));
        TestCase.assertEquals("IST", zone.getNameKey(summer.getMillis()));
    }

    // -----------------------------------------------------------------------
    public void testWindhoek() {
        DateTimeZone zone = DateTimeZone.forID("Africa/Windhoek");
        DateTime dtDec1990 = new DateTime(1990, 12, 1, 0, 0, 0, 0, zone);
        TestCase.assertEquals(3600000, zone.getStandardOffset(dtDec1990.getMillis()));
        TestCase.assertEquals(7200000, zone.getOffset(dtDec1990.getMillis()));
        TestCase.assertEquals(false, zone.isStandardOffset(dtDec1990.getMillis()));
        DateTime dtDec1994 = new DateTime(1994, 12, 1, 0, 0, 0, 0, zone);
        TestCase.assertEquals(3600000, zone.getStandardOffset(dtDec1994.getMillis()));
        TestCase.assertEquals(7200000, zone.getOffset(dtDec1994.getMillis()));
        TestCase.assertEquals(false, zone.isStandardOffset(dtDec1994.getMillis()));
        DateTime dtJun1995 = new DateTime(1995, 6, 1, 0, 0, 0, 0, zone);
        TestCase.assertEquals(3600000, zone.getStandardOffset(dtJun1995.getMillis()));
        TestCase.assertEquals(3600000, zone.getOffset(dtJun1995.getMillis()));
        TestCase.assertEquals(true, zone.isStandardOffset(dtJun1995.getMillis()));
    }

    // -----------------------------------------------------------------------
    public void testSerialization1() throws Exception {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(zone);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        DateTimeZone result = ((DateTimeZone) (ois.readObject()));
        ois.close();
        TestCase.assertSame(zone, result);
    }

    // -----------------------------------------------------------------------
    public void testSerialization2() throws Exception {
        DateTimeZone zone = DateTimeZone.forID("+01:00");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(zone);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        DateTimeZone result = ((DateTimeZone) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(zone, result);
    }

    public void testCommentParse() throws Exception {
        // A bug in ZoneInfoCompiler's handling of comments broke Europe/Athens
        // after 1980. This test is included to make sure it doesn't break again.
        DateTimeZone zone = DateTimeZone.forID("Europe/Athens");
        DateTime dt = new DateTime(2005, 5, 5, 20, 10, 15, 0, zone);
        TestCase.assertEquals(1115313015000L, dt.getMillis());
    }

    public void testPatchedNameKeysLondon() throws Exception {
        // the tz database does not have unique name keys [1716305]
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        DateTime now = new DateTime(2007, 1, 1, 0, 0, 0, 0);
        String str1 = zone.getName(now.getMillis());
        String str2 = zone.getName(now.plusMonths(6).getMillis());
        TestCase.assertEquals(false, str1.equals(str2));
    }

    public void testPatchedNameKeysSydney() throws Exception {
        // the tz database does not have unique name keys [1716305]
        DateTimeZone zone = DateTimeZone.forID("Australia/Sydney");
        DateTime now = new DateTime(2007, 1, 1, 0, 0, 0, 0);
        String str1 = zone.getName(now.getMillis());
        String str2 = zone.getName(now.plusMonths(6).getMillis());
        TestCase.assertEquals(false, str1.equals(str2));
    }

    public void testPatchedNameKeysSydneyHistoric() throws Exception {
        // the tz database does not have unique name keys [1716305]
        DateTimeZone zone = DateTimeZone.forID("Australia/Sydney");
        DateTime now = new DateTime(1996, 1, 1, 0, 0, 0, 0);
        String str1 = zone.getName(now.getMillis());
        String str2 = zone.getName(now.plusMonths(6).getMillis());
        TestCase.assertEquals(false, str1.equals(str2));
    }

    public void testPatchedNameKeysGazaHistoric() throws Exception {
        // the tz database does not have unique name keys [1716305]
        DateTimeZone zone = DateTimeZone.forID("Africa/Johannesburg");
        DateTime now = new DateTime(1943, 1, 1, 0, 0, 0, 0);
        String str1 = zone.getName(now.getMillis());
        String str2 = zone.getName(now.plusMonths(6).getMillis());
        TestCase.assertEquals(false, str1.equals(str2));
    }
}

