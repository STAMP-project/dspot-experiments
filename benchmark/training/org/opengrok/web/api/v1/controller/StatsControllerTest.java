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


import Response.Status.INTERNAL_SERVER_ERROR;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.web.Statistics;


public class StatsControllerTest extends JerseyTest {
    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    @Test
    public void testClean() {
        Statistics stats = new Statistics();
        stats.addRequest();
        stats.addRequest();
        Assert.assertEquals(2, stats.getRequests());
        env.setStatistics(stats);
        target("stats").request().delete();
        Assert.assertEquals(0, env.getStatistics().getRequests());
    }

    @Test
    public void testGetClean() throws IOException {
        target("stats").request().delete();
        String output = target("stats").request().get(String.class);
        Statistics stats = Statistics.fromJson(output);
        Assert.assertEquals(0, stats.getRequests());
        Assert.assertEquals(1, stats.getMinutes());
        Assert.assertEquals(0, stats.getRequestCategories().size());
        Assert.assertEquals(0, stats.getTiming().size());
    }

    @Test
    public void testGet() throws IOException {
        target("stats").request().delete();
        env.getStatistics().addRequest();
        String output = target("stats").request().get(String.class);
        Statistics stats = Statistics.fromJson(output);
        Assert.assertEquals(1, stats.getRequests());
        Assert.assertEquals(1, stats.getMinutes());
        Assert.assertEquals(0, stats.getRequestCategories().size());
        Assert.assertEquals(0, stats.getTiming().size());
    }

    @Test
    public void testInvalidReload() {
        env.setStatisticsFilePath("/file/that/doesnot/exists");
        Response response = target("stats").path("reload").request().put(Entity.text(""));
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}

