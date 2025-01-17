package org.jetbrains.research.code.submissions.clustering.cli

import com.intellij.openapi.application.ApplicationStarter
import com.xenomachina.argparser.ArgParser
import org.jetbrains.research.code.submissions.clustering.cli.models.AbstractGraphBuilderArgs
import org.jetbrains.research.code.submissions.clustering.cli.models.Writer
import org.jetbrains.research.code.submissions.clustering.load.context.builder.gumtree.GumTreeGraphContextBuilder
import org.jetbrains.research.code.submissions.clustering.model.Language
import org.jetbrains.research.code.submissions.clustering.model.SubmissionsGraph
import org.jetbrains.research.code.submissions.clustering.util.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger
import kotlin.system.exitProcess

abstract class AbstractGraphBuilder : ApplicationStarter {
    protected val logger: Logger = Logger.getLogger(javaClass.name)
    private var toBinary: Boolean = false
    private var toCSV: Boolean = false
    private var toPNG: Boolean = false
    private var clustersToTxt = false
    private var clusteringRes = false
    protected var binInput: Path? = null
    private lateinit var lang: Language
    private lateinit var outputPath: String

    private fun getWriters() = listOf(
        Writer(SubmissionsGraph::writeToTxt, true),
        Writer(SubmissionsGraph::writeToBinary, toBinary),
        Writer(SubmissionsGraph::writeToCsv, toCSV),
        Writer(SubmissionsGraph::writeToPng, toPNG),
        Writer(SubmissionsGraph::writeClustersToTxt, clustersToTxt),
        Writer(SubmissionsGraph::writeClusteringResult, clusteringRes),
    )

    protected fun <T : AbstractGraphBuilderArgs> parseArgs(
        args: MutableList<String>,
        argsClassConstructor: (ArgParser) -> T
    ): T {
        val parser = ArgParser(args.drop(1).toTypedArray())
        return parser.parseInto(argsClassConstructor).apply {
            lang = Language.valueOf(Paths.get(language).toString())
            outputPath = Paths.get(outputDir).toString()
            binInput = binaryInput?.let { Paths.get(it) }
            toBinary = serializeGraph
            toCSV = saveCSV
            toPNG = visualize
            clustersToTxt = saveClusters
            clusteringRes = clusteringResult
        }
    }

    protected fun buildGraphContext() = GumTreeGraphContextBuilder()
        .setLanguage(lang)
        .buildContext()

    protected fun SubmissionsGraph.writeOutputData() {
        createFolder(outputPath)
        getWriters().filter { it.toWrite }.forEach { tryToWrite(it.writer) }
    }

    @Suppress("TooGenericExceptionCaught")
    protected fun startRunner(args: MutableList<String>, run: (MutableList<String>) -> Unit) {
        try {
            run(args)
        } catch (ex: Throwable) {
            logger.severe { ex.stackTraceToString() }
            exitProcess(1)
        } finally {
            exitProcess(0)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun SubmissionsGraph.tryToWrite(write: SubmissionsGraph.(String) -> Unit) {
        try {
            write(outputPath)
        } catch (ex: Throwable) {
            logger.severe { "Writing failed: $ex" }
        }
    }
}
