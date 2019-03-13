package org.bukkit;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Material.INK_SACK;
import static Material.WOOL;


@RunWith(Parameterized.class)
public class DyeColorTest {
    @Parameterized.Parameter
    public DyeColor dye;

    @Test
    @SuppressWarnings("deprecation")
    public void getByData() {
        byte data = dye.getData();
        DyeColor byData = DyeColor.getByData(data);
        Assert.assertThat(byData, is(dye));
    }

    @Test
    public void getByWoolData() {
        byte data = dye.getWoolData();
        DyeColor byData = DyeColor.getByWoolData(data);
        Assert.assertThat(byData, is(dye));
    }

    @Test
    public void getByDyeData() {
        byte data = dye.getDyeData();
        DyeColor byData = DyeColor.getByDyeData(data);
        Assert.assertThat(byData, is(dye));
    }

    @Test
    public void getDyeDyeColor() {
        testColorable(new org.bukkit.material.Dye(INK_SACK, dye.getDyeData()));
    }

    @Test
    public void getWoolDyeColor() {
        testColorable(new org.bukkit.material.Wool(WOOL, dye.getWoolData()));
    }
}

