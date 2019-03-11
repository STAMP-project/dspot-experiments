package org.robolectric.shadows;


import AttributeResource.NULL_VALUE;
import android.R.attr;
import android.R.attr.background;
import android.R.attr.gravity;
import android.R.attr.id;
import android.R.attr.keycode;
import android.R.attr.width;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;

import static org.robolectric.R.attr.aspectRatio;
import static org.robolectric.R.attr.isSugary;
import static org.robolectric.R.attr.responses;
import static org.robolectric.R.attr.scrollBars;
import static org.robolectric.R.id.snippet_text;


@RunWith(AndroidJUnit4.class)
public class ShadowTypedArrayTest {
    private Context context;

    @Test
    public void getResources() throws Exception {
        Assert.assertNotNull(context.obtainStyledAttributes(new int[]{  }).getResources());
    }

    @Test
    public void getInt_shouldReturnDefaultValue() throws Exception {
        assertThat(context.obtainStyledAttributes(new int[]{ attr.alpha }).getInt(0, (-1))).isEqualTo((-1));
    }

    @Test
    public void getInteger_shouldReturnDefaultValue() throws Exception {
        assertThat(context.obtainStyledAttributes(new int[]{ attr.alpha }).getInteger(0, (-1))).isEqualTo((-1));
    }

    @Test
    public void getInt_withFlags_shouldReturnValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(gravity, "top|left").build(), new int[]{ attr.gravity });
        assertThat(typedArray.getInt(0, (-1))).isEqualTo(51);
    }

    @Test
    public void getResourceId_shouldReturnDefaultValue() throws Exception {
        assertThat(context.obtainStyledAttributes(new int[]{ attr.alpha }).getResourceId(0, (-1))).isEqualTo((-1));
    }

    @Test
    public void getResourceId_shouldReturnActualValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(id, "@+id/snippet_text").build(), new int[]{ attr.id });
        assertThat(typedArray.getResourceId(0, (-1))).isEqualTo(snippet_text);
    }

    @Test
    public void getFraction_shouldReturnDefaultValue() throws Exception {
        assertThat(context.obtainStyledAttributes(new int[]{ attr.width }).getDimension(0, (-1.0F))).isEqualTo((-1.0F));
    }

    @Test
    public void getFraction_shouldReturnGivenValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(width, "50%").build(), new int[]{ attr.width });
        assertThat(typedArray.getFraction(0, 100, 1, (-1))).isEqualTo(50.0F);
    }

    @Test
    public void getDimension_shouldReturnDefaultValue() throws Exception {
        assertThat(context.obtainStyledAttributes(new int[]{ attr.width }).getDimension(0, (-1.0F))).isEqualTo((-1.0F));
    }

    @Test
    public void getDimension_shouldReturnGivenValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(width, "50dp").build(), new int[]{ attr.width });
        assertThat(typedArray.getDimension(0, (-1))).isEqualTo(50.0F);
    }

    @Test
    public void getDrawable_withExplicitColorValue_shouldReturnColorDrawable() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(background, "#ff777777").build(), new int[]{ attr.background });
        ColorDrawable drawable = ((ColorDrawable) (typedArray.getDrawable(0)));
        assertThat(drawable.getColor()).isEqualTo(-8947849);
    }

    @Test
    public void getTextArray_whenNoSuchAttribute_shouldReturnNull() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(keycode, "@array/greetings").build(), new int[]{ attr.absListViewStyle });
        CharSequence[] textArray = typedArray.getTextArray(0);
        assertThat(textArray).isInstanceOf(CharSequence[].class);
        for (CharSequence text : textArray) {
            assertThat(text).isNull();
        }
    }

    @Test
    public void getTextArray_shouldReturnValues() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(responses, "@array/greetings").build(), new int[]{ responses });
        assertThat(typedArray.getTextArray(0)).asList().containsExactly("hola", "Hello");
    }

    @Test
    public void hasValue_withValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(responses, "@array/greetings").build(), new int[]{ responses });
        assertThat(typedArray.hasValue(0)).isTrue();
    }

    @Test
    public void hasValue_withoutValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(null, new int[]{ responses });
        assertThat(typedArray.hasValue(0)).isFalse();
    }

    @Test
    public void hasValue_withNullValue() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(responses, NULL_VALUE).build(), new int[]{ responses });
        assertThat(typedArray.hasValue(0)).isFalse();
    }

    @Test
    public void shouldEnumeratePresentValues() throws Exception {
        TypedArray typedArray = context.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(responses, "@array/greetings").addAttribute(aspectRatio, "1").build(), new int[]{ scrollBars, responses, isSugary });
        assertThat(typedArray.getIndexCount()).isEqualTo(1);
        assertThat(typedArray.getIndex(0)).isEqualTo(1);
    }
}

