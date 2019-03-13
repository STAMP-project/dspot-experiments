/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.entity.filtering;


import CustomAnnotationLiteral.INSTANCE;
import EntityFilteringFeature.ENTITY_FILTERING_SCOPE;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.util.runner.ConcurrentRunner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.glassfish.jersey.internal.inject.CustomAnnotationLiteral.INSTANCE;
import static org.glassfish.jersey.tests.e2e.entity.filtering.PrimaryDetailedView.Factory.get;


/**
 *
 *
 * @author Michal Gajdos
 */
@RunWith(ConcurrentRunner.class)
public class EntityFilteringClientTest extends EntityFilteringTest {
    public static final MediaType ENTITY_FILTERING = new MediaType("entity", "filtering");

    @Path("/")
    @Consumes("entity/filtering")
    @Produces("entity/filtering")
    public static class Resource {
        @POST
        public String post(final String value) {
            return value;
        }
    }

    @Test
    public void testEntityAnnotationsPrimaryView() throws Exception {
        final String fields = target().request().post(javax.ws.rs.client.Entity.entity(new org.glassfish.jersey.tests.e2e.entity.filtering.domain.OneFilteringOnClassEntity(), EntityFilteringClientTest.ENTITY_FILTERING, new java.lang.annotation.Annotation[]{ get() }), String.class);
        EntityFilteringTest.assertSameFields(fields, ("field,accessor,property,subEntities.field2,subEntities.property2,subEntities.property1," + "subEntities.field1,defaultEntities.field,defaultEntities.property"));
    }

    @Test
    public void testEntityAnnotationsDefaultView() throws Exception {
        final String fields = target().request().post(javax.ws.rs.client.Entity.entity(new org.glassfish.jersey.tests.e2e.entity.filtering.domain.OneFilteringOnClassEntity(), EntityFilteringClientTest.ENTITY_FILTERING, new java.lang.annotation.Annotation[]{ new DefaultFilteringScope() }), String.class);
        MatcherAssert.assertThat(fields, CoreMatchers.equalTo(""));
    }

    @Test
    public void testEntityAnnotationsInvalidView() throws Exception {
        final String fields = target().request().post(javax.ws.rs.client.Entity.entity(new org.glassfish.jersey.tests.e2e.entity.filtering.domain.OneFilteringOnClassEntity(), EntityFilteringClientTest.ENTITY_FILTERING, new java.lang.annotation.Annotation[]{ INSTANCE }), String.class);
        MatcherAssert.assertThat(fields, CoreMatchers.equalTo(""));
    }

    @Test
    public void testConfigurationPrimaryView() throws Exception {
        testConfiguration(("field,accessor,property,subEntities.field2,subEntities.property2,subEntities.property1," + "subEntities.field1,defaultEntities.field,defaultEntities.property"), get());
    }

    @Test
    public void testConfigurationDefaultView() throws Exception {
        testConfiguration("", new DefaultFilteringScope());
    }

    @Test
    public void testConfigurationMultipleViews() throws Exception {
        testConfiguration(("field,accessor,property,subEntities.field2,subEntities.property2,subEntities.property1," + "subEntities.field1,defaultEntities.field,defaultEntities.property"), get(), INSTANCE);
    }

    @Test
    public void testInvalidConfiguration() throws Exception {
        final ClientConfig config = new ClientConfig().property(ENTITY_FILTERING_SCOPE, "invalid_value");
        configureClient(config);
        final String fields = target(getBaseUri()).request().post(javax.ws.rs.client.Entity.entity(new org.glassfish.jersey.tests.e2e.entity.filtering.domain.OneFilteringOnClassEntity(), EntityFilteringClientTest.ENTITY_FILTERING), String.class);
        MatcherAssert.assertThat(fields, CoreMatchers.equalTo(""));
    }

    @Test
    public void testEntityAnnotationsOverConfiguration() throws Exception {
        final ClientConfig config = new ClientConfig().property(ENTITY_FILTERING_SCOPE, SecondaryDetailedView.Factory.get());
        configureClient(config);
        final String fields = target(getBaseUri()).request().post(javax.ws.rs.client.Entity.entity(new org.glassfish.jersey.tests.e2e.entity.filtering.domain.ManyFilteringsOnClassEntity(), EntityFilteringClientTest.ENTITY_FILTERING, new java.lang.annotation.Annotation[]{ get() }), String.class);
        EntityFilteringTest.assertSameFields(fields, ("field,accessor,property,manyEntities.property1,manyEntities.field1,oneEntities.field2," + ("oneEntities.property2,oneEntities.property1,oneEntities.field1,defaultEntities.field,defaultEntities" + ".property")));
    }
}

