package water.parser;


import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;


/**
 * Test suite for orc parser.
 * <p>
 * This test will attempt to parse a bunch of files (orc and csv).  We compare the frames of these files and make
 * sure that they are equivalent.
 * <p>
 * -- Requested by Tomas N.
 */
public class ParseTestORCCSV extends TestUtil {
    private String[] csvFiles = new String[]{ "smalldata/parser/orc/orc2csv/testTimeStamp.csv", "smalldata/parser/orc/orc2csv/testDate1900.csv", "smalldata/parser/orc/orc2csv/testDate2038.csv" };

    private String[] orcFiles = new String[]{ "smalldata/parser/orc/testTimeStamp.orc", "smalldata/parser/orc/TestOrcFile.testDate1900.orc", "smalldata/parser/orc/TestOrcFile.testDate2038.orc" };

    @Test
    public void testSkippedAllColumns() {
        Scope.enter();
        try {
            int[] skipped_columns = new int[]{ 0, 1 };
            Frame f1 = parse_test_file(orcFiles[0], skipped_columns);
            Assert.fail("orc skipped all columns test failed...");
        } catch (Exception ex) {
            System.out.println("Skipped all columns test passed!");
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testParseOrcCsvFiles() {
        Scope.enter();
        DateTimeZone currTimeZn = ParseTime.getTimezone();
        if (currTimeZn.getID().matches("America/Los_Angeles")) {
            try {
                for (int f_index = 0; f_index < (csvFiles.length); f_index++) {
                    Frame csv_frame = parse_test_file(csvFiles[f_index], "\\N", 0, null);
                    Frame orc_frame = parse_test_file(orcFiles[f_index], null, 0, null);
                    Scope.track(csv_frame);
                    Scope.track(orc_frame);
                    Assert.assertTrue(((("Fails: " + (csvFiles[f_index])) + " != ") + (orcFiles[f_index])), TestUtil.isBitIdentical(orc_frame, csv_frame));// both frames should equal

                }
            } finally {
                Scope.exit();
            }
        }
    }
}

