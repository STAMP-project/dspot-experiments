/**
 * *****************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.auth.core;


import NoAuthenticationAuthenticationProvider.NO_AUTH_ID;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pentaho.di.core.auth.DelegatingKerberosConsumer;
import org.pentaho.di.core.auth.DelegatingKerberosConsumerForClassloaderBridging;
import org.pentaho.di.core.auth.DelegatingNoAuthConsumer;
import org.pentaho.di.core.auth.DelegatingUsernamePasswordConsumer;
import org.pentaho.di.core.auth.KerberosAuthenticationProvider;
import org.pentaho.di.core.auth.KerberosAuthenticationProviderProxyInterface;
import org.pentaho.di.core.auth.NoAuthenticationAuthenticationProvider;
import org.pentaho.di.core.auth.UsernamePasswordAuthenticationProvider;


public class AuthenticationManagerTest {
    private AuthenticationManager manager;

    private NoAuthenticationAuthenticationProvider noAuthenticationAuthenticationProvider;

    @SuppressWarnings("unchecked")
    @Test
    public void testNoAuthProviderAndConsumer() throws AuthenticationConsumptionException, AuthenticationFactoryException {
        manager.registerConsumerClass(DelegatingNoAuthConsumer.class);
        AuthenticationConsumer<Object, NoAuthenticationAuthenticationProvider> consumer = Mockito.mock(AuthenticationConsumer.class);
        manager.getAuthenticationPerformer(Object.class, AuthenticationConsumer.class, NO_AUTH_ID).perform(consumer);
        Mockito.verify(consumer).consume(noAuthenticationAuthenticationProvider);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUsernamePasswordProviderConsumer() throws AuthenticationConsumptionException, AuthenticationFactoryException {
        manager.registerConsumerClass(DelegatingNoAuthConsumer.class);
        manager.registerConsumerClass(DelegatingUsernamePasswordConsumer.class);
        UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider = new UsernamePasswordAuthenticationProvider("upass", "u", "pass");
        manager.registerAuthenticationProvider(usernamePasswordAuthenticationProvider);
        AuthenticationConsumer<Object, UsernamePasswordAuthenticationProvider> consumer = Mockito.mock(AuthenticationConsumer.class);
        manager.getAuthenticationPerformer(Object.class, AuthenticationConsumer.class, usernamePasswordAuthenticationProvider.getId()).perform(consumer);
        Mockito.verify(consumer).consume(usernamePasswordAuthenticationProvider);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testKerberosProviderConsumer() throws AuthenticationConsumptionException, AuthenticationFactoryException {
        manager.registerConsumerClass(DelegatingNoAuthConsumer.class);
        manager.registerConsumerClass(DelegatingUsernamePasswordConsumer.class);
        manager.registerConsumerClass(DelegatingKerberosConsumer.class);
        KerberosAuthenticationProvider kerberosAuthenticationProvider = new KerberosAuthenticationProvider("kerb", "kerb", true, "pass", true, "none");
        manager.registerAuthenticationProvider(kerberosAuthenticationProvider);
        AuthenticationConsumer<Object, KerberosAuthenticationProvider> consumer = Mockito.mock(AuthenticationConsumer.class);
        manager.getAuthenticationPerformer(Object.class, AuthenticationConsumer.class, kerberosAuthenticationProvider.getId()).perform(consumer);
        Mockito.verify(consumer).consume(kerberosAuthenticationProvider);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetSupportedPerformers() throws AuthenticationConsumptionException, AuthenticationFactoryException {
        manager.registerConsumerClass(DelegatingNoAuthConsumer.class);
        manager.registerConsumerClass(DelegatingUsernamePasswordConsumer.class);
        manager.registerConsumerClass(DelegatingKerberosConsumer.class);
        UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider = new UsernamePasswordAuthenticationProvider("upass", "u", "pass");
        manager.registerAuthenticationProvider(usernamePasswordAuthenticationProvider);
        KerberosAuthenticationProvider kerberosAuthenticationProvider = new KerberosAuthenticationProvider("kerb", "kerb", true, "pass", true, "none");
        manager.registerAuthenticationProvider(kerberosAuthenticationProvider);
        List<AuthenticationPerformer<Object, AuthenticationConsumer>> performers = manager.getSupportedAuthenticationPerformers(Object.class, AuthenticationConsumer.class);
        Assert.assertEquals(3, performers.size());
        Set<String> ids = new HashSet<String>(Arrays.asList(NO_AUTH_ID, usernamePasswordAuthenticationProvider.getId(), kerberosAuthenticationProvider.getId()));
        for (AuthenticationPerformer<Object, AuthenticationConsumer> performer : performers) {
            ids.remove(performer.getAuthenticationProvider().getId());
        }
        Assert.assertEquals(0, ids.size());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRegisterUnregisterProvider() throws AuthenticationFactoryException {
        manager.registerConsumerClass(DelegatingNoAuthConsumer.class);
        manager.registerConsumerClass(DelegatingUsernamePasswordConsumer.class);
        List<AuthenticationPerformer<Object, AuthenticationConsumer>> performers = manager.getSupportedAuthenticationPerformers(Object.class, AuthenticationConsumer.class);
        Assert.assertEquals(1, performers.size());
        Set<String> ids = new HashSet<String>(Arrays.asList(NO_AUTH_ID));
        for (AuthenticationPerformer<Object, AuthenticationConsumer> performer : performers) {
            ids.remove(performer.getAuthenticationProvider().getId());
        }
        Assert.assertEquals(0, ids.size());
        UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider = new UsernamePasswordAuthenticationProvider("upass", "u", "pass");
        manager.registerAuthenticationProvider(usernamePasswordAuthenticationProvider);
        performers = manager.getSupportedAuthenticationPerformers(Object.class, AuthenticationConsumer.class);
        Assert.assertEquals(2, performers.size());
        ids = new HashSet<String>(Arrays.asList(NO_AUTH_ID, usernamePasswordAuthenticationProvider.getId()));
        for (AuthenticationPerformer<Object, AuthenticationConsumer> performer : performers) {
            ids.remove(performer.getAuthenticationProvider().getId());
        }
        Assert.assertEquals(0, ids.size());
        manager.unregisterAuthenticationProvider(usernamePasswordAuthenticationProvider);
        performers = manager.getSupportedAuthenticationPerformers(Object.class, AuthenticationConsumer.class);
        Assert.assertEquals(1, performers.size());
        ids = new HashSet<String>(Arrays.asList(NO_AUTH_ID));
        for (AuthenticationPerformer<Object, AuthenticationConsumer> performer : performers) {
            ids.remove(performer.getAuthenticationProvider().getId());
        }
        Assert.assertEquals(0, ids.size());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testRegisterConsumerFactory() throws AuthenticationConsumptionException, AuthenticationFactoryException {
        AuthenticationConsumer<Object, KerberosAuthenticationProvider> authConsumer = Mockito.mock(AuthenticationConsumer.class);
        AuthenticationConsumerFactory<Object, AuthenticationConsumer, KerberosAuthenticationProvider> factory = Mockito.mock(AuthenticationConsumerFactory.class);
        Mockito.when(factory.getReturnType()).thenReturn(Object.class);
        Mockito.when(factory.getCreateArgType()).thenReturn(AuthenticationConsumer.class);
        Mockito.when(factory.getConsumedType()).thenReturn(KerberosAuthenticationProvider.class);
        Mockito.when(factory.create(authConsumer)).thenReturn(authConsumer);
        KerberosAuthenticationProvider kerberosAuthenticationProvider = new KerberosAuthenticationProvider("kerb", "kerb", true, "pass", true, "none");
        manager.registerAuthenticationProvider(kerberosAuthenticationProvider);
        manager.registerConsumerFactory(factory);
        manager.getAuthenticationPerformer(Object.class, AuthenticationConsumer.class, kerberosAuthenticationProvider.getId()).perform(authConsumer);
        Mockito.verify(authConsumer).consume(kerberosAuthenticationProvider);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClassLoaderBridgingPerformer() throws AuthenticationConsumptionException, AuthenticationFactoryException {
        manager.setAuthenticationPerformerFactory(new AuthenticationPerformerFactory() {
            @Override
            public <ReturnType, CreateArgType, ConsumedType> AuthenticationPerformer<ReturnType, CreateArgType> create(AuthenticationProvider authenticationProvider, AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> authenticationConsumer) {
                if (AuthenticationConsumerInvocationHandler.isCompatible(authenticationConsumer.getConsumedType(), authenticationProvider)) {
                    return new org.pentaho.di.core.auth.core.impl.ClassloaderBridgingAuthenticationPerformer<ReturnType, CreateArgType, ConsumedType>(authenticationProvider, authenticationConsumer);
                }
                return null;
            }
        });
        manager.registerConsumerClass(DelegatingNoAuthConsumer.class);
        manager.registerConsumerClass(DelegatingUsernamePasswordConsumer.class);
        manager.registerConsumerClass(DelegatingKerberosConsumerForClassloaderBridging.class);
        KerberosAuthenticationProvider kerberosAuthenticationProvider = new KerberosAuthenticationProvider("kerb", "kerb", true, "pass", true, "none");
        manager.registerAuthenticationProvider(kerberosAuthenticationProvider);
        AuthenticationConsumer<Object, KerberosAuthenticationProviderProxyInterface> consumer = Mockito.mock(AuthenticationConsumer.class);
        @SuppressWarnings("rawtypes")
        AuthenticationPerformer<Object, AuthenticationConsumer> performer = manager.getAuthenticationPerformer(Object.class, AuthenticationConsumer.class, kerberosAuthenticationProvider.getId());
        Assert.assertNotNull(performer);
        performer.perform(consumer);
        ArgumentCaptor<KerberosAuthenticationProviderProxyInterface> captor = ArgumentCaptor.forClass(KerberosAuthenticationProviderProxyInterface.class);
        Mockito.verify(consumer).consume(captor.capture());
        Assert.assertEquals(kerberosAuthenticationProvider.getId(), captor.getValue().getId());
        Assert.assertEquals(kerberosAuthenticationProvider.getDisplayName(), captor.getValue().getDisplayName());
        Assert.assertEquals(kerberosAuthenticationProvider.getPrincipal(), captor.getValue().getPrincipal());
        Assert.assertEquals(kerberosAuthenticationProvider.getPassword(), captor.getValue().getPassword());
        Assert.assertEquals(kerberosAuthenticationProvider.getKeytabLocation(), captor.getValue().getKeytabLocation());
        Assert.assertEquals(kerberosAuthenticationProvider.isUseKeytab(), captor.getValue().isUseKeytab());
        Assert.assertEquals(kerberosAuthenticationProvider.isUseExternalCredentials(), captor.getValue().isUseExternalCredentials());
    }
}

