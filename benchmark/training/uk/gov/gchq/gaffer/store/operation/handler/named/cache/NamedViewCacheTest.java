/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.operation.handler.named.cache;


import TestGroups.EDGE;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.elementdefinition.view.NamedViewDetail;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.user.User;


public class NamedViewCacheTest {
    private static NamedViewCache cache;

    private static final String GAFFER_USER = "gaffer user";

    private static final String ADVANCED_GAFFER_USER = "advanced gaffer user";

    private static final String ADMIN_AUTH = "admin auth";

    private static final String EMPTY_ADMIN_AUTH = "";

    private static final String EXCEPTION_EXPECTED = "Exception expected";

    private static final String STANDARD_VIEW_NAME = "standardView";

    private static final String ALTERNATIVE_VIEW_NAME = "alternativeView";

    private View standardView = new View.Builder().build();

    private View alternativeView = new View.Builder().edge(EDGE).build();

    private final User blankUser = new User();

    private User standardUser = new User.Builder().opAuths(NamedViewCacheTest.GAFFER_USER).userId("123").build();

    private User userWithAdminAuth = new User.Builder().opAuths(NamedViewCacheTest.ADMIN_AUTH).userId("adminUser").build();

    private User advancedUser = new User.Builder().opAuths(NamedViewCacheTest.GAFFER_USER, NamedViewCacheTest.ADVANCED_GAFFER_USER).userId("456").build();

    private NamedViewDetail standard = new NamedViewDetail.Builder().name(NamedViewCacheTest.STANDARD_VIEW_NAME).description("standard View").creatorId(standardUser.getUserId()).view(standardView).build();

    private NamedViewDetail alternative = new NamedViewDetail.Builder().name(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).description("alternative View").creatorId(advancedUser.getUserId()).view(alternativeView).build();

    @Test
    public void shouldAddNamedView() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(standard, false);
        NamedViewDetail namedViewFromCache = NamedViewCacheTest.cache.getNamedView(standard.getName());
        Assert.assertEquals(standard, namedViewFromCache);
    }

    @Test
    public void shouldThrowExceptionIfNamedViewAlreadyExists() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(standard, false);
        try {
            NamedViewCacheTest.cache.addNamedView(standard, false);
            Assert.fail(NamedViewCacheTest.EXCEPTION_EXPECTED);
        } catch (OverwritingException e) {
            Assert.assertTrue(e.getMessage().equals(("Cache entry already exists for key: " + (NamedViewCacheTest.STANDARD_VIEW_NAME))));
        }
    }

    @Test
    public void shouldThrowExceptionWhenDeletingIfKeyIsNull() throws CacheOperationFailedException {
        try {
            NamedViewCacheTest.cache.deleteNamedView(null);
            Assert.fail(NamedViewCacheTest.EXCEPTION_EXPECTED);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("NamedView name cannot be null"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenGettingIfKeyIsNull() throws CacheOperationFailedException {
        try {
            NamedViewCacheTest.cache.getNamedView(null);
            Assert.fail(NamedViewCacheTest.EXCEPTION_EXPECTED);
        } catch (CacheOperationFailedException e) {
            Assert.assertTrue(e.getMessage().contains("NamedView name cannot be null"));
        }
    }

    @Test
    public void shouldRemoveNamedView() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(standard, false);
        NamedViewCacheTest.cache.deleteNamedView(standard.getName());
    }

    @Test
    public void shouldReturnEmptySetIfThereAreNoOperationsInTheCache() throws CacheOperationFailedException {
        CloseableIterable<NamedViewDetail> views = NamedViewCacheTest.cache.getAllNamedViews();
        Assert.assertEquals(0, Iterables.size(views));
    }

    @Test
    public void shouldBeAbleToReturnAllNamedViewsFromCache() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(standard, false);
        NamedViewCacheTest.cache.addNamedView(alternative, false);
        Set<NamedViewDetail> allViews = Sets.newHashSet(NamedViewCacheTest.cache.getAllNamedViews());
        Assert.assertTrue(allViews.contains(standard));
        Assert.assertTrue(allViews.contains(alternative));
        Assert.assertEquals(2, allViews.size());
    }

    @Test
    public void shouldAllowUsersWriteAccessToTheirOwnViews() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(standard, false, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        NamedViewCacheTest.cache.addNamedView(new NamedViewDetail.Builder().name(NamedViewCacheTest.STANDARD_VIEW_NAME).view("").build(), true, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        Assert.assertEquals("", NamedViewCacheTest.cache.getNamedView(NamedViewCacheTest.STANDARD_VIEW_NAME).getView());
    }

    @Test
    public void shouldThrowExceptionIfUnauthorisedUserTriesToOverwriteView() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(alternative, false, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        try {
            NamedViewCacheTest.cache.addNamedView(standard, true, blankUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        } catch (final CacheOperationFailedException e) {
            Assert.assertTrue(e.getMessage().contains("does not have permission to overwrite"));
        }
    }

    @Test
    public void shouldAllowUserToOverwriteViewWithPermission() throws CacheOperationFailedException {
        // Given
        NamedViewDetail namedViewDetailWithUsersAllowedToWrite = new NamedViewDetail.Builder().name(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).description("alternative View").creatorId(advancedUser.getUserId()).writers(Arrays.asList(NamedViewCacheTest.GAFFER_USER)).view(alternativeView).build();
        NamedViewCacheTest.cache.addNamedView(namedViewDetailWithUsersAllowedToWrite, false, advancedUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        // When
        NamedViewCacheTest.cache.addNamedView(new NamedViewDetail.Builder().name(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).view("").build(), true, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        // Then
        Assert.assertEquals("", NamedViewCacheTest.cache.getNamedView(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).getView());
    }

    @Test
    public void shouldThrowExceptionIfUnauthorisedUserTriesToDeleteView() throws CacheOperationFailedException {
        NamedViewCacheTest.cache.addNamedView(standard, false, advancedUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        try {
            NamedViewCacheTest.cache.deleteNamedView(NamedViewCacheTest.STANDARD_VIEW_NAME, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        } catch (final CacheOperationFailedException e) {
            Assert.assertTrue(e.getMessage().contains("does not have permission to delete named view"));
        }
    }

    @Test
    public void shouldAllowUserToDeleteViewWithNoPermissionsSet() throws CacheOperationFailedException {
        // Given
        NamedViewDetail namedViewDetailWithUsersAllowedToWrite = new NamedViewDetail.Builder().name(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).description("alternative View").view(alternativeView).build();
        NamedViewCacheTest.cache.addNamedView(namedViewDetailWithUsersAllowedToWrite, false);
        // When / Then - no exceptions
        NamedViewCacheTest.cache.deleteNamedView(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
    }

    @Test
    public void shouldAllowUserToDeleteViewWithPermission() throws CacheOperationFailedException {
        // Given
        NamedViewDetail namedViewDetailWithUsersAllowedToWrite = new NamedViewDetail.Builder().name(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).description("alternative View").creatorId(advancedUser.getUserId()).writers(Arrays.asList(NamedViewCacheTest.GAFFER_USER)).view(alternativeView).build();
        NamedViewCacheTest.cache.addNamedView(namedViewDetailWithUsersAllowedToWrite, false, advancedUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        // When / Then - no exceptions
        NamedViewCacheTest.cache.deleteNamedView(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME, standardUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
    }

    @Test
    public void shouldAllowUserToAddWithAdminAuth() throws CacheOperationFailedException {
        // Given
        NamedViewCacheTest.cache.addNamedView(alternative, false, advancedUser, NamedViewCacheTest.EMPTY_ADMIN_AUTH);
        NamedViewDetail alternativeWithADifferentView = new NamedViewDetail.Builder().name(NamedViewCacheTest.ALTERNATIVE_VIEW_NAME).description("alternative View").creatorId(standardUser.getUserId()).view(new View()).build();
        // When / Then - no exceptions
        NamedViewCacheTest.cache.addNamedView(alternativeWithADifferentView, true, userWithAdminAuth, NamedViewCacheTest.ADMIN_AUTH);
    }
}

