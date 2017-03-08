package me.tatarka.gradle.pom;

import groovy.xml.XmlUtil

/**
 * @author Vyacheslav Rusakov
 * @since 28.07.2016
 */
class MergeTestHelper {

    static Node merge(String xml, Closure update) {
        Node base = new XmlParser().parseText(xml)
        XmlMerger.mergePom(base, update)
        base
    }

    static String xml(Node base) {
        def res = "\n" + XmlUtil.serialize(base).replaceAll("\r", "").replaceAll(" +\n", "\n")
        println res
        res
    }
}
