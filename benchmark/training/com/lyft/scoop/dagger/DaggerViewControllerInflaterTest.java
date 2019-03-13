package com.lyft.scoop.dagger;


import Scoop.Builder;
import com.lyft.scoop.Scoop;
import com.lyft.scoop.ViewController;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DaggerViewControllerInflaterTest {
    @Mock
    DaggerInjector mockDaggerInjector;

    private DaggerViewControllerInflater daggerViewControllerInflater;

    private Builder scoopBuilder;

    private Scoop scoop;

    @Test
    public void testCreateViewController() {
        daggerViewControllerInflater.createViewController(scoop, DaggerViewControllerInflaterTest.TestViewController.class);
        Mockito.verify(mockDaggerInjector).get(ArgumentMatchers.eq(DaggerViewControllerInflaterTest.TestViewController.class));
    }

    static class TestViewController extends ViewController {
        @Override
        protected int layoutId() {
            return 0;
        }
    }
}

