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
package org.opengrok.web.api.v1.controller;


import QueryBuilder.FULL;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.util.TestRepository;


@ConditionalRun(CtagsInstalled.class)
public class SuggesterControllerProjectsDisabledTest extends JerseyTest {
    @ClassRule
    public static ConditionalRunRule rule = new ConditionalRunRule();

    private static final RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private static TestRepository repository;

    @Test
    public void suggestionsSimpleTest() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam("field", FULL).queryParam(FULL, "inner").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("innermethod", "innerclass"));
    }
}

