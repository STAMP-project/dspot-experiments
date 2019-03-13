package org.parceler;


import Parcel.Serialization;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by john on 9/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MismatchedTypeTest {
    @Parcel(Serialization.BEAN)
    public static class Victim {
        private int value;

        @ParcelConstructor
        public Victim(Integer value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Test
    public void testMismatched() {
        MismatchedTypeTest.Victim victim = new MismatchedTypeTest.Victim(42);
        MismatchedTypeTest.Victim output = Parcels.unwrap(ParcelsTestUtil.wrap(victim));
        Assert.assertEquals(42, output.getValue());
    }
}

