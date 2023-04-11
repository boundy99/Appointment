package Controller;

import com.example.appointmentprogram.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * @author Abdoulaye Boundy Djikine
 *The ModifyAppointment class is a controller class for the appointment modification view.
 *It allows the user to modify the details of an existing appointment, including its ID, title,
 *description, location, type, start and end times, customer ID, user ID, and contact ID.
 */
public class ModifyAppointment {
    /**
     * Button to cancel modifying an appointment.
     */
    @FXML
    private Button appointmentCancelButton;

    /**
     * ComboBox for selecting the contact for an appointment.
     */
    @FXML
    private ComboBox<String> appointmentContact;

    /**
     * TextField to input the description of an appointment.
     */
    @FXML
    private TextField appointmentDescription;

    /**
     * DatePicker for selecting the end date of an appointment.
     */
    @FXML
    private DatePicker appointmentEndDate;

    /**
     * ComboBox for selecting the end time of an appointment.
     */
    @FXML
    private ComboBox<LocalTime> appointmentEndTime;

    /**
     * TextField to input the ID of an appointment.
     */
    @FXML
    private TextField appointmentId;

    /**
     * TextField to input the location of an appointment.
     */
    @FXML
    private TextField appointmentLocation;

    /**
     * Button to save changes made to an appointment.
     */
    @FXML
    private Button appointmentSaveButton;

    /**
     * DatePicker for selecting the start date of an appointment.
     */
    @FXML
    private DatePicker appointmentStartDate;

    /**
     * ComboBox for selecting the start time of an appointment.
     */
    @FXML
    private ComboBox<LocalTime> appointmentStartTime;

    /**
     * TextField to input the title of an appointment.
     */
    @FXML
    private TextField appointmentTitle;

    /**
     * TextField to input the type of an appointment.
     */
    @FXML
    private TextField appointmentType;

    /**
     * ComboBox for selecting the customer ID associated with an appointment.
     */
    @FXML
    private ComboBox<Integer> customerId;

    /**
     * ComboBox for selecting the user ID associated with an appointment.
     */
    @FXML
    private ComboBox<Integer> userId;

    /**
     * The appointment to be modified.
     */
    private static Appointment select;

    /**
     * Method to handle the cancel button being clicked.
     * @param event The event associated with the button click.
     * @throws IOException if there is an error loading the associated FXML file.
     */
    @FXML
    void cancelOnClick(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../com/example/appointmentprogram/add-customer.fxml")));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Method to handle the save button being clicked.
     * @param event The event associated with the button click.
     * @throws SQLException if there is an error with the SQL query.
     * @throws IOException  if there is an error loading the associated FXML file.
     */
    @FXML
    void saveOnClick(ActionEvent event) throws SQLException, IOException {
        if(allFieldsFilled()){
            LocalTime convertedStartTime = TimeConversions.convertToEasternTime(appointmentStartDate.getValue(), appointmentStartTime.getValue());
            LocalTime convertedEndTime = TimeConversions.convertToEasternTime(appointmentEndDate.getValue(), appointmentEndTime.getValue());
            if(convertedStartTime.getHour() < 8|| convertedStartTime.getHour() > 22 || convertedEndTime.getHour() < 8 || convertedEndTime.getHour() > 22 || ((convertedStartTime.getHour() == 22) && convertedStartTime.getMinute() > 0) || ((convertedEndTime.getHour() == 22) && convertedEndTime.getMinute() > 0)){
                alert(1);
                return;
            }
            if(!appointmentStartDate.getValue().isEqual(appointmentEndDate.getValue())){
                alert(2);
                return;
            }
            if(appointmentStartDate.getValue().isBefore(LocalDate.now())){
                alert(8);
                return;
            }
            if(appointmentStartDate.getValue().equals(LocalDate.now()) && appointmentStartTime.getValue().isBefore(LocalTime.now())){
                alert(7);
                return;
            }
            if(appointmentStartDate.getValue().isAfter(appointmentEndDate.getValue())){
                alert(3);
                return;
            }
            if(appointmentStartTime.getValue().isAfter(appointmentEndTime.getValue())){
                alert(4);
                return;
            }
            if(appointmentStartTime.getValue().equals(appointmentEndTime.getValue())){
                alert(5);
                return;
            }
            LocalDateTime startDateTime = LocalDateTime.of(appointmentStartDate.getValue(), appointmentStartTime.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(appointmentEndDate.getValue(), appointmentEndTime.getValue());
            if(appointmentOverlap()){
                alert(6);
                return;
            }
            Connection conn = DBAccess.getConnection();
            DBAccess.setPreparedStatement(conn, "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?");
            PreparedStatement preparedStatement = DBAccess.getPreparedStatement();
            preparedStatement.setInt(1, Integer.parseInt(appointmentId.getText()));
            preparedStatement.setString(2, appointmentTitle.getText());
            preparedStatement.setString(3, appointmentDescription.getText());
            preparedStatement.setString(4, appointmentLocation.getText());
            preparedStatement.setString(5, appointmentType.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(endDateTime));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(9, userId.getValue());
            preparedStatement.setInt(10, customerId.getValue());
            preparedStatement.setInt(11, userId.getValue());
            preparedStatement.setInt(12, IDNameConversions.convertContactNameToID(appointmentContact.getValue()));
            preparedStatement.setInt(13, Integer.parseInt(appointmentId.getText()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../com/example/appointmentprogram/main.fxml")));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("The appointment with ID: " + appointmentId.getText() + " was successfully updated");
            alert.showAndWait();
            return;

        }
        else {
            alert(9);
        }
    }
    /**
     * Shows an alert message based on the input integer.
     * @param input integer that determines which alert message to show
     */
    public void alert(int input){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch (input) {
            case 1 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Selected time is outside of business operations!");
                alert.setContentText("Business operation times are from 8am-10pm EST");
                alert.showAndWait();
            }
            case 2 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start/end date!");
                alert.setContentText("Appointments must start and end in the same day due to business operation constraints");
                alert.showAndWait();
            }
            case 3 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("An appointment cannot have a start date after an end date");
                alert.showAndWait();
            }
            case 4 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have a start time after an end time");
                alert.showAndWait();
            }
            case 5 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have the same start time and end time");
                alert.showAndWait();
            }
            case 6 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Appointment overlap!");
                alert.setContentText("This appointment overlaps with an existing appointment");
                alert.showAndWait();
            }
            case 7 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have a start time before the current time");
                alert.showAndWait();
            }
            case 8 -> {
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("An appointment cannot have a start date before the current date");
                alert.showAndWait();
            }
            case 9 -> {
                alert.setTitle("Error");
                alert.setHeaderText("One or more fields empty!");
                alert.setContentText("Please fill out all required fields");
                alert.showAndWait();
            }
        }
    }
    /**
     * Checks if all required fields are filled out.
     * @return true if all required fields are filled out, false otherwise
     */
    public Boolean allFieldsFilled(){
        if(!appointmentTitle.getText().isEmpty() && !appointmentType.getText().isEmpty() && !appointmentDescription.getText().isEmpty() && !appointmentLocation.getText().isEmpty() && appointmentStartDate.getValue() != null && appointmentStartTime.getValue() != null && appointmentEndDate.getValue() != null && appointmentEndTime.getValue() != null && customerId.getValue() != null && userId.getValue() != null && appointmentContact.getValue() != null){
            return true;
        }
        return false;
    }
    /**
     * Checks if a new appointment overlaps with an existing appointment.
     * @return true if there is an overlap, false otherwise
     * @throws SQLException if there is an error in accessing the database.
     */

    public Boolean appointmentOverlap() throws SQLException {
        ObservableList<Appointment> listOfAppointments = FetchDB.getAppointmentsFromDatabase();
        LocalDateTime startDateTime = LocalDateTime.of(appointmentStartDate.getValue(), appointmentStartTime.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(appointmentEndDate.getValue(), appointmentEndTime.getValue());
        for(Appointment a: listOfAppointments){
            if((!appointmentId.getText().equals(String.valueOf(a.getAppointmentId()))) && ((startDateTime.isBefore(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())) || (startDateTime.isAfter(a.getStartTime()) && endDateTime.isBefore(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isAfter(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isBefore(a.getStartTime())) || (startDateTime.equals(a.getStartTime()) && endDateTime.equals(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isEqual(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isEqual(a.getStartTime())) || (startDateTime.isBefore(a.getStartTime()) && endDateTime.isEqual(a.getEndTime())) || (startDateTime.isEqual(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())))){
                return true;
            }
        }
        return false;
    }
    /**
     * Initializes the Modify Appointment form.
     * @throws SQLException if there is a problem with the database connection
     */
    public void initialize() throws SQLException{
        select = Main.getSelectedAppointment();
        ObservableList<Customer> listOfCustomers = FetchDB.getCustomersFromDatabase();
        ObservableList<User> listOfUsers = FetchDB.getUsersFromDatabase();
        ObservableList<Contact> listOfContacts = FetchDB.getContactsFromDatabase();
        ObservableList<Integer> customerIdList = FXCollections.observableArrayList();
        ObservableList<Integer> userIdList = FXCollections.observableArrayList();
        ObservableList<String> contactNameList = FXCollections.observableArrayList();
        for(Customer c: listOfCustomers){
            customerIdList.add(c.getCustomerID());
        }
        for(User u : listOfUsers){
            userIdList.add(u.getUserID());
        }
        for(Contact c: listOfContacts){
            contactNameList.add(c.getContactName());
        }
        customerId.setItems(customerIdList);
        userId.setItems(userIdList);
        appointmentContact.setItems(contactNameList);
        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstTime = LocalTime.MIN.plusHours(1);
        LocalTime lastTime = LocalTime.MAX.minusHours(1);
        while(firstTime.isBefore(lastTime)){
            appointmentTimes.add(firstTime);
            firstTime = firstTime.plusMinutes(15);
        }
        appointmentStartTime.setItems(appointmentTimes);
        appointmentEndTime.setItems(appointmentTimes);

        appointmentId.setText(String.valueOf(select.getAppointmentId()));
        appointmentTitle.setText(select.getTitle());
        appointmentType.setText(select.getType());
        appointmentDescription.setText(select.getDescription());
        appointmentLocation.setText(select.getLocation());
        appointmentStartDate.setValue(select.getStartTime().toLocalDate());
        appointmentStartTime.setValue(select.getStartTime().toLocalTime());
        appointmentEndDate.setValue(select.getEndTime().toLocalDate());
        appointmentEndTime.setValue(select.getEndTime().toLocalTime());
        customerId.setValue(select.getCustomerId());
        userId.setValue(select.getUserId());
        appointmentContact.setValue(select.getContactName());
    }

}
