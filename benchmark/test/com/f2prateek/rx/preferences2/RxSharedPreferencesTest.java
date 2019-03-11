package com.f2prateek.rx.preferences2;


import android.annotation.SuppressLint;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


// 
// 
// 
@RunWith(RobolectricTestRunner.class)
@SuppressLint("ApplySharedPref")
@SuppressWarnings({ "ResourceType", "ConstantConditions" })
public class RxSharedPreferencesTest {
    private RxSharedPreferences rxPreferences;

    @Test
    public void clearRemovesAllPreferences() {
        Preference<String> preference = rxPreferences.getString("key", "default");
        preference.set("foo");
        rxPreferences.clear();
        assertThat(preference.get()).isEqualTo("default");
    }

    @Test
    public void createWithNullThrows() {
        try {
            RxSharedPreferences.create(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("preferences == null");
        }
    }

    @Test
    public void booleanNullKeyThrows() {
        try {
            rxPreferences.getBoolean(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
        try {
            rxPreferences.getBoolean(null, false);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void booleanNullDefaultValueThrows() {
        try {
            rxPreferences.getBoolean("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void enumNullKeyThrows() {
        try {
            rxPreferences.getEnum(null, Roshambo.ROCK, Roshambo.class);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void enumNullClassThrows() {
        try {
            rxPreferences.getEnum("key", Roshambo.ROCK, null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("enumClass == null");
        }
    }

    @Test
    public void enumNullDefaultValueThrows() {
        try {
            rxPreferences.getEnum("key", null, Roshambo.class);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void floatNullKeyThrows() {
        try {
            rxPreferences.getFloat(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
        try {
            rxPreferences.getFloat(null, 0.0F);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void floatNullDefaultValueThrows() {
        try {
            rxPreferences.getFloat("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void integerNullKeyThrows() {
        try {
            rxPreferences.getInteger(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
        try {
            rxPreferences.getInteger(null, 0);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void integerNullDefaultValueThrows() {
        try {
            rxPreferences.getInteger("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void longNullKeyThrows() {
        try {
            rxPreferences.getLong(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
        try {
            rxPreferences.getLong(null, 0L);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void longNullDefaultValueThrows() {
        try {
            rxPreferences.getLong("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void objectNullKeyThrows() {
        try {
            rxPreferences.getObject(null, new Point(1, 2), new PointPreferenceConverter());
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void objectNullAdapterThrows() {
        try {
            rxPreferences.getObject("key", new Point(1, 2), null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("converter == null");
        }
    }

    @Test
    public void objectNullDefaultValueThrows() {
        try {
            rxPreferences.getObject("key", null, new PointPreferenceConverter());
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void stringNullKeyThrows() {
        try {
            rxPreferences.getString(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
        try {
            rxPreferences.getString(null, "default");
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void stringNullDefaultValueThrows() {
        try {
            rxPreferences.getString("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }

    @Test
    public void stringSetNullKeyThrows() {
        try {
            rxPreferences.getStringSet(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
        try {
            rxPreferences.getStringSet(null, Collections.<String>emptySet());
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("key == null");
        }
    }

    @Test
    public void stringSetNullDefaultValueThrows() {
        try {
            rxPreferences.getStringSet("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("defaultValue == null");
        }
    }
}

