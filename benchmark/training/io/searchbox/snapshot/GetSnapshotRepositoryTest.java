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
public class GetSnapshotRepositoryTest {
    private String repository = "leeseohoo";

    private String repository2 = "kangsungjeon";

    @Test
    public void testRepositorySingleName() {
        GetSnapshotRepository getSnapshotRepository = new GetSnapshotRepository.Builder(repository).build();
        Assert.assertEquals("GET", getSnapshotRepository.getRestMethodName());
        Assert.assertEquals("/_snapshot/leeseohoo", getSnapshotRepository.getURI(UNKNOWN));
    }

    @Test
    public void testRepositoryMultipleNames() {
        GetSnapshotRepository getSnapshotRepository = new GetSnapshotRepository.Builder(repository).addRepository(Arrays.asList(repository, repository2)).build();
        Assert.assertEquals("/_snapshot/leeseohoo,kangsungjeon", getSnapshotRepository.getURI(UNKNOWN));
    }

    @Test
    public void testRepositoryAll() {
        GetSnapshotRepository getSnapshotRepository = new GetSnapshotRepository.Builder().build();
        Assert.assertEquals("/_snapshot/_all", getSnapshotRepository.getURI(UNKNOWN));
    }
}

