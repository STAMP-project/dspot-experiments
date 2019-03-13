package com.nytimes.android.external.fs3;


import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;


public class UtilTest {
    private final Util util = new Util();

    @Test
    public void testSimplifyPath() {
        assertThat(util.simplifyPath("/a/b/c/d")).isEqualTo("/a/b/c/d");
        assertThat(util.simplifyPath("/a/../b/")).isEqualTo("/b");
        assertThat(util.simplifyPath("/a/./b/c/../d")).isEqualTo("/a/b/d");
        assertThat(util.simplifyPath("./a")).isEqualTo("/a");
        assertThat(util.simplifyPath(null)).isEqualTo("");
        assertThat(util.simplifyPath("")).isEqualTo("");
    }

    @Test
    public void createParentDirTest() throws IOException {
        File child = Mockito.mock(File.class);
        File parent = Mockito.mock(File.class);
        Mockito.when(child.getCanonicalFile()).thenReturn(child);
        Mockito.when(child.getParentFile()).thenReturn(parent);
        Mockito.when(parent.isDirectory()).thenReturn(true);
        util.createParentDirs(child);
        Mockito.verify(parent).mkdirs();
    }
}

