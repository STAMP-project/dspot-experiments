package azkaban.project.validator;


import ValidationStatus.ERROR;
import ValidationStatus.WARN;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test adding messages to {@link ValidationReport}
 */
public class ValidationReportTest {
    @Test
    public void testAddWarnLevelInfoMsg() {
        final ValidationReport report = new ValidationReport();
        final String msg = "test warn level info message.";
        report.addWarnLevelInfoMsg(msg);
        for (final String info : report.getInfoMsgs()) {
            Assert.assertEquals("Info message added through addWarnLevelInfoMsg should have level set to WARN", ValidationReport.getInfoMsgLevel(info), WARN);
            Assert.assertEquals("Retrieved info message does not match the original one.", ValidationReport.getInfoMsg(info), msg);
        }
    }

    @Test
    public void testAddErrorLevelInfoMsg() {
        final ValidationReport report = new ValidationReport();
        final String msg = "test error level error message.";
        report.addErrorLevelInfoMsg(msg);
        for (final String info : report.getInfoMsgs()) {
            Assert.assertEquals("Info message added through addErrorLevelInfoMsg should have level set to ERROR", ValidationReport.getInfoMsgLevel(info), ERROR);
            Assert.assertEquals("Retrieved info message does not match the original one.", ValidationReport.getInfoMsg(info), msg);
        }
    }

    @Test
    public void testAddMsgs() {
        final ValidationReport report = new ValidationReport();
        final Set<String> msgs = new HashSet<>();
        msgs.add("test msg 1.");
        msgs.add("test msg 2.");
        report.addWarningMsgs(msgs);
        Assert.assertEquals("Level of severity is not warn.", report.getStatus(), WARN);
        report.addErrorMsgs(msgs);
        Assert.assertEquals("Number of error messages retrieved does not match.", report.getErrorMsgs().size(), 2);
        Assert.assertEquals("Number of warn messages retrieved does not match.", report.getWarningMsgs().size(), 2);
        Assert.assertEquals("Level of severity is not error.", report.getStatus(), ERROR);
    }
}

