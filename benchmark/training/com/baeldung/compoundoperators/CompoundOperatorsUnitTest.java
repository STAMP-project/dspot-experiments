package com.baeldung.compoundoperators;


import org.junit.Assert;
import org.junit.Test;


public class CompoundOperatorsUnitTest {
    @Test
    public void whenAssignmentOperatorIsUsed_thenValueIsAssigned() {
        int x = 5;
        Assert.assertEquals(5, x);
    }

    @Test
    public void whenCompoundAssignmentUsed_thenSameAsSimpleAssignment() {
        int a = 3;
        int b = 3;
        int c = -2;
        a = a * c;// Simple assignment operator

        b *= c;// Compound assignment operator

        Assert.assertEquals(a, b);
    }

    @Test
    public void whenAssignmentOperatorIsUsed_thenValueIsReturned() {
        long x = 1;
        long y = x += 2;
        Assert.assertEquals(3, y);
        Assert.assertEquals(y, x);
    }

    @Test
    public void whenCompoundOperatorsAreUsed_thenOperationsArePerformedAndAssigned() {
        // Simple assignment
        int x = 5;// x is 5

        // Incrementation
        x += 5;// x is 10

        Assert.assertEquals(10, x);
        // Decrementation
        x -= 2;// x is 8

        Assert.assertEquals(8, x);
        // Multiplication
        x *= 2;// x is 16

        Assert.assertEquals(16, x);
        // Division
        x /= 4;// x is 4

        Assert.assertEquals(4, x);
        // Modulus
        x %= 3;// x is 1

        Assert.assertEquals(1, x);
        // Binary AND
        x &= 4;// x is 0

        Assert.assertEquals(0, x);
        // Binary exclusive OR
        x ^= 4;// x is 4

        Assert.assertEquals(4, x);
        // Binary inclusive OR
        x |= 8;// x is 12

        Assert.assertEquals(12, x);
        // Binary Left Shift
        x <<= 2;// x is 48

        Assert.assertEquals(48, x);
        // Binary Right Shift
        x >>= 2;// x is 12

        Assert.assertEquals(12, x);
        // Shift right zero fill
        x >>>= 1;// x is 6

        Assert.assertEquals(6, x);
    }

    @Test(expected = NullPointerException.class)
    public void whenArrayIsNull_thenThrowNullException() {
        int[] numbers = null;
        // Trying Incrementation
        numbers[2] += 5;
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void whenArrayIndexNotCorrect_thenThrowArrayIndexException() {
        int[] numbers = new int[]{ 0, 1 };
        // Trying Incrementation
        numbers[2] += 5;
    }

    @Test
    public void whenArrayIndexIsCorrect_thenPerformOperation() {
        int[] numbers = new int[]{ 0, 1 };
        // Incrementation
        numbers[1] += 5;
        Assert.assertEquals(6, numbers[1]);
    }
}

