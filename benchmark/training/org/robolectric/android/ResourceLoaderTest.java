package org.robolectric.android;


import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.res.ResName;
import org.robolectric.res.ResourceTable;

import static org.robolectric.R.bool.different_resource_boolean;


@RunWith(AndroidJUnit4.class)
public class ResourceLoaderTest {
    private String optsForO;

    @Test
    @Config(qualifiers = "w0dp")
    public void checkDefaultBooleanValue() throws Exception {
        assertThat(ApplicationProvider.getApplicationContext().getResources().getBoolean(different_resource_boolean)).isEqualTo(false);
    }

    @Test
    @Config(qualifiers = "w820dp")
    public void checkQualifiedBooleanValue() throws Exception {
        assertThat(ApplicationProvider.getApplicationContext().getResources().getBoolean(different_resource_boolean)).isEqualTo(true);
    }

    @Test
    public void checkForPollution1() throws Exception {
        checkForPollutionHelper();
    }

    @Test
    public void checkForPollution2() throws Exception {
        checkForPollutionHelper();
    }

    @Test
    public void shouldMakeInternalResourcesAvailable() throws Exception {
        ResourceTable resourceProvider = RuntimeEnvironment.getSystemResourceTable();
        ResName internalResource = new ResName("android", "string", "badPin");
        Integer resId = resourceProvider.getResourceId(internalResource);
        assertThat(resId).isNotNull();
        assertThat(resourceProvider.getResName(resId)).isEqualTo(internalResource);
        Class<?> internalRIdClass = Robolectric.class.getClassLoader().loadClass(("com.android.internal.R$" + (internalResource.type)));
        int internalResourceId;
        internalResourceId = ((Integer) (internalRIdClass.getDeclaredField(internalResource.name).get(null)));
        assertThat(resId).isEqualTo(internalResourceId);
        assertThat(ApplicationProvider.getApplicationContext().getResources().getString(resId)).isEqualTo("The old PIN you typed isn't correct.");
    }
}

