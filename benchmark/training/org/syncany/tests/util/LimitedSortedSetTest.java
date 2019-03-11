/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.tests.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.util.LimitedSortedSet;


public class LimitedSortedSetTest {
    @Test
    public void testLimitedSortedSet1() {
        LimitedSortedSet<Integer> lss = new LimitedSortedSet<Integer>(3);
        lss.add(5);
        Assert.assertEquals("[5]", lss.toString());
        lss.add(4);
        Assert.assertEquals("[4, 5]", lss.toString());
        lss.add(3);
        Assert.assertEquals("[3, 4, 5]", lss.toString());
        lss.add(2);
        Assert.assertEquals("[2, 3, 4]", lss.toString());
        lss.add(1);
        Assert.assertEquals("[1, 2, 3]", lss.toString());
        lss.add(6);
        Assert.assertEquals("[1, 2, 3]", lss.toString());
        lss.add(0);
        Assert.assertEquals("[0, 1, 2]", lss.toString());
        lss.remove(0);
        Assert.assertEquals("[1, 2]", lss.toString());
        lss.remove(6);
        Assert.assertEquals("[1, 2]", lss.toString());
        lss.addAll(Arrays.asList(new Integer[]{ 8, 7, 1, -1 }));
        Assert.assertEquals("[-1, 1, 2]", lss.toString());
    }
}

