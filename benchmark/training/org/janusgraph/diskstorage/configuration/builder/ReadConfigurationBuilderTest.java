/**
 * Copyright 2019 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.configuration.builder;


import JanusGraphConstants.STORAGE_VERSION;
import JanusGraphConstants.TITAN_ID_STORE_NAME;
import JanusGraphConstants.VERSION;
import ReadConfigurationBuilder.BACKLEVEL_STORAGE_VERSION_EXCEPTION;
import ReadConfigurationBuilder.INCOMPATIBLE_STORAGE_VERSION_EXCEPTION;
import org.janusgraph.core.JanusGraphException;
import org.janusgraph.diskstorage.configuration.BasicConfiguration;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.configuration.ReadConfiguration;
import org.janusgraph.diskstorage.configuration.backend.KCVSConfiguration;
import org.janusgraph.diskstorage.configuration.backend.builder.KCVSConfigurationBuilder;
import org.janusgraph.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import org.janusgraph.diskstorage.keycolumnvalue.StoreFeatures;
import org.janusgraph.diskstorage.util.time.TimestampProviders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Tests for building ReadConfiguration
 */
@ExtendWith(MockitoExtension.class)
public class ReadConfigurationBuilderTest {
    @Mock
    private ReadConfiguration localConfig;

    @Mock
    private BasicConfiguration localBasicConfiguration;

    @Mock
    private ModifiableConfiguration overwrite;

    @Mock
    private KeyColumnValueStoreManager storeManager;

    @Mock
    private ModifiableConfigurationBuilder modifiableConfigurationBuilder;

    @Mock
    private StoreFeatures features;

    @Mock
    private ModifiableConfiguration globalWrite;

    @Mock
    private KCVSConfigurationBuilder kcvsConfigurationBuilder;

    @Mock
    private KCVSConfiguration keyColumnValueStoreConfiguration;

    @Mock
    private ReadConfiguration readConfiguration;

    @Mock
    private TimestampProviders timestampProviders;

    private final ReadConfigurationBuilder readConfigurationBuilder = new ReadConfigurationBuilder();

    @Test
    public void shouldOverwriteStoreManagerName() {
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        buildConfiguration();
        Mockito.verify(overwrite).set(ArgumentMatchers.eq(LOCK_LOCAL_MEDIATOR_GROUP), ArgumentMatchers.any());
    }

    @Test
    public void shouldNotOverwriteStoreManagerName() {
        Mockito.when(localBasicConfiguration.has(LOCK_LOCAL_MEDIATOR_GROUP)).thenReturn(true);
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        buildConfiguration();
        Mockito.verify(overwrite, Mockito.never()).set(ArgumentMatchers.eq(LOCK_LOCAL_MEDIATOR_GROUP), ArgumentMatchers.any());
    }

    @Test
    public void shouldFreezeModifiableConfiguration() {
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        buildConfiguration();
        Mockito.verify(globalWrite).freezeConfiguration();
    }

    @Test
    public void shouldNotSetupTimestampProviderOnExistedOne() {
        Mockito.when(localBasicConfiguration.has(ArgumentMatchers.any())).thenAnswer(( invocation) -> TIMESTAMP_PROVIDER.equals(invocation.getArguments()[0]));
        buildConfiguration();
        Mockito.verify(globalWrite, Mockito.never()).set(ArgumentMatchers.eq(TIMESTAMP_PROVIDER), ArgumentMatchers.any());
    }

    @Test
    public void shouldSetupDefaultTimestampProvider() {
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        buildConfiguration();
        Mockito.verify(globalWrite).set(TIMESTAMP_PROVIDER, TIMESTAMP_PROVIDER.getDefaultValue());
    }

    @Test
    public void shouldSetupBackendPreferenceTimestampProvider() {
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        Mockito.when(features.hasTimestamps()).thenReturn(true);
        Mockito.when(features.getPreferredTimestamps()).thenReturn(timestampProviders);
        buildConfiguration();
        Mockito.verify(globalWrite).set(TIMESTAMP_PROVIDER, timestampProviders);
    }

    @Test
    public void shouldSetupJanusGraphAndStorageVersions() {
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        buildConfiguration();
        Mockito.verify(globalWrite).set(INITIAL_JANUSGRAPH_VERSION, VERSION);
        Mockito.verify(globalWrite).set(INITIAL_STORAGE_VERSION, STORAGE_VERSION);
    }

    @Test
    public void shouldNotFreezeModifiableConfiguration() {
        Mockito.when(globalWrite.isFrozen()).thenReturn(true);
        globalWriteInitialJanusGraphVersionMock();
        buildConfiguration();
        Mockito.verify(globalWrite, Mockito.never()).freezeConfiguration();
    }

    @Test
    public void shouldBuildReadConfiguration() {
        Mockito.when(storeManager.getFeatures()).thenReturn(features);
        Mockito.when(keyColumnValueStoreConfiguration.asReadConfiguration()).thenReturn(readConfiguration);
        ReadConfiguration result = buildConfiguration();
        Assertions.assertEquals(readConfiguration, result);
    }

    @Test
    public void shouldSetupVersionsWithDisallowedUpgradeFromNoInitialStorageVersion() {
        frozenGlobalWriteWithAllowUpgradeMock(true);
        globalWriteInitialJanusGraphVersionMock();
        buildConfiguration();
        verifySetupVersionsWithDisallowedUpgrade();
    }

    @Test
    public void shouldSetupVersionsWithDisallowedUpgradeFromInitialStorageVersion() {
        frozenGlobalWriteWithAllowUpgradeMock(true);
        Mockito.when(globalWrite.has(INITIAL_STORAGE_VERSION)).thenReturn(true);
        concreteStorageVersionMock(((Integer.parseInt(STORAGE_VERSION)) - 1));
        buildConfiguration();
        verifySetupVersionsWithDisallowedUpgrade();
    }

    @Test
    public void shouldThrowExceptionWhenStorageVersionLessThenInternalStorageVersion() {
        frozenGlobalWriteWithAllowUpgradeMock(true);
        Mockito.when(globalWrite.has(INITIAL_STORAGE_VERSION)).thenReturn(true);
        concreteStorageVersionMock(((Integer.parseInt(STORAGE_VERSION)) + 1));
        JanusGraphException exception = Assertions.assertThrows(JanusGraphException.class, this::buildConfiguration);
        Assertions.assertEquals(String.format(BACKLEVEL_STORAGE_VERSION_EXCEPTION, globalWrite.get(INITIAL_STORAGE_VERSION), STORAGE_VERSION, null), exception.getMessage());
    }

    @Test
    public void shouldNotSetupVersionsWithDisallowedUpgradeOnEqualStorageVersions() {
        frozenGlobalWriteWithAllowUpgradeMock(true);
        Mockito.when(globalWrite.has(INITIAL_STORAGE_VERSION)).thenReturn(true);
        concreteStorageVersionMock(Integer.parseInt(STORAGE_VERSION));
        buildConfiguration();
        Mockito.verify(globalWrite, Mockito.never()).set(ALLOW_UPGRADE, false);
    }

    @Test
    public void shouldThrowExceptionOnDisallowedUpgradeAndNonEqualStorageVersions() {
        frozenGlobalWriteWithAllowUpgradeMock(false);
        JanusGraphException exception = Assertions.assertThrows(JanusGraphException.class, this::buildConfiguration);
        String storageVersion = (globalWrite.has(INITIAL_STORAGE_VERSION)) ? globalWrite.get(INITIAL_STORAGE_VERSION) : "1";
        Assertions.assertEquals(String.format(INCOMPATIBLE_STORAGE_VERSION_EXCEPTION, storageVersion, STORAGE_VERSION, null), exception.getMessage());
    }

    @Test
    public void shouldSetTitanIDStoreNameWhenKeystoreNotExists() {
        Mockito.when(globalWrite.isFrozen()).thenReturn(true);
        allowUpgradeWithJanusGraphIDStoreNameMock();
        globalWriteTitanCompatibleVersionMock();
        buildConfiguration();
        Mockito.verify(overwrite).set(IDS_STORE_NAME, TITAN_ID_STORE_NAME);
    }

    @Test
    public void shouldNotSetTitanIDStoreNameWhenKeystoreExists() {
        Mockito.when(globalWrite.isFrozen()).thenReturn(true);
        allowUpgradeWithJanusGraphIDStoreNameMock();
        globalWriteTitanCompatibleVersionMock();
        Mockito.when(keyColumnValueStoreConfiguration.get(IDS_STORE_NAME.getName(), IDS_STORE_NAME.getDatatype())).thenReturn("test_value");
        buildConfiguration();
        Mockito.verify(overwrite, Mockito.never()).set(IDS_STORE_NAME, TITAN_ID_STORE_NAME);
    }

    @Test
    public void shouldPassOnDisallowedManagedOverwritesWithNoOptions() {
        Mockito.when(globalWrite.isFrozen()).thenReturn(true);
        upgradeWithStaleConfigMock(false);
        globalWriteInitialJanusGraphVersionMock();
        Assertions.assertDoesNotThrow(this::buildConfiguration);
    }
}

