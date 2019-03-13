package zmq.socket.pubsub;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Msg;


public class TrieTest {
    private static final Msg prefix = new Msg(new byte[]{ 1, 2, 3, 4, 5 });

    @Test
    public void testAddRemoveNodeOnTop() {
        Trie trie = new Trie();
        boolean rc = trie.add(null, 0, 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = trie.rm(null, 0, 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveMultiNodesBelowLevel() {
        Trie trie = new Trie();
        boolean rc = trie.add(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] abv = Arrays.copyOf(TrieTest.prefix.data(), TrieTest.prefix.size());
        abv[1] = 0;
        Msg above = new Msg(abv);
        rc = trie.add(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = -1;
        above = new Msg(abv);
        rc = trie.add(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = trie.rm(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 0;
        above = new Msg(abv);
        rc = trie.rm(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = -1;
        above = new Msg(abv);
        rc = trie.rm(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveMultiNodesAboveLevel() {
        Trie trie = new Trie();
        boolean rc = trie.add(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] abv = Arrays.copyOf(TrieTest.prefix.data(), TrieTest.prefix.size());
        abv[1] = 3;
        Msg above = new Msg(abv);
        rc = trie.add(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 33;
        above = new Msg(abv);
        rc = trie.add(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = trie.rm(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 3;
        above = new Msg(abv);
        rc = trie.rm(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 33;
        above = new Msg(abv);
        rc = trie.rm(above, 0, above.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveMultiNodesSameLevel() {
        Trie trie = new Trie();
        boolean rc = trie.add(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = trie.add(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = trie.rm(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = trie.rm(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveOneNode() {
        Trie trie = new Trie();
        boolean rc = trie.add(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = trie.rm(TrieTest.prefix, 0, TrieTest.prefix.size());
        Assert.assertThat(rc, CoreMatchers.is(true));
    }
}

