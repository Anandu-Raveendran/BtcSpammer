import os
import subprocess
from subprocess import Popen
from datetime import datetime
import logging
from subprocess import Popen, PIPE, STDOUT

"""
Set below flag to True to enable debug logs
"""
ENABLE_DEBUG_LOGS = False


if(ENABLE_DEBUG_LOGS):
    logging.basicConfig(filename='version_helper.log',filemode='w', 
        format='%(name)s - %(levelname)s - %(message)s',
        level=logging.DEBUG
    )


"""
Usage: python-c "import version_helper; version_helper.run_in_all_dir()

Call this from the parent dir.
It traverses the immediate sub dirs and executes get_local_repo_version_with_file()

"""
def run_in_all_dir():
    logging.debug('run_in_all_dir')

    for path in os.listdir("."):
        logging.debug('changing dir to:' + path)
        if os.path.isdir(path):
            get_local_repo_version_with_file("",path)
        else : 
            logging.debug(path + ' is not a dir. Skipping...')
            

"""
Usage: python-c "import version_helper; version_helper.get_local_repo_version()"

Call this in the curr dir to get the git changes.

Throws exception in case of error.
"""

def get_local_repo_version(fname = ""):    
    logging.debug('get_local_repo_version called with fname: '+ fname)
  
    proc = Popen("git reflog -1", stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
    proc.wait()
    std_output1 = proc.stdout.read().decode()
    std_error1 = proc.stderr.read().decode()
    rc1 = proc.returncode

    if(rc1 != 0):
        errorstr = "git reflog error. Error code: " + str(rc1)
        logging.error(std_error1)
        raise Exception(errorstr)


    proc = Popen(["git", "status"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
    proc.wait()
    std_output2 = proc.stdout.read().decode()
    std_error2 = proc.stderr.read().decode()
    rc2 = proc.returncode

    if(rc2 != 0):
        errorstr = "git status error: " + str(rc2)
        logging.error(std_error2)
        raise Exception(errorstr)

    logging.debug("get_local_repo_version completed.")
    ret =  [
            (std_output1, std_error1, rc1),
            (std_output2, std_error2, rc2)
           ]
    return ret


"""
Usage: python-c "import version_helper; version_helper.get_local_repo_version_with_file(<fileName>, <path>)"
NOTE: both the arguments are default and not compulsory.

It saves the tuple from get_local_repo_version to 
a new file named YYYY_MM_DD_HH_MM_test_software_version.txt or
to the file name file provided as argument

Throws exception in case of error.
"""

def get_local_repo_version_with_file(fname = '', path = "."):
    
    if(path != "."):
        rc = os.chdir(path)
        if(rc != 0):
            errorstr = "Can't change to dir: " + path + ". Error code: " + str(rc)
            logging.error(errorstr)
            raise Exception(errorstr)

    if fname:
        out_file_name = fname
    else:
        dateTimeObj = datetime.now()
        out_file_name = dateTimeObj.strftime("%Y_%m_%d_%H_%M")
        out_file_name += "_test_software_version.txt"

    ret_list = get_local_repo_version(fname)

    logging.debug("Returned tuple " + str(ret_list))

    f = open(out_file_name, "a")
    cmd = "STD_OUT: " + str(ret_list[0][0]) + "\n"      \
            + str(ret_list[1][0]) + "\n"                \
                                                        \
            +"STD_ERR: " + str(ret_list[0][1]) + "\n"   \
            + str(ret_list[1][1]) + "\n"                \
                                                        \
            +"RC: " + str(ret_list[0][2]) + "\n"        \
            + str(ret_list[1][2]) + "\n"

    logging.debug("Writing " +cmd+ " to file: " + out_file_name)

    f.write(cmd)
    f.close()
    
