package json.chao.com.wanandroid.presenter.main;


import CollectContract.View;
import R.string.cancel_collect_fail;
import R.string.failed_to_obtain_collection_data;
import json.chao.com.wanandroid.BasePresenterTest;
import json.chao.com.wanandroid.BuildConfig;
import json.chao.com.wanandroid.component.RxBus;
import json.chao.com.wanandroid.core.bean.main.collect.FeedArticleData;
import json.chao.com.wanandroid.core.bean.main.collect.FeedArticleListData;
import json.chao.com.wanandroid.core.event.CollectEvent;
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
public class CollectPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private CollectPresenter mCollectPresenter;

    @Test
    public void collectBus() {
        RxBus.getDefault().post(new CollectEvent(false));
        Mockito.verify(mView).showRefreshEvent();
    }

    @Test
    public void getCollectListError() {
        // ??????Cookies???????
        CookiesManager.clearAllCookies();
        mCollectPresenter.getCollectList(0, false);
        Mockito.verify(mView).showErrorMsg(mApplication.getString(failed_to_obtain_collection_data));
    }

    @Test
    public void getCollectListErrorShow() {
        CookiesManager.clearAllCookies();
        mCollectPresenter.getCollectList(0, true);
        Mockito.verify(mView).showErrorMsg(mApplication.getString(failed_to_obtain_collection_data));
        Mockito.verify(mView).showError();
    }

    @Test
    public void getCollectListSuccess() {
        // ??Collect?????????Cookie
        login();
        mCollectPresenter.getCollectList(0, true);
        Mockito.verify(mView).showCollectList(ArgumentMatchers.any(FeedArticleListData.class));
    }

    @Test
    public void cancelCollectFail() {
        CookiesManager.clearAllCookies();
        FeedArticleData feedArticleData = new FeedArticleData();
        feedArticleData.setCollect(true);
        mCollectPresenter.cancelCollectPageArticle(0, feedArticleData);
        Mockito.verify(mView).showErrorMsg(mApplication.getString(cancel_collect_fail));
        Mockito.verify(mView).showError();
    }

    @Test
    public void cancelCollectSuccess() {
        login();
        FeedArticleData feedArticleData = new FeedArticleData();
        feedArticleData.setCollect(true);
        mCollectPresenter.cancelCollectPageArticle(0, feedArticleData);
        // ???eq()??????Equals????????????????????????????????????????
        feedArticleData.setCollect(false);
        // ??verify??????????????????????eq()?????????????
        Mockito.verify(mView).showCancelCollectPageArticleData(ArgumentMatchers.eq(0), ArgumentMatchers.eq(feedArticleData), ArgumentMatchers.any(FeedArticleListData.class));
    }
}

