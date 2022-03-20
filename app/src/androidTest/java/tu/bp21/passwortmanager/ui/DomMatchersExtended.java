package tu.bp21.passwortmanager.ui;

import androidx.test.espresso.remote.annotation.RemoteMsgConstructor;
import androidx.test.espresso.remote.annotation.RemoteMsgField;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class DomMatchersExtended {

    public static Matcher<Document> hasNoElementWithXpath(final String xpath) {
        return new HasElementWithXPathMatcher(xpath);
    }

    private static NodeList extractNodeListForXPath(String xpath, Document document) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile(xpath);
            return (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    static final class HasElementWithXPathMatcher extends TypeSafeMatcher<Document> {

        @RemoteMsgField(order = 0)
        private final String xpath;

        @RemoteMsgConstructor
        HasElementWithXPathMatcher(final String xpath) {
            this.xpath = xpath;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has element with xpath: " + xpath);
        }

        @Override
        public boolean matchesSafely(Document document) {
            NodeList nodeList = extractNodeListForXPath(xpath, document);
            return nodeList == null || nodeList.getLength() == 0;
        }
    }
}
