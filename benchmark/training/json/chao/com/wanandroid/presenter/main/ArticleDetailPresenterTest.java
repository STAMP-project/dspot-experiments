package json.chao.com.wanandroid.presenter.main;


import ArticleDetailContract.View;
import R.string.cancel_collect_fail;
import R.string.collect_fail;
import json.chao.com.wanandroid.BasePresenterTest;
import json.chao.com.wanandroid.BuildConfig;
import json.chao.com.wanandroid.core.bean.main.collect.FeedArticleListData;
import json.chao.com.wanandroid.core.http.cookies.CookiesManager;
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
 * @unknown 2018/6/11
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ArticleDetailPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private ArticleDetailPresenter mArticleDetailPresenter;

    @Test
    public void collectFail() {
        CookiesManager.clearAllCookies();
        mArticleDetailPresenter.addCollectArticle(0);
        Mockito.verify(mView).showErrorMsg(mApplication.getString(collect_fail));
        Mockito.verify(mView).showError();
    }

    @Test
    public void collectSuccess() {
        login();
        mArticleDetailPresenter.addCollectArticle(0);
        Mockito.verify(mView).showCollectArticleData(ArgumentMatchers.any(FeedArticleListData.class));
    }

    @Test
    public void unCollectFail() {
        CookiesManager.clearAllCookies();
        mArticleDetailPresenter.cancelCollectArticle(0);
        Mockito.verify(mView).showErrorMsg(mApplication.getString(cancel_collect_fail));
        Mockito.verify(mView).showError();
    }

    @Test
    public void unCollectSuccess() {
        login();
        mArticleDetailPresenter.cancelCollectArticle(0);
        Mockito.verify(mView).showCancelCollectArticleData(ArgumentMatchers.any(FeedArticleListData.class));
    }

    @Test
    public void unCollectPagerFail() {
        CookiesManager.clearAllCookies();
        mArticleDetailPresenter.cancelCollectPageArticle(0);
        Mockito.verify(mView).showErrorMsg(mApplication.getString(cancel_collect_fail));
        Mockito.verify(mView).showError();
    }

    @Test
    public void unCollectPagerSuccess() {
        login();
        mArticleDetailPresenter.cancelCollectPageArticle(0);
        Mockito.verify(mView).showCancelCollectArticleData(ArgumentMatchers.any(FeedArticleListData.class));
    }
}

