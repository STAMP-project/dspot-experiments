import os
import json
import toolbox

def run(path_to_json_file):
    with open(path_to_json_file) as json_data:
        data_dict = json.load(json_data)
        return [item['full_name'] for item in data_dict['items']]

if __name__ == '__main__':

    with open(toolbox.path_to_project_txt, 'w') as file_out:
        for filename in os.listdir(toolbox.path_to_folder_with_json):
            output = run(toolbox.path_to_folder_with_json + filename)
            file_out.write('\n'.join(output))