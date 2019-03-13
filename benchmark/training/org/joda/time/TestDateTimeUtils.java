/**
 * Copyright 2001-2013 Stephen Colebourne
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


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import junit.framework.TestCase;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.joda.time.base.AbstractInstant;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeZone.UTC;


/**
 * This class is a Junit unit test for DateTimeUtils.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeUtils extends TestCase {
    private static final GJChronology GJ = GJChronology.getInstance();

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

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // 2002-04-05
    private long TEST_TIME1 = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    // 2003-05-06
    private long TEST_TIME2 = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

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

    public TestDateTimeUtils(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testClass() {
        Class<?> cls = DateTimeUtils.class;
        TestCase.assertEquals(true, Modifier.isPublic(cls.getModifiers()));
        TestCase.assertEquals(false, Modifier.isFinal(cls.getModifiers()));
        TestCase.assertEquals(1, cls.getDeclaredConstructors().length);
        TestCase.assertEquals(true, Modifier.isProtected(cls.getDeclaredConstructors()[0].getModifiers()));
        new DateTimeUtils() {};
    }

    // -----------------------------------------------------------------------
    public void testSystemMillis() {
        long nowSystem = System.currentTimeMillis();
        long now = DateTimeUtils.currentTimeMillis();
        TestCase.assertTrue((now >= nowSystem));
        TestCase.assertTrue(((now - nowSystem) < 10000L));
    }

    // -----------------------------------------------------------------------
    public void testSystemMillisSecurity() {
        if (TestDateTimeUtils.OLD_JDK) {
            return;
        }
        try {
            try {
                Policy.setPolicy(TestDateTimeUtils.RESTRICT);
                System.setSecurityManager(new SecurityManager());
                DateTimeUtils.setCurrentMillisSystem();
                TestCase.fail();
            } catch (SecurityException ex) {
                // ok
            } finally {
                System.setSecurityManager(null);
                Policy.setPolicy(TestDateTimeUtils.ALLOW);
            }
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    // -----------------------------------------------------------------------
    public void testFixedMillis() {
        try {
            DateTimeUtils.setCurrentMillisFixed(0L);
            TestCase.assertEquals(0L, DateTimeUtils.currentTimeMillis());
            TestCase.assertEquals(0L, DateTimeUtils.currentTimeMillis());
            TestCase.assertEquals(0L, DateTimeUtils.currentTimeMillis());
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
        long nowSystem = System.currentTimeMillis();
        long now = DateTimeUtils.currentTimeMillis();
        TestCase.assertTrue((now >= nowSystem));
        TestCase.assertTrue(((now - nowSystem) < 10000L));
    }

    // -----------------------------------------------------------------------
    public void testFixedMillisSecurity() {
        if (TestDateTimeUtils.OLD_JDK) {
            return;
        }
        try {
            try {
                Policy.setPolicy(TestDateTimeUtils.RESTRICT);
                System.setSecurityManager(new SecurityManager());
                DateTimeUtils.setCurrentMillisFixed(0L);
                TestCase.fail();
            } catch (SecurityException ex) {
                // ok
            } finally {
                System.setSecurityManager(null);
                Policy.setPolicy(TestDateTimeUtils.ALLOW);
            }
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    // -----------------------------------------------------------------------
    public void testOffsetMillis() {
        try {
            // set time to one day ago
            DateTimeUtils.setCurrentMillisOffset(((((-24) * 60) * 60) * 1000));
            long nowSystem = System.currentTimeMillis();
            long now = DateTimeUtils.currentTimeMillis();
            long nowAdjustDay = now + (((24 * 60) * 60) * 1000);
            TestCase.assertTrue((now < nowSystem));
            TestCase.assertTrue((nowAdjustDay >= nowSystem));
            TestCase.assertTrue(((nowAdjustDay - nowSystem) < 10000L));
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
        long nowSystem = System.currentTimeMillis();
        long now = DateTimeUtils.currentTimeMillis();
        TestCase.assertTrue((now >= nowSystem));
        TestCase.assertTrue(((now - nowSystem) < 10000L));
    }

    // -----------------------------------------------------------------------
    public void testOffsetMillisToZero() {
        long now1 = 0L;
        try {
            // set time to one day ago
            DateTimeUtils.setCurrentMillisOffset(0);
            now1 = DateTimeUtils.currentTimeMillis();
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
        long now2 = DateTimeUtils.currentTimeMillis();
        TestCase.assertEquals(((Math.abs((now1 - now2))) < 100), true);
    }

    // -----------------------------------------------------------------------
    public void testOffsetMillisSecurity() {
        if (TestDateTimeUtils.OLD_JDK) {
            return;
        }
        try {
            try {
                Policy.setPolicy(TestDateTimeUtils.RESTRICT);
                System.setSecurityManager(new SecurityManager());
                DateTimeUtils.setCurrentMillisOffset(((((-24) * 60) * 60) * 1000));
                TestCase.fail();
            } catch (SecurityException ex) {
                // ok
            } finally {
                System.setSecurityManager(null);
                Policy.setPolicy(TestDateTimeUtils.ALLOW);
            }
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    // -----------------------------------------------------------------------
    public void testMillisProvider() {
        try {
            DateTimeUtils.setCurrentMillisProvider(new MillisProvider() {
                public long getMillis() {
                    return 1L;
                }
            });
            TestCase.assertEquals(1L, DateTimeUtils.currentTimeMillis());
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    public void testMillisProvider_null() {
        try {
            DateTimeUtils.setCurrentMillisProvider(null);
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testMillisProviderSecurity() {
        if (TestDateTimeUtils.OLD_JDK) {
            return;
        }
        try {
            try {
                Policy.setPolicy(TestDateTimeUtils.RESTRICT);
                System.setSecurityManager(new SecurityManager());
                DateTimeUtils.setCurrentMillisProvider(new MillisProvider() {
                    public long getMillis() {
                        return 0L;
                    }
                });
                TestCase.fail();
            } catch (SecurityException ex) {
                // ok
            } finally {
                System.setSecurityManager(null);
                Policy.setPolicy(TestDateTimeUtils.ALLOW);
            }
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_RI() {
        Instant i = new Instant(123L);
        TestCase.assertEquals(123L, DateTimeUtils.getInstantMillis(i));
        try {
            DateTimeUtils.setCurrentMillisFixed(TEST_TIME_NOW);
            TestCase.assertEquals(TEST_TIME_NOW, DateTimeUtils.getInstantMillis(null));
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    // -----------------------------------------------------------------------
    public void testGetInstantChronology_RI() {
        DateTime dt = new DateTime(123L, BuddhistChronology.getInstance());
        TestCase.assertEquals(BuddhistChronology.getInstance(), DateTimeUtils.getInstantChronology(dt));
        Instant i = new Instant(123L);
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), DateTimeUtils.getInstantChronology(i));
        AbstractInstant ai = new AbstractInstant() {
            public long getMillis() {
                return 0L;
            }

            public Chronology getChronology() {
                return null;// testing for this

            }
        };
        TestCase.assertEquals(ISOChronology.getInstance(), DateTimeUtils.getInstantChronology(ai));
        TestCase.assertEquals(ISOChronology.getInstance(), DateTimeUtils.getInstantChronology(null));
    }

    // -----------------------------------------------------------------------
    public void testGetIntervalChronology_RInterval() {
        Interval dt = new Interval(123L, 456L, BuddhistChronology.getInstance());
        TestCase.assertEquals(BuddhistChronology.getInstance(), DateTimeUtils.getIntervalChronology(dt));
        TestCase.assertEquals(ISOChronology.getInstance(), DateTimeUtils.getIntervalChronology(null));
        MutableInterval ai = new MutableInterval() {
            private static final long serialVersionUID = 1L;

            public Chronology getChronology() {
                return null;// testing for this

            }
        };
        TestCase.assertEquals(ISOChronology.getInstance(), DateTimeUtils.getIntervalChronology(ai));
    }

    // -----------------------------------------------------------------------
    public void testGetIntervalChronology_RI_RI() {
        DateTime dt1 = new DateTime(123L, BuddhistChronology.getInstance());
        DateTime dt2 = new DateTime(123L, CopticChronology.getInstance());
        TestCase.assertEquals(BuddhistChronology.getInstance(), DateTimeUtils.getIntervalChronology(dt1, dt2));
        TestCase.assertEquals(BuddhistChronology.getInstance(), DateTimeUtils.getIntervalChronology(dt1, null));
        TestCase.assertEquals(CopticChronology.getInstance(), DateTimeUtils.getIntervalChronology(null, dt2));
        TestCase.assertEquals(ISOChronology.getInstance(), DateTimeUtils.getIntervalChronology(null, null));
    }

    // -----------------------------------------------------------------------
    public void testGetReadableInterval_ReadableInterval() {
        ReadableInterval input = new Interval(0, 100L);
        TestCase.assertEquals(input, DateTimeUtils.getReadableInterval(input));
        try {
            DateTimeUtils.setCurrentMillisFixed(TEST_TIME_NOW);
            TestCase.assertEquals(new Interval(TEST_TIME_NOW, TEST_TIME_NOW), DateTimeUtils.getReadableInterval(null));
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Chronology() {
        TestCase.assertEquals(BuddhistChronology.getInstance(), DateTimeUtils.getChronology(BuddhistChronology.getInstance()));
        TestCase.assertEquals(ISOChronology.getInstance(), DateTimeUtils.getChronology(null));
    }

    // -----------------------------------------------------------------------
    public void testGetZone_Zone() {
        TestCase.assertEquals(TestDateTimeUtils.PARIS, DateTimeUtils.getZone(TestDateTimeUtils.PARIS));
        TestCase.assertEquals(DateTimeZone.getDefault(), DateTimeUtils.getZone(null));
    }

    // -----------------------------------------------------------------------
    public void testGetPeriodType_PeriodType() {
        TestCase.assertEquals(PeriodType.dayTime(), DateTimeUtils.getPeriodType(PeriodType.dayTime()));
        TestCase.assertEquals(PeriodType.standard(), DateTimeUtils.getPeriodType(null));
    }

    // -----------------------------------------------------------------------
    public void testGetDurationMillis_RI() {
        Duration dur = new Duration(123L);
        TestCase.assertEquals(123L, DateTimeUtils.getDurationMillis(dur));
        TestCase.assertEquals(0L, DateTimeUtils.getDurationMillis(null));
    }

    // -----------------------------------------------------------------------
    public void test_julianDay() {
        DateTime base = new DateTime(1970, 1, 1, 0, 0, UTC);
        TestCase.assertEquals(2440587.5, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(2440588, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay(2440587.5));
        base = base.plusHours(6);
        TestCase.assertEquals(2440587.75, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(2440588, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay(2440587.75));
        base = base.plusHours(6);
        TestCase.assertEquals(2440588.0, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(2440588, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay(2440588.0));
        base = base.plusHours(6);
        TestCase.assertEquals(2440588.25, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(2440588, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay(2440588.25));
        base = base.plusHours(6);
        TestCase.assertEquals(2440588.5, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(2440589, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay(2440588.5));
        base = new DateTime(2012, 8, 31, 23, 50, UTC);
        TestCase.assertEquals(2456171.4930555555, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(2456171, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        base = new DateTime((-4713), 1, 1, 12, 0, JulianChronology.getInstanceUTC());
        TestCase.assertEquals(0.0, DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(0, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay(0.0));
        base = new DateTime((-4713), 1, 1, 0, 0, JulianChronology.getInstanceUTC());
        TestCase.assertEquals((-0.5), DateTimeUtils.toJulianDay(base.getMillis()), 1.0E-4);
        TestCase.assertEquals(0, DateTimeUtils.toJulianDayNumber(base.getMillis()));
        TestCase.assertEquals(base.getMillis(), DateTimeUtils.fromJulianDay((-0.5)));
    }
}

