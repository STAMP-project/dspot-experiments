package io.ribot.app;


import io.ribot.app.data.DataManager;
import io.ribot.app.ui.main.MainMvpView;
import io.ribot.app.ui.main.MainPresenter;
import io.ribot.app.util.RxSchedulersOverrideRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;


@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    @Mock
    MainMvpView mMockMainMvpView;

    @Mock
    DataManager mMockDataManager;

    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Test
    public void signOutSuccessful() {
        Mockito.doReturn(Observable.empty()).when(mMockDataManager).signOut();
        mMainPresenter.signOut();
        Mockito.verify(mMockMainMvpView).onSignedOut();
    }
}

