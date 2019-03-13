/**
 * The MIT License
 * Copyright (c) 2016 Thomas Bauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.tls;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test of the Callable
 *
 * In this test {@link DateFormatCallable} is used by 4 threads in parallel
 * <p>
 * After a successful run 5 date values should be in the result object of each thread. All dates
 * should have the same value (15.11.2015). To avoid problems with time zone not the date instances
 * themselves are compared by the test. For the test the dates are converted into string format DD.MM.YYY
 * <p>
 * Additionally the number of list entries are tested for both the list with the date values
 * and the list with the exceptions
 *
 * @author Thomas Bauer, January 2017
 */
public class DateFormatCallableTestMultiThread {
    // Class variables used in setup() have to be static because setup() has to be static
    /**
     * Result object given back by DateFormatCallable, one for each thread
     *   -- Array with converted date values
     *   -- Array with thrown exceptions
     */
    static Result[] result = new Result[4];

    /**
     * The date values created by the run of of DateFormatRunnalbe. List will be filled in the setup() method
     */
    /* nothing needed here */
    @SuppressWarnings("serial")
    static class StringArrayList extends ArrayList<String> {}

    static List<String>[] createdDateValues = new DateFormatCallableTestMultiThread.StringArrayList[4];

    /**
     * Expected number of date values in the date value list created by each thread
     */
    int expectedCounterDateValues = 5;

    /**
     * Expected number of exceptions in the exception list created by each thread
     */
    int expectedCounterExceptions = 0;

    /**
     * Expected content of the list containing the date values created by each thread
     */
    List<String> expectedDateValues = Arrays.asList("15.11.2015", "15.11.2015", "15.11.2015", "15.11.2015", "15.11.2015");

    /**
     * Test date values after the run of DateFormatRunnalbe. A correct run should deliver 5 times 15.12.2015
     * by each thread
     */
    @Test
    public void testDateValues() {
        for (int i = 0; i < (DateFormatCallableTestMultiThread.createdDateValues.length); i++) {
            Assertions.assertEquals(expectedDateValues, DateFormatCallableTestMultiThread.createdDateValues[i]);
        }
    }

    /**
     * Test number of dates in the list after the run of DateFormatRunnalbe. A correct run should
     * deliver 5 date values by each thread
     */
    @Test
    public void testCounterDateValues() {
        for (int i = 0; i < (DateFormatCallableTestMultiThread.result.length); i++) {
            Assertions.assertEquals(expectedCounterDateValues, DateFormatCallableTestMultiThread.result[i].getDateList().size());
        }
    }

    /**
     * Test number of Exceptions in the list after the run of DateFormatRunnalbe. A correct run should
     * deliver no exceptions
     */
    @Test
    public void testCounterExceptions() {
        for (int i = 0; i < (DateFormatCallableTestMultiThread.result.length); i++) {
            Assertions.assertEquals(expectedCounterExceptions, DateFormatCallableTestMultiThread.result[i].getExceptionList().size());
        }
    }
}

