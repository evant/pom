package me.tatarka.gradle.pom;

import spock.lang.Specification

import static me.tatarka.gradle.pom.MergeTestHelper.*

/**
 * @author Vyacheslav Rusakov
 * @since 29.07.2016
 */
class MergeTest extends Specification {

    def "Check merge"() {

        setup:
        Node base = merge("""
    <project>
        <name>sample</name>
        <description>desc</description>
        <scm>
            <url>some</url>
        </scm>
    </project>
    """) {
            name "ttt"
            scm {
                test "tata"
            }
        }

        expect:
        xml(base) == """
<?xml version="1.0" encoding="UTF-8"?><project>
  <name>ttt</name>
  <description>desc</description>
  <scm>
    <url>some</url>
    <test>tata</test>
  </scm>
</project>
"""
    }
}
