package me.tatarka.gradle.pom;


import groovy.util.Node;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.XmlProvider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

/**
 * Pom plugin "fixes" maven-publish plugin pom generation.
 * <p>
 * Plugin adds simplified pom configuration extension. Using pom closure in build new sections could be added
 * to resulted pom. If multiple maven publications configured, pom modification will be applied to all of them.
 *
 * @author Vyacheslav Rusakov
 * @since 04.11.2015
 */
public class PomPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // extensions mechanism not used because we need free closure for pom xml modification
        project.getConvention().getPlugins().put("pom", new PomConvention());
        activatePomModifications(project);
    }

    private void activatePomModifications(Project project) {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(final Project project) {
                PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
                // apply to all configured maven publications
                publishing.getPublications().withType(MavenPublication.class, new Action<MavenPublication>() {
                    @Override
                    public void execute(MavenPublication mavenPublication) {
                        mavenPublication.getPom().withXml(new Action<XmlProvider>() {
                            @Override
                            public void execute(XmlProvider xmlProvider) {
                                Node pomXml = xmlProvider.asNode();
                                applyUserPom(project, pomXml);
                            }
                        });
                    }
                });
            }
        });
    }

    private void applyUserPom(Project project, Node pomXml) {
        PomConvention pomExt = project.getConvention().getPlugin(PomConvention.class);
        if (pomExt.config != null) {
            XmlMerger.mergePom(pomXml, pomExt.config);
        }
        // apply defaults if required
        if (pomXml.get("name") == null) {
            pomXml.appendNode("name", project.getName());
        }
        if (project.getDescription() != null && pomXml.get("description") == null) {
            pomXml.appendNode("description", project.getDescription());
        }
    }
}
