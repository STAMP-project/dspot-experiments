package com.reactnativenavigation.parse;


import Typeface.BOLD;
import Typeface.NORMAL;
import android.graphics.Color;
import android.graphics.Typeface;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.NullText;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.utils.TypefaceLoader;
import org.json.JSONObject;
import org.junit.Test;


public class OptionsTest extends BaseTest {
    private static final String TITLE = "the title";

    private static final Number TITLE_HEIGHT = new Number(100);

    private static final String FAB_ID = "FAB";

    private static final String FAB_ALIGN_HORIZONTALLY = "right";

    private static final String FAB_ALIGN_VERTICALLY = "bottom";

    private static final int TOP_BAR_BACKGROUND_COLOR = -15584170;

    private static final int FAB_BACKGROUND_COLOR = Color.BLUE;

    private static final int FAB_CLICK_COLOR = Color.RED;

    private static final int FAB_RIPPLE_COLOR = Color.GREEN;

    private static final Boolean FAB_VISIBLE = true;

    private static final Boolean FAB_HIDE_ON_SCROLL = true;

    private static final int TOP_BAR_TEXT_COLOR = -15584170;

    private static final int TOP_BAR_FONT_SIZE = 18;

    private static final String TOP_BAR_FONT_FAMILY = "HelveticaNeue-CondensedBold";

    private static final int SUBTITLE_FONT_SIZE = 14;

    private static final int SUBTITLE_TEXT_COLOR = -15584169;

    private static final int SCREEN_BACKGROUND_COLOR = -15584168;

    private static final String SUBTITLE_FONT_FAMILY = "HelveticaNeue-Condensed";

    private static final Typeface SUBTITLE_TYPEFACE = Typeface.create("HelveticaNeue-Condensed", NORMAL);

    private static final String SUBTITLE_ALIGNMENT = "center";

    private static final Typeface TOP_BAR_TYPEFACE = Typeface.create("HelveticaNeue-CondensedBold", BOLD);

    private static final Bool TOP_BAR_VISIBLE = new Bool(true);

    private static final Bool TOP_BAR_DRAW_BEHIND = new Bool(true);

    private static final Bool TOP_BAR_HIDE_ON_SCROLL = new Bool(true);

    private static final Bool BOTTOM_TABS_ANIMATE = new Bool(true);

    private static final Bool BOTTOM_TABS_VISIBLE = new Bool(true);

    private static final String BOTTOM_TABS_BADGE = "3";

    private static final String BOTTOM_TABS_CURRENT_TAB_ID = "ComponentId";

    private static final Number BOTTOM_TABS_CURRENT_TAB_INDEX = new Number(1);

    private TypefaceLoader mockLoader;

    @Test
    public void parsesNullAsDefaultEmptyOptions() {
        assertThat(Options.parse(mockLoader, null)).isNotNull();
    }

    @Test
    public void parsesJson() throws Exception {
        JSONObject layout = new JSONObject().put("backgroundColor", OptionsTest.SCREEN_BACKGROUND_COLOR);
        JSONObject json = new JSONObject().put("topBar", createTopBar(OptionsTest.TOP_BAR_VISIBLE.get())).put("fab", createFab()).put("bottomTabs", createBottomTabs()).put("layout", layout);
        Options result = Options.parse(mockLoader, json);
        assertResult(result);
    }

    @Test
    public void mergeDoesNotMutate() throws Exception {
        JSONObject json1 = new JSONObject();
        json1.put("topBar", createTopBar(true));
        Options options1 = Options.parse(mockLoader, json1);
        options1.topBar.title.text = new Text("some title");
        JSONObject json2 = new JSONObject();
        json2.put("topBar", createTopBar(false));
        Options options2 = Options.parse(mockLoader, json2);
        options2.topBar.title.text = new NullText();
        Options merged = options1.mergeWith(options2);
        assertThat(options1.topBar.visible.get()).isTrue();
        assertThat(merged.topBar.visible.get()).isFalse();
        assertThat(merged.topBar.title.text.get()).isEqualTo("some title");
    }

    @Test
    public void mergeDefaultOptions() throws Exception {
        JSONObject layout = new JSONObject().put("backgroundColor", OptionsTest.SCREEN_BACKGROUND_COLOR);
        JSONObject json = new JSONObject().put("topBar", createTopBar(OptionsTest.TOP_BAR_VISIBLE.get())).put("fab", createFab()).put("bottomTabs", createBottomTabs()).put("layout", layout);
        Options defaultOptions = Options.parse(mockLoader, json);
        Options options = new Options();
        assertResult(options.mergeWith(defaultOptions));
    }

    @Test
    public void mergedDefaultOptionsDontOverrideGivenOptions() throws Exception {
        JSONObject layout = new JSONObject().put("backgroundColor", OptionsTest.SCREEN_BACKGROUND_COLOR);
        JSONObject defaultJson = new JSONObject().put("topBar", createOtherTopBar()).put("fab", createOtherFab()).put("bottomTabs", createOtherBottomTabs()).put("layout", layout);
        Options defaultOptions = Options.parse(mockLoader, defaultJson);
        JSONObject json = new JSONObject().put("topBar", createTopBar(OptionsTest.TOP_BAR_VISIBLE.get())).put("bottomTabs", createBottomTabs());
        Options options = Options.parse(mockLoader, json);
        options.withDefaultOptions(defaultOptions);
        assertResult(options);
    }

    @Test
    public void defaultEmptyOptions() {
        Options uut = new Options();
        assertThat(uut.topBar.title.text.get("")).isEmpty();
        assertThat(uut.layout.backgroundColor.hasValue()).isFalse();
    }

    @Test
    public void topBar_defaultOptions() {
        Options uut = new Options();
        assertThat(uut.topBar.visible.isFalseOrUndefined()).isTrue();
        assertThat(uut.topBar.animate.isTrueOrUndefined()).isTrue();
    }

    @Test
    public void clear_topBarOptions() {
        Options uut = new Options();
        uut.topBar.title.text = new Text("some title");
        uut.clearTopBarOptions();
        assertThat(uut.topBar.title.text.hasValue()).isFalse();
    }

    @Test
    public void clear_bottomTabsOptions() {
        Options uut = new Options();
        uut.bottomTabsOptions.backgroundColor = new com.reactnativenavigation.parse.params.Colour(Color.RED);
        uut.clearBottomTabsOptions();
        assertThat(uut.bottomTabsOptions.backgroundColor.hasValue()).isFalse();
    }

    @Test
    public void clear_topTabsOptions() {
        Options uut = new Options();
        uut.topTabs.fontSize = new Number(666);
        uut.clearTopTabsOptions();
        assertThat(uut.topTabs.fontSize.hasValue()).isFalse();
    }

    @Test
    public void clear_topTabOptions() {
        Options uut = new Options();
        uut.topTabOptions.title = new Text("some title");
        uut.clearTopTabOptions();
        assertThat(uut.topTabOptions.title.hasValue()).isFalse();
    }
}

