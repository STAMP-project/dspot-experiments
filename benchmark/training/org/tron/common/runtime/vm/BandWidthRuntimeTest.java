/**
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.common.runtime.vm;


import com.google.protobuf.Any;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.runtime.TVMTestUtils;
import org.tron.common.runtime.vm.program.invoke.ProgramInvokeFactoryImpl;
import org.tron.common.storage.DepositImpl;
import org.tron.core.Constant;
import org.tron.core.Wallet;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.ReceiptCapsule;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.core.db.TransactionTrace;
import org.tron.core.exception.TronException;
import org.tron.protos.Contract.TriggerSmartContract;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Contract;


/**
 * pragma solidity ^0.4.24;
 *
 * contract ForI{
 *
 * uint256 public balances;
 *
 * function setCoin(uint receiver) public { for(uint i=0;i<receiver;i++){ balances = balances++; } }
 * }
 */
public class BandWidthRuntimeTest {
    public static final long totalBalance = 10000000000000L;

    private static String dbPath = "output_BandWidthRuntimeTest_test";

    private static String dbDirectory = "db_BandWidthRuntimeTest_test";

    private static String indexDirectory = "index_BandWidthRuntimeTest_test";

    private static AnnotationConfigApplicationContext context;

    private static Manager dbManager;

    private static String OwnerAddress = "TCWHANtDDdkZCTo2T2peyEq3Eg9c2XB7ut";

    private static String TriggerOwnerAddress = "TCSgeWapPJhCqgWRxXCKb6jJ5AgNWSGjPA";

    private static String TriggerOwnerTwoAddress = "TPMBUANrTwwQAPwShn7ZZjTJz1f3F8jknj";

    static {
        Args.setParam(new String[]{ "--output-directory", BandWidthRuntimeTest.dbPath, "--storage-db-directory", BandWidthRuntimeTest.dbDirectory, "--storage-index-directory", BandWidthRuntimeTest.indexDirectory, "-w" }, "config-test-mainnet.conf");
        BandWidthRuntimeTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void testSuccess() {
        try {
            byte[] contractAddress = createContract();
            AccountCapsule triggerOwner = BandWidthRuntimeTest.dbManager.getAccountStore().get(Wallet.decodeFromBase58Check(BandWidthRuntimeTest.TriggerOwnerAddress));
            long energy = triggerOwner.getEnergyUsage();
            TriggerSmartContract triggerContract = TVMTestUtils.createTriggerContract(contractAddress, "setCoin(uint256)", "3", false, 0, Wallet.decodeFromBase58Check(BandWidthRuntimeTest.TriggerOwnerAddress));
            Transaction transaction = Transaction.newBuilder().setRawData(raw.newBuilder().addContract(Contract.newBuilder().setParameter(Any.pack(triggerContract)).setType(ContractType.TriggerSmartContract)).setFeeLimit(1000000000)).build();
            TransactionCapsule trxCap = new TransactionCapsule(transaction);
            TransactionTrace trace = new TransactionTrace(trxCap, BandWidthRuntimeTest.dbManager);
            BandWidthRuntimeTest.dbManager.consumeBandwidth(trxCap, trace);
            BlockCapsule blockCapsule = null;
            DepositImpl deposit = DepositImpl.createRoot(BandWidthRuntimeTest.dbManager);
            Runtime runtime = new org.tron.common.runtime.RuntimeImpl(trace, blockCapsule, deposit, new ProgramInvokeFactoryImpl());
            trace.init(blockCapsule);
            trace.exec();
            trace.finalization();
            triggerOwner = BandWidthRuntimeTest.dbManager.getAccountStore().get(Wallet.decodeFromBase58Check(BandWidthRuntimeTest.TriggerOwnerAddress));
            energy = triggerOwner.getEnergyUsage();
            long balance = triggerOwner.getBalance();
            Assert.assertEquals(45706, trace.getReceipt().getEnergyUsageTotal());
            Assert.assertEquals(45706, energy);
            Assert.assertEquals(BandWidthRuntimeTest.totalBalance, balance);
        } catch (TronException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testSuccessNoBandd() {
        try {
            byte[] contractAddress = createContract();
            TriggerSmartContract triggerContract = TVMTestUtils.createTriggerContract(contractAddress, "setCoin(uint256)", "50", false, 0, Wallet.decodeFromBase58Check(BandWidthRuntimeTest.TriggerOwnerTwoAddress));
            Transaction transaction = Transaction.newBuilder().setRawData(raw.newBuilder().addContract(Contract.newBuilder().setParameter(Any.pack(triggerContract)).setType(ContractType.TriggerSmartContract)).setFeeLimit(1000000000)).build();
            TransactionCapsule trxCap = new TransactionCapsule(transaction);
            TransactionTrace trace = new TransactionTrace(trxCap, BandWidthRuntimeTest.dbManager);
            BandWidthRuntimeTest.dbManager.consumeBandwidth(trxCap, trace);
            long bandWidth = (trxCap.getSerializedSize()) + (Constant.MAX_RESULT_SIZE_IN_TX);
            BlockCapsule blockCapsule = null;
            DepositImpl deposit = DepositImpl.createRoot(BandWidthRuntimeTest.dbManager);
            Runtime runtime = new org.tron.common.runtime.RuntimeImpl(trace, blockCapsule, deposit, new ProgramInvokeFactoryImpl());
            trace.init(blockCapsule);
            trace.exec();
            trace.finalization();
            AccountCapsule triggerOwnerTwo = BandWidthRuntimeTest.dbManager.getAccountStore().get(Wallet.decodeFromBase58Check(BandWidthRuntimeTest.TriggerOwnerTwoAddress));
            long balance = triggerOwnerTwo.getBalance();
            ReceiptCapsule receipt = trace.getReceipt();
            Assert.assertEquals(bandWidth, receipt.getNetUsage());
            Assert.assertEquals(522850, receipt.getEnergyUsageTotal());
            Assert.assertEquals(50000, receipt.getEnergyUsage());
            Assert.assertEquals(47285000, receipt.getEnergyFee());
            Assert.assertEquals(((BandWidthRuntimeTest.totalBalance) - (receipt.getEnergyFee())), balance);
        } catch (TronException e) {
            Assert.assertNotNull(e);
        }
    }
}

