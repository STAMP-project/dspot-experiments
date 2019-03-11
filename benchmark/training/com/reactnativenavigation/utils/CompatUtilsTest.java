package com.reactnativenavigation.utils;


import com.reactnativenavigation.BaseTest;
import org.junit.Test;


public class CompatUtilsTest extends BaseTest {
    @Test
    public void generateViewId() throws Exception {
        assertThat(CompatUtils.generateViewId()).isPositive().isNotEqualTo(CompatUtils.generateViewId());
    }
}

