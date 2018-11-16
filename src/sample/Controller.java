package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {
    @FXML
    public Button rdfButton;
    @FXML
    public Button professorButton;
    @FXML
    public Button studentButton;
    @FXML
    public Button departmentButton;
    @FXML
    public Button lessonButton;
    @FXML
    public Button aboutButton;
    @FXML
    public TableView dataTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn ageColumn;
    @FXML
    private TableColumn phoneColumn;
    @FXML
    public ChoiceBox filterChoice;
    @FXML
    public TextField searchQuery;
    @FXML
    public Button searchButton;
    @FXML
    public ChoiceBox departmentChoice;
    @FXML
    public TableView triplesTable;
    @FXML
    private TableColumn subjectColumn;
    @FXML
    private TableColumn predicateColumn;
    @FXML
    private TableColumn objectColumn;
    @FXML
    public TextField uriQuery;
    @FXML
    public Button uriButton;



}
