package com.esoxjem.movieguide.listing.sorting;


import SortType.FAVORITES;
import SortType.HIGHEST_RATED;
import SortType.MOST_POPULAR;
import SortType.NEWEST;
import com.esoxjem.movieguide.RxSchedulerRule;
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
public class SortingDialogPresenterImplTest {
    @Rule
    public RxSchedulerRule rule = new RxSchedulerRule();

    @Mock
    private SortingDialogInteractor interactor;

    @Mock
    private SortingDialogView view;

    private SortingDialogPresenterImpl presenter;

    @Test
    public void shouldCheckPopularIfLastSavedOptionIsPopular() throws Exception {
        Mockito.when(interactor.getSelectedSortingOption()).thenReturn(MOST_POPULAR.getValue());
        presenter.setLastSavedOption();
        Mockito.verify(view).setPopularChecked();
    }

    @Test
    public void shouldCheckHighestRatedIfLastSavedOptionIsHighestRated() throws Exception {
        Mockito.when(interactor.getSelectedSortingOption()).thenReturn(HIGHEST_RATED.getValue());
        presenter.setLastSavedOption();
        Mockito.verify(view).setHighestRatedChecked();
    }

    @Test
    public void shouldCheckFavoritesIfLastSavedOptionIsFavorites() throws Exception {
        Mockito.when(interactor.getSelectedSortingOption()).thenReturn(FAVORITES.getValue());
        presenter.setLastSavedOption();
        Mockito.verify(view).setFavoritesChecked();
    }

    @Test
    public void shouldCheckNewestMoviesIfLastSavedOptionIsHighestRated() throws Exception {
        Mockito.when(interactor.getSelectedSortingOption()).thenReturn(NEWEST.getValue());
        presenter.setLastSavedOption();
        Mockito.verify(view).setNewestChecked();
    }

    @Test
    public void onPopularMoviesSelected() throws Exception {
        presenter.onPopularMoviesSelected();
        Mockito.verify(interactor).setSortingOption(MOST_POPULAR);
    }

    @Test
    public void onHighestRatedMoviesSelected() throws Exception {
        presenter.onHighestRatedMoviesSelected();
        Mockito.verify(interactor).setSortingOption(HIGHEST_RATED);
    }

    @Test
    public void onFavoritesSelected() throws Exception {
        presenter.onFavoritesSelected();
        Mockito.verify(interactor).setSortingOption(FAVORITES);
    }

    @Test
    public void onNewestMoviesSelected() throws Exception {
        presenter.onNewestMoviesSelected();
        Mockito.verify(interactor).setSortingOption(NEWEST);
    }
}

