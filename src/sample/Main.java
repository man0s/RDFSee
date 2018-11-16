package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.File;
import java.io.InputStream;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RDF", "*.rdf"));

        controller.rdfButton.setOnAction((event) -> {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    // create an empty model
                    Model model = ModelFactory.createDefaultModel();


                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("File Parsing Error");
                    alert.setHeaderText("Something went wrong with the parsing..!");
                    alert.showAndWait();
                    ex.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("File Selection Error");
                alert.setHeaderText("Please select a valid RDF file..!");
                alert.showAndWait();
            }
        });

        controller.setData("Emmanouil Katefidis", "21", "2610621332");
        controller.setUriData("Panagiota Preza", "22", "2610987883");

        primaryStage.setTitle("RDFSee");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Desktop")
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
