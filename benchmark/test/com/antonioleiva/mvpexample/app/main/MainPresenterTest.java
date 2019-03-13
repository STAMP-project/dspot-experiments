package com.antonioleiva.mvpexample.app.main;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    @Mock
    MainView view;

    @Mock
    FindItemsInteractor interactor;

    private MainPresenter presenter;

    @Test
    public void checkIfShowsProgressOnResume() {
        presenter.onResume();
        Mockito.verify(view, Mockito.times(1)).showProgress();
    }

    @Test
    public void checkIfShowsMessageOnItemClick() {
        presenter.onItemClicked(1);
        Mockito.verify(view, Mockito.times(1)).showMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void checkIfRightMessageIsDisplayed() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        presenter.onItemClicked(1);
        Mockito.verify(view, Mockito.times(1)).showMessage(captor.capture());
        MatcherAssert.assertThat(captor.getValue(), CoreMatchers.is("Position 2 clicked"));
    }

    @Test
    public void checkIfViewIsReleasedOnDestroy() {
        presenter.onDestroy();
        Assert.assertNull(presenter.getMainView());
    }

    @Test
    public void checkIfItemsArePassedToView() {
        List<String> items = Arrays.asList("Model", "View", "Controller");
        presenter.onFinished(items);
        Mockito.verify(view, Mockito.times(1)).setItems(items);
        Mockito.verify(view, Mockito.times(1)).hideProgress();
    }
}

