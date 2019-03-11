package org.baeldung.persistence.service;


import java.io.Serializable;
import java.util.List;
import org.baeldung.persistence.model.Foo;
import org.baeldung.util.IDUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractServicePersistenceIntegrationTest<T extends Serializable> {
    // tests
    // find - one
    /*  */
    @Test
    public final void givenResourceDoesNotExist_whenResourceIsRetrieved_thenNoResourceIsReceived() {
        // When
        final Foo createdResource = getApi().findOne(IDUtil.randomPositiveLong());
        // Then
        Assert.assertNull(createdResource);
    }

    @Test
    public void givenResourceExists_whenResourceIsRetrieved_thenNoExceptions() {
        final Foo existingResource = persistNewEntity();
        getApi().findOne(existingResource.getId());
    }

    @Test
    public void givenResourceDoesNotExist_whenResourceIsRetrieved_thenNoExceptions() {
        getApi().findOne(IDUtil.randomPositiveLong());
    }

    @Test
    public void givenResourceExists_whenResourceIsRetrieved_thenTheResultIsNotNull() {
        final Foo existingResource = persistNewEntity();
        final Foo retrievedResource = getApi().findOne(existingResource.getId());
        Assert.assertNotNull(retrievedResource);
    }

    @Test
    public void givenResourceExists_whenResourceIsRetrieved_thenResourceIsRetrievedCorrectly() {
        final Foo existingResource = persistNewEntity();
        final Foo retrievedResource = getApi().findOne(existingResource.getId());
        Assert.assertEquals(existingResource, retrievedResource);
    }

    // find - one - by name
    // find - all
    /*  */
    @Test
    public void whenAllResourcesAreRetrieved_thenNoExceptions() {
        getApi().findAll();
    }

    /*  */
    @Test
    public void whenAllResourcesAreRetrieved_thenTheResultIsNotNull() {
        final List<Foo> resources = getApi().findAll();
        Assert.assertNotNull(resources);
    }

    /*  */
    @Test
    public void givenAtLeastOneResourceExists_whenAllResourcesAreRetrieved_thenRetrievedResourcesAreNotEmpty() {
        persistNewEntity();
        // When
        final List<Foo> allResources = getApi().findAll();
        // Then
        Assert.assertThat(allResources, Matchers.not(Matchers.<Foo>empty()));
    }

    /*  */
    @Test
    public void givenAnResourceExists_whenAllResourcesAreRetrieved_thenTheExistingResourceIsIndeedAmongThem() {
        final Foo existingResource = persistNewEntity();
        final List<Foo> resources = getApi().findAll();
        Assert.assertThat(resources, Matchers.hasItem(existingResource));
    }

    /*  */
    @Test
    public void whenAllResourcesAreRetrieved_thenResourcesHaveIds() {
        persistNewEntity();
        // When
        final List<Foo> allResources = getApi().findAll();
        // Then
        for (final Foo resource : allResources) {
            Assert.assertNotNull(resource.getId());
        }
    }

    // create
    /*  */
    @Test(expected = RuntimeException.class)
    public void whenNullResourceIsCreated_thenException() {
        getApi().create(null);
    }

    /*  */
    @Test
    public void whenResourceIsCreated_thenNoExceptions() {
        persistNewEntity();
    }

    /*  */
    @Test
    public void whenResourceIsCreated_thenResourceIsRetrievable() {
        final Foo existingResource = persistNewEntity();
        Assert.assertNotNull(getApi().findOne(existingResource.getId()));
    }

    /*  */
    @Test
    public void whenResourceIsCreated_thenSavedResourceIsEqualToOriginalResource() {
        final Foo originalResource = createNewEntity();
        final Foo savedResource = getApi().create(originalResource);
        Assert.assertEquals(originalResource, savedResource);
    }

    @Test(expected = RuntimeException.class)
    public void whenResourceWithFailedConstraintsIsCreated_thenException() {
        final Foo invalidResource = createNewEntity();
        invalidate(invalidResource);
        getApi().create(invalidResource);
    }

    // update
    /*  */
    @Test(expected = RuntimeException.class)
    public void whenNullResourceIsUpdated_thenException() {
        getApi().update(null);
    }

    /*  */
    @Test
    public void givenResourceExists_whenResourceIsUpdated_thenNoExceptions() {
        // Given
        final Foo existingResource = persistNewEntity();
        // When
        getApi().update(existingResource);
    }

    /**
     * - can also be the ConstraintViolationException which now occurs on the update operation will not be translated; as a consequence, it will be a TransactionSystemException
     */
    @Test(expected = RuntimeException.class)
    public void whenResourceIsUpdatedWithFailedConstraints_thenException() {
        final Foo existingResource = persistNewEntity();
        invalidate(existingResource);
        getApi().update(existingResource);
    }

    /*  */
    @Test
    public void givenResourceExists_whenResourceIsUpdated_thenUpdatesArePersisted() {
        // Given
        final Foo existingResource = persistNewEntity();
        // When
        change(existingResource);
        getApi().update(existingResource);
        final Foo updatedResource = getApi().findOne(existingResource.getId());
        // Then
        Assert.assertEquals(existingResource, updatedResource);
    }
}

