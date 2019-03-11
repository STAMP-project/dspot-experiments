package zmq.socket.pubsub;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Msg;
import zmq.pipe.Pipe;
import zmq.socket.pubsub.Mtrie.IMtrieHandler;


public class MTrieTest {
    private static final class MtrieHandler implements IMtrieHandler {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public void invoke(Pipe pipe, byte[] data, int size, XPub pub) {
            counter.incrementAndGet();
        }
    }

    private Pipe pipe;

    private MTrieTest.MtrieHandler handler;

    private static final Msg prefix = new Msg(new byte[]{ 1, 2, 3, 4, 5 });

    @Test
    public void testAddRemoveNodeOnTop() {
        Mtrie mtrie = new Mtrie();
        boolean rc = mtrie.addOnTop(pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(new Msg(1), pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveMultiNodesBelowLevel() {
        Mtrie mtrie = new Mtrie();
        Pipe other = createPipe();
        Pipe third = createPipe();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] abv = Arrays.copyOf(MTrieTest.prefix.data(), MTrieTest.prefix.size());
        abv[1] = 0;
        Msg above = new Msg(abv);
        rc = mtrie.add(above, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = -1;
        above = new Msg(abv);
        rc = mtrie.add(above, third);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 0;
        above = new Msg(abv);
        rc = mtrie.rm(above, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = -1;
        above = new Msg(abv);
        rc = mtrie.rm(above, third);
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveMultiNodesAboveLevel() {
        Mtrie mtrie = new Mtrie();
        Pipe other = createPipe();
        Pipe third = createPipe();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] abv = Arrays.copyOf(MTrieTest.prefix.data(), MTrieTest.prefix.size());
        abv[1] = 0;
        Msg above = new Msg(abv);
        rc = mtrie.add(above, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 33;
        above = new Msg(abv);
        rc = mtrie.add(above, third);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 0;
        above = new Msg(abv);
        rc = mtrie.rm(above, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 33;
        above = new Msg(abv);
        rc = mtrie.rm(above, third);
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveMultiNodesSameLevel() {
        Mtrie mtrie = new Mtrie();
        Pipe other = createPipe();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.add(MTrieTest.prefix, other);
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = mtrie.rm(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = mtrie.rm(MTrieTest.prefix, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveOneNode() {
        Mtrie mtrie = new Mtrie();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
    }

    @Test
    public void testAddRemoveOneNodeWithFunctionCall() {
        Mtrie mtrie = new Mtrie();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(pipe, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(1));
    }

    @Test
    public void testAddRemoveMultiNodesSameLevelWithFunctionCall() {
        Mtrie mtrie = new Mtrie();
        Pipe other = createPipe();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.add(MTrieTest.prefix, other);
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = mtrie.rm(pipe, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(0));
        rc = mtrie.rm(other, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(1));
    }

    @Test
    public void testAddRemoveNodeOnTopWithFunctionCall() {
        Mtrie mtrie = new Mtrie();
        boolean rc = mtrie.addOnTop(pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(pipe, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(1));
    }

    @Test
    public void testAddRemoveMultiNodesBelowLevelWithFunctionCall() {
        Mtrie mtrie = new Mtrie();
        Pipe other = createPipe();
        Pipe third = createPipe();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] abv = Arrays.copyOf(MTrieTest.prefix.data(), MTrieTest.prefix.size());
        abv[1] = 0;
        Msg above = new Msg(abv);
        rc = mtrie.add(above, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = -1;
        above = new Msg(abv);
        rc = mtrie.add(above, third);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(pipe, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(1));
        abv[1] = 0;
        above = new Msg(abv);
        rc = mtrie.rm(other, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(2));
        abv[1] = -1;
        above = new Msg(abv);
        rc = mtrie.rm(third, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(3));
    }

    @Test
    public void testAddRemoveMultiNodesAboveLevelWithFunctionCall() {
        Mtrie mtrie = new Mtrie();
        Pipe other = createPipe();
        Pipe third = createPipe();
        boolean rc = mtrie.add(MTrieTest.prefix, pipe);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] abv = Arrays.copyOf(MTrieTest.prefix.data(), MTrieTest.prefix.size());
        abv[1] = 3;
        Msg above = new Msg(abv);
        rc = mtrie.add(above, other);
        Assert.assertThat(rc, CoreMatchers.is(true));
        abv[1] = 33;
        above = new Msg(abv);
        rc = mtrie.add(above, third);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = mtrie.rm(pipe, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(1));
        abv[1] = 3;
        above = new Msg(abv);
        rc = mtrie.rm(other, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(2));
        abv[1] = 33;
        above = new Msg(abv);
        rc = mtrie.rm(third, handler, null);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Assert.assertThat(handler.counter.get(), CoreMatchers.is(3));
    }
}

