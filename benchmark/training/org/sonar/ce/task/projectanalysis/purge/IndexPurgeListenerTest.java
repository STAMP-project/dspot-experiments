/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.purge;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.server.component.index.ComponentIndexer;
import org.sonar.server.issue.index.IssueIndexer;


public class IndexPurgeListenerTest {
    private IssueIndexer issueIndexer = Mockito.mock(IssueIndexer.class);

    private ComponentIndexer componentIndexer = Mockito.mock(ComponentIndexer.class);

    private IndexPurgeListener underTest = new IndexPurgeListener(issueIndexer, componentIndexer);

    @Test
    public void test_onComponentDisabling() {
        String uuid = "123456";
        String projectUuid = "P789";
        List<String> uuids = Collections.singletonList(uuid);
        underTest.onComponentsDisabling(projectUuid, uuids);
        Mockito.verify(componentIndexer).delete(projectUuid, uuids);
    }

    @Test
    public void test_onIssuesRemoval() {
        underTest.onIssuesRemoval("P1", Arrays.asList("ISSUE1", "ISSUE2"));
        Mockito.verify(issueIndexer).deleteByKeys("P1", Arrays.asList("ISSUE1", "ISSUE2"));
    }
}

