package org.dromara.raincat.core.spi;


import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.dromara.raincat.common.enums.CompensationCacheTypeEnum;
import org.dromara.raincat.common.enums.SerializeProtocolEnum;
import org.dromara.raincat.common.holder.ServiceBootstrap;
import org.dromara.raincat.common.serializer.ObjectSerializer;
import org.dromara.raincat.core.helper.SpringBeanUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceBootstrapTest {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBootstrapTest.class);

    @Test
    public void loadFirst() throws Exception {
        final ObjectSerializer objectSerializer = ServiceBootstrap.loadFirst(ObjectSerializer.class);
        ServiceBootstrapTest.LOGGER.info("??????????{}", objectSerializer.getClass().getName());
    }

    @Test
    public void loadAll() {
        // spi  serialize
        final SerializeProtocolEnum serializeProtocolEnum = SerializeProtocolEnum.HESSIAN;
        final ServiceLoader<ObjectSerializer> objectSerializers = ServiceBootstrap.loadAll(ObjectSerializer.class);
        final Optional<ObjectSerializer> serializer = StreamSupport.stream(objectSerializers.spliterator(), false).filter(( objectSerializer) -> Objects.equals(objectSerializer.getScheme(), serializeProtocolEnum.getSerializeProtocol())).findFirst();
        serializer.ifPresent(( objectSerializer) -> LOGGER.info("??????????{}", objectSerializer.getClass().getName()));
        // spi  RecoverRepository support
        final CompensationCacheTypeEnum compensationCacheTypeEnum = CompensationCacheTypeEnum.DB;
        final ServiceLoader<TransactionRecoverRepository> recoverRepositories = ServiceBootstrap.loadAll(TransactionRecoverRepository.class);
        final Optional<TransactionRecoverRepository> repositoryOptional = StreamSupport.stream(recoverRepositories.spliterator(), false).filter(( recoverRepository) -> Objects.equals(recoverRepository.getScheme(), compensationCacheTypeEnum.getCompensationCacheType())).findFirst();
        // ?compensationCache?????spring??
        repositoryOptional.ifPresent(( repository) -> {
            serializer.ifPresent(repository::setSerializer);
            SpringBeanUtils.getInstance().registerBean(.class.getName(), repository);
        });
    }
}

