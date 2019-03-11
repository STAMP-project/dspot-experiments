package json.chao.com.wanandroid.presenter.main;


import UsageDialogContract.View;
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
public class UsageDialogPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private UsageDialogPresenter mUsageDialogPresenter;

    @Test
    public void getUsageSites() {
        mUsageDialogPresenter.getUsefulSites();
        Mockito.verify(mView).showUsefulSites(ArgumentMatchers.any());
    }
}

