package org.robolectric.shadows;


import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowVpnServiceTest {
    private Context context;

    @Test
    public void prepare() throws Exception {
        Intent intent = new Intent("foo");
        ShadowVpnService.setPrepareResult(intent);
        assertThat(VpnService.prepare(context)).isEqualTo(intent);
    }
}

