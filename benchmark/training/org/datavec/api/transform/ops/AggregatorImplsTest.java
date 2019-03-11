/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.transform.ops;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Created by huitseeker on 5/14/17.
 */
public class AggregatorImplsTest {
    private List<Integer> intList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private List<String> stringList = new ArrayList<>(Arrays.asList("arakoa", "abracadabra", "blast", "acceptance"));

    @Test
    public void aggregableFirstTest() {
        AggregatorImpls.AggregableFirst<Integer> first = new AggregatorImpls.AggregableFirst<>();
        for (int i = 0; i < (intList.size()); i++) {
            first.accept(intList.get(i));
        }
        Assert.assertEquals(1, first.get().toInt());
        AggregatorImpls.AggregableFirst<String> firstS = new AggregatorImpls.AggregableFirst<>();
        for (int i = 0; i < (stringList.size()); i++) {
            firstS.accept(stringList.get(i));
        }
        Assert.assertTrue(firstS.get().toString().equals("arakoa"));
        AggregatorImpls.AggregableFirst<Integer> reverse = new AggregatorImpls.AggregableFirst<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        first.combine(reverse);
        Assert.assertEquals(1, first.get().toInt());
    }

    @Test
    public void aggregableLastTest() {
        AggregatorImpls.AggregableLast<Integer> last = new AggregatorImpls.AggregableLast<>();
        for (int i = 0; i < (intList.size()); i++) {
            last.accept(intList.get(i));
        }
        Assert.assertEquals(9, last.get().toInt());
        AggregatorImpls.AggregableLast<String> lastS = new AggregatorImpls.AggregableLast<>();
        for (int i = 0; i < (stringList.size()); i++) {
            lastS.accept(stringList.get(i));
        }
        Assert.assertTrue(lastS.get().toString().equals("acceptance"));
        AggregatorImpls.AggregableLast<Integer> reverse = new AggregatorImpls.AggregableLast<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        last.combine(reverse);
        Assert.assertEquals(1, last.get().toInt());
    }

    @Test
    public void aggregableCountTest() {
        AggregatorImpls.AggregableCount<Integer> cnt = new AggregatorImpls.AggregableCount<>();
        for (int i = 0; i < (intList.size()); i++) {
            cnt.accept(intList.get(i));
        }
        Assert.assertEquals(9, cnt.get().toInt());
        AggregatorImpls.AggregableCount<String> lastS = new AggregatorImpls.AggregableCount<>();
        for (int i = 0; i < (stringList.size()); i++) {
            lastS.accept(stringList.get(i));
        }
        Assert.assertEquals(4, lastS.get().toInt());
        AggregatorImpls.AggregableCount<Integer> reverse = new AggregatorImpls.AggregableCount<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        cnt.combine(reverse);
        Assert.assertEquals(18, cnt.get().toInt());
    }

    @Test
    public void aggregableMaxTest() {
        AggregatorImpls.AggregableMax<Integer> mx = new AggregatorImpls.AggregableMax<>();
        for (int i = 0; i < (intList.size()); i++) {
            mx.accept(intList.get(i));
        }
        Assert.assertEquals(9, mx.get().toInt());
        AggregatorImpls.AggregableMax<Integer> reverse = new AggregatorImpls.AggregableMax<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        mx.combine(reverse);
        Assert.assertEquals(9, mx.get().toInt());
    }

    @Test
    public void aggregableRangeTest() {
        AggregatorImpls.AggregableRange<Integer> mx = new AggregatorImpls.AggregableRange<>();
        for (int i = 0; i < (intList.size()); i++) {
            mx.accept(intList.get(i));
        }
        Assert.assertEquals(8, mx.get().toInt());
        AggregatorImpls.AggregableRange<Integer> reverse = new AggregatorImpls.AggregableRange<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(((intList.get((((intList.size()) - i) - 1))) + 9));
        }
        mx.combine(reverse);
        Assert.assertEquals(17, mx.get().toInt());
    }

    @Test
    public void aggregableMinTest() {
        AggregatorImpls.AggregableMin<Integer> mn = new AggregatorImpls.AggregableMin<>();
        for (int i = 0; i < (intList.size()); i++) {
            mn.accept(intList.get(i));
        }
        Assert.assertEquals(1, mn.get().toInt());
        AggregatorImpls.AggregableMin<Integer> reverse = new AggregatorImpls.AggregableMin<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        mn.combine(reverse);
        Assert.assertEquals(1, mn.get().toInt());
    }

    @Test
    public void aggregableSumTest() {
        AggregatorImpls.AggregableSum<Integer> sm = new AggregatorImpls.AggregableSum<>();
        for (int i = 0; i < (intList.size()); i++) {
            sm.accept(intList.get(i));
        }
        Assert.assertEquals(45, sm.get().toInt());
        AggregatorImpls.AggregableSum<Integer> reverse = new AggregatorImpls.AggregableSum<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        sm.combine(reverse);
        Assert.assertEquals(90, sm.get().toInt());
    }

    @Test
    public void aggregableMeanTest() {
        AggregatorImpls.AggregableMean<Integer> mn = new AggregatorImpls.AggregableMean<>();
        for (int i = 0; i < (intList.size()); i++) {
            mn.accept(intList.get(i));
        }
        Assert.assertEquals(9L, ((long) (mn.getCount())));
        Assert.assertEquals(5.0, mn.get().toDouble(), 0.001);
        AggregatorImpls.AggregableMean<Integer> reverse = new AggregatorImpls.AggregableMean<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        mn.combine(reverse);
        Assert.assertEquals(18L, ((long) (mn.getCount())));
        Assert.assertEquals(5.0, mn.get().toDouble(), 0.001);
    }

    @Test
    public void aggregableStdDevTest() {
        AggregatorImpls.AggregableStdDev<Integer> sd = new AggregatorImpls.AggregableStdDev<>();
        for (int i = 0; i < (intList.size()); i++) {
            sd.accept(intList.get(i));
        }
        Assert.assertTrue(((Math.abs(((sd.get().toDouble()) - 2.7386))) < 1.0E-4));
        AggregatorImpls.AggregableStdDev<Integer> reverse = new AggregatorImpls.AggregableStdDev<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        sd.combine(reverse);
        Assert.assertTrue(("" + (sd.get().toDouble())), ((Math.abs(((sd.get().toDouble()) - 1.8787))) < 1.0E-4));
    }

    @Test
    public void aggregableVariance() {
        AggregatorImpls.AggregableVariance<Integer> sd = new AggregatorImpls.AggregableVariance<>();
        for (int i = 0; i < (intList.size()); i++) {
            sd.accept(intList.get(i));
        }
        Assert.assertTrue(((Math.abs(((sd.get().toDouble()) - (60.0 / 8)))) < 1.0E-4));
        AggregatorImpls.AggregableVariance<Integer> reverse = new AggregatorImpls.AggregableVariance<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        sd.combine(reverse);
        Assert.assertTrue(("" + (sd.get().toDouble())), ((Math.abs(((sd.get().toDouble()) - 3.5294))) < 1.0E-4));
    }

    @Test
    public void aggregableUncorrectedStdDevTest() {
        AggregatorImpls.AggregableUncorrectedStdDev<Integer> sd = new AggregatorImpls.AggregableUncorrectedStdDev<>();
        for (int i = 0; i < (intList.size()); i++) {
            sd.accept(intList.get(i));
        }
        Assert.assertTrue(((Math.abs(((sd.get().toDouble()) - 2.582))) < 1.0E-4));
        AggregatorImpls.AggregableUncorrectedStdDev<Integer> reverse = new AggregatorImpls.AggregableUncorrectedStdDev<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        sd.combine(reverse);
        Assert.assertTrue(("" + (sd.get().toDouble())), ((Math.abs(((sd.get().toDouble()) - 1.8257))) < 1.0E-4));
    }

    @Test
    public void aggregablePopulationVariance() {
        AggregatorImpls.AggregablePopulationVariance<Integer> sd = new AggregatorImpls.AggregablePopulationVariance<>();
        for (int i = 0; i < (intList.size()); i++) {
            sd.accept(intList.get(i));
        }
        Assert.assertTrue(((Math.abs(((sd.get().toDouble()) - (60.0 / 9)))) < 1.0E-4));
        AggregatorImpls.AggregablePopulationVariance<Integer> reverse = new AggregatorImpls.AggregablePopulationVariance<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        sd.combine(reverse);
        Assert.assertTrue(("" + (sd.get().toDouble())), ((Math.abs(((sd.get().toDouble()) - (30.0 / 9)))) < 1.0E-4));
    }

    @Test
    public void aggregableCountUniqueTest() {
        // at this low range, it's linear counting
        AggregatorImpls.AggregableCountUnique<Integer> cu = new AggregatorImpls.AggregableCountUnique<>();
        for (int i = 0; i < (intList.size()); i++) {
            cu.accept(intList.get(i));
        }
        Assert.assertEquals(9, cu.get().toInt());
        cu.accept(1);
        Assert.assertEquals(9, cu.get().toInt());
        AggregatorImpls.AggregableCountUnique<Integer> reverse = new AggregatorImpls.AggregableCountUnique<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        cu.combine(reverse);
        Assert.assertEquals(9, cu.get().toInt());
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void incompatibleAggregatorTest() {
        AggregatorImpls.AggregableSum<Integer> sm = new AggregatorImpls.AggregableSum<>();
        for (int i = 0; i < (intList.size()); i++) {
            sm.accept(intList.get(i));
        }
        Assert.assertEquals(45, sm.get().toInt());
        AggregatorImpls.AggregableMean<Integer> reverse = new AggregatorImpls.AggregableMean<>();
        for (int i = 0; i < (intList.size()); i++) {
            reverse.accept(intList.get((((intList.size()) - i) - 1)));
        }
        exception.expect(UnsupportedOperationException.class);
        sm.combine(reverse);
        Assert.assertEquals(45, sm.get().toInt());
    }
}

