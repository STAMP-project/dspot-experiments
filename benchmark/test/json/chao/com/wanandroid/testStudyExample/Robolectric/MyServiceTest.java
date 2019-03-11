package json.chao.com.wanandroid.testStudyExample.Robolectric;


import json.chao.com.wanandroid.BuildConfig;
import json.chao.com.wanandroid.test.MyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author quchao
 * @unknown 2018/6/6
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MyServiceTest {
    private ServiceController<MyService> mServiceController;

    private MyService mMyService;

    @Test
    public void serviceLifecycle() {
        mServiceController.create();
        mServiceController.startCommand(0, 0);
        mServiceController.bind();
        mServiceController.unbind();
        mServiceController.destroy();
    }
}

