package com.zegoggles.smssync.activity.donation;


import BillingResponse.ERROR;
import BillingResponse.ITEM_ALREADY_OWNED;
import BillingResponse.ITEM_UNAVAILABLE;
import BillingResponse.OK;
import BillingResponse.USER_CANCELED;
import com.android.billingclient.api.Purchase;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class DonationActivityTest {
    DonationActivity activity;

    @Test
    public void testResultMessageOK() {
        activity.onPurchasesUpdated(OK, Collections.<Purchase>emptyList());
        assertToast("Donation successful, thank you!");
    }

    @Test
    public void testResultMessageItemUnavailable() {
        activity.onPurchasesUpdated(ITEM_UNAVAILABLE, Collections.<Purchase>emptyList());
        assertToast("Donation failed: not available");
    }

    @Test
    public void testResultMessageItemAlreadyOwned() {
        activity.onPurchasesUpdated(ITEM_ALREADY_OWNED, Collections.<Purchase>emptyList());
        assertToast("Donation failed: you have already donated");
    }

    @Test
    public void testResultMessageUserCanceled() {
        activity.onPurchasesUpdated(USER_CANCELED, Collections.<Purchase>emptyList());
        assertToast("Donation failed: canceled");
    }

    @Test
    public void testResultMessageError() {
        activity.onPurchasesUpdated(ERROR, Collections.<Purchase>emptyList());
        assertToast("Donation failed: unspecified error: 6");
    }
}

