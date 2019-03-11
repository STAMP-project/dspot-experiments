package uk.ivanc.archimvvm;


import Observable.OnPropertyChangedCallback;
import R.string.text_forks;
import R.string.text_stars;
import R.string.text_watchers;
import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import uk.ivanc.archimvvm.model.Repository;
import uk.ivanc.archimvvm.viewmodel.ItemRepoViewModel;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ItemRepoViewModelTest {
    ArchiApplication application;

    @Test
    public void shouldGetName() {
        Repository repository = new Repository();
        repository.name = "ivan";
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(application, repository);
        Assert.assertEquals(repository.name, itemRepoViewModel.getName());
    }

    @Test
    public void shouldGetDescription() {
        Repository repository = new Repository();
        repository.description = "This is the description";
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(application, repository);
        Assert.assertEquals(repository.description, itemRepoViewModel.getDescription());
    }

    @Test
    public void shouldGetStars() {
        Repository repository = new Repository();
        repository.stars = 10;
        String expectedString = application.getString(text_stars, repository.stars);
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(application, repository);
        Assert.assertEquals(expectedString, itemRepoViewModel.getStars());
    }

    @Test
    public void shouldGetForks() {
        Repository repository = new Repository();
        repository.forks = 5;
        String expectedString = application.getString(text_forks, repository.forks);
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(application, repository);
        Assert.assertEquals(expectedString, itemRepoViewModel.getForks());
    }

    @Test
    public void shouldGetWatchers() {
        Repository repository = new Repository();
        repository.watchers = 7;
        String expectedString = application.getString(text_watchers, repository.watchers);
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(application, repository);
        Assert.assertEquals(expectedString, itemRepoViewModel.getWatchers());
    }

    @Test
    public void shouldStartActivityOnItemClick() {
        Repository repository = new Repository();
        Context mockContext = Mockito.mock(Context.class);
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(mockContext, repository);
        itemRepoViewModel.onItemClick(new android.view.View(application));
        Mockito.verify(mockContext).startActivity(ArgumentMatchers.any(Intent.class));
    }

    @Test
    public void shouldNotifyPropertyChangeWhenSetRepository() {
        Repository repository = new Repository();
        ItemRepoViewModel itemRepoViewModel = new ItemRepoViewModel(application, repository);
        Observable.OnPropertyChangedCallback mockCallback = Mockito.mock(OnPropertyChangedCallback.class);
        itemRepoViewModel.addOnPropertyChangedCallback(mockCallback);
        itemRepoViewModel.setRepository(repository);
        Mockito.verify(mockCallback).onPropertyChanged(ArgumentMatchers.any(Observable.class), ArgumentMatchers.anyInt());
    }
}

