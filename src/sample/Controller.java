package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {
    @FXML
    public MenuItem rdfButton;
    @FXML
    public MenuItem professorButton;
    @FXML
    public MenuItem studentButton;
    @FXML
    public MenuItem departmentButton;
    @FXML
    public MenuItem lessonButton;
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

    private final ObservableList<Data> data = FXCollections.observableArrayList();
    private final ObservableList<URIData> uridata = FXCollections.observableArrayList();

    public void setData(String name, String age, String phone) {
        data.add(new Data(name, age, phone));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Data,String>("Name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Data,String>("Age"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Data,String>("Phone"));
        nameColumn.setStyle("-fx-alignment: CENTER;");
        ageColumn.setStyle("-fx-alignment: CENTER;");
        phoneColumn.setStyle("-fx-alignment: CENTER;");
        dataTable.setItems(data);
    }

    public void setUriData(String subject, String predicate, String object) {
        uridata.add(new URIData(subject, predicate, object));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<URIData,String>("Subject"));
        predicateColumn.setCellValueFactory(new PropertyValueFactory<URIData,String>("Predicate"));
        objectColumn.setCellValueFactory(new PropertyValueFactory<URIData,String>("Object"));
        subjectColumn.setStyle("-fx-alignment: CENTER;");
        predicateColumn.setStyle("-fx-alignment: CENTER;");
        objectColumn.setStyle("-fx-alignment: CENTER;");
        triplesTable.setItems(uridata);
    }


    public static class Data {
        private final SimpleStringProperty Name;
        private final SimpleStringProperty Age;
        private final SimpleStringProperty Phone;

        private Data(String name, String age, String phone) {
            this.Name = new SimpleStringProperty(name);
            this.Age = new SimpleStringProperty(age);
            this.Phone = new SimpleStringProperty(phone);
        }

        public String getName() { return Name.get(); }
        public void setName(String name) { Name.set(name); }
        public String getAge() { return Age.get(); }
        public void setAge(String age) { Age.set(age); }
        public String getPhone() { return Phone.get(); }
        public void setPhone(String phone) { Phone.set(phone); }
    }

    public static class URIData {
        private final SimpleStringProperty Subject;
        private final SimpleStringProperty Predicate;
        private final SimpleStringProperty Object;

        private URIData(String subject, String predicate, String object) {
            this.Subject = new SimpleStringProperty(subject);
            this.Predicate = new SimpleStringProperty(predicate);
            this.Object = new SimpleStringProperty(object);
        }

        public String getSubject() { return Subject.get(); }
        public void setSubject(String subject) { Subject.set(subject); }
        public String getPredicate() { return Predicate.get(); }
        public void setPredicate(String predicate) { Predicate.set(predicate); }
        public String getObject() { return Object.get(); }
        public void setObject(String object) { Object.set(object); }
    }



}
