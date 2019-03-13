package com.airbnb.android.airmapview;


import AirMapViewTypes.NATIVE;
import AirMapViewTypes.WEB;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultAirMapViewBuilderTest {
    @Test
    public void shouldReturnNativeAirMapViewByDefault() {
        DefaultAirMapViewBuilder factory = new DefaultAirMapViewBuilder(null, true);
        Assert.assertThat(factory.builder(), CoreMatchers.instanceOf(NativeAirMapViewBuilder.class));
    }

    @Test
    public void shouldReturnWebAirMapViewIfDefaultNotSupported() {
        DefaultAirMapViewBuilder factory = new DefaultAirMapViewBuilder(null, false);
        Assert.assertThat(factory.builder(), CoreMatchers.instanceOf(WebAirMapViewBuilder.class));
    }

    @Test
    public void shouldReturnNativeAirMapViewWhenRequestedExplicitly() {
        DefaultAirMapViewBuilder factory = new DefaultAirMapViewBuilder(null, true);
        AirMapViewBuilder builder = factory.builder(NATIVE);
        Assert.assertThat(builder, CoreMatchers.instanceOf(NativeAirMapViewBuilder.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenRequestedNativeWebViewAndNotSupported() {
        DefaultAirMapViewBuilder factory = new DefaultAirMapViewBuilder(null, false);
        factory.builder(NATIVE);
    }

    @Test
    public void shouldReturnWebAirMapViewWhenRequestedExplicitly() {
        DefaultAirMapViewBuilder factory = new DefaultAirMapViewBuilder(null, false);
        AirMapViewBuilder builder = factory.builder(WEB);
        Assert.assertThat(builder, CoreMatchers.instanceOf(WebAirMapViewBuilder.class));
    }
}

