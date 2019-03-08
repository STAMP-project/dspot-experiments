
import sys
import json
import subprocess

numbers = [

]

cmd = 'curl -o java_repositories_{}.json https://api.github.com/search/repositories\?q\=stars:{}+language:java -H "Authorization: token "'


def curl(parameter, prefix):
    final_cmd = cmd.format(parameter, prefix + parameter.replace('_', '..'))
    print final_cmd
    subprocess.call(final_cmd, shell=True)


def execute(parameter, prefix=''):
    output = []
    curl(parameter, prefix)
    with open('java_repositories_{}.json'.format(parameter)) as json_data:
        data_dict = json.load(json_data)
    for project in data_dict['items']:
        print project['full_name'], project['stargazers_count']
        output.append(project['full_name'])
    print len(output)
    return output

def run():
    global numbers
    for current in numbers:
        for i in range(0, 2):
            print execute(''.join([current[i], '_', current[i+1]]))

if __name__ == '__main__':
    is_number_2 = len(sys.argv) > 1 and sys.argv[1] == '2'
    run()