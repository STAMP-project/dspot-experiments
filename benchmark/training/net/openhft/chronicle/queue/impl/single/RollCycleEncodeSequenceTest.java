package net.openhft.chronicle.queue.impl.single;


import Sequence.NOT_FOUND_RETRY;
import java.nio.ByteBuffer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.ref.BinaryTwoLongReference;
import net.openhft.chronicle.queue.RollCycles;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by Jerry Shea on 22/11/17.
 */
@RunWith(Parameterized.class)
public class RollCycleEncodeSequenceTest {
    private final BinaryTwoLongReference longValue;

    private final RollCycleEncodeSequence rollCycleEncodeSequence;

    private final Bytes<ByteBuffer> bytes;

    public RollCycleEncodeSequenceTest(final RollCycles cycle) {
        longValue = new BinaryTwoLongReference();
        bytes = Bytes.elasticByteBuffer();
        longValue.bytesStore(bytes, 0, 16);
        rollCycleEncodeSequence = new RollCycleEncodeSequence(longValue, cycle.defaultIndexCount(), cycle.defaultIndexSpacing());
    }

    @Test
    public void forWritePosition() {
        longValue.setOrderedValue(1);
        longValue.setOrderedValue2(2);
        // a cast to int of this magic number was causing problems
        long forWritePosition = 2147601492L;
        long sequence = rollCycleEncodeSequence.getSequence(forWritePosition);
        Assert.assertEquals(NOT_FOUND_RETRY, sequence);
    }

    @Test
    public void setGet() {
        int sequenceInitial = 11;
        int position = 262788;
        rollCycleEncodeSequence.setSequence(sequenceInitial, position);
        long sequence = rollCycleEncodeSequence.getSequence(position);
        Assert.assertEquals(sequenceInitial, sequence);
    }

    @Test
    public void setGetPositionNeedsMasking() {
        int sequenceInitial = 11;
        long position = 1250999896491L;
        rollCycleEncodeSequence.setSequence(sequenceInitial, position);
        long sequence = rollCycleEncodeSequence.getSequence(position);
        Assert.assertEquals(sequenceInitial, sequence);
    }

    @Test
    public void setGetPositionMinus1() {
        int sequenceInitial = 11;
        long position = (1L << 48) - 1;
        rollCycleEncodeSequence.setSequence(sequenceInitial, position);
        long sequence = rollCycleEncodeSequence.getSequence(position);
        Assert.assertEquals(sequenceInitial, sequence);
    }
}

