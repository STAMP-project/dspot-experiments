package edu.umd.cs.findbugs.ba.npe;


import org.junit.Assert;
import org.junit.Test;


public class IsNullValueTest {
    @Test
    public void testMerge1() {
        IsNullValue nullValue = IsNullValue.nullValue();
        IsNullValue nullExceptionValue = IsNullValue.nullValue().toExceptionValue();
        IsNullValue result = IsNullValue.merge(nullValue, nullExceptionValue);
        Assert.assertTrue(result.isDefinitelyNull());
        Assert.assertFalse(result.isException());
    }

    @Test
    public void testMerge2() {
        IsNullValue nullExceptionValue = IsNullValue.nullValue().toExceptionValue();
        IsNullValue nonNullValue = IsNullValue.nonNullValue();
        IsNullValue nsp_e = IsNullValue.merge(nonNullValue, nullExceptionValue);
        Assert.assertTrue(nsp_e.isNullOnSomePath());
        Assert.assertTrue(nsp_e.isException());
        Assert.assertEquals(nsp_e, IsNullValue.nullOnSimplePathValue().toExceptionValue());
    }

    @Test
    public void testMerge3() {
        IsNullValue nullValue = IsNullValue.nullValue();
        IsNullValue nsp_e = IsNullValue.nullOnSimplePathValue().toExceptionValue();
        IsNullValue nsp = IsNullValue.merge(nullValue, nsp_e);
        Assert.assertTrue(nsp.isNullOnSomePath());
        Assert.assertFalse(nsp.isException());
    }

    @Test
    public void testMerge5() {
        IsNullValue checkedNonNull = IsNullValue.checkedNonNullValue();
        IsNullValue nsp_e = IsNullValue.nullOnSimplePathValue().toExceptionValue();
        IsNullValue nsp_e2 = IsNullValue.merge(checkedNonNull, nsp_e);
        Assert.assertTrue(nsp_e2.isNullOnSomePath());
        Assert.assertTrue(nsp_e2.isException());
    }

    @Test
    public void testMerge6() {
        IsNullValue checkedNull_e = IsNullValue.checkedNullValue().toExceptionValue();
        IsNullValue unknown = IsNullValue.nonReportingNotNullValue();
        IsNullValue nsp_e = IsNullValue.merge(checkedNull_e, unknown);
        Assert.assertTrue(nsp_e.isNullOnSomePath());
        Assert.assertTrue(nsp_e.isException());
    }
}

