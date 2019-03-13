package com.orientechnologies.orient.core.metadata.schema;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the covert method of the OType class.
 *
 * @author Michael MacFadden
 */
public class OTypeConvertTest {
    // 
    // General cases
    // 
    @Test
    public void testSameType() {
        ArrayList<Object> aList = new ArrayList<Object>();
        aList.add(1);
        aList.add("2");
        Object result = OType.convert(aList, ArrayList.class);
        Assert.assertEquals(result, aList);
    }

    @Test
    public void testAssignableType() {
        ArrayList<Object> aList = new ArrayList<Object>();
        aList.add(1);
        aList.add("2");
        Object result = OType.convert(aList, List.class);
        Assert.assertEquals(result, aList);
    }

    @Test
    public void testNull() {
        Object result = OType.convert(null, Boolean.class);
        Assert.assertEquals(result, null);
    }

    @Test
    public void testCannotConvert() {
        // Expected behavior is to not convert and return null
        Object result = OType.convert(true, Long.class);
        Assert.assertEquals(result, null);
    }

    // 
    // To String
    // 
    @Test
    public void testToStringFromString() {
        Object result = OType.convert("foo", String.class);
        Assert.assertEquals(result, "foo");
    }

    @Test
    public void testToStringFromNumber() {
        Object result = OType.convert(10, String.class);
        Assert.assertEquals(result, "10");
    }

    // 
    // To Byte
    // 
    @Test
    public void testToBytePrimitiveFromByte() {
        Object result = OType.convert(((byte) (10)), Byte.TYPE);
        Assert.assertEquals(result, ((byte) (10)));
    }

    @Test
    public void testToByteFromByte() {
        Object result = OType.convert(((byte) (10)), Byte.class);
        Assert.assertEquals(result, ((byte) (10)));
    }

    @Test
    public void testToByteFromString() {
        Object result = OType.convert("10", Byte.class);
        Assert.assertEquals(result, ((byte) (10)));
    }

    @Test
    public void testToByteFromNumber() {
        Object result = OType.convert(10.0, Byte.class);
        Assert.assertEquals(result, ((byte) (10)));
    }

    // 
    // To Short
    // 
    @Test
    public void testToShortPrmitveFromShort() {
        Object result = OType.convert(((short) (10)), Short.TYPE);
        Assert.assertEquals(result, ((short) (10)));
    }

    @Test
    public void testToShortFromShort() {
        Object result = OType.convert(((short) (10)), Short.class);
        Assert.assertEquals(result, ((short) (10)));
    }

    @Test
    public void testToShortFromString() {
        Object result = OType.convert("10", Short.class);
        Assert.assertEquals(result, ((short) (10)));
    }

    @Test
    public void testToShortFromNumber() {
        Object result = OType.convert(10.0, Short.class);
        Assert.assertEquals(result, ((short) (10)));
    }

    // 
    // To Integer
    // 
    @Test
    public void testToIntegerPrimitveFromInteger() {
        Object result = OType.convert(10, Integer.TYPE);
        Assert.assertEquals(result, 10);
    }

    @Test
    public void testToIntegerFromInteger() {
        Object result = OType.convert(10, Integer.class);
        Assert.assertEquals(result, 10);
    }

    @Test
    public void testToIntegerFromString() {
        Object result = OType.convert("10", Integer.class);
        Assert.assertEquals(result, 10);
    }

    @Test
    public void testToIntegerFromNumber() {
        Object result = OType.convert(10.0, Integer.class);
        Assert.assertEquals(result, 10);
    }

    // 
    // To Long
    // 
    @Test
    public void testToLongPrimitiveFromLong() {
        Object result = OType.convert(10L, Long.TYPE);
        Assert.assertEquals(result, 10L);
    }

    @Test
    public void testToLongFromLong() {
        Object result = OType.convert(10L, Long.class);
        Assert.assertEquals(result, 10L);
    }

    @Test
    public void testToLongFromString() {
        Object result = OType.convert("10", Long.class);
        Assert.assertEquals(result, 10L);
    }

    @Test
    public void testToLongFromNumber() {
        Object result = OType.convert(10.0, Long.class);
        Assert.assertEquals(result, 10L);
    }

    // 
    // To Float
    // 
    @Test
    public void testToFloatPrimitiveFromFloat() {
        Object result = OType.convert(10.65F, Float.TYPE);
        Assert.assertEquals(result, 10.65F);
    }

    @Test
    public void testToFloatFromFloat() {
        Object result = OType.convert(10.65F, Float.class);
        Assert.assertEquals(result, 10.65F);
    }

    @Test
    public void testToFloatFromString() {
        Object result = OType.convert("10.65", Float.class);
        Assert.assertEquals(result, 10.65F);
    }

    @Test
    public void testToFloatFromNumber() {
        Object result = OType.convert(4, Float.class);
        Assert.assertEquals(result, 4.0F);
    }

    // 
    // To BigDecimal
    // 
    @Test
    public void testToBigDecimalFromBigDecimal() {
        Object result = OType.convert(new BigDecimal("10.65"), BigDecimal.class);
        Assert.assertEquals(result, new BigDecimal("10.65"));
    }

    @Test
    public void testToBigDecimalFromString() {
        Object result = OType.convert("10.65", BigDecimal.class);
        Assert.assertEquals(result, new BigDecimal("10.65"));
    }

    @Test
    public void testToBigDecimalFromNumber() {
        Object result = OType.convert(4.98, BigDecimal.class);
        Assert.assertEquals(result, new BigDecimal("4.98"));
    }

    // 
    // To Double
    // 
    @Test
    public void testToDoublePrimitiveFromDouble() {
        Object result = OType.convert(5.4, Double.TYPE);
        Assert.assertEquals(result, 5.4);
    }

    @Test
    public void testToDoubleFromDouble() {
        Object result = OType.convert(5.4, Double.class);
        Assert.assertEquals(result, 5.4);
    }

    @Test
    public void testToDoubleFromString() {
        Object result = OType.convert("5.4", Double.class);
        Assert.assertEquals(result, 5.4);
    }

    @Test
    public void testToDoubleFromFloat() {
        Object result = OType.convert(5.4F, Double.class);
        Assert.assertEquals(result, 5.4);
    }

    @Test
    public void testToDoubleFromNonFloatNumber() {
        Object result = OType.convert(5, Double.class);
        Assert.assertEquals(result, 5.0);
    }

    // 
    // To Boolean
    // 
    @Test
    public void testToBooleanPrimitiveFromBoolean() {
        Object result = OType.convert(true, Boolean.TYPE);
        Assert.assertEquals(result, true);
    }

    @Test
    public void testToBooleanFromBoolean() {
        Object result = OType.convert(true, Boolean.class);
        Assert.assertEquals(result, true);
    }

    @Test
    public void testToBooleanFromFalseString() {
        Object result = OType.convert("false", Boolean.class);
        Assert.assertEquals(result, false);
    }

    @Test
    public void testToBooleanFromTrueString() {
        Object result = OType.convert("true", Boolean.class);
        Assert.assertEquals(result, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBooleanFromInvalidString() {
        OType.convert("invalid", Boolean.class);
    }

    @Test
    public void testToBooleanFromZeroNumber() {
        Object result = OType.convert(0, Boolean.class);
        Assert.assertEquals(result, false);
    }

    @Test
    public void testToBooleanFromNonZeroNumber() {
        Object result = OType.convert(1, Boolean.class);
        Assert.assertEquals(result, true);
    }

    // 
    // To Date
    // 
    @Test
    public void testToDateFromDate() {
        Date d = Calendar.getInstance().getTime();
        Object result = OType.convert(d, Date.class);
        Assert.assertEquals(result, d);
    }

    @Test
    public void testToDateFromNumber() {
        Long time = System.currentTimeMillis();
        Object result = OType.convert(time, Date.class);
        Assert.assertEquals(result, new Date(time));
    }

    @Test
    public void testToDateFromLongString() {
        Long time = System.currentTimeMillis();
        Object result = OType.convert(time.toString(), Date.class);
        Assert.assertEquals(result, new Date(time));
    }

    @Test
    public void testToDateFromDateString() {
        Long time = System.currentTimeMillis();
        Object result = OType.convert(time.toString(), Date.class);
        Assert.assertEquals(result, new Date(time));
    }

    // 
    // To Set
    // 
    @Test
    public void testToSetFromSet() {
        HashSet<Object> set = new HashSet<Object>();
        set.add(1);
        set.add("2");
        Object result = OType.convert(set, Set.class);
        Assert.assertEquals(result, set);
    }

    @Test
    public void testToSetFromCollection() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add("2");
        Object result = OType.convert(list, Set.class);
        HashSet<Object> expected = new HashSet<Object>();
        expected.add(1);
        expected.add("2");
        Assert.assertEquals(result, expected);
    }

    @Test
    public void testToSetFromNonCollection() {
        HashSet<Object> set = new HashSet<Object>();
        set.add(1);
        Object result = OType.convert(1, Set.class);
        Assert.assertEquals(result, set);
    }

    // 
    // To List
    // 
    @Test
    public void testToListFromList() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add("2");
        Object result = OType.convert(list, List.class);
        Assert.assertEquals(result, list);
    }

    @Test
    public void testToListFromCollection() {
        HashSet<Object> set = new HashSet<Object>();
        set.add(1);
        set.add("2");
        @SuppressWarnings("unchecked")
        List<Object> result = ((List<Object>) (OType.convert(set, List.class)));
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.containsAll(set));
    }

    @Test
    public void testToListFromNonCollection() {
        ArrayList<Object> expected = new ArrayList<Object>();
        expected.add(1);
        Object result = OType.convert(1, List.class);
        Assert.assertEquals(result, expected);
    }

    // 
    // To List
    // 
    @Test
    public void testToCollectionFromList() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add("2");
        Object result = OType.convert(list, Collection.class);
        Assert.assertEquals(result, list);
    }

    @Test
    public void testToCollectionFromCollection() {
        HashSet<Object> set = new HashSet<Object>();
        set.add(1);
        set.add("2");
        @SuppressWarnings("unchecked")
        Collection<Object> result = ((Collection<Object>) (OType.convert(set, Collection.class)));
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.containsAll(set));
    }

    @Test
    public void testToCollectionFromNonCollection() {
        @SuppressWarnings("unchecked")
        Collection<Object> result = ((Collection<Object>) (OType.convert(1, Collection.class)));
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(1));
    }
}

