package water.parser;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;


public class ClientParserZipGzipTest extends TestUtil {
    @Test
    public void testBasic() throws IOException {
        // airlines_small_csv.zip is a zip file that contains 4 csv files
        Frame one_zip_directory = TestUtil.parse_test_file("smalldata/parser/hexdev_497/airlines_small_csv.zip");
        // airlines_small_csv is a folder that contains the 4 csv files not compressed.
        Frame one_csv_directory = TestUtil.parse_test_file("smalldata/parser/hexdev_497/airlines_small_csv/all_airlines.csv");
        // H2O frames built from both sources should be equal.  Verify that here.
        Assert.assertTrue(TestUtil.isBitIdentical(one_zip_directory, one_csv_directory));
        if (one_zip_directory != null)
            one_zip_directory.delete();

        if (one_csv_directory != null)
            one_csv_directory.delete();

    }
}

