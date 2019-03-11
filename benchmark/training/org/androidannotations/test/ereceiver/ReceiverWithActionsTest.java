/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.ereceiver;


import ReceiverWithActions.EXTRA_ARG_NAME1;
import ReceiverWithActions.EXTRA_ARG_NAME2;
import RuntimeEnvironment.application;
import android.content.Intent;
import android.net.Uri;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static ReceiverWithActions.ACTION_EXTRA_INTENT_PARAMETERS_TEST;
import static ReceiverWithActions.ACTION_EXTRA_PARAMETER_TEST;
import static ReceiverWithActions.ACTION_MULTIPLE_TEST_1;
import static ReceiverWithActions.ACTION_MULTIPLE_TEST_2;
import static ReceiverWithActions.ACTION_PARAMETER_TEST;
import static ReceiverWithActions.ACTION_SCHEME_TEST;
import static ReceiverWithActions.ACTION_SIMPLE_TEST;
import static ReceiverWithActions.DATA_SCHEME;


@RunWith(RobolectricTestRunner.class)
public class ReceiverWithActionsTest {
    private static final String EXTRA_PARAMETER_VALUE = "string value";

    private ReceiverWithActions receiver;

    @Test
    public void onSimpleActionTest() {
        receiver.onReceive(application, new Intent(ACTION_SIMPLE_TEST));
        Assert.assertTrue(receiver.simpleActionReceived);
    }

    @Test
    public void onActionWithReceiverTest() {
        Intent intent = new Intent(ACTION_SCHEME_TEST, Uri.parse(((DATA_SCHEME) + "://androidannotations.org")));
        receiver.onReceive(application, intent);
        Assert.assertTrue(receiver.actionWithSchemeReceived);
    }

    @Test
    public void onParameterActionTest() {
        Intent intent = new Intent(ACTION_PARAMETER_TEST);
        intent.putExtra(EXTRA_ARG_NAME2, ReceiverWithActionsTest.EXTRA_PARAMETER_VALUE);
        receiver.onReceive(application, intent);
        Assert.assertTrue(receiver.parameterActionReceived);
        Assert.assertEquals(ReceiverWithActionsTest.EXTRA_PARAMETER_VALUE, receiver.parameterActionValue);
    }

    @Test
    public void onExtraParameterActionTest() {
        Intent intent = new Intent(ACTION_EXTRA_PARAMETER_TEST);
        intent.putExtra(EXTRA_ARG_NAME1, ReceiverWithActionsTest.EXTRA_PARAMETER_VALUE);
        receiver.onReceive(application, intent);
        Assert.assertTrue(receiver.extraParameterActionReceived);
        Assert.assertEquals(ReceiverWithActionsTest.EXTRA_PARAMETER_VALUE, receiver.extraParameterActionValue);
    }

    @Test
    public void onMultipleActionsTest() {
        Assert.assertEquals(0, receiver.multipleActionCall);
        Intent intent = new Intent(ACTION_MULTIPLE_TEST_1);
        receiver.onReceive(application, intent);
        Assert.assertEquals(1, receiver.multipleActionCall);
        intent = new Intent(ACTION_MULTIPLE_TEST_2);
        receiver.onReceive(application, intent);
        Assert.assertEquals(2, receiver.multipleActionCall);
    }

    @Test
    public void onIntentParametersActionTest() {
        Intent intent = new Intent(ACTION_EXTRA_INTENT_PARAMETERS_TEST);
        Intent extraIntent = new Intent("someAction");
        intent.putExtra("extraIntent", extraIntent);
        receiver.onReceive(application, intent);
        Assert.assertEquals(intent, receiver.originalIntent);
        Assert.assertEquals(extraIntent, receiver.extraIntent);
    }
}

