import sys

def size(path_to_diff):
    nb_insertion = 0
    nb_deletion = 0
    with open(path_to_diff, 'r') as diff:
        for line in diff:
            nb_insertion = nb_insertion + ( 1 if line.startswith('+') and not line.startswith('+++') else 0)
            nb_deletion = nb_deletion + ( 1 if line.startswith('-') and not line.startswith('---') else 0)
    return nb_insertion, nb_deletion