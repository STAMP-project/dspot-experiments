package org.robolectric.shadows;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowResultReceiverTest {
    @Test
    public void callingSend_shouldCallOverridenOnReceiveResultWithTheSameArguments() throws Exception {
        ShadowResultReceiverTest.TestResultReceiver testResultReceiver = new ShadowResultReceiverTest.TestResultReceiver(null);
        Bundle bundle = new Bundle();
        testResultReceiver.send(5, bundle);
        Assert.assertEquals(5, testResultReceiver.resultCode);
        Assert.assertEquals(bundle, testResultReceiver.resultData);
    }

    static class TestResultReceiver extends ResultReceiver {
        int resultCode;

        Bundle resultData;

        public TestResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            this.resultCode = resultCode;
            this.resultData = resultData;
        }
    }
}

