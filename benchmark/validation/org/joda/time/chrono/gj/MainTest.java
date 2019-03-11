/**
 * Copyright 2001-2005 Stephen Colebourne
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
package org.joda.time.chrono.gj;


import java.util.Random;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.JulianChronology;


/**
 * Tests either the Julian or Gregorian chronology from org.joda.time.chrono.gj
 * against the implementations in this package. It tests all the date fields
 * against their principal methods.
 * <p>
 * Randomly generated values are fed into the DateTimeField methods and the
 * results are compared between the two chronologies. If any result doesn't
 * match, an error report is generated and the program exits. Each time this
 * test program is run, the pseudo random number generator is seeded with the
 * same value. This ensures consistent results between test runs.
 * <p>
 * The main method accepts three optional arguments: iterations, mode, seed. By
 * default, iterations is set to 1,000,000. The test will take several minutes
 * to run, depending on the computer's performance. Every 5 seconds a progress
 * message is printed.
 * <p>
 * The mode can be either 'g' for proleptic gregorian (the default) or 'j' for
 * proleptic julian. To override the default random number generator seed, pass
 * in a third argument which accepts a long signed integer.
 *
 * @author Brian S O'Neill
 */
public class MainTest extends TestCase {
    public static final int GREGORIAN_MODE = 0;

    public static final int JULIAN_MODE = 1;

    private static final long MILLIS_PER_YEAR = ((long) ((((365.2425 * 24) * 60) * 60) * 1000));

    private static final long _1000_YEARS = 1000 * (MainTest.MILLIS_PER_YEAR);

    private static final long _500_YEARS = 500 * (MainTest.MILLIS_PER_YEAR);

    private static final long MAX_MILLIS = (10000 - 1970) * (MainTest.MILLIS_PER_YEAR);

    private static final long MIN_MILLIS = ((-10000) - 1970) * (MainTest.MILLIS_PER_YEAR);

    // Show progess reports every 5 seconds.
    private static final long UPDATE_INTERVAL = 5000;

    // -----------------------------------------------------------------------
    private final int iIterations;

    private final int iMode;

    private final long iSeed;

    private final Chronology iTest;

    private final Chronology iActual;

    /**
     *
     *
     * @param iterations
     * 		number of test iterations to perform
     * @param mode
     * 		GREGORIAN_MODE or JULIAN_MODE,0=Gregorian, 1=Julian
     * @param seed
     * 		seed for random number generator
     */
    public MainTest(int iterations, int mode, long seed) {
        super("testChronology");
        iIterations = iterations;
        iMode = mode;
        iSeed = seed;
        if (mode == (MainTest.GREGORIAN_MODE)) {
            iTest = new TestGregorianChronology();
            iActual = GregorianChronology.getInstanceUTC();
        } else {
            iTest = new TestJulianChronology();
            iActual = JulianChronology.getInstanceUTC();
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Main junit test
     */
    public void testChronology() {
        int iterations = iIterations;
        long seed = iSeed;
        String modeStr;
        if ((iMode) == (MainTest.GREGORIAN_MODE)) {
            modeStr = "Gregorian";
        } else {
            modeStr = "Julian";
        }
        System.out.println((((("\nTesting " + modeStr) + " chronology over ") + iterations) + " iterations"));
        Random rnd = new Random(seed);
        long updateMillis = (System.currentTimeMillis()) + (MainTest.UPDATE_INTERVAL);
        for (int i = 0; i < iterations; i++) {
            long now = System.currentTimeMillis();
            if (now >= updateMillis) {
                updateMillis = now + (MainTest.UPDATE_INTERVAL);
                double complete = ((int) ((((double) (i)) / iterations) * 1000.0)) / 10.0;
                if (complete < 100) {
                    System.out.println((((("" + complete) + "% complete (i=") + i) + ")"));
                }
            }
            long millis = MainTest.randomMillis(rnd);
            int value = (rnd.nextInt(200)) - 100;
            // millis2 is used for difference tests.
            long millis2 = (millis + ((rnd.nextLong()) % (MainTest._1000_YEARS))) - (MainTest._500_YEARS);
            try {
                testFields(millis, value, millis2);
            } catch (RuntimeException e) {
                System.out.println(("Failure index: " + i));
                System.out.println(("Test millis: " + millis));
                System.out.println(("Test value: " + value));
                System.out.println(("Test millis2: " + millis2));
                TestCase.fail(e.getMessage());
            }
        }
        System.out.println((("100% complete (i=" + iterations) + ")"));
    }
}

