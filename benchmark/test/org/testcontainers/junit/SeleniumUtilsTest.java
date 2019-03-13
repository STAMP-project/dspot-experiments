package org.testcontainers.junit;


import java.io.IOException;
import org.junit.Test;


/**
 * Created by Julien LAMY
 */
public class SeleniumUtilsTest {
    @Test
    public void detectSeleniumVersionUnder3() throws IOException {
        checkSeleniumVersionDetected("manifests/MANIFEST-2.45.0.MF", "2.45.0");
    }

    @Test
    public void detectSeleniumVersionUpper3() throws IOException {
        checkSeleniumVersionDetected("manifests/MANIFEST-3.5.2.MF", "3.5.2");
    }
}

