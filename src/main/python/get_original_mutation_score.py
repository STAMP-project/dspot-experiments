
if __name__ == '__main__':

    projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
            "protostuff", "logback", "retrofit"]
    prefix = '/home/bdanglot/workspace/dspot-experiments/original/january-2017/'

    for p in projects:
        with open(prefix + p + '/mutations.csv') as mutations_csv:
            content = mutations_csv.read().split('\n')
            score = 0
            nbMutant = 0
            for line in content:
                if 'KILLED' in line.split(','):
                    score = score + 1
                    nbMutant = nbMutant + 1
                elif 'SURVIVED' in line.split(','):
                    nbMutant = nbMutant + 1
            print p, "{0:.2f}".format(float(score) / float(nbMutant) * 100.0)


