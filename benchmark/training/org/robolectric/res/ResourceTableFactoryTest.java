package org.robolectric.res;


import android.R.id.text1;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.R;

import static org.robolectric.R.id.burritos;
import static org.robolectric.R.mipmap.mipmap_reference;


@RunWith(JUnit4.class)
public class ResourceTableFactoryTest {
    private ResourceTable appResourceTable;

    private ResourceTable systemResourceTable;

    @Test
    public void shouldHandleMipmapReferences() {
        assertThat(appResourceTable.getResourceId(new ResName("org.robolectric:mipmap/mipmap_reference"))).isEqualTo(mipmap_reference);
    }

    @Test
    public void shouldHandleStyleable() throws Exception {
        assertThat(appResourceTable.getResourceId(new ResName("org.robolectric:id/burritos"))).isEqualTo(burritos);
        assertThat(appResourceTable.getResourceId(new ResName("org.robolectric:styleable/TitleBar_textStyle"))).isEqualTo(0);
    }

    @Test
    public void shouldPrefixAllSystemResourcesWithAndroid() throws Exception {
        assertThat(systemResourceTable.getResourceId(new ResName("android:id/text1"))).isEqualTo(text1);
    }

    @Test
    public void shouldRetainPackageNameForFullyQualifiedQueries() throws Exception {
        assertThat(systemResourceTable.getResName(text1).getFullyQualifiedName()).isEqualTo("android:id/text1");
        assertThat(appResourceTable.getResName(burritos).getFullyQualifiedName()).isEqualTo("org.robolectric:id/burritos");
    }
}

