/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.memory;


import FinalizeManager.SOFT_REFERENCE_FACTORY;
import FinalizeManager.WEAK_REFERENCE_FACTORY;
import NewEnv.Type;
import com.liferay.portal.kernel.test.GCUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
@NewEnv(type = Type.CLASSLOADER)
public class FinalizeManagerTest {
    @ClassRule
    @Rule
    public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(CodeCoverageAssertor.INSTANCE, NewEnvTestRule.INSTANCE);

    @NewEnv(type = Type.NONE)
    @Test
    public void testConstructor() {
        new FinalizeManager();
    }

    @Test
    public void testManualClear() throws Exception {
        Object object1 = new Object();
        FinalizeManagerTest.MarkFinalizeAction markFinalizeAction1 = new FinalizeManagerTest.MarkFinalizeAction();
        Reference<Object> reference1 = FinalizeManager.register(object1, markFinalizeAction1, WEAK_REFERENCE_FACTORY);
        Map<Object, FinalizeAction> finalizeActions = ReflectionTestUtil.getFieldValue(FinalizeManager.class, "_finalizeActions");
        Assert.assertEquals(markFinalizeAction1, finalizeActions.get(FinalizeManagerTest._newIdentityKey(reference1)));
        reference1.clear();
        Assert.assertNull(finalizeActions.get(FinalizeManagerTest._newIdentityKey(reference1)));
        object1 = null;
        Object object2 = new Object();
        FinalizeManagerTest.MarkFinalizeAction markFinalizeAction2 = new FinalizeManagerTest.MarkFinalizeAction();
        Reference<Object> reference2 = FinalizeManager.register(object2, markFinalizeAction2, WEAK_REFERENCE_FACTORY);
        Assert.assertEquals(markFinalizeAction2, finalizeActions.get(FinalizeManagerTest._newIdentityKey(reference2)));
        reference1.enqueue();
        object2 = null;
        GCUtil.gc(true);
        _waitUntilMarked(markFinalizeAction2);
        Assert.assertFalse(markFinalizeAction1.isMarked());
    }

    @Test
    public void testRegisterationIdentity() throws Exception {
        String testString = new String("testString");
        FinalizeManagerTest.MarkFinalizeAction markFinalizeAction = new FinalizeManagerTest.MarkFinalizeAction();
        Reference<?> reference1 = FinalizeManager.register(testString, markFinalizeAction, SOFT_REFERENCE_FACTORY);
        Map<Object, FinalizeAction> finalizeActions = ReflectionTestUtil.getFieldValue(FinalizeManager.class, "_finalizeActions");
        Assert.assertEquals(finalizeActions.toString(), 1, finalizeActions.size());
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference1)));
        Reference<?> reference2 = FinalizeManager.register(testString, markFinalizeAction, SOFT_REFERENCE_FACTORY);
        Assert.assertEquals(reference1, reference2);
        Assert.assertNotSame(reference1, reference2);
        Assert.assertEquals(finalizeActions.toString(), 2, finalizeActions.size());
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference1)));
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference2)));
        reference2.clear();
        Assert.assertEquals(finalizeActions.toString(), 1, finalizeActions.size());
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference1)));
        reference2 = FinalizeManager.register(new String(testString), markFinalizeAction, SOFT_REFERENCE_FACTORY);
        Assert.assertEquals(finalizeActions.toString(), 2, finalizeActions.size());
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference1)));
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference2)));
        reference2.clear();
        Assert.assertEquals(finalizeActions.toString(), 1, finalizeActions.size());
        Assert.assertTrue(finalizeActions.containsKey(FinalizeManagerTest._newIdentityKey(reference1)));
        reference1.clear();
        Assert.assertTrue(finalizeActions.toString(), finalizeActions.isEmpty());
    }

    @Test
    public void testRegisterPhantom() throws InterruptedException {
        doTestRegister(FinalizeManagerTest.ReferenceType.PHANTOM);
    }

    @Test
    public void testRegisterSoft() throws InterruptedException {
        doTestRegister(FinalizeManagerTest.ReferenceType.SOFT);
    }

    @Test
    public void testRegisterWeak() throws InterruptedException {
        doTestRegister(FinalizeManagerTest.ReferenceType.WEAK);
    }

    private final BlockingQueue<String> _finalizedIds = new LinkedBlockingDeque<>();

    private static enum ReferenceType {

        PHANTOM,
        SOFT,
        WEAK;}

    private class FinalizeRecorder {
        public FinalizeRecorder(String id) {
            _id = id;
        }

        @Override
        protected void finalize() {
            _finalizedIds.offer(_id);
        }

        private final String _id;
    }

    private class MarkFinalizeAction implements FinalizeAction {
        @Override
        public void doFinalize(Reference<?> reference) {
            Object referent = _getReferent(reference);
            if (referent instanceof FinalizeManagerTest.FinalizeRecorder) {
                FinalizeManagerTest.FinalizeRecorder finalizeRecorder = ((FinalizeManagerTest.FinalizeRecorder) (referent));
                _id = finalizeRecorder._id;
            }
            _marked = true;
        }

        public String getId() {
            return _id;
        }

        public boolean isMarked() {
            return _marked;
        }

        private volatile String _id;

        private volatile boolean _marked;
    }
}

