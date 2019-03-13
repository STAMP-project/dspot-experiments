/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.plugin.request.util;


import com.navercorp.pinpoint.bootstrap.plugin.request.ServerRequestWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class NameSpaceCheckFactoryTest {
    @Test
    public void newNamespace_valid() {
        String myNamespace = "myNamespace";
        NameSpaceChecker<ServerRequestWrapper> myNamespaceChecker = newNameSpaceChecker(myNamespace);
        ServerRequestWrapper serverRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Mockito.when(serverRequestWrapper.getHeader(ArgumentMatchers.anyString())).thenReturn(myNamespace);
        Assert.assertTrue(myNamespaceChecker.checkNamespace(serverRequestWrapper));
    }

    @Test
    public void newNamespace_empty_namespace() {
        String myNamespace = "myNamespace";
        NameSpaceChecker<ServerRequestWrapper> myNamespaceChecker = newNameSpaceChecker(myNamespace);
        ServerRequestWrapper serverRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Mockito.when(serverRequestWrapper.getHeader(ArgumentMatchers.anyString())).thenReturn(null);
        Assert.assertTrue(myNamespaceChecker.checkNamespace(serverRequestWrapper));
    }

    @Test
    public void newNamespace_collision() {
        String myNamespace = "myNamespace";
        NameSpaceChecker<ServerRequestWrapper> myNamespaceChecker = newNameSpaceChecker(myNamespace);
        ServerRequestWrapper serverRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Mockito.when(serverRequestWrapper.getHeader(ArgumentMatchers.anyString())).thenReturn("collision_namespace");
        Assert.assertFalse(myNamespaceChecker.checkNamespace(serverRequestWrapper));
    }

    @Test
    public void newNamespace_empty_config() {
        String myNamespace = "";
        NameSpaceChecker<ServerRequestWrapper> myNamespaceChecker = newNameSpaceChecker(myNamespace);
        ServerRequestWrapper serverRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Mockito.when(serverRequestWrapper.getHeader(ArgumentMatchers.anyString())).thenReturn("invalid_namespace");
        Assert.assertTrue(myNamespaceChecker.checkNamespace(serverRequestWrapper));
    }
}

