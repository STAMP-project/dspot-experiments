package android.content.res;


import Build.VERSION_CODES;
import Color.BLACK;
import Color.MAGENTA;
import R.anim.animation_list;
import R.anim.test_anim_1;
import R.array.empty_int_array;
import R.array.greetings;
import R.array.items;
import R.array.referenced_colors_int_array;
import R.array.string_array_values;
import R.array.typed_array_references;
import R.array.typed_array_values;
import R.array.typed_array_with_resource_id;
import R.array.with_references_int_array;
import R.array.zero_to_four_int_array;
import R.bool.false_bool_value;
import R.bool.reference_to_true;
import R.bool.true_as_item;
import R.bool.true_bool_value;
import R.color.background;
import R.color.blue;
import R.color.color_state_list;
import R.color.color_with_alpha;
import R.color.white;
import R.dimen.test_dip_dimen;
import R.dimen.test_dp_dimen;
import R.dimen.test_in_dimen;
import R.dimen.test_mm_dimen;
import R.dimen.test_pt_dimen;
import R.dimen.test_px_dimen;
import R.dimen.test_sp_dimen;
import R.drawable.an_image;
import R.drawable.nine_patch_drawable;
import R.drawable.rainbow;
import R.drawable.vector;
import R.font.downloadable;
import R.font.vt323;
import R.font.vt323_regular;
import R.fraction.fifth_as_reference;
import R.fraction.fifth_of_parent_as_reference;
import R.fraction.half;
import R.fraction.half_of_parent;
import R.fraction.quarter_as_item;
import R.fraction.quarter_of_parent_as_item;
import R.id.id_declared_in_item_tag;
import R.id.id_declared_in_layout;
import R.integer.loneliest_number;
import R.integer.meaning_of_life;
import R.integer.meaning_of_life_as_item;
import R.integer.reference_to_meaning_of_life;
import R.integer.test_integer1;
import R.integer.test_integer2;
import R.integer.test_large_hex;
import R.integer.test_value_with_zero;
import R.layout.activity_main;
import R.layout.custom_layout;
import R.layout.different_screen_sizes;
import R.layout.multiline_layout;
import R.menu.test;
import R.mipmap.mipmap_reference;
import R.mipmap.mipmap_reference_xml;
import R.mipmap.robolectric;
import R.mipmap.robolectric_xml;
import R.plurals.beer;
import R.raw.raw_resource;
import R.string.bad_example;
import R.string.escaped_apostrophe;
import R.string.escaped_quotes;
import R.string.font_tag_with_attribute;
import R.string.greeting;
import R.string.hello;
import R.string.internal_newlines;
import R.string.internal_whitespace_blocks;
import R.string.interpolate;
import R.string.leading_and_trailing_new_lines;
import R.string.link_tag_with_attribute;
import R.string.new_lines_and_tabs;
import R.string.non_breaking_space;
import R.string.say_it_with_item;
import R.string.some_html;
import R.string.space;
import R.string.string_with_spaces;
import R.string.surrounding_quotes;
import R.style.MyBlackTheme;
import R.style.MyBlueTheme;
import R.style.Theme_Robolectric;
import R.xml.preferences;
import Resources.NotFoundException;
import Resources.Theme;
import XmlResourceParser.START_DOCUMENT;
import XmlResourceParser.START_TAG;
import android.R.attr;
import android.R.attr.viewportHeight;
import android.R.attr.windowBackground;
import android.R.color.black;
import android.R.color.darker_gray;
import android.R.string.copy;
import android.R.style.TextAppearance_Small;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.test.filters.SdkSuppress;
import androidx.test.runner.AndroidJUnit4;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link Resources}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class ResourcesTest {
    private Resources resources;

    private Context context;

    @Test
    public void getString() throws Exception {
        assertThat(resources.getString(hello)).isEqualTo("Hello");
        assertThat(resources.getString(say_it_with_item)).isEqualTo("flowers");
    }

    @Test
    public void getString_withReference() throws Exception {
        assertThat(resources.getString(greeting)).isEqualTo("Howdy");
    }

    @Test
    public void getString_withInterpolation() throws Exception {
        assertThat(resources.getString(interpolate, "value")).isEqualTo("Here is a value!");
    }

    @Test
    public void getString_withHtml() throws Exception {
        assertThat(resources.getString(some_html, "value")).isEqualTo("Hello, world");
    }

    @Test
    public void getString_withSurroundingQuotes() throws Exception {
        assertThat(resources.getString(surrounding_quotes, "value")).isEqualTo("This'll work");
    }

    @Test
    public void getStringWithEscapedApostrophes() throws Exception {
        assertThat(resources.getString(escaped_apostrophe)).isEqualTo("This'll also work");
    }

    @Test
    public void getStringWithEscapedQuotes() throws Exception {
        assertThat(resources.getString(escaped_quotes)).isEqualTo("Click \"OK\"");
    }

    @Test
    public void getString_StringWithInlinedQuotesAreStripped() throws Exception {
        assertThat(resources.getString(bad_example)).isEqualTo("This is a bad string.");
    }

    @Test
    public void getStringShouldStripNewLines() {
        assertThat(resources.getString(leading_and_trailing_new_lines)).isEqualTo("Some text");
    }

    @Test
    public void preserveEscapedNewlineAndTab() {
        assertThat(resources.getString(new_lines_and_tabs, 4)).isEqualTo("4\tmph\nfaster");
    }

    @Test
    public void getStringShouldConvertCodePoints() {
        assertThat(resources.getString(non_breaking_space)).isEqualTo("Closing soon:\u00a05pm");
        assertThat(resources.getString(space)).isEqualTo("Closing soon: 5pm");
    }

    @Test
    public void getMultilineLayoutResource_shouldResolveLayoutReferencesWithLineBreaks() {
        // multiline_layout is a layout reference to activity_main layout.
        TypedValue multilineLayoutValue = new TypedValue();
        /* resolveRefs */
        resources.getValue(multiline_layout, multilineLayoutValue, true);
        TypedValue mainActivityLayoutValue = new TypedValue();
        /* resolveRefs */
        resources.getValue(activity_main, mainActivityLayoutValue, false);
        assertThat(multilineLayoutValue.string).isEqualTo(mainActivityLayoutValue.string);
    }

    @Test
    public void getText_withHtml() throws Exception {
        assertThat(resources.getText(some_html, "value").toString()).isEqualTo("Hello, world");
        // TODO: Raw resources have lost the tags early, but the following call should return a
        // SpannedString
        // assertThat(resources.getText(R.string.some_html)).isInstanceOf(SpannedString.class);
    }

    @Test
    public void getText_plainString() throws Exception {
        assertThat(resources.getText(hello, "value").toString()).isEqualTo("Hello");
        assertThat(resources.getText(hello)).isInstanceOf(String.class);
    }

    @Test
    public void getText_withLayoutId() throws Exception {
        // This isn't _really_ supported by the platform (gives a lint warning that getText() expects a String resource type
        // but the actual platform behaviour is to return a string that equals "res/layout/layout_file.xml" so the current
        // Robolectric behaviour deviates from the platform as we append the full file path from the current working directory.
        assertThat(resources.getText(different_screen_sizes, "value").toString()).containsMatch("layout/different_screen_sizes.xml$");
    }

    @Test
    public void getStringArray() throws Exception {
        assertThat(resources.getStringArray(items)).isEqualTo(new String[]{ "foo", "bar" });
        assertThat(resources.getStringArray(greetings)).isEqualTo(new String[]{ "hola", "Hello" });
    }

    @Test
    public void withIdReferenceEntry_obtainTypedArray() {
        TypedArray typedArray = resources.obtainTypedArray(typed_array_with_resource_id);
        assertThat(typedArray.length()).isEqualTo(2);
        assertThat(typedArray.getResourceId(0, 0)).isEqualTo(id_declared_in_item_tag);
        assertThat(typedArray.getResourceId(1, 0)).isEqualTo(id_declared_in_layout);
    }

    @Test
    public void obtainTypedArray() throws Exception {
        final TypedArray valuesTypedArray = resources.obtainTypedArray(typed_array_values);
        assertThat(valuesTypedArray.getString(0)).isEqualTo("abcdefg");
        assertThat(valuesTypedArray.getInt(1, 0)).isEqualTo(3875);
        assertThat(valuesTypedArray.getInteger(1, 0)).isEqualTo(3875);
        assertThat(valuesTypedArray.getFloat(2, 0.0F)).isEqualTo(2.0F);
        assertThat(valuesTypedArray.getColor(3, BLACK)).isEqualTo(MAGENTA);
        assertThat(valuesTypedArray.getColor(4, BLACK)).isEqualTo(Color.parseColor("#00ffff"));
        assertThat(valuesTypedArray.getDimension(5, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_PX, 8, resources.getDisplayMetrics()));
        assertThat(valuesTypedArray.getDimension(6, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_DIP, 12, resources.getDisplayMetrics()));
        assertThat(valuesTypedArray.getDimension(7, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_DIP, 6, resources.getDisplayMetrics()));
        assertThat(valuesTypedArray.getDimension(8, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_MM, 3, resources.getDisplayMetrics()));
        assertThat(valuesTypedArray.getDimension(9, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_IN, 4, resources.getDisplayMetrics()));
        assertThat(valuesTypedArray.getDimension(10, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_SP, 36, resources.getDisplayMetrics()));
        assertThat(valuesTypedArray.getDimension(11, 0.0F)).isEqualTo(applyDimension(COMPLEX_UNIT_PT, 18, resources.getDisplayMetrics()));
        final TypedArray refsTypedArray = resources.obtainTypedArray(typed_array_references);
        assertThat(refsTypedArray.getString(0)).isEqualTo("apple");
        assertThat(refsTypedArray.getString(1)).isEqualTo("banana");
        assertThat(refsTypedArray.getInt(2, 0)).isEqualTo(5);
        assertThat(refsTypedArray.getBoolean(3, false)).isTrue();
        assertThat(refsTypedArray.getResourceId(8, 0)).isEqualTo(string_array_values);
        assertThat(refsTypedArray.getTextArray(8)).asList().containsAllOf("abcdefg", "3875", "2.0", "#ffff00ff", "#00ffff", "8px", "12dp", "6dip", "3mm", "4in", "36sp", "18pt");
        assertThat(refsTypedArray.getResourceId(9, 0)).isEqualTo(Theme_Robolectric);
    }

    @Test
    public void getInt() throws Exception {
        assertThat(resources.getInteger(meaning_of_life)).isEqualTo(42);
        assertThat(resources.getInteger(test_integer1)).isEqualTo(2000);
        assertThat(resources.getInteger(test_integer2)).isEqualTo(9);
        assertThat(resources.getInteger(test_large_hex)).isEqualTo((-65536));
        assertThat(resources.getInteger(test_value_with_zero)).isEqualTo(7210);
        assertThat(resources.getInteger(meaning_of_life_as_item)).isEqualTo(42);
    }

    @Test
    public void getInt_withReference() throws Exception {
        assertThat(resources.getInteger(reference_to_meaning_of_life)).isEqualTo(42);
    }

    @Test
    public void getIntArray() throws Exception {
        assertThat(resources.getIntArray(empty_int_array)).isEqualTo(new int[]{  });
        assertThat(resources.getIntArray(zero_to_four_int_array)).isEqualTo(new int[]{ 0, 1, 2, 3, 4 });
        assertThat(resources.getIntArray(with_references_int_array)).isEqualTo(new int[]{ 0, 2000, 1 });
        assertThat(resources.getIntArray(referenced_colors_int_array)).isEqualTo(new int[]{ 1, -1, -16777216, -657931, -2144569683 });
    }

    @Test
    public void getBoolean() throws Exception {
        assertThat(resources.getBoolean(false_bool_value)).isEqualTo(false);
        assertThat(resources.getBoolean(true_as_item)).isEqualTo(true);
    }

    @Test
    public void getBoolean_withReference() throws Exception {
        assertThat(resources.getBoolean(reference_to_true)).isEqualTo(true);
    }

    @Test
    public void getDimension() throws Exception {
        assertThat(resources.getDimension(test_dip_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_DIP, 20, resources.getDisplayMetrics()));
        assertThat(resources.getDimension(test_dp_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_DIP, 8, resources.getDisplayMetrics()));
        assertThat(resources.getDimension(test_in_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_IN, 99, resources.getDisplayMetrics()));
        assertThat(resources.getDimension(test_mm_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_MM, 42, resources.getDisplayMetrics()));
        assertThat(resources.getDimension(test_px_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_PX, 15, resources.getDisplayMetrics()));
        assertThat(resources.getDimension(test_pt_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_PT, 12, resources.getDisplayMetrics()));
        assertThat(resources.getDimension(test_sp_dimen)).isEqualTo(applyDimension(COMPLEX_UNIT_SP, 5, resources.getDisplayMetrics()));
    }

    @Test
    public void getDimensionPixelSize() throws Exception {
        assertThat(resources.getDimensionPixelSize(test_dip_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_DIP, 20)));
        assertThat(resources.getDimensionPixelSize(test_dp_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_DIP, 8)));
        assertThat(resources.getDimensionPixelSize(test_in_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_IN, 99)));
        assertThat(resources.getDimensionPixelSize(test_mm_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_MM, 42)));
        assertThat(resources.getDimensionPixelSize(test_px_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_PX, 15)));
        assertThat(resources.getDimensionPixelSize(test_pt_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_PT, 12)));
        assertThat(resources.getDimensionPixelSize(test_sp_dimen)).isIn(ResourcesTest.onePixelOf(convertDimension(COMPLEX_UNIT_SP, 5)));
    }

    @Test
    public void getDimensionPixelOffset() {
        assertThat(resources.getDimensionPixelOffset(test_dip_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_DIP, 20));
        assertThat(resources.getDimensionPixelOffset(test_dp_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_DIP, 8));
        assertThat(resources.getDimensionPixelOffset(test_in_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_IN, 99));
        assertThat(resources.getDimensionPixelOffset(test_mm_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_MM, 42));
        assertThat(resources.getDimensionPixelOffset(test_px_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_PX, 15));
        assertThat(resources.getDimensionPixelOffset(test_pt_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_PT, 12));
        assertThat(resources.getDimensionPixelOffset(test_sp_dimen)).isEqualTo(convertDimension(COMPLEX_UNIT_SP, 5));
    }

    @Test
    public void getDimension_withReference() {
        assertThat(resources.getBoolean(reference_to_true)).isEqualTo(true);
    }

    @Test(expected = NotFoundException.class)
    public void getStringArray_shouldThrowExceptionIfNotFound() {
        resources.getStringArray((-1));
    }

    @Test(expected = NotFoundException.class)
    public void getIntegerArray_shouldThrowExceptionIfNotFound() {
        resources.getIntArray((-1));
    }

    @Test
    public void getQuantityString() {
        assertThat(resources.getQuantityString(beer, 1)).isEqualTo("a beer");
        assertThat(resources.getQuantityString(beer, 2)).isEqualTo("some beers");
        assertThat(resources.getQuantityString(beer, 3)).isEqualTo("some beers");
    }

    @Test
    public void getQuantityText() {
        // Feature not supported in legacy (raw) resource mode.
        Assume.assumeFalse(ResourcesTest.isRobolectricLegacyMode());
        assertThat(resources.getQuantityText(beer, 1)).isEqualTo("a beer");
        assertThat(resources.getQuantityText(beer, 2)).isEqualTo("some beers");
        assertThat(resources.getQuantityText(beer, 3)).isEqualTo("some beers");
    }

    @Test
    public void getFraction() {
        final int myself = 300;
        final int myParent = 600;
        assertThat(resources.getFraction(half, myself, myParent)).isEqualTo(150.0F);
        assertThat(resources.getFraction(half_of_parent, myself, myParent)).isEqualTo(300.0F);
        assertThat(resources.getFraction(quarter_as_item, myself, myParent)).isEqualTo(75.0F);
        assertThat(resources.getFraction(quarter_of_parent_as_item, myself, myParent)).isEqualTo(150.0F);
        assertThat(resources.getFraction(fifth_as_reference, myself, myParent)).isWithin(0.01F).of(60.0F);
        assertThat(resources.getFraction(fifth_of_parent_as_reference, myself, myParent)).isWithin(0.01F).of(120.0F);
    }

    @Test
    public void testConfiguration() {
        Configuration configuration = resources.getConfiguration();
        assertThat(configuration).isNotNull();
        assertThat(configuration.locale).isNotNull();
    }

    @Test
    public void testConfigurationReturnsTheSameInstance() {
        assertThat(resources.getConfiguration()).isSameAs(resources.getConfiguration());
    }

    @Test
    public void testNewTheme() {
        assertThat(resources.newTheme()).isNotNull();
    }

    @Test(expected = NotFoundException.class)
    public void testGetDrawableNullRClass() {
        assertThat(resources.getDrawable((-12345))).isInstanceOf(BitmapDrawable.class);
    }

    @Test
    public void testGetAnimationDrawable() {
        assertThat(resources.getDrawable(animation_list)).isInstanceOf(AnimationDrawable.class);
    }

    @Test
    public void testGetColorDrawable() {
        Drawable drawable = resources.getDrawable(color_with_alpha);
        assertThat(drawable).isInstanceOf(ColorDrawable.class);
        assertThat(((ColorDrawable) (drawable)).getColor()).isEqualTo(-2144569683);
    }

    @Test
    public void getColor() {
        assertThat(resources.getColor(color_with_alpha)).isEqualTo(-2144569683);
    }

    @Test
    public void getColor_withReference() {
        assertThat(resources.getColor(background)).isEqualTo(-657931);
    }

    @Test(expected = NotFoundException.class)
    public void testGetColor_Missing() {
        resources.getColor(11234);
    }

    @Test
    public void testGetColorStateList() {
        assertThat(resources.getColorStateList(color_state_list)).isInstanceOf(ColorStateList.class);
    }

    @Test
    public void testGetBitmapDrawable() {
        assertThat(resources.getDrawable(an_image)).isInstanceOf(BitmapDrawable.class);
    }

    @Test
    public void testGetNinePatchDrawable() {
        assertThat(resources.getDrawable(nine_patch_drawable)).isInstanceOf(NinePatchDrawable.class);
    }

    @Test(expected = NotFoundException.class)
    public void testGetBitmapDrawableForUnknownId() {
        assertThat(resources.getDrawable(Integer.MAX_VALUE)).isInstanceOf(BitmapDrawable.class);
    }

    @Test
    public void testGetIdentifier() throws Exception {
        final String resourceType = "string";
        final String packageName = context.getPackageName();
        final String resourceName = "hello";
        final int resId1 = resources.getIdentifier(resourceName, resourceType, packageName);
        assertThat(resId1).isEqualTo(hello);
        final String typedResourceName = (resourceType + "/") + resourceName;
        final int resId2 = resources.getIdentifier(typedResourceName, resourceType, packageName);
        assertThat(resId2).isEqualTo(hello);
        final String fqn = (packageName + ":") + typedResourceName;
        final int resId3 = resources.getIdentifier(fqn, resourceType, packageName);
        assertThat(resId3).isEqualTo(hello);
    }

    @Test
    public void getIdentifier() {
        String string = resources.getString(hello);
        assertThat(string).isEqualTo("Hello");
        int id = resources.getIdentifier("hello", "string", context.getPackageName());
        assertThat(id).isEqualTo(hello);
        String hello = resources.getString(id);
        assertThat(hello).isEqualTo("Hello");
    }

    @Test
    public void getIdentifier_nonExistantResource() {
        int id = resources.getIdentifier("just_alot_of_crap", "string", context.getPackageName());
        assertThat(id).isEqualTo(0);
    }

    /**
     * Public framework symbols are defined here: https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/values/public.xml
     * Private framework symbols are defined here: https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/values/symbols.xml
     *
     * These generate android.R and com.android.internal.R respectively, when Framework Java code does not need to reference a framework resource
     * it will not have an R value generated. Robolectric is then missing an identifier for this resource so we must generate a placeholder ourselves.
     */
    // @Config(sdk = Build.VERSION_CODES.LOLLIPOP) // android:color/secondary_text_material_dark was added in API 21
    @Test
    @SdkSuppress(minSdkVersion = LOLLIPOP)
    @Config(minSdk = LOLLIPOP)
    public void shouldGenerateIdsForResourcesThatAreMissingRValues() throws Exception {
        int identifier_missing_from_r_file = resources.getIdentifier("secondary_text_material_dark", "color", "android");
        // We expect Robolectric to generate a placeholder identifier where one was not generated in the android R files.
        assertThat(identifier_missing_from_r_file).isNotEqualTo(0);
        // We expect to be able to successfully android:color/secondary_text_material_dark to a ColorStateList.
        assertThat(resources.getColorStateList(identifier_missing_from_r_file)).isNotNull();
    }

    @Test
    public void getSystemShouldReturnSystemResources() throws Exception {
        assertThat(Resources.getSystem()).isInstanceOf(Resources.class);
    }

    @Test
    public void multipleCallsToGetSystemShouldReturnSameInstance() throws Exception {
        assertThat(Resources.getSystem()).isEqualTo(Resources.getSystem());
    }

    @Test
    public void applicationResourcesShouldHaveBothSystemAndLocalValues() throws Exception {
        assertThat(context.getResources().getString(copy)).isEqualTo("Copy");
        assertThat(context.getResources().getString(R.string.copy)).isEqualTo("Local Copy");
    }

    @Test
    public void systemResourcesShouldReturnCorrectSystemId() throws Exception {
        assertThat(Resources.getSystem().getIdentifier("copy", "string", "android")).isEqualTo(copy);
    }

    @Test
    public void systemResourcesShouldReturnZeroForLocalId() throws Exception {
        assertThat(Resources.getSystem().getIdentifier("copy", "string", context.getPackageName())).isEqualTo(0);
    }

    @Test
    public void testGetXml() throws Exception {
        XmlResourceParser parser = resources.getXml(preferences);
        assertThat(parser).isNotNull();
        assertThat(ResourcesTest.findRootTag(parser)).isEqualTo("PreferenceScreen");
        parser = resources.getXml(custom_layout);
        assertThat(parser).isNotNull();
        assertThat(ResourcesTest.findRootTag(parser)).isEqualTo("org.robolectric.android.CustomView");
        parser = resources.getXml(test);
        assertThat(parser).isNotNull();
        assertThat(ResourcesTest.findRootTag(parser)).isEqualTo("menu");
        parser = resources.getXml(rainbow);
        assertThat(parser).isNotNull();
        assertThat(ResourcesTest.findRootTag(parser)).isEqualTo("layer-list");
        parser = resources.getXml(test_anim_1);
        assertThat(parser).isNotNull();
        assertThat(ResourcesTest.findRootTag(parser)).isEqualTo("set");
        parser = resources.getXml(color_state_list);
        assertThat(parser).isNotNull();
        assertThat(ResourcesTest.findRootTag(parser)).isEqualTo("selector");
    }

    @Test(expected = NotFoundException.class)
    public void testGetXml_nonexistentResource() {
        resources.getXml(0);
    }

    @Test(expected = NotFoundException.class)
    public void testGetXml_nonxmlfile() {
        resources.getXml(an_image);
    }

    @Test
    public void openRawResource_shouldLoadRawResources() throws Exception {
        InputStream resourceStream = resources.openRawResource(raw_resource);
        assertThat(resourceStream).isNotNull();
        // assertThat(TestUtil.readString(resourceStream)).isEqualTo("raw txt file contents");
    }

    @Test
    public void openRawResource_shouldLoadDrawables() throws Exception {
        InputStream resourceStream = resources.openRawResource(an_image);
        Bitmap bitmap = BitmapFactory.decodeStream(resourceStream);
        assertThat(bitmap.getHeight()).isEqualTo(53);
        assertThat(bitmap.getWidth()).isEqualTo(64);
    }

    @Test
    public void openRawResource_withNonFile_throwsNotFoundException() throws Exception {
        try {
            resources.openRawResource(hello);
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
        try {
            resources.openRawResource(hello, new TypedValue());
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
        try {
            resources.openRawResource((-1234), new TypedValue());
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
    }

    @Test
    public void openRawResourceFd_withNonFile_throwsNotFoundException() throws Exception {
        try {
            resources.openRawResourceFd(hello);
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
        try {
            resources.openRawResourceFd((-1234));
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
    }

    @Test
    public void getXml_withNonFile_throwsNotFoundException() throws Exception {
        try {
            resources.getXml(hello);
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
        try {
            resources.getXml((-1234));
            Assert.fail("should throw");
        } catch (Resources e) {
            // cool
        }
    }

    @Test
    public void themeResolveAttribute_shouldSupportNotDereferencingResource() {
        TypedValue out = new TypedValue();
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(MyBlackTheme, false);
        theme.resolveAttribute(windowBackground, out, false);
        assertThat(out.type).isEqualTo(TYPE_REFERENCE);
        assertThat(out.data).isEqualTo(black);
    }

    // @Test
    // public void obtainAttributes_shouldUseReferencedIdFromAttributeSet() throws Exception {
    // // android:id/mask was introduced in API 21, but it's still possible for apps built against API 21 to refer to it
    // // in older runtimes because referenced resource ids are compiled (by aapt) into the binary XML format.
    // AttributeSet attributeSet = Robolectric.buildAttributeSet()
    // .addAttribute(android.R.attr.id, "@android:id/mask").build();
    // TypedArray typedArray = resources.obtainAttributes(attributeSet, new int[]{android.R.attr.id});
    // assertThat(typedArray.getResourceId(0, -9)).isEqualTo(android.R.id.mask);
    // }
    // 
    // @Test
    // public void obtainAttributes() {
    // TypedArray typedArray = resources.obtainAttributes(Robolectric.buildAttributeSet()
    // .addAttribute(R.attr.styleReference, "@xml/shortcuts")
    // .build(), new int[]{R.attr.styleReference});
    // assertThat(typedArray).isNotNull();
    // assertThat(typedArray.peekValue(0).resourceId).isEqualTo(R.xml.shortcuts);
    // }
    @Test
    public void obtainStyledAttributesShouldDereferenceValues() {
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(MyBlackTheme, false);
        TypedArray arr = theme.obtainStyledAttributes(new int[]{ attr.windowBackground });
        TypedValue value = new TypedValue();
        arr.getValue(0, value);
        arr.recycle();
        assertThat(value.type).isAtLeast(TYPE_FIRST_COLOR_INT);
        assertThat(value.type).isAtMost(TYPE_LAST_INT);
    }

    // @Test
    // public void obtainStyledAttributes_shouldCheckXmlFirst_fromAttributeSetBuilder() throws Exception {
    // 
    // // This simulates a ResourceProvider built from a 21+ SDK as viewportHeight / viewportWidth were introduced in API 21
    // // but the public ID values they are assigned clash with private com.android.internal.R values on older SDKs. This
    // // test ensures that even on older SDKs, on calls to obtainStyledAttributes() Robolectric will first check for matching
    // // resource ID values in the AttributeSet before checking the theme.
    // 
    // AttributeSet attributes = Robolectric.buildAttributeSet()
    // .addAttribute(android.R.attr.viewportWidth, "12.0")
    // .addAttribute(android.R.attr.viewportHeight, "24.0")
    // .build();
    // 
    // TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributes, new int[] {
    // android.R.attr.viewportWidth,
    // android.R.attr.viewportHeight
    // }, 0, 0);
    // assertThat(typedArray.getFloat(0, 0)).isEqualTo(12.0f);
    // assertThat(typedArray.getFloat(1, 0)).isEqualTo(24.0f);
    // typedArray.recycle();
    // }
    @Test
    public void obtainStyledAttributes_shouldCheckXmlFirst_fromXmlLoadedFromResources() throws Exception {
        // This simulates a ResourceProvider built from a 21+ SDK as viewportHeight / viewportWidth were introduced in API 21
        // but the public ID values they are assigned clash with private com.android.internal.R values on older SDKs. This
        // test ensures that even on older SDKs, on calls to obtainStyledAttributes() Robolectric will first check for matching
        // resource ID values in the AttributeSet before checking the theme.
        XmlResourceParser xml = context.getResources().getXml(vector);
        xml.next();
        xml.next();
        AttributeSet attributeSet = Xml.asAttributeSet(xml);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, new int[]{ attr.viewportWidth, attr.viewportHeight }, 0, 0);
        assertThat(typedArray.getFloat(0, 0)).isEqualTo(12.0F);
        assertThat(typedArray.getFloat(1, 0)).isEqualTo(24.0F);
        typedArray.recycle();
    }

    // @Test
    // public void obtainStyledAttributesShouldCheckXmlFirst_andFollowReferences() throws Exception {
    // 
    // // This simulates a ResourceProvider built from a 21+ SDK as viewportHeight / viewportWidth were introduced in API 21
    // // but the public ID values they are assigned clash with private com.android.internal.R values on older SDKs. This
    // // test ensures that even on older SDKs, on calls to obtainStyledAttributes() Robolectric will first check for matching
    // // resource ID values in the AttributeSet before checking the theme.
    // 
    // AttributeSet attributes = Robolectric.buildAttributeSet()
    // .addAttribute(android.R.attr.viewportWidth, "@integer/test_integer1")
    // .addAttribute(android.R.attr.viewportHeight, "@integer/test_integer2")
    // .build();
    // 
    // TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributes, new int[] {
    // android.R.attr.viewportWidth,
    // android.R.attr.viewportHeight
    // }, 0, 0);
    // assertThat(typedArray.getFloat(0, 0)).isEqualTo(2000);
    // assertThat(typedArray.getFloat(1, 0)).isEqualTo(9);
    // typedArray.recycle();
    // }
    @Test
    @SdkSuppress(minSdkVersion = LOLLIPOP)
    @Config(minSdk = LOLLIPOP)
    public void whenAttrIsDefinedInRuntimeSdk_getResourceName_findsResource() {
        assertThat(context.getResources().getResourceName(viewportHeight)).isEqualTo("android:attr/viewportHeight");
    }

    @Test
    @SdkSuppress(maxSdkVersion = KITKAT)
    @Config(maxSdk = KITKAT_WATCH)
    public void whenAttrIsNotDefinedInRuntimeSdk_getResourceName_doesntFindRequestedResourceButInsteadFindsInternalResourceWithSameId() {
        // asking for an attr defined after the current SDK doesn't have a defined result; in this case it returns
        // numberPickerStyle from com.internal.android.R
        assertThat(context.getResources().getResourceName(viewportHeight)).isNotEqualTo("android:attr/viewportHeight");
        assertThat(context.getResources().getIdentifier("viewportHeight", "attr", "android")).isEqualTo(0);
    }

    @Test
    public void subClassInitializedOK() {
        ResourcesTest.SubClassResources subClassResources = new ResourcesTest.SubClassResources(resources);
        assertThat(subClassResources.openRawResource(raw_resource)).isNotNull();
    }

    @Test
    public void applyStyleForced() {
        final Resources.Theme theme = resources.newTheme();
        theme.applyStyle(MyBlackTheme, true);
        TypedArray arr = theme.obtainStyledAttributes(new int[]{ attr.windowBackground, attr.textColorHint });
        final TypedValue blackBackgroundColor = new TypedValue();
        arr.getValue(0, blackBackgroundColor);
        assertThat(blackBackgroundColor.resourceId).isEqualTo(black);
        arr.recycle();
        theme.applyStyle(MyBlueTheme, true);
        arr = theme.obtainStyledAttributes(new int[]{ attr.windowBackground, attr.textColor, attr.textColorHint });
        final TypedValue blueBackgroundColor = new TypedValue();
        arr.getValue(0, blueBackgroundColor);
        assertThat(blueBackgroundColor.resourceId).isEqualTo(blue);
        final TypedValue blueTextColor = new TypedValue();
        arr.getValue(1, blueTextColor);
        assertThat(blueTextColor.resourceId).isEqualTo(white);
        final TypedValue blueTextColorHint = new TypedValue();
        arr.getValue(2, blueTextColorHint);
        assertThat(blueTextColorHint.resourceId).isEqualTo(darker_gray);
        arr.recycle();
    }

    @Test
    public void applyStyleNotForced() {
        final Resources.Theme theme = resources.newTheme();
        // Apply black theme
        theme.applyStyle(MyBlackTheme, true);
        TypedArray arr = theme.obtainStyledAttributes(new int[]{ attr.windowBackground, attr.textColorHint });
        final TypedValue blackBackgroundColor = new TypedValue();
        arr.getValue(0, blackBackgroundColor);
        assertThat(blackBackgroundColor.resourceId).isEqualTo(black);
        final TypedValue blackTextColorHint = new TypedValue();
        arr.getValue(1, blackTextColorHint);
        assertThat(blackTextColorHint.resourceId).isEqualTo(darker_gray);
        arr.recycle();
        // Apply blue theme
        theme.applyStyle(MyBlueTheme, false);
        arr = theme.obtainStyledAttributes(new int[]{ attr.windowBackground, attr.textColor, attr.textColorHint });
        final TypedValue blueBackgroundColor = new TypedValue();
        arr.getValue(0, blueBackgroundColor);
        assertThat(blueBackgroundColor.resourceId).isEqualTo(black);
        final TypedValue blueTextColor = new TypedValue();
        arr.getValue(1, blueTextColor);
        assertThat(blueTextColor.resourceId).isEqualTo(white);
        final TypedValue blueTextColorHint = new TypedValue();
        arr.getValue(2, blueTextColorHint);
        assertThat(blueTextColorHint.resourceId).isEqualTo(darker_gray);
        arr.recycle();
    }

    @Test
    public void getValueShouldClearTypedArrayBetweenCalls() throws Exception {
        TypedValue outValue = new TypedValue();
        resources.getValue(hello, outValue, true);
        assertThat(outValue.type).isEqualTo(TYPE_STRING);
        assertThat(outValue.string).isEqualTo(resources.getString(hello));
        // outValue.data is an index into the String block which we don't know for raw xml resources.
        assertThat(outValue.assetCookie).isNotEqualTo(0);
        resources.getValue(blue, outValue, true);
        assertThat(outValue.type).isEqualTo(TYPE_INT_COLOR_RGB8);
        assertThat(outValue.data).isEqualTo(-16776961);
        assertThat(outValue.string).isNull();
        // outValue.assetCookie is not supported with raw XML
        resources.getValue(loneliest_number, outValue, true);
        assertThat(outValue.type).isEqualTo(TYPE_INT_DEC);
        assertThat(outValue.data).isEqualTo(1);
        assertThat(outValue.string).isNull();
        resources.getValue(true_bool_value, outValue, true);
        assertThat(outValue.type).isEqualTo(TYPE_INT_BOOLEAN);
        assertThat(outValue.data).isNotEqualTo(0);// true == traditionally 0xffffffff, -1 in Java but

        // tests should be checking for non-zero
        assertThat(outValue.string).isNull();
    }

    @Test
    public void getXml() throws Exception {
        XmlResourceParser xmlResourceParser = resources.getXml(preferences);
        assertThat(xmlResourceParser).isNotNull();
        assertThat(xmlResourceParser.next()).isEqualTo(START_DOCUMENT);
        assertThat(xmlResourceParser.next()).isEqualTo(START_TAG);
        assertThat(xmlResourceParser.getName()).isEqualTo("PreferenceScreen");
    }

    @Test
    public void whenMissingXml_throwNotFoundException() throws Exception {
        try {
            resources.getXml(12344);
            Assert.fail();
        } catch (Resources e) {
            assertThat(e.getMessage()).contains("Resource ID #0x3038");
        }
    }

    @Test
    public void stringWithSpaces() throws Exception {
        // this differs from actual Android behavior, which collapses whitespace as "Up to 25 USD"
        assertThat(resources.getString(string_with_spaces, "25", "USD")).isEqualTo("Up to 25 USD");
    }

    @Test
    public void internalWhiteSpaceShouldBeCollapsed() throws Exception {
        assertThat(resources.getString(internal_whitespace_blocks)).isEqualTo("Whitespace in the middle");
        assertThat(resources.getString(internal_newlines)).isEqualTo("Some Newlines");
    }

    @Test
    public void fontTagWithAttributesShouldBeRead() throws Exception {
        assertThat(resources.getString(font_tag_with_attribute)).isEqualTo("This string has a font tag");
    }

    @Test
    public void linkTagWithAttributesShouldBeRead() throws Exception {
        assertThat(resources.getString(link_tag_with_attribute)).isEqualTo("This string has a link tag");
    }

    @Test
    public void getResourceTypeName_mipmap() {
        assertThat(resources.getResourceTypeName(mipmap_reference)).isEqualTo("mipmap");
        assertThat(resources.getResourceTypeName(robolectric)).isEqualTo("mipmap");
    }

    @Test
    public void getDrawable_mipmapReferencesResolve() {
        Drawable reference = resources.getDrawable(mipmap_reference);
        Drawable original = resources.getDrawable(robolectric);
        assertThat(reference.getMinimumHeight()).isEqualTo(original.getMinimumHeight());
        assertThat(reference.getMinimumWidth()).isEqualTo(original.getMinimumWidth());
    }

    @Test
    @SdkSuppress(minSdkVersion = VERSION_CODES.O)
    @Config(minSdk = VERSION_CODES.O)
    public void getDrawable_mipmapReferencesResolveXml() {
        Drawable reference = resources.getDrawable(robolectric_xml);
        Drawable original = resources.getDrawable(mipmap_reference_xml);
        assertThat(reference.getMinimumHeight()).isEqualTo(original.getMinimumHeight());
        assertThat(reference.getMinimumWidth()).isEqualTo(original.getMinimumWidth());
    }

    @Test
    public void forUntouchedThemes_copyTheme_shouldCopyNothing() throws Exception {
        Resources.Theme theme1 = resources.newTheme();
        Resources.Theme theme2 = resources.newTheme();
        theme2.setTo(theme1);
    }

    @Test
    public void getResourceIdentifier_shouldReturnValueFromRClass() throws Exception {
        assertThat(resources.getIdentifier("id_declared_in_item_tag", "id", context.getPackageName())).isEqualTo(id_declared_in_item_tag);
        assertThat(resources.getIdentifier("id/id_declared_in_item_tag", null, context.getPackageName())).isEqualTo(id_declared_in_item_tag);
        assertThat(resources.getIdentifier(((context.getPackageName()) + ":id_declared_in_item_tag"), "id", null)).isEqualTo(id_declared_in_item_tag);
        assertThat(resources.getIdentifier(((context.getPackageName()) + ":id/id_declared_in_item_tag"), "other", "other")).isEqualTo(id_declared_in_item_tag);
    }

    @Test
    public void whenPackageIsUnknown_getResourceIdentifier_shouldReturnZero() throws Exception {
        assertThat(resources.getIdentifier("whatever", "id", "some.unknown.package")).isEqualTo(0);
        assertThat(resources.getIdentifier("id/whatever", null, "some.unknown.package")).isEqualTo(0);
        assertThat(resources.getIdentifier("some.unknown.package:whatever", "id", null)).isEqualTo(0);
        assertThat(resources.getIdentifier("some.unknown.package:id/whatever", "other", "other")).isEqualTo(0);
        assertThat(resources.getIdentifier("whatever", "drawable", "some.unknown.package")).isEqualTo(0);
        assertThat(resources.getIdentifier("drawable/whatever", null, "some.unknown.package")).isEqualTo(0);
        assertThat(resources.getIdentifier("some.unknown.package:whatever", "drawable", null)).isEqualTo(0);
        assertThat(resources.getIdentifier("some.unknown.package:id/whatever", "other", "other")).isEqualTo(0);
    }

    @Test
    public void whenIdIsAbsentInXmlButPresentInRClass_getResourceIdentifier_shouldReturnIdFromRClass_probablyBecauseItWasDeclaredInALayout() throws Exception {
        assertThat(resources.getIdentifier("id_declared_in_layout", "id", context.getPackageName())).isEqualTo(id_declared_in_layout);
    }

    @Test
    public void whenResourceIsAbsentInXml_getResourceIdentifier_shouldReturn0() throws Exception {
        assertThat(resources.getIdentifier("fictitiousDrawable", "drawable", context.getPackageName())).isEqualTo(0);
    }

    @Test
    public void whenResourceIsAbsentInXml_getResourceIdentifier_shouldReturnId() throws Exception {
        assertThat(resources.getIdentifier("an_image", "drawable", context.getPackageName())).isEqualTo(an_image);
    }

    @Test
    public void whenResourceIsXml_getResourceIdentifier_shouldReturnId() throws Exception {
        assertThat(resources.getIdentifier("preferences", "xml", context.getPackageName())).isEqualTo(preferences);
    }

    @Test
    public void whenResourceIsRaw_getResourceIdentifier_shouldReturnId() throws Exception {
        assertThat(resources.getIdentifier("raw_resource", "raw", context.getPackageName())).isEqualTo(raw_resource);
    }

    @Test
    public void getResourceValue_colorARGB8() {
        TypedValue outValue = new TypedValue();
        resources.getValue(test_ARGB8, outValue, false);
        assertThat(outValue.type).isEqualTo(TYPE_INT_COLOR_ARGB8);
        assertThat(Color.blue(outValue.data)).isEqualTo(2);
    }

    @Test
    public void getResourceValue_colorRGB8() {
        TypedValue outValue = new TypedValue();
        resources.getValue(test_RGB8, outValue, false);
        assertThat(outValue.type).isEqualTo(TYPE_INT_COLOR_RGB8);
        assertThat(Color.blue(outValue.data)).isEqualTo(4);
    }

    @Test
    public void getResourceEntryName_forStyle() throws Exception {
        assertThat(resources.getResourceEntryName(TextAppearance_Small)).isEqualTo("TextAppearance.Small");
    }

    @Test
    @SdkSuppress(minSdkVersion = O)
    @Config(minSdk = O)
    public void getFont() {
        // Feature not supported in legacy (raw) resource mode.
        Assume.assumeFalse(ResourcesTest.isRobolectricLegacyMode());
        Typeface typeface = resources.getFont(vt323_regular);
        assertThat(typeface).isNotNull();
    }

    @Test
    @SdkSuppress(minSdkVersion = O)
    @Config(minSdk = O)
    public void getFontFamily() {
        // Feature not supported in legacy (raw) resource mode.
        Assume.assumeFalse(ResourcesTest.isRobolectricLegacyMode());
        Typeface typeface = resources.getFont(vt323);
        assertThat(typeface).isNotNull();
    }

    @Test
    @SdkSuppress(minSdkVersion = O)
    @Config(minSdk = O)
    public void getFontFamily_downloadable() {
        // Feature not supported in legacy (raw) resource mode.
        Assume.assumeFalse(ResourcesTest.isRobolectricLegacyMode());
        Typeface typeface = resources.getFont(downloadable);
        assertThat(typeface).isNotNull();
    }

    private static class SubClassResources extends Resources {
        public SubClassResources(Resources res) {
            super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
        }
    }
}

