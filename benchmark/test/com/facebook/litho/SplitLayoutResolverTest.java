/**
 * Copyright 2018-present Facebook, Inc.
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


import com.facebook.litho.config.LayoutThreadPoolConfiguration;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import java.util.Set;
import java.util.concurrent.ExecutorCompletionService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.shadows.ShadowLooper;


@PrepareForTest({ ThreadUtils.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@RunWith(ComponentsTestRunner.class)
public class SplitLayoutResolverTest {
    @Rule
    public PowerMockRule mPowerMockRule = new PowerMockRule();

    private Component mComponent;

    private static final String splitTag = "test_split_tag";

    private LayoutThreadPoolConfiguration mMainConfig;

    private LayoutThreadPoolConfiguration mBgConfig;

    private Set<String> mEnabledComponent;

    private ComponentContext mContext;

    private ExecutorCompletionService mainService;

    private ExecutorCompletionService bgService;

    private ShadowLooper mLayoutThreadShadowLooper;

    class MyTestComponent extends InlineLayoutSpec {
        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).child(TestDrawableComponent.create(mContext)).child(TestDrawableComponent.create(mContext)).child(TestDrawableComponent.create(mContext)).build();
        }
    }

    @Test
    public void testSplitMainThreadLayouts() {
        SplitLayoutResolver.createForTag(SplitLayoutResolverTest.splitTag, mMainConfig, mBgConfig, mEnabledComponent);
        SplitLayoutResolver resolver = SplitLayoutResolver.getForTag(SplitLayoutResolverTest.splitTag);
        Mockito.when(ThreadUtils.isMainThread()).thenReturn(true);
        Whitebox.setInternalState(resolver, "mainService", mainService);
        Whitebox.setInternalState(resolver, "bgService", bgService);
        ComponentTree tree = ComponentTree.create(mContext, mComponent).splitLayoutTag(SplitLayoutResolverTest.splitTag).build();
        tree.setRootAndSizeSpec(mComponent, SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), new Size());
        Mockito.verify(mainService).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0));
        Mockito.verify(mainService).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(1));
        Mockito.verify(mainService, Mockito.never()).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(2));
        Mockito.verify(bgService, Mockito.never()).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any());
    }

    @Test
    public void testSplitBgThreadLayouts() {
        SplitLayoutResolver.createForTag(SplitLayoutResolverTest.splitTag, mMainConfig, mBgConfig, mEnabledComponent);
        SplitLayoutResolver resolver = SplitLayoutResolver.getForTag(SplitLayoutResolverTest.splitTag);
        Mockito.when(ThreadUtils.isMainThread()).thenReturn(false);
        Whitebox.setInternalState(resolver, "mainService", mainService);
        Whitebox.setInternalState(resolver, "bgService", bgService);
        final ComponentTree tree = ComponentTree.create(mContext, mComponent).splitLayoutTag(SplitLayoutResolverTest.splitTag).build();
        tree.setRootAndSizeSpecAsync(mComponent, SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        mLayoutThreadShadowLooper.runOneTask();
        Mockito.verify(bgService).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0));
        Mockito.verify(bgService).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(1));
        Mockito.verify(bgService, Mockito.never()).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(2));
        Mockito.verify(mainService, Mockito.never()).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any());
    }

    @Test
    public void testOnlyMainEnabled() {
        SplitLayoutResolver.createForTag(SplitLayoutResolverTest.splitTag, mMainConfig, null, mEnabledComponent);
        SplitLayoutResolver resolver = SplitLayoutResolver.getForTag(SplitLayoutResolverTest.splitTag);
        Mockito.when(ThreadUtils.isMainThread()).thenReturn(false);
        Whitebox.setInternalState(resolver, "mainService", mainService);
        final ComponentTree tree = ComponentTree.create(mContext, mComponent).splitLayoutTag(SplitLayoutResolverTest.splitTag).build();
        tree.setRootAndSizeSpecAsync(mComponent, SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        mLayoutThreadShadowLooper.runOneTask();
        Mockito.verify(bgService, Mockito.never()).submit(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0));
    }
}

