import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class UserLogin2 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login2.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image("file:/C:/Users/Kahan/OneDrive/VS Code/Project - SEM-II/Solo Project/src/Images/logo.png");
        stage.setX(900);
        stage.setY(150);
        stage.getIcons().add(icon);
        stage.setTitle("Connectify");
        stage.setScene(scene);
        stage.show();
    }

    public static void signalLoginSuccess() {
        UserLogin1.getLatch().countDown();
    }
}
