import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ClientController {

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private VBox messageArea;

    @FXML
    private ScrollPane scrollPane;

    @FXML 
    private Label viewNameField;

    static String finalTableName;

    @FXML
    public void initialize() throws Exception {
        login2Controller l = new login2Controller();
        viewNameField.setText(l.name);

        generateTable();
        startMessageReceiver();
    }

    private void loadChatHistory(String user1, String user2, String tableName) {
        MessageStack messageStack = new MessageStack();
        List<Message> messages = messageStack.toList();
        for (Message msg : messages) {
            Platform.runLater(() -> {
                HBox messageBox = formatLabel(msg.getMessage(), 
                    msg.getSender().equals(user1) ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT, msg.getTimestamp());
                messageArea.getChildren().add(messageBox);
                scrollPane.setVvalue(1.0);
            });
        }
    }


    @FXML
    public void handleSendButtonAction() {
        String out = messageField.getText();
        if (out.isEmpty()) {
            return;
        }

        try {
            login1Controller l1 = new login1Controller();
            login2Controller l2 = new login2Controller();
            if (l1.dos != null) {
                l1.dos.writeUTF(out);
                l1.dos.flush();

                System.out.println("Client sent: " + out);

                saveMessageToDatabase(l1.phone, l2.phone, out,finalTableName);

                Platform.runLater(() -> {
                    HBox messageBox = formatLabel(out, javafx.geometry.Pos.CENTER_RIGHT);
                    messageArea.getChildren().add(messageBox);
                    messageField.clear();
                    scrollPane.setVvalue(1.0);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HBox formatLabel(String out, javafx.geometry.Pos position) {
        Label messageLabel = new Label(out);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-background-color: #d3ffd3; -fx-padding: 10px; -fx-background-radius: 10px;");
        messageLabel.setMinHeight(Label.USE_PREF_SIZE);

        Text timeText = new Text(getCurrentTime());
        timeText.setStyle("-fx-font-size: 12px; -fx-fill: gray; -fx-padding: 2px;");

        VBox messageBox = new VBox(messageLabel, timeText);
        messageBox.setAlignment(position == javafx.geometry.Pos.CENTER_RIGHT ? javafx.geometry.Pos.BOTTOM_RIGHT : javafx.geometry.Pos.BOTTOM_LEFT);
        messageBox.setSpacing(5);

        HBox hbox = new HBox(messageBox);
        hbox.setAlignment(position);
        hbox.setPadding(new Insets(10));

        return hbox;
    }

    public HBox formatLabel(String out, javafx.geometry.Pos position, String time) {
        Label messageLabel = new Label(out);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-background-color: #d3ffd3; -fx-padding: 10px; -fx-background-radius: 10px;");
        messageLabel.setMinHeight(Label.USE_PREF_SIZE);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = dateTime.format(outputFormatter);
        Text timeText = new Text(formattedTime);
        timeText.setStyle("-fx-font-size: 12px; -fx-fill: gray; -fx-padding: 2px;");

        VBox messageBox = new VBox(messageLabel, timeText);
        messageBox.setAlignment(position == javafx.geometry.Pos.CENTER_RIGHT ? javafx.geometry.Pos.BOTTOM_RIGHT : javafx.geometry.Pos.BOTTOM_LEFT);
        messageBox.setSpacing(5);

        HBox hbox = new HBox(messageBox);
        hbox.setAlignment(position);
        hbox.setPadding(new Insets(10));

        return hbox;
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    private void saveMessageToDatabase(String sender, String receiver, String message, String tableName) {
    String insertQuery = "INSERT INTO "+tableName+" (sender, receiver, message) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/connectify", "root", "");
         PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

        stmt.setString(1, sender);
        stmt.setString(2, receiver);
        stmt.setString(3, message);

        stmt.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    public void startMessageReceiver() {
        login1Controller l = new login1Controller();
        new Thread(() -> {
            System.out.println("Message receiver thread started for client");
            try {
                while (true) {
                    String message = l.dis.readUTF();
                    System.out.println("Client Received message: " + message);
                    Platform.runLater(() -> {
                        HBox messageBox = formatLabel(message, javafx.geometry.Pos.CENTER_LEFT);
                        messageArea.getChildren().add(messageBox);
                        scrollPane.setVvalue(1.0);
                    });
                }
            }catch(NullPointerException e){
                
            }
             catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void generateTable() throws Exception {
        login1Controller l1 = new login1Controller();
        String phone1 = l1.phone;

        login2Controller l2 = new login2Controller();
        String phone2 = l2.phone;

        String tableName1 = phone1+"_WITH_"+phone2;
        String tableName2 = phone2+"_WITH_"+phone1;

        boolean found1 = false;
        boolean found2 = false;

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/connectify", "root", "");
        DatabaseMetaData dmd = con.getMetaData();

        ResultSet tables = dmd.getTables(null, null,"%", new String[] {"TABLE"});
        while(tables.next()) {
            if(tables.getString("TABLE_NAME").equalsIgnoreCase(tableName1)) {
                found1 = true;
                finalTableName = tableName1;
                break;
            }
            else if (tables.getString("TABLE_NAME").equalsIgnoreCase(tableName2)) {
                found2 = true;
                finalTableName = tableName2;
                break;
            }
        }

        if(found1) {
            loadChatHistory(phone1, phone2,tableName1);
        }
        else if(found2) {
            loadChatHistory(phone1, phone2,tableName2);
        }
    }
}
