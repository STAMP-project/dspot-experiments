package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PreSave;
import dev.morphia.query.FindOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class EnumMappingTest extends TestBase {
    @Test
    public void getMapOfEnum() {
        EnumMappingTest.Class1 entity = new EnumMappingTest.Class1();
        entity.getMap().put("key", EnumMappingTest.Foo.BAR);
        getDs().save(entity);
        getMorphia().map(EnumMappingTest.Class1.class);
        entity = getDs().find(EnumMappingTest.Class1.class).find(new FindOptions().limit(1)).tryNext();
        final Map<String, EnumMappingTest.Foo> map = entity.getMap();
        EnumMappingTest.Foo b = map.get("key");
        Assert.assertNotNull(b);
    }

    @Test
    public void testCustomer() {
        EnumMappingTest.Customer customer = new EnumMappingTest.Customer();
        customer.add(EnumMappingTest.WebTemplateType.CrewContract, new EnumMappingTest.WebTemplate("template #1"));
        customer.add(EnumMappingTest.WebTemplateType.CrewContractHeader, new EnumMappingTest.WebTemplate("template #2"));
        getDs().save(customer);
        EnumMappingTest.Customer loaded = getDs().get(customer);
        Assert.assertEquals(customer.map, loaded.map);
    }

    @Test
    public void testCustomerWithArrayList() {
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        getMorphia().getMapper().getOptions().setStoreNulls(true);
        getMorphia().map(EnumMappingTest.CustomerWithArrayList.class);
        EnumMappingTest.CustomerWithArrayList customer = new EnumMappingTest.CustomerWithArrayList();
        List<EnumMappingTest.WebTemplate> templates1 = new ArrayList<EnumMappingTest.WebTemplate>();
        templates1.add(new EnumMappingTest.WebTemplate("template #1.1"));
        templates1.add(new EnumMappingTest.WebTemplate("template #1.2"));
        customer.add(EnumMappingTest.WebTemplateType.CrewContract, templates1);
        List<EnumMappingTest.WebTemplate> templates2 = new ArrayList<EnumMappingTest.WebTemplate>();
        templates1.add(new EnumMappingTest.WebTemplate("template #2.1"));
        templates1.add(new EnumMappingTest.WebTemplate("template #2.2"));
        customer.add(EnumMappingTest.WebTemplateType.CrewContractHeader, templates2);
        getDs().save(customer);
        EnumMappingTest.CustomerWithArrayList loaded = getDs().get(customer);
        Assert.assertEquals(customer.mapWithArrayList, loaded.mapWithArrayList);
    }

    @Test
    public void testCustomerWithList() {
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        getMorphia().getMapper().getOptions().setStoreNulls(true);
        getMorphia().map(EnumMappingTest.CustomerWithArrayList.class);
        EnumMappingTest.CustomerWithList customer = new EnumMappingTest.CustomerWithList();
        List<EnumMappingTest.WebTemplate> templates1 = new ArrayList<EnumMappingTest.WebTemplate>();
        templates1.add(new EnumMappingTest.WebTemplate("template #1.1"));
        templates1.add(new EnumMappingTest.WebTemplate("template #1.2"));
        customer.add(EnumMappingTest.WebTemplateType.CrewContract, templates1);
        List<EnumMappingTest.WebTemplate> templates2 = new ArrayList<EnumMappingTest.WebTemplate>();
        templates1.add(new EnumMappingTest.WebTemplate("template #2.1"));
        templates1.add(new EnumMappingTest.WebTemplate("template #2.2"));
        customer.add(EnumMappingTest.WebTemplateType.CrewContractHeader, templates2);
        getDs().save(customer);
        EnumMappingTest.CustomerWithList loaded = getDs().get(customer);
        Assert.assertEquals(customer.mapWithList, loaded.mapWithList);
    }

    @Test
    public void testEnumMapping() {
        getDs().getDB().dropDatabase();
        getMorphia().map(EnumMappingTest.ContainsEnum.class);
        getDs().save(new EnumMappingTest.ContainsEnum());
        Assert.assertEquals(1, getDs().find(EnumMappingTest.ContainsEnum.class).field("foo").equal(EnumMappingTest.Foo.BAR).count());
        Assert.assertEquals(1, getDs().find(EnumMappingTest.ContainsEnum.class).filter("foo", EnumMappingTest.Foo.BAR).count());
        Assert.assertEquals(1, getDs().find(EnumMappingTest.ContainsEnum.class).disableValidation().filter("foo", EnumMappingTest.Foo.BAR).count());
    }

    enum Foo {

        BAR,
        BAZ;}

    public enum WebTemplateType {

        CrewContract("Contract"),
        CrewContractHeader("Contract Header");
        private String text;

        WebTemplateType(final String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    @Entity("user")
    public static class Class1 {
        @Id
        private ObjectId id;

        private Map<String, EnumMappingTest.Foo> map = new HashMap<String, EnumMappingTest.Foo>();

        public Map<String, EnumMappingTest.Foo> getMap() {
            return map;
        }
    }

    public static class ContainsEnum {
        @Id
        private ObjectId id;

        private EnumMappingTest.Foo foo = EnumMappingTest.Foo.BAR;

        @PreSave
        void testMapping() {
        }
    }

    @Embedded
    public static class WebTemplate {
        private ObjectId id = new ObjectId();

        private String templateName;

        private String content;

        public WebTemplate() {
        }

        public WebTemplate(final String content) {
            this.content = content;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((templateName) != null ? templateName.hashCode() : 0);
            result = (31 * result) + ((content) != null ? content.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final EnumMappingTest.WebTemplate that = ((EnumMappingTest.WebTemplate) (o));
            if ((content) != null ? !(content.equals(that.content)) : (that.content) != null) {
                return false;
            }
            if ((id) != null ? !(id.equals(that.id)) : (that.id) != null) {
                return false;
            }
            if ((templateName) != null ? !(templateName.equals(that.templateName)) : (that.templateName) != null) {
                return false;
            }
            return true;
        }
    }

    @Entity(noClassnameStored = true)
    public static class Customer {
        private final Map<EnumMappingTest.WebTemplateType, EnumMappingTest.WebTemplate> map = new HashMap<EnumMappingTest.WebTemplateType, EnumMappingTest.WebTemplate>();

        @Id
        private ObjectId id;

        public void add(final EnumMappingTest.WebTemplateType type, final EnumMappingTest.WebTemplate template) {
            map.put(type, template);
        }
    }

    @Entity(noClassnameStored = true)
    public static class CustomerWithList {
        private final Map<EnumMappingTest.WebTemplateType, List<EnumMappingTest.WebTemplate>> mapWithList = new HashMap<EnumMappingTest.WebTemplateType, List<EnumMappingTest.WebTemplate>>();

        @Id
        private ObjectId id;

        public void add(final EnumMappingTest.WebTemplateType type, final List<EnumMappingTest.WebTemplate> templates) {
            mapWithList.put(type, templates);
        }
    }

    @Entity(noClassnameStored = true)
    public static class CustomerWithArrayList {
        private final Map<EnumMappingTest.WebTemplateType, List<EnumMappingTest.WebTemplate>> mapWithArrayList = new HashMap<EnumMappingTest.WebTemplateType, List<EnumMappingTest.WebTemplate>>();

        @Id
        private ObjectId id;

        public void add(final EnumMappingTest.WebTemplateType type, final List<EnumMappingTest.WebTemplate> templates) {
            mapWithArrayList.put(type, templates);
        }
    }
}

