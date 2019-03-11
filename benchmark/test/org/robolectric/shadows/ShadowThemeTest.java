package org.robolectric.shadows;


import android.R.attr.layout_height;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.testing.TestActivity;

import static android.R.attr.layout_height;
import static org.robolectric.R.attr.child_string;
import static org.robolectric.R.attr.parent_string;
import static org.robolectric.R.attr.string1;
import static org.robolectric.R.attr.string2;
import static org.robolectric.R.id.button;
import static org.robolectric.R.layout.styles_button_with_style_layout;
import static org.robolectric.R.style.SimpleChildWithAdditionalAttributes;
import static org.robolectric.R.style.SimpleChildWithOverride;
import static org.robolectric.R.style.SimpleParent;
import static org.robolectric.R.style.SimpleParent_ImplicitChild;
import static org.robolectric.R.style.StyleA;
import static org.robolectric.R.style.StyleB;
import static org.robolectric.R.style.Theme_Robolectric;
import static org.robolectric.R.style.Theme_Robolectric_EmptyParent;
import static org.robolectric.R.style.Theme_ThemeContainingStyleReferences;
import static org.robolectric.R.xml.temp;
import static org.robolectric.R.xml.temp_parent;


@RunWith(AndroidJUnit4.class)
public class ShadowThemeTest {
    private Resources resources;

    @Test
    public void whenExplicitlySetOnActivity_afterSetContentView_activityGetsThemeFromActivityInManifest() throws Exception {
        TestActivity activity = Robolectric.buildActivity(ShadowThemeTest.TestActivityWithAnotherTheme.class).create().get();
        setTheme(Theme_Robolectric);
        Button theButton = ((Button) (findViewById(button)));
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-65536);
    }

    @Test
    public void whenExplicitlySetOnActivity_beforeSetContentView_activityUsesNewTheme() throws Exception {
        ActivityController<ShadowThemeTest.TestActivityWithAnotherTheme> activityController = Robolectric.buildActivity(ShadowThemeTest.TestActivityWithAnotherTheme.class);
        TestActivity activity = activityController.get();
        setTheme(Theme_Robolectric);
        activityController.create();
        Button theButton = ((Button) (findViewById(button)));
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-16711936);
    }

    @Test
    public void whenSetOnActivityInManifest_activityGetsThemeFromActivityInManifest() throws Exception {
        TestActivity activity = Robolectric.buildActivity(ShadowThemeTest.TestActivityWithAnotherTheme.class).create().get();
        Button theButton = ((Button) (findViewById(button)));
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-65536);
    }

    @Test
    public void whenNotSetOnActivityInManifest_activityGetsThemeFromApplicationInManifest() throws Exception {
        TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().get();
        Button theButton = ((Button) (findViewById(button)));
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-16711936);
    }

    @Test
    public void shouldResolveReferencesThatStartWithAQuestionMark() throws Exception {
        TestActivity activity = Robolectric.buildActivity(ShadowThemeTest.TestActivityWithAnotherTheme.class).create().get();
        Button theButton = ((Button) (findViewById(button)));
        assertThat(theButton.getMinWidth()).isEqualTo(8);
        assertThat(theButton.getMinHeight()).isEqualTo(8);
    }

    @Test
    public void forStylesWithImplicitParents_shouldInheritValuesNotDefinedInChild() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(Theme_Robolectric_EmptyParent, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ string1 }).hasValue(0)).isFalse();
    }

    @Test
    public void shouldApplyParentStylesFromAttrs() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(SimpleParent, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ parent_string }).getString(0)).isEqualTo("parent string");
    }

    @Test
    public void applyStyle_shouldOverrideParentAttrs() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(SimpleChildWithOverride, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ parent_string }).getString(0)).isEqualTo("parent string overridden by child");
    }

    @Test
    public void applyStyle_shouldOverrideImplicitParentAttrs() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(SimpleParent_ImplicitChild, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ parent_string }).getString(0)).isEqualTo("parent string overridden by child");
    }

    @Test
    public void applyStyle_shouldInheritParentAttrs() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(SimpleChildWithAdditionalAttributes, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ child_string }).getString(0)).isEqualTo("child string");
        assertThat(theme.obtainStyledAttributes(new int[]{ parent_string }).getString(0)).isEqualTo("parent string");
    }

    @Test
    public void setTo_shouldCopyAllAttributesToEmptyTheme() throws Exception {
        Resources.Theme theme1 = resources.newTheme();
        theme1.applyStyle(Theme_Robolectric, false);
        assertThat(theme1.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from Theme.Robolectric");
        Resources.Theme theme2 = resources.newTheme();
        theme2.setTo(theme1);
        assertThat(theme2.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from Theme.Robolectric");
    }

    @Test
    public void setTo_whenDestThemeIsModified_sourceThemeShouldNotMutate() throws Exception {
        Resources.Theme sourceTheme = resources.newTheme();
        sourceTheme.applyStyle(StyleA, false);
        Resources.Theme destTheme = resources.newTheme();
        destTheme.setTo(sourceTheme);
        destTheme.applyStyle(StyleB, true);
        assertThat(destTheme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style B");
        assertThat(sourceTheme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style A");
    }

    @Test
    public void setTo_whenSourceThemeIsModified_destThemeShouldNotMutate() throws Exception {
        Resources.Theme sourceTheme = resources.newTheme();
        sourceTheme.applyStyle(StyleA, false);
        Resources.Theme destTheme = resources.newTheme();
        destTheme.setTo(sourceTheme);
        sourceTheme.applyStyle(StyleB, true);
        assertThat(destTheme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style A");
        assertThat(sourceTheme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style B");
    }

    @Test
    public void applyStyle_withForceFalse_shouldApplyButNotOverwriteExistingAttributeValues() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(StyleA, false);
        assertThat(theme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style A");
        theme.applyStyle(StyleB, false);
        assertThat(theme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style A");
    }

    @Test
    public void applyStyle_withForceTrue_shouldApplyAndOverwriteExistingAttributeValues() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(StyleA, false);
        assertThat(theme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style A");
        theme.applyStyle(StyleB, true);
        assertThat(theme.obtainStyledAttributes(new int[]{ string1 }).getString(0)).isEqualTo("string 1 from style B");
    }

    @Test
    public void whenStyleSpecifiesAttr_obtainStyledAttribute_findsCorrectValue() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(Theme_Robolectric, false);
        theme.applyStyle(Theme_ThemeContainingStyleReferences, true);
        assertThat(theme.obtainStyledAttributes(Robolectric.buildAttributeSet().setStyleAttribute("?attr/styleReference").build(), new int[]{ string2 }, 0, 0).getString(0)).isEqualTo("string 2 from YetAnotherStyle");
        assertThat(theme.obtainStyledAttributes(Robolectric.buildAttributeSet().setStyleAttribute("?styleReference").build(), new int[]{ string2 }, 0, 0).getString(0)).isEqualTo("string 2 from YetAnotherStyle");
    }

    @Test
    public void xml_whenStyleSpecifiesAttr_obtainStyledAttribute_findsCorrectValue() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(Theme_Robolectric, false);
        theme.applyStyle(Theme_ThemeContainingStyleReferences, true);
        assertThat(theme.obtainStyledAttributes(getFirstElementAttrSet(temp), new int[]{ string2 }, 0, 0).getString(0)).isEqualTo("string 2 from YetAnotherStyle");
        assertThat(theme.obtainStyledAttributes(getFirstElementAttrSet(temp_parent), new int[]{ string2 }, 0, 0).getString(0)).isEqualTo("string 2 from YetAnotherStyle");
    }

    @Test
    public void whenAttrSetAttrSpecifiesAttr_obtainStyledAttribute_returnsItsValue() throws Exception {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(Theme_Robolectric, false);
        theme.applyStyle(Theme_ThemeContainingStyleReferences, true);
        assertThat(theme.obtainStyledAttributes(Robolectric.buildAttributeSet().addAttribute(string2, "?attr/string1").build(), new int[]{ string2 }, 0, 0).getString(0)).isEqualTo("string 1 from Theme.Robolectric");
    }

    @Test
    public void dimenRef() throws Exception {
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(layout_height, "@dimen/test_px_dimen").build();
        TypedArray typedArray = resources.newTheme().obtainStyledAttributes(attributeSet, new int[]{ layout_height }, 0, 0);
        assertThat(typedArray.getDimensionPixelSize(0, (-1))).isEqualTo(15);
    }

    @Test
    public void dimenRefRef() throws Exception {
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(layout_height, "@dimen/ref_to_px_dimen").build();
        TypedArray typedArray = resources.newTheme().obtainStyledAttributes(attributeSet, new int[]{ layout_height }, 0, 0);
        assertThat(typedArray.getDimensionPixelSize(0, (-1))).isEqualTo(15);
    }

    @Test
    public void obtainStyledAttributes_shouldFindAttributeInDefaultStyle() throws Exception {
        Theme theme = resources.newTheme();
        TypedArray typedArray = theme.obtainStyledAttributes(StyleA, new int[]{ string1 });
        assertThat(typedArray.getString(0)).isEqualTo("string 1 from style A");
    }

    @Test
    public void shouldApplyFromStyleAttribute() throws Exception {
        ShadowThemeTest.TestWithStyleAttrActivity activity = Robolectric.buildActivity(ShadowThemeTest.TestWithStyleAttrActivity.class).create().get();
        View button = activity.findViewById(button);
        assertThat(button.getLayoutParams().width).isEqualTo(42);// comes via style attr

    }

    public static class TestActivityWithAnotherTheme extends TestActivity {}

    public static class TestWithStyleAttrActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(styles_button_with_style_layout);
        }
    }
}

