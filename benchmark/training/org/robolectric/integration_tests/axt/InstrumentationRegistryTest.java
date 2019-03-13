package org.robolectric.integration_tests.axt;


import android.app.Instrumentation;
import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * {@link InstrumentationRegistry} tests.
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentationRegistryTest {
    private static Instrumentation priorInstrumentation = null;

    private static Context priorContext = null;

    @Test
    public void getArguments() {
        assertThat(InstrumentationRegistry.getArguments()).isNotNull();
    }

    @Test
    public void getInstrumentation() {
        assertThat(InstrumentationRegistry.getInstrumentation()).isNotNull();
    }

    @Test
    public void getTargetContext() {
        assertThat(InstrumentationRegistry.getTargetContext()).isNotNull();
        assertThat(InstrumentationRegistry.getContext()).isNotNull();
    }

    @Test
    public void uniqueInstancesPerTest() {
        checkInstances();
    }

    @Test
    public void uniqueInstancesPerTest2() {
        checkInstances();
    }
}

