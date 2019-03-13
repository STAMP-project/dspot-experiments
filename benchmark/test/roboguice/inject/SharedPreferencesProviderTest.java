package roboguice.inject;


import RoboGuice.DEFAULT_STAGE;
import RoboGuice.Util;
import Robolectric.application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.content.TestSharedPreferences;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.util.Strings;


@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(RobolectricTestRunner.class)
public class SharedPreferencesProviderTest {
    @Test
    public void shouldInjectDefaultSharedPrefs() throws Exception {
        final SharedPreferencesProviderTest.A a = Robolectric.buildActivity(SharedPreferencesProviderTest.A.class).create().get();
        final Field f = TestSharedPreferences.class.getDeclaredField("filename");
        f.setAccessible(true);
        Assert.assertTrue(Strings.notEmpty(f.get(a.prefs)));
        Assert.assertThat(f.get(a.prefs), CoreMatchers.equalTo(f.get(PreferenceManager.getDefaultSharedPreferences(a))));
    }

    @Test
    public void shouldInjectNamedSharedPrefs() throws Exception {
        RoboGuice.getOrCreateBaseApplicationInjector(application, DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(application), new SharedPreferencesProviderTest.ModuleA());
        try {
            final SharedPreferencesProviderTest.A a = Robolectric.buildActivity(SharedPreferencesProviderTest.A.class).create().get();
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);
            Assert.assertEquals("FOOBAR", f.get(a.prefs));
        } finally {
            Util.reset();
        }
    }

    @Test
    public void shouldFallbackOnOldDefaultIfPresent() throws Exception {
        final File oldDefault = new File("shared_prefs/default.xml");
        final File oldDir = new File("shared_prefs");
        oldDir.mkdirs();
        oldDefault.createNewFile();
        try {
            final SharedPreferencesProviderTest.A a = Robolectric.buildActivity(SharedPreferencesProviderTest.A.class).create().get();
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);
            Assert.assertTrue(Strings.notEmpty(f.get(a.prefs)));
            Assert.assertEquals("default.xml", f.get(a.prefs));
        } finally {
            oldDefault.delete();
            oldDir.delete();
        }
    }

    @Test
    public void shouldNotFallbackOnOldDefaultIfNamedFileSpecified() throws Exception {
        RoboGuice.getOrCreateBaseApplicationInjector(application, DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(application), new SharedPreferencesProviderTest.ModuleA());
        final File oldDefault = new File("shared_prefs/default.xml");
        final File oldDir = new File("shared_prefs");
        oldDir.mkdirs();
        oldDefault.createNewFile();
        try {
            final SharedPreferencesProviderTest.A a = Robolectric.buildActivity(SharedPreferencesProviderTest.A.class).create().get();
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);
            Assert.assertTrue(Strings.notEmpty(f.get(a.prefs)));
            Assert.assertEquals("FOOBAR", f.get(a.prefs));
        } finally {
            Util.reset();
            oldDefault.delete();
            oldDir.delete();
        }
    }

    public static class A extends RoboActivity {
        @Inject
        SharedPreferences prefs;
    }

    public static class ModuleA extends AbstractModule {
        @Override
        protected void configure() {
            bindConstant().annotatedWith(SharedPreferencesName.class).to("FOOBAR");
        }
    }
}

