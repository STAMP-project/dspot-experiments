/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.testing;


import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.drawable.DrawableTestUtils;
import com.facebook.drawee.drawable.VisibilityAwareDrawable;
import com.facebook.drawee.drawable.VisibilityCallback;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class DraweeMocksTest {
    @Test
    public void testMockProviderOf() {
        Object obj = Mockito.mock(Object.class);
        Supplier<Object> provider = DraweeMocks.supplierOf(obj);
        Assert.assertEquals(obj, provider.get());
        Assert.assertEquals(obj, provider.get());
        Assert.assertEquals(obj, provider.get());
        Assert.assertEquals(obj, provider.get());
        Assert.assertEquals(obj, provider.get());
        Object obj1 = Mockito.mock(Object.class);
        Object obj2 = Mockito.mock(Object.class);
        Object obj3 = Mockito.mock(Object.class);
        Supplier<Object> multiProvider = DraweeMocks.supplierOf(obj1, obj2, obj3);
        Assert.assertEquals(obj1, multiProvider.get());
        Assert.assertEquals(obj2, multiProvider.get());
        Assert.assertEquals(obj3, multiProvider.get());
        Assert.assertEquals(obj3, multiProvider.get());
        Assert.assertEquals(obj3, multiProvider.get());
    }

    @Test
    public void testMockBuilderOfDrawableHierarchies() {
        GenericDraweeHierarchy gdh = DraweeMocks.mockDraweeHierarchy();
        GenericDraweeHierarchyBuilder builder = DraweeMocks.mockBuilderOf(gdh);
        Assert.assertEquals(gdh, builder.build());
        Assert.assertEquals(gdh, builder.build());
        Assert.assertEquals(gdh, builder.build());
        Assert.assertEquals(gdh, builder.build());
        Assert.assertEquals(gdh, builder.build());
        GenericDraweeHierarchy gdh1 = DraweeMocks.mockDraweeHierarchy();
        GenericDraweeHierarchy gdh2 = DraweeMocks.mockDraweeHierarchy();
        GenericDraweeHierarchy gdh3 = DraweeMocks.mockDraweeHierarchy();
        GenericDraweeHierarchyBuilder multiBuilder = DraweeMocks.mockBuilderOf(gdh1, gdh2, gdh3);
        Assert.assertEquals(gdh1, multiBuilder.build());
        Assert.assertEquals(gdh2, multiBuilder.build());
        Assert.assertEquals(gdh3, multiBuilder.build());
        Assert.assertEquals(gdh3, multiBuilder.build());
        Assert.assertEquals(gdh3, multiBuilder.build());
    }

    @Test
    public void testMockDrawable_VisibilityCallback() {
        boolean reset = true;
        Drawable drawable = DrawableTestUtils.mockDrawable();
        Assert.assertTrue((drawable instanceof VisibilityAwareDrawable));
        VisibilityAwareDrawable visibilityAwareDrawable = ((VisibilityAwareDrawable) (drawable));
        VisibilityCallback visibilityCallback = Mockito.mock(VisibilityCallback.class);
        visibilityAwareDrawable.setVisibilityCallback(visibilityCallback);
        InOrder inOrder = Mockito.inOrder(visibilityCallback);
        drawable.setVisible(false, reset);
        inOrder.verify(visibilityCallback).onVisibilityChange(false);
        drawable.setVisible(true, reset);
        inOrder.verify(visibilityCallback).onVisibilityChange(true);
        drawable.setVisible(false, reset);
        inOrder.verify(visibilityCallback).onVisibilityChange(false);
    }
}

