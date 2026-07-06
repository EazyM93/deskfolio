package io.deskfolio.ui.shell;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationShellSmokeTest {

    private static final String FXML_NAMESPACE = "http://javafx.com/fxml/1";

    @Test
    void shellResourcesAreAvailableOnClasspath() {
        assertThat(ShellController.class.getResource("shell.fxml")).isNotNull();
        assertThat(ApplicationShellSmokeTest.class.getResource("/io/deskfolio/ui/dashboard/dashboard.fxml")).isNotNull();
        assertThat(ApplicationShellSmokeTest.class.getResource("/io/deskfolio/ui/transaction/transactions.fxml")).isNotNull();
        assertThat(ApplicationShellSmokeTest.class.getResource("/io/deskfolio/theme/dark-theme.css")).isNotNull();
        assertThat(ApplicationShellSmokeTest.class.getResource("/logback.xml")).isNotNull();
    }

    @Test
    void shellProvidesSidebarToggleControl() throws Exception {
        Document document = loadShellDocument();

        assertThat(hasElementWithFxId(document, "VBox", "sidebar")).isTrue();
        assertThat(hasButton(document, "sidebarToggleButton", "#toggleSidebar")).isTrue();
    }

    private Document loadShellDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        try (InputStream inputStream = ShellController.class.getResourceAsStream("shell.fxml")) {
            assertThat(inputStream).isNotNull();
            return factory.newDocumentBuilder().parse(inputStream);
        }
    }

    private boolean hasElementWithFxId(Document document, String tagName, String fxId) {
        NodeList nodes = document.getElementsByTagName(tagName);
        for (int index = 0; index < nodes.getLength(); index++) {
            Element element = (Element) nodes.item(index);
            if (fxId.equals(element.getAttributeNS(FXML_NAMESPACE, "id"))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasButton(Document document, String fxId, String action) {
        NodeList nodes = document.getElementsByTagName("Button");
        for (int index = 0; index < nodes.getLength(); index++) {
            Element element = (Element) nodes.item(index);
            if (fxId.equals(element.getAttributeNS(FXML_NAMESPACE, "id"))
                    && action.equals(element.getAttribute("onAction"))) {
                return true;
            }
        }
        return false;
    }
}
