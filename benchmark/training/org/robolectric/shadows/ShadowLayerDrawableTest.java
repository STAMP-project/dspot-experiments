package org.robolectric.shadows;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowLayerDrawableTest {
    /**
     * drawables
     */
    protected Drawable drawable1000;

    protected Drawable drawable2000;

    protected Drawable drawable3000;

    protected Drawable drawable4000;

    /**
     * drawables
     */
    protected Drawable[] drawables;

    @Test
    public void testGetNumberOfLayers() {
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        Assert.assertEquals("count", 3, layerDrawable.getNumberOfLayers());
    }

    @Test
    public void testSetDrawableByLayerId1() throws Exception {
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int index = 1;
        int layerId = 345;
        layerDrawable.setId(index, layerId);
        layerDrawable.setDrawableByLayerId(layerId, drawable4000);
        Assert.assertEquals(Shadows.shadowOf(drawable4000).getCreatedFromResId(), Shadows.shadowOf(layerDrawable.getDrawable(index)).getCreatedFromResId());
    }

    @Test
    public void testSetDrawableByLayerId2() throws Exception {
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int index = 0;
        int layerId = 345;
        layerDrawable.setId(index, layerId);
        layerDrawable.setDrawableByLayerId(layerId, drawable4000);
        Assert.assertEquals(Shadows.shadowOf(drawable4000).getCreatedFromResId(), Shadows.shadowOf(layerDrawable.getDrawable(index)).getCreatedFromResId());
    }

    @Test
    public void setDrawableByLayerId_shouldReturnFalseIfIdNotFound() throws Exception {
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        boolean ret = layerDrawable.setDrawableByLayerId(123, drawable4000);
        Assert.assertFalse(ret);
    }

    @Test
    public void setDrawableByLayerId_shouldReturnTrueIfIdWasFound() throws Exception {
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int index = 0;
        int layerId = 345;
        layerDrawable.setId(index, layerId);
        boolean ret = layerDrawable.setDrawableByLayerId(layerId, drawable4000);
        Assert.assertTrue(ret);
    }
}

