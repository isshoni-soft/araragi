package tv.isshoni.araragi.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import tv.isshoni.araragi.plugin.exception.MalformedTestingOpensException
import tv.isshoni.araragi.plugin.extension.AraragiExtension

class AraragiPlugin implements Plugin<Project> {

    private static createPatch(String module_name, Project project) {
        return ['--patch-module', "$module_name=${project.tasks.compileJava.destinationDirectory.asFile.get().path}"]
    }

    private static createOpens(String first, String second) {
        return ['--add-opens', "$first/$second=ALL-UNNAMED"]
    }

    @Override
    void apply(Project project) {
        def araragiExtension = project.extensions.create('araragi', AraragiExtension)
        def testExtension = araragiExtension.testingExtension

        project.tasks.named('test') {
            it.doFirst {
                for (String str : testExtension.patchModulesList) {
                    jvmArgs += createPatch(str, project)
                }

                LinkedList<String> opens = testExtension.opensList

                if (opens.size() % 2 != 0) {
                    throw new MalformedTestingOpensException(opens.size())
                }

                for (int x = 0; x < opens.size(); x += 2) {
                    String first = opens.get(x)
                    String second = opens.get(x + 1)

                    jvmArgs += createOpens(first, second)
                }
            }
        }

        project.tasks.withType(Test) {
            it.doFirst {
                testLogging {
                    if (testExtension.prettyBoolean) {
                        // set options for log level LIFECYCLE
                        events TestLogEvent.FAILED,
                                TestLogEvent.PASSED,
                                TestLogEvent.SKIPPED,
                                TestLogEvent.STANDARD_OUT
                        exceptionFormat TestExceptionFormat.FULL
                        showExceptions true
                        showCauses true
                        showStackTraces true

                        // set options for log level DEBUG and INFO
                        debug {
                            events TestLogEvent.STARTED,
                                    TestLogEvent.FAILED,
                                    TestLogEvent.PASSED,
                                    TestLogEvent.SKIPPED,
                                    TestLogEvent.STANDARD_ERROR,
                                    TestLogEvent.STANDARD_OUT
                            exceptionFormat TestExceptionFormat.FULL
                        }

                        info.events = debug.events
                        info.exceptionFormat = debug.exceptionFormat
                    }

                    afterSuite { desc, result ->
                        if (!desc.parent) { // will match the outermost suite
                            EnumMap<TestResult.ResultType, StyledTextOutput.Style> textColors = new EnumMap<>(TestResult.ResultType.class)
                            textColors.put(TestResult.ResultType.SUCCESS, StyledTextOutput.Style.Success)
                            textColors.put(TestResult.ResultType.FAILURE, StyledTextOutput.Style.Failure)
                            textColors.put(TestResult.ResultType.SKIPPED, StyledTextOutput.Style.Info)

                            EnumMap<TestResult.ResultType, StyledTextOutput.Style> borderColors = new EnumMap<>(TestResult.ResultType.class)
                            borderColors.put(TestResult.ResultType.SUCCESS, StyledTextOutput.Style.SuccessHeader)
                            borderColors.put(TestResult.ResultType.FAILURE, StyledTextOutput.Style.FailureHeader)
                            borderColors.put(TestResult.ResultType.SKIPPED, StyledTextOutput.Style.Header)

                            def out = project.services.get(StyledTextOutputFactory).create('araragi')

                            def outBefore = "Results - "
                            def outResult = "${result.resultType} "
                            def outNumTests = "(${result.testCount} tests: "
                            def outNumPassed = "${result.successfulTestCount} passed"
                            def outNumFailed = "${result.failedTestCount} failed"
                            def outNumSkipped = "${result.skippedTestCount} skipped"
                            def output = outBefore + outResult + outNumTests + outNumPassed + ", " + outNumFailed + ", " + outNumSkipped + ")"
                            def startItem = '|  ', endItem = '  |'
                            def repeatLength = startItem.length() + output.length() + endItem.length()

                            if (testExtension.prettyBoolean) {
                                out.style(borderColors.get(result.resultType)).text('\n' + ('-' * repeatLength) + '\n' + startItem)
                                        .style(StyledTextOutput.Style.Header).text(outBefore)
                                        .style(textColors.get(result.resultType)).text(outResult)
                                        .style(StyledTextOutput.Style.Header).text(outNumTests)
                                        .style(StyledTextOutput.Style.Success).text(outNumPassed)
                                        .style(StyledTextOutput.Style.Header).text(", ")
                                        .style(StyledTextOutput.Style.Failure).text(outNumFailed)
                                        .style(StyledTextOutput.Style.Header).text(", ")
                                        .style(StyledTextOutput.Style.Info).text(outNumSkipped)
                                        .style(StyledTextOutput.Style.Header).text(")")
                                        .style(borderColors.get(result.resultType)).println(endItem + '\n' + ('-' * repeatLength))
                            } else {
                                out.style(StyledTextOutput.Style.Header).text(outBefore)
                                        .style(textColors.get(result.resultType)).text(outResult)
                                        .style(StyledTextOutput.Style.Header).text(outNumTests)
                                        .style(StyledTextOutput.Style.Success).text(outNumPassed)
                                        .style(StyledTextOutput.Style.Header).text(", ")
                                        .style(StyledTextOutput.Style.Failure).text(outNumFailed)
                                        .style(StyledTextOutput.Style.Header).text(", ")
                                        .style(StyledTextOutput.Style.Info).text(outNumSkipped)
                                        .style(StyledTextOutput.Style.Header).println(")")
                            }
                        }
                    }
                }
            }
        }
    }
}
