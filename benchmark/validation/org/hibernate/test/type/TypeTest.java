/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import BigDecimalType.INSTANCE;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TypeTest extends BaseUnitTestCase {
    @Test
    public void testBigDecimalType() {
        final BigDecimal original = BigDecimal.valueOf(100);
        final BigDecimal copy = BigDecimal.valueOf(100);
        final BigDecimal different = BigDecimal.valueOf(999);
        runBasicTests(INSTANCE, original, copy, different);
    }

    @Test
    public void testBigIntegerType() {
        final BigInteger original = BigInteger.valueOf(100);
        final BigInteger copy = BigInteger.valueOf(100);
        final BigInteger different = BigInteger.valueOf(999);
        runBasicTests(BigIntegerType.INSTANCE, original, copy, different);
    }

    @Test
    public void testBinaryType() {
        final byte[] original = new byte[]{ 1, 2, 3, 4 };
        final byte[] copy = new byte[]{ 1, 2, 3, 4 };
        final byte[] different = new byte[]{ 4, 3, 2, 1 };
        runBasicTests(BinaryType.INSTANCE, original, copy, different);
        runBasicTests(ImageType.INSTANCE, original, copy, different);
        runBasicTests(MaterializedBlobType.INSTANCE, original, copy, different);
    }

    @Test
    @SuppressWarnings({ "BooleanConstructorCall" })
    public void testBooleanType() {
        final Boolean original = Boolean.TRUE;
        final Boolean copy = new Boolean(true);
        final Boolean different = Boolean.FALSE;
        runBasicTests(BooleanType.INSTANCE, original, copy, different);
        runBasicTests(NumericBooleanType.INSTANCE, original, copy, different);
        runBasicTests(YesNoType.INSTANCE, original, copy, different);
        runBasicTests(TrueFalseType.INSTANCE, original, copy, different);
    }

    @Test
    public void testByteType() {
        final Byte original = 0;
        final Byte copy = new Byte(((byte) (0)));
        final Byte different = 9;
        runBasicTests(ByteType.INSTANCE, original, copy, different);
    }

    @Test
    public void testCalendarDateType() {
        final Calendar original = new GregorianCalendar();
        final Calendar copy = new GregorianCalendar();
        final Calendar different = new GregorianCalendar();
        different.set(Calendar.MONTH, 9);
        different.set(Calendar.DAY_OF_MONTH, 9);
        different.set(Calendar.YEAR, 2999);
        runBasicTests(CalendarDateType.INSTANCE, original, copy, different);
    }

    @Test
    public void testCalendarType() {
        final long now = System.currentTimeMillis();
        final Calendar original = new GregorianCalendar();
        original.clear();
        original.setTimeInMillis(now);
        final Calendar copy = new GregorianCalendar();
        copy.clear();
        copy.setTimeInMillis(now);
        final Calendar different = new GregorianCalendar();
        different.setTimeInMillis((now + 9999));
        runBasicTests(CalendarType.INSTANCE, original, copy, different);
    }

    @Test
    public void testCharacterArrayType() {
        final Character[] original = new Character[]{ 'a', 'b' };
        final Character[] copy = new Character[]{ 'a', 'b' };
        final Character[] different = new Character[]{ 'a', 'b', 'c' };
        runBasicTests(CharacterArrayType.INSTANCE, original, copy, different);
    }

    @Test
    public void testCharacterType() {
        final Character original = 'a';
        final Character copy = new Character('a');
        final Character different = 'b';
        runBasicTests(CharacterType.INSTANCE, original, copy, different);
    }

    @Test
    public void testCharArrayType() {
        final char[] original = new char[]{ 'a', 'b' };
        final char[] copy = new char[]{ 'a', 'b' };
        final char[] different = new char[]{ 'a', 'b', 'c' };
        runBasicTests(CharArrayType.INSTANCE, original, copy, different);
        runBasicTests(CharArrayType.INSTANCE, original, copy, different);
    }

    @Test
    public void testClassType() {
        final Class original = TypeTest.class;
        final Class copy = ((Class) (SerializationHelper.clone(original)));
        final Class different = String.class;
        runBasicTests(ClassType.INSTANCE, original, copy, different);
    }

    @Test
    public void testCurrencyType() {
        final Currency original = Currency.getInstance(Locale.US);
        final Currency copy = Currency.getInstance(Locale.US);
        final Currency different = Currency.getInstance(Locale.UK);
        runBasicTests(CurrencyType.INSTANCE, original, copy, different);
    }

    @Test
    public void testDateType() {
        final long now = System.currentTimeMillis();
        final Date original = new Date(now);
        final Date copy = new Date(now);
        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.setTimeInMillis(now);
        cal.add(Calendar.YEAR, 1);
        final Date different = new Date(cal.getTime().getTime());
        runBasicTests(DateType.INSTANCE, original, copy, different);
    }

    @Test
    public void testDoubleType() {
        final Double original = Double.valueOf(100);
        final Double copy = Double.valueOf(100);
        final Double different = Double.valueOf(999);
        runBasicTests(DoubleType.INSTANCE, original, copy, different);
    }

    @Test
    public void testFloatType() {
        final Float original = Float.valueOf(100);
        final Float copy = Float.valueOf(100);
        final Float different = Float.valueOf(999);
        runBasicTests(FloatType.INSTANCE, original, copy, different);
    }

    @Test
    public void testIntegerType() {
        final Integer original = 100;
        final Integer copy = new Integer(100);
        final Integer different = 999;
        runBasicTests(IntegerType.INSTANCE, original, copy, different);
    }

    @Test
    public void testLocaleType() {
        final Locale original = new Locale("ab");
        final Locale copy = new Locale("ab");
        final Locale different = new Locale("yz");
        runBasicTests(LocaleType.INSTANCE, original, copy, different);
    }

    @Test
    public void testLongType() {
        final Long original = 100L;
        final Long copy = new Long(100L);
        final Long different = 999L;
        runBasicTests(LongType.INSTANCE, original, copy, different);
    }

    private static class SerializableImpl implements Serializable {
        private final int number;

        SerializableImpl(int number) {
            this.number = number;
        }

        @SuppressWarnings({ "EqualsWhichDoesntCheckParameterClass" })
        public boolean equals(Object obj) {
            return (this.number) == (((TypeTest.SerializableImpl) (obj)).number);
        }
    }

    @Test
    public void testSerializableType() {
        final TypeTest.SerializableImpl original = new TypeTest.SerializableImpl(1);
        final TypeTest.SerializableImpl copy = new TypeTest.SerializableImpl(1);
        final TypeTest.SerializableImpl different = new TypeTest.SerializableImpl(2);
        runBasicTests(SerializableType.INSTANCE, original, copy, different);
        runBasicTests(new org.hibernate.type.SerializableType<TypeTest.SerializableImpl>(TypeTest.SerializableImpl.class), original, copy, different);
    }

    @Test
    public void testShortType() {
        final Short original = 100;
        final Short copy = new Short(((short) (100)));
        final Short different = 999;
        runBasicTests(ShortType.INSTANCE, original, copy, different);
    }

    @Test
    public void testStringType() {
        final String original = "abc";
        final String copy = new String(original.toCharArray());
        final String different = "xyz";
        runBasicTests(StringType.INSTANCE, original, copy, different);
        runBasicTests(TextType.INSTANCE, original, copy, different);
        runBasicTests(MaterializedClobType.INSTANCE, original, copy, different);
    }

    @Test
    public void testTimestampType() {
        final long now = System.currentTimeMillis();
        final Timestamp original = new Timestamp(now);
        final Timestamp copy = new Timestamp(now);
        final Timestamp different = new Timestamp((now + 9999));
        runBasicTests(TimestampType.INSTANCE, original, copy, different);
    }

    @Test
    public void testTimeType() {
        final long now = System.currentTimeMillis();
        final Time original = new Time(now);
        final Time copy = new Time(now);
        final Time different = new Time((now + 9999));
        runBasicTests(TimeType.INSTANCE, original, copy, different);
    }

    @Test
    public void testDates() {
        final long now = System.currentTimeMillis();
        final java.util.Date original = new java.util.Date(now);
        final java.util.Date copy = new java.util.Date(now);
        final java.util.Date different = new java.util.Date((now + 9999));
        final java.util.Date different2 = new java.util.Date((now + ((((1000L * 60L) * 60L) * 24L) * 365L)));
        runBasicTests(TimeType.INSTANCE, original, copy, different);
        runBasicTests(TimestampType.INSTANCE, original, copy, different);
        runBasicTests(DateType.INSTANCE, original, copy, different2);
    }

    @Test
    public void testTimeZoneType() {
        final TimeZone original = new SimpleTimeZone((-1), "abc");
        final TimeZone copy = new SimpleTimeZone((-1), "abc");
        final TimeZone different = new SimpleTimeZone((-2), "xyz");
        runBasicTests(TimeZoneType.INSTANCE, original, copy, different);
    }
}

