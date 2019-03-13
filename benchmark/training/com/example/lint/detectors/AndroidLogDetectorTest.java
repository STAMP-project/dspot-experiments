package com.example.lint.detectors;


import com.example.lint.util.AbstractDetectorTest;
import org.junit.Test;


public class AndroidLogDetectorTest extends AbstractDetectorTest {
    @Test
    public void test_detector_not_triggering() throws Exception {
        String file = "AndroidLogNotExistingTestCase.java";
        assertThat(lintFiles(file)).isEqualTo(AbstractDetectorTest.NO_WARNINGS);
    }

    @Test
    public void test_detector_triggering() throws Exception {
        String file = "AndroidLogExistingTestCase.java";
        String expectedOutcome = "AndroidLogExistingTestCase.java:8: Error: Don\'t use Android Log class methods. [InvalidLogStatement]\n" + (("        Log.d(\"tag\", \"log message\");\n" + "        ~~~~~\n") + "1 errors, 0 warnings\n");
        String outcome = lintFiles(file);
        // There's a weird invisible char that makes equalTo fail :/
        assertThat(outcome).isEqualToIgnoringWhitespace(expectedOutcome);
    }
}

