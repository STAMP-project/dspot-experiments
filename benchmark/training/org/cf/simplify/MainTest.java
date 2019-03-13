package org.cf.simplify;


import java.io.IOException;
import org.cf.smalivm.UnhandledVirtualException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MainTest {
    @Test
    public void runsLauncher() throws IOException, UnhandledVirtualException {
        Launcher launcher = Mockito.mock(Launcher.class);
        Main.setLauncher(launcher);
        String[] args = new String[0];
        Main.main(args);
        Mockito.verify(launcher, Mockito.times(1)).run(ArgumentMatchers.eq(args));
    }
}

