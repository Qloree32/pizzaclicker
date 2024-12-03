module pizzaclicker {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires java.base;
    requires java.desktop;

    opens pizzaclicker to javafx.fxml;
    exports pizzaclicker;
}
