package cowork.ui;

import cowork.buttons.CategoryColorMap;
import cowork.buttons.IconLoader;
import cowork.buttons.SuiteButton;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ButtonPanelTest {

    static { System.setProperty("java.awt.headless", "true"); }

    @Test
    void iconAndTextAreasDispatchTheButtonAndExposeItsContextMenu() throws Exception {
        SuiteButton suiteButton = new SuiteButton("Prepare Report", "Operations", "TASK_TEMPLATE");
        AtomicReference<SuiteButton> clicked = new AtomicReference<>();
        AtomicInteger clickCount = new AtomicInteger();

        SwingUtilities.invokeAndWait(() -> {
            ButtonPanel panel = new ButtonPanel(
                    new CategoryColorMap(), new IconLoader(), List.of(suiteButton));
            panel.setClickListener(new RecordingListener(clicked, clickCount));

            AbstractButton card = findButton(panel, suiteButton.getLabel());
            assertNotNull(card);
            card.setSize(220, 36);

            // The icon and text are both painted by the AbstractButton. Clicking
            // either painted region must therefore follow the same action path.
            click(card, 18, 18, MouseEvent.BUTTON1, false);
            assertSame(suiteButton, clicked.get());
            click(card, 80, 18, MouseEvent.BUTTON1, false);
            assertSame(suiteButton, clicked.get());
            assertEquals(2, clickCount.get());

            JComponent contentAtIcon = (JComponent) SwingUtilities.getDeepestComponentAt(card, 18, 18);
            assertSame(card, contentAtIcon);
            assertNotNull(contentAtIcon.getComponentPopupMenu());
            assertEquals("Edit", ((JMenuItem) contentAtIcon.getComponentPopupMenu().getComponent(0)).getText());
            assertEquals("Delete", ((JMenuItem) contentAtIcon.getComponentPopupMenu().getComponent(1)).getText());

            click(card, 18, 18, MouseEvent.BUTTON3, false);
            assertEquals(2, clickCount.get(), "right clicks must not invoke the primary action");
        });
    }

    private static void click(AbstractButton button, int x, int y, int mouseButton, boolean popupTrigger) {
        long now = System.currentTimeMillis();
        int modifiers = mouseButton == MouseEvent.BUTTON1
                ? MouseEvent.BUTTON1_DOWN_MASK : MouseEvent.BUTTON3_DOWN_MASK;
        button.dispatchEvent(new MouseEvent(button, MouseEvent.MOUSE_PRESSED, now,
                modifiers, x, y, 1, popupTrigger, mouseButton));
        button.dispatchEvent(new MouseEvent(button, MouseEvent.MOUSE_RELEASED, now + 1,
                0, x, y, 1, popupTrigger, mouseButton));
    }

    private static AbstractButton findButton(Container root, String text) {
        for (Component child : root.getComponents()) {
            if (child instanceof AbstractButton button && text.equals(button.getText())) return button;
            if (child instanceof Container container) {
                AbstractButton found = findButton(container, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private record RecordingListener(AtomicReference<SuiteButton> clicked, AtomicInteger clickCount)
            implements ButtonPanel.ButtonClickListener {
        @Override public void onButtonClicked(SuiteButton button) {
            clicked.set(button);
            clickCount.incrementAndGet();
        }
        @Override public void onCreateButton() { }
        @Override public void onEditButton(SuiteButton button) { }
        @Override public void onDeleteButton(SuiteButton button) { }
    }
}
