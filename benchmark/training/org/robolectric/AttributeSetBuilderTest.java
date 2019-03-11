package org.robolectric;


import AttributeResource.NULL_VALUE;
import android.R.attr.height;
import android.R.attr.id;
import android.R.attr.text;
import android.R.attr.width;
import android.R.string.ok;
import android.util.AttributeSet;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.res.AttributeResource;

import static org.robolectric.R.attr.animalStyle;
import static org.robolectric.R.attr.aspectRatio;
import static org.robolectric.R.attr.isSugary;
import static org.robolectric.R.attr.itemType;
import static org.robolectric.R.attr.message;
import static org.robolectric.R.attr.numColumns;
import static org.robolectric.R.attr.scrollBars;
import static org.robolectric.R.attr.string2;
import static org.robolectric.R.attr.sugarinessPercent;
import static org.robolectric.R.id.text1;
import static org.robolectric.R.string.howdy;
import static org.robolectric.R.string.ok;
import static org.robolectric.R.style.Gastropod;


/**
 * Tests for {@link Robolectric#buildAttributeSet()}
 */
@RunWith(AndroidJUnit4.class)
public class AttributeSetBuilderTest {
    private static final String APP_NS = AttributeResource.RES_AUTO_NS_URI;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getAttributeResourceValue_shouldReturnTheResourceValue() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(text, "@android:string/ok").build();
        assertThat(roboAttributeSet.getAttributeResourceValue(AttributeResource.ANDROID_NS, "text", 0)).isEqualTo(ok);
    }

    @Test
    public void getAttributeResourceValueWithLeadingWhitespace_shouldReturnTheResourceValue() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(text, " @android:string/ok").build();
        assertThat(roboAttributeSet.getAttributeResourceValue(AttributeResource.ANDROID_NS, "text", 0)).isEqualTo(ok);
    }

    @Test
    public void getSystemAttributeResourceValue_shouldReturnDefaultValueForNullResourceId() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(text, NULL_VALUE).build();
        assertThat(roboAttributeSet.getAttributeResourceValue(((AttributeResource.ANDROID_RES_NS_PREFIX) + "com.some.namespace"), "text", 0)).isEqualTo(0);
    }

    @Test
    public void getSystemAttributeResourceValue_shouldReturnDefaultValueForNonMatchingNamespaceId() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(id, "@+id/text1").build();
        assertThat(roboAttributeSet.getAttributeResourceValue(((AttributeResource.ANDROID_RES_NS_PREFIX) + "com.some.other.namespace"), "id", 0)).isEqualTo(0);
    }

    @Test
    public void shouldCopeWithDefiningLocalIds() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(id, "@+id/text1").build();
        assertThat(roboAttributeSet.getAttributeResourceValue(AttributeResource.ANDROID_NS, "id", 0)).isEqualTo(text1);
    }

    @Test
    public void getAttributeResourceValue_withNamespace_shouldReturnTheResourceValue() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(message, "@string/howdy").build();
        assertThat(roboAttributeSet.getAttributeResourceValue(AttributeSetBuilderTest.APP_NS, "message", 0)).isEqualTo(howdy);
    }

    @Test
    public void getAttributeResourceValue_shouldReturnDefaultValueWhenAttributeIsNull() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(text, NULL_VALUE).build();
        assertThat(roboAttributeSet.getAttributeResourceValue(AttributeSetBuilderTest.APP_NS, "message", (-1))).isEqualTo((-1));
    }

    @Test
    public void getAttributeResourceValue_shouldReturnDefaultValueWhenNotInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getAttributeResourceValue(AttributeSetBuilderTest.APP_NS, "message", (-1))).isEqualTo((-1));
    }

    @Test
    public void getAttributeBooleanValue_shouldGetBooleanValuesFromAttributes() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(isSugary, "true").build();
        assertThat(roboAttributeSet.getAttributeBooleanValue(AttributeSetBuilderTest.APP_NS, "isSugary", false)).isTrue();
    }

    @Test
    public void getAttributeBooleanValue_withNamespace_shouldGetBooleanValuesFromAttributes() throws Exception {
        // org.robolectric.lib1.R values should be reconciled to match org.robolectric.R values.
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(isSugary, "true").build();
        assertThat(roboAttributeSet.getAttributeBooleanValue(AttributeSetBuilderTest.APP_NS, "isSugary", false)).isTrue();
    }

    @Test
    public void getAttributeBooleanValue_shouldReturnDefaultBooleanValueWhenNotInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getAttributeBooleanValue(((AttributeResource.ANDROID_RES_NS_PREFIX) + "com.some.namespace"), "isSugary", true)).isTrue();
    }

    @Test
    public void getAttributeValue_byName_shouldReturnValueFromAttribute() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(isSugary, "oh heck yeah").build();
        assertThat(roboAttributeSet.getAttributeValue(AttributeSetBuilderTest.APP_NS, "isSugary")).isEqualTo("false");
        assertThat(roboAttributeSet.getAttributeBooleanValue(AttributeSetBuilderTest.APP_NS, "isSugary", true)).isEqualTo(false);
        assertThat(roboAttributeSet.getAttributeBooleanValue(AttributeSetBuilderTest.APP_NS, "animalStyle", true)).isEqualTo(true);
    }

    @Test
    public void getAttributeValue_byNameWithReference_shouldReturnFullyQualifiedValueFromAttribute() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(isSugary, "@string/ok").build();
        assertThat(roboAttributeSet.getAttributeValue(AttributeSetBuilderTest.APP_NS, "isSugary")).isEqualTo(("@" + (ok)));
    }

    @Test
    public void getAttributeValue_byId_shouldReturnValueFromAttribute() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(isSugary, "oh heck yeah").build();
        assertThat(roboAttributeSet.getAttributeValue(0)).isEqualTo("false");
    }

    @Test
    public void getAttributeValue_byIdWithReference_shouldReturnValueFromAttribute() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(isSugary, "@string/ok").build();
        assertThat(roboAttributeSet.getAttributeValue(0)).isEqualTo(("@" + (ok)));
    }

    @Test
    public void getAttributeIntValue_shouldReturnValueFromAttribute() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(sugarinessPercent, "100").build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "sugarinessPercent", 0)).isEqualTo(100);
    }

    @Test
    public void getAttributeIntValue_shouldReturnHexValueFromAttribute() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(sugarinessPercent, "0x10").build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "sugarinessPercent", 0)).isEqualTo(16);
    }

    @Test
    public void getAttributeIntValue_whenTypeAllowsIntOrEnum_withInt_shouldReturnInt() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(numColumns, "3").build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "numColumns", 0)).isEqualTo(3);
    }

    @Test
    public void getAttributeIntValue_shouldReturnValueFromAttributeWhenNotInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "sugarinessPercent", 42)).isEqualTo(42);
    }

    @Test
    public void getAttributeIntValue_shouldReturnEnumValuesForEnumAttributesWhenNotInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "itemType", 24)).isEqualTo(24);
    }

    @Test
    public void getAttributeIntValue_shouldReturnEnumValuesForEnumAttributesInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(itemType, "ungulate").build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "itemType", 24)).isEqualTo(1);
        AttributeSet roboAttributeSet2 = Robolectric.buildAttributeSet().addAttribute(itemType, "marsupial").build();
        assertThat(roboAttributeSet2.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "itemType", 24)).isEqualTo(0);
    }

    @Test
    public void shouldFailOnMissingEnumValue() throws Exception {
        try {
            Robolectric.buildAttributeSet().addAttribute(itemType, "simian").build();
            Assert.fail("should fail");
        } catch (Exception e) {
            // expected
            assertThat(e.getMessage()).contains("no value found for simian");
        }
    }

    @Test
    public void shouldFailOnMissingFlagValue() throws Exception {
        try {
            Robolectric.buildAttributeSet().addAttribute(scrollBars, "temporal").build();
            Assert.fail("should fail");
        } catch (Exception e) {
            // expected
            assertThat(e.getMessage()).contains("no value found for temporal");
        }
    }

    @Test
    public void getAttributeIntValue_shouldReturnFlagValuesForFlagAttributesInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(scrollBars, "horizontal|vertical").build();
        assertThat(roboAttributeSet.getAttributeIntValue(AttributeSetBuilderTest.APP_NS, "scrollBars", 24)).isEqualTo((256 | 512));
    }

    @Test
    public void getAttributeFloatValue_shouldGetFloatValuesFromAttributes() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(aspectRatio, "1234.456").build();
        assertThat(roboAttributeSet.getAttributeFloatValue(AttributeSetBuilderTest.APP_NS, "aspectRatio", 78.9F)).isEqualTo(1234.456F);
    }

    @Test
    public void getAttributeFloatValue_shouldReturnDefaultFloatValueWhenNotInAttributeSet() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getAttributeFloatValue(AttributeSetBuilderTest.APP_NS, "aspectRatio", 78.9F)).isEqualTo(78.9F);
    }

    @Test
    public void getClassAndIdAttribute_returnsZeroWhenNotSpecified() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getClassAttribute()).isNull();
        assertThat(roboAttributeSet.getIdAttribute()).isNull();
    }

    @Test
    public void getClassAndIdAttribute_returnsAttr() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().setIdAttribute("the id").setClassAttribute("the class").build();
        assertThat(roboAttributeSet.getClassAttribute()).isEqualTo("the class");
        assertThat(roboAttributeSet.getIdAttribute()).isEqualTo("the id");
    }

    @Test
    public void getStyleAttribute_returnsZeroWhenNoStyle() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().build();
        assertThat(roboAttributeSet.getStyleAttribute()).isEqualTo(0);
    }

    @Test
    public void getStyleAttribute_returnsCorrectValue() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().setStyleAttribute("@style/Gastropod").build();
        assertThat(roboAttributeSet.getStyleAttribute()).isEqualTo(Gastropod);
    }

    @Test
    public void getStyleAttribute_whenStyleIsBogus() throws Exception {
        try {
            Robolectric.buildAttributeSet().setStyleAttribute("@style/non_existent_style").build();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().contains("no such resource @style/non_existent_style while resolving value for style");
        }
    }

    @Test
    public void getAttributeNameResource() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(aspectRatio, "1").build();
        assertThat(roboAttributeSet.getAttributeNameResource(0)).isEqualTo(aspectRatio);
    }

    @Test
    public void shouldReturnAttributesInOrderOfNameResId() throws Exception {
        AttributeSet roboAttributeSet = Robolectric.buildAttributeSet().addAttribute(height, "1px").addAttribute(animalStyle, "meow").addAttribute(width, "1px").build();
        assertThat(Arrays.asList(roboAttributeSet.getAttributeName(0), roboAttributeSet.getAttributeName(1), roboAttributeSet.getAttributeName(2))).containsExactly("height", "width", "animalStyle");
        assertThat(Arrays.asList(roboAttributeSet.getAttributeNameResource(0), roboAttributeSet.getAttributeNameResource(1), roboAttributeSet.getAttributeNameResource(2))).containsExactly(height, width, animalStyle);
    }

    @Test
    public void whenAttrSetAttrSpecifiesUnknownStyle_throwsException() throws Exception {
        try {
            Robolectric.buildAttributeSet().addAttribute(string2, "?org.robolectric:attr/noSuchAttr").build();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("no such attr ?org.robolectric:attr/noSuchAttr");
            assertThat(e.getMessage()).contains("while resolving value for org.robolectric:attr/string2");
        }
    }

    @Test
    public void whenAttrSetAttrSpecifiesUnknownReference_throwsException() throws Exception {
        try {
            Robolectric.buildAttributeSet().addAttribute(string2, "@org.robolectric:attr/noSuchRes").build();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("no such resource @org.robolectric:attr/noSuchRes");
            assertThat(e.getMessage()).contains("while resolving value for org.robolectric:attr/string2");
        }
    }
}

