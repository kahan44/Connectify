import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;

public class UserLogin1 extends Application {
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        launch(args);
    }   

    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("login1.fxml")); 
        Scene scene = new Scene(root);
        Image icon = new Image("file:/C:/Users/Kahan/OneDrive/VS Code/Project - SEM-II/Solo Project/src/Images/logo.png");
        stage.setX(300);
        stage.setY(150);
        stage.getIcons().add(icon);
        stage.setTitle("Connectify");
        stage.setScene(scene);
        stage.show();
    }

    public static CountDownLatch getLatch() {
        return latch;
    }
}
