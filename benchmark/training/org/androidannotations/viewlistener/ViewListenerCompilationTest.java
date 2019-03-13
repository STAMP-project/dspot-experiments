/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.viewlistener;


import java.io.IOException;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Test;


public class ViewListenerCompilationTest extends AAProcessorTestHelper {
    @Test
    public void ensureAdapterViewListenerCompilationSuccessful() throws IOException {
        assertCompilationSuccessful(compileFiles(AdapterViewListenerActivity.class));
    }

    @Test
    public void ensureTextViewListenerCompilationSuccessful() throws IOException {
        assertCompilationSuccessful(compileFiles(TextViewListenerActivity.class));
    }

    @Test
    public void ensureTextViewListenerCompilationError() throws IOException {
        assertCompilationError(compileFiles(BadEditorActionViewListenerActivity.class));
    }

    @Test
    public void ensureCompundButtonListenerCompilationSuccessful() throws IOException {
        assertCompilationSuccessful(compileFiles(CompoundButtonListenerActivity.class));
    }
}

