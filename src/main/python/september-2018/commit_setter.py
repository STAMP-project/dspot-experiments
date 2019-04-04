import toolbox
import sys

def set_commit(path_to_project_root, project, index_commit):
    cmd = [
        "java",
        "-cp",
        toolbox.get_absolute_path(toolbox.path_to_jar),
        toolbox.full_qualified_name_repositories_setter,
        "--path-to-repository", path_to_project_root,
        "--project", project,
        "--folder-with-json", toolbox.prefix_current_dataset,
        "--commit-index", str(index_commit),
    ]
    toolbox.print_and_call(cmd)

if __name__ == '__main__':
    if len(sys.argv) < 4:
        print "usage <path-to-folder> <project> <index-commit>"
    else:
        set_commit(sys.argv[1], sys.argv[2], sys.argv[3])