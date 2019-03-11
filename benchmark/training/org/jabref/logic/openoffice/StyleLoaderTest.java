package org.jabref.logic.openoffice;


import StyleLoader.DEFAULT_AUTHORYEAR_STYLE_PATH;
import StyleLoader.DEFAULT_NUMERICAL_STYLE_PATH;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


public class StyleLoaderTest {
    private static int numberOfInternalStyles = 2;

    private StyleLoader loader;

    private OpenOfficePreferences preferences;

    private LayoutFormatterPreferences layoutPreferences;

    private Charset encoding;

    @Test
    public void throwNPEWithNullPreferences() {
        Assertions.assertThrows(NullPointerException.class, () -> loader = new StyleLoader(null, layoutPreferences, Mockito.mock(Charset.class)));
    }

    @Test
    public void throwNPEWithNullLayoutPreferences() {
        Assertions.assertThrows(NullPointerException.class, () -> loader = new StyleLoader(Mockito.mock(OpenOfficePreferences.class), null, Mockito.mock(Charset.class)));
    }

    @Test
    public void throwNPEWithNullCharset() {
        Assertions.assertThrows(NullPointerException.class, () -> loader = new StyleLoader(Mockito.mock(OpenOfficePreferences.class), layoutPreferences, null));
    }

    @Test
    public void testGetStylesWithEmptyExternal() {
        preferences.setExternalStyles(Collections.emptyList());
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        Assertions.assertEquals(2, loader.getStyles().size());
    }

    @Test
    public void testAddStyleLeadsToOneMoreStyle() throws URISyntaxException {
        preferences.setExternalStyles(Collections.emptyList());
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        String filename = Paths.get(StyleLoader.class.getResource(DEFAULT_AUTHORYEAR_STYLE_PATH).toURI()).toFile().getPath();
        loader.addStyleIfValid(filename);
        Assertions.assertEquals(((StyleLoaderTest.numberOfInternalStyles) + 1), loader.getStyles().size());
    }

    @Test
    public void testAddInvalidStyleLeadsToNoMoreStyle() {
        preferences.setExternalStyles(Collections.emptyList());
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        int beforeAdding = loader.getStyles().size();
        loader.addStyleIfValid("DefinitelyNotAValidFileNameOrWeAreExtremelyUnlucky");
        Assertions.assertEquals(beforeAdding, loader.getStyles().size());
    }

    @Test
    public void testInitalizeWithOneExternalFile() throws URISyntaxException {
        String filename = Paths.get(StyleLoader.class.getResource(DEFAULT_AUTHORYEAR_STYLE_PATH).toURI()).toFile().getPath();
        Mockito.when(preferences.getExternalStyles()).thenReturn(Collections.singletonList(filename));
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        Assertions.assertEquals(((StyleLoaderTest.numberOfInternalStyles) + 1), loader.getStyles().size());
    }

    @Test
    public void testInitalizeWithIncorrectExternalFile() {
        preferences.setExternalStyles(Collections.singletonList("DefinitelyNotAValidFileNameOrWeAreExtremelyUnlucky"));
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        Assertions.assertEquals(StyleLoaderTest.numberOfInternalStyles, loader.getStyles().size());
    }

    @Test
    public void testInitalizeWithOneExternalFileRemoveStyle() throws URISyntaxException {
        String filename = Paths.get(StyleLoader.class.getResource(DEFAULT_AUTHORYEAR_STYLE_PATH).toURI()).toFile().getPath();
        Mockito.when(preferences.getExternalStyles()).thenReturn(Collections.singletonList(filename));
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        List<OOBibStyle> toremove = new ArrayList<>();
        int beforeRemoving = loader.getStyles().size();
        for (OOBibStyle style : loader.getStyles()) {
            if (!(style.isFromResource())) {
                toremove.add(style);
            }
        }
        for (OOBibStyle style : toremove) {
            Assertions.assertTrue(loader.removeStyle(style));
        }
        Assertions.assertEquals((beforeRemoving - 1), loader.getStyles().size());
    }

    @Test
    public void testInitalizeWithOneExternalFileRemoveStyleUpdatesPreferences() throws URISyntaxException {
        String filename = Paths.get(StyleLoader.class.getResource(DEFAULT_AUTHORYEAR_STYLE_PATH).toURI()).toFile().getPath();
        Mockito.when(preferences.getExternalStyles()).thenReturn(Collections.singletonList(filename));
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        List<OOBibStyle> toremove = new ArrayList<>();
        for (OOBibStyle style : loader.getStyles()) {
            if (!(style.isFromResource())) {
                toremove.add(style);
            }
        }
        for (OOBibStyle style : toremove) {
            Assertions.assertTrue(loader.removeStyle(style));
        }
        // As the prefs are mocked away, the getExternalStyles still returns the initial one
        Assertions.assertFalse(preferences.getExternalStyles().isEmpty());
    }

    @Test
    public void testAddSameStyleTwiceLeadsToOneMoreStyle() throws URISyntaxException {
        preferences.setExternalStyles(Collections.emptyList());
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        int beforeAdding = loader.getStyles().size();
        String filename = Paths.get(StyleLoader.class.getResource(DEFAULT_AUTHORYEAR_STYLE_PATH).toURI()).toFile().getPath();
        loader.addStyleIfValid(filename);
        loader.addStyleIfValid(filename);
        Assertions.assertEquals((beforeAdding + 1), loader.getStyles().size());
    }

    @Test
    public void testAddNullStyleThrowsNPE() {
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        Assertions.assertThrows(NullPointerException.class, () -> loader.addStyleIfValid(null));
    }

    @Test
    public void testGetDefaultUsedStyleWhenEmpty() {
        Mockito.when(preferences.getCurrentStyle()).thenReturn(DEFAULT_AUTHORYEAR_STYLE_PATH);
        preferences.clearCurrentStyle();
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        OOBibStyle style = loader.getUsedStyle();
        Assertions.assertTrue(style.isValid());
        Assertions.assertEquals(DEFAULT_AUTHORYEAR_STYLE_PATH, style.getPath());
        Assertions.assertEquals(DEFAULT_AUTHORYEAR_STYLE_PATH, preferences.getCurrentStyle());
    }

    @Test
    public void testGetStoredUsedStyle() {
        Mockito.when(preferences.getCurrentStyle()).thenReturn(DEFAULT_NUMERICAL_STYLE_PATH);
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        OOBibStyle style = loader.getUsedStyle();
        Assertions.assertTrue(style.isValid());
        Assertions.assertEquals(DEFAULT_NUMERICAL_STYLE_PATH, style.getPath());
        Assertions.assertEquals(DEFAULT_NUMERICAL_STYLE_PATH, preferences.getCurrentStyle());
    }

    @Test
    public void testGetDefaultUsedStyleWhenIncorrect() {
        Mockito.when(preferences.getCurrentStyle()).thenReturn("ljlkjlkjnljnvdlsjniuhwelfhuewfhlkuewhfuwhelu");
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        OOBibStyle style = loader.getUsedStyle();
        Assertions.assertTrue(style.isValid());
        Assertions.assertEquals(DEFAULT_AUTHORYEAR_STYLE_PATH, style.getPath());
    }

    @Test
    public void testRemoveInternalStyleReturnsFalseAndDoNotRemove() {
        preferences.setExternalStyles(Collections.emptyList());
        loader = new StyleLoader(preferences, layoutPreferences, encoding);
        List<OOBibStyle> toremove = new ArrayList<>();
        for (OOBibStyle style : loader.getStyles()) {
            if (style.isFromResource()) {
                toremove.add(style);
            }
        }
        Assertions.assertFalse(loader.removeStyle(toremove.get(0)));
        Assertions.assertEquals(StyleLoaderTest.numberOfInternalStyles, loader.getStyles().size());
    }
}

