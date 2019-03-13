package a;


import java.io.File;
import org.junit.Test;


public class BaksmaliTest {
    @Test
    public void t() throws Exception {
        File dir = new File("../dex-translator/src/test/resources/dexes");
        File[] fs = dir.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if ((f.getName().endsWith(".dex")) || (f.getName().endsWith(".apk"))) {
                    dotest(f.toPath());
                }
            }
        }
    }
}

