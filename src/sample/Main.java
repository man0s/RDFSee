package sample;

import com.sun.deploy.xml.XMLNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.jena.base.Sys;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public class Main extends Application {
    public File file = null;
    public String depchoice = "All";

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        //initialization
        Logger.getRootLogger().setLevel(Level.OFF);
        controller.filterChoice.valueProperty().set(null);
        controller.filterChoice.getItems().add("Minimum Age");
        controller.filterChoice.getItems().add("Maximum Age");
        controller.filterChoice.getItems().add("Department City");
        controller.filterChoice.getSelectionModel().selectFirst();


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
            depchoice = controller.departmentChoice.getValue().toString();
            try {
                    Model model = FileManager.get().loadModel(file.toString());
                    String queryString = null;
                    if(depchoice.equals("All")) {
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
                                "?dep uni:dep_name '" + depchoice + "' ." +
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

        controller.searchButton.setOnAction((event) -> {
            controller.dataTable.getItems().clear();
            String choice = controller.filterChoice.getValue().toString();
            try {
                Model model = FileManager.get().loadModel(file.toString());
                String queryString = null;
                if(choice.equals("Minimum Age")) {
                    if(depchoice.equals("All")) {
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
                                "?person uni:has_phone ?phone ." +
                                " FILTER (?age >= '" + controller.searchQuery.getText() +"') }";
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
                                "?dep uni:dep_name '" + depchoice + "' ." +
                                "?person uni:has_name ?name ." +
                                "?person uni:has_age ?age ." +
                                "?person uni:has_phone ?phone ." +
                                " FILTER (?age >= '" + controller.searchQuery.getText() +"') }";
                    }
                } else if(choice.equals("Maximum Age")) {
                    if(depchoice.equals("All")) {
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
                                "?person uni:has_phone ?phone ." +
                                " FILTER (?age <= '" + controller.searchQuery.getText() +"') }";
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
                                "?dep uni:dep_name '" + depchoice + "' ." +
                                "?person uni:has_name ?name ." +
                                "?person uni:has_age ?age ." +
                                "?person uni:has_phone ?phone ." +
                                " FILTER (?age <= '" + controller.searchQuery.getText() +"') }";
                    }
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
                            "?dep uni:dep_city '" + controller.searchQuery.getText() + "' ." +
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
                alert.setTitle("Search Error");
                alert.setHeaderText("Something went wrong with the search..!");
                alert.showAndWait();
                ex.printStackTrace();
            }


        });

        controller.uriButton.setOnAction((event) -> {
            controller.triplesTable .getItems().clear();
            String personURI = controller.uriQuery.getText();
            try {
                Model model = FileManager.get().loadModel(file.toString());
                InfModel infmodel = ModelFactory.createRDFSModel(model);

                // list the statements in the Model
                StmtIterator iter = infmodel.listStatements();

                 //print out the predicate, subject and object of each statement
                while (iter.hasNext()) {
                    Statement stmt = iter.nextStatement();  // get next statement
                    Resource subject = stmt.getSubject();     // get the subject
                    Property predicate = stmt.getPredicate();   // get the predicate
                    RDFNode object = stmt.getObject();      // get the object
                    String subjectURI = "http://www.university.fake/university#" + stmt.getSubject().toString().substring(4);

                    if (subjectURI.equals(personURI)) {
                        controller.setUriData(subject.toString(), predicate.toString(), object.toString());
                    }

                }



            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Triples Error");
                alert.setHeaderText("Something went wrong with the triples..!");
                alert.showAndWait();
                ex.printStackTrace();
            }
        });

        controller.professorButton.setOnAction((event) -> {
            // Create the custom dialog.
            Dialog<Staff> dialog = new Dialog<>();
            dialog.setTitle("Add Professor");


            // Set the button types.
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            // Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(40, 170, 20, 20));

            TextField name = new TextField();
            name.setPromptText("Name");
            TextField phone = new TextField();
            phone.setPromptText("Phone");
            TextField age = new TextField();
            age.setPromptText("Age");
            ObservableList<String> options =
                    FXCollections.observableArrayList();
            ComboBox<String> comboBox = new ComboBox<>(options);

            grid.add(new Label("Name:"), 0, 0);
            grid.add(name, 1, 0);
            grid.add(new Label("Phone:"), 0, 1);
            grid.add(phone, 1, 1);
            grid.add(new Label("Age:"), 0, 2);
            grid.add(age, 1, 2);
            grid.add(new Label("Department:"), 0, 3);
            grid.add(comboBox, 1, 3);

            Model model = FileManager.get().loadModel(file.toString());

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
                for (; result.hasNext(); ) {
                    QuerySolution soln = result.nextSolution();
                    String depname = soln.getLiteral("depname").toString();
                    options.add(depname.toString());
                }
            }
            comboBox.getSelectionModel().selectFirst();


            dialog.getDialogPane().setContent(grid);
            BooleanBinding bb = new BooleanBinding() {
                {
                    super.bind(name.textProperty(),
                            phone.textProperty(),
                            age.textProperty());
                }

                @Override
                protected boolean computeValue() {
                    return (name.getText().isEmpty()
                            || phone.getText().isEmpty()
                            || age.getText().isEmpty());
                }
            };

            Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
            addButton.disableProperty().bind(bb); //disable button when inputs are empty

            // Request focus on the username field by default.
            Platform.runLater(() -> name.requestFocus());

            // when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    //do the queries
                    return new Staff(name.getText(), phone.getText(), age.getText(), comboBox.getValue().toString());
                }
                return null;
            });

            dialog.showAndWait();

        });

        controller.studentButton.setOnAction((event) -> {

        });

        controller.departmentButton.setOnAction((event) -> {

        });

        controller.lessonButton.setOnAction((event) -> {

        });

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

    private static class Staff {
        String name;
        String phone;
        String age;
        String department;

        public Staff(String name, String phone, String age, String department) {
            this.name = name;
            this.phone = phone;
            this.age = age;
            this.department = department;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
