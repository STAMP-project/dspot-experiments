/**
 * Copyright 2006-2008 the original author or authors.
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
package org.springframework.batch.core.listener;


import java.util.Collections;
import org.junit.Test;
import org.springframework.batch.core.ItemProcessListener;


/**
 *
 *
 * @author Dave Syer
 * @author Will Schipp
 */
public class CompositeItemProcessListenerTests {
    private ItemProcessListener<Object, Object> listener;

    private CompositeItemProcessListener<Object, Object> compositeListener;

    @Test
    public void testBeforeRProcess() {
        Object item = new Object();
        listener.beforeProcess(item);
        compositeListener.beforeProcess(item);
    }

    @Test
    public void testAfterRead() {
        Object item = new Object();
        Object result = new Object();
        listener.afterProcess(item, result);
        compositeListener.afterProcess(item, result);
    }

    @Test
    public void testOnReadError() {
        Object item = new Object();
        Exception ex = new Exception();
        listener.onProcessError(item, ex);
        compositeListener.onProcessError(item, ex);
    }

    @Test
    public void testSetListeners() throws Exception {
        compositeListener.setListeners(Collections.<ItemProcessListener<? super Object, ? super Object>>singletonList(listener));
        listener.beforeProcess(null);
        compositeListener.beforeProcess(null);
    }
}

