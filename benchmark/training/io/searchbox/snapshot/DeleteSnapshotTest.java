package io.searchbox.snapshot;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshotTest {
    private String repository = "leeseohoo";

    private String snapshot = "leeseola";

    @Test
    public void testSnapshot() {
        DeleteSnapshot deleteSnapshot = new DeleteSnapshot.Builder(repository, snapshot).build();
        Assert.assertEquals("DELETE", deleteSnapshot.getRestMethodName());
        Assert.assertEquals("/_snapshot/leeseohoo/leeseola", deleteSnapshot.getURI(UNKNOWN));
    }
}

