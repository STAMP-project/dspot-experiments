package com.pushtorefresh.storio3.contentresolver.design;


import BackpressureStrategy.LATEST;
import android.database.Cursor;
import android.net.Uri;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class GetOperationDesignTest extends OperationDesignTest {
    @SuppressWarnings("unchecked")
    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOContentResolver().get().cursor().withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare().executeAsBlocking();
    }

    @Test
    public void getListOfObjectsBlocking() {
        List<Article> articles = storIOContentResolver().get().listOfObjects(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void getObjectBlocking() {
        Article article = storIOContentResolver().get().object(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void getCursorAsFlowable() {
        Flowable<Cursor> flowable = storIOContentResolver().get().cursor().withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare().asRxFlowable(LATEST);
    }

    @Test
    public void getListOfObjectsAsFlowable() {
        Flowable<List<Article>> flowable = storIOContentResolver().get().listOfObjects(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().asRxFlowable(LATEST);
    }

    @Test
    public void getObjectAsFlowable() {
        Flowable<Optional<Article>> flowable = storIOContentResolver().get().object(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().asRxFlowable(LATEST);
    }

    @Test
    public void getCursorAsSingle() {
        Single<Cursor> single = storIOContentResolver().get().cursor().withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare().asRxSingle();
    }

    @Test
    public void getListOfObjectsAsSingle() {
        Single<List<Article>> single = storIOContentResolver().get().listOfObjects(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void getObjectAsSingle() {
        Single<Optional<Article>> single = storIOContentResolver().get().object(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void getObjectAsMaybe() {
        Maybe<Article> maybe = storIOContentResolver().get().object(Article.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(ArticleMeta.GET_RESOLVER).prepare().asRxMaybe();
    }
}

