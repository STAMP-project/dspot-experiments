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


import java.lang.annotation.Annotation;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.tests.e2e.entity.filtering.DefaultFilteringScope;
import org.glassfish.jersey.tests.e2e.entity.filtering.PrimaryDetailedView;
import org.glassfish.jersey.tests.e2e.entity.filtering.SecondaryDetailedView;
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


/**
 * Use-cases with entity-filtering annotations on class, JSON output.
 *
 * @author Michal Gajdos
 */
@RunWith(Parameterized.class)
public class JsonEntityFilteringOnClassTest extends JerseyTest {
    public JsonEntityFilteringOnClassTest(final Class<Feature> filteringProvider) {
        super(new ResourceConfig(JsonEntityFilteringOnClassTest.Resource.class, EntityFilteringFeature.class).register(filteringProvider));
        enable(TestProperties.DUMP_ENTITY);
        enable(TestProperties.LOG_TRAFFIC);
    }

    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public static class Resource {
        @GET
        @Path("OneFilteringEntity")
        @PrimaryDetailedView
        public OneFilteringOnClassEntity getOneFilteringEntity() {
            return OneFilteringOnClassEntity.INSTANCE;
        }

        @GET
        @Path("OneFilteringEntityDefaultView")
        public OneFilteringOnClassEntity getOneFilteringEntityDefaultView() {
            return OneFilteringOnClassEntity.INSTANCE;
        }

        @POST
        @Path("OneFilteringEntity")
        public String postOneFilteringEntity(final String value) {
            return value;
        }

        @GET
        @Path("OneFilteringEntityDefaultViewResponse")
        public Response getOneFilteringEntityDefaultViewResponse() {
            return Response.ok().entity(OneFilteringOnClassEntity.INSTANCE, new Annotation[]{ new DefaultFilteringScope() }).build();
        }

        @GET
        @Path("ManyFilteringsEntityPrimaryView")
        @PrimaryDetailedView
        public ManyFilteringsOnClassEntity getManyFilteringsEntityPrimaryView() {
            return ManyFilteringsOnClassEntity.INSTANCE;
        }

        @GET
        @Path("ManyFilteringsEntitySecondaryView")
        @SecondaryDetailedView
        public ManyFilteringsOnClassEntity getManyFilteringsEntitySecondaryView() {
            return ManyFilteringsOnClassEntity.INSTANCE;
        }

        @GET
        @Path("ManyFilteringsEntityDefaultView")
        public ManyFilteringsOnClassEntity getManyFilteringsEntityDefaultView() {
            return ManyFilteringsOnClassEntity.INSTANCE;
        }

        @GET
        @Path("ManyFilteringsEntityManyViews")
        @PrimaryDetailedView
        @SecondaryDetailedView
        public ManyFilteringsOnClassEntity getManyFilteringsEntityManyViews() {
            return ManyFilteringsOnClassEntity.INSTANCE;
        }
    }

    @Test
    public void testOneEntityFilteringOnClass() throws Exception {
        final OneFilteringOnClassEntity entity = target("OneFilteringEntity").request().get(OneFilteringOnClassEntity.class);
        // OneFilteringOnClassEntity
        Assert.assertThat(entity.field, CoreMatchers.is(10));
        Assert.assertThat(entity.accessorTransient, CoreMatchers.is("propertyproperty"));
        Assert.assertThat(entity.getProperty(), CoreMatchers.is("property"));
        // FilteredClassEntity
        final FilteredClassEntity filtered = entity.getFiltered();
        Assert.assertThat(filtered, CoreMatchers.notNullValue());
        Assert.assertThat(filtered.field, CoreMatchers.is(0));
        Assert.assertThat(filtered.getProperty(), CoreMatchers.nullValue());
        // DefaultFilteringSubEntity
        Assert.assertThat(entity.getDefaultEntities(), CoreMatchers.notNullValue());
        Assert.assertThat(entity.getDefaultEntities().size(), CoreMatchers.is(1));
        final DefaultFilteringSubEntity defaultFilteringSubEntity = entity.getDefaultEntities().get(0);
        Assert.assertThat(defaultFilteringSubEntity.field, CoreMatchers.is(true));
        Assert.assertThat(defaultFilteringSubEntity.getProperty(), CoreMatchers.is(20L));
        // OneFilteringSubEntity
        Assert.assertThat(entity.getSubEntities(), CoreMatchers.notNullValue());
        Assert.assertThat(entity.getSubEntities().size(), CoreMatchers.is(1));
        final OneFilteringSubEntity oneFilteringSubEntity = entity.getSubEntities().get(0);
        Assert.assertThat(oneFilteringSubEntity.field1, CoreMatchers.is(20));
        Assert.assertThat(oneFilteringSubEntity.field2, CoreMatchers.is(30));
        Assert.assertThat(oneFilteringSubEntity.getProperty1(), CoreMatchers.is("property1"));
        Assert.assertThat(oneFilteringSubEntity.getProperty2(), CoreMatchers.is("property2"));
    }

    @Test
    public void testOneEntityFilteringOnClassDefaultViewResponse() throws Exception {
        final OneFilteringOnClassEntity entity = target("OneFilteringEntityDefaultViewResponse").request().get(OneFilteringOnClassEntity.class);
        // OneFilteringOnClassEntity
        Assert.assertThat(entity.field, CoreMatchers.is(0));
        Assert.assertThat(entity.accessorTransient, CoreMatchers.nullValue());
        Assert.assertThat(entity.getProperty(), CoreMatchers.nullValue());
        // FilteredClassEntity
        final FilteredClassEntity filtered = entity.getFiltered();
        Assert.assertThat(filtered, CoreMatchers.nullValue());
        // DefaultFilteringSubEntity
        Assert.assertThat(entity.getDefaultEntities(), CoreMatchers.nullValue());
        // OneFilteringSubEntity
        Assert.assertThat(entity.getSubEntities(), CoreMatchers.nullValue());
    }

    @Test
    public void testOneEntityFilteringOnClassDefaultView() throws Exception {
        final OneFilteringOnClassEntity entity = target("OneFilteringEntityDefaultView").request().get(OneFilteringOnClassEntity.class);
        // OneFilteringOnClassEntity
        Assert.assertThat(entity.field, CoreMatchers.is(0));
        Assert.assertThat(entity.accessorTransient, CoreMatchers.nullValue());
        Assert.assertThat(entity.getProperty(), CoreMatchers.nullValue());
        // FilteredClassEntity
        final FilteredClassEntity filtered = entity.getFiltered();
        Assert.assertThat(filtered, CoreMatchers.nullValue());
        // DefaultFilteringSubEntity
        Assert.assertThat(entity.getDefaultEntities(), CoreMatchers.nullValue());
        // OneFilteringSubEntity
        Assert.assertThat(entity.getSubEntities(), CoreMatchers.nullValue());
    }

    @Test
    public void testMultipleViewsOnClass() throws Exception {
        testOneEntityFilteringOnClass();
        testOneEntityFilteringOnClassDefaultView();
    }

    @Test
    public void testManyFilteringsEntityPrimaryView() throws Exception {
        final ManyFilteringsOnClassEntity entity = target("ManyFilteringsEntityPrimaryView").request().get(ManyFilteringsOnClassEntity.class);
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

    @Test
    public void testManyFilteringsEntitySecondaryView() throws Exception {
        final ManyFilteringsOnClassEntity entity = target("ManyFilteringsEntitySecondaryView").request().get(ManyFilteringsOnClassEntity.class);
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
        Assert.assertThat(oneFilteringSubEntity.field2, CoreMatchers.is(0));
        Assert.assertThat(oneFilteringSubEntity.getProperty1(), CoreMatchers.nullValue());
        Assert.assertThat(oneFilteringSubEntity.getProperty2(), CoreMatchers.is("property2"));
        // ManyFilteringsSubEntity
        Assert.assertThat(entity.manyEntities, CoreMatchers.notNullValue());
        Assert.assertThat(entity.manyEntities.size(), CoreMatchers.is(1));
        final ManyFilteringsSubEntity manyFilteringsSubEntity = entity.manyEntities.get(0);
        Assert.assertThat(manyFilteringsSubEntity.field1, CoreMatchers.is(60));
        Assert.assertThat(manyFilteringsSubEntity.field2, CoreMatchers.is(70));
        Assert.assertThat(manyFilteringsSubEntity.getProperty1(), CoreMatchers.nullValue());
        Assert.assertThat(manyFilteringsSubEntity.getProperty2(), CoreMatchers.is("property2"));
    }

    @Test
    public void testManyFilteringsEntityDefaultView() throws Exception {
        final ManyFilteringsOnClassEntity entity = target("ManyFilteringsEntityDefaultView").request().get(ManyFilteringsOnClassEntity.class);
        // ManyFilteringsOnClassEntity
        Assert.assertThat(entity.field, CoreMatchers.is(0));
        Assert.assertThat(entity.accessorTransient, CoreMatchers.nullValue());
        Assert.assertThat(entity.getProperty(), CoreMatchers.nullValue());
        // FilteredClassEntity
        final FilteredClassEntity filtered = entity.filtered;
        Assert.assertThat(filtered, CoreMatchers.nullValue());
        // DefaultFilteringSubEntity
        Assert.assertThat(entity.defaultEntities, CoreMatchers.nullValue());
        // OneFilteringSubEntity
        Assert.assertThat(entity.oneEntities, CoreMatchers.nullValue());
        // ManyFilteringsSubEntity
        Assert.assertThat(entity.manyEntities, CoreMatchers.nullValue());
    }

    @Test
    public void testManyFilteringsEntityManyViews() throws Exception {
        final ManyFilteringsOnClassEntity entity = target("ManyFilteringsEntityManyViews").request().get(ManyFilteringsOnClassEntity.class);
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
        Assert.assertThat(manyFilteringsSubEntity.field2, CoreMatchers.is(70));
        Assert.assertThat(manyFilteringsSubEntity.getProperty1(), CoreMatchers.is("property1"));
        Assert.assertThat(manyFilteringsSubEntity.getProperty2(), CoreMatchers.is("property2"));
    }
}

