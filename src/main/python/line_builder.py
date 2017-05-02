import sys
import extract_rates_from_file

def getLine(rates):
    return str(rates[0]) + "&" + \
       str(rates[4]) + " " + '{:.2%}'.format(float(rates[4]) / float(rates[0])).replace("%", "\\%") + "&" + \
       str(rates[1]) + "&" + \
       str(rates[2]) + " " + '{:.2%}'.format(float(rates[2]) / float(rates[1])).replace("%", "\\%") + "&" + \
       str(rates[3]) + " " + '{:.2%}'.format(float(rates[3]) / float(rates[1])).replace("%", "\\%") + "&" + \
       '{:.2%}'.format(float(rates[3]) / float(rates[2])).replace("%", "\\%")

if __name__ == '__main__':
    print getLine(extract_rates_from_file.extract(sys.argv[1]))