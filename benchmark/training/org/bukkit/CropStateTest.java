package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CropStateTest {
    @Test
    public void getByData() {
        for (CropState cropState : CropState.values()) {
            Assert.assertThat(CropState.getByData(cropState.getData()), CoreMatchers.is(cropState));
        }
    }
}

