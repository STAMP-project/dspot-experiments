package com.arellomobile.mvp.sample.github.mvp.presenters;


import com.arellomobile.mvp.sample.github.mvp.models.Repository;
import com.arellomobile.mvp.sample.github.mvp.views.RepositoryView..State;
import com.arellomobile.mvp.sample.github.test.GithubSampleTestRunner;
import com.arellomobile.mvp.sample.github.test.TestComponentRule;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(GithubSampleTestRunner.class)
public class RepositoryPresenterTest {
    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule();

    @Mock
    RepositoryView$$State repositoryViewState;

    private RepositoryPresenter presenter;

    private Repository repository;

    @Test
    public void repository_shouldShwRepository() {
        presenter.onFirstViewAttach();
        Mockito.verify(repositoryViewState).showRepository(repository);
        Mockito.verify(repositoryViewState, Mockito.never()).updateLike(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void repository_shouldSetLikeTrueForRepository() {
        presenter.updateLikes(Collections.singletonList(0), Collections.singletonList(0));
        Mockito.verify(repositoryViewState).updateLike(true, true);
    }

    @Test
    public void repository_shouldSetLikeFalseForRepository() {
        presenter.updateLikes(Collections.singletonList(1), Collections.singletonList(1));
        Mockito.verify(repositoryViewState).updateLike(false, false);
    }
}

