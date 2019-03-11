package json.chao.com.wanandroid.presenter.navigation;


import NavigationContract.View;
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
public class NavigationPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private NavigationPresenter mNavigationPresenter;

    @Test
    public void getNavigationListData() {
        mNavigationPresenter.getNavigationListData(true);
        Mockito.verify(mView).showNavigationListData(ArgumentMatchers.any());
    }
}

