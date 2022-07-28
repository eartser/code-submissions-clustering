import org.jetbrains.intellij.tasks.RunIdeTask

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":code-submissions-clustering-core"))
    // Need for CLI
    implementation(libs.kotlin.argparser)
    implementation(libs.dataframe)
    // Need for tests
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.console)
}

open class BaseCLITask : RunIdeTask() {
    init {
        jvmArgs = listOf("-Djava.awt.headless=true", "--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
        standardInput = System.`in`
        standardOutput = System.`out`
    }
}

tasks {
    register("load", BaseCLITask::class) {
        dependsOn(build)
        val input: String? by project
        val output: String? by project
        args = listOfNotNull(
            "load",
            input?.let { "--input_file=$it" },
            output?.let { "--output_path=$it" }
        )
    }
    test {
        useJUnitPlatform()
    }
}
