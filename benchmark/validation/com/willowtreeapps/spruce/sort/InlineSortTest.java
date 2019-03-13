/**
 * Spruce
 *
 *     Copyright (c) 2017 WillowTree, Inc.
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *     The above copyright notice and this permission notice shall be included in
 *     all copies or substantial portions of the Software.
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *     THE SOFTWARE.
 */
package com.willowtreeapps.spruce.sort;


import CorneredSort.Corner;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class InlineSortTest {
    private ViewGroup mockParent;

    private List<View> mockChildren;

    @Test
    public void test_positive_inter_object_delay() {
        List<SpruceTimedView> resultViews = /* interObjectDelay= */
        /* reversed= */
        new InlineSort(1, false, Corner.BOTTOM_RIGHT).getViewListWithTimeOffsets(mockParent, mockChildren);
        Assert.assertEquals(0, resultViews.get(0).getTimeOffset());
        Assert.assertEquals(1, resultViews.get(1).getTimeOffset());
        Assert.assertEquals(2, resultViews.get(2).getTimeOffset());
    }

    @Test
    public void test_inter_object_delay_of_zero() {
        List<SpruceTimedView> resultViews = /* interObjectDelay= */
        /* reversed= */
        new InlineSort(0, false, Corner.BOTTOM_RIGHT).getViewListWithTimeOffsets(mockParent, mockChildren);
        Assert.assertEquals(0, resultViews.get(0).getTimeOffset());
        Assert.assertEquals(0, resultViews.get(1).getTimeOffset());
        Assert.assertEquals(0, resultViews.get(2).getTimeOffset());
    }

    @Test
    public void test_negative_inter_object_delay() {
        List<SpruceTimedView> resultViews = /* interObjectDelay= */
        /* reversed= */
        new InlineSort((-1), false, Corner.BOTTOM_RIGHT).getViewListWithTimeOffsets(mockParent, mockChildren);
        Assert.assertEquals(0, resultViews.get(0).getTimeOffset());
        Assert.assertEquals((-1), resultViews.get(1).getTimeOffset());
        Assert.assertEquals((-2), resultViews.get(2).getTimeOffset());
    }
}

