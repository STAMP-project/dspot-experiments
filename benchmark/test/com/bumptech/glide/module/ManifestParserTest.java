package com.bumptech.glide.module;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
@SuppressWarnings("deprecation")
public class ManifestParserTest {
    private static final String MODULE_VALUE = "GlideModule";

    @Mock
    private Context context;

    private ManifestParser parser;

    private ApplicationInfo applicationInfo;

    @Test
    public void testParse_returnsEmptyListIfNoModulesListed() {
        assertThat(parser.parse()).isEmpty();
    }

    @Test
    public void testParse_withSingleValidModuleName_returnsListContainingModule() {
        addModuleToManifest(ManifestParserTest.TestModule1.class);
        List<GlideModule> modules = parser.parse();
        assertThat(modules).hasSize(1);
        assertThat(modules.get(0)).isInstanceOf(ManifestParserTest.TestModule1.class);
    }

    @Test
    public void testParse_withMultipleValidModuleNames_returnsListContainingModules() {
        addModuleToManifest(ManifestParserTest.TestModule1.class);
        addModuleToManifest(ManifestParserTest.TestModule2.class);
        List<GlideModule> modules = parser.parse();
        assertThat(modules).hasSize(2);
        assertThat(modules).contains(new ManifestParserTest.TestModule1());
        assertThat(modules).contains(new ManifestParserTest.TestModule2());
    }

    @Test
    public void testParse_withValidModuleName_ignoresMetadataWithoutGlideModuleValue() {
        applicationInfo.metaData.putString(ManifestParserTest.TestModule1.class.getName(), ((ManifestParserTest.MODULE_VALUE) + "test"));
        assertThat(parser.parse()).isEmpty();
    }

    @Test(expected = RuntimeException.class)
    public void testThrows_whenModuleNameNotFound() {
        addToManifest("fakeClassName");
        parser.parse();
    }

    @Test(expected = RuntimeException.class)
    public void testThrows_whenClassInManifestIsNotAModule() {
        addModuleToManifest(ManifestParserTest.InvalidClass.class);
        parser.parse();
    }

    @Test(expected = RuntimeException.class)
    public void testThrows_whenPackageNameNotFound() {
        Mockito.when(context.getPackageName()).thenReturn("fakePackageName");
        parser.parse();
    }

    private static class InvalidClass {}

    public static class TestModule1 implements GlideModule {
        @Override
        public void applyOptions(@NonNull
        Context context, @NonNull
        GlideBuilder builder) {
        }

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ManifestParserTest.TestModule1;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    public static class TestModule2 implements GlideModule {
        @Override
        public void applyOptions(@NonNull
        Context context, @NonNull
        GlideBuilder builder) {
        }

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ManifestParserTest.TestModule2;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}

