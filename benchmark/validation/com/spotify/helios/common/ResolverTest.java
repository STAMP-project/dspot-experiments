/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.common;


import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.LookupResult;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ResolverTest {
    @Mock
    DnsSrvResolver resolver;

    @Test
    public void testSupplier() throws Exception {
        final List<LookupResult> lookupResults = ImmutableList.of(LookupResult.create("master1.example.com", 443, 1, 1, 1), LookupResult.create("master2.example.com", 443, 1, 1, 1), LookupResult.create("master3.example.com", 443, 1, 1, 1));
        final URI[] expectedUris = new URI[]{ new URI("https://master1.example.com:443"), new URI("https://master2.example.com:443"), new URI("https://master3.example.com:443") };
        Mockito.when(resolver.resolve("_helios._https.example.com")).thenReturn(lookupResults);
        final Supplier<List<URI>> supplier = Resolver.supplier("helios", "example.com", resolver);
        final List<URI> uris = supplier.get();
        Assert.assertThat(uris.size(), Matchers.equalTo(3));
        Assert.assertThat(uris, Matchers.containsInAnyOrder(expectedUris));
    }

    @Test
    public void testSupplierWithHttpFallback() throws Exception {
        final List<LookupResult> lookupResults = ImmutableList.of(LookupResult.create("master1.example.com", 80, 1, 1, 1), LookupResult.create("master2.example.com", 80, 1, 1, 1), LookupResult.create("master3.example.com", 80, 1, 1, 1));
        final URI[] expectedUris = new URI[]{ new URI("http://master1.example.com:80"), new URI("http://master2.example.com:80"), new URI("http://master3.example.com:80") };
        Mockito.when(resolver.resolve("_helios._https.example.com")).thenReturn(Collections.<LookupResult>emptyList());
        Mockito.when(resolver.resolve("_helios._http.example.com")).thenReturn(lookupResults);
        final Supplier<List<URI>> supplier = Resolver.supplier("helios", "example.com", resolver);
        final List<URI> uris = supplier.get();
        Assert.assertThat(uris.size(), Matchers.equalTo(3));
        Assert.assertThat(uris, Matchers.containsInAnyOrder(expectedUris));
    }
}

