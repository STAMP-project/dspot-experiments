package com.arellomobile.mvp.sample.github.mvp.presenters;


import com.arellomobile.mvp.sample.github.mvp.models.Repository;
import com.arellomobile.mvp.sample.github.mvp.views.HomeView;
import com.arellomobile.mvp.sample.github.mvp.views.HomeView..State;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class HomePresenterTest {
    @Mock
    HomeView homeView;

    @Mock
    HomeView$$State homeViewState;

    private HomePresenter presenter;

    @Test
    public void details_shouldShowDetailsContainer() {
        Repository emptyRepository = emptyRepository();
        presenter.onRepositorySelection(0, emptyRepository);
        Mockito.verify(homeViewState).showDetailsContainer();
        Mockito.verify(homeViewState).showDetails(0, emptyRepository);
    }
}

