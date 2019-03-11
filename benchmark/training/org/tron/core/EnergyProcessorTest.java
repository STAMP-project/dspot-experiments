package org.tron.core;


import Constant.TEST_CONF;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.Parameter.AdaptiveResourceLimitConstants;
import org.tron.core.config.Parameter.ChainConstant;
import org.tron.core.config.args.Args;
import org.tron.core.db.EnergyProcessor;
import org.tron.core.db.Manager;


@Slf4j
public class EnergyProcessorTest {
    private static Manager dbManager;

    private static final String dbPath = "EnergyProcessorTest";

    private static TronApplicationContext context;

    private static final String ASSET_NAME;

    private static final String CONTRACT_PROVIDER_ADDRESS;

    private static final String USER_ADDRESS;

    static {
        Args.setParam(new String[]{ "--output-directory", EnergyProcessorTest.dbPath }, TEST_CONF);
        EnergyProcessorTest.context = new TronApplicationContext(DefaultConfig.class);
        ASSET_NAME = "test_token";
        CONTRACT_PROVIDER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        USER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
    }

    @Test
    public void testUseContractCreatorEnergy() throws Exception {
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyWeight(10000000L);
        AccountCapsule ownerCapsule = EnergyProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(EnergyProcessorTest.CONTRACT_PROVIDER_ADDRESS));
        EnergyProcessorTest.dbManager.getAccountStore().put(ownerCapsule.getAddress().toByteArray(), ownerCapsule);
        EnergyProcessor processor = new EnergyProcessor(EnergyProcessorTest.dbManager);
        long energy = 10000;
        long now = 1526647838000L;
        boolean result = processor.useEnergy(ownerCapsule, energy, now);
        Assert.assertEquals(false, result);
        ownerCapsule.setFrozenForEnergy(10000000L, 0L);
        result = processor.useEnergy(ownerCapsule, energy, now);
        Assert.assertEquals(true, result);
        AccountCapsule ownerCapsuleNew = EnergyProcessorTest.dbManager.getAccountStore().get(ByteArray.fromHexString(EnergyProcessorTest.CONTRACT_PROVIDER_ADDRESS));
        Assert.assertEquals(1526647838000L, ownerCapsuleNew.getLatestOperationTime());
        Assert.assertEquals(1526647838000L, ownerCapsuleNew.getAccountResource().getLatestConsumeTimeForEnergy());
        Assert.assertEquals(10000L, ownerCapsuleNew.getAccountResource().getEnergyUsage());
    }

    @Test
    public void updateAdaptiveTotalEnergyLimit() {
        EnergyProcessor processor = new EnergyProcessor(EnergyProcessorTest.dbManager);
        // open
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveAllowAdaptiveEnergy(1);
        // Test resource usage auto reply
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp(1526647838000L);
        long now = EnergyProcessorTest.dbManager.getWitnessController().getHeadSlot();
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyAverageTime(now);
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyAverageUsage(4000L);
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveLatestBlockHeaderTimestamp((1526647838000L + ((AdaptiveResourceLimitConstants.PERIODS_MS) / 2)));
        processor.updateTotalEnergyAverageUsage();
        Assert.assertEquals(2000L, EnergyProcessorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyAverageUsage());
        // test saveTotalEnergyLimit
        long ratio = (ChainConstant.WINDOW_SIZE_MS) / (AdaptiveResourceLimitConstants.PERIODS_MS);
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyLimit((10000L * ratio));
        Assert.assertEquals(1000L, EnergyProcessorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyTargetLimit());
        // Test exceeds resource limit
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyCurrentLimit((10000L * ratio));
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyAverageUsage(3000L);
        processor.updateAdaptiveTotalEnergyLimit();
        Assert.assertEquals((10000L * ratio), EnergyProcessorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyCurrentLimit());
        // Test exceeds resource limit 2
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyCurrentLimit((20000L * ratio));
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyAverageUsage(3000L);
        processor.updateAdaptiveTotalEnergyLimit();
        Assert.assertEquals((((20000L * ratio) * 99) / 100L), EnergyProcessorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyCurrentLimit());
        // Test less than resource limit
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyCurrentLimit((20000L * ratio));
        EnergyProcessorTest.dbManager.getDynamicPropertiesStore().saveTotalEnergyAverageUsage(500L);
        processor.updateAdaptiveTotalEnergyLimit();
        Assert.assertEquals((((20000L * ratio) * 1000) / 999L), EnergyProcessorTest.dbManager.getDynamicPropertiesStore().getTotalEnergyCurrentLimit());
    }
}

