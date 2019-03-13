/**
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
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
 */
package org.connectbot;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.connectbot.bean.HostBean;
import org.connectbot.mock.BeanAssertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Kenny Root
 */
@RunWith(AndroidJUnit4.class)
public class HostBeanTest {
    private static final String[] FIELDS = new String[]{ "nickname", "username", "hostname", "port" };

    private HostBean host1;

    private HostBean host2;

    @Test
    public void id_Equality() {
        host1.setId(1);
        host2.setId(1);
        Assert.assertTrue(host1.equals(host2));
        Assert.assertTrue(((host1.hashCode()) == (host2.hashCode())));
    }

    @Test
    public void id_Inequality() {
        host1.setId(1);
        host2.setId(2);
        // HostBeans shouldn't be equal when their IDs are not the same
        Assert.assertFalse("HostBeans are equal when their ID is different", host1.equals(host2));
        Assert.assertFalse("HostBean hash codes are equal when their ID is different", ((host1.hashCode()) == (host2.hashCode())));
    }

    @Test
    public void id_Equality2() {
        host1.setId(1);
        host2.setId(1);
        host2.setNickname("Work");
        host2.setUsername("alice");
        host2.setHostname("client.example.com");
        Assert.assertTrue("HostBeans are not equal when their ID is the same but other fields are different!", host1.equals(host2));
        Assert.assertTrue("HostBeans hashCodes are not equal when their ID is the same but other fields are different!", ((host1.hashCode()) == (host2.hashCode())));
    }

    @Test
    public void equals_Empty_Success() {
        HostBean bean1 = new HostBean();
        HostBean bean2 = new HostBean();
        Assert.assertEquals(bean1, bean2);
    }

    @Test
    public void equals_NicknameDifferent_Failure() {
        host1.setNickname("Work");
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_NicknameNull_Failure() {
        host1.setNickname(null);
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_ProtocolNull_Failure() {
        host1.setProtocol(null);
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_ProtocolDifferent_Failure() {
        host1.setProtocol("fake");
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_UserDifferent_Failure() {
        host1.setUsername("joe");
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_UserNull_Failure() {
        host1.setUsername(null);
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_HostDifferent_Failure() {
        host1.setHostname("work.example.com");
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void equals_HostNull_Failure() {
        host1.setHostname(null);
        Assert.assertNotEquals(host1, host2);
    }

    @Test
    public void testBeanMeetsEqualsContract() {
        BeanAssertions.assertMeetsEqualsContract(HostBean.class, HostBeanTest.FIELDS);
    }

    @Test
    public void testBeanMeetsHashCodeContract() {
        BeanAssertions.assertMeetsHashCodeContract(HostBean.class, HostBeanTest.FIELDS);
    }
}

