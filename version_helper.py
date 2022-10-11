import os
from typing import Tuple, List
from datetime import datetime
from subprocess import Popen, PIPE


class GitVersionChecker():
    def __run_cmd(self, cmd: str) -> Tuple[str, str, int]:
        """
        This is a helper function which executed commands passed to it.
        This function is called only by the other functions in the file.

        Args:
            cmd (str): The command to be executed on the shell.
        Returns:
            typle( std_out(str), std_err(str), rc(int) ):
                returns a tuple of std out, std err and return code(rc)
        """

        with Popen(cmd, stdout=PIPE, stderr=PIPE, shell=True) as proc:
            proc.wait(timeout=60)
            std_output = proc.stdout.read().decode()
            std_error = proc.stderr.read().decode()
            rc = proc.returncode

            return (std_output, std_error, rc)

    def get_local_repo_version(self) -> List[Tuple[str, str, int]]:
        """
        Gets the git rev information, modification check and release check.
        It executes the commands git reflog -1, git status and git describe
        and returns the result as a list of tuples.

        Returns:
            list[tuple[str, str, int]]:
            Returns the results of the commands:-
            git reflog at list[0],
            git status at list[1] and
            git describe at list[2].
        """

        ret1 = self.__run_cmd("git reflog -1")
        ret2 = self.__run_cmd("git status")
        ret3 = self.__run_cmd("git describe --exact-match")

        ret = [ret1, ret2, ret3]
        return ret

    def get_local_repo_version_with_file(
        self,
        fname: str = "",
        repo_path: str = ""
    ) -> None:

        """
        It saves the tuples returned from get_local_repo_version() to
        a new default file named YYYY_MM_DD_HH_MM_test_software_version.txt or
        to the file name file provided as a function argument(repo_path)

        args:
            fname(str): the file path to save to instead of default file.
            repo_path(str): the path where the git commands should execute.
            By default it executes two folder above the current dir.
            Usually repo_path should be root of the git project.

        """

        if (repo_path == ""):
            repo_path = os.path.abspath(os.path.join(__file__, "../../.."))
        if (repo_path != "."):
            os.chdir(repo_path)

        if fname:
            out_file_name = fname
        else:
            dateTimeObj = datetime.now()
            out_file_name = dateTimeObj.strftime("%Y_%m_%d_%H_%M")
            out_file_name += "_test_software_version.txt"

        ret_list = self.get_local_repo_version()

        cmd = "Rev information\n"                                       \
            + "STD_OUT: [" + str(ret_list[0][0]) + "]\n"                \
            + "STD_ERR: [" + str(ret_list[0][1]) + "]\n"                \
            + "RC: [" + str(ret_list[0][2]) + "]\n"                     \
                                                                            \
            + "\nModification check\n"                                  \
            + "STD_OUT: [" + str(ret_list[1][0]) + "]\n"                \
            + "STD_ERR: [" + str(ret_list[1][1]) + "]\n"                \
            + "RC: [" + str(ret_list[1][2]) + "]\n"                     \
                                                                            \
            + "\nRelease check\n"                                       \
            + "STD_OUT: [" + str(ret_list[2][0]) + "]\n"                \
            + "STD_ERR: [" + str(ret_list[2][1]) + "]\n"                \
            + "RC: [" + str(ret_list[2][2]) + "]\n"

        with open(out_file_name, "a") as f:
            f.write(cmd)
            f.close()


gvc = GitVersionChecker()
gvc.get_local_repo_version_with_file()
