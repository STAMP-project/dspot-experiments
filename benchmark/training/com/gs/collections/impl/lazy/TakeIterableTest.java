/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.lazy;


import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.impl.block.procedure.CountProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


public class TakeIterableTest extends AbstractLazyIterableTestCase {
    private TakeIterable<Integer> takeIterable;

    private TakeIterable<Integer> emptyListTakeIterable;

    private TakeIterable<Integer> zeroCountTakeIterable;

    private TakeIterable<Integer> sameCountTakeIterable;

    private TakeIterable<Integer> higherCountTakeIterable;

    @Test(expected = IllegalArgumentException.class)
    public void negative_throws() {
        new TakeIterable(Interval.oneTo(5), (-1));
    }

    @Test
    public void basic() {
        Assert.assertEquals(2, this.takeIterable.size());
        Assert.assertEquals(FastList.newListWith(1, 2), this.takeIterable.toList());
        Assert.assertEquals(0, this.emptyListTakeIterable.size());
        Assert.assertEquals(0, this.zeroCountTakeIterable.size());
        Assert.assertEquals(5, this.higherCountTakeIterable.size());
        Assert.assertEquals(5, this.sameCountTakeIterable.size());
    }

    @Test
    public void forEach() {
        CountProcedure<Integer> cb1 = new CountProcedure();
        this.takeIterable.forEach(cb1);
        Assert.assertEquals(2, cb1.getCount());
        CountProcedure<Integer> cb2 = new CountProcedure();
        this.emptyListTakeIterable.forEach(cb2);
        Assert.assertEquals(0, cb2.getCount());
        CountProcedure<Integer> cb3 = new CountProcedure();
        this.zeroCountTakeIterable.forEach(cb3);
        Assert.assertEquals(0, cb3.getCount());
        CountProcedure<Integer> cb5 = new CountProcedure();
        this.sameCountTakeIterable.forEach(cb5);
        Assert.assertEquals(5, cb5.getCount());
        CountProcedure<Integer> cb6 = new CountProcedure();
        this.higherCountTakeIterable.forEach(cb6);
        Assert.assertEquals(5, cb6.getCount());
    }

    @Test
    public void forEachWithIndex() {
        FastList<Integer> indices = FastList.newList(5);
        ObjectIntProcedure<Integer> indexRecordingProcedure = ( each, index) -> indices.add(index);
        this.takeIterable.forEachWithIndex(indexRecordingProcedure);
        Assert.assertEquals(FastList.newListWith(0, 1), indices);
        indices.clear();
        this.emptyListTakeIterable.forEachWithIndex(indexRecordingProcedure);
        Verify.assertSize(0, indices);
        indices.clear();
        this.zeroCountTakeIterable.forEachWithIndex(indexRecordingProcedure);
        Verify.assertSize(0, indices);
        indices.clear();
        this.sameCountTakeIterable.forEachWithIndex(indexRecordingProcedure);
        Assert.assertEquals(FastList.newListWith(0, 1, 2, 3, 4), indices);
        indices.clear();
        this.higherCountTakeIterable.forEachWithIndex(indexRecordingProcedure);
        Assert.assertEquals(FastList.newListWith(0, 1, 2, 3, 4), indices);
    }

    @Test
    public void forEachWith() {
        Procedure2<Integer, Sum> sumAdditionProcedure = ( each, sum) -> sum.add(each);
        Sum sum1 = new IntegerSum(0);
        this.takeIterable.forEachWith(sumAdditionProcedure, sum1);
        Assert.assertEquals(3, sum1.getValue().intValue());
        Sum sum2 = new IntegerSum(0);
        this.emptyListTakeIterable.forEachWith(sumAdditionProcedure, sum2);
        Assert.assertEquals(0, sum2.getValue().intValue());
        Sum sum3 = new IntegerSum(0);
        this.zeroCountTakeIterable.forEachWith(sumAdditionProcedure, sum3);
        Assert.assertEquals(0, sum3.getValue().intValue());
        Sum sum5 = new IntegerSum(0);
        this.sameCountTakeIterable.forEachWith(sumAdditionProcedure, sum5);
        Assert.assertEquals(15, sum5.getValue().intValue());
        Sum sum6 = new IntegerSum(0);
        this.higherCountTakeIterable.forEachWith(sumAdditionProcedure, sum6);
        Assert.assertEquals(15, sum6.getValue().intValue());
    }

    @Override
    @Test
    public void iterator() {
        Sum sum1 = new IntegerSum(0);
        for (Integer each : this.takeIterable) {
            sum1.add(each);
        }
        Assert.assertEquals(3, sum1.getValue().intValue());
        Sum sum2 = new IntegerSum(0);
        for (Integer each : this.emptyListTakeIterable) {
            sum2.add(each);
        }
        Assert.assertEquals(0, sum2.getValue().intValue());
        Sum sum3 = new IntegerSum(0);
        for (Integer each : this.zeroCountTakeIterable) {
            sum3.add(each);
        }
        Assert.assertEquals(0, sum3.getValue().intValue());
        Sum sum5 = new IntegerSum(0);
        for (Integer each : this.sameCountTakeIterable) {
            sum5.add(each);
        }
        Assert.assertEquals(15, sum5.getValue().intValue());
        Sum sum6 = new IntegerSum(0);
        for (Integer each : this.higherCountTakeIterable) {
            sum6.add(each);
        }
        Assert.assertEquals(15, sum6.getValue().intValue());
    }

    @Override
    @Test
    public void distinct() {
        super.distinct();
        Assert.assertEquals(FastList.newListWith(3, 2, 4, 1), new TakeIterable(FastList.newListWith(3, 2, 2, 4, 1, 3, 1, 5), 7).distinct().toList());
    }
}

