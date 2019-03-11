package org.tron.core.db;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.capsule.VotesCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.protos.Protocol.Vote;


@Slf4j
public class VotesStoreTest {
    private static final String dbPath = "output-votesStore-test";

    private static TronApplicationContext context;

    VotesStore votesStore;

    static {
        Args.setParam(new String[]{ "-d", VotesStoreTest.dbPath }, TEST_CONF);
        VotesStoreTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void putAndGetVotes() {
        List<Vote> oldVotes = new ArrayList<Vote>();
        VotesCapsule votesCapsule = new VotesCapsule(ByteString.copyFromUtf8("100000000x"), oldVotes);
        this.votesStore.put(votesCapsule.createDbKey(), votesCapsule);
        Assert.assertTrue("votesStore is empyt", votesStore.iterator().hasNext());
        Assert.assertTrue(votesStore.has(votesCapsule.createDbKey()));
        VotesCapsule votesSource = this.votesStore.get(ByteString.copyFromUtf8("100000000x").toByteArray());
        Assert.assertEquals(votesCapsule.getAddress(), votesSource.getAddress());
        Assert.assertEquals(ByteString.copyFromUtf8("100000000x"), votesSource.getAddress());
        // votesCapsule = new VotesCapsule(ByteString.copyFromUtf8(""), oldVotes);
        // this.votesStore.put(votesCapsule.createDbKey(), votesCapsule);
        // votesSource = this.votesStore.get(ByteString.copyFromUtf8("").toByteArray());
        // Assert.assertEquals(votesStore.getAllVotes().size(), 2);
        // Assert.assertEquals(votesCapsule.getAddress(), votesSource.getAddress());
        // Assert.assertEquals(null, votesSource.getAddress());
    }
}

