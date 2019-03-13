package com.esoxjem.movieguide.details;


import com.esoxjem.movieguide.Movie;
import com.esoxjem.movieguide.Review;
import com.esoxjem.movieguide.RxSchedulerRule;
import com.esoxjem.movieguide.Video;
import com.esoxjem.movieguide.favorites.FavoritesInteractor;
import io.reactivex.Observable;
import java.net.SocketTimeoutException;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author arunsasidharan
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieDetailsPresenterImplTest {
    @Rule
    public RxSchedulerRule rule = new RxSchedulerRule();

    @Mock
    private MovieDetailsView view;

    @Mock
    private MovieDetailsInteractor movieDetailsInteractor;

    @Mock
    private FavoritesInteractor favoritesInteractor;

    @Mock
    List<Video> videos;

    @Mock
    Movie movie;

    @Mock
    List<Review> reviews;

    private MovieDetailsPresenterImpl movieDetailsPresenter;

    @Test
    public void shouldUnfavoriteIfFavoriteTapped() {
        Mockito.when(movie.getId()).thenReturn("12345");
        Mockito.when(favoritesInteractor.isFavorite(movie.getId())).thenReturn(true);
        movieDetailsPresenter.onFavoriteClick(movie);
        Mockito.verify(view).showUnFavorited();
    }

    @Test
    public void shouldFavoriteIfUnfavoriteTapped() {
        Mockito.when(movie.getId()).thenReturn("12345");
        Mockito.when(favoritesInteractor.isFavorite(movie.getId())).thenReturn(false);
        movieDetailsPresenter.onFavoriteClick(movie);
        Mockito.verify(view).showFavorited();
    }

    @Test
    public void shouldBeAbleToShowTrailers() {
        Mockito.when(movie.getId()).thenReturn("12345");
        Observable<List<Video>> responseObservable = Observable.just(videos);
        Mockito.when(movieDetailsInteractor.getTrailers(movie.getId())).thenReturn(responseObservable);
        movieDetailsPresenter.showTrailers(movie);
        Mockito.verify(view).showTrailers(videos);
    }

    @Test
    public void shouldFailSilentlyWhenNoTrailers() throws Exception {
        Mockito.when(movie.getId()).thenReturn("12345");
        Mockito.when(movieDetailsInteractor.getTrailers(movie.getId())).thenReturn(Observable.error(new SocketTimeoutException()));
        movieDetailsPresenter.showTrailers(movie);
        Mockito.verifyZeroInteractions(view);
    }

    @Test
    public void shouldBeAbleToShowReviews() {
        Observable<List<Review>> responseObservable = Observable.just(reviews);
        Mockito.when(movie.getId()).thenReturn("12345");
        Mockito.when(movieDetailsInteractor.getReviews(movie.getId())).thenReturn(responseObservable);
        movieDetailsPresenter.showReviews(movie);
        Mockito.verify(view).showReviews(reviews);
    }

    @Test
    public void shouldFailSilentlyWhenNoReviews() throws Exception {
        Mockito.when(movie.getId()).thenReturn("12345");
        Mockito.when(movieDetailsInteractor.getReviews(movie.getId())).thenReturn(Observable.error(new SocketTimeoutException()));
        movieDetailsPresenter.showReviews(movie);
        Mockito.verifyZeroInteractions(view);
    }
}

