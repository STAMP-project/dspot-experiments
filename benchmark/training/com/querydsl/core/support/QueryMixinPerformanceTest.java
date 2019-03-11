package com.querydsl.core.support;


import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.PathMetadataFactory;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class QueryMixinPerformanceTest {
    public static final int iterations = 2000000;

    @Test
    public void normal() {
        // 1791
        EntityPath<DummyEntity> entity = new com.querydsl.core.types.dsl.EntityPathBase<DummyEntity>(DummyEntity.class, "entity");
        com.querydsl.core.types.dsl.EntityPathBase<DummyEntity> other = new com.querydsl.core.types.dsl.EntityPathBase<DummyEntity>(DummyEntity.class, PathMetadataFactory.forProperty(entity, "other"));
        long start = System.currentTimeMillis();
        for (int i = 0; i < (QueryMixinPerformanceTest.iterations); i++) {
            QueryMixin<?> mixin = new QueryMixin<Void>();
            mixin.from(entity);
            mixin.where(other.eq(new DummyEntity()));
            mixin.setProjection(entity);
        }
        System.err.println(((System.currentTimeMillis()) - start));
    }

    @Test
    public void array_arguments() {
        // 2260
        EntityPath<DummyEntity> entity = new com.querydsl.core.types.dsl.EntityPathBase<DummyEntity>(DummyEntity.class, "entity");
        EntityPath[] entities = new EntityPath[]{ entity };
        com.querydsl.core.types.dsl.EntityPathBase<DummyEntity> other = new com.querydsl.core.types.dsl.EntityPathBase<DummyEntity>(DummyEntity.class, PathMetadataFactory.forProperty(entity, "other"));
        long start = System.currentTimeMillis();
        for (int i = 0; i < (QueryMixinPerformanceTest.iterations); i++) {
            QueryMixin<?> mixin = new QueryMixin<Void>();
            mixin.from(entities);
            mixin.where(other.eq(new DummyEntity()));
            mixin.setProjection(entity);
        }
        System.err.println(((System.currentTimeMillis()) - start));
    }
}

