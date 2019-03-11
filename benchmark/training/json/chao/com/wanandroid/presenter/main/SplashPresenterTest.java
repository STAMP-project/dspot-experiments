package json.chao.com.wanandroid.presenter.main;


import SplashContract.View;
import json.chao.com.wanandroid.BasePresenterTest;
import json.chao.com.wanandroid.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;


/**
 *
 *
 * @author quchao
 * @unknown 2018/6/12
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class SplashPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private SplashPresenter mSplashPresenter;

    @Test
    public void jumpToMain() throws InterruptedException {
        Thread.sleep(3000);
        verify(mView).jumpToMain();
    }
}

