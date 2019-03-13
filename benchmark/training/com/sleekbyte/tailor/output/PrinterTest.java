package com.sleekbyte.tailor.output;


import Rules.LOWER_CAMEL_CASE;
import Severity.ERROR;
import Severity.WARNING;
import com.sleekbyte.tailor.common.Location;
import com.sleekbyte.tailor.common.Severity;
import com.sleekbyte.tailor.format.Formatter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link Printer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PrinterTest {
    private static final String WARNING_MSG = "this is a warning";

    private static final String ERROR_MSG = "this is an error";

    private static final int LINE_NUMBER = 23;

    private static final int COLUMN_NUMBER = 13;

    private File inputFile = new File("abc.swift");

    private Formatter formatter = Mockito.mock(Formatter.class);

    private Printer printer = new Printer(inputFile, Severity.ERROR, formatter);

    private Printer warnPrinter = new Printer(inputFile, Severity.WARNING, formatter);

    @Test
    public void testFormatterDisplayMessage() throws IOException {
        printer.error(LOWER_CAMEL_CASE, PrinterTest.ERROR_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        List<ViolationMessage> violationsMessage = printer.getViolationMessages();
        printer.printAllMessages();
        Mockito.verify(formatter).displayViolationMessages(violationsMessage, inputFile);
    }

    @Test
    public void testFormatterParseErrorMessage() throws IOException {
        printer.setShouldPrintParseErrorMessage(true);
        printer.printAllMessages();
        Mockito.verify(formatter).displayParseErrorMessage(inputFile);
    }

    @Test
    public void testError() throws IOException {
        printer.error(LOWER_CAMEL_CASE, PrinterTest.ERROR_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        ViolationMessage message = printer.getViolationMessages().get(0);
        Assert.assertEquals(message.getSeverity(), ERROR);
        validateViolationMessage(message, LOWER_CAMEL_CASE, PrinterTest.ERROR_MSG, PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER);
        printer.printAllMessages();
    }

    @Test
    public void testWarn() throws IOException {
        printer.warn(LOWER_CAMEL_CASE, PrinterTest.WARNING_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        ViolationMessage message = printer.getViolationMessages().get(0);
        Assert.assertEquals(message.getSeverity(), WARNING);
        validateViolationMessage(message, LOWER_CAMEL_CASE, PrinterTest.WARNING_MSG, PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER);
        printer.printAllMessages();
    }

    @Test
    public void testWarnWithLocationSuccess() throws IOException {
        printer.warn(LOWER_CAMEL_CASE, PrinterTest.WARNING_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        ViolationMessage message = printer.getViolationMessages().get(0);
        Assert.assertEquals(WARNING, message.getSeverity());
        printer.printAllMessages();
    }

    @Test
    public void testErrorWithLocationSuccess() throws IOException {
        printer.error(LOWER_CAMEL_CASE, PrinterTest.ERROR_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        ViolationMessage message = printer.getViolationMessages().get(0);
        Assert.assertEquals(ERROR, message.getSeverity());
        printer.printAllMessages();
    }

    @Test
    public void testErrorWithMaxSeverityWarn() throws IOException {
        warnPrinter.error(LOWER_CAMEL_CASE, PrinterTest.ERROR_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        ViolationMessage message = warnPrinter.getViolationMessages().get(0);
        Assert.assertEquals(WARNING, message.getSeverity());
        warnPrinter.printAllMessages();
    }

    @Test
    public void testWarnWithMaxSeverityWarn() throws IOException {
        warnPrinter.warn(LOWER_CAMEL_CASE, PrinterTest.WARNING_MSG, new Location(PrinterTest.LINE_NUMBER, PrinterTest.COLUMN_NUMBER));
        ViolationMessage message = warnPrinter.getViolationMessages().get(0);
        Assert.assertEquals(WARNING, message.getSeverity());
        warnPrinter.printAllMessages();
    }
}

