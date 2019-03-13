package a;


import com.googlecode.d2j.node.DexClassNode;
import com.googlecode.d2j.node.DexFileNode;
import com.googlecode.d2j.smali.BaksmaliDumpOut;
import com.googlecode.d2j.smali.BaksmaliDumper;
import com.googlecode.d2j.smali.Smali;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import org.junit.Test;


public class SmaliTest {
    @Test
    public void test() throws IOException {
        DexFileNode dfn = new DexFileNode();
        try (InputStream is = SmaliTest.class.getResourceAsStream("/a.smali")) {
            Smali.smaliFile("a.smali", is, dfn);
        }
        for (DexClassNode dcn : dfn.clzs) {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(System.out));
            new BaksmaliDumper(true, true).baksmaliClass(dcn, new BaksmaliDumpOut(w));
            w.flush();
        }
    }

    @Test
    public void test2() throws IOException {
        File dir = new File("../dex-translator/src/test/resources/dexes");
        File[] fs = dir.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if ((f.getName().endsWith(".dex")) || (f.getName().endsWith(".apk"))) {
                    System.out.println(f.getName());
                    dotest(f);
                }
            }
        }
    }
}

