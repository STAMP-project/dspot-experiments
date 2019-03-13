package com.hitherejoe.bourboncorecommon.ui.browse;


import com.hitherejoe.bourboncorecommon.data.DataManager;
import com.hitherejoe.bourboncorecommon.data.model.Shot;
import com.hitherejoe.bourboncorecommon.util.RxSchedulersOverrideRule;
import com.hitherejoe.bourboncorecommon.util.TestDataFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Single;


@RunWith(MockitoJUnitRunner.class)
public class BrowsePresenterTest {
    @Mock
    BrowseMvpView mMockBrowseMvpView;

    @Mock
    DataManager mMockDataManager;

    private BrowsePresenter mBrowsePresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Test
    public void getShotsSuccessful() {
        List<Shot> shots = TestDataFactory.makeShots(5);
        stubDataManagerGetShots(Single.just(shots));
        mBrowsePresenter.getShots(0, 0);
        Mockito.verify(mMockBrowseMvpView).showProgress();
        Mockito.verify(mMockBrowseMvpView).showShots(shots);
        Mockito.verify(mMockBrowseMvpView).hideProgress();
    }

    @Test
    public void getShotsEmpty() {
        List<Shot> shots = new ArrayList<>();
        stubDataManagerGetShots(Single.just(shots));
        mBrowsePresenter.getShots(0, 0);
        Mockito.verify(mMockBrowseMvpView).showProgress();
        Mockito.verify(mMockBrowseMvpView).showEmpty();
        Mockito.verify(mMockBrowseMvpView).hideProgress();
    }

    @Test
    public void getShotsFailure() {
        stubDataManagerGetShots(Single.<List<Shot>>error(new RuntimeException()));
        mBrowsePresenter.getShots(0, 0);
        Mockito.verify(mMockBrowseMvpView).showProgress();
        Mockito.verify(mMockBrowseMvpView).showError();
        Mockito.verify(mMockBrowseMvpView).hideProgress();
    }
}

