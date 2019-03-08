import json
import subprocess

cmd = 'curl -o java_repositories_{}.json https://api.github.com/search/repositories\?q\=stars:{}+language:java -H "Authorization: 90f16c3a42653bf99e89b57f3624ebad701fedc4%"'

numbers = [
    [
        '1325',
        '1337',
        '1350'
    ],
    [
        '2000',
        '2025',
        '2050'
    ],
    [
        '1175',
        '1187',
        '1200'
    ],
]


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


if __name__ == '__main__':
    global numbers
    for current in numbers:
        for i in range(0, len(current) - 1):
            print execute(''.join([current[i], '_', current[i + 1]]))
