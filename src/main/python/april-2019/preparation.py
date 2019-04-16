import sys
import toolbox
import xml.etree.ElementTree as ET

xml_namespace = 'http://maven.apache.org/POM/4.0.0'
xml_prefix = '{' + xml_namespace + '}'
xml_build_tag = xml_prefix + 'build'
xml_plugins_tag = xml_prefix + 'plugins'
xml_plugin_management_tag = xml_prefix + 'pluginManagement'
xml_group_id_tag = xml_prefix + 'groupId'
xml_artifact_id_tag = xml_prefix + 'artifactId'
xml_configuration_tag = xml_prefix + 'configuration'
xml_includes_test_source_roots_tag = xml_prefix + 'includesTestSourceRoots'
xml_properties_tag = xml_prefix + 'properties'
xml_xwiki_enforcer_skip_tag = xml_prefix + 'xwiki.enforcer.skip'
xml_xwiki_clover_skip_tag = xml_prefix + 'xwiki.clover.skip'

xwiki_commons_tools_path = "xwiki-commons-tools/"

xml_properties_maven_compiler_source_with_error_tag = xml_prefix + 'maven.compile.source'
xml_properties_maven_compiler_source_without_error_tag = xml_prefix + 'maven.compiler.source'
xml_properties_maven_compiler_target_with_error_tag = xml_prefix + 'maven.compile.target'
xml_properties_maven_compiler_target_without_error_tag = xml_prefix + 'maven.compiler.target'

xml_maven_compiler_text = '1.5'

def prepare(project):
    if project == "xwiki-commons":
        set_true_include_test_roots(project)
    elif project == "commons-cli":
        fix_maven_compiler_properties(project)

def fix_maven_compiler_properties(project):
    fix_maven_compiler_properties_for_given_pom(toolbox.prefix_dataset + "/" + project + "/pom.xml")
    fix_maven_compiler_properties_for_given_pom(toolbox.prefix_dataset + "/" + project + "_parent/pom.xml")

def fix_maven_compiler_properties_for_given_pom(path_to_root_pom):
    ET.register_namespace('', xml_namespace)
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    properties_node = root_pom.findall(xml_properties_tag)[0]
    if len(properties_node.findall(xml_properties_maven_compiler_source_with_error_tag)) > 0:
        properties_node.findall(xml_properties_maven_compiler_source_with_error_tag)[0].tag = xml_properties_maven_compiler_source_without_error_tag
    if len(properties_node.findall(xml_properties_maven_compiler_target_with_error_tag)) > 0:
        properties_node.findall(xml_properties_maven_compiler_target_with_error_tag)[0].tag = xml_properties_maven_compiler_target_without_error_tag
    '''
    if len(properties_node.findall(xml_properties_maven_compiler_source_with_error_tag)) > 0:
        xml_properties_maven_compiler_source_without_error_node = ET.SubElement(properties_node, xml_properties_maven_compiler_source_without_error_tag)
        xml_properties_maven_compiler_source_without_error_node.text = xml_maven_compiler_text
        xml_properties_maven_compiler_target_without_error_node = ET.SubElement(properties_node, xml_properties_maven_compiler_target_without_error_tag)
        xml_properties_maven_compiler_target_without_error_node.text = xml_maven_compiler_text
    '''
    tree.write(path_to_root_pom)


def set_true_include_test_roots(project):
    ET.register_namespace('', xml_namespace)
    # set in top pom enforcer skip to true
    path_to_root_pom = toolbox.prefix_dataset + "/" + project + "/pom.xml"
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    root_pom.findall(xml_properties_tag)[0].findall(xml_xwiki_enforcer_skip_tag)[0].text = "true"
    current_xml = root_pom.findall(xml_build_tag)[0].findall(xml_plugin_management_tag)[0].findall(xml_plugins_tag)[0]
    # include test code into the clover instrumentation
    for child in current_xml:
        group_id = child.findall(xml_group_id_tag)[0].text
        artifact_id = child.findall(xml_artifact_id_tag)[0].text
        if group_id == 'org.openclover' and artifact_id == 'clover-maven-plugin':
            if len(child.findall(xml_configuration_tag)) > 0:
                if len(child.findall(xml_configuration_tag)[0].findall(xml_includes_test_source_roots_tag)) > 0:
                    child.findall(xml_configuration_tag)[0].findall(xml_includes_test_source_roots_tag)[0].text = "true"
    tree.write(path_to_root_pom)

    # set in the xwiki-commons-tools pom to not skip clover in the submodule
    path_to_root_pom = toolbox.prefix_dataset + project + "/" + xwiki_commons_tools_path + "pom.xml"
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    if len(root_pom.findall(xml_properties_tag)) > 0:
        for i in root_pom.findall(xml_properties_tag)[0]:
            if i.tag == xml_xwiki_clover_skip_tag:
                i.text = "false"
    tree.write(path_to_root_pom)

    # set in the second version enforcer skip to true
    path_to_root_pom = toolbox.prefix_dataset + "/" + project + toolbox.suffix_parent + "/pom.xml"
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    root_pom.findall(xml_properties_tag)[0].findall(xml_xwiki_enforcer_skip_tag)[0].text = "true"
    current_xml = root_pom.findall(xml_build_tag)[0].findall(xml_plugin_management_tag)[0].findall(xml_plugins_tag)[0]
    # include test code into the clover instrumentation
    for child in current_xml:
        group_id = child.findall(xml_group_id_tag)[0].text
        artifact_id = child.findall(xml_artifact_id_tag)[0].text
        if group_id == 'org.openclover' and artifact_id == 'clover-maven-plugin':
            if len(child.findall(xml_configuration_tag)) > 0:
                if len(child.findall(xml_configuration_tag)[0].findall(xml_includes_test_source_roots_tag)) > 0:
                    child.findall(xml_configuration_tag)[0].findall(xml_includes_test_source_roots_tag)[0].text = "true"
    tree.write(path_to_root_pom)

    # set in the xwiki-commons-tools pom to not skip clover in the submodule for the second version
    path_to_root_pom = toolbox.prefix_dataset + "/" + project + toolbox.suffix_parent + "/" + xwiki_commons_tools_path + "/pom.xml"
    tree = ET.parse(path_to_root_pom)
    root_pom = tree.getroot()
    if len(root_pom.findall(xml_properties_tag)) > 0:
        for i in root_pom.findall(xml_properties_tag)[0]:
            if i.tag == xml_xwiki_clover_skip_tag:
                i.text = "false"
    tree.write(path_to_root_pom)

def add_needed_options(cmd, project):
    if project == "xwiki-commons":
        cmd.append("-Duse-maven-to-exe-test=true")
    return cmd


if __name__ == '__main__':
    print "prepare", sys.argv[1]
    prepare(sys.argv[1])
