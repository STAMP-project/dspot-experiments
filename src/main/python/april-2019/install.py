import sys
import toolbox


def install(owner, project):
    toolbox.set_output_log_path(
        toolbox.prefix_result + project + "_install.log"
    )

    toolbox.print_and_call_in_a_file(
        " ".join(["git", "clone",
                  toolbox.prefix_url_github + owner + "/" + project, toolbox.prefix_dataset + project]
                 )
    )

    toolbox.print_and_call_in_a_file(
        " ".join(["git", "clone",
                  toolbox.prefix_url_github + owner + "/" + project,
                  toolbox.prefix_dataset + project + toolbox.suffix_parent]
                 )
    )

if __name__ == '__main__':
    owner = sys.argv[1]
    project = sys.argv[2]
    install(owner, project)
