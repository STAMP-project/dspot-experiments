package org.robolectric.shadows;


import android.text.SpannedString;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowSpannedStringTest {
    @Test
    public void toString_shouldDelegateToUnderlyingCharSequence() {
        SpannedString spannedString = new SpannedString("foo");
        Assert.assertEquals("foo", spannedString.toString());
    }

    @Test
    public void valueOfSpannedString_shouldReturnItself() {
        SpannedString spannedString = new SpannedString("foo");
        Assert.assertSame(spannedString, SpannedString.valueOf(spannedString));
    }

    @Test
    public void valueOfCharSequence_shouldReturnNewSpannedString() {
        Assert.assertEquals("foo", SpannedString.valueOf("foo").toString());
    }
}

