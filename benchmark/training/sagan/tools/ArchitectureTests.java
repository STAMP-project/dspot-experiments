package sagan.tools;


import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ArchitectureTests {
    @Test
    public void hasADisplayNameForCocoa32Bit() throws Exception {
        DownloadLink link = new DownloadLink("", "", "", "", "32");
        Architecture architecture = new Architecture("Mac OS X (Cocoa)", Arrays.asList(link));
        MatcherAssert.assertThat(architecture.getDisplayName(), Matchers.is("COCOA, 32BIT"));
    }

    @Test
    public void hasADisplayNameForCocoa64Bit() throws Exception {
        DownloadLink link = new DownloadLink("", "", "", "", "64");
        Architecture architecture = new Architecture("Mac OS X (Cocoa, 64Bit)", Arrays.asList(link));
        MatcherAssert.assertThat(architecture.getDisplayName(), Matchers.is("COCOA, 64BIT"));
    }

    @Test
    public void hasADisplayNameForGTK32Bit() throws Exception {
        DownloadLink link = new DownloadLink("", "", "", "", "32");
        Architecture architecture = new Architecture("Linux (GTK)", Arrays.asList(link));
        MatcherAssert.assertThat(architecture.getDisplayName(), Matchers.is("GTK, 32BIT"));
    }

    @Test
    public void hasADisplayNameForGTK64Bit() throws Exception {
        DownloadLink link = new DownloadLink("", "", "", "", "64");
        Architecture architecture = new Architecture("Linux (GTK, 64Bit)", Arrays.asList(link));
        MatcherAssert.assertThat(architecture.getDisplayName(), Matchers.is("GTK, 64BIT"));
    }

    @Test
    public void hasADisplayNameForWIN32Bit() throws Exception {
        DownloadLink link = new DownloadLink("", "", "", "", "32");
        Architecture architecture = new Architecture("Windows", Arrays.asList(link));
        MatcherAssert.assertThat(architecture.getDisplayName(), Matchers.is("WIN, 32BIT"));
    }

    @Test
    public void hasADisplayNameForWIN64Bit() throws Exception {
        DownloadLink link = new DownloadLink("", "", "", "", "64");
        Architecture architecture = new Architecture("Windows (64bit)", Arrays.asList(link));
        MatcherAssert.assertThat(architecture.getDisplayName(), Matchers.is("WIN, 64BIT"));
    }
}

