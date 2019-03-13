package org.robolectric.integration_tests.agp;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test asserting that test-only activities can be declared in a dependency project's manifest as a
 * workaround for the fact that Android Gradle Plugin doesn't merge the test manifest (as of 3.4).
 *
 * When http://issuetracker.google.com/issues/127986458 is fixed, we can collapse
 * `:integration_tests:agp:testsupport` back into `:integration_tests:agp`.
 */
@RunWith(AndroidJUnit4.class)
public class TestActivityTest {
    @Test
    public void testActivitiesCanBeDeclaredInADependencyLibrary() throws Exception {
        ActivityScenario.launch(TestActivity.class);
    }
}

