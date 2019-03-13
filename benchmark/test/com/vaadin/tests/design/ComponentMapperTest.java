package com.vaadin.tests.design;


import Design.DefaultComponentMapper;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.Design.ComponentFactory;
import com.vaadin.ui.declarative.Design.ComponentMapper;
import com.vaadin.ui.declarative.DesignContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ComponentMapperTest {
    private static final ComponentMapper defaultMapper = Design.getComponentMapper();

    private static final ThreadLocal<ComponentMapper> currentMapper = new ThreadLocal<>();

    static {
        Design.setComponentMapper(new ComponentMapper() {
            @Override
            public Component tagToComponent(String tag, ComponentFactory componentFactory, DesignContext context) {
                return getActualMapper().tagToComponent(tag, componentFactory, context);
            }

            @Override
            public String componentToTag(Component component, DesignContext context) {
                return getActualMapper().componentToTag(component, context);
            }

            private ComponentMapper getActualMapper() {
                ComponentMapper mapper = ComponentMapperTest.currentMapper.get();
                if (mapper == null) {
                    mapper = ComponentMapperTest.defaultMapper;
                }
                return mapper;
            }
        });
    }

    private final class CustomComponentMapper extends Design.DefaultComponentMapper {
        @Override
        public Component tagToComponent(String tag, ComponentFactory componentFactory, DesignContext context) {
            if (tag.startsWith("custom-")) {
                ComponentMapperTest.ComponentWithCustomTagName component = ((ComponentMapperTest.ComponentWithCustomTagName) (componentFactory.createComponent(ComponentMapperTest.ComponentWithCustomTagName.class.getName(), context)));
                component.tagName = tag;
                return component;
            } else {
                return super.tagToComponent(tag, componentFactory, context);
            }
        }

        @Override
        public String componentToTag(Component component, DesignContext context) {
            if (component instanceof ComponentMapperTest.ComponentWithCustomTagName) {
                ComponentMapperTest.ComponentWithCustomTagName withCustomTagName = ((ComponentMapperTest.ComponentWithCustomTagName) (component));
                return withCustomTagName.tagName;
            } else {
                return super.componentToTag(component, context);
            }
        }
    }

    public static class ComponentWithCustomTagName extends Label {
        private String tagName;
    }

    @Test
    public void testCustomComponentMapperRead() {
        ComponentMapperTest.currentMapper.set(new ComponentMapperTest.CustomComponentMapper());
        Component component = Design.read(new ByteArrayInputStream("<custom-foobar />".getBytes()));
        Assert.assertTrue(("<custom-foobar> should resolve " + (ComponentMapperTest.ComponentWithCustomTagName.class.getSimpleName())), (component instanceof ComponentMapperTest.ComponentWithCustomTagName));
        Assert.assertEquals("custom-foobar", ((ComponentMapperTest.ComponentWithCustomTagName) (component)).tagName);
    }

    @Test
    public void testCustomComponentMapperWrite() throws IOException {
        ComponentMapperTest.currentMapper.set(new ComponentMapperTest.CustomComponentMapper());
        ComponentMapperTest.ComponentWithCustomTagName component = new ComponentMapperTest.ComponentWithCustomTagName();
        component.tagName = "custom-special";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(component, bos);
        String writtenDesign = new String(bos.toByteArray());
        Assert.assertTrue(("Written design should contain \"<custom-special\", but instead got " + writtenDesign), writtenDesign.contains("<custom-special"));
    }
}

