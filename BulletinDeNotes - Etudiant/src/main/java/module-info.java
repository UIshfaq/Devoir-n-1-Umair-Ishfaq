module sio.gestionsubventions {
    requires javafx.controls;
    requires javafx.fxml;
    opens sio.bulletin to javafx.fxml;
    opens sio.bulletin.Model;
    exports sio.bulletin;
}