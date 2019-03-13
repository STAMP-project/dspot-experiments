/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.e2e.entity.filtering.json;


import CustomAnnotationLiteral.INSTANCE;
import EntityFilteringFeature.ENTITY_FILTERING_SCOPE;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.tests.e2e.entity.filtering.DefaultFilteringScope;
import org.glassfish.jersey.tests.e2e.entity.filtering.domain.DefaultFilteringSubEntity;
import org.glassfish.jersey.tests.e2e.entity.filtering.domain.FilteredClassEntity;
import org.glassfish.jersey.tests.e2e.entity.filtering.domain.ManyFilteringsOnClassEntity;
import org.glassfish.jersey.tests.e2e.entity.filtering.domain.ManyFilteringsSubEntity;
import org.glassfish.jersey.tests.e2e.entity.filtering.domain.OneFilteringOnClassEntity;
import org.glassfish.jersey.tests.e2e.entity.filtering.domain.OneFilteringSubEntity;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.glassfish.jersey.internal.inject.CustomAnnotationLiteral.INSTANCE;
import static org.glassfish.jersey.tests.e2e.entity.filtering.PrimaryDetailedView.Factory.get;


/**
 *
 *
 * @author Michal Gajdos
 */
@RunWith(Parameterized.class)
public class JsonEntityFilteringClientTest extends JerseyTest {
    @Parameterized.Parameter
    public Class<Feature> filteringProvider;

    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public static class Resource {
        @POST
        public String post(final String value) {
            return value;
        }
    }

    @Test
    public void testEntityAnnotationsPrimaryView() throws Exception {
        final OneFilteringOnClassEntity entity = target().request().post(javax.ws.rs.client.Entity.entity(OneFilteringOnClassEntity.INSTANCE, MediaType.APPLICATION_JSON_TYPE, new java.lang.annotation.Annotation[]{ get() }), OneFilteringOnClassEntity.class);
        _testPrimaryViewEntity(entity);
    }

    @Test
    public void testEntityAnnotationsDefaultView() throws Exception {
        final OneFilteringOnClassEntity entity = target().request().post(javax.ws.rs.client.Entity.entity(OneFilteringOnClassEntity.INSTANCE, MediaType.APPLICATION_JSON_TYPE, new java.lang.annotation.Annotation[]{ new DefaultFilteringScope() }), OneFilteringOnClassEntity.class);
        _testEmptyEntity(entity);
    }

    @Test
    public void testEntityAnnotationsInvalidView() throws Exception {
        final OneFilteringOnClassEntity entity = target().request().post(javax.ws.rs.client.Entity.entity(OneFilteringOnClassEntity.INSTANCE, MediaType.APPLICATION_JSON_TYPE, new java.lang.annotation.Annotation[]{ INSTANCE }), OneFilteringOnClassEntity.class);
        _testEmptyEntity(entity);
    }

    @Test
    public void testConfigurationPrimaryView() throws Exception {
        _testPrimaryViewEntity(retrieveEntity(get()));
    }

    @Test
    public void testConfigurationDefaultView() throws Exception {
        _testEmptyEntity(retrieveEntity(new DefaultFilteringScope()));
    }

    @Test
    public void testConfigurationMultipleViews() throws Exception {
        _testPrimaryViewEntity(retrieveEntity(get(), INSTANCE));
    }

    @Test
    public void testInvalidConfiguration() throws Exception {
        final ClientConfig config = new ClientConfig().property(ENTITY_FILTERING_SCOPE, "invalid_value");
        configureClient(config);
        final OneFilteringOnClassEntity entity = target(getBaseUri()).request().post(javax.ws.rs.client.Entity.entity(OneFilteringOnClassEntity.INSTANCE, MediaType.APPLICATION_JSON_TYPE), OneFilteringOnClassEntity.class);
        _testEmptyEntity(entity);
    }

    @Test
    public void testEntityAnnotationsOverConfiguration() throws Exception {
        final ClientConfig config = new ClientConfig().property(ENTITY_FILTERING_SCOPE, org.glassfish.jersey.tests.e2e.entity.filtering.SecondaryDetailedView.Factory.get());
        configureClient(config);
        final ManyFilteringsOnClassEntity entity = target(getBaseUri()).request().post(javax.ws.rs.client.Entity.entity(ManyFilteringsOnClassEntity.INSTANCE, MediaType.APPLICATION_JSON_TYPE, new java.lang.annotation.Annotation[]{ get() }), ManyFilteringsOnClassEntity.class);
        // ManyFilteringsOnClassEntity
        Assert.assertThat(entity.field, CoreMatchers.is(50));
        Assert.assertThat(entity.accessorTransient, CoreMatchers.is("propertyproperty"));
        Assert.assertThat(entity.getProperty(), CoreMatchers.is("property"));
        // FilteredClassEntity
        final FilteredClassEntity filtered = entity.filtered;
        Assert.assertThat(filtered, CoreMatchers.notNullValue());
        Assert.assertThat(filtered.field, CoreMatchers.is(0));
        Assert.assertThat(filtered.getProperty(), CoreMatchers.nullValue());
        // DefaultFilteringSubEntity
        Assert.assertThat(entity.defaultEntities, CoreMatchers.notNullValue());
        Assert.assertThat(entity.defaultEntities.size(), CoreMatchers.is(1));
        final DefaultFilteringSubEntity defaultFilteringSubEntity = entity.defaultEntities.get(0);
        Assert.assertThat(defaultFilteringSubEntity.field, CoreMatchers.is(true));
        Assert.assertThat(defaultFilteringSubEntity.getProperty(), CoreMatchers.is(20L));
        // OneFilteringSubEntity
        Assert.assertThat(entity.oneEntities, CoreMatchers.notNullValue());
        Assert.assertThat(entity.oneEntities.size(), CoreMatchers.is(1));
        final OneFilteringSubEntity oneFilteringSubEntity = entity.oneEntities.get(0);
        Assert.assertThat(oneFilteringSubEntity.field1, CoreMatchers.is(20));
        Assert.assertThat(oneFilteringSubEntity.field2, CoreMatchers.is(30));
        Assert.assertThat(oneFilteringSubEntity.getProperty1(), CoreMatchers.is("property1"));
        Assert.assertThat(oneFilteringSubEntity.getProperty2(), CoreMatchers.is("property2"));
        // ManyFilteringsSubEntity
        Assert.assertThat(entity.manyEntities, CoreMatchers.notNullValue());
        Assert.assertThat(entity.manyEntities.size(), CoreMatchers.is(1));
        final ManyFilteringsSubEntity manyFilteringsSubEntity = entity.manyEntities.get(0);
        Assert.assertThat(manyFilteringsSubEntity.field1, CoreMatchers.is(60));
        Assert.assertThat(manyFilteringsSubEntity.field2, CoreMatchers.is(0));
        Assert.assertThat(manyFilteringsSubEntity.getProperty1(), CoreMatchers.is("property1"));
        Assert.assertThat(manyFilteringsSubEntity.getProperty2(), CoreMatchers.nullValue());
    }
}

