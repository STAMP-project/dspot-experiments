package io.searchbox.snapshot;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshotRepositoryTest {
    @Test
    public void testRepository() {
        String repository = "leeseohoo";
        DeleteSnapshotRepository deleteSnapshotRepository = new DeleteSnapshotRepository.Builder(repository).build();
        Assert.assertEquals("DELETE", deleteSnapshotRepository.getRestMethodName());
        Assert.assertEquals("/_snapshot/leeseohoo", deleteSnapshotRepository.getURI(UNKNOWN));
    }
}

