package org.parceler;


import android.arch.lifecycle.MutableLiveData;
import android.os.Parcelable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MutableLiveDataTest {
    @Parcel
    public static class TestTarget {
        MutableLiveData<MutableLiveDataTest.TestParcel> test = new MutableLiveData<MutableLiveDataTest.TestParcel>();
    }

    @Parcel
    public static class TestParcel {
        String value;
    }

    @Test
    public void testMutableLiveData() {
        MutableLiveDataTest.TestParcel parcel = new MutableLiveDataTest.TestParcel();
        parcel.value = "test";
        MutableLiveDataTest.TestTarget target = new MutableLiveDataTest.TestTarget();
        target.test.setValue(parcel);
        Parcelable wrap = ParcelsTestUtil.wrap(target);
        MutableLiveDataTest.TestTarget unwrap = Parcels.unwrap(wrap);
        Assert.assertNotNull(unwrap.test.getValue());
        Assert.assertEquals("test", unwrap.test.getValue().value);
    }
}

