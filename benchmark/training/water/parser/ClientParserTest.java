package water.parser;


import java.io.IOException;
import org.junit.Test;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;


public class ClientParserTest extends TestUtil {
    @Test
    public void testBasic() throws IOException {
        Scope.enter();
        String[] files = new String[]{ "smalldata/iris/multiple_iris_files_wheader/iris1.csv", "smalldata/chicago/chicagoCensus.csv", "smalldata/chicago/chicagoCrimes10k.csv.zip" };
        try {
            for (String f : files) {
                Frame frame = TestUtil.parse_test_file(Key.make(("data_1_" + f)), f);
                frame.delete();
            }
        } finally {
            Scope.exit();
        }
    }
}

