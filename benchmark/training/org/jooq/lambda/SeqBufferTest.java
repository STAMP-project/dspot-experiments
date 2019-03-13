package org.jooq.lambda;


import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Linkowski
 */
public class SeqBufferTest {
    private final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

    @Test
    public void testBufferingSpliteratorFollowedByNonBufferingOne() {
        SeqBuffer<Integer> buffer = buffer();
        Assert.assertEquals(list, buffer.seq().toList());// call 1: buffering spliterator + complete consumption

        Assert.assertEquals(list, buffer.seq().toList());// call 2: buffered spliterator + complete consumption

    }

    @Test
    public void testTwoBufferingSpliteratorsConsumedOneAfterAnother() {
        SeqBuffer<Integer> buffer = buffer();
        Seq<Integer> seq1 = buffer.seq();// call 1: buffering spliterator

        Seq<Integer> seq2 = buffer.seq();// call 2: buffering spliterator

        Assert.assertEquals(list, seq1.toList());// complete consumption of 1

        Assert.assertEquals(list, seq2.toList());// complete consumption of 2

        Assert.assertEquals(list, buffer.seq().toList());// call 3: buffered spliterator + complete consumption

    }

    @Test
    public void testTwoBufferingSpliteratorsConsumedInParallel() {
        SeqBuffer<Integer> buffer = buffer();
        List<Tuple2<Integer, Integer>> expected = Seq.zip(list, list).toList();
        // calls 1 & 2: two buffering spliterators and parallel consumption of both
        List<Tuple2<Integer, Integer>> actual = Seq.zip(buffer.seq(), buffer.seq()).toList();
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(list, buffer.seq().toList());// call 3: buffered spliterator + complete consumption

    }

    @Test
    public void testTwoBufferedSpliteratorsConsumedInterchangeably() {
        SeqBuffer<Integer> buffer = buffer();
        // consume some x, then some y, then again some x, etc.
        Spliterator<Integer> x = buffer.seq().spliterator();// call 1: buffering spliterator

        Assert.assertTrue(x.tryAdvance(( i) -> verifyInt(1, i)));
        Assert.assertTrue(x.tryAdvance(( i) -> verifyInt(2, i)));
        Spliterator<Integer> y = buffer.seq().spliterator();// call 2: buffering spliterator

        Assert.assertTrue(y.tryAdvance(( i) -> verifyInt(1, i)));
        Assert.assertTrue(x.tryAdvance(( i) -> verifyInt(3, i)));
        Assert.assertTrue(y.tryAdvance(( i) -> verifyInt(2, i)));
        Assert.assertTrue(y.tryAdvance(( i) -> verifyInt(3, i)));
        Assert.assertTrue(y.tryAdvance(( i) -> verifyInt(4, i)));
        Assert.assertTrue(x.tryAdvance(( i) -> verifyInt(4, i)));
        Assert.assertTrue(x.tryAdvance(( i) -> verifyInt(5, i)));
        Assert.assertTrue(y.tryAdvance(( i) -> verifyInt(5, i)));
        Assert.assertFalse(x.tryAdvance(AssertionError::new));
        Assert.assertFalse(y.tryAdvance(AssertionError::new));
        Assert.assertEquals(list, buffer.seq().toList());// call 3: buffered spliterator + complete consumption

    }

    @Test
    public void testThreadSafetyDuringParallelConsumption() {
        int numThreads = 10;
        int maxVariation = 100;
        int numElements = 10000;
        Supplier<Seq<Integer>> s = () -> Seq.range(0, numElements);
        SeqBuffer<Integer> buffer = SeqBuffer.of(s.get());
        class TestThread extends Thread {
            private final Random random = new Random();

            volatile List<Integer> actualList;

            @Override
            public void run() {
                actualList = buffer.seq().peek(( i) -> randomWait()).toList();
            }

            private void randomWait() {
                for (int i = 0, variation = random.nextInt(maxVariation); i < variation; i++) {
                }
            }
        }
        List<TestThread> testThreads = Seq.generate(() -> new TestThread()).limit(numThreads).toList();
        testThreads.forEach(Thread::start);
        testThreads.forEach(Unchecked.consumer(Thread::join));
        List<Integer> expectedList = s.get().toList();
        for (TestThread testThread : testThreads) {
            Assert.assertEquals(expectedList, testThread.actualList);
        }
    }
}

