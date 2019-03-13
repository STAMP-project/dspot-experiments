/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak.testsuite.admin;


import ComponentRepresentation.SECRET_VALUE;
import TestProvider.DetailsRepresentation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ComponentResource;
import org.keycloak.admin.client.resource.ComponentsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.components.TestProvider;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ComponentsTest extends AbstractAdminTest {
    private ComponentsResource components;

    private volatile CountDownLatch remainingDeleteSubmissions;

    private static final int NUMBER_OF_THREADS = 4;

    private static final int NUMBER_OF_TASKS = (ComponentsTest.NUMBER_OF_THREADS) * 5;

    private static final int NUMBER_OF_CHILDREN = 3;

    @Test
    public void testConcurrencyWithoutChildren() throws InterruptedException {
        testConcurrency(( s, i) -> s.submit(new ComponentsTest.CreateAndDeleteComponent(s, i)));
        Assert.assertThat(realm.components().query(realm.toRepresentation().getId(), TestProvider.class.getName()), Matchers.hasSize(0));
    }

    @Test
    public void testConcurrencyWithChildren() throws InterruptedException {
        testConcurrency(( s, i) -> s.submit(new ComponentsTest.CreateAndDeleteComponentWithFlatChildren(s, i)));
        Assert.assertThat(realm.components().query(realm.toRepresentation().getId(), TestProvider.class.getName()), Matchers.hasSize(0));
    }

    @Test
    public void testNotDeadlocked() {
        for (int i = 0; i < 50; i++) {
            ComponentRepresentation rep = createComponentRepresentation(("test-" + i));
            rep.getConfig().putSingle("required", "required-value");
            createComponent(rep);
            List<ComponentRepresentation> list = realm.components().query(realmId, TestProvider.class.getName());
            Assert.assertEquals((i + 1), list.size());
        }
    }

    @Test
    public void testCreateValidation() {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        // Check validation is invoked
        try {
            createComponent(rep);
        } catch (WebApplicationException e) {
            assertError(e.getResponse(), "'Required' is required");
        }
        rep.getConfig().putSingle("required", "Required");
        rep.getConfig().putSingle("number", "invalid");
        // Check validation is invoked
        try {
            createComponent(rep);
        } catch (WebApplicationException e) {
            assertError(e.getResponse(), "'Number' should be a number");
        }
    }

    @Test
    public void testCreateEmptyValues() {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        rep.getConfig().addFirst("required", "foo");
        rep.getConfig().addFirst("val1", "");
        rep.getConfig().put("val2", null);
        rep.getConfig().put("val3", Collections.emptyList());
        String id = createComponent(rep);
        ComponentRepresentation returned = components.component(id).toRepresentation();
        Assert.assertEquals("foo", returned.getSubType());
        Assert.assertEquals(1, returned.getConfig().size());
        Assert.assertTrue(returned.getConfig().containsKey("required"));
    }

    @Test
    public void testCreateWithoutGivenId() {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        rep.getConfig().addFirst("required", "foo");
        rep.setId(null);
        String id = createComponent(rep);
        Assert.assertNotNull(id);
    }

    @Test
    public void testCreateWithGivenId() {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        rep.getConfig().addFirst("required", "foo");
        rep.setId("fixed-id");
        String id = createComponent(rep);
        Assert.assertEquals("fixed-id", id);
    }

    @Test
    public void testUpdate() {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        rep.getConfig().addFirst("required", "foo");
        rep.getConfig().addFirst("val1", "one");
        rep.getConfig().addFirst("val2", "two");
        rep.getConfig().addFirst("val3", "three");
        String id = createComponent(rep);
        ComponentRepresentation returned = components.component(id).toRepresentation();
        Assert.assertEquals(4, returned.getConfig().size());
        Assert.assertEquals("foo", returned.getConfig().getFirst("required"));
        Assert.assertEquals("one", returned.getConfig().getFirst("val1"));
        Assert.assertEquals("two", returned.getConfig().getFirst("val2"));
        Assert.assertEquals("three", returned.getConfig().getFirst("val3"));
        // Check value updated
        returned.getConfig().putSingle("val1", "one-updated");
        // Check null deletes property
        returned.getConfig().putSingle("val2", null);
        components.component(id).update(returned);
        returned = components.component(id).toRepresentation();
        Assert.assertEquals(3, returned.getConfig().size());
        Assert.assertEquals("one-updated", returned.getConfig().getFirst("val1"));
        Assert.assertFalse(returned.getConfig().containsKey("val2"));
        // Check empty string is deleted
        returned.getConfig().addFirst("val1", "");
        components.component(id).update(returned);
        returned = components.component(id).toRepresentation();
        Assert.assertEquals(2, returned.getConfig().size());
        // Check empty list removes property
        returned.getConfig().put("val3", Collections.emptyList());
        components.component(id).update(returned);
        returned = components.component(id).toRepresentation();
        Assert.assertEquals(1, returned.getConfig().size());
    }

    @Test
    public void testRename() {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        rep.getConfig().addFirst("required", "foo");
        String id = createComponent(rep);
        ComponentRepresentation returned = components.component(id).toRepresentation();
        Assert.assertEquals("mycomponent", returned.getName());
        rep.setName("myupdatedcomponent");
        components.component(id).update(rep);
        returned = components.component(id).toRepresentation();
        Assert.assertEquals("myupdatedcomponent", returned.getName());
    }

    @Test
    public void testSecretConfig() throws Exception {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        rep.getConfig().addFirst("secret", "some secret value!!");
        rep.getConfig().addFirst("required", "some required value");
        String id = createComponent(rep);
        // Check secret value is not returned
        ComponentRepresentation returned = components.component(id).toRepresentation();
        Assert.assertEquals(SECRET_VALUE, returned.getConfig().getFirst("secret"));
        // Check secret not leaked in admin events
        AdminEventRepresentation event = testingClient.testing().pollAdminEvent();
        Assert.assertFalse(event.getRepresentation().contains("some secret value!!"));
        Assert.assertTrue(event.getRepresentation().contains(SECRET_VALUE));
        Map<String, TestProvider.DetailsRepresentation> details = testingClient.testing(AbstractAdminTest.REALM_NAME).getTestComponentDetails();
        // Check value is set correctly
        Assert.assertEquals("some secret value!!", details.get("mycomponent").getConfig().get("secret").get(0));
        returned.getConfig().putSingle("priority", "200");
        components.component(id).update(returned);
        ComponentRepresentation returned2 = components.component(id).toRepresentation();
        Assert.assertEquals(SECRET_VALUE, returned2.getConfig().getFirst("secret"));
        // Check secret not leaked in admin events
        event = testingClient.testing().pollAdminEvent();
        Assert.assertFalse(event.getRepresentation().contains("some secret value!!"));
        Assert.assertTrue(event.getRepresentation().contains(SECRET_VALUE));
        // Check secret value is not set to '*********'
        details = testingClient.testing(AbstractAdminTest.REALM_NAME).getTestComponentDetails();
        Assert.assertEquals("some secret value!!", details.get("mycomponent").getConfig().get("secret").get(0));
        returned2.getConfig().putSingle("secret", "updated secret value!!");
        components.component(id).update(returned2);
        // Check secret value is updated
        details = testingClient.testing(AbstractAdminTest.REALM_NAME).getTestComponentDetails();
        Assert.assertEquals("updated secret value!!", details.get("mycomponent").getConfig().get("secret").get(0));
        ComponentRepresentation returned3 = components.query().stream().filter(( c) -> c.getId().equals(returned2.getId())).findFirst().get();
        Assert.assertEquals(SECRET_VALUE, returned3.getConfig().getFirst("secret"));
    }

    @Test
    public void testLongValueInComponentConfigAscii() throws Exception {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        String value = StringUtils.repeat("0123456789", 400);// 4000 8-bit characters

        rep.getConfig().addFirst("required", "foo");
        rep.getConfig().addFirst("val1", value);
        String id = createComponent(rep);
        ComponentRepresentation returned = components.component(id).toRepresentation();
        Assert.assertEquals(value, returned.getConfig().getFirst("val1"));
    }

    @Test
    public void testLongValueInComponentConfigExtLatin() throws Exception {
        ComponentRepresentation rep = createComponentRepresentation("mycomponent");
        String value = StringUtils.repeat("??????????", 400);// 4000 Unicode extended-Latin characters

        rep.getConfig().addFirst("required", "foo");
        rep.getConfig().addFirst("val1", value);
        String id = createComponent(rep);
        ComponentRepresentation returned = components.component(id).toRepresentation();
        Assert.assertEquals(value, returned.getConfig().getFirst("val1"));
    }

    private class CreateComponent implements Runnable {
        protected final ExecutorService s;

        protected final int i;

        protected final RealmResource realm;

        public CreateComponent(ExecutorService s, int i, RealmResource realm) {
            this.s = s;
            this.i = i;
            this.realm = realm;
        }

        public CreateComponent(ExecutorService s, int i) {
            this(s, i, ComponentsTest.this.realm);
        }

        @Override
        public void run() {
            log.debugf("Started for i=%d ", i);
            ComponentRepresentation rep = createComponentRepresentation(("test-" + (i)));
            rep.getConfig().putSingle("required", "required-value");
            rep.setParentId(this.realm.toRepresentation().getId());
            String id = createComponent(this.realm, rep);
            Assert.assertThat(id, Matchers.notNullValue());
            createChildren(id);
            log.debugf("Finished: i=%d, id=%s", i, id);
            scheduleDeleteComponent(id);
            remainingDeleteSubmissions.countDown();
        }

        protected void scheduleDeleteComponent(String id) {
        }

        protected void createChildren(String id) {
        }
    }

    private class CreateAndDeleteComponent extends ComponentsTest.CreateComponent {
        public CreateAndDeleteComponent(ExecutorService s, int i) {
            super(s, i);
        }

        @Override
        protected void scheduleDeleteComponent(String id) {
            s.submit(new ComponentsTest.DeleteComponent(id));
        }
    }

    private class CreateComponentWithFlatChildren extends ComponentsTest.CreateComponent {
        public CreateComponentWithFlatChildren(ExecutorService s, int i, RealmResource realm) {
            super(s, i, realm);
        }

        public CreateComponentWithFlatChildren(ExecutorService s, int i) {
            super(s, i);
        }

        @Override
        protected void createChildren(String id) {
            for (int j = 0; j < (ComponentsTest.NUMBER_OF_CHILDREN); j++) {
                ComponentRepresentation rep = createComponentRepresentation(((("test-" + (i)) + ":") + j));
                rep.setParentId(id);
                rep.getConfig().putSingle("required", "required-value");
                Assert.assertThat(createComponent(this.realm, rep), Matchers.notNullValue());
            }
        }
    }

    private class CreateAndDeleteComponentWithFlatChildren extends ComponentsTest.CreateAndDeleteComponent {
        public CreateAndDeleteComponentWithFlatChildren(ExecutorService s, int i) {
            super(s, i);
        }

        @Override
        protected void createChildren(String id) {
            for (int j = 0; j < (ComponentsTest.NUMBER_OF_CHILDREN); j++) {
                ComponentRepresentation rep = createComponentRepresentation(((("test-" + (i)) + ":") + j));
                rep.setParentId(id);
                rep.getConfig().putSingle("required", "required-value");
                Assert.assertThat(createComponent(this.realm, rep), Matchers.notNullValue());
            }
        }
    }

    private class DeleteComponent implements Runnable {
        private final String id;

        public DeleteComponent(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            log.debugf("Started, id=%s", id);
            ComponentResource c = realm.components().component(id);
            Assert.assertThat(c.toRepresentation(), Matchers.notNullValue());
            c.remove();
            log.debugf("Finished, id=%s", id);
        }
    }
}

