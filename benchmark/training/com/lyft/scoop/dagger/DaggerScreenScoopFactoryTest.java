package com.lyft.scoop.dagger;


import Scoop.Builder;
import com.lyft.scoop.Scoop;
import com.lyft.scoop.Screen;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DaggerScreenScoopFactoryTest {
    @Mock
    DaggerInjector mockDaggerInjector;

    private DaggerScreenScoopFactory daggerScreenScooper;

    private Builder scoopBuilder;

    private Scoop scoop;

    @Test
    public void testAddModule() {
        Mockito.when(mockDaggerInjector.extend(ArgumentMatchers.any(DaggerScreenScoopFactoryTest.TestModule.class))).thenReturn(mockDaggerInjector);
        Assert.assertNotNull(daggerScreenScooper.addServices(scoopBuilder, new DaggerScreenScoopFactoryTest.TestScreen(), scoop));
    }

    @Test
    public void testNoModule() {
        Assert.assertNotNull(daggerScreenScooper.addServices(scoopBuilder, new DaggerScreenScoopFactoryTest.TestNoModuleScreen(), scoop));
    }

    @DaggerModule(DaggerScreenScoopFactoryTest.TestModule.class)
    private static class TestScreen extends Screen {}

    static class TestNoModuleScreen extends Screen {}

    static class TestModule {}
}

