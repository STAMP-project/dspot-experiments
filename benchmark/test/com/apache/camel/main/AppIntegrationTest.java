package com.apache.camel.main;


import com.baeldung.camel.main.App;
import junit.framework.TestCase;
import org.junit.Test;


public class AppIntegrationTest extends TestCase {
    private static final String FILE_NAME = "file.txt";

    private static final String SAMPLE_INPUT_DIR = "src/test/data/sampleInputFile/";

    private static final String TEST_INPUT_DIR = "src/test/data/input/";

    private static final String UPPERCASE_OUTPUT_DIR = "src/test/data/outputUpperCase/";

    private static final String LOWERCASE_OUTPUT_DIR = "src/test/data/outputLowerCase/";

    @Test
    public final void testMain() throws Exception {
        App.main(null);
        String inputFileContent = readFileContent(((AppIntegrationTest.SAMPLE_INPUT_DIR) + (AppIntegrationTest.FILE_NAME)));
        String outputUpperCase = readFileContent(((AppIntegrationTest.UPPERCASE_OUTPUT_DIR) + (AppIntegrationTest.FILE_NAME)));
        String outputLowerCase = readFileContent(((AppIntegrationTest.LOWERCASE_OUTPUT_DIR) + (AppIntegrationTest.FILE_NAME)));
        System.out.println((("Input File content = [" + inputFileContent) + "]"));
        System.out.println((("UpperCaseOutput file content = [" + outputUpperCase) + "]"));
        System.out.println((("LowerCaseOtput file content = [" + outputLowerCase) + "]"));
        TestCase.assertEquals(inputFileContent.toUpperCase(), outputUpperCase);
        TestCase.assertEquals(inputFileContent.toLowerCase(), outputLowerCase);
    }
}

