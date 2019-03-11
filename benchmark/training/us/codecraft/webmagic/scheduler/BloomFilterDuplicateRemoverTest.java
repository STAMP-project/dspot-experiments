package us.codecraft.webmagic.scheduler;


import org.junit.Test;
import us.codecraft.webmagic.Request;


/**
 *
 *
 * @author code4crafer@gmail.com
 */
public class BloomFilterDuplicateRemoverTest {
    @Test
    public void testRemove() throws Exception {
        BloomFilterDuplicateRemover bloomFilterDuplicateRemover = new BloomFilterDuplicateRemover(10);
        boolean isDuplicate = bloomFilterDuplicateRemover.isDuplicate(new Request("a"), null);
        assertThat(isDuplicate).isFalse();
        isDuplicate = bloomFilterDuplicateRemover.isDuplicate(new Request("a"), null);
        assertThat(isDuplicate).isTrue();
        isDuplicate = bloomFilterDuplicateRemover.isDuplicate(new Request("b"), null);
        assertThat(isDuplicate).isFalse();
        isDuplicate = bloomFilterDuplicateRemover.isDuplicate(new Request("b"), null);
        assertThat(isDuplicate).isTrue();
    }
}

