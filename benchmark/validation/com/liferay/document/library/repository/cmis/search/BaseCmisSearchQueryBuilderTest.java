/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.document.library.repository.cmis.search;


import CapabilityQuery.BOTHCOMBINED;
import CapabilityQuery.FULLTEXTONLY;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import org.junit.Test;


/**
 *
 *
 * @author Mika Koivisto
 * @author Andr? de Oliveira
 */
public class BaseCmisSearchQueryBuilderTest {
    @Test
    public void testBooleanQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("+test* -test.doc");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals(("((cmis:name LIKE 'test%' AND NOT(cmis:name = 'test.doc')) OR " + ("(cmis:createdBy LIKE 'test%' AND NOT(cmis:createdBy = " + "'test.doc')))")), cmisQuery);
    }

    @Test
    public void testContainsCombinedSupportedQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", BOTHCOMBINED.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals(("((cmis:name = 'test' OR cmis:createdBy = 'test') OR " + "CONTAINS('test'))"), cmisQuery);
    }

    @Test
    public void testContainsCombinedSupportedWildcardQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test*.jpg");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", BOTHCOMBINED.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals(("((cmis:name LIKE 'test%.jpg' OR cmis:createdBy LIKE " + "'test%.jpg') OR CONTAINS('(test AND .jpg)'))"), cmisQuery);
    }

    @Test
    public void testContainsOnlySupportedQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", FULLTEXTONLY.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("CONTAINS('test')", cmisQuery);
    }

    @Test
    public void testContainsOnlySupportedQueryMultipleKeywords() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test multiple");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", FULLTEXTONLY.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("CONTAINS('(test OR multiple)')", cmisQuery);
    }

    @Test
    public void testContainsOnlySupportedQueryWithConjunction() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("+test +multiple");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", FULLTEXTONLY.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("CONTAINS('(test multiple)')", cmisQuery);
    }

    @Test
    public void testContainsOnlySupportedQueryWithNegation() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test -multiple");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", FULLTEXTONLY.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("CONTAINS('(-multiple OR test)')", cmisQuery);
    }

    @Test
    public void testContainsOnlySupportedQueryWithNegationPhrase() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test -\"multiple words\"");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", FULLTEXTONLY.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("CONTAINS(\'(-\\\'multiple words\\\' OR test)\')", cmisQuery);
    }

    @Test
    public void testContainsOnlySupportedWithApostrophe() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test's");
        QueryConfig queryConfig = searchContext.getQueryConfig();
        queryConfig.setAttribute("capabilityQuery", FULLTEXTONLY.value());
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("CONTAINS(\'test\\\'s\')", cmisQuery);
    }

    @Test
    public void testExactFilenameQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test.jpg");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("(cmis:name = 'test.jpg' OR cmis:createdBy = 'test.jpg')", cmisQuery);
    }

    @Test
    public void testFolderQuery() throws Exception {
        String folderQuery = buildFolderQuery(false);
        assertQueryEquals(((("((IN_FOLDER('" + (BaseCmisSearchQueryBuilderTest._MAPPED_ID)) + "') AND (cmis:name = 'test' OR ") + "cmis:createdBy = 'test')) OR CONTAINS('test'))"), folderQuery);
    }

    @Test
    public void testFuzzyQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test~");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("(cmis:name LIKE 'test%' OR cmis:createdBy LIKE 'test%')", cmisQuery);
    }

    @Test
    public void testPhraseQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("\"My test document.jpg\"");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals(("(cmis:name = 'My test document.jpg' OR cmis:createdBy = 'My " + "test document.jpg')"), cmisQuery);
    }

    @Test
    public void testPrefixQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("Test*");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("(cmis:name LIKE 'Test%' OR cmis:createdBy LIKE 'Test%')", cmisQuery);
    }

    @Test
    public void testProximityQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("\"test document\"~10");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("(cmis:name = 'test document' OR cmis:createdBy = 'test document')", cmisQuery);
    }

    @Test
    public void testRangeQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("createDate:[20091011000000 TO 20091110235959]");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals(("cmis:creationDate >= 2009-10-11T00:00:00.000Z AND " + "cmis:creationDate <= 2009-11-10T23:59:59.000Z"), cmisQuery);
    }

    @Test
    public void testSubfolderQuery() throws Exception {
        String folderQuery = buildFolderQuery(true);
        assertQueryEquals(((("((IN_TREE('" + (BaseCmisSearchQueryBuilderTest._MAPPED_ID)) + "') AND (cmis:name = 'test' OR ") + "cmis:createdBy = 'test')) OR CONTAINS('test'))"), folderQuery);
    }

    @Test
    public void testWildcardFieldQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("+title:test*.jpg +userName:bar*");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("(cmis:name LIKE 'test%.jpg' AND cmis:createdBy LIKE 'bar%')", cmisQuery);
    }

    @Test
    public void testWildcardQuery() throws Exception {
        SearchContext searchContext = getSearchContext();
        searchContext.setKeywords("test*.jpg");
        String cmisQuery = buildQuery(searchContext);
        assertQueryEquals("(cmis:name LIKE 'test%.jpg' OR cmis:createdBy LIKE 'test%.jpg')", cmisQuery);
    }

    private static final long _DL_FOLDER_ID = RandomTestUtil.randomLong();

    private static final String _INDEX_DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";

    private static final String _MAPPED_ID = "1000";

    private static final String _QUERY_POSTFIX = " ORDER BY HITS DESC";

    private static final String _QUERY_PREFIX = "SELECT cmis:objectId, SCORE() AS HITS FROM cmis:document WHERE ";

    private CMISSearchQueryBuilder _cmisSearchQueryBuilder;
}

