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
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(shadows = { ShadowPointF.class })
public class CorneredSortTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ViewGroup mockParent = Mockito.mock(ViewGroup.class);

    private List<View> mockChildren = new ArrayList<>();

    private CorneredSort corneredSort;

    @Test
    public void test_get_distance_point_for_top_left() {
        corneredSort = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, Corner.TOP_LEFT);
        PointF resultPoint = corneredSort.getDistancePoint(mockParent, mockChildren);
        Assert.assertEquals(new PointF(0.0F, 0.0F), resultPoint);
    }

    @Test
    public void test_get_distance_point_for_top_right() {
        corneredSort = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, Corner.TOP_RIGHT);
        // fake a width of 20, this will be the expected value
        Mockito.when(mockParent.getWidth()).thenReturn(20);
        PointF resultPoint = corneredSort.getDistancePoint(mockParent, mockChildren);
        Assert.assertEquals(new PointF(20.0F, 0.0F), resultPoint);
    }

    @Test
    public void test_get_distance_point_for_bottom_left() {
        corneredSort = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, Corner.BOTTOM_LEFT);
        // fake a height of 20, this will be the expected value
        Mockito.when(mockParent.getHeight()).thenReturn(20);
        PointF resultPoint = corneredSort.getDistancePoint(mockParent, mockChildren);
        Assert.assertEquals(new PointF(0.0F, 20.0F), resultPoint);
    }

    @Test
    public void test_get_distance_point_for_bottom_right() {
        corneredSort = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, Corner.BOTTOM_RIGHT);
        // fake a width of 20, this will be the expected value
        Mockito.when(mockParent.getWidth()).thenReturn(20);
        // fake a height of 20, this will be the expected value
        Mockito.when(mockParent.getHeight()).thenReturn(20);
        PointF resultPoint = corneredSort.getDistancePoint(mockParent, mockChildren);
        PointF expectedPoint = new PointF(20.0F, 20.0F);
        Assert.assertEquals(expectedPoint, resultPoint);
    }

    @Test
    public void test_corner_sort_constructor_throws_illegal_argument_exception_for_invalid_corner() throws IllegalArgumentException {
        expectedException.expect(IllegalArgumentException.class);
        corneredSort = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, Corner.valueOf("invalid"));
    }

    @Test
    public void test_corner_sort_constructor_throws_npe_for_null_corner() throws NullPointerException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Corner can't be null and must be a valid type");
        corneredSort = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, null);
    }

    @Test
    public void test_positive_inter_object_delay() {
        List<SpruceTimedView> resultViews = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(1, false, Corner.TOP_LEFT).getViewListWithTimeOffsets(mockParent, mockChildren);
        Assert.assertEquals(0, resultViews.get(0).getTimeOffset());
        Assert.assertEquals(1, resultViews.get(1).getTimeOffset());
        Assert.assertEquals(2, resultViews.get(2).getTimeOffset());
    }

    @Test
    public void test_inter_object_delay_of_zero() {
        List<SpruceTimedView> resultViews = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort(0, false, Corner.TOP_LEFT).getViewListWithTimeOffsets(mockParent, mockChildren);
        Assert.assertEquals(0, resultViews.get(0).getTimeOffset());
        Assert.assertEquals(0, resultViews.get(1).getTimeOffset());
        Assert.assertEquals(0, resultViews.get(2).getTimeOffset());
    }

    @Test
    public void test_negative_inter_object_delay() {
        List<SpruceTimedView> resultViews = /* interObjectDelay= */
        /* reversed= */
        new CorneredSort((-1), false, Corner.TOP_LEFT).getViewListWithTimeOffsets(mockParent, mockChildren);
        Assert.assertEquals(0, resultViews.get(0).getTimeOffset());
        Assert.assertEquals((-1), resultViews.get(1).getTimeOffset());
        Assert.assertEquals((-2), resultViews.get(2).getTimeOffset());
    }
}

