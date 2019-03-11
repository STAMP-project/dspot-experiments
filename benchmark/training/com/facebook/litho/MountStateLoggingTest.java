/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import com.facebook.litho.testing.TestComponent;
import com.facebook.litho.testing.TestViewComponent;
import com.facebook.litho.testing.logging.TestComponentsLogger;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;


/**
 * Tests that Mount events are only logged when tracing is enabled.
 */
@RunWith(ComponentsTestRunner.class)
public class MountStateLoggingTest {
    private ComponentContext mContext;

    private TestComponentsLogger mComponentsLogger;

    @Test
    public void testLogWhenTracing() {
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final TestComponent child2 = TestViewComponent.create(mContext).build();
        mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        List<String> mountedNames = new ArrayList<>();
        List<String> unmountedNames = new ArrayList<>();
        mountedNames.add("TestViewComponent");
        mountedNames.add("TestViewComponent");
        verifyLoggingAndResetLogger(2, 0, mountedNames, unmountedNames);
    }

    @Test
    public void testNoLogWhenTracingDisabled() {
        mContext = new ComponentContext(RuntimeEnvironment.application, "tag", new TestComponentsLogger() {
            @Override
            public boolean isTracing(PerfEvent logEvent) {
                return false;
            }
        });
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final TestComponent child2 = TestViewComponent.create(mContext).build();
        mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        final List<PerfEvent> loggedPerfEvents = mComponentsLogger.getLoggedPerfEvents();
        assertThat(loggedPerfEvents).isEmpty();
    }
}

