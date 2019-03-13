package com.baeldung.jackson.annotation.extra;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;


public class ExtraAnnotationUnitTest {
    @Test
    public void whenNotUsingJsonIdentityReferenceAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        IdentityReferenceBeans.BeanWithoutIdentityReference bean = new IdentityReferenceBeans.BeanWithoutIdentityReference(1, "Bean Without Identity Reference Annotation");
        String jsonString = mapper.writeValueAsString(bean);
        Assert.assertThat(jsonString, CoreMatchers.containsString("Bean Without Identity Reference Annotation"));
    }

    @Test
    public void whenUsingJsonIdentityReferenceAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        IdentityReferenceBeans.BeanWithIdentityReference bean = new IdentityReferenceBeans.BeanWithIdentityReference(1, "Bean With Identity Reference Annotation");
        String jsonString = mapper.writeValueAsString(bean);
        Assert.assertEquals("1", jsonString);
    }

    @Test
    public void whenNotUsingJsonAppendAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AppendBeans.BeanWithoutAppend bean = new AppendBeans.BeanWithoutAppend(2, "Bean Without Append Annotation");
        ObjectWriter writer = mapper.writerFor(AppendBeans.BeanWithoutAppend.class).withAttribute("version", "1.0");
        String jsonString = writer.writeValueAsString(bean);
        Assert.assertThat(jsonString, CoreMatchers.not(CoreMatchers.containsString("version")));
        Assert.assertThat(jsonString, CoreMatchers.not(CoreMatchers.containsString("1.0")));
    }

    @Test
    public void whenUsingJsonAppendAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AppendBeans.BeanWithAppend bean = new AppendBeans.BeanWithAppend(2, "Bean With Append Annotation");
        ObjectWriter writer = mapper.writerFor(AppendBeans.BeanWithAppend.class).withAttribute("version", "1.0");
        String jsonString = writer.writeValueAsString(bean);
        Assert.assertThat(jsonString, CoreMatchers.containsString("version"));
        Assert.assertThat(jsonString, CoreMatchers.containsString("1.0"));
    }

    @Test
    public void whenUsingJsonNamingAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        NamingBean bean = new NamingBean(3, "Naming Bean");
        String jsonString = mapper.writeValueAsString(bean);
        Assert.assertThat(jsonString, CoreMatchers.containsString("bean_name"));
    }

    @Test
    public void whenUsingJsonPropertyDescriptionAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper wrapper = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(PropertyDescriptionBean.class, wrapper);
        JsonSchema jsonSchema = wrapper.finalSchema();
        String jsonString = mapper.writeValueAsString(jsonSchema);
        System.out.println(jsonString);
        Assert.assertThat(jsonString, CoreMatchers.containsString("This is a description of the name property"));
    }

    @Test
    public void whenUsingJsonPOJOBuilderAnnotation_thenCorrect() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"id\":5,\"name\":\"POJO Builder Bean\"}";
        POJOBuilderBean bean = mapper.readValue(jsonString, POJOBuilderBean.class);
        Assert.assertEquals(5, bean.getIdentity());
        Assert.assertEquals("POJO Builder Bean", bean.getBeanName());
    }

    @Test
    public void whenUsingJsonTypeIdAnnotation_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(NON_FINAL);
        TypeIdBean bean = new TypeIdBean(6, "Type Id Bean");
        String jsonString = mapper.writeValueAsString(bean);
        Assert.assertThat(jsonString, CoreMatchers.containsString("Type Id Bean"));
    }

    @Test
    public void whenUsingJsonTypeIdResolverAnnotation_thenCorrect() throws IOException {
        TypeIdResolverStructure.FirstBean bean1 = new TypeIdResolverStructure.FirstBean(1, "Bean 1");
        TypeIdResolverStructure.LastBean bean2 = new TypeIdResolverStructure.LastBean(2, "Bean 2");
        List<TypeIdResolverStructure.AbstractBean> beans = new ArrayList<>();
        beans.add(bean1);
        beans.add(bean2);
        TypeIdResolverStructure.BeanContainer serializedContainer = new TypeIdResolverStructure.BeanContainer();
        serializedContainer.setBeans(beans);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(serializedContainer);
        Assert.assertThat(jsonString, CoreMatchers.containsString("bean1"));
        Assert.assertThat(jsonString, CoreMatchers.containsString("bean2"));
        TypeIdResolverStructure.BeanContainer deserializedContainer = mapper.readValue(jsonString, TypeIdResolverStructure.BeanContainer.class);
        List<TypeIdResolverStructure.AbstractBean> beanList = deserializedContainer.getBeans();
        Assert.assertThat(beanList.get(0), CoreMatchers.instanceOf(TypeIdResolverStructure.FirstBean.class));
        Assert.assertThat(beanList.get(1), CoreMatchers.instanceOf(TypeIdResolverStructure.LastBean.class));
    }
}

