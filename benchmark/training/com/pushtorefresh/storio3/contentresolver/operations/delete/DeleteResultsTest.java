package com.pushtorefresh.storio3.contentresolver.operations.delete;


import android.net.Uri;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.test.ToStringChecker;
import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Required for correct Uri impl
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteResultsTest {
    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullResults() {
        DeleteResults.newInstance(null);
    }

    @Test
    public void resultsShouldBeEqual() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString1", DeleteResult.newInstance(1, Mockito.mock(Uri.class)));
        results.put("testString2", DeleteResult.newInstance(1, Mockito.mock(Uri.class)));
        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);
        assertThat(deleteResults.results()).isEqualTo(results);
    }

    @Test
    public void checkWasDeleted() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString1", DeleteResult.newInstance(1, Uri.parse("content://testUri")));
        results.put("testString2", DeleteResult.newInstance(1, Uri.parse("content://testUri")));
        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);
        assertThat(deleteResults.wasDeleted("testString1")).isTrue();
        assertThat(deleteResults.wasDeleted("testString2")).isTrue();
        assertThat(deleteResults.wasDeleted("testString3")).isFalse();
    }

    @Test
    public void checkWasNotDeleted() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString1", DeleteResult.newInstance(1, Uri.parse("content://testUri")));
        results.put("testString2", DeleteResult.newInstance(1, Uri.parse("content://testUri")));
        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);
        assertThat(deleteResults.wasNotDeleted("testString1")).isFalse();
        assertThat(deleteResults.wasNotDeleted("testString2")).isFalse();
        assertThat(deleteResults.wasNotDeleted("testString3")).isTrue();
    }

    @Test
    public void checkWasNotDeletedIfZeroNumberOfRows() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString", DeleteResult.newInstance(0, Uri.parse("content://testUri")));
        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);
        assertThat(deleteResults.wasDeleted("testString")).isFalse();
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(DeleteResults.class).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(DeleteResults.class).check();
    }
}

