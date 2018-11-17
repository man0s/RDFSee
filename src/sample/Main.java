package sample;

import com.sun.deploy.xml.XMLNode;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.File;
import java.io.InputStream;
import java.util.List;


public class Main extends Application {
    public File file = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RDF", "*.rdf"));


        controller.rdfButton.setOnAction((event) -> {
            configureFileChooser(fileChooser);
            file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Model model = FileManager.get().loadModel(file.toString());
                    controller.departmentChoice.valueProperty().set(null);
                    controller.departmentChoice.getItems().add("All");
                    controller.departmentChoice.getSelectionModel().selectFirst();

                    String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                            "PREFIX rdfs: <http://www.w3.org/2000/01/22-rdf-schema#>" +
                            "PREFIX uni: <http://www.university.fake/university#>" +
                            "SELECT distinct ?depname " +
                            "WHERE {" +
                            "{ ?person rdf:type <uni:Person> }" +
                            "UNION { ?person rdf:type <uni:Student> }" +
                            "UNION { ?person rdf:type <uni:Professor> }" +
                            "?person uni:member_of ?dep ." +
                            "?dep uni:dep_name ?depname }";

                    Query query = QueryFactory.create(queryString);
                    try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                        ResultSet result = qexec.execSelect();
                        for ( ; result.hasNext(); ) {
                            QuerySolution soln = result.nextSolution();
                            String depname = soln.getLiteral("depname").toString();
                            controller.departmentChoice.getItems().add(depname.toString());
                        }
                    }

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

        controller.departmentChoice.setOnAction((event) -> {
            controller.dataTable.getItems().clear();
            String choice = controller.departmentChoice.getValue().toString();
            try {
                    Model model = FileManager.get().loadModel(file.toString());
                    String queryString = null;
                    if(choice.equals("All")) {
                        queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                                "PREFIX rdfs: <http://www.w3.org/2000/01/22-rdf-schema#>" +
                                "PREFIX uni: <http://www.university.fake/university#>" +
                                "SELECT ?name ?age ?phone " +
                                "WHERE {" +
                                "{ ?person rdf:type <uni:Person> }" +
                                "UNION { ?person rdf:type <uni:Student> }" +
                                "UNION { ?person rdf:type <uni:Professor> }" +
                                "?person uni:has_name ?name ." +
                                "?person uni:has_age ?age ." +
                                "?person uni:has_phone ?phone }";
                    } else {
                        queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                                "PREFIX rdfs: <http://www.w3.org/2000/01/22-rdf-schema#>" +
                                "PREFIX uni: <http://www.university.fake/university#>" +
                                "SELECT ?name ?age ?phone " +
                                "WHERE {" +
                                "{ ?person rdf:type <uni:Person> }" +
                                "UNION { ?person rdf:type <uni:Student> }" +
                                "UNION { ?person rdf:type <uni:Professor> }" +
                                "?person uni:member_of ?dep ." +
                                "?dep uni:dep_name '" + choice + "' ." +
                                "?person uni:has_name ?name ." +
                                "?person uni:has_age ?age ." +
                                "?person uni:has_phone ?phone }";
                    }

                    Query query = QueryFactory.create(queryString);
                    try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                        ResultSet result = qexec.execSelect();
                        for ( ; result.hasNext(); ) {
                            QuerySolution soln = result.nextSolution();
                            Literal name = soln.getLiteral("name");
                            Literal age = soln.getLiteral("age");
                            Literal phone = soln.getLiteral("phone");

                            controller.setData(name.toString() , age.toString() , phone.toString() );
                        }
                    }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Day Filter Error");
                alert.setHeaderText("Something went wrong with the department filter..!");
                alert.showAndWait();
                ex.printStackTrace();
            }


        });


        //controller.setUriData("Panagiota Preza", "22", "2610987883");

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
