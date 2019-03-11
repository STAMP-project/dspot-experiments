package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.R;
import org.robolectric.res.android.ResTable_config;
import org.robolectric.util.TestUtil;

import static org.robolectric.R.raw.raw_no_ext;
import static org.robolectric.R.raw.raw_resource;


@RunWith(JUnit4.class)
public class RawResourceLoaderTest {
    private PackageResourceTable resourceTable;

    @Test
    public void shouldReturnRawResourcesWithExtensions() throws Exception {
        String f = ((String) (resourceTable.getValue(raw_resource, new ResTable_config()).getData()));
        assertThat(f).isEqualTo(TestUtil.testResources().getResourceBase().resolve("raw").resolve("raw_resource.txt").toString());
    }

    @Test
    public void shouldReturnRawResourcesWithoutExtensions() throws Exception {
        String f = ((String) (resourceTable.getValue(raw_no_ext, new ResTable_config()).getData()));
        assertThat(f).isEqualTo(TestUtil.testResources().getResourceBase().resolve("raw").resolve("raw_no_ext").toString());
    }
}

