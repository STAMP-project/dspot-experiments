package org.robolectric.shadows;


import ResType.CHAR_SEQUENCE;
import ResType.COLOR;
import ResType.DRAWABLE;
import ResType.INTEGER;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.res.ResType;
import org.robolectric.res.TypedResource;
import org.robolectric.res.XmlContext;


@RunWith(AndroidJUnit4.class)
public class ConverterTest {
    private XmlContext xmlContext;

    @Test
    public void fromCharSequence_asInt_shouldHandleSpacesInString() {
        final TypedResource<String> resource = new TypedResource(" 100 ", ResType.CHAR_SEQUENCE, xmlContext);
        assertThat(Converter.getConverter(CHAR_SEQUENCE).asInt(resource)).isEqualTo(100);
    }

    @Test
    public void fromCharSequence_asCharSequence_shouldHandleSpacesInString() {
        final TypedResource<String> resource = new TypedResource(" Robolectric ", ResType.CHAR_SEQUENCE, xmlContext);
        assertThat(Converter.getConverter(CHAR_SEQUENCE).asCharSequence(resource)).isEqualTo("Robolectric");
    }

    @Test
    public void fromColor_asInt_shouldHandleSpacesInString() {
        final TypedResource<String> resource = new TypedResource(" #aaaaaa ", ResType.COLOR, xmlContext);
        assertThat(Converter.getConverter(COLOR).asInt(resource)).isEqualTo((-5592406));
    }

    @Test
    public void fromDrawableValue_asInt_shouldHandleSpacesInString() {
        final TypedResource<String> resource = new TypedResource(" #aaaaaa ", ResType.DRAWABLE, xmlContext);
        assertThat(Converter.getConverter(DRAWABLE).asInt(resource)).isEqualTo((-5592406));
    }

    @Test
    public void fromInt_asInt_shouldHandleSpacesInString() {
        final TypedResource<String> resource = new TypedResource(" 100 ", ResType.INTEGER, xmlContext);
        assertThat(Converter.getConverter(INTEGER).asInt(resource)).isEqualTo(100);
    }
}

