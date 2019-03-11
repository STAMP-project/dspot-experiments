package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class StateTest {
    @Test
    public void testUriGeneration() {
        State action = new State.Builder().build();
        Assert.assertEquals("/_cluster/state", action.getURI(UNKNOWN));
    }

    @Test
    public void testUriGenerationWithOptionalFields() {
        State action = new State.Builder().withBlocks().withMetadata().build();
        Assert.assertEquals("/_cluster/state/blocks,metadata", action.getURI(UNKNOWN));
    }
}

