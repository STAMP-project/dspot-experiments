package uk.ivanc.archimvvm;


import EditorInfo.IME_ACTION_SEARCH;
import MainViewModel.DataListener;
import R.string.error_username_not_found;
import R.string.text_empty_repos;
import View.INVISIBLE;
import View.VISIBLE;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import uk.ivanc.archimvvm.model.GithubService;
import uk.ivanc.archimvvm.model.Repository;
import uk.ivanc.archimvvm.util.MockModelFabric;
import uk.ivanc.archimvvm.viewmodel.MainViewModel;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainViewModelTest {
    GithubService githubService;

    ArchiApplication application;

    MainViewModel mainViewModel;

    DataListener dataListener;

    @Test
    public void shouldSearchUsernameWithRepos() {
        String username = "usernameWithRepos";
        TextView textView = new TextView(application);
        textView.setText(username);
        List<Repository> mockRepos = MockModelFabric.newListOfRepositories(10);
        Mockito.doReturn(Observable.just(mockRepos)).when(githubService).publicRepositories(username);
        mainViewModel.onSearchAction(textView, IME_ACTION_SEARCH, null);
        Mockito.verify(dataListener).onRepositoriesChanged(mockRepos);
        Assert.assertEquals(mainViewModel.infoMessageVisibility.get(), INVISIBLE);
        Assert.assertEquals(mainViewModel.progressVisibility.get(), INVISIBLE);
        Assert.assertEquals(mainViewModel.recyclerViewVisibility.get(), VISIBLE);
    }

    @Test
    public void shouldSearchInvalidUsername() {
        String username = "invalidUsername";
        TextView textView = new TextView(application);
        textView.setText(username);
        HttpException mockHttpException = new HttpException(Response.error(404, Mockito.mock(ResponseBody.class)));
        Mockito.when(githubService.publicRepositories(username)).thenReturn(Observable.<List<Repository>>error(mockHttpException));
        mainViewModel.onSearchAction(textView, IME_ACTION_SEARCH, null);
        Mockito.verify(dataListener, Mockito.never()).onRepositoriesChanged(ArgumentMatchers.anyListOf(Repository.class));
        Assert.assertEquals(mainViewModel.infoMessage.get(), application.getString(error_username_not_found));
        Assert.assertEquals(mainViewModel.infoMessageVisibility.get(), VISIBLE);
        Assert.assertEquals(mainViewModel.progressVisibility.get(), INVISIBLE);
        Assert.assertEquals(mainViewModel.recyclerViewVisibility.get(), INVISIBLE);
    }

    @Test
    public void shouldSearchUsernameWithNoRepos() {
        String username = "usernameWithoutRepos";
        TextView textView = new TextView(application);
        textView.setText(username);
        Mockito.when(githubService.publicRepositories(username)).thenReturn(Observable.just(Collections.<Repository>emptyList()));
        mainViewModel.onSearchAction(textView, IME_ACTION_SEARCH, null);
        Mockito.verify(dataListener).onRepositoriesChanged(Collections.<Repository>emptyList());
        Assert.assertEquals(mainViewModel.infoMessage.get(), application.getString(text_empty_repos));
        Assert.assertEquals(mainViewModel.infoMessageVisibility.get(), VISIBLE);
        Assert.assertEquals(mainViewModel.progressVisibility.get(), INVISIBLE);
        Assert.assertEquals(mainViewModel.recyclerViewVisibility.get(), INVISIBLE);
    }
}

