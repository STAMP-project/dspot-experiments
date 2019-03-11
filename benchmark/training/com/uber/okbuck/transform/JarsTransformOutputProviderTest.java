package com.uber.okbuck.transform;


import Format.JAR;
import java.io.File;
import org.junit.Test;


public class JarsTransformOutputProviderTest {
    private static final String NAME = "buck-out/bin/app/java_classes_preprocess_in_bin_prodDebug/buck-out/gen/.okbuck/workspace/" + "__app.rxscreenshotdetector-release.aar#aar_prebuilt_jar__/classes.jar";

    private static final String INPUT = "buck-out/bin/app/java_classes_preprocess_in_bin_prodDebug/";

    private static final String OUTPUT = "buck-out/bin/app/java_classes_preprocess_out_bin_prodDebug/";

    private static final String EXPECTED_OUTPUT_JAR = "buck-out/bin/app/java_classes_preprocess_out_bin_prodDebug/buck-out/gen/.okbuck/workspace/" + "__app.rxscreenshotdetector-release.aar#aar_prebuilt_jar__/classes.jar";

    private File inputJarFile;

    private File outputJarFile;

    private File inputFolder;

    private File outputFolder;

    private JarsTransformOutputProvider provider;

    @Test
    public void getContentLocation() throws Exception {
        File output = provider.getContentLocation(inputJarFile.getAbsolutePath(), null, null, JAR);
        assertThat(output.getAbsolutePath()).isEqualTo(outputJarFile.getAbsolutePath());
    }
}

