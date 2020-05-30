package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //thêm icon cho ứng dụng
        File file=new File("src/icons/logo.png");
        Image image=new Image(file.toURI().toString());

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Mp3 Player");
        primaryStage.getIcons().add(image);
        
        primaryStage.setScene(new Scene(root, 850, 510));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
