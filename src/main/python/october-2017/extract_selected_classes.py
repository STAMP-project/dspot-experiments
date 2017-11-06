import read_select_classes


def run(projects):
    for project in projects:
        top, worst, pr = read_select_classes.read([project])

        print "{}~{}\\\\{}~{}".format(top[0].split(".")[-1],
                                      top[1].split(".")[-1],
                                      worst[0].split(".")[-1],
                                      worst[1].split(".")[-1]
                                      )

if __name__ == '__main__':

    projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    run(projects=projects)
