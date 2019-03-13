package org.ethereum.manager;


import DirtiesContext.ClassMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.ethereum.core.Block;
import org.ethereum.core.Blockchain;
import org.ethereum.core.EventDispatchThread;
import org.ethereum.core.Genesis;
import org.ethereum.db.DbFlushManager;
import org.ethereum.listener.CompositeEthereumListener;
import org.ethereum.util.ByteUtil;
import org.ethereum.validator.BlockHeaderRule;
import org.ethereum.validator.BlockHeaderValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlockLoaderTest.Config.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BlockLoaderTest {
    private static class Holder<T> {
        private T object;

        public Holder(T object) {
            this.object = object;
        }

        public T get() {
            return object;
        }

        public void set(T object) {
            this.object = object;
        }

        public boolean isEmpty() {
            return Objects.isNull(object);
        }
    }

    static class Config {
        private static final Genesis GENESIS_BLOCK = new Genesis(ByteUtil.EMPTY_BYTE_ARRAY, ByteUtil.EMPTY_BYTE_ARRAY, ByteUtil.EMPTY_BYTE_ARRAY, ByteUtil.EMPTY_BYTE_ARRAY, ByteUtil.EMPTY_BYTE_ARRAY, 0, 0, 0, 0, ByteUtil.EMPTY_BYTE_ARRAY, ByteUtil.EMPTY_BYTE_ARRAY, ByteUtil.EMPTY_BYTE_ARRAY);

        @Bean
        public BlockHeaderValidator headerValidator() {
            BlockHeaderValidator validator = Mockito.mock(BlockHeaderValidator.class);
            Mockito.when(validator.validate(ArgumentMatchers.any())).thenReturn(new BlockHeaderRule.ValidationResult(true, null));
            Mockito.when(validator.validateAndLog(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
            return validator;
        }

        @Bean
        public Blockchain blockchain(BlockLoaderTest.Holder<Block> lastBlockHolder) {
            Blockchain blockchain = Mockito.mock(Blockchain.class);
            Mockito.when(blockchain.getBestBlock()).thenAnswer(( invocation) -> lastBlockHolder.get());
            Mockito.when(blockchain.tryToConnect(ArgumentMatchers.any(Block.class))).thenAnswer(( invocation) -> {
                lastBlockHolder.set(invocation.getArgument(0));
                return ImportResult.IMPORTED_BEST;
            });
            return blockchain;
        }

        @Bean
        public BlockLoaderTest.Holder<Block> lastBlockHolder() {
            return new BlockLoaderTest.Holder(BlockLoaderTest.Config.GENESIS_BLOCK);
        }

        @Bean
        public DbFlushManager dbFlushManager() {
            return Mockito.mock(DbFlushManager.class);
        }

        @Bean
        public CompositeEthereumListener ethereumListener() {
            return Mockito.mock(CompositeEthereumListener.class);
        }

        @Bean
        public EventDispatchThread dispatchThread() {
            return EventDispatchThread.getDefault();
        }

        @Bean
        public BlockLoader blockLoader(BlockHeaderValidator headerValidator, Blockchain blockchain, DbFlushManager dbFlushManager) {
            return new BlockLoader(headerValidator, blockchain, dbFlushManager);
        }
    }

    @Autowired
    private BlockLoader blockLoader;

    private List<Path> dumps;

    @Test
    public void testRegularLoading() {
        Path[] paths = dumps.toArray(new Path[]{  });
        boolean loaded = blockLoader.loadBlocks(paths);
        Assert.assertTrue(loaded);
    }

    @Test
    public void testInconsistentDumpsLoading() {
        List<Path> reversed = new ArrayList<>(dumps);
        Collections.reverse(reversed);
        Path[] paths = reversed.toArray(new Path[]{  });
        boolean loaded = blockLoader.loadBlocks(paths);
        Assert.assertFalse(loaded);
    }

    @Test
    public void testNoDumpsLoading() {
        Assert.assertFalse(blockLoader.loadBlocks());
    }
}

