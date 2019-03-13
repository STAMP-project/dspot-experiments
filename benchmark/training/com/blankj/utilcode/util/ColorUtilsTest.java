package com.blankj.utilcode.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2019/01/15
 *     desc  : test ColorUtils
 * </pre>
 */
public class ColorUtilsTest extends BaseTest {
    @Test
    public void setAlphaComponent() {
        Assert.assertEquals(-2130706433, ColorUtils.setAlphaComponent(-1, 128));
        Assert.assertEquals(-2130706433, ColorUtils.setAlphaComponent(-1, 0.5F));
    }

    @Test
    public void setRedComponent() {
        Assert.assertEquals(-8323073, ColorUtils.setRedComponent(-1, 128));
        Assert.assertEquals(-8323073, ColorUtils.setRedComponent(-1, 0.5F));
    }

    @Test
    public void setGreenComponent() {
        Assert.assertEquals(-32513, ColorUtils.setGreenComponent(-1, 128));
        Assert.assertEquals(-32513, ColorUtils.setGreenComponent(-1, 0.5F));
    }

    @Test
    public void setBlueComponent() {
        Assert.assertEquals(-128, ColorUtils.setBlueComponent(-1, 128));
        Assert.assertEquals(-128, ColorUtils.setBlueComponent(-1, 0.5F));
    }

    @Test
    public void string2Int() {
        Assert.assertEquals(-1, ColorUtils.string2Int("#ffffff"));
        Assert.assertEquals(-1, ColorUtils.string2Int("#ffffffff"));
        Assert.assertEquals(-1, ColorUtils.string2Int("white"));
    }

    @Test
    public void int2RgbString() {
        Assert.assertEquals("#000001", ColorUtils.int2RgbString(1));
        Assert.assertEquals("#ffffff", ColorUtils.int2RgbString(16777215));
    }

    @Test
    public void int2ArgbString() {
        Assert.assertEquals("#ff000001", ColorUtils.int2ArgbString(1));
        Assert.assertEquals("#ffffffff", ColorUtils.int2ArgbString(16777215));
    }
}

