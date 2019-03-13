package net.openhft.chronicle.map;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Test;


/**
 * Created by catst01 on 24/10/2018.
 */
public class MissSizedMapsTest {
    @Test(timeout = 60000)
    public void testSmallEntries() throws IOException, URISyntaxException {
        ChronicleMap<String, String> actual = ChronicleMapBuilder.of(String.class, String.class).averageKey("D-6.0149935894066442E18").averageValue("226|16533|4|1|1|testHarness").entries((150 << 10)).createPersistedTo(File.createTempFile("chronicle", "cmap"));
        check(actual);
    }
}

