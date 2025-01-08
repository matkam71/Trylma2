module com.example.trylma2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.trylma2 to javafx.fxml;
    exports com.example.trylma2;
}