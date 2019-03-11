package water.parser;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;


/**
 * Test suite for orc parser.
 *
 * This test will attempt to perform multi-file parsing of a csv and orc file and compare
 * the frame summary statistics to make sure they are equivalent.
 *
 * -- Requested by Tomas N.
 */
public class ParseTestMultiFileOrc extends TestUtil {
    private double EPSILON = 1.0E-9;

    private long ERRORMARGIN = 1000L;// error margin when compare timestamp.


    int totalFilesTested = 0;

    int numberWrong = 0;

    private String[] csvDirectories = new String[]{ "smalldata/smalldata/synthetic_perfect_separation/" };

    private String[] orcDirectories = new String[]{ "smalldata/parser/orc/synthetic_perfect_separation/" };

    @Test
    public void testParseMultiFileOrcs() {
        for (final boolean disableParallelParse : new boolean[]{ false, true }) {
            final ParseSetupTransformer pst = new ParseSetupTransformer() {
                @Override
                public ParseSetup transformSetup(ParseSetup guessedSetup) {
                    guessedSetup.disableParallelParse = disableParallelParse;
                    return guessedSetup;
                }
            };
            for (int f_index = 0; f_index < (csvDirectories.length); f_index++) {
                Frame csv_frame = parse_test_folder(csvDirectories[f_index], "\\N", 0, null, pst);
                byte[] types = csv_frame.types();
                for (int index = 0; index < (types.length); index++) {
                    if ((types[index]) == 0)
                        types[index] = 4;

                }
                Frame orc_frame = parse_test_folder(orcDirectories[f_index], null, 0, types, pst);
                Assert.assertTrue(TestUtil.isIdenticalUpToRelTolerance(csv_frame, orc_frame, 1.0E-5));
                csv_frame.delete();
                orc_frame.delete();
            }
        }
    }
}

