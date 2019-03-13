/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.braintree;


import WebhookNotification.Kind.ACCOUNT_UPDATER_DAILY_REPORT;
import WebhookNotification.Kind.CONNECTED_MERCHANT_PAYPAL_STATUS_CHANGED;
import WebhookNotification.Kind.CONNECTED_MERCHANT_STATUS_TRANSITIONED;
import WebhookNotification.Kind.DISBURSEMENT;
import WebhookNotification.Kind.DISBURSEMENT_EXCEPTION;
import WebhookNotification.Kind.DISPUTE_LOST;
import WebhookNotification.Kind.DISPUTE_OPENED;
import WebhookNotification.Kind.DISPUTE_WON;
import WebhookNotification.Kind.PARTNER_MERCHANT_CONNECTED;
import WebhookNotification.Kind.PARTNER_MERCHANT_DECLINED;
import WebhookNotification.Kind.PARTNER_MERCHANT_DISCONNECTED;
import WebhookNotification.Kind.SUBSCRIPTION_CANCELED;
import WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY;
import WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY;
import WebhookNotification.Kind.SUBSCRIPTION_TRIAL_ENDED;
import WebhookNotification.Kind.SUBSCRIPTION_WENT_ACTIVE;
import WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE;
import WebhookNotification.Kind.SUB_MERCHANT_ACCOUNT_APPROVED;
import WebhookNotification.Kind.SUB_MERCHANT_ACCOUNT_DECLINED;
import WebhookNotification.Kind.TRANSACTION_DISBURSED;
import WebhookNotification.Kind.TRANSACTION_SETTLED;
import WebhookNotification.Kind.TRANSACTION_SETTLEMENT_DECLINED;
import com.braintreegateway.ConnectedMerchantPayPalStatusChanged;
import com.braintreegateway.ConnectedMerchantStatusTransitioned;
import com.braintreegateway.WebhookNotification;
import org.apache.camel.component.braintree.internal.BraintreeApiCollection;
import org.apache.camel.component.braintree.internal.WebhookNotificationGatewayApiMethod;
import org.junit.Test;


public class WebhookNotificationGatewayIntegrationTest extends AbstractBraintreeTestSupport {
    private static final String PATH_PREFIX = BraintreeApiCollection.getCollection().getApiName(WebhookNotificationGatewayApiMethod.class).getName();

    @Test
    public void testParseSubscription() throws Exception {
        runParseSubscriptionTest(SUBSCRIPTION_CANCELED);
        runParseSubscriptionTest(SUBSCRIPTION_CHARGED_SUCCESSFULLY);
        runParseSubscriptionTest(SUBSCRIPTION_CHARGED_UNSUCCESSFULLY);
        runParseSubscriptionTest(SUBSCRIPTION_TRIAL_ENDED);
        runParseSubscriptionTest(SUBSCRIPTION_WENT_ACTIVE);
        runParseSubscriptionTest(SUBSCRIPTION_WENT_PAST_DUE);
    }

    @Test
    public void testParseMerchantAccount() throws Exception {
        runParseMerchantAccountTest(SUB_MERCHANT_ACCOUNT_APPROVED);
        runParseMerchantAccountTest(SUB_MERCHANT_ACCOUNT_DECLINED);
    }

    @Test
    public void testParseTransaction() throws Exception {
        runParseTransactionTest(TRANSACTION_DISBURSED);
        runParseTransactionTest(TRANSACTION_SETTLED);
        runParseTransactionTest(TRANSACTION_SETTLEMENT_DECLINED);
    }

    @Test
    public void testParseDisbursement() throws Exception {
        runParseDisbursementTest(DISBURSEMENT);
        runParseDisbursementTest(DISBURSEMENT_EXCEPTION);
    }

    @Test
    public void testParseDispute() throws Exception {
        runParseDisputeTest(DISPUTE_OPENED);
        runParseDisputeTest(DISPUTE_LOST);
        runParseDisputeTest(DISPUTE_WON);
    }

    @Test
    public void testParsePartnerMerchant() throws Exception {
        runParsePartnerMerchantTest(PARTNER_MERCHANT_CONNECTED);
        runParsePartnerMerchantTest(PARTNER_MERCHANT_DISCONNECTED);
        runParsePartnerMerchantTest(PARTNER_MERCHANT_DECLINED);
    }

    @Test
    public void testParseConnectedMerchantStatusTransitioned() throws Exception {
        final WebhookNotification result = sendSampleNotification(CONNECTED_MERCHANT_STATUS_TRANSITIONED, "my_merchant_public_id");
        assertNotNull("parse result", result);
        assertEquals(CONNECTED_MERCHANT_STATUS_TRANSITIONED, result.getKind());
        ConnectedMerchantStatusTransitioned connectedMerchantStatusTransitioned = result.getConnectedMerchantStatusTransitioned();
        assertEquals("my_merchant_public_id", connectedMerchantStatusTransitioned.getMerchantPublicId());
        assertEquals("oauth_application_client_id", connectedMerchantStatusTransitioned.getOAuthApplicationClientId());
        assertEquals("new_status", connectedMerchantStatusTransitioned.getStatus());
    }

    @Test
    public void testParseConnectedMerchantPayPalStatusChanged() throws Exception {
        final WebhookNotification result = sendSampleNotification(CONNECTED_MERCHANT_PAYPAL_STATUS_CHANGED, "my_merchant_public_id");
        assertNotNull("parse result", result);
        assertEquals(CONNECTED_MERCHANT_PAYPAL_STATUS_CHANGED, result.getKind());
        ConnectedMerchantPayPalStatusChanged connectedMerchantPayPalStatusChanged = result.getConnectedMerchantPayPalStatusChanged();
        assertEquals("my_merchant_public_id", connectedMerchantPayPalStatusChanged.getMerchantPublicId());
        assertEquals("oauth_application_client_id", connectedMerchantPayPalStatusChanged.getOAuthApplicationClientId());
        assertEquals("link", connectedMerchantPayPalStatusChanged.getAction());
    }

    @Test
    public void testParseAccountUpdater() throws Exception {
        runParsePAccountUpdaterTest(ACCOUNT_UPDATER_DAILY_REPORT);
    }
}

