package application.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import application.gui.GUIController;
import javafx.scene.control.MenuItem;

public class LanguageMenuItem extends MenuItem {
    private Locale         locale;
    private ResourceBundle bundle;
    private GUIController  controller;

    public LanguageMenuItem(Locale locale, GUIController controller) {
        super();
        if (locale == null)
            throw new IllegalArgumentException("locale cannot be null");
        if (controller == null)
            throw new IllegalArgumentException("controller cannot be null");
        this.locale = locale;
        this.controller = controller;
        bundle = ResourceBundle.getBundle("resources.i18n.Locale", this.locale);
        setText(bundle.getString("language.name"));
        setOnAction((event) -> {
            this.controller.setResourceBundle(bundle);
        });
    }
}
