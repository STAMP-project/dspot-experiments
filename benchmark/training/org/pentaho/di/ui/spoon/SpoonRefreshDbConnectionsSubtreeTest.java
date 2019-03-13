/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon;


import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.ui.core.widget.tree.TreeNode;
import org.pentaho.di.ui.spoon.tree.provider.DBConnectionFolderProvider;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class SpoonRefreshDbConnectionsSubtreeTest {
    private DBConnectionFolderProvider dbConnectionFolderProvider;

    private TreeNode treeNode;

    @Test
    public void noConnectionsExist() {
        AbstractMeta meta = Mockito.mock(AbstractMeta.class);
        Mockito.when(meta.getDatabases()).thenReturn(Collections.<DatabaseMeta>emptyList());
        callRefreshWith(meta, null);
        // one call - to create a parent tree node
        verifyNumberOfNodesCreated(0);
    }

    @Test
    public void severalConnectionsExist() {
        AbstractMeta meta = SpoonRefreshDbConnectionsSubtreeTest.prepareMetaWithThreeDbs();
        callRefreshWith(meta, null);
        verifyNumberOfNodesCreated(3);
    }

    @Test
    public void onlyOneMatchesFiltering() {
        AbstractMeta meta = SpoonRefreshDbConnectionsSubtreeTest.prepareMetaWithThreeDbs();
        callRefreshWith(meta, "2");
        verifyNumberOfNodesCreated(1);
    }
}

