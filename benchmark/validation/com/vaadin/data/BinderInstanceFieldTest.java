package com.vaadin.data;


import com.vaadin.annotations.PropertyId;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class BinderInstanceFieldTest {
    public static class BindAllFields extends FormLayout {
        private TextField firstName;

        private DateField birthDate;
    }

    public static class BindFieldsUsingAnnotation extends FormLayout {
        @PropertyId("firstName")
        private TextField nameField;

        @PropertyId("birthDate")
        private DateField birthDateField;
    }

    public static class BindNestedFieldsUsingAnnotation extends FormLayout {
        @PropertyId("address.streetAddress")
        private TextField streetAddressField;
    }

    public static class BindDeepNestedFieldsUsingAnnotation extends FormLayout {
        @PropertyId("first.address.streetAddress")
        private TextField firstStreetField;

        @PropertyId("second.address.streetAddress")
        private TextField secondStreetField;
    }

    public static class BindDeepNestingFieldsWithCircularStructure extends FormLayout {
        @PropertyId("child.name")
        private TextField childName;

        @PropertyId("child.child.name")
        private TextField grandchildName;

        @PropertyId("child.child.child.child.child.child.child.child.name")
        private TextField eighthLevelGrandchildName;

        @PropertyId("child.child.child.child.child.child.child.child.child.child.child.child.child.name")
        private TextField distantGreatGrandchildName;
    }

    public static class BindOnlyOneField extends FormLayout {
        private TextField firstName;

        private TextField noFieldInPerson;
    }

    public static class BindWithNoFieldInPerson extends FormLayout {
        private TextField firstName;

        private DateField birthDate;

        private TextField noFieldInPerson;
    }

    public static class BindFieldHasWrongType extends FormLayout {
        private String firstName;

        private DateField birthDate;
    }

    public static class BindGenericField extends FormLayout {
        private BinderInstanceFieldTest.CustomField<String> firstName;
    }

    public static class BindGenericWrongTypeParameterField extends FormLayout {
        private BinderInstanceFieldTest.CustomField<Boolean> firstName;
    }

    public static class BindWrongTypeParameterField extends FormLayout {
        private BinderInstanceFieldTest.IntegerTextField firstName;
    }

    public static class BindOneFieldRequiresConverter extends FormLayout {
        private TextField firstName;

        private TextField age;
    }

    public static class BindGeneric<T> extends FormLayout {
        private BinderInstanceFieldTest.CustomField<T> firstName;
    }

    public static class BindRaw extends FormLayout {
        private BinderInstanceFieldTest.CustomField firstName;
    }

    public static class BindAbstract extends FormLayout {
        private AbstractTextField firstName;
    }

    public static class BindNonInstantiatableType extends FormLayout {
        private BinderInstanceFieldTest.NoDefaultCtor firstName;
    }

    public static class BindComplextHierarchyGenericType extends FormLayout {
        private BinderInstanceFieldTest.ComplexHierarchy firstName;
    }

    public static class NoDefaultCtor extends TextField {
        public NoDefaultCtor(int arg) {
        }
    }

    public static class IntegerTextField extends BinderInstanceFieldTest.CustomField<Integer> {}

    public static class ComplexHierarchy extends BinderInstanceFieldTest.Generic<Long> {}

    public static class Generic<T> extends BinderInstanceFieldTest.ComplexGeneric<Boolean, String, T> {}

    public static class ComplexGeneric<U, V, S> extends BinderInstanceFieldTest.CustomField<V> {}

    public static class CustomField<T> extends AbstractField<T> {
        private T value;

        @Override
        public T getValue() {
            return value;
        }

        @Override
        protected void doSetValue(T value) {
            this.value = value;
        }
    }

    static final class Couple {
        Person first;

        Person second;

        public Person getFirst() {
            return first;
        }

        public Person getSecond() {
            return second;
        }

        public void setFirst(Person first) {
            this.first = first;
        }

        public void setSecond(Person second) {
            this.second = second;
        }
    }

    final class NestingStructure {
        BinderInstanceFieldTest.NestingStructure child;

        String name;

        public BinderInstanceFieldTest.NestingStructure getChild() {
            return child;
        }

        public void setChild(BinderInstanceFieldTest.NestingStructure child) {
            this.child = child;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void bindInstanceFields_bindAllFields() {
        BinderInstanceFieldTest.BindAllFields form = new BinderInstanceFieldTest.BindAllFields();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("foo");
        person.setBirthDate(LocalDate.now());
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertEquals(person.getBirthDate(), form.birthDate.getValue());
        form.firstName.setValue("bar");
        form.birthDate.setValue(person.getBirthDate().plusDays(345));
        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
        Assert.assertEquals(form.birthDate.getValue(), person.getBirthDate());
    }

    @Test(expected = IllegalStateException.class)
    public void bind_instanceFields_noArgsConstructor() {
        BinderInstanceFieldTest.BindAllFields form = new BinderInstanceFieldTest.BindAllFields();
        Binder<Person> binder = new Binder();
        binder.bindInstanceFields(form);
    }

    @Test
    public void bindInstanceFields_bindOnlyOneFields() {
        BinderInstanceFieldTest.BindOnlyOneField form = new BinderInstanceFieldTest.BindOnlyOneField();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("foo");
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertNull(form.noFieldInPerson);
        form.firstName.setValue("bar");
        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
    }

    @Test
    public void bindInstanceFields_bindNotHasValueField_fieldIsNull() {
        BinderInstanceFieldTest.BindFieldHasWrongType form = new BinderInstanceFieldTest.BindFieldHasWrongType();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("foo");
        binder.setBean(person);
        Assert.assertNull(form.firstName);
    }

    @Test
    public void bindInstanceFields_genericField() {
        BinderInstanceFieldTest.BindGenericField form = new BinderInstanceFieldTest.BindGenericField();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("foo");
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        form.firstName.setValue("bar");
        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_genericFieldWithWrongTypeParameter() {
        BinderInstanceFieldTest.BindGenericWrongTypeParameterField form = new BinderInstanceFieldTest.BindGenericWrongTypeParameterField();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_generic() {
        BinderInstanceFieldTest.BindGeneric<String> form = new BinderInstanceFieldTest.BindGeneric<>();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_rawFieldType() {
        BinderInstanceFieldTest.BindRaw form = new BinderInstanceFieldTest.BindRaw();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_abstractFieldType() {
        BinderInstanceFieldTest.BindAbstract form = new BinderInstanceFieldTest.BindAbstract();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_noInstantiatableFieldType() {
        BinderInstanceFieldTest.BindNonInstantiatableType form = new BinderInstanceFieldTest.BindNonInstantiatableType();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_wrongFieldType() {
        BinderInstanceFieldTest.BindWrongTypeParameterField form = new BinderInstanceFieldTest.BindWrongTypeParameterField();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test
    public void bindInstanceFields_complexGenericHierarchy() {
        BinderInstanceFieldTest.BindComplextHierarchyGenericType form = new BinderInstanceFieldTest.BindComplextHierarchyGenericType();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("foo");
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        setValue("bar");
        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
    }

    @Test
    public void bindInstanceFields_bindNotHasValueField_fieldIsNotReplaced() {
        BinderInstanceFieldTest.BindFieldHasWrongType form = new BinderInstanceFieldTest.BindFieldHasWrongType();
        Binder<Person> binder = new Binder(Person.class);
        String name = "foo";
        form.firstName = name;
        Person person = new Person();
        person.setFirstName("foo");
        binder.setBean(person);
        Assert.assertEquals(name, form.firstName);
    }

    @Test
    public void bindInstanceFields_bindAllFieldsUsingAnnotations() {
        BinderInstanceFieldTest.BindFieldsUsingAnnotation form = new BinderInstanceFieldTest.BindFieldsUsingAnnotation();
        Binder<Person> binder = new Binder(Person.class);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("foo");
        person.setBirthDate(LocalDate.now());
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.nameField.getValue());
        Assert.assertEquals(person.getBirthDate(), form.birthDateField.getValue());
        form.nameField.setValue("bar");
        form.birthDateField.setValue(person.getBirthDate().plusDays(345));
        Assert.assertEquals(form.nameField.getValue(), person.getFirstName());
        Assert.assertEquals(form.birthDateField.getValue(), person.getBirthDate());
    }

    @Test
    public void bindInstanceFields_bindNestedFieldUsingAnnotation() {
        BinderInstanceFieldTest.BindNestedFieldsUsingAnnotation form = new BinderInstanceFieldTest.BindNestedFieldsUsingAnnotation();
        Binder<Person> binder = new Binder(Person.class, true);
        binder.bindInstanceFields(form);
        Person person = new Person();
        Address address = new Address();
        address.setStreetAddress("Foo st.");
        person.setAddress(address);
        binder.setBean(person);
        Assert.assertEquals("Reading nested properties bound using annotation", person.getAddress().getStreetAddress(), form.streetAddressField.getValue());
        form.streetAddressField.setValue("Bar ave.");
        Assert.assertEquals("Changing nested properties bound using annotation", form.streetAddressField.getValue(), person.getAddress().getStreetAddress());
    }

    @Test
    public void bindInstanceFields_bindDeepNestedFieldsUsingAnnotation() {
        BinderInstanceFieldTest.BindDeepNestedFieldsUsingAnnotation form = new BinderInstanceFieldTest.BindDeepNestedFieldsUsingAnnotation();
        Binder<BinderInstanceFieldTest.Couple> binder = new Binder(BinderInstanceFieldTest.Couple.class, true);
        binder.bindInstanceFields(form);
        Person first = new Person();
        Person second = new Person();
        Address firstAddress = new Address();
        firstAddress.setStreetAddress("Foo st.");
        first.setAddress(firstAddress);
        Address secondAddress = new Address();
        second.setAddress(secondAddress);
        secondAddress.setStreetAddress("Bar ave.");
        BinderInstanceFieldTest.Couple couple = new BinderInstanceFieldTest.Couple();
        couple.setFirst(first);
        couple.setSecond(second);
        binder.setBean(couple);
        Assert.assertEquals("Binding deep nested properties using annotation", couple.first.getAddress().getStreetAddress(), form.firstStreetField.getValue());
        Assert.assertEquals("Binding parallel deep nested properties using annotation", couple.second.getAddress().getStreetAddress(), form.secondStreetField.getValue());
        form.firstStreetField.setValue(second.getAddress().getStreetAddress());
        Assert.assertEquals("Updating value in deep nested properties", form.firstStreetField.getValue(), first.getAddress().getStreetAddress());
    }

    @Test
    public void bindInstanceFields_circular() {
        BinderInstanceFieldTest.BindDeepNestingFieldsWithCircularStructure form = new BinderInstanceFieldTest.BindDeepNestingFieldsWithCircularStructure();
        Binder<BinderInstanceFieldTest.NestingStructure> binder = new Binder(BinderInstanceFieldTest.NestingStructure.class, true);
        binder.bindInstanceFields(form);
        BinderInstanceFieldTest.NestingStructure parent = new BinderInstanceFieldTest.NestingStructure();
        parent.setName("parent");
        BinderInstanceFieldTest.NestingStructure child = new BinderInstanceFieldTest.NestingStructure();
        child.setName("child");
        parent.setChild(child);
        BinderInstanceFieldTest.NestingStructure grandchild = new BinderInstanceFieldTest.NestingStructure();
        grandchild.setName("grandchild");
        child.setChild(grandchild);
        BinderInstanceFieldTest.NestingStructure root = grandchild;
        for (int i = 1; i < 15; i++) {
            BinderInstanceFieldTest.NestingStructure ns = new BinderInstanceFieldTest.NestingStructure();
            ns.setName(("great " + (root.getName())));
            root.setChild(ns);
            root = ns;
        }
        binder.setBean(parent);
        Assert.assertEquals(child.getName(), form.childName.getValue());
        Assert.assertEquals(grandchild.getName(), form.grandchildName.getValue());
        Assert.assertNotNull("Reading nested properties within default supported nested depth (max 10 levels)", form.eighthLevelGrandchildName);
        // only 10 levels of nesting properties are scanned by default
        Assert.assertNull("By default, only 10 levels of nesting properties are scanned.", form.distantGreatGrandchildName);
    }

    @Test
    public void bindInstanceFields_customNestingLevel() {
        BinderInstanceFieldTest.BindDeepNestingFieldsWithCircularStructure form = new BinderInstanceFieldTest.BindDeepNestingFieldsWithCircularStructure();
        int customScanningDepth = 5;
        PropertyFilterDefinition shallowFilter = new PropertyFilterDefinition(customScanningDepth, Arrays.asList("java.lang"));
        Binder<BinderInstanceFieldTest.NestingStructure> binder = new Binder(BeanPropertySet.get(BinderInstanceFieldTest.NestingStructure.class, true, shallowFilter));
        binder.bindInstanceFields(form);
        BinderInstanceFieldTest.NestingStructure parent = new BinderInstanceFieldTest.NestingStructure();
        parent.setName("parent");
        BinderInstanceFieldTest.NestingStructure child = new BinderInstanceFieldTest.NestingStructure();
        child.setName("child");
        parent.setChild(child);
        BinderInstanceFieldTest.NestingStructure grandchild = new BinderInstanceFieldTest.NestingStructure();
        grandchild.setName("grandchild");
        child.setChild(grandchild);
        BinderInstanceFieldTest.NestingStructure root = grandchild;
        for (int i = 1; i < 15; i++) {
            BinderInstanceFieldTest.NestingStructure ns = new BinderInstanceFieldTest.NestingStructure();
            ns.setName(("great " + (root.getName())));
            root.setChild(ns);
            root = ns;
        }
        binder.setBean(parent);
        Assert.assertEquals(child.getName(), form.childName.getValue());
        Assert.assertEquals("Reading 3rd level nesting works when custom scanning depth is 5", grandchild.getName(), form.grandchildName.getValue());
        Assert.assertNull("Reading eighth level nesting doesn't work when custom scanning depth is 5", form.eighthLevelGrandchildName);
    }

    @Test
    public void bindInstanceFields_bindNotBoundFieldsOnly_customBindingIsNotReplaced() {
        BinderInstanceFieldTest.BindAllFields form = new BinderInstanceFieldTest.BindAllFields();
        Binder<Person> binder = new Binder(Person.class);
        TextField name = new TextField();
        form.firstName = name;
        binder.forField(form.firstName).withValidator(new StringLengthValidator("Name is invalid", 3, 10)).bind("firstName");
        binder.bindInstanceFields(form);
        Person person = new Person();
        String personName = "foo";
        person.setFirstName(personName);
        person.setBirthDate(LocalDate.now());
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertEquals(person.getBirthDate(), form.birthDate.getValue());
        // the instance is not overridden
        Assert.assertEquals(name, form.firstName);
        // Test automatic binding
        form.birthDate.setValue(person.getBirthDate().plusDays(345));
        Assert.assertEquals(form.birthDate.getValue(), person.getBirthDate());
        // Test custom binding
        form.firstName.setValue("aa");
        Assert.assertEquals(personName, person.getFirstName());
        Assert.assertFalse(binder.validate().isOk());
    }

    @Test
    public void bindInstanceFields_fieldsAreConfigured_customBindingIsNotReplaced() {
        BinderInstanceFieldTest.BindWithNoFieldInPerson form = new BinderInstanceFieldTest.BindWithNoFieldInPerson();
        Binder<Person> binder = new Binder(Person.class);
        TextField name = new TextField();
        form.firstName = name;
        binder.forField(form.firstName).withValidator(new StringLengthValidator("Name is invalid", 3, 10)).bind("firstName");
        TextField ageField = new TextField();
        form.noFieldInPerson = ageField;
        binder.forField(form.noFieldInPerson).withConverter(new StringToIntegerConverter("")).bind(Person::getAge, Person::setAge);
        binder.bindInstanceFields(form);
        Person person = new Person();
        String personName = "foo";
        int age = 11;
        person.setFirstName(personName);
        person.setAge(age);
        binder.setBean(person);
        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertEquals(String.valueOf(person.getAge()), form.noFieldInPerson.getValue());
        // the instances are not overridden
        Assert.assertEquals(name, form.firstName);
        Assert.assertEquals(ageField, form.noFieldInPerson);
        // Test correct age
        age += 56;
        form.noFieldInPerson.setValue(String.valueOf(age));
        Assert.assertEquals(form.noFieldInPerson.getValue(), String.valueOf(person.getAge()));
        // Test incorrect name
        form.firstName.setValue("aa");
        Assert.assertEquals(personName, person.getFirstName());
        Assert.assertFalse(binder.validate().isOk());
    }

    @Test
    public void bindInstanceFields_preconfiguredFieldNotBoundToPropertyPreserved() {
        BinderInstanceFieldTest.BindOneFieldRequiresConverter form = new BinderInstanceFieldTest.BindOneFieldRequiresConverter();
        form.age = new TextField();
        form.firstName = new TextField();
        Binder<Person> binder = new Binder(Person.class);
        binder.forField(form.age).withConverter(( str) -> (Integer.parseInt(str)) / 2, ( integer) -> Integer.toString((integer * 2))).bind(Person::getAge, Person::setAge);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("first");
        person.setAge(45);
        binder.setBean(person);
        Assert.assertEquals("90", form.age.getValue());
    }

    @Test
    public void bindInstanceFields_explicitelyBoundFieldAndNotBoundField() {
        BinderInstanceFieldTest.BindOnlyOneField form = new BinderInstanceFieldTest.BindOnlyOneField();
        Binder<Person> binder = new Binder(Person.class);
        binder.forField(new TextField()).bind("firstName");
        binder.bindInstanceFields(form);
    }

    @Test
    public void bindInstanceFields_tentativelyBoundFieldAndNotBoundField() {
        BinderInstanceFieldTest.BindOnlyOneField form = new BinderInstanceFieldTest.BindOnlyOneField();
        Binder<Person> binder = new Binder(Person.class);
        TextField field = new TextField();
        form.firstName = field;
        // This is an incomplete binding which is supposed to be configured
        // manually
        binder.forMemberField(field);
        // bindInstanceFields will not complain even though it can't bind
        // anything as there is a binding in progress (an exception will be
        // thrown later if the binding is not completed)
        binder.bindInstanceFields(form);
    }
}

