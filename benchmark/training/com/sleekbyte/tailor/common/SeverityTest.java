package com.sleekbyte.tailor.common;


import Severity.ERROR;
import Severity.IllegalSeverityException;
import Severity.WARNING;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link Severity}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SeverityTest {
    @Test
    public void testSeverityParserWithValidInputs() throws IllegalSeverityException {
        TestCase.assertEquals(Severity.parseSeverity("error"), ERROR);
        TestCase.assertEquals(Severity.parseSeverity("warning"), WARNING);
        TestCase.assertEquals(Severity.parseSeverity("Error"), ERROR);
        TestCase.assertEquals(Severity.parseSeverity("ERROR"), ERROR);
    }

    @Test(expected = IllegalSeverityException.class)
    public void testSeverityParserWithInvalidInputs() throws IllegalSeverityException {
        Severity.parseSeverity("invalid");
    }

    @Test
    public void testSeverityToString() {
        TestCase.assertEquals(ERROR.toString(), Messages.ERROR);
        TestCase.assertEquals(WARNING.toString(), Messages.WARNING);
    }

    @Test
    public void testSeverityMinimum() {
        TestCase.assertEquals(Severity.min(ERROR, ERROR), ERROR);
        TestCase.assertEquals(Severity.min(ERROR, WARNING), WARNING);
        TestCase.assertEquals(Severity.min(WARNING, ERROR), WARNING);
    }
}

