package net.tanoshi.test.junit.runner;

import java.io.IOException;
import java.util.List;

import net.tanoshi.test.junit.IsExcuteDelegate;
import net.tanoshi.test.junit.JruRunner;
import net.tanoshi.test.junit.StatementProvider;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.agileinsider.concordion.junit.ConcordionStatementBuilder;
import org.agileinsider.concordion.junit.SpecificationFrameworkMethod;
import org.concordion.api.Resource;
import org.concordion.internal.ClassPathSource;
import org.concordion.internal.ParsingException;
import org.concordion.internal.XMLParser;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class JruConcordionPlusRunner implements JruRunner, IsExcuteDelegate {

    private FrameworkMethod specificationMethod;
    private Description specificationDescription;
    private ConcordionStatementBuilder statementBuilder;
    private TestClass testClass;

    @Override
    public void computeTestMethods(List<FrameworkMethod> testMethods) {
        testMethods.add(specificationMethod);
    }

    @Override
    public Description getDescription(FrameworkMethod method) {
        if (method == specificationMethod) {
            // TODO disgusting work around to make is show in concordion plus correctly.
            String dottedClassName = testClass.getJavaClass().getName();
            String slashedClassName = dottedClassName.replaceAll("\\.", "/");
            String specificationName = slashedClassName.replaceAll("(Fixture|Test)$", "");
            String resourcePath = "/" + specificationName + ".html";
            
            Resource resource = new Resource(resourcePath);
            XMLParser parser = new XMLParser();
            ClassPathSource source = new ClassPathSource();
            try {
                Document document = parser.parse(source.createInputStream(resource), String.format("[%s: %s]", source, resource.getPath()));
                XPathContext context = new XPathContext("cp", "http://www.agileinsider.org/concordion/plus");
                Nodes query = document.query("//@cp:scenario | //@cp:ignore", context);
                for(int i = 0; i < query.size(); i++) {
                    Attribute attribute = (Attribute) query.get(i);
                    if(attribute.getLocalName().equals("scenario")) {
                        specificationDescription.addChild(Description.createTestDescription(testClass.getJavaClass(), "- Scenario: " + attribute.getValue()));
                    } else {
                        // TODO why no ignore?
                        specificationDescription.addChild(Description.createTestDescription(testClass.getJavaClass(), "@Ignore: - Scenario: " + attribute.getValue()));
                    }
                }
            } catch (ParsingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // if we could return a nested description here...
            return specificationDescription;
        }
        return null;
    }

    @Override
    public void init(TestClass testClass) {
        this.testClass = testClass;
        Class<?> fixtureClass = testClass.getJavaClass();

        String testDescription = "Executable Specification: '" + fixtureClass.getSimpleName().replaceAll("Test$", "")
                + "'";
        specificationDescription = Description.createTestDescription(fixtureClass, testDescription);
        statementBuilder = new ConcordionStatementBuilder(fixtureClass);
        specificationMethod = new SpecificationFrameworkMethod(testDescription);
    }

    @Override
    public Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (method == specificationMethod) {
            return statementBuilder.withFixture(test).buildStatement();
        }

        return null;
    }

    @Override
    public void onAfter(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAfterMethod(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBefore(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBeforeMethod(Statement statement, Object testObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRun(RunNotifier notifier) {
    }

    @Override
    public boolean postRun(FrameworkMethod method, RunNotifier notifier) {
        return false;
    }

    @Override
    public boolean runTest(StatementProvider statementProvider, FrameworkMethod method, RunNotifier notifier) {
        if (method == specificationMethod) {
            statementBuilder.withRunNotifier(notifier);

            EachTestNotifier specificationNotifier = new EachTestNotifier(notifier, specificationDescription);
            specificationNotifier.fireTestStarted();
            try {
                statementProvider.get(method).evaluate();
            } catch (AssumptionViolatedException e) {
                specificationNotifier.addFailedAssumption(e);
            } catch (Throwable e) {
                specificationNotifier.addFailure(e);
            } finally {
                specificationNotifier.fireTestFinished();
            }
            return true;
        }
        return false;
    }

    @Override
    public void updateChildren(List<FrameworkMethod> child) {
    }
}
