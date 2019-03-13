package org.robolectric.shadows;


import Context.MODE_PRIVATE;
import SharedPreferences.Editor;
import SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class ShadowSharedPreferencesTest {
    private static final String FILENAME = "filename";

    private Editor editor;

    private SharedPreferences sharedPreferences;

    private final Set<String> stringSet = new HashSet<>();

    private Context context;

    @Test
    public void commit_shouldStoreValues() throws Exception {
        editor.commit();
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences(ShadowSharedPreferencesTest.FILENAME, MODE_PRIVATE);
        Assert.assertTrue(anotherSharedPreferences.getBoolean("boolean", false));
        assertThat(anotherSharedPreferences.getFloat("float", 666.0F)).isEqualTo(1.1F);
        assertThat(anotherSharedPreferences.getInt("int", 666)).isEqualTo(2);
        assertThat(anotherSharedPreferences.getLong("long", 666L)).isEqualTo(3L);
        assertThat(anotherSharedPreferences.getString("string", "wacka wa")).isEqualTo("foobar");
        assertThat(anotherSharedPreferences.getStringSet("stringSet", null)).isEqualTo(stringSet);
    }

    @Test
    public void commit_shouldClearEditsThatNeedRemoveAndEditsThatNeedCommit() throws Exception {
        editor.commit();
        editor.remove("string").commit();
        assertThat(sharedPreferences.getString("string", "no value for key")).isEqualTo("no value for key");
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences(ShadowSharedPreferencesTest.FILENAME, MODE_PRIVATE);
        anotherSharedPreferences.edit().putString("string", "value for key").commit();
        editor.commit();
        assertThat(sharedPreferences.getString("string", "no value for key")).isEqualTo("value for key");
    }

    @Test
    public void getAll_shouldReturnAllValues() throws Exception {
        editor.commit();
        assertThat(sharedPreferences.getAll()).hasSize(6);
        assertThat(sharedPreferences.getAll().get("int")).isEqualTo(2);
    }

    @Test
    public void commit_shouldRemoveValuesThenSetValues() throws Exception {
        editor.putString("deleteMe", "foo").commit();
        editor.remove("deleteMe");
        editor.putString("dontDeleteMe", "baz");
        editor.remove("dontDeleteMe");
        editor.commit();
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences(ShadowSharedPreferencesTest.FILENAME, MODE_PRIVATE);
        assertThat(anotherSharedPreferences.getBoolean("boolean", false)).isTrue();
        assertThat(anotherSharedPreferences.getFloat("float", 666.0F)).isEqualTo(1.1F);
        assertThat(anotherSharedPreferences.getInt("int", 666)).isEqualTo(2);
        assertThat(anotherSharedPreferences.getLong("long", 666L)).isEqualTo(3L);
        assertThat(anotherSharedPreferences.getString("string", "wacka wa")).isEqualTo("foobar");
        assertThat(anotherSharedPreferences.getString("deleteMe", "awol")).isEqualTo("awol");
        assertThat(anotherSharedPreferences.getString("dontDeleteMe", "oops")).isEqualTo("oops");
    }

    @Test
    public void commit_shouldClearThenSetValues() throws Exception {
        editor.putString("deleteMe", "foo");
        editor.clear();
        editor.putString("dontDeleteMe", "baz");
        editor.commit();
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences(ShadowSharedPreferencesTest.FILENAME, MODE_PRIVATE);
        Assert.assertTrue(anotherSharedPreferences.getBoolean("boolean", false));
        assertThat(anotherSharedPreferences.getFloat("float", 666.0F)).isEqualTo(1.1F);
        assertThat(anotherSharedPreferences.getInt("int", 666)).isEqualTo(2);
        assertThat(anotherSharedPreferences.getLong("long", 666L)).isEqualTo(3L);
        assertThat(anotherSharedPreferences.getString("string", "wacka wa")).isEqualTo("foobar");
        // Android always calls clear before put on any open editor, so here "foo" is preserved rather than cleared.
        assertThat(anotherSharedPreferences.getString("deleteMe", "awol")).isEqualTo("foo");
        assertThat(anotherSharedPreferences.getString("dontDeleteMe", "oops")).isEqualTo("baz");
    }

    @Test
    public void putString_shouldRemovePairIfValueIsNull() throws Exception {
        editor.putString("deleteMe", "foo");
        editor.putString("deleteMe", null);
        editor.commit();
        assertThat(sharedPreferences.getString("deleteMe", null)).isNull();
    }

    @Test
    public void putStringSet_shouldRemovePairIfValueIsNull() throws Exception {
        editor.putStringSet("deleteMe", new HashSet<String>());
        editor.putStringSet("deleteMe", null);
        editor.commit();
        assertThat(sharedPreferences.getStringSet("deleteMe", null)).isNull();
    }

    @Test
    public void apply_shouldStoreValues() throws Exception {
        editor.apply();
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences(ShadowSharedPreferencesTest.FILENAME, MODE_PRIVATE);
        assertThat(anotherSharedPreferences.getString("string", "wacka wa")).isEqualTo("foobar");
    }

    @Test
    public void shouldReturnDefaultValues() throws Exception {
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences("bazBang", MODE_PRIVATE);
        Assert.assertFalse(anotherSharedPreferences.getBoolean("boolean", false));
        assertThat(anotherSharedPreferences.getFloat("float", 666.0F)).isEqualTo(666.0F);
        assertThat(anotherSharedPreferences.getInt("int", 666)).isEqualTo(666);
        assertThat(anotherSharedPreferences.getLong("long", 666L)).isEqualTo(666L);
        assertThat(anotherSharedPreferences.getString("string", "wacka wa")).isEqualTo("wacka wa");
    }

    @Test
    public void shouldRemoveRegisteredListenersOnUnresgister() {
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences("bazBang", MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener mockListener = Mockito.mock(OnSharedPreferenceChangeListener.class);
        anotherSharedPreferences.registerOnSharedPreferenceChangeListener(mockListener);
        anotherSharedPreferences.unregisterOnSharedPreferenceChangeListener(mockListener);
        anotherSharedPreferences.edit().putString("key", "value");
        Mockito.verifyZeroInteractions(mockListener);
    }

    @Test
    public void shouldTriggerRegisteredListeners() {
        SharedPreferences anotherSharedPreferences = context.getSharedPreferences("bazBang", MODE_PRIVATE);
        final String testKey = "foo";
        final List<String> transcript = new ArrayList<>();
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                transcript.add((key + " called"));
            }
        };
        anotherSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        anotherSharedPreferences.edit().putString(testKey, "bar").commit();
        assertThat(transcript).containsExactly((testKey + " called"));
    }

    @Test
    public void defaultSharedPreferences() throws Exception {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("foo", "bar").commit();
        SharedPreferences anotherSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String restored = anotherSharedPreferences.getString("foo", null);
        assertThat(restored).isEqualTo("bar");
    }

    /**
     * Tests a sequence of operations in SharedPrefereces that would previously cause a deadlock.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void commit_multipleTimes() throws Exception {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("foo", true).apply();
        sharedPreferences.edit().putBoolean("bar", true).commit();
        Assert.assertTrue(sharedPreferences.getBoolean("foo", false));
        Assert.assertTrue(sharedPreferences.getBoolean("bar", false));
    }
}

