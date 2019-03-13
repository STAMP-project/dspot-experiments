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
package org.androidannotations.test.eintentservice;


import android.content.Intent;
import org.androidannotations.test.EmptyActivityWithoutLayout;
import org.androidannotations.test.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static IntentServiceHandledAction.actionForTestHandled;


@RunWith(RobolectricTestRunner.class)
public class MyIntentServiceTest {
    @Test
    public void testAction() {
        actionForTestHandled = null;
        // Simulate call to intent builder and retrieve the configured Intent
        EmptyActivityWithoutLayout context = setupActivity(EmptyActivityWithoutLayout_.class);
        Intent intent = // 
        // 
        IntentServiceHandledAction_.intent(context).myActionOneParam("test").get();
        // Simulate the creation of IntentService by Android
        IntentServiceHandledAction intentServiceHandledAction = new IntentServiceHandledAction_();
        intentServiceHandledAction.onHandleIntent(intent);
        assertThat(IntentServiceHandledAction.actionForTestHandled).isEqualTo("test");
    }
}

