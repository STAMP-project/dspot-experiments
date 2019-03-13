package io.vavr.collection;


import io.vavr.Serializables;
import io.vavr.Value;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;


public class BitSetTest extends AbstractSortedSetTest {
    private static final int MAX_BIT = 1000000;

    private enum E {

        V1,
        V2,
        V3;}

    // BitSet specific
    @Test
    public void testBitSet1() {
        BitSet<Integer> bs = BitSet.empty();
        bs = bs.add(2);
        assertThat(bs.head()).isEqualTo(2);
        assertThat(bs.length()).isEqualTo(1);
        bs = bs.add(4);
        assertThat(bs.head()).isEqualTo(2);
        assertThat(bs.length()).isEqualTo(2);
        bs = bs.remove(2);
        assertThat(bs.head()).isEqualTo(4);
        assertThat(bs.contains(2)).isFalse();
        assertThat(bs.length()).isEqualTo(1);
        bs = bs.remove(4);
        assertThat(bs.isEmpty()).isTrue();
        assertThat(bs.length()).isEqualTo(0);
    }

    @Test
    public void testBitSet2() {
        BitSet<Integer> bs = BitSet.empty();
        bs = bs.add(2);
        assertThat(bs.head()).isEqualTo(2);
        assertThat(bs.add(2)).isSameAs(bs);
        assertThat(bs.length()).isEqualTo(1);
        bs = bs.add(70);
        assertThat(bs.head()).isEqualTo(2);
        assertThat(bs.add(2)).isSameAs(bs);
        assertThat(bs.add(70)).isSameAs(bs);
        assertThat(bs.length()).isEqualTo(2);
        bs = bs.remove(2);
        assertThat(bs.head()).isEqualTo(70);
        assertThat(bs.contains(2)).isFalse();
        assertThat(bs.length()).isEqualTo(1);
        bs = bs.remove(70);
        assertThat(bs.isEmpty()).isTrue();
        assertThat(bs.length()).isEqualTo(0);
        bs = bs.add(2);
        bs = bs.add(70);
        bs = bs.add(3);
        assertThat(bs.length()).isEqualTo(3);
        bs = bs.add(71);
        assertThat(bs.length()).isEqualTo(4);
        bs = bs.add(701);
        assertThat(bs.length()).isEqualTo(5);
    }

    @Test
    public void testBitSetN() {
        BitSet<Integer> bs = BitSet.empty();
        bs = bs.add(2);
        assertThat(bs.head()).isEqualTo(2);
        bs = bs.add(700);
        assertThat(bs.head()).isEqualTo(2);
        assertThat(bs.add(2)).isSameAs(bs);
        assertThat(bs.add(700)).isSameAs(bs);
        bs = bs.remove(2);
        assertThat(bs.head()).isEqualTo(700);
        assertThat(bs.contains(2)).isFalse();
        bs = bs.remove(700);
        assertThat(bs.isEmpty()).isTrue();
    }

    @Test
    public void testFactories() {
        assertThat(BitSet.of(7).contains(7)).isTrue();// BitSet1, < 64

        assertThat(BitSet.of(77).contains(77)).isTrue();// BitSet2, < 2*64

        assertThat(BitSet.of(777).contains(777)).isTrue();// BitSetN, >= 2*64

        assertThat(BitSet.ofAll(List.of(1).toJavaStream())).isEqualTo(BitSet.of(1));
        assertThat(BitSet.fill(1, () -> 1)).isEqualTo(BitSet.of(1));
        assertThat(BitSet.tabulate(1, ( i) -> 1)).isEqualTo(BitSet.of(1));
    }

    @Test
    public void shouldAllAll() {
        assertThat(BitSet.empty().add(7).addAll(List.of(1, 2))).isEqualTo(BitSet.of(1, 2, 7));
        assertThat(BitSet.empty().add(77).addAll(List.of(1, 2))).isEqualTo(BitSet.of(1, 2, 77));
        assertThat(BitSet.empty().add(777).addAll(List.of(1, 2))).isEqualTo(BitSet.of(1, 2, 777));
    }

    @Test
    public void shouldCollectInts() {
        final Traversable<Integer> actual = Stream.of(1, 2, 3).collect(BitSet.collector());
        assertThat(actual).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void testEnums() {
        BitSet<BitSetTest.E> bs = BitSet.withEnum(BitSetTest.E.class).empty();
        bs = bs.add(BitSetTest.E.V2);
        assert (bs.head()) == (BitSetTest.E.V2);
        bs = bs.add(BitSetTest.E.V3);
        assert (bs.head()) == (BitSetTest.E.V2);
        bs = bs.remove(BitSetTest.E.V2);
        assert (bs.head()) == (BitSetTest.E.V3);
        assert !(bs.contains(BitSetTest.E.V2));
        assert bs.contains(BitSetTest.E.V3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAddNegativeElementToEmpty() {
        BitSet.empty().add((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAddNegativeElementToBitSet2() {
        BitSet.empty().add(77).add((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAddNegativeElementToBitSetN() {
        BitSet.empty().add(777).add((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAddNegativeElements() {
        BitSet.empty().addAll(List.of((-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowContainsNegativeElements() {
        BitSet.empty().contains((-1));
    }

    @Test
    public void shouldSerializeDeserializeNativeBitSet() {
        final Object actual = Serializables.deserialize(Serializables.serialize(BitSet.of(1, 2, 3)));
        final Object expected = BitSet.of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSerializeDeserializeEnumBitSet() {
        final Object actual = Serializables.deserialize(Serializables.serialize(BitSet.withEnum(BitSetTest.E.class).of(BitSetTest.E.V1, BitSetTest.E.V2)));
        final Object expected = BitSet.withEnum(BitSetTest.E.class).of(BitSetTest.E.V1, BitSetTest.E.V2);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldBehaveExactlyLikeAnotherBitSet() {
        for (int i = 0; i < 10; i++) {
            final Random random = getRandom(123456789);
            final java.util.BitSet mutableBitSet = new java.util.BitSet();
            BitSet<Integer> functionalBitSet = BitSet.empty();
            final int size = 5000;
            for (int j = 0; j < size; j++) {
                /* Insert */
                if (((random.nextInt()) % 3) == 0) {
                    assertMinimumsAreEqual(mutableBitSet, functionalBitSet);
                    final int value = random.nextInt(size);
                    mutableBitSet.set(value);
                    functionalBitSet = functionalBitSet.add(value);
                }
                assertMinimumsAreEqual(mutableBitSet, functionalBitSet);
                /* Delete */
                if (((random.nextInt()) % 5) == 0) {
                    if (!(mutableBitSet.isEmpty())) {
                        mutableBitSet.clear(mutableBitSet.nextSetBit(0));
                    }
                    if (!(functionalBitSet.isEmpty())) {
                        functionalBitSet = functionalBitSet.tail();
                    }
                    assertMinimumsAreEqual(mutableBitSet, functionalBitSet);
                }
            }
            final Collection<Integer> oldValues = mutableBitSet.stream().sorted().boxed().collect(Collectors.toList());
            final Collection<Integer> newValues = functionalBitSet.toJavaList();
            assertThat(oldValues).isEqualTo(newValues);
        }
    }

    // -- toSortedSet
    @Override
    @Test
    public void shouldConvertToSortedSetWithoutComparatorOnComparable() {
        final Value<Integer> value = BitSet.of(3, 7, 1, 15, 0);
        final Set<Integer> set = value.toSortedSet();
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(TreeSet.of(3));
        } else {
            assertThat(set).isEqualTo(TreeSet.of(0, 1, 3, 7, 15));
        }
    }

    // -- toPriorityQueue
    @Test
    @Override
    public void shouldConvertToPriorityQueueUsingImplicitComparator() {
        final Value<Integer> value = BitSet.of(1, 3, 2);
        final PriorityQueue<Integer> queue = value.toPriorityQueue();
        if (value.isSingleValued()) {
            assertThat(queue).isEqualTo(PriorityQueue.of(1));
        } else {
            assertThat(queue).isEqualTo(PriorityQueue.of(1, 2, 3));
        }
    }

    @Test
    @Override
    public void shouldConvertToPriorityQueueUsingExplicitComparator() {
        final Comparator<Integer> comparator = Comparator.naturalOrder();
        final Value<Integer> value = BitSet.of(1, 3, 2);
        final PriorityQueue<Integer> queue = value.toPriorityQueue(comparator);
        if (value.isSingleValued()) {
            assertThat(queue).isEqualTo(PriorityQueue.of(comparator, 1));
        } else {
            assertThat(queue).isEqualTo(PriorityQueue.of(comparator, 1, 2, 3));
        }
    }

    // -- head, init, last, tail
    @Test
    public void shouldReturnHeadOfNonEmptyHavingReversedOrder() {
        // BitSet can't have reverse order
    }

    @Test
    public void shouldReturnInitOfNonEmptyHavingReversedOrder() {
        // BitSet can't have reverse order
    }

    @Test
    public void shouldReturnLastOfNonEmptyHavingReversedOrder() {
        // BitSet can't have reverse order
    }

    @Test
    public void shouldReturnTailOfNonEmptyHavingReversedOrder() {
        // BitSet can't have reverse order
    }

    // -- classes
    private static class Mapper<T> implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Map<Integer, T> fromIntMap = new HashMap<>();

        private final Map<T, Integer> toIntMap = new HashMap<>();

        private int nextValue = 0;

        synchronized T fromInt(Integer i) {
            if (i < (nextValue)) {
                return fromIntMap.get(i);
            } else {
                throw new RuntimeException();
            }
        }

        synchronized Integer toInt(T value) {
            Integer i = toIntMap.get(value);
            if (i == null) {
                i = (nextValue)++;
                toIntMap.put(value, i);
                fromIntMap.put(i, value);
            }
            return i;
        }
    }
}

