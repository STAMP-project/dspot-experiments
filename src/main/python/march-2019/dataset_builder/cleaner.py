import os
import toolbox

def sorting_function(filename):
    return int(
        int(toolbox.get_ith_border_from_filename(filename, 0)) +
        int((toolbox.get_ith_border_from_filename(filename, 1) if filename.count('_') > 2 else 0))
    )

if __name__ == '__main__':
    filenames = os.listdir(toolbox.path_to_folder_with_json)
    list.sort(filenames, key=sorting_function)
    for i in range(0, len(filenames) - 1):
        border_1 = toolbox.get_ith_border_from_filename(filenames[i], 0)
        border_2 = toolbox.get_ith_border_from_filename(filenames[i+1], 0)
        if border_1 == border_2:
            print 'deleting {} because {} exists'.format(filenames[i+1], filenames[i])
            os.remove(toolbox.path_to_folder_with_json + filenames[i+1])