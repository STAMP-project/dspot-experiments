package json.chao.com.wanandroid.presenter.main;


import R.string.account_password_null_tint;
import R.string.password_not_same;
import R.string.register_fail;
import RegisterContract.View;
import json.chao.com.wanandroid.BasePresenterTest;
import json.chao.com.wanandroid.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class RegisterPresenterTest extends BasePresenterTest {
    @Mock
    private View mView;

    private RegisterPresenter mRegisterPresenter;

    @Test
    public void noInputAllInfo() {
        mRegisterPresenter.getRegisterData("JsonChao", "", "123");
        Mockito.verify(mView).showSnackBar(mApplication.getString(account_password_null_tint));
    }

    @Test
    public void inputPasswordMismatching() {
        mRegisterPresenter.getRegisterData("JsonChao", "qaz", "qaz123");
        Mockito.verify(mView).showSnackBar(mApplication.getString(password_not_same));
    }

    @Test
    public void inputCorrectInfo() {
        // ?????????????????
        mRegisterPresenter.getRegisterData("13458524151", "qaz123", "qaz123");
        Mockito.verify(mView).showRegisterSuccess();
    }

    @Test
    public void inputExistInfo() {
        mRegisterPresenter.getRegisterData("2243963927", "qaz123", "qaz123");
        Mockito.verify(mView).showErrorMsg(mApplication.getString(register_fail));
        Mockito.verify(mView).showError();
    }
}

