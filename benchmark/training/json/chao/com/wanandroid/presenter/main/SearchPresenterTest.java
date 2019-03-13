package json.chao.com.wanandroid.presenter.main;


import SearchContract.View;
import json.chao.com.wanandroid.BasePresenterTest;
import json.chao.com.wanandroid.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author quchao
 * @unknown 2018/6/12
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class SearchPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private SearchPresenter mSearchPresenter;

    // @Test
    // public void addHistoryDataSuccess() {
    // mSearchPresenter.clearHistoryData();
    // mSearchPresenter.addHistoryData("ok");
    // Mockito.verify(mView).judgeToTheSearchListActivity();
    // }
    @Test
    public void getTopSearchDataSuccess() {
        mSearchPresenter.getTopSearchData();
        Mockito.verify(mView).showTopSearchData(ArgumentMatchers.any());
    }
}

