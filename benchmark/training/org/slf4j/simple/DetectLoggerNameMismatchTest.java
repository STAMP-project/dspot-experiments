/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.simple;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests that detecting logger name mismatches works and doesn't cause problems
 * or trigger if disabled.
 * <p>
 * This test can't live inside slf4j-api because the NOP Logger doesn't
 * remember its name.
 *
 * @author Alexander Dorokhine
 * @author Ceki G&uuml;lc&uuml;
 */
public class DetectLoggerNameMismatchTest {
    private static final String MISMATCH_STRING = "Detected logger name mismatch";

    static String NAME_OF_THIS_CLASS = DetectLoggerNameMismatchTest.class.getName();

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private final PrintStream oldErr = System.err;

    /* Pass in the wrong class to the Logger with the check disabled, and make sure there are no errors. */
    @Test
    public void testNoTriggerWithoutProperty() {
        DetectLoggerNameMismatchTest.setTrialEnabled(false);
        Logger logger = LoggerFactory.getLogger(String.class);
        Assert.assertEquals("java.lang.String", logger.getName());
        assertMismatchDetected(false);
    }

    /* Pass in the wrong class to the Logger with the check enabled, and make sure there ARE errors. */
    @Test
    public void testTriggerWithProperty() {
        DetectLoggerNameMismatchTest.setTrialEnabled(true);
        LoggerFactory.getLogger(String.class);
        assertMismatchDetected(true);
    }

    /* Checks the whole error message to ensure all the names show up correctly. */
    @Test
    public void testTriggerWholeMessage() {
        DetectLoggerNameMismatchTest.setTrialEnabled(true);
        LoggerFactory.getLogger(String.class);
        boolean success = String.valueOf(byteArrayOutputStream).contains(((("Detected logger name mismatch. Given name: \"java.lang.String\"; " + "computed name: \"") + (DetectLoggerNameMismatchTest.NAME_OF_THIS_CLASS)) + "\"."));
        Assert.assertTrue(("Actual value of byteArrayOutputStream: " + (String.valueOf(byteArrayOutputStream))), success);
    }

    /* Checks that there are no errors with the check enabled if the class matches. */
    @Test
    public void testPassIfMatch() {
        DetectLoggerNameMismatchTest.setTrialEnabled(true);
        Logger logger = LoggerFactory.getLogger(DetectLoggerNameMismatchTest.class);
        Assert.assertEquals(DetectLoggerNameMismatchTest.class.getName(), logger.getName());
        assertMismatchDetected(false);
    }

    @Test
    public void verifyLoggerDefinedInBaseWithOverridenGetClassMethod() {
        DetectLoggerNameMismatchTest.setTrialEnabled(true);
        Square square = new Square();
        Assert.assertEquals(Square.class.getName(), square.logger.getName());
        assertMismatchDetected(false);
    }
}

