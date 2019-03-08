import json
import os
import toolbox

def must_split(path_to_json_file):
    with open(path_to_json_file) as json_data:
        data_dict = json.load(json_data)
        return data_dict['total_count'] > 30


if __name__ == '__main__':

    print 'numbers = ['

    for filename in os.listdir(toolbox.path_to_folder_with_json):
        if must_split(toolbox.path_to_folder_with_json + filename):
            splitted = toolbox.split_border(filename)
            new_border = str(int(splitted[2]) + ((int(splitted[3]) - int(splitted[2])) / 2))
            print '\t', '['
            print '\t', '\t', '\'' + splitted[2] + '\','
            print '\t', '\t', '\'' + new_border + '\','
            print '\t', '\t', '\'' + splitted[3] + '\''
            print '\t', '],'

    print ']'
