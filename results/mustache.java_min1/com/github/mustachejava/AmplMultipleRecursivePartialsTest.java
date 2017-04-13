

package com.github.mustachejava;


public final class AmplMultipleRecursivePartialsTest {
    private static final java.lang.String TEMPLATE_FILE = "multiple_recursive_partials.html";

    private static java.io.File root;

    @java.lang.SuppressWarnings(value = "unused")
    private static class Model {
        com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type type;

        java.util.List<com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model> items;

        Model(com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type type, java.util.List<com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model> items) {
            this.type = type;
            this.items = items;
        }

        Model(com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type type) {
            this.type = type;
        }

        com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type getType() {
            return type;
        }

        java.util.List<com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model> getItems() {
            return items;
        }
    }

    @java.lang.SuppressWarnings(value = "unused")
    private enum Type {
FOO, BAR;
        boolean isFoo() {
            return (this) == (com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type.FOO);
        }

        boolean isBar() {
            return (this) == (com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type.BAR);
        }
    }

    @org.junit.BeforeClass
    public static void setUp() throws java.lang.Exception {
        java.io.File file = new java.io.File("compiler/src/test/resources");
        com.github.mustachejava.AmplMultipleRecursivePartialsTest.root = (new java.io.File(file, com.github.mustachejava.AmplMultipleRecursivePartialsTest.TEMPLATE_FILE).exists()) ? file : new java.io.File("src/test/resources");
    }

    @org.junit.Test
    public void shouldHandleTemplateWithMultipleRecursivePartials() throws java.lang.Exception {
        com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplMultipleRecursivePartialsTest.root);
        com.github.mustachejava.Mustache template = factory.compile(com.github.mustachejava.AmplMultipleRecursivePartialsTest.TEMPLATE_FILE);
        java.io.StringWriter sw = new java.io.StringWriter();
        com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model model = new com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model(com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type.FOO, java.util.Arrays.asList(new com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model(com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type.BAR), new com.github.mustachejava.AmplMultipleRecursivePartialsTest.Model(com.github.mustachejava.AmplMultipleRecursivePartialsTest.Type.FOO)));
        template.execute(sw, model);
        org.junit.Assert.assertEquals("I\'m a foo!\nIn foo: I\'m a bar!\n\nIn foo: I\'m a foo!\n\n\n", sw.toString());
    }
}

