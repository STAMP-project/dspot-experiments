/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.actions;


import org.eclipse.che.ide.api.app.StartUpAction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vitalii Parfonov
 */
public class StartUpActionsParserTest {
    @Test
    public void test() {
        final StartUpAction startUpAction = StartUpActionsParser.parseActionQuery("createProject:projectName=test;projectType=maven");
        Assert.assertEquals("createProject", startUpAction.getActionId());
        Assert.assertNotNull(startUpAction.getParameters());
        Assert.assertEquals(2, startUpAction.getParameters().size());
        Assert.assertTrue(startUpAction.getParameters().containsKey("projectName"));
        Assert.assertTrue(startUpAction.getParameters().containsKey("projectType"));
        Assert.assertNotNull(startUpAction.getParameters().get("projectName"));
        Assert.assertNotNull(startUpAction.getParameters().get("projectType"));
        Assert.assertEquals("test", startUpAction.getParameters().get("projectName"));
        Assert.assertEquals("maven", startUpAction.getParameters().get("projectType"));
    }

    @Test
    public void test2() {
        final StartUpAction startUpAction = StartUpActionsParser.parseActionQuery("createProject:projectName;projectType");
        Assert.assertEquals("createProject", startUpAction.getActionId());
        Assert.assertNotNull(startUpAction.getParameters());
        Assert.assertEquals(2, startUpAction.getParameters().size());
        Assert.assertTrue(startUpAction.getParameters().containsKey("projectName"));
        Assert.assertTrue(startUpAction.getParameters().containsKey("projectType"));
        Assert.assertNull(startUpAction.getParameters().get("projectName"));
        Assert.assertNull(startUpAction.getParameters().get("projectType"));
    }

    @Test
    public void test3() {
        final StartUpAction startUpAction = StartUpActionsParser.parseActionQuery("createProject");
        Assert.assertEquals("createProject", startUpAction.getActionId());
        Assert.assertNull(startUpAction.getParameters());
    }
}

