package com.reactnativenavigation.react;


import com.reactnativenavigation.BaseTest;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;

import static RuntimeEnvironment.application;


public class NavigationReactNativeHostTest extends BaseTest {
    @Test
    public void getPackagesDefaults() {
        NavigationReactNativeHost uut = new NavigationReactNativeHost(application, false, null);
        assertThat(uut.getPackages()).hasSize(2).extracting("class").containsOnly(MainReactPackage.class, NavigationPackage.class);
    }

    @Test
    public void getPackagesAddsAdditionalPackages() {
        ReactPackage myPackage = Mockito.mock(ReactPackage.class);
        NavigationReactNativeHost uut = new NavigationReactNativeHost(application, false, Collections.singletonList(myPackage));
        assertThat(uut.getPackages()).hasSize(3).containsOnlyOnce(myPackage);
    }
}

