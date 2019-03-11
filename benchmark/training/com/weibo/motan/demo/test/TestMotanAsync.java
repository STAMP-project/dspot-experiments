/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.motan.demo.test;


import com.weibo.motan.demo.service.TestInterface;
import com.weibo.motan.demo.service.TestInterfaceAsync;
import com.weibo.motan.demo.service.TestSuperInterface;
import org.junit.Test;


public class TestMotanAsync {
    @Test
    public void testAsyncMethodGenerate() throws NoSuchMethodException, SecurityException {
        // direct methods and origin methods
        validateMethods(TestInterface.class.getDeclaredMethods(), TestInterfaceAsync.class);
        // methods from superinterface
        validateMethods(TestSuperInterface.class.getDeclaredMethods(), TestInterfaceAsync.class);
    }
}

