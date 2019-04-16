import sys
import xml.etree.ElementTree as ET

xml_namespace = 'http://maven.apache.org/POM/4.0.0'
xml_prefix = '{' + xml_namespace + '}'
xml_dependencies_tag = xml_prefix + 'dependencies'
xml_dependency_tag = xml_prefix + 'dependency'
xml_groupId_tag = xml_prefix + 'groupId'
xml_artifactId_tag = xml_prefix + 'artifactId'
xml_version_tag = xml_prefix + 'version'

def enforce(path_to_pom):
    ET.register_namespace('', xml_namespace)
    # set in top pom enforcer skip to true
    tree = ET.parse(path_to_pom)
    root_pom = tree.getroot()
    dependencies = root_pom.findall(xml_dependencies_tag)[0]
    done = False
    for dependency in dependencies.findall(xml_dependency_tag):
        group_id = dependency.findall(xml_groupId_tag)[0]
        artifact_id = dependency.findall(xml_artifactId_tag)[0]
        if group_id == "junit" and artifact_id == "junit":
            dependency.findall(xml_version_tag).text = "4.12"
            done = True
            break

    if not done:
        new_dependency = ET.SubElement(dependencies, xml_dependency_tag)
        new_group_id = ET.SubElement(new_dependency, xml_groupId_tag)
        new_group_id.text = "junit"
        new_artifact_id = ET.SubElement(new_dependency, xml_artifactId_tag)
        new_artifact_id.text = "junit"
        new_version_id = ET.SubElement(new_dependency, xml_version_tag)
        new_version_id.text = "4.12"

    tree.write(path_to_pom)

if __name__ == '__main__':
    enforce(sys.argv[0])