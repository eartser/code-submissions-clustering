package org.jetbrains.research.code.submissions.clustering.load.distance.measurers

import com.github.gumtreediff.actions.EditScript
import com.github.gumtreediff.actions.EditScriptGenerator
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator
import com.github.gumtreediff.gen.TreeGenerator
import com.github.gumtreediff.matchers.MappingStore
import com.github.gumtreediff.matchers.Matcher
import com.github.gumtreediff.matchers.Matchers
import com.github.gumtreediff.tree.Tree
import org.jetbrains.research.code.submissions.clustering.load.CodeDistanceMeasurer

class GumTreeDistanceMeasurer(private val treeGenerator: TreeGenerator) : CodeDistanceMeasurer {
    private fun String.parseTree(treeGenerator: TreeGenerator) =
        treeGenerator.generateFrom().string(this)?.root ?: error("Can not parse code: $this")
    override fun computeDistance(code: String, otherCode: String): Int {
        val source = code.parseTree(treeGenerator)
        val destination = otherCode.parseTree(treeGenerator)
        val defaultMatcher: Matcher = Matchers.getInstance().matcher
        val mappings: MappingStore = defaultMatcher.match(source, destination)
        val editScriptGenerator: EditScriptGenerator = SimplifiedChawatheScriptGenerator()
        val actions: EditScript = editScriptGenerator.computeActions(mappings)
        return actions.size()
    }
}
