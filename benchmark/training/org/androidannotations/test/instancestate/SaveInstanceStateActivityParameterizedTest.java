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
package org.androidannotations.test.instancestate;


import android.os.Bundle;
import java.lang.reflect.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;


@RunWith(ParameterizedRobolectricTestRunner.class)
public class SaveInstanceStateActivityParameterizedTest {
    private Object value;

    private String fieldName;

    private Field field;

    public SaveInstanceStateActivityParameterizedTest(String fieldName, Object value) throws Exception {
        this.fieldName = fieldName;
        this.value = value;
        field = SaveInstanceStateActivity.class.getDeclaredField(fieldName);
        field.setAccessible(true);
    }

    @Test
    public void canSaveField() throws Exception {
        SaveInstanceStateActivity_ savedActivity = Robolectric.buildActivity(SaveInstanceStateActivity_.class).create().get();
        Bundle bundle = saveField(savedActivity);
        assertThat(bundle.get(fieldName)).isEqualTo(value);
    }

    @Test
    public void canLoadField() throws Exception {
        SaveInstanceStateActivity_ savedActivity = Robolectric.buildActivity(SaveInstanceStateActivity_.class).create().get();
        Bundle bundle = saveField(savedActivity);
        SaveInstanceStateActivity_ recreatedActivity = Robolectric.buildActivity(SaveInstanceStateActivity_.class).create().get();
        Object initialFieldValue = field.get(recreatedActivity);
        assertThat(initialFieldValue).isNotEqualTo(value);
        recreatedActivity.onCreate(bundle);
        Object loadedFieldValue = field.get(recreatedActivity);
        assertThat(loadedFieldValue).isEqualTo(value);
    }
}

