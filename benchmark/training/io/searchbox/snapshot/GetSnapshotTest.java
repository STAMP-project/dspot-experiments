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
public class GetSnapshotTest {
    private String repository = "kangsungjeon";

    private String snapshot = "leeseohoo";

    private String snapshot2 = "kangsungjeon";

    @Test
    public void testSnapshotMultipleNames() {
        GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).addSnapshot(Arrays.asList(snapshot, snapshot2)).build();
        Assert.assertEquals("/_snapshot/kangsungjeon/leeseohoo,kangsungjeon", getSnapshotRepository.getURI(UNKNOWN));
    }

    @Test
    public void testSnapshotAll() {
        GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).build();
        Assert.assertEquals("/_snapshot/kangsungjeon/_all", getSnapshotRepository.getURI(UNKNOWN));
    }
}

