import os
from datetime import datetime

def run_in_all_dir():
    
    for path in os.listdir("."):
        print(path)
        if os.path.isdir(path):
            get_local_repo_version("",path)

def get_local_repo_version(fname = "", path = "."):    

    if fname:
        out_file_name = fname
    else:
        dateTimeObj = datetime.now()
        out_file_name = dateTimeObj.strftime("%Y_%m_%d_%H_%M")
        out_file_name += "_test_software_version.txt"

    if(path != "."):
        rc = os.chdir(path)
        if(rc != 0):
            raise Exception("Can't change to dir: " + path + ". Error code: " + str(rc))
  
    cmd = 'echo std_out:  > '+out_file_name
    os.system(cmd)

    cmd = 'git reflog -1 >> '+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        raise Exception("git reflog error. Error code: " + str(rc))

    cmd = 'git status >> '+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        raise Exception("git status error. Error code: " + str(rc))

    cmd = 'echo std_err:  >> '+out_file_name
    os.system(cmd)

    cmd = "echo rc: "+ str(rc) +" >> "+out_file_name
    os.system(cmd)

def get_local_repo_version_with_file(fname = ''):

    if fname:
        get_local_repo_version(fname)
    else:
        print("Usage: get_local_repo_version_with_file('file_name')\n")
        raise Exception("Need filename as argument to save to.")
