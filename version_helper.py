import os
from datetime import datetime
import logging

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
It traverses the immediate sub dirs and executes get_local_repo_version()

"""
def run_in_all_dir():
    logging.debug('run_in_all_dir')

    for path in os.listdir("."):
        logging.debug('changing dir to:' + path)
        if os.path.isdir(path):
            get_local_repo_version("",path)
        else : 
            logging.debug(path + ' is not a dir.')

"""
Usage: python-c "import version_helper; version_helper.get_local_repo_version()"

Call this in the dir to get the git changes.
it saves to a new file named YYYY_MM_DD_HH_MM_test_software_version.txt

Throws exception in case of error.
"""

def get_local_repo_version(fname = "", path = "."):    
    logging.debug('get_local_repo_version called with fname: '+ fname + " in path:" + path)

    if fname:
        out_file_name = fname
    else:
        dateTimeObj = datetime.now()
        out_file_name = dateTimeObj.strftime("%Y_%m_%d_%H_%M")
        out_file_name += "_test_software_version.txt"

    if(path != "."):
        rc = os.chdir(path)
        if(rc != 0):
            errorstr = "Can't change to dir: " + path + ". Error code: " + str(rc)
            logging.error(errorstr)
            raise Exception(errorstr)
  
    cmd = 'echo std_out:  > '+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        errorstr = "echo std_out > file error " + str(rc)
        logging.error(errorstr)
        raise Exception(errorstr)

    cmd = 'git reflog -1 >> '+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        errorstr = "git reflog error. Error code: " + str(rc)
        logging.error(errorstr)
        raise Exception(errorstr)


    cmd = 'git status >> '+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        errorstr = "git status error. Error code: " + str(rc)
        logging.error(errorstr)
        raise Exception(errorstr)

    cmd = 'echo std_err:  >> '+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        errorstr = "echo std_out > file error " + str(rc)
        logging.error(errorstr)
        raise Exception(errorstr)


    cmd = "echo rc: "+ str(rc) +" >> "+out_file_name
    rc = os.system(cmd)
    if(rc != 0):
        errorstr = "echo std_out rc > file error " + str(rc)
        logging.error(errorstr)
        raise Exception(errorstr)
    
    logging.debug("get_local_repo_version completed. wrote to " + out_file_name)


"""
Usage: python-c "import version_helper; version_helper.get_local_repo_version_with_file(fileName)"

Call this in the dir to get the git changes.
It saves data to the fileName file provided as argument

Throws exception in case of error.
"""

def get_local_repo_version_with_file(fname = ''):
    logging.debug('get_local_repo_version_with_file called with fname:' + fname)

    if fname:
        get_local_repo_version(fname)
    else:
        print("Usage: get_local_repo_version_with_file('file_name')\n")
        raise Exception("Need filename as argument to save to.")

