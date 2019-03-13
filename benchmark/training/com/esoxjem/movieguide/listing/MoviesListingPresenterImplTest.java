package com.esoxjem.movieguide.listing;


import com.esoxjem.movieguide.Movie;
import com.esoxjem.movieguide.RxSchedulerRule;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author arunsasidharan
 */
@RunWith(MockitoJUnitRunner.class)
public class MoviesListingPresenterImplTest {
    @Rule
    public RxSchedulerRule rule = new RxSchedulerRule();

    @Mock
    private MoviesListingInteractor interactor;

    @Mock
    private MoviesListingView view;

    private List<Movie> movies = new ArrayList<>(0);

    private MoviesListingPresenterImpl presenter;

    @Test
    public void shouldBeAbleToDisplayMovies() {
        // given:
        Observable<List<Movie>> responseObservable = Observable.just(movies);
        Mockito.when(interactor.fetchMovies(ArgumentMatchers.anyInt())).thenReturn(responseObservable);
        // when:
        presenter.setView(view);
        // then:
        Mockito.verify(view).showMovies(movies);
    }
}

