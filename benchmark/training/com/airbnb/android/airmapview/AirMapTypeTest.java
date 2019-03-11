package com.airbnb.android.airmapview;


import android.os.Bundle;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AirMapTypeTest {
    @Test
    public void shouldConvertToBundle() {
        Bundle bundle = Mockito.mock(Bundle.class);
        AirMapType mapType = new GoogleWebMapType();
        mapType.toBundle(bundle);
        Mockito.verify(bundle).putString("map_domain", mapType.getDomain());
        Mockito.verify(bundle).putString("map_url", mapType.getMapUrl());
        Mockito.verify(bundle).putString("map_file_name", mapType.getFileName());
    }

    @Test
    public void shouldConstructFromBundle() {
        GoogleWebMapType mapType = new GoogleWebMapType();
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getString("map_domain", "")).thenReturn(mapType.getDomain());
        Mockito.when(bundle.getString("map_url", "")).thenReturn(mapType.getMapUrl());
        Mockito.when(bundle.getString("map_file_name", "")).thenReturn(mapType.getFileName());
        Assert.assertThat(AirMapType.fromBundle(bundle), IsEqual.<AirMapType>equalTo(mapType));
    }
}

