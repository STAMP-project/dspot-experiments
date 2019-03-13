package com.reactnativenavigation.presentation;


import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.utils.CollectionUtils;
import com.reactnativenavigation.viewcontrollers.ViewController;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;


public class RenderCheckerTest extends BaseTest {
    private RenderChecker uut;

    @Test
    public void areRendered() {
        Collection<ViewController> items = Arrays.asList(renderedComponent(), renderedComponent(), renderedComponent());
        assertThat(uut.areRendered(items)).isTrue();
        CollectionUtils.forEach(items, ( i) -> verify(i).isRendered());
    }

    @Test
    public void areRendered_reduce() {
        Collection<ViewController> items = Arrays.asList(renderedComponent(), notRenderedComponent(), renderedComponent());
        assertThat(uut.areRendered(items)).isFalse();
    }
}

