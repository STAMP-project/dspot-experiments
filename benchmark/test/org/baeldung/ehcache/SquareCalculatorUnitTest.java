package org.baeldung.ehcache;


import org.baeldung.ehcache.calculator.SquaredCalculator;
import org.baeldung.ehcache.config.CacheHelper;
import org.junit.Assert;
import org.junit.Test;


public class SquareCalculatorUnitTest {
    private SquaredCalculator squaredCalculator = new SquaredCalculator();

    private CacheHelper cacheHelper = new CacheHelper();

    @Test
    public void whenCalculatingSquareValueOnce_thenCacheDontHaveValues() {
        for (int i = 10; i < 15; i++) {
            Assert.assertFalse(cacheHelper.getSquareNumberCache().containsKey(i));
            System.out.println((((("Square value of " + i) + " is: ") + (squaredCalculator.getSquareValueOfNumber(i))) + "\n"));
        }
    }

    @Test
    public void whenCalculatingSquareValueAgain_thenCacheHasAllValues() {
        for (int i = 10; i < 15; i++) {
            Assert.assertFalse(cacheHelper.getSquareNumberCache().containsKey(i));
            System.out.println((((("Square value of " + i) + " is: ") + (squaredCalculator.getSquareValueOfNumber(i))) + "\n"));
        }
        for (int i = 10; i < 15; i++) {
            Assert.assertTrue(cacheHelper.getSquareNumberCache().containsKey(i));
            System.out.println((((("Square value of " + i) + " is: ") + (squaredCalculator.getSquareValueOfNumber(i))) + "\n"));
        }
    }
}

