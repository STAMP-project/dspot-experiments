/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.suggest.query;


import java.util.Arrays;
import org.apache.lucene.index.Term;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.suggest.query.customized.CustomPhraseQuery;


public class SuggesterPhraseQueryTest {
    @Test
    public void prefixQueryTest() {
        SuggesterPhraseQuery q = new SuggesterPhraseQuery("test", "ident", Arrays.asList("one", "two", "tident"), 0);
        SuggesterPrefixQuery prefixQuery = ((SuggesterPrefixQuery) (q.getSuggesterQuery()));
        Assert.assertEquals("t", prefixQuery.getPrefix().text());
    }

    @Test
    public void phraseQueryTest() throws Exception {
        SuggesterPhraseQuery q = new SuggesterPhraseQuery("test", "ident", Arrays.asList("one", "two", "tident"), 0);
        CustomPhraseQuery query = q.getPhraseQuery();
        Assert.assertEquals(2, query.offset);
        Term[] terms = getTerms(query);
        Assert.assertEquals("one", terms[0].text());
        Assert.assertEquals("two", terms[1].text());
    }
}

