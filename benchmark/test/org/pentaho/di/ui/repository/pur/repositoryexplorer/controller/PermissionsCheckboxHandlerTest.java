/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.ui.repository.pur.repositoryexplorer.controller;


import RepositoryFilePermission.ACL_MANAGEMENT;
import RepositoryFilePermission.ALL;
import RepositoryFilePermission.DELETE;
import RepositoryFilePermission.READ;
import RepositoryFilePermission.WRITE;
import java.util.EnumSet;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.ui.xul.components.XulCheckbox;


public class PermissionsCheckboxHandlerTest {
    private XulCheckbox readCheckbox;

    private XulCheckbox writeCheckbox;

    private XulCheckbox deleteCheckbox;

    private XulCheckbox manageCheckbox;

    private PermissionsCheckboxHandler permissionsCheckboxHandler;

    @Test
    public void testSetAllUncheckedUnchecksAll() {
        boolean checked = false;
        permissionsCheckboxHandler.setAllChecked(checked);
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(checked);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(checked);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(checked);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(checked);
    }

    @Test
    public void testSetAllCheckedChecksAll() {
        boolean checked = true;
        permissionsCheckboxHandler.setAllChecked(checked);
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(checked);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(checked);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(checked);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(checked);
    }

    @Test
    public void testSetAllDisabledDisablesAll() {
        boolean disabled = true;
        permissionsCheckboxHandler.setAllDisabled(disabled);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(disabled);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(disabled);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(disabled);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(disabled);
    }

    @Test
    public void testSetAllEnabledEnablesAll() {
        boolean disabled = false;
        permissionsCheckboxHandler.setAllDisabled(disabled);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(disabled);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(disabled);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(disabled);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(disabled);
    }

    @Test
    public void testProcessCheckboxesNoneCheckedEnableAppropriateTrue() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.noneOf(RepositoryFilePermission.class), permissionsCheckboxHandler.processCheckboxes(true));
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testProcessCheckboxesNoneCheckedEnableAppropriateFalse() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.noneOf(RepositoryFilePermission.class), permissionsCheckboxHandler.processCheckboxes());
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(readCheckbox, Mockito.never()).setDisabled(false);
    }

    @Test
    public void testProcessCheckboxesReadCheckedEnableAppropriateTrue() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(true);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.of(READ), permissionsCheckboxHandler.processCheckboxes(true));
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testProcessCheckboxesReadCheckedEnableAppropriateFalse() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(true);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.of(READ), permissionsCheckboxHandler.processCheckboxes());
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testProcessCheckboxesWriteCheckedEnableAppropriateTrue() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(true);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.of(READ, WRITE), permissionsCheckboxHandler.processCheckboxes(true));
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testProcessCheckboxesWriteCheckedEnableAppropriateFalse() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(true);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.of(READ, WRITE), permissionsCheckboxHandler.processCheckboxes());
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testProcessCheckboxesDeleteCheckedEnableAppropriateTrue() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(true);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.of(READ, WRITE, DELETE), permissionsCheckboxHandler.processCheckboxes(true));
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testProcessCheckboxesDeleteCheckedEnableAppropriateFalse() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(true);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(false);
        Assert.assertEquals(EnumSet.of(READ, WRITE, DELETE), permissionsCheckboxHandler.processCheckboxes());
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testProcessCheckboxesManageCheckedEnableAppropriateTrue() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(true);
        Assert.assertEquals(EnumSet.of(READ, WRITE, DELETE, ACL_MANAGEMENT), permissionsCheckboxHandler.processCheckboxes(true));
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testProcessCheckboxesManageCheckedEnableAppropriateFalse() {
        Mockito.when(readCheckbox.isChecked()).thenReturn(false);
        Mockito.when(writeCheckbox.isChecked()).thenReturn(false);
        Mockito.when(deleteCheckbox.isChecked()).thenReturn(false);
        Mockito.when(manageCheckbox.isChecked()).thenReturn(true);
        Assert.assertEquals(EnumSet.of(READ, WRITE, DELETE, ACL_MANAGEMENT), permissionsCheckboxHandler.processCheckboxes());
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesNoPermissionsAppropriateTrue() {
        permissionsCheckboxHandler.updateCheckboxes(true, EnumSet.noneOf(RepositoryFilePermission.class));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testUpdateCheckboxesNoPermissionsAppropriateFalse() {
        permissionsCheckboxHandler.updateCheckboxes(false, EnumSet.noneOf(RepositoryFilePermission.class));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(readCheckbox, Mockito.never()).setDisabled(false);
    }

    @Test
    public void testUpdateCheckboxesReadPermissionsAppropriateTrue() {
        permissionsCheckboxHandler.updateCheckboxes(true, EnumSet.of(READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesReadPermissionsAppropriateFalse() {
        permissionsCheckboxHandler.updateCheckboxes(false, EnumSet.of(READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesWritePermissionsAppropriateTrue() {
        permissionsCheckboxHandler.updateCheckboxes(true, EnumSet.of(WRITE, READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesWritePermissionsAppropriateFalse() {
        permissionsCheckboxHandler.updateCheckboxes(false, EnumSet.of(WRITE, READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesDeletePermissionsAppropriateTrue() {
        permissionsCheckboxHandler.updateCheckboxes(true, EnumSet.of(DELETE, WRITE, READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(false);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testUpdateCheckboxesDeletePermissionsAppropriateFalse() {
        permissionsCheckboxHandler.updateCheckboxes(false, EnumSet.of(DELETE, WRITE, READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(false);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesManagePermissionsAppropriateTrue() {
        permissionsCheckboxHandler.updateCheckboxes(true, EnumSet.of(ACL_MANAGEMENT, DELETE, WRITE, READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testUpdateCheckboxesManagePermissionsAppropriateFalse() {
        permissionsCheckboxHandler.updateCheckboxes(false, EnumSet.of(ACL_MANAGEMENT, DELETE, WRITE, READ));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }

    @Test
    public void testUpdateCheckboxesAllPermissionsAppropriateTrue() {
        permissionsCheckboxHandler.updateCheckboxes(true, EnumSet.of(ALL));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(false);
    }

    @Test
    public void testUpdateCheckboxesAllPermissionsAppropriateFalse() {
        permissionsCheckboxHandler.updateCheckboxes(false, EnumSet.of(ALL));
        Mockito.verify(readCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setChecked(true);
        Mockito.verify(readCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(writeCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(deleteCheckbox, Mockito.times(1)).setDisabled(true);
        Mockito.verify(manageCheckbox, Mockito.times(1)).setDisabled(true);
    }
}

