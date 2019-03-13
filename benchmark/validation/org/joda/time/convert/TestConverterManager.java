/**
 * Copyright 2001-2006 Stephen Colebourne
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
package org.joda.time.convert;


import NullConverter.INSTANCE;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.JodaTimePermission;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.TimeOfDay;
import org.joda.time.format.DateTimeFormatter;


/**
 * This class is a JUnit test for ConverterManager.
 *
 * @author Stephen Colebourne
 */
public class TestConverterManager extends TestCase {
    private static final boolean OLD_JDK;

    static {
        String str = System.getProperty("java.version");
        boolean old = true;
        if (((((str.length()) > 3) && ((str.charAt(0)) == '1')) && ((str.charAt(1)) == '.')) && ((((str.charAt(2)) == '4') || ((str.charAt(2)) == '5')) || ((str.charAt(2)) == '6'))) {
            old = false;
        }
        OLD_JDK = old;
    }

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

    public TestConverterManager(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = ConverterManager.class;
        TestCase.assertEquals(true, Modifier.isPublic(cls.getModifiers()));
        Constructor con = cls.getDeclaredConstructor(((Class[]) (null)));
        TestCase.assertEquals(1, cls.getDeclaredConstructors().length);
        TestCase.assertEquals(true, Modifier.isProtected(con.getModifiers()));
        Field fld = cls.getDeclaredField("INSTANCE");
        TestCase.assertEquals(true, Modifier.isPrivate(fld.getModifiers()));
    }

    // -----------------------------------------------------------------------
    public void testGetInstantConverter() {
        InstantConverter c = ConverterManager.getInstance().getInstantConverter(new Long(0L));
        TestCase.assertEquals(Long.class, c.getSupportedType());
        c = ConverterManager.getInstance().getInstantConverter(new DateTime());
        TestCase.assertEquals(ReadableInstant.class, c.getSupportedType());
        c = ConverterManager.getInstance().getInstantConverter("");
        TestCase.assertEquals(String.class, c.getSupportedType());
        c = ConverterManager.getInstance().getInstantConverter(new Date());
        TestCase.assertEquals(Date.class, c.getSupportedType());
        c = ConverterManager.getInstance().getInstantConverter(new GregorianCalendar());
        TestCase.assertEquals(Calendar.class, c.getSupportedType());
        c = ConverterManager.getInstance().getInstantConverter(null);
        TestCase.assertEquals(null, c.getSupportedType());
        try {
            ConverterManager.getInstance().getInstantConverter(Boolean.TRUE);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetInstantConverterRemovedNull() {
        try {
            ConverterManager.getInstance().removeInstantConverter(INSTANCE);
            try {
                ConverterManager.getInstance().getInstantConverter(null);
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
        } finally {
            ConverterManager.getInstance().addInstantConverter(INSTANCE);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testGetInstantConverterOKMultipleMatches() {
        InstantConverter c = new InstantConverter() {
            public long getInstantMillis(Object object, Chronology chrono) {
                return 0;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return ReadableDateTime.class;
            }
        };
        try {
            ConverterManager.getInstance().addInstantConverter(c);
            InstantConverter ok = ConverterManager.getInstance().getInstantConverter(new DateTime());
            // ReadableDateTime and ReadableInstant both match, but RI discarded as less specific
            TestCase.assertEquals(ReadableDateTime.class, ok.getSupportedType());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(c);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testGetInstantConverterBadMultipleMatches() {
        InstantConverter c = new InstantConverter() {
            public long getInstantMillis(Object object, Chronology chrono) {
                return 0;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return Serializable.class;
            }
        };
        try {
            ConverterManager.getInstance().addInstantConverter(c);
            try {
                ConverterManager.getInstance().getInstantConverter(new DateTime());
                TestCase.fail();
            } catch (IllegalStateException ex) {
                // Serializable and ReadableInstant both match, so cannot pick
            }
        } finally {
            ConverterManager.getInstance().removeInstantConverter(c);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testGetInstantConverters() {
        InstantConverter[] array = ConverterManager.getInstance().getInstantConverters();
        TestCase.assertEquals(6, array.length);
    }

    // -----------------------------------------------------------------------
    public void testAddInstantConverter1() {
        InstantConverter c = new InstantConverter() {
            public long getInstantMillis(Object object, Chronology chrono) {
                return 0;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        try {
            InstantConverter removed = ConverterManager.getInstance().addInstantConverter(c);
            TestCase.assertEquals(null, removed);
            TestCase.assertEquals(Boolean.class, ConverterManager.getInstance().getInstantConverter(Boolean.TRUE).getSupportedType());
            TestCase.assertEquals(7, ConverterManager.getInstance().getInstantConverters().length);
        } finally {
            ConverterManager.getInstance().removeInstantConverter(c);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testAddInstantConverter2() {
        InstantConverter c = new InstantConverter() {
            public long getInstantMillis(Object object, Chronology chrono) {
                return 0;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return String.class;
            }
        };
        try {
            InstantConverter removed = ConverterManager.getInstance().addInstantConverter(c);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(String.class, ConverterManager.getInstance().getInstantConverter("").getSupportedType());
            TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
        } finally {
            ConverterManager.getInstance().addInstantConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testAddInstantConverter3() {
        InstantConverter removed = ConverterManager.getInstance().addInstantConverter(StringConverter.INSTANCE);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testAddInstantConverter4() {
        InstantConverter removed = ConverterManager.getInstance().addInstantConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testAddInstantConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().addInstantConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testRemoveInstantConverter1() {
        try {
            InstantConverter removed = ConverterManager.getInstance().removeInstantConverter(StringConverter.INSTANCE);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(5, ConverterManager.getInstance().getInstantConverters().length);
        } finally {
            ConverterManager.getInstance().addInstantConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testRemoveInstantConverter2() {
        InstantConverter c = new InstantConverter() {
            public long getInstantMillis(Object object, Chronology chrono) {
                return 0;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        InstantConverter removed = ConverterManager.getInstance().removeInstantConverter(c);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testRemoveInstantConverter3() {
        InstantConverter removed = ConverterManager.getInstance().removeInstantConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    public void testRemoveInstantConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().removeInstantConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(6, ConverterManager.getInstance().getInstantConverters().length);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    private static final int PARTIAL_SIZE = 7;

    public void testGetPartialConverter() {
        PartialConverter c = ConverterManager.getInstance().getPartialConverter(new Long(0L));
        TestCase.assertEquals(Long.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPartialConverter(new TimeOfDay());
        TestCase.assertEquals(ReadablePartial.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPartialConverter(new DateTime());
        TestCase.assertEquals(ReadableInstant.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPartialConverter("");
        TestCase.assertEquals(String.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPartialConverter(new Date());
        TestCase.assertEquals(Date.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPartialConverter(new GregorianCalendar());
        TestCase.assertEquals(Calendar.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPartialConverter(null);
        TestCase.assertEquals(null, c.getSupportedType());
        try {
            ConverterManager.getInstance().getPartialConverter(Boolean.TRUE);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetPartialConverterRemovedNull() {
        try {
            ConverterManager.getInstance().removePartialConverter(INSTANCE);
            try {
                ConverterManager.getInstance().getPartialConverter(null);
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
        } finally {
            ConverterManager.getInstance().addPartialConverter(INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testGetPartialConverterOKMultipleMatches() {
        PartialConverter c = new PartialConverter() {
            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono) {
                return null;
            }

            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono, DateTimeFormatter parser) {
                return null;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return ReadableDateTime.class;
            }
        };
        try {
            ConverterManager.getInstance().addPartialConverter(c);
            PartialConverter ok = ConverterManager.getInstance().getPartialConverter(new DateTime());
            // ReadableDateTime and ReadablePartial both match, but RI discarded as less specific
            TestCase.assertEquals(ReadableDateTime.class, ok.getSupportedType());
        } finally {
            ConverterManager.getInstance().removePartialConverter(c);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testGetPartialConverterBadMultipleMatches() {
        PartialConverter c = new PartialConverter() {
            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono) {
                return null;
            }

            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono, DateTimeFormatter parser) {
                return null;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return Serializable.class;
            }
        };
        try {
            ConverterManager.getInstance().addPartialConverter(c);
            try {
                ConverterManager.getInstance().getPartialConverter(new DateTime());
                TestCase.fail();
            } catch (IllegalStateException ex) {
                // Serializable and ReadablePartial both match, so cannot pick
            }
        } finally {
            ConverterManager.getInstance().removePartialConverter(c);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testGetPartialConverters() {
        PartialConverter[] array = ConverterManager.getInstance().getPartialConverters();
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, array.length);
    }

    // -----------------------------------------------------------------------
    public void testAddPartialConverter1() {
        PartialConverter c = new PartialConverter() {
            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono) {
                return null;
            }

            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono, DateTimeFormatter parser) {
                return null;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        try {
            PartialConverter removed = ConverterManager.getInstance().addPartialConverter(c);
            TestCase.assertEquals(null, removed);
            TestCase.assertEquals(Boolean.class, ConverterManager.getInstance().getPartialConverter(Boolean.TRUE).getSupportedType());
            TestCase.assertEquals(((TestConverterManager.PARTIAL_SIZE) + 1), ConverterManager.getInstance().getPartialConverters().length);
        } finally {
            ConverterManager.getInstance().removePartialConverter(c);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testAddPartialConverter2() {
        PartialConverter c = new PartialConverter() {
            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono) {
                return null;
            }

            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono, DateTimeFormatter parser) {
                return null;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return String.class;
            }
        };
        try {
            PartialConverter removed = ConverterManager.getInstance().addPartialConverter(c);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(String.class, ConverterManager.getInstance().getPartialConverter("").getSupportedType());
            TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
        } finally {
            ConverterManager.getInstance().addPartialConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testAddPartialConverter3() {
        PartialConverter removed = ConverterManager.getInstance().addPartialConverter(StringConverter.INSTANCE);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testAddPartialConverter4() {
        PartialConverter removed = ConverterManager.getInstance().addPartialConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testAddPartialConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().addPartialConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testRemovePartialConverter1() {
        try {
            PartialConverter removed = ConverterManager.getInstance().removePartialConverter(StringConverter.INSTANCE);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(((TestConverterManager.PARTIAL_SIZE) - 1), ConverterManager.getInstance().getPartialConverters().length);
        } finally {
            ConverterManager.getInstance().addPartialConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testRemovePartialConverter2() {
        PartialConverter c = new PartialConverter() {
            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono) {
                return null;
            }

            public int[] getPartialValues(ReadablePartial partial, Object object, Chronology chrono, DateTimeFormatter parser) {
                return null;
            }

            public Chronology getChronology(Object object, DateTimeZone zone) {
                return null;
            }

            public Chronology getChronology(Object object, Chronology chrono) {
                return null;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        PartialConverter removed = ConverterManager.getInstance().removePartialConverter(c);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testRemovePartialConverter3() {
        PartialConverter removed = ConverterManager.getInstance().removePartialConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    public void testRemovePartialConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().removeInstantConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.PARTIAL_SIZE, ConverterManager.getInstance().getPartialConverters().length);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    private static int DURATION_SIZE = 5;

    public void testGetDurationConverter() {
        DurationConverter c = ConverterManager.getInstance().getDurationConverter(new Long(0L));
        TestCase.assertEquals(Long.class, c.getSupportedType());
        c = ConverterManager.getInstance().getDurationConverter(new Duration(123L));
        TestCase.assertEquals(ReadableDuration.class, c.getSupportedType());
        c = ConverterManager.getInstance().getDurationConverter(new Interval(0L, 1000L));
        TestCase.assertEquals(ReadableInterval.class, c.getSupportedType());
        c = ConverterManager.getInstance().getDurationConverter("");
        TestCase.assertEquals(String.class, c.getSupportedType());
        c = ConverterManager.getInstance().getDurationConverter(null);
        TestCase.assertEquals(null, c.getSupportedType());
        try {
            ConverterManager.getInstance().getDurationConverter(Boolean.TRUE);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetDurationConverterRemovedNull() {
        try {
            ConverterManager.getInstance().removeDurationConverter(INSTANCE);
            try {
                ConverterManager.getInstance().getDurationConverter(null);
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
        } finally {
            ConverterManager.getInstance().addDurationConverter(INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testGetDurationConverters() {
        DurationConverter[] array = ConverterManager.getInstance().getDurationConverters();
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, array.length);
    }

    // -----------------------------------------------------------------------
    public void testAddDurationConverter1() {
        DurationConverter c = new DurationConverter() {
            public long getDurationMillis(Object object) {
                return 0;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        try {
            DurationConverter removed = ConverterManager.getInstance().addDurationConverter(c);
            TestCase.assertEquals(null, removed);
            TestCase.assertEquals(Boolean.class, ConverterManager.getInstance().getDurationConverter(Boolean.TRUE).getSupportedType());
            TestCase.assertEquals(((TestConverterManager.DURATION_SIZE) + 1), ConverterManager.getInstance().getDurationConverters().length);
        } finally {
            ConverterManager.getInstance().removeDurationConverter(c);
        }
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    public void testAddDurationConverter2() {
        DurationConverter c = new DurationConverter() {
            public long getDurationMillis(Object object) {
                return 0;
            }

            public Class getSupportedType() {
                return String.class;
            }
        };
        try {
            DurationConverter removed = ConverterManager.getInstance().addDurationConverter(c);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(String.class, ConverterManager.getInstance().getDurationConverter("").getSupportedType());
            TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
        } finally {
            ConverterManager.getInstance().addDurationConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    public void testAddDurationConverter3() {
        DurationConverter removed = ConverterManager.getInstance().addDurationConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    public void testAddDurationConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().addDurationConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testRemoveDurationConverter1() {
        try {
            DurationConverter removed = ConverterManager.getInstance().removeDurationConverter(StringConverter.INSTANCE);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(((TestConverterManager.DURATION_SIZE) - 1), ConverterManager.getInstance().getDurationConverters().length);
        } finally {
            ConverterManager.getInstance().addDurationConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    public void testRemoveDurationConverter2() {
        DurationConverter c = new DurationConverter() {
            public long getDurationMillis(Object object) {
                return 0;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        DurationConverter removed = ConverterManager.getInstance().removeDurationConverter(c);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    public void testRemoveDurationConverter3() {
        DurationConverter removed = ConverterManager.getInstance().removeDurationConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    public void testRemoveDurationConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().removeDurationConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.DURATION_SIZE, ConverterManager.getInstance().getDurationConverters().length);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    private static int PERIOD_SIZE = 5;

    public void testGetPeriodConverter() {
        PeriodConverter c = ConverterManager.getInstance().getPeriodConverter(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        TestCase.assertEquals(ReadablePeriod.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPeriodConverter(new Duration(123L));
        TestCase.assertEquals(ReadableDuration.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPeriodConverter(new Interval(0L, 1000L));
        TestCase.assertEquals(ReadableInterval.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPeriodConverter("");
        TestCase.assertEquals(String.class, c.getSupportedType());
        c = ConverterManager.getInstance().getPeriodConverter(null);
        TestCase.assertEquals(null, c.getSupportedType());
        try {
            ConverterManager.getInstance().getPeriodConverter(Boolean.TRUE);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetPeriodConverterRemovedNull() {
        try {
            ConverterManager.getInstance().removePeriodConverter(INSTANCE);
            try {
                ConverterManager.getInstance().getPeriodConverter(null);
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
        } finally {
            ConverterManager.getInstance().addPeriodConverter(INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testGetPeriodConverters() {
        PeriodConverter[] array = ConverterManager.getInstance().getPeriodConverters();
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, array.length);
    }

    // -----------------------------------------------------------------------
    public void testAddPeriodConverter1() {
        PeriodConverter c = new PeriodConverter() {
            public void setInto(ReadWritablePeriod duration, Object object, Chronology c) {
            }

            public PeriodType getPeriodType(Object object) {
                return null;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        try {
            PeriodConverter removed = ConverterManager.getInstance().addPeriodConverter(c);
            TestCase.assertEquals(null, removed);
            TestCase.assertEquals(Boolean.class, ConverterManager.getInstance().getPeriodConverter(Boolean.TRUE).getSupportedType());
            TestCase.assertEquals(((TestConverterManager.PERIOD_SIZE) + 1), ConverterManager.getInstance().getPeriodConverters().length);
        } finally {
            ConverterManager.getInstance().removePeriodConverter(c);
        }
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    public void testAddPeriodConverter2() {
        PeriodConverter c = new PeriodConverter() {
            public void setInto(ReadWritablePeriod duration, Object object, Chronology c) {
            }

            public PeriodType getPeriodType(Object object) {
                return null;
            }

            public Class getSupportedType() {
                return String.class;
            }
        };
        try {
            PeriodConverter removed = ConverterManager.getInstance().addPeriodConverter(c);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(String.class, ConverterManager.getInstance().getPeriodConverter("").getSupportedType());
            TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
        } finally {
            ConverterManager.getInstance().addPeriodConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    public void testAddPeriodConverter3() {
        PeriodConverter removed = ConverterManager.getInstance().addPeriodConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    public void testAddPeriodConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().addPeriodConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testRemovePeriodConverter1() {
        try {
            PeriodConverter removed = ConverterManager.getInstance().removePeriodConverter(StringConverter.INSTANCE);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(((TestConverterManager.PERIOD_SIZE) - 1), ConverterManager.getInstance().getPeriodConverters().length);
        } finally {
            ConverterManager.getInstance().addPeriodConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    public void testRemovePeriodConverter2() {
        PeriodConverter c = new PeriodConverter() {
            public void setInto(ReadWritablePeriod duration, Object object, Chronology c) {
            }

            public PeriodType getPeriodType(Object object) {
                return null;
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        PeriodConverter removed = ConverterManager.getInstance().removePeriodConverter(c);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    public void testRemovePeriodConverter3() {
        PeriodConverter removed = ConverterManager.getInstance().removePeriodConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    public void testRemovePeriodConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().removePeriodConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.PERIOD_SIZE, ConverterManager.getInstance().getPeriodConverters().length);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    private static int INTERVAL_SIZE = 3;

    public void testGetIntervalConverter() {
        IntervalConverter c = ConverterManager.getInstance().getIntervalConverter(new Interval(0L, 1000L));
        TestCase.assertEquals(ReadableInterval.class, c.getSupportedType());
        c = ConverterManager.getInstance().getIntervalConverter("");
        TestCase.assertEquals(String.class, c.getSupportedType());
        c = ConverterManager.getInstance().getIntervalConverter(null);
        TestCase.assertEquals(null, c.getSupportedType());
        try {
            ConverterManager.getInstance().getIntervalConverter(Boolean.TRUE);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            ConverterManager.getInstance().getIntervalConverter(new Long(0));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetIntervalConverterRemovedNull() {
        try {
            ConverterManager.getInstance().removeIntervalConverter(INSTANCE);
            try {
                ConverterManager.getInstance().getIntervalConverter(null);
                TestCase.fail();
            } catch (IllegalArgumentException ex) {
            }
        } finally {
            ConverterManager.getInstance().addIntervalConverter(INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testGetIntervalConverters() {
        IntervalConverter[] array = ConverterManager.getInstance().getIntervalConverters();
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, array.length);
    }

    // -----------------------------------------------------------------------
    public void testAddIntervalConverter1() {
        IntervalConverter c = new IntervalConverter() {
            public boolean isReadableInterval(Object object, Chronology chrono) {
                return false;
            }

            public void setInto(ReadWritableInterval interval, Object object, Chronology chrono) {
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        try {
            IntervalConverter removed = ConverterManager.getInstance().addIntervalConverter(c);
            TestCase.assertEquals(null, removed);
            TestCase.assertEquals(Boolean.class, ConverterManager.getInstance().getIntervalConverter(Boolean.TRUE).getSupportedType());
            TestCase.assertEquals(((TestConverterManager.INTERVAL_SIZE) + 1), ConverterManager.getInstance().getIntervalConverters().length);
        } finally {
            ConverterManager.getInstance().removeIntervalConverter(c);
        }
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    public void testAddIntervalConverter2() {
        IntervalConverter c = new IntervalConverter() {
            public boolean isReadableInterval(Object object, Chronology chrono) {
                return false;
            }

            public void setInto(ReadWritableInterval interval, Object object, Chronology chrono) {
            }

            public Class getSupportedType() {
                return String.class;
            }
        };
        try {
            IntervalConverter removed = ConverterManager.getInstance().addIntervalConverter(c);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(String.class, ConverterManager.getInstance().getIntervalConverter("").getSupportedType());
            TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
        } finally {
            ConverterManager.getInstance().addIntervalConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    public void testAddIntervalConverter3() {
        IntervalConverter removed = ConverterManager.getInstance().addIntervalConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    public void testAddIntervalConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().addIntervalConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testRemoveIntervalConverter1() {
        try {
            IntervalConverter removed = ConverterManager.getInstance().removeIntervalConverter(StringConverter.INSTANCE);
            TestCase.assertEquals(StringConverter.INSTANCE, removed);
            TestCase.assertEquals(((TestConverterManager.INTERVAL_SIZE) - 1), ConverterManager.getInstance().getIntervalConverters().length);
        } finally {
            ConverterManager.getInstance().addIntervalConverter(StringConverter.INSTANCE);
        }
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    public void testRemoveIntervalConverter2() {
        IntervalConverter c = new IntervalConverter() {
            public boolean isReadableInterval(Object object, Chronology chrono) {
                return false;
            }

            public void setInto(ReadWritableInterval interval, Object object, Chronology chrono) {
            }

            public Class getSupportedType() {
                return Boolean.class;
            }
        };
        IntervalConverter removed = ConverterManager.getInstance().removeIntervalConverter(c);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    public void testRemoveIntervalConverter3() {
        IntervalConverter removed = ConverterManager.getInstance().removeIntervalConverter(null);
        TestCase.assertEquals(null, removed);
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    public void testRemoveIntervalConverterSecurity() {
        if (TestConverterManager.OLD_JDK) {
            return;
        }
        try {
            Policy.setPolicy(TestConverterManager.RESTRICT);
            System.setSecurityManager(new SecurityManager());
            ConverterManager.getInstance().removeIntervalConverter(StringConverter.INSTANCE);
            TestCase.fail();
        } catch (SecurityException ex) {
            // ok
        } finally {
            System.setSecurityManager(null);
            Policy.setPolicy(TestConverterManager.ALLOW);
        }
        TestCase.assertEquals(TestConverterManager.INTERVAL_SIZE, ConverterManager.getInstance().getIntervalConverters().length);
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("ConverterManager[6 instant,7 partial,5 duration,5 period,3 interval]", ConverterManager.getInstance().toString());
    }
}

