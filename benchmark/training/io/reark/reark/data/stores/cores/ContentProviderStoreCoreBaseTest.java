package io.reark.reark.data.stores.cores;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;


public class ContentProviderStoreCoreBaseTest {
    private ContentProviderStoreCoreBase<Integer> contentStoreCore;

    @Test
    public void groupOperations_WithNoElements_DoesNotEmit() {
        contentStoreCore.<Integer>groupOperations(Observable.never()).test().awaitDone((2 * (ContentProviderStoreCoreBase.DEFAULT_GROUPING_TIMEOUT_MS)), TimeUnit.MILLISECONDS).assertNotComplete().assertNoValues();
    }

    @Test
    public void groupOperations_WithOneElement_EmitsOneGroup() {
        contentStoreCore.groupOperations(ContentProviderStoreCoreBaseTest.createSource(1)).test().awaitDone((2 * (ContentProviderStoreCoreBase.DEFAULT_GROUPING_TIMEOUT_MS)), TimeUnit.MILLISECONDS).assertNotComplete().assertValueCount(1);
    }

    @Test
    public void groupOperations_WithGroupMaxSizeElements_EmitsOneGroup() {
        contentStoreCore.groupOperations(ContentProviderStoreCoreBaseTest.createSource(ContentProviderStoreCoreBase.DEFAULT_GROUP_MAX_SIZE)).test().awaitDone((2 * (ContentProviderStoreCoreBase.DEFAULT_GROUPING_TIMEOUT_MS)), TimeUnit.MILLISECONDS).assertNotComplete().assertValueCount(1);
    }

    @Test
    public void groupOperations_WithOneOverGroupMaxSizeElements_EmitsTwoGroups() {
        contentStoreCore.groupOperations(ContentProviderStoreCoreBaseTest.createSource(((ContentProviderStoreCoreBase.DEFAULT_GROUP_MAX_SIZE) + 1))).test().awaitDone((2 * (ContentProviderStoreCoreBase.DEFAULT_GROUPING_TIMEOUT_MS)), TimeUnit.MILLISECONDS).assertNotComplete().assertValueCount(2);
    }

    @Test
    public void groupOperations_WithThreeTimesGroupMaxSizeElements_EmitsThreeGroups() {
        contentStoreCore.groupOperations(ContentProviderStoreCoreBaseTest.createSource((3 * (ContentProviderStoreCoreBase.DEFAULT_GROUP_MAX_SIZE)))).test().awaitDone((2 * (ContentProviderStoreCoreBase.DEFAULT_GROUPING_TIMEOUT_MS)), TimeUnit.MILLISECONDS).assertNotComplete().assertValueCount(3);
    }

    @SuppressWarnings({ "ReturnOfNull", "ConstantConditions", "ZeroLengthArrayAllocation" })
    private static class NullContentStore extends ContentProviderStoreCoreBase<Integer> {
        NullContentStore() {
            super(Mockito.mock(ContentResolver.class));
        }

        @NonNull
        @Override
        protected String getAuthority() {
            return null;
        }

        @NonNull
        @Override
        protected ContentObserver getContentObserver() {
            return null;
        }

        @NonNull
        @Override
        protected Uri getContentUri() {
            return null;
        }

        @NonNull
        @Override
        protected String[] getProjection() {
            return new String[0];
        }

        @NonNull
        @Override
        protected Integer read(@NonNull
        Cursor cursor) {
            return null;
        }

        @NonNull
        @Override
        protected ContentValues getContentValuesForItem(@NonNull
        Integer item) {
            return null;
        }
    }
}

