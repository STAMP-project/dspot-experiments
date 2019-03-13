package org.cf.simplify;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.cf.smalivm.UnhandledVirtualException;
import org.cf.smalivm.VirtualMachineFactory;
import org.junit.Assert;
import org.junit.Test;


public class LauncherTest {
    @Test
    public void runsWithoutMajorFailureWithSmaliFolder() throws IOException, UnhandledVirtualException {
        Launcher launcher = new Launcher(new VirtualMachineFactory());
        File outFile = File.createTempFile("simplify-test", ".tmp");
        launcher.run(new String[]{ "src/test/resources/obfuscated-example", "-it", "WhiteNoise", "-o", outFile.getAbsolutePath() });
        Assert.assertTrue(outFile.exists());
        Files.delete(outFile.toPath());
    }

    @Test
    public void runsWithoutMajorFailureWithDexFile() throws IOException, UnhandledVirtualException {
        Launcher launcher = new Launcher(new VirtualMachineFactory());
        File outFile = File.createTempFile("simplify-test", ".tmp");
        launcher.run(new String[]{ "src/test/resources/obfuscated-example.zip", "-it", "WhiteNoise", "-o", outFile.getAbsolutePath() });
        Assert.assertTrue(outFile.exists());
        Files.delete(outFile.toPath());
    }
}

