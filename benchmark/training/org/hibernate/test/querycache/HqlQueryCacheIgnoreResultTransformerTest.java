/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querycache;


import org.hibernate.testing.FailureExpected;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class HqlQueryCacheIgnoreResultTransformerTest extends AbstractQueryCacheResultTransformerTest {
    @Test
    @Override
    @FailureExpected(jiraKey = "N/A", message = "HQL query using Transformers.ALIAS_TO_ENTITY_MAP with no projection")
    public void testAliasToEntityMapNoProjectionList() throws Exception {
        super.testAliasToEntityMapNoProjectionList();
    }

    @Test
    @Override
    @FailureExpected(jiraKey = "N/A", message = "HQL query using Transformers.ALIAS_TO_ENTITY_MAP with no projection")
    public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
        super.testAliasToEntityMapNoProjectionMultiAndNullList();
    }

    @Test
    @Override
    @FailureExpected(jiraKey = "N/A", message = "HQL query using Transformers.ALIAS_TO_ENTITY_MAP with no projection")
    public void testAliasToEntityMapNoProjectionNullAndNonNullAliasList() throws Exception {
        super.testAliasToEntityMapNoProjectionNullAndNonNullAliasList();
    }

    @Test
    @Override
    @FailureExpected(jiraKey = "HHH-3345", message = "HQL query using 'select new' and 'join fetch'")
    public void testMultiSelectNewMapUsingAliasesWithFetchJoinList() throws Exception {
        super.testMultiSelectNewMapUsingAliasesWithFetchJoinList();
    }
}

