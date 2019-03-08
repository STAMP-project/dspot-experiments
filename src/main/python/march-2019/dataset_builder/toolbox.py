
path_to_folder_with_json = 'dataset/march-2019/json/'
path_to_project_txt = 'dataset/march-2019/files/projects.txt'

def get_ith_border_from_filename(filename, i):
    return split_border(filename)[i+2]

def split_border(filename):
    return filename.split('.')[0].split('_')