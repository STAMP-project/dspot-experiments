package io.searchbox.snapshot;


import ElasticsearchVersion.UNKNOWN;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author happyprg(hongsgo@gmail.com)
 */
public class SnapshotStatusTest {
    private String repository = "leeseohoo";

    private String snapshot = "leeseola";

    private String snapshot2 = "kangsungjeon";

    @Test
    public void testSnapshotSingleName() {
        SnapshotStatus snapshotStatus = new SnapshotStatus.Builder(repository).addSnapshot(snapshot).build();
        Assert.assertEquals("GET", snapshotStatus.getRestMethodName());
        Assert.assertEquals("/_snapshot/leeseohoo/leeseola/_status", snapshotStatus.getURI(UNKNOWN));
    }

    @Test
    public void testSnapshotMultipleNames() {
        SnapshotStatus snapshotStatus = new SnapshotStatus.Builder(repository).addSnapshot(Arrays.asList(snapshot, snapshot2)).build();
        Assert.assertEquals("/_snapshot/leeseohoo/leeseola,kangsungjeon/_status", snapshotStatus.getURI(UNKNOWN));
    }
}

