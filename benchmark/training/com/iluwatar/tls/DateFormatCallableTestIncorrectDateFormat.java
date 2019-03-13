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
 * In this test {@link DateFormatCallable} is tested with only one thread (i.e. without concurrency situation)
 * <p>
 * An incorrect formatted date is passed to the Callable
 * After a successful run 0 date values and 5 exceptions should be in the result object.
 *
 * @author Thomas Bauer, January 2017
 */
public class DateFormatCallableTestIncorrectDateFormat {
    // Class variables used in setup() have to be static because setup() has to be static
    /**
     * Result object given back by DateFormatCallable
     *   -- Array with converted date values
     *   -- Array with thrown exceptions
     */
    static Result result;

    /**
     * The date values created by the run of DateFormatRunnalbe. List will be filled in the setup() method
     */
    static List<String> createdExceptions = new ArrayList<String>();

    /**
     * Expected number of date values in the date value list created by the run of DateFormatRunnalbe
     */
    int expectedCounterDateValues = 0;

    /**
     * Expected number of exceptions in the exception list created by the run of DateFormatRunnalbe.
     */
    int expectedCounterExceptions = 5;

    /**
     * Expected content of the list containing the exceptions created by the run of DateFormatRunnalbe
     */
    List<String> expectedExceptions = Arrays.asList("class java.text.ParseException: Unparseable date: \"15.12.2015\"", "class java.text.ParseException: Unparseable date: \"15.12.2015\"", "class java.text.ParseException: Unparseable date: \"15.12.2015\"", "class java.text.ParseException: Unparseable date: \"15.12.2015\"", "class java.text.ParseException: Unparseable date: \"15.12.2015\"");

    /**
     * Test Exceptions after the run of DateFormatRunnalbe. A correct run should deliver 5 times the
     * same exception
     */
    @Test
    public void testExceptions() {
        Assertions.assertEquals(expectedExceptions, DateFormatCallableTestIncorrectDateFormat.result.getExceptionList());
    }

    /**
     * Test number of dates in the list after the run of DateFormatRunnalbe. A correct run should deliver no date values
     */
    @Test
    public void testCounterDateValues() {
        Assertions.assertEquals(expectedCounterDateValues, DateFormatCallableTestIncorrectDateFormat.result.getDateList().size());
    }

    /**
     * Test number of Exceptions in the list after the run of DateFormatRunnalbe. A correct run should
     * deliver 5 exceptions
     */
    @Test
    public void testCounterExceptions() {
        Assertions.assertEquals(expectedCounterExceptions, DateFormatCallableTestIncorrectDateFormat.result.getExceptionList().size());
    }
}

