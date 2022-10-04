import os
import subprocess
from subprocess import Popen
from datetime import datetime
from subprocess import Popen, PIPE, STDOUT

"""
Usage: python-c "import version_helper; version_helper.get_local_repo_version()"

Call this in the curr dir to get the git changes.

Throws exception in case of error.
"""

def get_local_repo_version():      

    ret1 = run_cmd("git reflog -1")
    ret2 = run_cmd("git status")

    ret =  [ ret1, ret2 ]
    return ret

def run_cmd(cmd:str):
    with Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True) as proc:
        proc.wait(timeout=60)
        std_output = proc.stdout.read().decode()
        std_error = proc.stderr.read().decode()
        rc = proc.returncode

        if(rc != 0):
            errorstr = "git status error: " + str(rc)
            raise Exception(errorstr)
        return (std_output, std_error, rc)
    


"""
Usage: python-c "import version_helper; version_helper.get_local_repo_version_with_file(<file_name>, <repo_path>)"
NOTE: both the arguments are default and not compulsory.

It saves the tuple from get_local_repo_version to 
a new file named YYYY_MM_DD_HH_MM_test_software_version.txt or
to the file name file provided as argument

Throws exception in case of error.
"""

def get_local_repo_version_with_file(fname:str = "", repo_path:str = ""):
    
    if(repo_path == ""):
        repo_path = os.path.abspath(os.path.join(__file__ ,"../.."))
    
    if(repo_path != "."):
        os.chdir(repo_path)

    if fname:
        out_file_name = fname
    else:
        dateTimeObj = datetime.now()
        out_file_name = dateTimeObj.strftime("%Y_%m_%d_%H_%M")
        out_file_name += "_test_software_version.txt"

    ret_list = get_local_repo_version()

    cmd = "STD_OUT: " + str(ret_list[0][0]) + "\n"      \
            + str(ret_list[1][0]) + "\n"                \
                                                        \
            +"STD_ERR: " + str(ret_list[0][1]) + "\n"   \
            + str(ret_list[1][1]) + "\n"                \
                                                        \
            +"RC: " + str(ret_list[0][2]) + "\n"        \
            + str(ret_list[1][2]) + "\n"

    with open(out_file_name, "a") as f:
        f.write(cmd)
        f.close()
    
