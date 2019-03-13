package org.robolectric.shadows;


import android.content.ContentValues;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowContentValuesTest {
    private static final String KEY = "key";

    private ContentValues contentValues;

    @Test
    public void shouldBeEqualIfBothContentValuesAreEmpty() {
        ContentValues valuesA = new ContentValues();
        ContentValues valuesB = new ContentValues();
        assertThat(valuesA).isEqualTo(valuesB);
    }

    @Test
    public void shouldBeEqualIfBothContentValuesHaveSameValues() {
        ContentValues valuesA = new ContentValues();
        valuesA.put("String", "A");
        valuesA.put("Integer", 23);
        valuesA.put("Boolean", false);
        ContentValues valuesB = new ContentValues();
        valuesB.putAll(valuesA);
        assertThat(valuesA).isEqualTo(valuesB);
    }

    @Test
    public void shouldNotBeEqualIfContentValuesHaveDifferentValue() {
        ContentValues valuesA = new ContentValues();
        valuesA.put("String", "A");
        ContentValues valuesB = new ContentValues();
        assertThat(valuesA).isNotEqualTo(valuesB);
        valuesB.put("String", "B");
        assertThat(valuesA).isNotEqualTo(valuesB);
    }

    @Test
    public void getAsBoolean_zero() {
        contentValues.put(ShadowContentValuesTest.KEY, 0);
        assertThat(contentValues.getAsBoolean(ShadowContentValuesTest.KEY)).isFalse();
    }

    @Test
    public void getAsBoolean_one() {
        contentValues.put(ShadowContentValuesTest.KEY, 1);
        assertThat(contentValues.getAsBoolean(ShadowContentValuesTest.KEY)).isTrue();
    }

    @Test
    public void getAsBoolean_false() {
        contentValues.put(ShadowContentValuesTest.KEY, false);
        assertThat(contentValues.getAsBoolean(ShadowContentValuesTest.KEY)).isFalse();
    }

    @Test
    public void getAsBoolean_true() {
        contentValues.put(ShadowContentValuesTest.KEY, true);
        assertThat(contentValues.getAsBoolean(ShadowContentValuesTest.KEY)).isTrue();
    }

    @Test
    public void getAsBoolean_falseString() {
        contentValues.put(ShadowContentValuesTest.KEY, "false");
        assertThat(contentValues.getAsBoolean(ShadowContentValuesTest.KEY)).isFalse();
    }

    @Test
    public void getAsBoolean_trueString() {
        contentValues.put(ShadowContentValuesTest.KEY, "true");
        assertThat(contentValues.getAsBoolean(ShadowContentValuesTest.KEY)).isTrue();
    }
}

