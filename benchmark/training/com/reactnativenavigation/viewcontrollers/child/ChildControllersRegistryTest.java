package com.reactnativenavigation.viewcontrollers.child;


import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.viewcontrollers.ChildController;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import org.junit.Test;
import org.mockito.Mockito;


public class ChildControllersRegistryTest extends BaseTest {
    private ChildControllersRegistry uut;

    private ChildController child1;

    private ChildController child2;

    @Test
    public void onViewAppeared() {
        child1.onViewAppeared();
        Mockito.verify(child1, Mockito.times(0)).onViewBroughtToFront();
        assertThat(uut.size()).isOne();
    }

    @Test
    public void onViewDisappear() {
        child1.onViewAppeared();
        child2.onViewAppeared();
        assertThat(uut.size()).isEqualTo(2);
        child2.onViewDisappear();
        Mockito.verify(child1, Mockito.times(1)).onViewBroughtToFront();
        assertThat(uut.size()).isOne();
    }
}

