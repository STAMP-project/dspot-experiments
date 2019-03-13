package com.pushtorefresh.storio3.contentresolver.design;


import BackpressureStrategy.MISSING;
import android.net.Uri;
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResults;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class DeleteOperationDesignTest extends OperationDesignTest {
    @Test
    public void deleteByQueryBlocking() {
        final DeleteQuery deleteQuery = DeleteQuery.builder().uri(Mockito.mock(Uri.class)).where("some_field = ?").whereArgs("someValue").build();
        DeleteResult deleteResult = storIOContentResolver().delete().byQuery(deleteQuery).prepare().executeAsBlocking();
    }

    @Test
    public void deleteByQueryFlowable() {
        final DeleteQuery deleteQuery = DeleteQuery.builder().uri(Mockito.mock(Uri.class)).where("some_field = ?").whereArgs("someValue").build();
        Flowable<DeleteResult> deleteResultFlowable = storIOContentResolver().delete().byQuery(deleteQuery).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void deleteByQuerySingle() {
        final DeleteQuery deleteQuery = DeleteQuery.builder().uri(Mockito.mock(Uri.class)).where("some_field = ?").whereArgs("someValue").build();
        Single<DeleteResult> deleteResultSingle = storIOContentResolver().delete().byQuery(deleteQuery).prepare().asRxSingle();
    }

    @Test
    public void deleteByQueryCompletable() {
        final DeleteQuery deleteQuery = DeleteQuery.builder().uri(Mockito.mock(Uri.class)).where("some_field = ?").whereArgs("someValue").build();
        Completable completable = storIOContentResolver().delete().byQuery(deleteQuery).prepare().asRxCompletable();
    }

    @Test
    public void deleteObjectsBlocking() {
        final List<Article> articles = new ArrayList<Article>();
        DeleteResults<Article> deleteResults = storIOContentResolver().delete().objects(articles).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void deleteObjectsFlowable() {
        final List<Article> articles = new ArrayList<Article>();
        Flowable<DeleteResults<Article>> deleteResultsFlowable = storIOContentResolver().delete().objects(articles).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void deleteObjectsSingle() {
        final List<Article> articles = new ArrayList<Article>();
        Single<DeleteResults<Article>> deleteResultsSingle = storIOContentResolver().delete().objects(articles).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void deleteObjectsCompletable() {
        final List<Article> articles = new ArrayList<Article>();
        Completable completable = storIOContentResolver().delete().objects(articles).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().asRxCompletable();
    }

    @Test
    public void deleteObjectBlocking() {
        Article article = Mockito.mock(Article.class);
        DeleteResult deleteResult = storIOContentResolver().delete().object(article).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void deleteObjectFlowable() {
        Article article = Mockito.mock(Article.class);
        Flowable<DeleteResult> deleteResultFlowable = storIOContentResolver().delete().object(article).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void deleteObjectSingle() {
        Article article = Mockito.mock(Article.class);
        Single<DeleteResult> deleteResultSingle = storIOContentResolver().delete().object(article).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void deleteObjectCompletable() {
        Article article = Mockito.mock(Article.class);
        Completable completable = storIOContentResolver().delete().object(article).withDeleteResolver(ArticleMeta.DELETE_RESOLVER).prepare().asRxCompletable();
    }
}

