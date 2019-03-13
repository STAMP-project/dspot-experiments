package com.pushtorefresh.storio3.sqlite.integration.auto_parcel;


import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AutoParcelTest {
    // Initialized in @Before
    @NonNull
    private StorIOSQLite storIOSQLite;

    @Test
    public void insertObject() {
        final Book book = Book.builder().id(1).title("What a great book").author("Somebody").build();
        final PutResult putResult = storIOSQLite.put().object(book).prepare().executeAsBlocking();
        assertThat(putResult.wasInserted()).isTrue();
        final List<Book> storedBooks = storIOSQLite.get().listOfObjects(Book.class).withQuery(Query.builder().table(BookTableMeta.TABLE).build()).prepare().executeAsBlocking();
        assertThat(storedBooks).hasSize(1);
        assertThat(storedBooks.get(0)).isEqualTo(book);
    }

    @Test
    public void updateObject() {
        final Book book = Book.builder().id(1).title("What a great book").author("Somebody").build();
        final PutResult putResult1 = storIOSQLite.put().object(book).prepare().executeAsBlocking();
        assertThat(putResult1.wasInserted()).isTrue();
        final Book bookWithUpdatedInfo = // Same id, should be updated
        Book.builder().id(1).title("Corrected title").author("Corrected author").build();
        final PutResult putResult2 = storIOSQLite.put().object(bookWithUpdatedInfo).prepare().executeAsBlocking();
        assertThat(putResult2.wasUpdated()).isTrue();
        final List<Book> storedBooks = storIOSQLite.get().listOfObjects(Book.class).withQuery(Query.builder().table(BookTableMeta.TABLE).build()).prepare().executeAsBlocking();
        assertThat(storedBooks).hasSize(1);
        assertThat(storedBooks.get(0)).isEqualTo(bookWithUpdatedInfo);
    }

    @Test
    public void deleteObject() {
        final Book book = Book.builder().id(1).title("What a great book").author("Somebody").build();
        final PutResult putResult = storIOSQLite.put().object(book).prepare().executeAsBlocking();
        assertThat(putResult.wasInserted()).isTrue();
        final DeleteResult deleteResult = storIOSQLite.delete().object(book).prepare().executeAsBlocking();
        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);
        final List<Book> storedBooks = storIOSQLite.get().listOfObjects(Book.class).withQuery(Query.builder().table(BookTableMeta.TABLE).build()).prepare().executeAsBlocking();
        assertThat(storedBooks).hasSize(0);
    }
}

