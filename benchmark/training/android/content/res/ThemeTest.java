package android.content.res;


import Color.BLACK;
import Color.WHITE;
import R.attr;
import R.attr.animalStyle;
import R.attr.isSugary;
import R.attr.logoHeight;
import R.attr.logoWidth;
import R.attr.someLayoutOne;
import R.attr.someLayoutTwo;
import R.attr.styleReference;
import R.dimen.test_dp_dimen;
import R.layout.activity_main;
import R.style.Gastropod;
import R.style.MyCustomView;
import R.style.ThemeWithSelfReferencingTextAttr;
import R.style.Theme_AnotherTheme;
import R.style.Theme_Robolectric_EmptyParent;
import R.style.Theme_ThirdTheme;
import R.style.Widget_AnotherTheme_Button;
import R.styleable.CustomView;
import R.styleable.CustomView_animalStyle;
import R.styleable.CustomView_aspectRatio;
import R.styleable.CustomView_aspectRatioEnabled;
import TypedValue.TYPE_DIMENSION;
import TypedValue.TYPE_REFERENCE;
import android.R.style.Theme_DeviceDefault;
import android.R.style.Theme_Light;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.util.TypedValue;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;

import static android.R.attr.buttonStyle;
import static android.R.attr.colorBackground;
import static android.R.attr.textAppearance;


/**
 * Compatibility test for {@link Resources.Theme}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class ThemeTest {
    private Resources resources;

    private Context context;

    @Test
    public void withEmptyTheme_returnsEmptyAttributes() throws Exception {
        assertThat(resources.newTheme().obtainStyledAttributes(new int[]{ attr.string1 }).hasValue(0)).isFalse();
    }

    @Test
    public void shouldLookUpStylesFromStyleResId() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedArray a = theme.obtainStyledAttributes(MyCustomView, CustomView);
        boolean enabled = a.getBoolean(CustomView_aspectRatioEnabled, false);
        assertThat(enabled).isTrue();
    }

    @Test
    public void shouldApplyStylesFromResourceReference() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedArray a = theme.obtainStyledAttributes(null, CustomView, animalStyle, 0);
        int animalStyleId = a.getResourceId(CustomView_animalStyle, 0);
        assertThat(animalStyleId).isEqualTo(Gastropod);
        assertThat(a.getFloat(CustomView_aspectRatio, 0.2F)).isEqualTo(1.69F);
    }

    @Test
    public void shouldApplyStylesFromAttributeReference() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_ThirdTheme, true);
        TypedArray a = theme.obtainStyledAttributes(null, CustomView, animalStyle, 0);
        int animalStyleId = a.getResourceId(CustomView_animalStyle, 0);
        assertThat(animalStyleId).isEqualTo(Gastropod);
        assertThat(a.getFloat(CustomView_aspectRatio, 0.2F)).isEqualTo(1.69F);
    }

    @Test
    public void shouldGetValuesFromAttributeReference() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_ThirdTheme, true);
        TypedValue value1 = new TypedValue();
        TypedValue value2 = new TypedValue();
        boolean resolved1 = theme.resolveAttribute(someLayoutOne, value1, true);
        boolean resolved2 = theme.resolveAttribute(someLayoutTwo, value2, true);
        assertThat(resolved1).isTrue();
        assertThat(resolved2).isTrue();
        assertThat(value1.resourceId).isEqualTo(activity_main);
        assertThat(value2.resourceId).isEqualTo(activity_main);
        assertThat(value1.coerceToString()).isEqualTo(value2.coerceToString());
    }

    @Test
    public void withResolveRefsFalse_shouldResolveValue() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedValue value = new TypedValue();
        boolean resolved = theme.resolveAttribute(logoWidth, value, false);
        assertThat(resolved).isTrue();
        assertThat(value.type).isEqualTo(TYPE_REFERENCE);
        assertThat(value.data).isEqualTo(test_dp_dimen);
    }

    @Test
    public void withResolveRefsFalse_shouldNotResolveResource() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedValue value = new TypedValue();
        boolean resolved = theme.resolveAttribute(logoHeight, value, false);
        assertThat(resolved).isTrue();
        assertThat(value.type).isEqualTo(TYPE_REFERENCE);
        assertThat(value.data).isEqualTo(test_dp_dimen);
    }

    @Test
    public void withResolveRefsTrue_shouldResolveResource() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedValue value = new TypedValue();
        boolean resolved = theme.resolveAttribute(logoHeight, value, true);
        assertThat(resolved).isTrue();
        assertThat(value.type).isEqualTo(TYPE_DIMENSION);
        assertThat(value.resourceId).isEqualTo(test_dp_dimen);
        assertThat(value.coerceToString()).isEqualTo("8.0dip");
    }

    @Test
    public void failToResolveCircularReference() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedValue value = new TypedValue();
        boolean resolved = theme.resolveAttribute(isSugary, value, false);
        assertThat(resolved).isFalse();
    }

    @Test
    public void canResolveAttrReferenceToDifferentPackage() throws Exception {
        Theme theme = resources.newTheme();
        theme.applyStyle(Theme_AnotherTheme, true);
        TypedValue value = new TypedValue();
        boolean resolved = theme.resolveAttribute(styleReference, value, false);
        assertThat(resolved).isTrue();
        assertThat(value.type).isEqualTo(TYPE_REFERENCE);
        assertThat(value.data).isEqualTo(Widget_AnotherTheme_Button);
    }

    @Test
    public void whenAThemeHasExplicitlyEmptyParentAttr_shouldHaveNoParent() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(Theme_Robolectric_EmptyParent, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ attr.string1 }).hasValue(0)).isFalse();
    }

    @Test
    public void shouldFindInheritedAndroidAttributeInTheme() throws Exception {
        context.setTheme(Theme_AnotherTheme);
        Resources.Theme theme1 = context.getTheme();
        TypedArray typedArray = theme1.obtainStyledAttributes(new int[]{ attr.typeface, buttonStyle });
        assertThat(typedArray.hasValue(0)).isTrue();// animalStyle

        assertThat(typedArray.hasValue(1)).isTrue();// layout_height

    }

    @Test
    public void themesShouldBeApplyableAcrossResources() throws Exception {
        Resources.Theme themeFromSystem = Resources.getSystem().newTheme();
        themeFromSystem.applyStyle(Theme_Light, true);
        Resources.Theme themeFromApp = resources.newTheme();
        themeFromApp.applyStyle(android.R.style.Theme, true);
        // themeFromSystem is Theme_Light, which has a white background...
        assertThat(themeFromSystem.obtainStyledAttributes(new int[]{ colorBackground }).getColor(0, 123)).isEqualTo(WHITE);
        // themeFromApp is Theme, which has a black background...
        assertThat(themeFromApp.obtainStyledAttributes(new int[]{ colorBackground }).getColor(0, 123)).isEqualTo(BLACK);
        themeFromApp.setTo(themeFromSystem);
        // themeFromApp now has style values from themeFromSystem, so now it has a black background...
        assertThat(themeFromApp.obtainStyledAttributes(new int[]{ colorBackground }).getColor(0, 123)).isEqualTo(WHITE);
    }

    @Test
    public void styleResolutionShouldIgnoreThemes() throws Exception {
        Resources.Theme themeFromSystem = resources.newTheme();
        themeFromSystem.applyStyle(Theme_DeviceDefault, true);
        themeFromSystem.applyStyle(ThemeWithSelfReferencingTextAttr, true);
        assertThat(themeFromSystem.obtainStyledAttributes(new int[]{ textAppearance }).getResourceId(0, 0)).isEqualTo(0);
    }
}

