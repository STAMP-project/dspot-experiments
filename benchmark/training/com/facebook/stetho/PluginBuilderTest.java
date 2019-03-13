package com.facebook.stetho;


import Build.VERSION_CODES;
import android.app.Activity;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.dumpapp.plugins.HprofDumperPlugin;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.facebook.stetho.inspector.protocol.module.Debugger;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(emulateSdk = VERSION_CODES.JELLY_BEAN)
@RunWith(RobolectricTestRunner.class)
public class PluginBuilderTest {
    private final Activity mActivity = Robolectric.setupActivity(Activity.class);

    @Test
    public void test_Remove_DefaultInspectorModulesBuilder() throws IOException {
        final Class<Debugger> debuggerClass = Debugger.class;
        Iterable<ChromeDevtoolsDomain> domains = new Stetho.DefaultInspectorModulesBuilder(mActivity).remove(debuggerClass.getName()).finish();
        boolean containsDebugggerDomain = false;
        for (ChromeDevtoolsDomain domain : domains) {
            if (domain.getClass().equals(debuggerClass)) {
                containsDebugggerDomain = true;
                break;
            }
        }
        Assert.assertFalse(containsDebugggerDomain);
    }

    @Test
    public void test_Remove_DefaultDumperPluginsBuilder() throws IOException {
        // HprofDumperPlugin.NAME is private
        final String hprofDumperPluginNAME = "hprof";
        final Iterable<DumperPlugin> dumperPlugins = remove(hprofDumperPluginNAME).finish();
        boolean containsDebugggerDomain = false;
        for (DumperPlugin plugin : dumperPlugins) {
            if (plugin.getClass().equals(HprofDumperPlugin.class)) {
                containsDebugggerDomain = true;
                break;
            }
        }
        Assert.assertFalse(containsDebugggerDomain);
    }
}

