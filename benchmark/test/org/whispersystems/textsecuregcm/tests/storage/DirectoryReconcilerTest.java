/**
 * Copyright (C) 2018 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.tests.storage;


import DirectoryReconciliationResponse.Status;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.entities.ClientContact;
import org.whispersystems.textsecuregcm.entities.DirectoryReconciliationRequest;
import org.whispersystems.textsecuregcm.entities.DirectoryReconciliationResponse;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.DirectoryManager;
import org.whispersystems.textsecuregcm.storage.DirectoryManager.BatchOperationHandle;
import org.whispersystems.textsecuregcm.storage.DirectoryReconciler;
import org.whispersystems.textsecuregcm.storage.DirectoryReconciliationClient;
import org.whispersystems.textsecuregcm.util.Util;


public class DirectoryReconcilerTest {
    private static final String VALID_NUMBER = "valid";

    private static final String INACTIVE_NUMBER = "inactive";

    private final Account activeAccount = Mockito.mock(Account.class);

    private final Account inactiveAccount = Mockito.mock(Account.class);

    private final BatchOperationHandle batchOperationHandle = Mockito.mock(BatchOperationHandle.class);

    private final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);

    private final DirectoryReconciliationClient reconciliationClient = Mockito.mock(DirectoryReconciliationClient.class);

    private final DirectoryReconciler directoryReconciler = new DirectoryReconciler(reconciliationClient, directoryManager);

    private final DirectoryReconciliationResponse successResponse = new DirectoryReconciliationResponse(Status.OK);

    private final DirectoryReconciliationResponse missingResponse = new DirectoryReconciliationResponse(Status.MISSING);

    @Test
    public void testCrawlChunkValid() {
        Mockito.when(reconciliationClient.sendChunk(ArgumentMatchers.any())).thenReturn(successResponse);
        directoryReconciler.onCrawlChunk(Optional.of(DirectoryReconcilerTest.VALID_NUMBER), Arrays.asList(activeAccount, inactiveAccount));
        Mockito.verify(activeAccount, Mockito.times(2)).getNumber();
        Mockito.verify(activeAccount, Mockito.times(2)).isActive();
        Mockito.verify(inactiveAccount, Mockito.times(2)).getNumber();
        Mockito.verify(inactiveAccount, Mockito.times(2)).isActive();
        ArgumentCaptor<DirectoryReconciliationRequest> request = ArgumentCaptor.forClass(DirectoryReconciliationRequest.class);
        Mockito.verify(reconciliationClient, Mockito.times(1)).sendChunk(request.capture());
        assertThat(request.getValue().getFromNumber()).isEqualTo(DirectoryReconcilerTest.VALID_NUMBER);
        assertThat(request.getValue().getToNumber()).isEqualTo(DirectoryReconcilerTest.INACTIVE_NUMBER);
        assertThat(request.getValue().getNumbers()).isEqualTo(Arrays.asList(DirectoryReconcilerTest.VALID_NUMBER));
        ArgumentCaptor<ClientContact> addedContact = ArgumentCaptor.forClass(ClientContact.class);
        Mockito.verify(directoryManager, Mockito.times(1)).startBatchOperation();
        Mockito.verify(directoryManager, Mockito.times(1)).add(ArgumentMatchers.eq(batchOperationHandle), addedContact.capture());
        Mockito.verify(directoryManager, Mockito.times(1)).remove(ArgumentMatchers.eq(batchOperationHandle), ArgumentMatchers.eq(DirectoryReconcilerTest.INACTIVE_NUMBER));
        Mockito.verify(directoryManager, Mockito.times(1)).stopBatchOperation(ArgumentMatchers.eq(batchOperationHandle));
        assertThat(addedContact.getValue().getToken()).isEqualTo(Util.getContactToken(DirectoryReconcilerTest.VALID_NUMBER));
        Mockito.verifyNoMoreInteractions(activeAccount);
        Mockito.verifyNoMoreInteractions(inactiveAccount);
        Mockito.verifyNoMoreInteractions(batchOperationHandle);
        Mockito.verifyNoMoreInteractions(directoryManager);
        Mockito.verifyNoMoreInteractions(reconciliationClient);
    }
}

