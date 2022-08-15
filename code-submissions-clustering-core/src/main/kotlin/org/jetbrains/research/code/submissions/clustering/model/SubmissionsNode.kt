package org.jetbrains.research.code.submissions.clustering.model

typealias SubmissionNodeId = Int

/**
 * @property code submissions code
 * @property stepId submissions step id
 * @property idList list of submission ids corresponding to [code]
 * @property id
 */
data class SubmissionsNode(
    val id: SubmissionNodeID,
    val code: String,
    val stepId: Int,
    val idList: MutableSet<Int>
) {
    constructor(submission: Submission, id: SubmissionNodeID) : this(
        id,
        submission.code,
        submission.stepId,
        mutableSetOf(submission.id)
    )

    override fun equals(other: Any?): Boolean = other is SubmissionsNode && other.code == code

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "\nSubmissionsNode(code = \n$code, \nidList = ${idList.toList().sorted()})"
}
