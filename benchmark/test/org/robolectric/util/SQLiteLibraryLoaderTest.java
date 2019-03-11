package org.robolectric.util;


import SQLiteLibraryLoader.LibraryNameMapper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.util.SQLiteLibraryLoader;


@RunWith(AndroidJUnit4.class)
public class SQLiteLibraryLoaderTest {
    private String savedOs;

    /**
     * Saved system properties.
     */
    private String savedArch;

    private SQLiteLibraryLoader loader;

    @Test
    public void shouldExtractNativeLibrary() {
        assertThat(loader.isLoaded()).isFalse();
        loader.doLoad();
        assertThat(loader.isLoaded()).isTrue();
    }

    @Test
    public void shouldFindLibraryForWindowsXPX86() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.WINDOWS), "Windows XP", "x86")).isEqualTo("windows-x86/sqlite4java.dll");
    }

    @Test
    public void shouldFindLibraryForWindows7X86() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.WINDOWS), "Windows 7", "x86")).isEqualTo("windows-x86/sqlite4java.dll");
    }

    @Test
    public void shouldFindLibraryForWindowsXPAmd64() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.WINDOWS), "Windows XP", "amd64")).isEqualTo("windows-x86_64/sqlite4java.dll");
    }

    @Test
    public void shouldFindLibraryForWindows7Amd64() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.WINDOWS), "Windows 7", "amd64")).isEqualTo("windows-x86_64/sqlite4java.dll");
    }

    @Test
    public void shouldFindLibraryForLinuxi386() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.LINUX), "Some linux version", "i386")).isEqualTo("linux-x86/libsqlite4java.so");
    }

    @Test
    public void shouldFindLibraryForLinuxx86() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.LINUX), "Some linux version", "x86")).isEqualTo("linux-x86/libsqlite4java.so");
    }

    @Test
    public void shouldFindLibraryForLinuxAmd64() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.LINUX), "Some linux version", "amd64")).isEqualTo("linux-x86_64/libsqlite4java.so");
    }

    @Test
    public void shouldFindLibraryForMacWithAnyArch() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.MAC), "Mac OS X", "any architecture")).isEqualTo("mac-x86_64/libsqlite4java.jnilib");
    }

    @Test
    public void shouldFindLibraryForMacWithAnyArchAndDyLibMapping() throws IOException {
        assertThat(loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.MAC_DYLIB), "Mac OS X", "any architecture")).isEqualTo("mac-x86_64/libsqlite4java.jnilib");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionIfUnknownNameAndArch() throws Exception {
        loadLibrary(new SQLiteLibraryLoader(SQLiteLibraryLoaderTest.LINUX), "ACME Electronic", "FooBar2000");
    }

    private static class LibraryMapperTest implements SQLiteLibraryLoader.LibraryNameMapper {
        private final String prefix;

        private final String ext;

        private LibraryMapperTest(String prefix, String ext) {
            this.prefix = prefix;
            this.ext = ext;
        }

        @Override
        public String mapLibraryName(String name) {
            return (((prefix) + name) + ".") + (ext);
        }
    }

    private static final LibraryNameMapper LINUX = new SQLiteLibraryLoaderTest.LibraryMapperTest("lib", "so");

    private static final LibraryNameMapper WINDOWS = new SQLiteLibraryLoaderTest.LibraryMapperTest("", "dll");

    private static final LibraryNameMapper MAC = new SQLiteLibraryLoaderTest.LibraryMapperTest("lib", "jnilib");

    private static final LibraryNameMapper MAC_DYLIB = new SQLiteLibraryLoaderTest.LibraryMapperTest("lib", "dylib");
}

