package com.andrei1058.bedwars.configuration;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleJarDistributionTest {

    @Test
    void descriptorKeepsTheConfigurableSingleJarEntrypoint() throws Exception {
        Path module = Path.of(System.getProperty("basedir"));
        String descriptor = Files.readString(module.resolve("src/main/resources/plugin.yml"));

        assertTrue(descriptor.contains("main: com.andrei1058.bedwars.BedWars"));
        assertFalse(descriptor.contains("${plugin.main}"));
    }

    @Test
    void reactorPublishesOnlyTheMonolithicPluginModule() throws Exception {
        Path module = Path.of(System.getProperty("basedir"));
        Document rootPom = parse(module.resolve("../pom.xml"));
        Document pluginPom = parse(module.resolve("pom.xml"));

        List<String> modules = directChildTexts(
                directChild(rootPom.getDocumentElement(), "modules"), "module");
        assertTrue(modules.contains("bedwars-plugin"));
        assertFalse(modules.contains("bedwars-lobby"));
        assertFalse(modules.contains("bedwars-arena"));
        assertEquals("simpmc-bedwars-plugin",
                directChildText(pluginPom.getDocumentElement(), "artifactId"));
        assertEquals("SimpMC-BedWars-${project.version}",
                directChildText(directChild(pluginPom.getDocumentElement(), "build"), "finalName"));
    }

    private static Document parse(Path path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newDocumentBuilder().parse(path.toFile());
    }

    private static Element directChild(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element element && name.equals(element.getTagName())) {
                return element;
            }
        }
        throw new IllegalStateException("Missing " + name);
    }

    private static String directChildText(Element parent, String name) {
        return directChild(parent, name).getTextContent().trim();
    }

    private static List<String> directChildTexts(Element parent, String name) {
        List<String> values = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element element && name.equals(element.getTagName())) {
                values.add(element.getTextContent().trim());
            }
        }
        return values;
    }
}
