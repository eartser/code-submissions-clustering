import logging
import subprocess
from pathlib import Path
from typing import List, Tuple


def run_in_subprocess(command: List[str], cwd: Path,
                      stdout_file=subprocess.PIPE,
                      stderr_file=subprocess.PIPE) -> Tuple[int, str, str]:
    process = subprocess.run(
        command,
        cwd=cwd,
        stdout=stdout_file,
        stderr=stderr_file,
    )

    stdout = process.stdout.decode('utf-8') if process.stdout else None
    stderr = process.stderr.decode('utf-8') if process.stderr else None

    if stdout:
        logging.debug("%s's stdout:\n%s" % (command[0], stdout))
    if stderr:
        logging.debug("%s's stderr:\n%s" % (command[0], stderr))

    return process.returncode, stdout, stderr
