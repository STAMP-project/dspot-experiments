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
package com.gs.collections.impl.block.comparator;


import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.list.mutable.FastList;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


public class FunctionComparatorTest {
    private static final FunctionComparatorTest.Band VAN_HALEN = new FunctionComparatorTest.Band("Van Halen");

    private static final FunctionComparatorTest.Band BON_JOVI = new FunctionComparatorTest.Band("Bon Jovi");

    private static final FunctionComparatorTest.Band METALLICA = new FunctionComparatorTest.Band("Metallica");

    private static final FunctionComparatorTest.Band SCORPIONS = new FunctionComparatorTest.Band("Scorpions");

    private static final FunctionComparatorTest.Band ACDC = new FunctionComparatorTest.Band("AC/DC");

    private static final FunctionComparatorTest.Band ZZTOP = new FunctionComparatorTest.Band("ZZ Top");

    @Test
    public void comparator() {
        FunctionComparator<FunctionComparatorTest.Band, String> comparator = new FunctionComparator(FunctionComparatorTest.Band.TO_NAME, String::compareTo);
        Assert.assertEquals(comparator.compare(FunctionComparatorTest.ACDC, FunctionComparatorTest.ZZTOP), FunctionComparatorTest.ACDC.getName().compareTo(FunctionComparatorTest.ZZTOP.getName()));
        Assert.assertEquals(comparator.compare(FunctionComparatorTest.ZZTOP, FunctionComparatorTest.ACDC), FunctionComparatorTest.ZZTOP.getName().compareTo(FunctionComparatorTest.ACDC.getName()));
    }

    @Test
    public void functionComparatorBuiltTheHardWay() {
        Comparator<FunctionComparatorTest.Band> byName = ( bandA, bandB) -> FunctionComparatorTest.Band.TO_NAME.valueOf(bandA).compareTo(FunctionComparatorTest.Band.TO_NAME.valueOf(bandB));
        MutableList<FunctionComparatorTest.Band> sortedList = this.createTestList().sortThis(byName);
        Assert.assertEquals(FastList.newListWith(FunctionComparatorTest.BON_JOVI, FunctionComparatorTest.METALLICA, FunctionComparatorTest.SCORPIONS, FunctionComparatorTest.VAN_HALEN), sortedList);
    }

    @Test
    public void functionComparatorBuiltTheEasyWay() {
        Comparator<FunctionComparatorTest.Band> byName = Comparators.byFunction(FunctionComparatorTest.Band.TO_NAME, String::compareTo);
        MutableList<FunctionComparatorTest.Band> sortedList = this.createTestList().sortThis(byName);
        Assert.assertEquals(FastList.newListWith(FunctionComparatorTest.BON_JOVI, FunctionComparatorTest.METALLICA, FunctionComparatorTest.SCORPIONS, FunctionComparatorTest.VAN_HALEN), sortedList);
    }

    private static final class Band {
        public static final Function<FunctionComparatorTest.Band, String> TO_NAME = new Function<FunctionComparatorTest.Band, String>() {
            public String valueOf(FunctionComparatorTest.Band band) {
                return band.name;
            }
        };

        private final String name;

        private Band(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean equals(Object other) {
            return ((this) == other) || ((other instanceof FunctionComparatorTest.Band) && (this.name.equals(((FunctionComparatorTest.Band) (other)).name)));
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}

