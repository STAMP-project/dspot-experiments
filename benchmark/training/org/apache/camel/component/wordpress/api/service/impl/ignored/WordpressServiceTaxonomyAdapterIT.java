/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.wordpress.api.service.impl.ignored;


import java.util.Map;
import org.apache.camel.component.wordpress.api.model.Taxonomy;
import org.apache.camel.component.wordpress.api.service.WordpressServiceTaxonomy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Not implemented yet")
public class WordpressServiceTaxonomyAdapterIT {
    private static WordpressServiceTaxonomy serviceTaxonomy;

    @Test
    public void testRetrieve() {
        final Taxonomy taxonomy = WordpressServiceTaxonomyAdapterIT.serviceTaxonomy.retrieve(null, "category");
        Assert.assertThat(taxonomy, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(taxonomy.getName(), CoreMatchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testList() {
        final Map<String, Taxonomy> taxs = WordpressServiceTaxonomyAdapterIT.serviceTaxonomy.list(null, null);
        Assert.assertThat(taxs, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        Assert.assertThat(taxs.size(), CoreMatchers.is(2));
    }
}

