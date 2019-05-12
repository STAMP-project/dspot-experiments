import json
import sys

import toolbox

if __name__ == '__main__':

    toolbox.init(sys.argv)

    prefix_resources = "src/main/resources/"
    prefix_output = "results/march-2019/"

    full_qualified_name_main_class = "eu.stamp_project.prettifier.Main"

    path_selection = 'dataset/march-2019/selection/use-case-selection.json'

    path_jar_dspot_prettifier = sys.argv[1]
    project = sys.argv[2]
    path_code2vec = sys.argv[3]
    path_code2vec_model = sys.argv[4]

    with open(path_selection) as data_file:
        json_selection = json.load(data_file)

    for path in json_selection[project]['paths']:
        toolbox.output_log_path = prefix_output + project + "_" + path.split('/')[-1] + ".log"
        cmd = " ".join(["java", "-cp", path_jar_dspot_prettifier, full_qualified_name_main_class,
            "--path-to-properties", prefix_resources + project + ".properties",
            "--test-criterion","PitMutantScoreSelector",
            "--path-to-code2vec", path_code2vec,
            "--path-to-code2vec-model", path_code2vec_model,
            "--path-to-amplified-test-class", path,
        ])
        toolbox.print_and_call_in_a_file(cmd)

