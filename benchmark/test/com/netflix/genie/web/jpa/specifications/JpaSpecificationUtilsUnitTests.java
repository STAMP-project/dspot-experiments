/**
 * Copyright 2015 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.jpa.specifications;


import JpaSpecificationUtils.PERCENT;
import com.google.common.collect.Sets;
import com.netflix.genie.test.categories.UnitTest;
import com.netflix.genie.web.jpa.entities.TagEntity;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static JpaSpecificationUtils.PERCENT;
import static JpaSpecificationUtils.TAG_DELIMITER;


/**
 * Unit tests for JpaSpecificationUtils.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class JpaSpecificationUtilsUnitTests {
    /**
     * Make sure the method to create the tag search string for jobs is working as expected.
     */
    @Test
    public void canCreateTagSearchString() {
        final String one = "oNe";
        final String two = "TwO";
        final String three = "3";
        final Set<TagEntity> tags = Sets.newHashSet();
        // Coerce to string... sigh
        Assert.assertThat(JpaSpecificationUtils.createTagSearchString(tags), Matchers.is(((TAG_DELIMITER) + (TAG_DELIMITER))));
        final TagEntity oneTag = new TagEntity();
        oneTag.setTag(one);
        final TagEntity twoTag = new TagEntity();
        twoTag.setTag(two);
        final TagEntity threeTag = new TagEntity();
        threeTag.setTag(three);
        tags.add(oneTag);
        Assert.assertThat(JpaSpecificationUtils.createTagSearchString(tags), Matchers.is((((TAG_DELIMITER) + one) + (TAG_DELIMITER))));
        tags.add(twoTag);
        Assert.assertThat(JpaSpecificationUtils.createTagSearchString(tags), Matchers.is(((((((TAG_DELIMITER) + one) + (TAG_DELIMITER)) + (TAG_DELIMITER)) + two) + (TAG_DELIMITER))));
        tags.add(threeTag);
        Assert.assertThat(JpaSpecificationUtils.createTagSearchString(tags), Matchers.is((((((((((TAG_DELIMITER) + three) + (TAG_DELIMITER)) + (TAG_DELIMITER)) + one) + (TAG_DELIMITER)) + (TAG_DELIMITER)) + two) + (TAG_DELIMITER))));
    }

    /**
     * Make sure if a string parameter contains a % it returns a like predicate but if not it returns an equals
     * predicate.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void canGetStringLikeOrEqualPredicate() {
        final CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        final Expression<String> expression = ((Expression<String>) (Mockito.mock(Expression.class)));
        final Predicate likePredicate = Mockito.mock(Predicate.class);
        final Predicate equalPredicate = Mockito.mock(Predicate.class);
        Mockito.when(cb.like(Mockito.eq(expression), Mockito.anyString())).thenReturn(likePredicate);
        Mockito.when(cb.equal(Mockito.eq(expression), Mockito.anyString())).thenReturn(equalPredicate);
        Assert.assertThat(JpaSpecificationUtils.getStringLikeOrEqualPredicate(cb, expression, "equal"), Matchers.is(equalPredicate));
        Assert.assertThat(JpaSpecificationUtils.getStringLikeOrEqualPredicate(cb, expression, "lik%e"), Matchers.is(likePredicate));
    }

    /**
     * Make sure we can get a valid like string for the tag list.
     */
    @Test
    public void canGetTagLikeString() {
        // coerce to String
        Assert.assertThat(JpaSpecificationUtils.getTagLikeString(Sets.newHashSet()), Matchers.is(PERCENT));
        Assert.assertThat(JpaSpecificationUtils.getTagLikeString(Sets.newHashSet("tag")), Matchers.is((((((PERCENT) + (TAG_DELIMITER)) + "tag") + (TAG_DELIMITER)) + (PERCENT))));
        Assert.assertThat(JpaSpecificationUtils.getTagLikeString(Sets.newHashSet("tag", "Stag", "rag")), Matchers.is((((((((((((((PERCENT) + (TAG_DELIMITER)) + "rag") + (TAG_DELIMITER)) + "%") + (TAG_DELIMITER)) + "Stag") + (TAG_DELIMITER)) + "%") + (TAG_DELIMITER)) + "tag") + (TAG_DELIMITER)) + (PERCENT))));
    }
}

