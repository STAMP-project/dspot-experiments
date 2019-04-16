import sys
import xml.etree.ElementTree as ET

xml_namespace = 'http://maven.apache.org/POM/4.0.0'
xml_prefix = '{' + xml_namespace + '}'
xml_build_tag = xml_prefix + 'build'
test_source_directory_tag = 'testSourceDirectory'
xml_test_source_directory_tag = xml_prefix + test_source_directory_tag

def switch(path_to_test_suite, path_to_pom_to_modify):
    ET.register_namespace('', xml_namespace)
    path_to_root_pom = path_to_pom_to_modify
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    if not root_pom.findall(xml_build_tag):
        xml_build_node = ET.SubElement(root_pom, xml_build_tag)
    else:
        xml_build_node = root_pom.findall(xml_build_tag)[0]
    if not xml_build_node.findall(xml_test_source_directory_tag):
        test_source_directory_node = ET.SubElement(xml_build_node, xml_test_source_directory_tag)
    else:
        test_source_directory_node = xml_build_node.findall(xml_test_source_directory_tag)[0]
    test_source_directory_node.text = path_to_test_suite
    tree.write(path_to_root_pom)

def clean(path_to_pom_to_modify):
    ET.register_namespace('', xml_namespace)
    path_to_root_pom = path_to_pom_to_modify
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    build_node = root_pom.findall(xml_build_tag)[0]
    build_node.remove(build_node.findall(xml_test_source_directory_tag)[0])
    tree.write(path_to_root_pom)

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "clean", sys.argv[1]
        clean(sys.argv[1])
    else:
        print "switch test suite for", sys.argv[2], "to", sys.argv[1]
        switch(sys.argv[1], sys.argv[2])
