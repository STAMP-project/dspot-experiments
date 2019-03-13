package json.chao.com.wanandroid.presenter.hierarchy;


import KnowledgeHierarchyContract.View;
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
public class KnowledgeHierarchyPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private KnowledgeHierarchyPresenter mKnowledgeHierarchyPresenter;

    @Test
    public void getKnowledgeHierarchyData() {
        mKnowledgeHierarchyPresenter.getKnowledgeHierarchyData(true);
        Mockito.verify(mView).showKnowledgeHierarchyData(ArgumentMatchers.any());
    }
}

