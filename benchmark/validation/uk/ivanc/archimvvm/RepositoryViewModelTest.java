package uk.ivanc.archimvvm;


import R.string.text_language;
import View.GONE;
import View.VISIBLE;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import uk.ivanc.archimvvm.model.GithubService;
import uk.ivanc.archimvvm.model.Repository;
import uk.ivanc.archimvvm.model.User;
import uk.ivanc.archimvvm.viewmodel.RepositoryViewModel;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RepositoryViewModelTest {
    GithubService githubService;

    ArchiApplication application;

    Repository repository;

    User owner;

    RepositoryViewModel viewModel;

    @Test
    public void shouldGetDescription() {
        Assert.assertEquals(repository.description, viewModel.getDescription());
    }

    @Test
    public void shouldGetHomepage() {
        Assert.assertEquals(repository.homepage, viewModel.getHomepage());
    }

    @Test
    public void shouldGetLanguage() {
        Assert.assertEquals(application.getString(text_language, repository.language), viewModel.getLanguage());
    }

    @Test
    public void shouldReturnHomepageVisibilityGone() {
        repository.homepage = null;
        Assert.assertEquals(GONE, viewModel.getHomepageVisibility());
    }

    @Test
    public void shouldReturnLanguageVisibilityGone() {
        repository.language = null;
        Assert.assertEquals(GONE, viewModel.getLanguageVisibility());
    }

    @Test
    public void shouldReturnForkVisibilityVisible() {
        repository.fork = true;
        Assert.assertEquals(VISIBLE, viewModel.getForkVisibility());
    }

    @Test
    public void shouldReturnForkVisibilityGone() {
        repository.fork = false;
        Assert.assertEquals(GONE, viewModel.getForkVisibility());
    }

    @Test
    public void shouldLoadFullOwnerOnInstantiation() {
        Assert.assertEquals(owner.name, viewModel.ownerName.get());
        Assert.assertEquals(owner.email, viewModel.ownerEmail.get());
        Assert.assertEquals(owner.location, viewModel.ownerLocation.get());
        Assert.assertEquals(VISIBLE, viewModel.ownerEmailVisibility.get());
        Assert.assertEquals(VISIBLE, viewModel.ownerLocationVisibility.get());
        Assert.assertEquals(VISIBLE, viewModel.ownerLayoutVisibility.get());
    }
}

