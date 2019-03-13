package com.pushtorefresh.storio3.contentresolver.design;


import BackpressureStrategy.MISSING;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResults;
import com.pushtorefresh.storio3.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio3.contentresolver.queries.UpdateQuery;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class PutOperationDesignTest extends OperationDesignTest {
    final PutResolver<ContentValues> putResolverForContentValues = new com.pushtorefresh.storio3.contentresolver.operations.put.DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull
        ContentValues object) {
            return InsertQuery.builder().uri(Mockito.mock(Uri.class)).build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull
        ContentValues object) {
            return UpdateQuery.builder().uri(Mockito.mock(Uri.class)).build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull
        ContentValues contentValues) {
            return contentValues;// easy

        }
    };

    @Test
    public void putObjectBlocking() {
        Article article = Article.newInstance(null, "test");
        PutResult putResult = storIOContentResolver().put().object(article).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void putObjectFlowable() {
        Article article = Article.newInstance(null, "test");
        Flowable<PutResult> putResultFlowable = storIOContentResolver().put().object(article).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putObjectSingle() {
        Article article = Article.newInstance(null, "test");
        Single<PutResult> putResultSingle = storIOContentResolver().put().object(article).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void putObjectCompletable() {
        Article article = Article.newInstance(null, "test");
        Completable completable = storIOContentResolver().put().object(article).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().asRxCompletable();
    }

    @Test
    public void putCollectionOfObjectsBlocking() {
        Collection<Article> articles = new ArrayList<Article>();
        PutResults<Article> putResults = storIOContentResolver().put().objects(articles).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void putCollectionOfObjectsFlowable() {
        Collection<Article> articles = new ArrayList<Article>();
        Flowable<PutResults<Article>> putResultsFlowable = storIOContentResolver().put().objects(articles).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putCollectionOfObjectsSingle() {
        Collection<Article> articles = new ArrayList<Article>();
        Single<PutResults<Article>> putResultsSingle = storIOContentResolver().put().objects(articles).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void putCollectionOfObjectsCompletable() {
        Collection<Article> articles = new ArrayList<Article>();
        Completable completable = storIOContentResolver().put().objects(articles).withPutResolver(ArticleMeta.PUT_RESOLVER).prepare().asRxCompletable();
    }

    @Test
    public void putContentValuesBlocking() {
        ContentValues contentValues = Mockito.mock(ContentValues.class);
        PutResult putResult = storIOContentResolver().put().contentValues(contentValues).withPutResolver(putResolverForContentValues).prepare().executeAsBlocking();
    }

    @Test
    public void putContentValuesFlowable() {
        ContentValues contentValues = Mockito.mock(ContentValues.class);
        Flowable<PutResult> putResultFlowable = storIOContentResolver().put().contentValues(contentValues).withPutResolver(putResolverForContentValues).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putContentValuesSingle() {
        ContentValues contentValues = Mockito.mock(ContentValues.class);
        Single<PutResult> putResultSingle = storIOContentResolver().put().contentValues(contentValues).withPutResolver(putResolverForContentValues).prepare().asRxSingle();
    }

    @Test
    public void putContentValuesCompletable() {
        ContentValues contentValues = Mockito.mock(ContentValues.class);
        Completable completable = storIOContentResolver().put().contentValues(contentValues).withPutResolver(putResolverForContentValues).prepare().asRxCompletable();
    }

    @Test
    public void putContentValuesIterableBlocking() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        PutResults<ContentValues> putResults = storIOContentResolver().put().contentValues(contentValuesList).withPutResolver(putResolverForContentValues).prepare().executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableFlowable() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        Flowable<PutResults<ContentValues>> putResultsFlowable = storIOContentResolver().put().contentValues(contentValuesList).withPutResolver(putResolverForContentValues).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putContentValuesIterableSingle() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        Single<PutResults<ContentValues>> putResultsSingle = storIOContentResolver().put().contentValues(contentValuesList).withPutResolver(putResolverForContentValues).prepare().asRxSingle();
    }

    @Test
    public void putContentValuesIterableCompletable() {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        Completable completable = storIOContentResolver().put().contentValues(contentValuesList).withPutResolver(putResolverForContentValues).prepare().asRxCompletable();
    }
}

