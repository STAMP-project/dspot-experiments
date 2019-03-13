package hudson.util;


import LineEndingConversion.EOLType.Unix;
import LineEndingConversion.EOLType.Windows;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


/**
 * Tests for the LineEndingConversion util class.
 *
 * @author David Ruhmann
 */
public class LineEndingConversionTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Issue("JENKINS-7478")
    @Test
    public void validateWindowsEOL() throws Exception {
        rule.assertStringContains(LineEndingConversion.convertEOL("echo A\necho B\recho C", Windows), "echo A\r\necho B\r\necho C");
    }

    @Test
    public void validateUnixEOL() throws Exception {
        rule.assertStringContains(LineEndingConversion.convertEOL("echo A\r\necho B\recho C", Unix), "echo A\necho B\necho C");
    }
}

