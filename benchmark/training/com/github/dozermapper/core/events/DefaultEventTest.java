/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.events;


import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.fieldmap.FieldMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static EventTypes.MAPPING_STARTED;


public class DefaultEventTest {
    @Test
    public void canConstruct() {
        Event mockedEvent = new DefaultEvent(MAPPING_STARTED, Mockito.mock(ClassMap.class), Mockito.mock(FieldMap.class), Mockito.mock(Object.class), Mockito.mock(Object.class), Mockito.mock(Object.class));
        Assert.assertNotNull(mockedEvent.getType());
        Assert.assertNotNull(mockedEvent.getClassMap());
        Assert.assertNotNull(mockedEvent.getFieldMap());
        Assert.assertNotNull(mockedEvent.getSourceObject());
        Assert.assertNotNull(mockedEvent.getDestinationObject());
        Assert.assertNotNull(mockedEvent.getDestinationValue());
        Assert.assertNotNull(mockedEvent.toString());
    }
}

