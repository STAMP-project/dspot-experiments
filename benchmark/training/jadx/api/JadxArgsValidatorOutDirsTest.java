package jadx.api;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static JadxArgs.DEFAULT_RES_DIR;
import static JadxArgs.DEFAULT_SRC_DIR;


public class JadxArgsValidatorOutDirsTest {
    private static final Logger LOG = LoggerFactory.getLogger(JadxArgsValidatorOutDirsTest.class);

    public JadxArgs args;

    @Test
    public void checkAllSet() {
        setOutDirs("r", "s", "r");
        checkOutDirs("r", "s", "r");
    }

    @Test
    public void checkRootOnly() {
        setOutDirs("out", null, null);
        checkOutDirs("out", ("out/" + (DEFAULT_SRC_DIR)), ("out/" + (DEFAULT_RES_DIR)));
    }

    @Test
    public void checkSrcOnly() {
        setOutDirs(null, "src", null);
        checkOutDirs("src", "src", ("src/" + (DEFAULT_RES_DIR)));
    }

    @Test
    public void checkResOnly() {
        setOutDirs(null, null, "res");
        checkOutDirs("res", ("res/" + (DEFAULT_SRC_DIR)), "res");
    }

    @Test
    public void checkNone() {
        setOutDirs(null, null, null);
        String inputFileBase = args.getInputFiles().get(0).getName().replace(".apk", "");
        checkOutDirs(inputFileBase, ((inputFileBase + "/") + (DEFAULT_SRC_DIR)), ((inputFileBase + "/") + (DEFAULT_RES_DIR)));
    }
}

