import os
from datetime import datetime

def get_local_repo_version(fname = ""):    

    if fname:
        out_file_name = fname
    else:
        dateTimeObj = datetime.now()
        out_file_name = dateTimeObj.strftime("%Y_%m_%d_%H_%M")
        out_file_name += "_test_software_version.txt"

    cmd = 'echo std_out:  > '+out_file_name
    os.system(cmd)

    cmd = 'git reflog -1 >> '+out_file_name
    rc = os.system(cmd)
    cmd = 'git status >> '+out_file_name
    os.system(cmd)

    cmd = 'echo std_err:  >> '+out_file_name
    os.system(cmd)

    cmd = "echo rc: "+ str(rc) +" >> "+out_file_name
    os.system(cmd)

def get_local_repo_version_with_file(fname):

    if fname:
        get_local_repo_version(fname)
    else:
        get_local_repo_version()
