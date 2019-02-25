import sys
import requests

if __name__ == '__main__':

    ACCESS_TOKEN = sys.argv[1]
    deposition_id = sys.argv[2]

    r = requests.get('https://www.zenodo.org/api/deposit/depositions/' + deposition_id,
                      params={'access_token': ACCESS_TOKEN}, json={},
                      headers={"Content-Type": "application/json"})

    print r.status_code
    print r.json()

    bucket_url = r.json()['links']['bucket']
    path_file_name = sys.argv[3]
    filename = path_file_name.split("/")[-1]

    r = requests.put('%s/%s' % (bucket_url,filename),
                     data=open(path_file_name, 'rb'),
                     headers={"Accept":"application/json",
                              "Authorization":"Bearer %s" % ACCESS_TOKEN,
                              "Content-Type":"application/octet-stream"})

    print r.status_code
