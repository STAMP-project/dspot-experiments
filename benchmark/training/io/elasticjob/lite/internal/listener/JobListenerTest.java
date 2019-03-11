/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.internal.listener;


import io.elasticjob.lite.internal.listener.fixture.FooJobListener;
import java.util.List;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class JobListenerTest {
    @Mock
    private TreeCacheEvent event;

    @Mock
    private List list;

    private FooJobListener fooJobListener;

    @Test
    public void assertChildEventWhenEventDataIsEmpty() throws Exception {
        Mockito.when(event.getData()).thenReturn(null);
        fooJobListener.childEvent(null, event);
        Mockito.verify(list, Mockito.times(0)).clear();
    }

    @Test
    public void assertChildEventSuccess() throws Exception {
        Mockito.when(event.getData()).thenReturn(new ChildData("/test_job", null, null));
        fooJobListener.childEvent(null, event);
        Mockito.verify(list).clear();
    }
}

