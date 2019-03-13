/**
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.list.primitive;


import BooleanLists.immutable;
import com.gs.collections.api.LazyIntIterable;
import com.gs.collections.api.iterator.IntIterator;
import com.gs.collections.impl.bag.mutable.primitive.IntHashBag;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.MutableInteger;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;
import com.gs.collections.impl.test.Verify;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class IntIntervalTest {
    private final IntInterval intInterval = IntInterval.oneTo(3);

    @Test
    public void fromAndToAndBy() {
        IntInterval interval = IntInterval.from(1);
        IntInterval interval2 = interval.to(10);
        IntInterval interval3 = interval2.by(2);
        Verify.assertEqualsAndHashCode(interval, IntInterval.fromTo(1, 1));
        Verify.assertEqualsAndHashCode(interval2, IntInterval.fromTo(1, 10));
        Verify.assertEqualsAndHashCode(interval3, IntInterval.fromToBy(1, 10, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromToBy_throws_step_size_zero() {
        IntInterval.fromToBy(0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void oneToBy_throws_step_size_zero() {
        IntInterval.oneToBy(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroToBy_throws_step_size_zero() {
        IntInterval.zeroToBy(0, 0);
    }

    @Test
    public void equalsAndHashCode() {
        IntInterval interval1 = IntInterval.oneTo(5);
        IntInterval interval2 = IntInterval.oneTo(5);
        IntInterval interval3 = IntInterval.zeroTo(5);
        Verify.assertPostSerializedEqualsAndHashCode(interval1);
        Verify.assertEqualsAndHashCode(interval1, interval2);
        Assert.assertNotEquals(interval1, interval3);
        Assert.assertNotEquals(interval3, interval1);
        Verify.assertEqualsAndHashCode(IntInterval.fromToBy(1, 5, 2), IntInterval.fromToBy(1, 6, 2));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(1, 2, 3), IntInterval.fromTo(1, 3));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith(3, 2, 1), IntInterval.fromTo(3, 1));
        Assert.assertNotEquals(IntArrayList.newListWith(1, 2, 3, 4), IntInterval.fromTo(1, 3));
        Assert.assertNotEquals(IntArrayList.newListWith(1, 2, 4), IntInterval.fromTo(1, 3));
        Assert.assertNotEquals(IntArrayList.newListWith(3, 2, 0), IntInterval.fromTo(3, 1));
        Verify.assertEqualsAndHashCode(IntArrayList.newListWith((-1), (-2), (-3)), IntInterval.fromTo((-1), (-3)));
    }

    @Test
    public void sumIntInterval() {
        Assert.assertEquals(15, ((int) (IntInterval.oneTo(5).sum())));
    }

    @Test
    public void maxIntInterval() {
        int value = IntInterval.oneTo(5).max();
        Assert.assertEquals(5, value);
    }

    @Test
    public void iterator() {
        IntIterator iterator = this.intInterval.intIterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(1L, iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(2L, iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(3L, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator_throws() {
        IntIterator iterator = this.intInterval.intIterator();
        while (iterator.hasNext()) {
            iterator.next();
        } 
        iterator.next();
    }

    @Test
    public void forEach() {
        long[] sum = new long[1];
        this.intInterval.forEach(( each) -> sum[0] += each);
        Assert.assertEquals(6L, sum[0]);
    }

    @Test
    public void injectInto() {
        IntInterval intInterval = IntInterval.oneTo(3);
        MutableInteger result = intInterval.injectInto(new MutableInteger(0), MutableInteger::add);
        Assert.assertEquals(new MutableInteger(6), result);
    }

    @Test
    public void injectIntoWithIndex() {
        IntInterval list1 = this.intInterval;
        IntInterval list2 = IntInterval.oneTo(3);
        MutableInteger result = list1.injectIntoWithIndex(new MutableInteger(0), ( object, value, index) -> object.add((value * (list2.get(index)))));
        Assert.assertEquals(new MutableInteger(14), result);
    }

    @Test
    public void size() {
        Verify.assertSize(3, this.intInterval);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void subList() {
        this.intInterval.subList(0, 1);
    }

    @Test
    public void dotProduct() {
        IntInterval list1 = this.intInterval;
        IntInterval list2 = IntInterval.oneTo(3);
        Assert.assertEquals(14, list1.dotProduct(list2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dotProduct_throwsOnListsOfDifferentSizes() {
        IntInterval list1 = this.intInterval;
        IntInterval list2 = IntInterval.oneTo(4);
        list1.dotProduct(list2);
    }

    @Test
    public void empty() {
        Assert.assertTrue(this.intInterval.notEmpty());
        Verify.assertNotEmpty(this.intInterval);
    }

    @Test
    public void count() {
        Assert.assertEquals(2L, IntInterval.zeroTo(2).count(IntPredicates.greaterThan(0)));
    }

    @Test
    public void anySatisfy() {
        Assert.assertTrue(IntInterval.fromTo((-1), 2).anySatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(IntInterval.oneTo(2).anySatisfy(IntPredicates.equal(0)));
    }

    @Test
    public void allSatisfy() {
        Assert.assertFalse(IntInterval.zeroTo(2).allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertTrue(IntInterval.oneTo(3).allSatisfy(IntPredicates.greaterThan(0)));
    }

    @Test
    public void noneSatisfy() {
        Assert.assertFalse(IntInterval.zeroTo(2).noneSatisfy(IntPredicates.isEven()));
        Assert.assertTrue(IntInterval.evensFromTo(2, 10).noneSatisfy(IntPredicates.isOdd()));
    }

    @Test
    public void select() {
        Verify.assertSize(3, this.intInterval.select(IntPredicates.lessThan(4)));
        Verify.assertSize(2, this.intInterval.select(IntPredicates.lessThan(3)));
    }

    @Test
    public void reject() {
        Verify.assertSize(0, this.intInterval.reject(IntPredicates.lessThan(4)));
        Verify.assertSize(1, this.intInterval.reject(IntPredicates.lessThan(3)));
    }

    @Test
    public void detectIfNone() {
        Assert.assertEquals(1L, this.intInterval.detectIfNone(IntPredicates.lessThan(4), 0));
        Assert.assertEquals(0L, this.intInterval.detectIfNone(IntPredicates.greaterThan(3), 0));
    }

    @Test
    public void collect() {
        Assert.assertEquals(FastList.newListWith(0, 1, 2), this.intInterval.collect(( parameter) -> parameter - 1).toList());
    }

    @Test
    public void lazyCollectPrimitives() {
        Assert.assertEquals(immutable.of(false, true, false), IntInterval.oneTo(3).asLazy().collectBoolean(( e) -> (e % 2) == 0).toList());
        Assert.assertEquals(CharLists.immutable.of(((char) (2)), ((char) (3)), ((char) (4))), IntInterval.oneTo(3).asLazy().collectChar(( e) -> ((char) (e + 1))).toList());
        Assert.assertEquals(ByteLists.immutable.of(((byte) (2)), ((byte) (3)), ((byte) (4))), IntInterval.oneTo(3).asLazy().collectByte(( e) -> ((byte) (e + 1))).toList());
        Assert.assertEquals(ShortLists.immutable.of(((short) (2)), ((short) (3)), ((short) (4))), IntInterval.oneTo(3).asLazy().collectShort(( e) -> ((short) (e + 1))).toList());
        Assert.assertEquals(IntLists.immutable.of(2, 3, 4), IntInterval.oneTo(3).asLazy().collectInt(( e) -> e + 1).toList());
        Assert.assertEquals(FloatLists.immutable.of(2.0F, 3.0F, 4.0F), IntInterval.oneTo(3).asLazy().collectFloat(( e) -> ((float) (e + 1))).toList());
        Assert.assertEquals(LongLists.immutable.of(2L, 3L, 4L), IntInterval.oneTo(3).asLazy().collectLong(( e) -> ((long) (e + 1))).toList());
        Assert.assertEquals(DoubleLists.immutable.of(2.0, 3.0, 4.0), IntInterval.oneTo(3).asLazy().collectDouble(( e) -> ((double) (e + 1))).toList());
    }

    @Test
    public void binarySearch() {
        IntInterval interval1 = IntInterval.oneTo(3);
        Assert.assertEquals((-1), interval1.binarySearch((-1)));
        Assert.assertEquals((-1), interval1.binarySearch(0));
        Assert.assertEquals(0, interval1.binarySearch(1));
        Assert.assertEquals(1, interval1.binarySearch(2));
        Assert.assertEquals(2, interval1.binarySearch(3));
        Assert.assertEquals((-4), interval1.binarySearch(4));
        Assert.assertEquals((-4), interval1.binarySearch(5));
        IntInterval interval2 = IntInterval.fromTo(7, 17).by(3);
        Assert.assertEquals(0, interval2.binarySearch(7));
        Assert.assertEquals(1, interval2.binarySearch(10));
        Assert.assertEquals(2, interval2.binarySearch(13));
        Assert.assertEquals(3, interval2.binarySearch(16));
        Assert.assertEquals((-1), interval2.binarySearch(6));
        Assert.assertEquals((-2), interval2.binarySearch(8));
        Assert.assertEquals((-2), interval2.binarySearch(9));
        Assert.assertEquals((-3), interval2.binarySearch(12));
        Assert.assertEquals((-4), interval2.binarySearch(15));
        Assert.assertEquals((-5), interval2.binarySearch(17));
        Assert.assertEquals((-5), interval2.binarySearch(19));
        IntInterval interval3 = IntInterval.fromTo((-21), (-11)).by(5);
        Assert.assertEquals((-1), interval3.binarySearch((-22)));
        Assert.assertEquals(0, interval3.binarySearch((-21)));
        Assert.assertEquals((-2), interval3.binarySearch((-17)));
        Assert.assertEquals(1, interval3.binarySearch((-16)));
        Assert.assertEquals((-3), interval3.binarySearch((-15)));
        Assert.assertEquals(2, interval3.binarySearch((-11)));
        Assert.assertEquals((-4), interval3.binarySearch((-9)));
        IntInterval interval4 = IntInterval.fromTo(50, 30).by((-10));
        Assert.assertEquals((-1), interval4.binarySearch(60));
        Assert.assertEquals(0, interval4.binarySearch(50));
        Assert.assertEquals((-2), interval4.binarySearch(45));
        Assert.assertEquals(1, interval4.binarySearch(40));
        Assert.assertEquals((-3), interval4.binarySearch(35));
        Assert.assertEquals(2, interval4.binarySearch(30));
        Assert.assertEquals((-4), interval4.binarySearch(25));
        IntInterval interval5 = IntInterval.fromTo((-30), (-50)).by((-10));
        Assert.assertEquals((-1), interval5.binarySearch((-20)));
        Assert.assertEquals(0, interval5.binarySearch((-30)));
        Assert.assertEquals((-2), interval5.binarySearch((-35)));
        Assert.assertEquals(1, interval5.binarySearch((-40)));
        Assert.assertEquals((-3), interval5.binarySearch((-47)));
        Assert.assertEquals(2, interval5.binarySearch((-50)));
        Assert.assertEquals((-4), interval5.binarySearch((-65)));
        IntInterval interval6 = IntInterval.fromTo(27, (-30)).by((-9));
        Assert.assertEquals((-1), interval6.binarySearch(30));
        Assert.assertEquals(0, interval6.binarySearch(27));
        Assert.assertEquals((-2), interval6.binarySearch(20));
        Assert.assertEquals(1, interval6.binarySearch(18));
        Assert.assertEquals((-3), interval6.binarySearch(15));
        Assert.assertEquals(2, interval6.binarySearch(9));
        Assert.assertEquals((-4), interval6.binarySearch(2));
        Assert.assertEquals(3, interval6.binarySearch(0));
        Assert.assertEquals((-5), interval6.binarySearch((-7)));
        Assert.assertEquals(4, interval6.binarySearch((-9)));
        Assert.assertEquals((-6), interval6.binarySearch((-12)));
        Assert.assertEquals(5, interval6.binarySearch((-18)));
        Assert.assertEquals((-7), interval6.binarySearch((-23)));
        Assert.assertEquals(6, interval6.binarySearch((-27)));
        Assert.assertEquals((-8), interval6.binarySearch((-28)));
        Assert.assertEquals((-8), interval6.binarySearch((-30)));
        IntInterval interval7 = IntInterval.fromTo((-1), 1).by(1);
        Assert.assertEquals((-1), interval7.binarySearch((-2)));
        Assert.assertEquals(0, interval7.binarySearch((-1)));
        Assert.assertEquals(1, interval7.binarySearch(0));
        Assert.assertEquals(2, interval7.binarySearch(1));
        Assert.assertEquals((-4), interval7.binarySearch(2));
    }

    @Test
    public void max() {
        Assert.assertEquals(9, IntInterval.oneTo(9).max());
    }

    @Test
    public void min() {
        Assert.assertEquals(1, IntInterval.oneTo(9).min());
    }

    @Test
    public void minIfEmpty() {
        Assert.assertEquals(1, IntInterval.oneTo(9).minIfEmpty(0));
    }

    @Test
    public void maxIfEmpty() {
        Assert.assertEquals(9, IntInterval.oneTo(9).maxIfEmpty(0));
    }

    @Test
    public void sum() {
        Assert.assertEquals(10L, IntInterval.oneTo(4).sum());
    }

    @Test
    public void average() {
        Assert.assertEquals(2.5, IntInterval.oneTo(4).average(), 0.0);
    }

    @Test
    public void median() {
        Assert.assertEquals(2.5, IntInterval.oneTo(4).median(), 0.0);
        Assert.assertEquals(3.0, IntInterval.oneTo(5).median(), 0.0);
    }

    @Test
    public void toArray() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, IntInterval.oneTo(4).toArray());
    }

    @Test
    public void toList() {
        Assert.assertEquals(IntArrayList.newListWith(1, 2, 3, 4), IntInterval.oneTo(4).toList());
    }

    @Test
    public void toSortedList() {
        Assert.assertEquals(IntArrayList.newListWith(1, 2, 3, 4), IntInterval.oneTo(4).toReversed().toSortedList());
    }

    @Test
    public void toSet() {
        Assert.assertEquals(IntHashSet.newSetWith(1, 2, 3, 4), IntInterval.oneTo(4).toSet());
    }

    @Test
    public void toBag() {
        Assert.assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), IntInterval.oneTo(4).toBag());
    }

    @Test
    public void asLazy() {
        Assert.assertEquals(IntInterval.oneTo(5).toSet(), IntInterval.oneTo(5).asLazy().toSet());
        Verify.assertInstanceOf(LazyIntIterable.class, IntInterval.oneTo(5).asLazy());
    }

    @Test
    public void toSortedArray() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, IntInterval.fromTo(4, 1).toSortedArray());
    }

    @Test
    public void testEquals() {
        IntInterval list1 = IntInterval.oneTo(4);
        IntInterval list2 = IntInterval.oneTo(4);
        IntInterval list3 = IntInterval.fromTo(4, 1);
        IntInterval list4 = IntInterval.fromTo(5, 8);
        IntInterval list5 = IntInterval.fromTo(5, 7);
        Verify.assertEqualsAndHashCode(list1, list2);
        Verify.assertPostSerializedEqualsAndHashCode(list1);
        Assert.assertNotEquals(list1, list3);
        Assert.assertNotEquals(list1, list4);
        Assert.assertNotEquals(list1, list5);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(FastList.newListWith(1, 2, 3).hashCode(), IntInterval.oneTo(3).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("[1, 2, 3]", this.intInterval.toString());
    }

    @Test
    public void makeString() {
        Assert.assertEquals("1, 2, 3", this.intInterval.makeString());
        Assert.assertEquals("1/2/3", this.intInterval.makeString("/"));
        Assert.assertEquals(this.intInterval.toString(), this.intInterval.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString() {
        StringBuilder appendable2 = new StringBuilder();
        this.intInterval.appendString(appendable2);
        Assert.assertEquals("1, 2, 3", appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.intInterval.appendString(appendable3, "/");
        Assert.assertEquals("1/2/3", appendable3.toString());
        StringBuilder appendable4 = new StringBuilder();
        this.intInterval.appendString(appendable4, "[", ", ", "]");
        Assert.assertEquals(this.intInterval.toString(), appendable4.toString());
    }

    @Test
    public void toReversed() {
        IntInterval forward = IntInterval.oneTo(5);
        IntInterval reverse = forward.toReversed();
        Assert.assertEquals(IntArrayList.newListWith(5, 4, 3, 2, 1), reverse);
    }

    @Test
    public void evens() {
        IntInterval interval = IntInterval.evensFromTo(0, 10);
        int[] evens = new int[]{ 0, 2, 4, 6, 8, 10 };
        int[] odds = new int[]{ 1, 3, 5, 7, 9 };
        this.assertIntIntervalContainsAll(interval, evens);
        this.denyIntIntervalContainsAny(interval, odds);
        Assert.assertEquals(6, interval.size());
        IntInterval reverseIntInterval = IntInterval.evensFromTo(10, 0);
        this.assertIntIntervalContainsAll(reverseIntInterval, evens);
        this.denyIntIntervalContainsAny(reverseIntInterval, odds);
        Assert.assertEquals(6, reverseIntInterval.size());
        IntInterval negativeIntInterval = IntInterval.evensFromTo((-5), 5);
        int[] negativeEvens = new int[]{ -4, -2, 0, 2, 4 };
        int[] negativeOdds = new int[]{ -3, -1, 1, 3 };
        this.assertIntIntervalContainsAll(negativeIntInterval, negativeEvens);
        this.denyIntIntervalContainsAny(negativeIntInterval, negativeOdds);
        Assert.assertEquals(5, negativeIntInterval.size());
        IntInterval reverseNegativeIntInterval = IntInterval.evensFromTo(5, (-5));
        this.assertIntIntervalContainsAll(reverseNegativeIntInterval, negativeEvens);
        this.denyIntIntervalContainsAny(reverseNegativeIntInterval, negativeOdds);
        Assert.assertEquals(5, reverseNegativeIntInterval.size());
    }

    @Test
    public void odds() {
        IntInterval interval1 = IntInterval.oddsFromTo(0, 10);
        Assert.assertTrue(interval1.containsAll(1, 3, 5, 7, 9));
        Assert.assertTrue(interval1.containsNone(2, 4, 6, 8));
        Assert.assertEquals(5, interval1.size());
        IntInterval reverseIntInterval1 = IntInterval.oddsFromTo(10, 0);
        Assert.assertTrue(reverseIntInterval1.containsAll(1, 3, 5, 7, 9));
        Assert.assertTrue(reverseIntInterval1.containsNone(0, 2, 4, 6, 8, 10));
        Assert.assertEquals(5, reverseIntInterval1.size());
        IntInterval interval2 = IntInterval.oddsFromTo((-5), 5);
        Assert.assertTrue(interval2.containsAll((-5), (-3), (-1), 1, 3, 5));
        Assert.assertTrue(interval2.containsNone((-4), (-2), 0, 2, 4));
        Assert.assertEquals(6, interval2.size());
        IntInterval reverseIntInterval2 = IntInterval.oddsFromTo(5, (-5));
        Assert.assertTrue(reverseIntInterval2.containsAll((-5), (-3), (-1), 1, 3, 5));
        Assert.assertTrue(reverseIntInterval2.containsNone((-4), (-2), 0, 2, 4));
        Assert.assertEquals(6, reverseIntInterval2.size());
    }

    @Test
    public void intervalSize() {
        Assert.assertEquals(100, IntInterval.fromTo(1, 100).size());
        Assert.assertEquals(50, IntInterval.fromToBy(1, 100, 2).size());
        Assert.assertEquals(34, IntInterval.fromToBy(1, 100, 3).size());
        Assert.assertEquals(25, IntInterval.fromToBy(1, 100, 4).size());
        Assert.assertEquals(20, IntInterval.fromToBy(1, 100, 5).size());
        Assert.assertEquals(17, IntInterval.fromToBy(1, 100, 6).size());
        Assert.assertEquals(15, IntInterval.fromToBy(1, 100, 7).size());
        Assert.assertEquals(13, IntInterval.fromToBy(1, 100, 8).size());
        Assert.assertEquals(12, IntInterval.fromToBy(1, 100, 9).size());
        Assert.assertEquals(10, IntInterval.fromToBy(1, 100, 10).size());
        Assert.assertEquals(11, IntInterval.fromTo(0, 10).size());
        Assert.assertEquals(1, IntInterval.zero().size());
        Assert.assertEquals(11, IntInterval.fromTo(0, (-10)).size());
        Assert.assertEquals(3, IntInterval.evensFromTo(2, (-2)).size());
        Assert.assertEquals(2, IntInterval.oddsFromTo(2, (-2)).size());
    }

    @Test
    public void contains() {
        Assert.assertTrue(IntInterval.zero().contains(0));
        Assert.assertTrue(IntInterval.oneTo(5).containsAll(1, 5));
        Assert.assertTrue(IntInterval.oneTo(5).containsNone(6, 7));
        Assert.assertFalse(IntInterval.oneTo(5).containsAll(1, 6));
        Assert.assertFalse(IntInterval.oneTo(5).containsNone(1, 6));
        Assert.assertFalse(IntInterval.oneTo(5).contains(0));
        Assert.assertTrue(IntInterval.fromTo((-1), (-5)).containsAll((-1), (-5)));
        Assert.assertFalse(IntInterval.fromTo((-1), (-5)).contains(1));
        Assert.assertTrue(IntInterval.zero().contains(Integer.valueOf(0)));
        Assert.assertFalse(IntInterval.oneTo(5).contains(Integer.valueOf(0)));
        Assert.assertFalse(IntInterval.fromTo((-1), (-5)).contains(Integer.valueOf(1)));
    }

    @Test
    public void intervalIterator() {
        IntInterval zero = IntInterval.zero();
        IntIterator zeroIterator = zero.intIterator();
        Assert.assertTrue(zeroIterator.hasNext());
        Assert.assertEquals(0, zeroIterator.next());
        Assert.assertFalse(zeroIterator.hasNext());
        IntInterval oneToFive = IntInterval.oneTo(5);
        IntIterator oneToFiveIterator = oneToFive.intIterator();
        for (int i = 1; i < 6; i++) {
            Assert.assertTrue(oneToFiveIterator.hasNext());
            Assert.assertEquals(i, oneToFiveIterator.next());
        }
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (oneToFiveIterator::next)));
        IntInterval threeToNegativeThree = IntInterval.fromTo(3, (-3));
        IntIterator threeToNegativeThreeIterator = threeToNegativeThree.intIterator();
        for (int i = 3; i > (-4); i--) {
            Assert.assertTrue(threeToNegativeThreeIterator.hasNext());
            Assert.assertEquals(i, threeToNegativeThreeIterator.next());
        }
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (threeToNegativeThreeIterator::next)));
    }

    @Test
    public void forEachWithIndex() {
        IntegerSum sum = new IntegerSum(0);
        IntInterval.oneTo(5).forEachWithIndex(( each, index) -> sum.add((each + index)));
        Assert.assertEquals(25, sum.getIntSum());
        IntegerSum zeroSum = new IntegerSum(0);
        IntInterval.fromTo(0, (-4)).forEachWithIndex(( each, index) -> zeroSum.add((each + index)));
        Assert.assertEquals(0, zeroSum.getIntSum());
    }

    @Test
    public void getFirst() {
        Assert.assertEquals(10, IntInterval.fromTo(10, (-10)).by((-5)).getFirst());
        Assert.assertEquals((-10), IntInterval.fromTo((-10), 10).by(5).getFirst());
        Assert.assertEquals(0, IntInterval.zero().getFirst());
    }

    @Test
    public void getLast() {
        Assert.assertEquals((-10), IntInterval.fromTo(10, (-10)).by((-5)).getLast());
        Assert.assertEquals((-10), IntInterval.fromTo(10, (-12)).by((-5)).getLast());
        Assert.assertEquals(10, IntInterval.fromTo((-10), 10).by(5).getLast());
        Assert.assertEquals(10, IntInterval.fromTo((-10), 12).by(5).getLast());
        Assert.assertEquals(0, IntInterval.zero().getLast());
    }

    @Test
    public void indexOf() {
        IntInterval interval = IntInterval.fromTo((-10), 12).by(5);
        Assert.assertEquals(0, interval.indexOf((-10)));
        Assert.assertEquals(1, interval.indexOf((-5)));
        Assert.assertEquals(2, interval.indexOf(0));
        Assert.assertEquals(3, interval.indexOf(5));
        Assert.assertEquals(4, interval.indexOf(10));
        Assert.assertEquals((-1), interval.indexOf((-15)));
        Assert.assertEquals((-1), interval.indexOf((-11)));
        Assert.assertEquals((-1), interval.indexOf((-9)));
        Assert.assertEquals((-1), interval.indexOf(11));
        Assert.assertEquals((-1), interval.indexOf(15));
        IntInterval backwardsIntInterval = IntInterval.fromTo(10, (-12)).by((-5));
        Assert.assertEquals(0, backwardsIntInterval.indexOf(10));
        Assert.assertEquals(1, backwardsIntInterval.indexOf(5));
        Assert.assertEquals(2, backwardsIntInterval.indexOf(0));
        Assert.assertEquals(3, backwardsIntInterval.indexOf((-5)));
        Assert.assertEquals(4, backwardsIntInterval.indexOf((-10)));
        Assert.assertEquals((-1), backwardsIntInterval.indexOf(15));
        Assert.assertEquals((-1), backwardsIntInterval.indexOf(11));
        Assert.assertEquals((-1), backwardsIntInterval.indexOf(9));
        Assert.assertEquals((-1), backwardsIntInterval.indexOf((-11)));
        Assert.assertEquals((-1), backwardsIntInterval.indexOf((-15)));
    }

    @Test
    public void lastIndexOf() {
        IntInterval interval = IntInterval.fromTo((-10), 12).by(5);
        Assert.assertEquals(0, interval.lastIndexOf((-10)));
        Assert.assertEquals(1, interval.lastIndexOf((-5)));
        Assert.assertEquals(2, interval.lastIndexOf(0));
        Assert.assertEquals(3, interval.lastIndexOf(5));
        Assert.assertEquals(4, interval.lastIndexOf(10));
        Assert.assertEquals((-1), interval.lastIndexOf((-15)));
        Assert.assertEquals((-1), interval.lastIndexOf((-11)));
        Assert.assertEquals((-1), interval.lastIndexOf((-9)));
        Assert.assertEquals((-1), interval.lastIndexOf(11));
        Assert.assertEquals((-1), interval.lastIndexOf(15));
        IntInterval backwardsIntInterval = IntInterval.fromTo(10, (-12)).by((-5));
        Assert.assertEquals(0, backwardsIntInterval.lastIndexOf(10));
        Assert.assertEquals(1, backwardsIntInterval.lastIndexOf(5));
        Assert.assertEquals(2, backwardsIntInterval.lastIndexOf(0));
        Assert.assertEquals(3, backwardsIntInterval.lastIndexOf((-5)));
        Assert.assertEquals(4, backwardsIntInterval.lastIndexOf((-10)));
        Assert.assertEquals((-1), backwardsIntInterval.lastIndexOf(15));
        Assert.assertEquals((-1), backwardsIntInterval.lastIndexOf(11));
        Assert.assertEquals((-1), backwardsIntInterval.lastIndexOf(9));
        Assert.assertEquals((-1), backwardsIntInterval.lastIndexOf((-11)));
        Assert.assertEquals((-1), backwardsIntInterval.lastIndexOf((-15)));
    }

    @Test
    public void get() {
        IntInterval interval = IntInterval.fromTo((-10), 12).by(5);
        Assert.assertEquals((-10), interval.get(0));
        Assert.assertEquals((-5), interval.get(1));
        Assert.assertEquals(0, interval.get(2));
        Assert.assertEquals(5, interval.get(3));
        Assert.assertEquals(10, interval.get(4));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> interval.get((-1)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> interval.get(5));
    }

    @Test
    public void containsAll() {
        Assert.assertTrue(IntInterval.fromTo(1, 3).containsAll(1, 2, 3));
        Assert.assertFalse(IntInterval.fromTo(1, 3).containsAll(1, 2, 4));
    }
}

