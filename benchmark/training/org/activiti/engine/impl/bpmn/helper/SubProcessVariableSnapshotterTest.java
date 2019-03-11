package org.activiti.engine.impl.bpmn.helper;


import java.util.Collections;
import java.util.Map;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 */
public class SubProcessVariableSnapshotterTest {
    private SubProcessVariableSnapshotter snapshotter = new SubProcessVariableSnapshotter();

    @Test
    public void setVariablesSnapshots_should_set_all_source_local_variables_in_the_snapshot_holder() throws Exception {
        // given
        Map<String, Object> variables = Collections.<String, Object>singletonMap("subCount", 1L);
        ExecutionEntity subProcessExecution = buildExecutionEntity(variables);
        ExecutionEntity snapshotHolderExecution = Mockito.mock(ExecutionEntity.class);
        // when
        snapshotter.setVariablesSnapshots(subProcessExecution, snapshotHolderExecution);
        // then
        Mockito.verify(snapshotHolderExecution).setVariablesLocal(variables);
    }

    @Test
    public void setVariablesSnapshots_should_set_parent_variables_in_the_snapshot_holder_when_parent_is_multi_instance() throws Exception {
        // given
        Map<String, Object> parentVariables = Collections.<String, Object>singletonMap("parentCount", 1L);
        ExecutionEntity parentExecution = buildExecutionEntity(parentVariables);
        Mockito.when(parentExecution.isMultiInstanceRoot()).thenReturn(true);
        Map<String, Object> localVariables = Collections.<String, Object>singletonMap("subCount", 1L);
        ExecutionEntity subProcessExecution = buildExecutionEntity(parentExecution, localVariables);
        ExecutionEntity snapshotHolderExecution = Mockito.mock(ExecutionEntity.class);
        // when
        snapshotter.setVariablesSnapshots(subProcessExecution, snapshotHolderExecution);
        // then
        Mockito.verify(snapshotHolderExecution).setVariablesLocal(localVariables);
        Mockito.verify(snapshotHolderExecution).setVariablesLocal(parentVariables);
    }

    @Test
    public void setVariablesSnapshots_should_not_set_parent_variables_in_the_snapshot_holder_when_parent_is_not_multi_instance() throws Exception {
        // given
        Map<String, Object> parentVariables = Collections.<String, Object>singletonMap("parentCount", 1L);
        ExecutionEntity parentExecution = buildExecutionEntity(parentVariables);
        Mockito.when(parentExecution.isMultiInstanceRoot()).thenReturn(false);
        Map<String, Object> localVariables = Collections.<String, Object>singletonMap("subCount", 1L);
        ExecutionEntity subProcessExecution = buildExecutionEntity(parentExecution, localVariables);
        ExecutionEntity snapshotHolderExecution = Mockito.mock(ExecutionEntity.class);
        // when
        snapshotter.setVariablesSnapshots(subProcessExecution, snapshotHolderExecution);
        // then
        Mockito.verify(snapshotHolderExecution).setVariablesLocal(localVariables);
        Mockito.verify(snapshotHolderExecution, Mockito.never()).setVariablesLocal(parentVariables);
    }
}

