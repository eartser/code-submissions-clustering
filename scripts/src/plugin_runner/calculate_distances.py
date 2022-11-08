"""Distance calculation script.

This script allows user to modify already serialized submissions graph(s) stored in [input_path]
by calculating distances between every pair of submissions nodes and adding edges according to
calculated distances.
By default, script creates 1 file for every serialized graph:
* [output_path]/[step_id]/graph.txt containing string representation of modified submissions graph
Optional arguments:
* --serialize: additionally creates [output_path]/[step_id]/graph.bin containing new serialized
submissions graph
"""

import argparse

from src.plugin_runner.utils import configure_parser
from src.utils.file_utils import create_dir, list_dirs
from src.utils.logger_utils import set_logger
from src.utils.models.cli_arguments import ClusteringArguments
from src.utils.runners.calculate_dist_runner import CalculateDistRunner
from src.utils.steps_processing_utils import process_steps


def build_initial_graph_filename(step_id: int, args: ClusteringArguments) -> str:
    """
    Build .bin file name storing input serialized submissions graph for step step_id.

    :param step_id: step id
    :param args: script arguments
    :return: .bin file name
    """
    return f'{args.input_file}/{step_id}/graph.bin'


def build_output_dir_name(step_id: int, args: ClusteringArguments) -> str:
    """
    Build directory name to store submissions graph output files for step step_id.

    :param step_id: step id
    :param args: script arguments
    :return: built directory name
    """
    return f'{args.output_path}/{step_id}'


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument(
        'input_path',
        help='Input directory with folders containing serialized graphs',
    )
    configure_parser(parser)
    args = ClusteringArguments(parser.parse_args())

    create_dir(args.output_path)
    logger = set_logger(args.output_path)

    step_ids = [int(d) for d in list_dirs(args.input_file)]
    task_runner = CalculateDistRunner()

    process_steps(
        step_ids, task_runner, args, logger,
        build_initial_graph_filename=build_initial_graph_filename,
        build_output_dir_name=build_output_dir_name,
    )
