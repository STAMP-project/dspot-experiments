package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ResourceIds}
 */
@RunWith(JUnit4.class)
public class ResourceIdsTest {
    @Test
    public void testIsFrameworkResource() {
        assertThat(ResourceIds.isFrameworkResource(16777216)).isTrue();
        assertThat(ResourceIds.isFrameworkResource(2130706432)).isFalse();
    }

    @Test
    public void testGetPackageIdentifier() {
        assertThat(ResourceIds.getPackageIdentifier(16777216)).isEqualTo(1);
        assertThat(ResourceIds.getPackageIdentifier(2130706432)).isEqualTo(127);
    }

    @Test
    public void testGetTypeIdentifier() {
        assertThat(ResourceIds.getTypeIdentifier(16881782)).isEqualTo(1);
        assertThat(ResourceIds.getTypeIdentifier(2138575412)).isEqualTo(120);
    }

    @Test
    public void testGetEntryIdentifier() {
        assertThat(ResourceIds.getEntryIdentifier(16881782)).isEqualTo(39030);
        assertThat(ResourceIds.getEntryIdentifier(2138575412)).isEqualTo(4660);
    }

    @Test
    public void testMakeIdentifier() {
        assertThat(ResourceIds.makeIdentifer(1, 1, 39030)).isEqualTo(16881782);
        assertThat(ResourceIds.makeIdentifer(127, 120, 4660)).isEqualTo(2138575412);
    }
}

