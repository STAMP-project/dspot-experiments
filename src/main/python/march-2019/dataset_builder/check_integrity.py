import os
import toolbox
import cleaner

if __name__ == '__main__':
    filenames = os.listdir(toolbox.path_to_folder_with_json)
    list.sort(filenames, key=cleaner.sorting_function)
    border_2 = toolbox.get_ith_border_from_filename(filenames[0], 1)
    for i in range(1, len(filenames) - 1):
        border_1 = toolbox.get_ith_border_from_filename(filenames[i], 0)
        if border_2 != border_1:
            print 'integrity comprised for {}'.format(filenames[i])
        border_2 = toolbox.get_ith_border_from_filename(filenames[i+1], 0)