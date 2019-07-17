import extract_table_iterations
import extract_table_seeds
import toolbox

def convert_time(time):
    time_in_second = time / 1000
    if time_in_second > 120:
        time_in_minute = float(time_in_second) / 60.0
        if time_in_minute > 120:
            time_in_hours = float(time_in_minute) / 60.0
            if time_in_hours > 48:
                time_in_days = time_in_hours / 24.0
                return "{0:.1f}".format(time_in_days) + 'd'
            else:
                return "{0:.1f}".format(time_in_hours) + 'h'
        else:
            return "{0:.1f}".format(time_in_minute) + 'm'
    else:
        return "{0:.1f}".format(time_in_second) + 's'


time = extract_table_iterations.build_table(projects=toolbox.projects) + extract_table_seeds.build_table(projects=toolbox.projects)

print time
print convert_time(time)

