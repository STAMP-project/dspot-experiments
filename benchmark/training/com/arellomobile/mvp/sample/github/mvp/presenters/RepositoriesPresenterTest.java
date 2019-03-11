package com.arellomobile.mvp.sample.github.mvp.presenters;


import com.arellomobile.mvp.sample.github.mvp.GithubService;
import com.arellomobile.mvp.sample.github.mvp.models.Repository;
import com.arellomobile.mvp.sample.github.mvp.views.RepositoriesView..State;
import com.arellomobile.mvp.sample.github.test.GithubSampleTestRunner;
import com.arellomobile.mvp.sample.github.test.TestComponentRule;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;


@RunWith(GithubSampleTestRunner.class)
public class RepositoriesPresenterTest {
    @Mock
    GithubService githubService;

    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule(testAppComponent());

    @Mock
    RepositoriesView$$State repositoriesViewState;

    private RepositoriesPresenter presenter;

    @Test
    public void repositories_shouldCloseError() {
        presenter.onErrorCancel();
        Mockito.verify(repositoriesViewState).hideError();
    }

    @Test
    public void repositories_shouldOnAttachLoadAndShowRepositores() {
        List<Repository> repositories = repositores();
        Mockito.when(githubService.getUserRepos(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(Observable.just(repositories));
        presenter.onFirstViewAttach();
        Mockito.verify(repositoriesViewState).onStartLoading();
        Mockito.verify(repositoriesViewState, Mockito.never()).showRefreshing();
        Mockito.verify(repositoriesViewState).showListProgress();
        Mockito.verify(repositoriesViewState).onFinishLoading();
        Mockito.verify(repositoriesViewState).setRepositories(repositories, false);
    }

    @Test
    public void repositories_shouldCorrectLoadNextRepositories() {
        List<Repository> repositories = repositores();
        Mockito.when(githubService.getUserRepos(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(Observable.just(repositories));
        presenter.loadNextRepositories(10);
        Mockito.verify(repositoriesViewState).onStartLoading();
        Mockito.verify(repositoriesViewState, Mockito.never()).showListProgress();
        Mockito.verify(repositoriesViewState, Mockito.never()).hideListProgress();
        Mockito.verify(repositoriesViewState).onFinishLoading();
        Mockito.verify(repositoriesViewState).addRepositories(repositories, false);
    }

    @Test
    public void repositories_shouldShowErrorIfSomeExceptionHappended() {
        RuntimeException someError = new RuntimeException();
        Mockito.when(githubService.getUserRepos(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(Observable.error(someError));
        presenter.loadNextRepositories(10);
        Mockito.verify(repositoriesViewState).onStartLoading();
        Mockito.verify(repositoriesViewState).onFinishLoading();
        Mockito.verify(repositoriesViewState).showError(someError.toString());
    }
}

