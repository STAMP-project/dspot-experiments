package org.robolectric.plugins;


import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.TestFakeApp;
import org.robolectric.annotation.Config;
import org.robolectric.pluginapi.config.ConfigurationStrategy;
import org.robolectric.pluginapi.config.Configurer;
import org.robolectric.shadows.ShadowView;
import org.robolectric.shadows.ShadowViewGroup;
import org.robolectric.shadows.testing.TestApplication;


@RunWith(JUnit4.class)
public class HierarchicalConfigurationStrategyTest {
    @Test
    public void defaultValuesAreMerged() throws Exception {
        assertThat(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation").manifest()).isEqualTo("AndroidManifest.xml");
    }

    @Test
    public void globalValuesAreMerged() throws Exception {
        assertThat(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation", new Config.Builder().setManifest("ManifestFromGlobal.xml").build()).manifest()).isEqualTo("ManifestFromGlobal.xml");
    }

    @Test
    public void whenClassHasConfigAnnotation_getConfig_shouldMergeClassAndMethodConfig() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1.class, "withoutAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-test", "test/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.test1" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1.class, "withDefaultsAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-test", "test/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.test1" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1.class, "withOverrideAnnotation"), new int[]{ 9 }, "furf", TestApplication.class, "com.example.method", "from-method", "method/res", "method/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test2.class }, new String[]{ "com.example.test1", "com.example.method1" }, new String[]{ "libs/method", "libs/test" });
    }

    @Test
    public void whenClassDoesntHaveConfigAnnotation_getConfig_shouldUseMethodConfig() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation"), new int[0], "AndroidManifest.xml", DEFAULT_APPLICATION, "", "", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{  });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withDefaultsAnnotation"), new int[0], "AndroidManifest.xml", DEFAULT_APPLICATION, "", "", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{  });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withOverrideAnnotation"), new int[]{ 9 }, "furf", TestFakeApp.class, "com.example.method", "from-method", "method/res", "method/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.method2" }, new String[]{ "libs/method" });
    }

    @Test
    public void whenClassDoesntHaveConfigAnnotation_getConfig_shouldMergeParentClassAndMethodConfig() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1B.class, "withoutAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-test", "test/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.test1" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1B.class, "withDefaultsAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-test", "test/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.test1" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1B.class, "withOverrideAnnotation"), new int[]{ 14 }, "foo", TestFakeApp.class, "com.example.test", "from-method5", "test/res", "method5/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1B.class }, new String[]{ "com.example.test1", "com.example.method5" }, new String[]{ "libs/test" });
    }

    @Test
    public void whenClassAndParentClassHaveConfigAnnotation_getConfig_shouldMergeParentClassAndMethodConfig() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1C.class, "withoutAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-class6", "class6/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1C.class }, new String[]{ "com.example.test1", "com.example.test6" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1C.class, "withDefaultsAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-class6", "class6/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1C.class }, new String[]{ "com.example.test1", "com.example.test6" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1C.class, "withOverrideAnnotation"), new int[]{ 14 }, "foo", TestFakeApp.class, "com.example.test", "from-method5", "class6/res", "method5/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test1C.class, HierarchicalConfigurationStrategyTest.Test1B.class }, new String[]{ "com.example.test1", "com.example.method5", "com.example.test6" }, new String[]{ "libs/test" });
    }

    @Test
    public void whenClassAndSubclassHaveConfigAnnotation_getConfig_shouldMergeClassSubclassAndMethodConfig() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1A.class, "withoutAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-subclass", "test/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.test1" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1A.class, "withDefaultsAnnotation"), new int[]{ 1 }, "foo", TestFakeApp.class, "com.example.test", "from-subclass", "test/res", "test/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.test1" }, new String[]{ "libs/test" });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test1A.class, "withOverrideAnnotation"), new int[]{ 9 }, "furf", TestApplication.class, "com.example.method", "from-method", "method/res", "method/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class, HierarchicalConfigurationStrategyTest.Test2.class }, new String[]{ "com.example.test1", "com.example.method1" }, new String[]{ "libs/method", "libs/test" });
    }

    @Test
    public void whenClassDoesntHaveConfigAnnotationButSubclassDoes_getConfig_shouldMergeSubclassAndMethodConfig() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2A.class, "withoutAnnotation"), new int[0], "AndroidManifest.xml", DEFAULT_APPLICATION, "", "from-subclass", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{  });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2A.class, "withDefaultsAnnotation"), new int[0], "AndroidManifest.xml", DEFAULT_APPLICATION, "", "from-subclass", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{  });
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2A.class, "withOverrideAnnotation"), new int[]{ 9 }, "furf", TestFakeApp.class, "com.example.method", "from-method", "method/res", "method/assets", new Class<?>[]{ HierarchicalConfigurationStrategyTest.Test1.class }, new String[]{ "com.example.method2" }, new String[]{ "libs/method" });
    }

    @Test
    public void shouldLoadDefaultsFromGlobalPropertiesFile() throws Exception {
        String properties = "sdk: 432\n" + (((((((("manifest: --none\n" + "qualifiers: from-properties-file\n") + "resourceDir: from/properties/file/res\n") + "assetDir: from/properties/file/assets\n") + "shadows: org.robolectric.shadows.ShadowView, org.robolectric.shadows.ShadowViewGroup\n") + "application: org.robolectric.TestFakeApp\n") + "packageName: com.example.test\n") + "instrumentedPackages: com.example.test1, com.example.test2\n") + "libraries: libs/test, libs/test2");
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation", ImmutableMap.of("robolectric.properties", properties)), new int[]{ 432 }, "--none", TestFakeApp.class, "com.example.test", "from-properties-file", "from/properties/file/res", "from/properties/file/assets", new Class<?>[]{ ShadowView.class, ShadowViewGroup.class }, new String[]{ "com.example.test1", "com.example.test2" }, new String[]{ "libs/test", "libs/test2" });
    }

    @Test
    public void shouldMergeConfigFromTestClassPackageProperties() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation", ImmutableMap.of("org/robolectric/robolectric.properties", "sdk: 432\n")), new int[]{ 432 }, "AndroidManifest.xml", DEFAULT_APPLICATION, "", "", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{  });
    }

    @Test
    public void shouldMergeConfigUpPackageHierarchy() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation", ImmutableMap.of("org/robolectric/robolectric.properties", "qualifiers: from-org-robolectric\nlibraries: FromOrgRobolectric\n", "org/robolectric.properties", "sdk: 123\nqualifiers: from-org\nlibraries: FromOrg\n", "robolectric.properties", "sdk: 456\nqualifiers: from-top-level\nlibraries: FromTopLevel\n")), new int[]{ 123 }, "AndroidManifest.xml", DEFAULT_APPLICATION, "", "from-org-robolectric", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{ "FromOrgRobolectric", "FromOrg", "FromTopLevel" });
    }

    @Test
    public void withEmptyShadowList_shouldLoadDefaultsFromGlobalPropertiesFile() throws Exception {
        HierarchicalConfigurationStrategyTest.assertConfig(configFor(HierarchicalConfigurationStrategyTest.Test2.class, "withoutAnnotation", ImmutableMap.of("robolectric.properties", "shadows:")), new int[0], "AndroidManifest.xml", DEFAULT_APPLICATION, "", "", "res", "assets", new Class<?>[]{  }, new String[]{  }, new String[]{  });
    }

    @Test
    public void testPrecedence() throws Exception {
        HierarchicalConfigurationStrategyTest.SpyConfigurer spyConfigurer = new HierarchicalConfigurationStrategyTest.SpyConfigurer();
        ConfigurationStrategy configStrategy = new HierarchicalConfigurationStrategy(spyConfigurer);
        assertThat(computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test1.class, "withoutAnnotation")).isEqualTo(((("default:(top):org:org.robolectric:org.robolectric.plugins" + ":") + (HierarchicalConfigurationStrategyTest.Test1.class.getName())) + ":withoutAnnotation"));
        assertThat(computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test1A.class, "withOverrideAnnotation")).isEqualTo(((((("default:(top):org:org.robolectric:org.robolectric.plugins" + ":") + (HierarchicalConfigurationStrategyTest.Test1.class.getName())) + ":") + (HierarchicalConfigurationStrategyTest.Test1A.class.getName())) + ":withOverrideAnnotation"));
    }

    @Test
    public void testTestClassMatters() throws Exception {
        HierarchicalConfigurationStrategyTest.SpyConfigurer spyConfigurer = new HierarchicalConfigurationStrategyTest.SpyConfigurer();
        ConfigurationStrategy configStrategy = new HierarchicalConfigurationStrategy(spyConfigurer);
        assertThat(computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test1.class, "withoutAnnotation")).isEqualTo(((("default:(top):org:org.robolectric:org.robolectric.plugins" + ":") + (HierarchicalConfigurationStrategyTest.Test1.class.getName())) + ":withoutAnnotation"));
        assertThat(computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test1A.class, "withoutAnnotation")).isEqualTo(((((("default:(top):org:org.robolectric:org.robolectric.plugins" + ":") + (HierarchicalConfigurationStrategyTest.Test1.class.getName())) + ":") + (HierarchicalConfigurationStrategyTest.Test1A.class.getName())) + ":withoutAnnotation"));
    }

    @Test
    public void testBigOAndCaching() throws Exception {
        HierarchicalConfigurationStrategyTest.SpyConfigurer spyConfigurer = new HierarchicalConfigurationStrategyTest.SpyConfigurer();
        ConfigurationStrategy configStrategy = new HierarchicalConfigurationStrategy(spyConfigurer);
        computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test1A.class, "withoutAnnotation");
        assertThat(spyConfigurer.log).containsExactly("default", "withoutAnnotation", HierarchicalConfigurationStrategyTest.Test1A.class.getName(), HierarchicalConfigurationStrategyTest.Test1.class.getName(), "org.robolectric.plugins", "org.robolectric", "org", "(top)").inOrder();
        spyConfigurer.log.clear();
        computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test1.class, "withoutAnnotation");
        assertThat(spyConfigurer.log).containsExactly("withoutAnnotation").inOrder();
        spyConfigurer.log.clear();
        computeConfig(configStrategy, HierarchicalConfigurationStrategyTest.Test2A.class, "withOverrideAnnotation");
        assertThat(spyConfigurer.log).containsExactly("withOverrideAnnotation", HierarchicalConfigurationStrategyTest.Test2A.class.getName(), HierarchicalConfigurationStrategyTest.Test2.class.getName()).inOrder();
    }

    @Ignore
    @Config(sdk = 1, manifest = "foo", application = TestFakeApp.class, packageName = "com.example.test", shadows = HierarchicalConfigurationStrategyTest.Test1.class, instrumentedPackages = "com.example.test1", libraries = "libs/test", qualifiers = "from-test", resourceDir = "test/res", assetDir = "test/assets")
    public static class Test1 {
        @Test
        public void withoutAnnotation() throws Exception {
        }

        @Test
        @Config
        public void withDefaultsAnnotation() throws Exception {
        }

        @Test
        @Config(sdk = 9, manifest = "furf", application = TestApplication.class, packageName = "com.example.method", shadows = HierarchicalConfigurationStrategyTest.Test2.class, instrumentedPackages = "com.example.method1", libraries = "libs/method", qualifiers = "from-method", resourceDir = "method/res", assetDir = "method/assets")
        public void withOverrideAnnotation() throws Exception {
        }
    }

    @Ignore
    public static class Test2 {
        @Test
        public void withoutAnnotation() throws Exception {
        }

        @Test
        @Config
        public void withDefaultsAnnotation() throws Exception {
        }

        @Test
        @Config(sdk = 9, manifest = "furf", application = TestFakeApp.class, packageName = "com.example.method", shadows = HierarchicalConfigurationStrategyTest.Test1.class, instrumentedPackages = "com.example.method2", libraries = "libs/method", qualifiers = "from-method", resourceDir = "method/res", assetDir = "method/assets")
        public void withOverrideAnnotation() throws Exception {
        }
    }

    @Ignore
    @Config(qualifiers = "from-subclass")
    public static class Test1A extends HierarchicalConfigurationStrategyTest.Test1 {}

    @Ignore
    @Config(qualifiers = "from-subclass")
    public static class Test2A extends HierarchicalConfigurationStrategyTest.Test2 {}

    @Ignore
    public static class Test1B extends HierarchicalConfigurationStrategyTest.Test1 {
        @Override
        @Test
        public void withoutAnnotation() throws Exception {
        }

        @Override
        @Test
        @Config
        public void withDefaultsAnnotation() throws Exception {
        }

        @Override
        @Test
        @Config(sdk = 14, shadows = HierarchicalConfigurationStrategyTest.Test1B.class, instrumentedPackages = "com.example.method5", packageName = "com.example.test", qualifiers = "from-method5", assetDir = "method5/assets")
        public void withOverrideAnnotation() throws Exception {
        }
    }

    @Ignore
    @Config(qualifiers = "from-class6", shadows = HierarchicalConfigurationStrategyTest.Test1C.class, instrumentedPackages = "com.example.test6", resourceDir = "class6/res")
    public static class Test1C extends HierarchicalConfigurationStrategyTest.Test1B {}

    private static class SpyConfigurer implements Configurer<String> {
        final List<String> log = new ArrayList<>();

        @Override
        public Class<String> getConfigClass() {
            return String.class;
        }

        @Nonnull
        @Override
        public String defaultConfig() {
            return log("default");
        }

        @Override
        public String getConfigFor(@Nonnull
        String packageName) {
            return log((packageName.isEmpty() ? "(top)" : packageName));
        }

        @Override
        public String getConfigFor(@Nonnull
        Class<?> testClass) {
            return log(testClass.getName());
        }

        @Override
        public String getConfigFor(@Nonnull
        Method method) {
            return log(method.getName());
        }

        @Nonnull
        @Override
        public String merge(@Nonnull
        String parentConfig, @Nonnull
        String childConfig) {
            return (parentConfig + ":") + childConfig;
        }

        private String log(String s) {
            log.add(s);
            return s;
        }
    }
}

