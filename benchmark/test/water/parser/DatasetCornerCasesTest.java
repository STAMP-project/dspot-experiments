package water.parser;


import CustomParser.ParserSetup;
import java.util.ArrayList;
import org.junit.Test;
import water.TestUtil;
import water.api.Constants.Extensions;


public class DatasetCornerCasesTest extends TestUtil {
    /* The following tests deal with one line dataset ended by different number of newlines. */
    /* HTWO-87-related bug test

     - only one line dataset - guessing parser should recognize it.
     - this datasets are ended by different number of \n (0x0A):
       - HTWO-87-one-line-dataset-0.csv    - the line is NOT ended by \n
       - HTWO-87-one-line-dataset-1.csv    - the line is ended by 1 \n     (0x0A)
       - HTWO-87-one-line-dataset-2.csv    - the line is ended by 2 \n     (0x0A 0x0A)
       - HTWO-87-one-line-dataset-1dos.csv - the line is ended by \r\n     (0x0D 0x0A)
       - HTWO-87-one-line-dataset-2dos.csv - the line is ended by 2 \r\n   (0x0D 0x0A 0x0D 0x0A)
     */
    @Test
    public void testOneLineDataset() {
        // max number of dataset files
        final String[] tests = new String[]{ "0", "1unix", "2unix", "1dos", "2dos" };
        final String test_dir = "smalldata/test/";
        final String test_prefix = "HTWO-87-one-line-dataset-";
        for (String s : tests) {
            String datasetFilename = ((test_dir + test_prefix) + s) + ".csv";
            String keyname = (test_prefix + s) + (Extensions.HEX);
            testOneLineDataset(datasetFilename, keyname);
        }
    }

    // Tests handling of extra columns showing up late in the parse
    @Test
    public void testExtraCols() {
        Key okey = Key.make("extra.hex");
        Key nfs = TestUtil.load_test_file("smalldata/test/test_parse_extra_cols.csv");
        ArrayList<Key> al = new ArrayList<Key>();
        al.add(nfs);
        // CustomParser.ParserSetup setup = new CustomParser.ParserSetup(CustomParser.ParserType.CSV, (byte)',', 8, true, null, false);
        CustomParser.ParserSetup setup0 = new CustomParser.ParserSetup();
        setup0._header = true;// Force header; file actually has 8 cols of header and 10 cols of data

        CustomParser.ParserSetup setup1 = GuessSetup.guessSetup(al, null, setup0, false)._setup;
        Frame fr = ParseDataset2.parse(okey, new Key[]{ nfs }, setup1, true);
        fr.delete();
    }
}

