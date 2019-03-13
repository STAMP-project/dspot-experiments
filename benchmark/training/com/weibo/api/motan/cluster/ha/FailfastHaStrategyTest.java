/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.cluster.ha;


import com.weibo.api.motan.BaseTestCase;
import com.weibo.api.motan.cluster.LoadBalance;
import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.example.IWorld;
import com.weibo.api.motan.rpc.Referer;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.junit.Test;


/**
 * Failfast ha strategy.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-18
 */
public class FailfastHaStrategyTest extends BaseTestCase {
    private FailfastHaStrategy<IWorld> failfastHaStrategy = new FailfastHaStrategy<IWorld>();

    @Test
    @SuppressWarnings("unchecked")
    public void testCall() {
        final LoadBalance<IWorld> loadBalance = BaseTestCase.mockery.mock(LoadBalance.class);
        final Referer<IWorld> referer = BaseTestCase.mockery.mock(Referer.class);
        final Request request = BaseTestCase.mockery.mock(Request.class);
        final Response response = BaseTestCase.mockery.mock(Response.class);
        BaseTestCase.mockery.checking(new Expectations() {
            {
                one(loadBalance).select(request);
                will(returnValue(referer));
                one(referer).call(request);
                will(returnValue(response));
            }
        });
        TestCase.assertEquals(response, failfastHaStrategy.call(request, loadBalance));
    }

    @Test(expected = MotanServiceException.class)
    @SuppressWarnings("unchecked")
    public void testCallError() {
        final LoadBalance<IWorld> loadBalance = BaseTestCase.mockery.mock(LoadBalance.class);
        final Referer<IWorld> referer = BaseTestCase.mockery.mock(Referer.class);
        final Request request = BaseTestCase.mockery.mock(Request.class);
        BaseTestCase.mockery.checking(new Expectations() {
            {
                one(loadBalance).select(request);
                will(returnValue(referer));
                one(referer).call(request);
                will(returnValue(null));
            }
        });
        TestCase.assertEquals(null, failfastHaStrategy.call(request, loadBalance));
    }
}

