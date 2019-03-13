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
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.web;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.configuration.Group;


public class ProjectHelperExtendedTest extends ProjectHelperTestBase {
    @Test
    public void testGetAllowedGroupSubgroups() {
        Set<Group> result = helper.getSubgroups(getAllowedGroupWithSubgroups());
        Assert.assertEquals(1, result.size());
        for (Group p : result) {
            Assert.assertTrue(p.getName().startsWith("allowed_"));
        }
    }

    @Test
    public void testGetUnAllowedGroupSubgroups() {
        Set<Group> result = helper.getSubgroups(getUnAllowedGroupWithSubgroups());
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testHasAllowedSubgroupAllowedSubgroups() {
        Group g = getAllowedGroupWithSubgroups();
        Assert.assertTrue(helper.hasAllowedSubgroup(g));
    }

    @Test
    public void testHasAllowedSubgroupUnAllowedSubgroups() {
        Group g = getUnAllowedGroupWithSubgroups();
        Assert.assertFalse(helper.hasAllowedSubgroup(g));
    }

    @Test
    public void testHasAllowedSubgroupAllowedNoSubgroups() {
        Group g = getAllowedGroupWithoutSubgroups();
        Assert.assertFalse(helper.hasAllowedSubgroup(g));
    }

    @Test
    public void testHasAllowedSubgroupUnAllowedNoSubgroups() {
        Group g = getUnAllowedGroupWithoutSubgroups();
        Assert.assertFalse(helper.hasAllowedSubgroup(g));
    }

    @Test
    public void testIsFavourite() {
        setupPageConfigRequest(("grouped_project_0_1," + (((((("grouped_repository_2_2," + "allowed_grouped_repository_0_2,") + "allowed_grouped_project_1_2,") + "allowed_ungrouped_project_2_1,") + "allowed_ungrouped_repository_2_1,") + "ungrouped_repository_1_1,") + "ungrouped_project_0_1")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("grouped_project_0_1")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("grouped_repository_2_2")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("allowed_grouped_repository_0_2")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("allowed_grouped_project_1_2")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("allowed_ungrouped_project_2_1")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("allowed_ungrouped_repository_2_1")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("ungrouped_repository_1_1")));
        Assert.assertTrue(helper.isFavourite(ProjectHelperExtendedTest.createProject("ungrouped_project_0_1")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("uknown")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("ungrouped_project_0_2")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("ungrouped_epository_1_1")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("allowed_grouped_repository_2_1")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("grouped_project__0_1")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("gd6sf8g718fd7gsd68dfg")));
        Assert.assertFalse(helper.isFavourite(ProjectHelperExtendedTest.createProject("Chuck Norris")));
    }

    @Test
    public void testHasUngroupedFavouritePositive() {
        setupPageConfigRequest(("grouped_project_0_1," + ((((("grouped_repository_2_2," + "allowed_grouped_repository_0_2,") + "allowed_grouped_project_1_2,") + "allowed_ungrouped_repository_2_1,") + "ungrouped_repository_1_1,") + "ungrouped_project_0_1")));
        Assert.assertTrue(helper.hasUngroupedFavourite());
    }

    @Test
    public void testHasUngroupedFavouriteNegative() {
        setupPageConfigRequest(("grouped_project_0_1," + (((("grouped_repository_2_2," + "allowed_grouped_repository_0_2,") + "allowed_grouped_project_1_2,") + "ungrouped_repository_1_1,") + "ungrouped_project_0_1")));
        Assert.assertFalse(helper.hasUngroupedFavourite());
    }

    @Test
    public void testHasFavourite() {
        String[] cookie = new String[]{ "grouped_project_2_1", "allowed_grouped_project_2_1", "ungrouped_project_2_1", "uknown", "allowed_grouped_project_0_1", "grouped_project_0_1" };
        boolean[] exp = new boolean[]{ false, true, false, false, false, false };
        Group[] groups = new Group[]{ Group.getByName("allowed_group_2"), Group.getByName("allowed_group_2"), Group.getByName("allowed_group_2"), Group.getByName("allowed_group_2"), Group.getByName("group_0"), Group.getByName("group_0") };
        Assert.assertTrue((((groups.length) == (exp.length)) && ((exp.length) == (cookie.length))));
        for (int i = 0; i < (exp.length); i++) {
            setupPageConfigRequest(cookie[i]);
            Assert.assertEquals(exp[i], helper.hasFavourite(groups[i]));
        }
    }
}

