/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon.delegates;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class SpoonSharedObjectDelegateTest {
    @Test
    public void isDuplicate_Same() {
        Assert.assertTrue(SpoonSharedObjectDelegate.isDuplicate(Collections.singletonList(mockObject("qwerty")), mockObject("qwerty")));
    }

    @Test
    public void isDuplicate_DifferentCase() {
        Assert.assertTrue(SpoonSharedObjectDelegate.isDuplicate(Collections.singletonList(mockObject("qwerty")), mockObject("Qwerty")));
    }

    @Test
    public void isDuplicate_NotSame() {
        Assert.assertFalse(SpoonSharedObjectDelegate.isDuplicate(Collections.singletonList(mockObject("qwerty")), mockObject("asdfg")));
    }
}

