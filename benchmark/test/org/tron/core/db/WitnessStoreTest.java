package org.tron.core.db;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.capsule.WitnessCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;


@Slf4j
public class WitnessStoreTest {
    private static final String dbPath = "output-witnessStore-test";

    private static TronApplicationContext context;

    WitnessStore witnessStore;

    static {
        Args.setParam(new String[]{ "-d", WitnessStoreTest.dbPath }, TEST_CONF);
        WitnessStoreTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void putAndGetWitness() {
        WitnessCapsule witnessCapsule = new WitnessCapsule(ByteString.copyFromUtf8("100000000x"), 100L, "");
        this.witnessStore.put(witnessCapsule.getAddress().toByteArray(), witnessCapsule);
        WitnessCapsule witnessSource = this.witnessStore.get(ByteString.copyFromUtf8("100000000x").toByteArray());
        Assert.assertEquals(witnessCapsule.getAddress(), witnessSource.getAddress());
        Assert.assertEquals(witnessCapsule.getVoteCount(), witnessSource.getVoteCount());
        Assert.assertEquals(ByteString.copyFromUtf8("100000000x"), witnessSource.getAddress());
        Assert.assertEquals(100L, witnessSource.getVoteCount());
        witnessCapsule = new WitnessCapsule(ByteString.copyFromUtf8(""), 100L, "");
        this.witnessStore.put(witnessCapsule.getAddress().toByteArray(), witnessCapsule);
        witnessSource = this.witnessStore.get(ByteString.copyFromUtf8("").toByteArray());
        Assert.assertEquals(witnessCapsule.getAddress(), witnessSource.getAddress());
        Assert.assertEquals(witnessCapsule.getVoteCount(), witnessSource.getVoteCount());
        Assert.assertEquals(ByteString.copyFromUtf8(""), witnessSource.getAddress());
        Assert.assertEquals(100L, witnessSource.getVoteCount());
    }
}

