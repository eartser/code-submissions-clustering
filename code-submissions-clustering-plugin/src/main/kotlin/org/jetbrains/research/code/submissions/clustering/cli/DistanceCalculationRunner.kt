package org.jetbrains.research.code.submissions.clustering.cli

import com.xenomachina.argparser.ArgParser
import org.jetbrains.research.code.submissions.clustering.load.distance.calculateDistances
import org.jetbrains.research.code.submissions.clustering.util.toSubmissionsGraph
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

object DistanceCalculationRunner : AbstractGraphBuilder() {
    private lateinit var inputFilename: String

    override fun getCommandName(): String = "calculate-dist"

    @Suppress("TooGenericExceptionCaught")
    override fun main(args: MutableList<String>) {
        try {
            parseArgs(args, ::DistanceCalculationRunnerArgs).run {
                inputFilename = Paths.get(inputFile).toString()
            }
            val file = File(inputFilename)
            val context = buildGraphContext()
            val submissionsGraph = file.toSubmissionsGraph().calculateDistances(context)
            submissionsGraph.writeOutputData()
        } catch (ex: Throwable) {
            logger.severe { ex.toString() }
        } finally {
            exitProcess(0)
        }
    }

    data class DistanceCalculationRunnerArgs(private val parser: ArgParser) : AbstractGraphBuilderArgs(parser) {
        val inputFile by parser.storing(
            "-i", "--inputFile",
            help = "Input .bin file with serialized graph"
        )
    }
}
