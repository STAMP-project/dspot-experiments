package com.pushtorefresh.storio3.contentresolver.queries;


import android.net.Uri;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.test.ToStringChecker;
import java.util.Arrays;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Required for correct Uri impl
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class UpdateQueryTest {
    @Test
    public void shouldNotAllowNullUriObject() {
        try {
            // noinspection ConstantConditions
            UpdateQuery.builder().uri(((Uri) (null)));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify uri").hasNoCause();
        }
    }

    @Test
    public void shouldNotAllowNullUriString() {
        try {
            // noinspection ConstantConditions
            UpdateQuery.builder().uri(((String) (null)));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Uri should not be null").hasNoCause();
        }
    }

    @Test
    public void shouldNotAllowEmptyUriString() {
        try {
            UpdateQuery.builder().uri("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Uri should not be null").hasNoCause();
        }
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder().uri(Mockito.mock(Uri.class)).build();
        assertThat(updateQuery.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder().uri(Mockito.mock(Uri.class)).build();
        assertThat(updateQuery.whereArgs()).isNotNull();
        assertThat(updateQuery.whereArgs()).isEmpty();
    }

    @Test
    public void completeBuilderShouldNotAllowNullUriObject() {
        try {
            // noinspection ConstantConditions
            UpdateQuery.builder().uri(Mockito.mock(Uri.class)).uri(((Uri) (null)));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify uri").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowNullUriString() {
        try {
            // noinspection ConstantConditions
            UpdateQuery.builder().uri(Mockito.mock(Uri.class)).uri(((String) (null)));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Uri should not be null").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyUriString() {
        try {
            UpdateQuery.builder().uri(Mockito.mock(Uri.class)).uri("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Uri should not be null").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateUriObject() {
        Uri oldUri = Mockito.mock(Uri.class);
        Uri newUri = Mockito.mock(Uri.class);
        UpdateQuery query = UpdateQuery.builder().uri(oldUri).uri(newUri).build();
        assertThat(query.uri()).isSameAs(newUri);
    }

    @Test
    public void completeBuilderShouldUpdateUriString() {
        Uri oldUri = Uri.parse("content://1");
        String newUri = "content://2";
        UpdateQuery query = UpdateQuery.builder().uri(oldUri).uri(newUri).build();
        assertThat(query.uri()).isEqualTo(Uri.parse(newUri));
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final Uri uri = Mockito.mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = new Object[]{ "arg1", "arg2", "arg3" };
        final UpdateQuery firstQuery = UpdateQuery.builder().uri(uri).where(where).whereArgs(whereArgs).build();
        final UpdateQuery secondQuery = firstQuery.toBuilder().build();
        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = new String[]{ "arg1", "arg2", "arg3" };
        final UpdateQuery updateQuery = UpdateQuery.builder().uri(Mockito.mock(Uri.class)).where("test_where").whereArgs(whereArgs).build();
        assertThat(updateQuery.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = Mockito.mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = new Object[]{ "arg1", "arg2", "arg3" };
        final UpdateQuery updateQuery = UpdateQuery.builder().uri(uri).where(where).whereArgs(whereArgs).build();
        assertThat(updateQuery.uri()).isEqualTo(uri);
        assertThat(updateQuery.where()).isEqualTo(where);
        assertThat(updateQuery.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(UpdateQuery.class).allFieldsShouldBeUsed().withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2")).verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(UpdateQuery.class).check();
    }
}

