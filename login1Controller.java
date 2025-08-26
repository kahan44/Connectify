import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class login1Controller extends Database {
    @FXML
    private TextField phone1Field;
    @FXML
    private TextField nameField;
    @FXML
    private TextField pass1Field;
    @FXML
    private TextField pass2Field;
    @FXML
    private TextField otpField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private TextField confirmNewPasswordField;
    
    private String otp;

    @FXML
    private TextField phone2Field;
    static private String tempPhone;
    
    @FXML
    private TextField phone0Field;

    @FXML
    private TextField pass0Field;

    @FXML
    private ImageView waitImage;

    private Stage stage;
    private Scene scene;
    private Parent root;

    static private AppUsers user;
    static List userList = new List();

    static private boolean syncStatus;
    static DataInputStream dis;
    static DataOutputStream dos;

    static String name;
    static String phone;

    public void syncUsers() throws Exception {
        connect();
        addToList(userList);
    }

    public void login(ActionEvent event) throws Exception {
        if (syncStatus == false) {
            syncUsers();
        }
        if (phone0Field.getText().length() == 10 && phone0Field.getText().matches("[0-9]{10}")) {
            if (userList.exists(phone0Field.getText())) {
                if (pass0Field.getText().equals(userList.getPassword(phone0Field.getText()))) {
                    userList.updateLoginStatus(phone0Field.getText());
                    name = userList.getName(phone0Field.getText());
                    phone = phone0Field.getText();
                    
                    waitImage.setFitWidth(349);
                    waitImage.setFitHeight(470);
                    waitImage.setLayoutX(0);
                    waitImage.setLayoutY(0);
                    waitImage.setImage(new Image("wait2.png"));

                    UserLogin2 u2 = new UserLogin2();
                    Stage newStage = new Stage();
                    u2.start(newStage);

                    new Thread(() -> {
                        try {
                            UserLogin1.getLatch().await();
                    
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
                                    Parent root = loader.load();
                                    ClientController clientController = loader.getController();
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.show();
                    
                                    new Thread(() -> {
                                                try {
                                                
                                                    Socket socket = new Socket("127.0.0.1", 6001);
                                                    dis = new DataInputStream(socket.getInputStream());
                                                    dos = new DataOutputStream(socket.getOutputStream());
                                                
                                                    System.out.println("Connected to server");
                                                
                                                    if (clientController != null) {
                                                        clientController.startMessageReceiver();
                                                    }
                                                
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }).start();                                    
                                } catch (IOException e) {

                                }
                            });
                            
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();                   
                } else {
                    pass0Field.clear();
                    pass0Field.setStyle("-fx-prompt-text-fill: red;");
                    pass0Field.setPromptText("Incorrect Password!");
                }
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("User not found, Please verify phone number!");
                alert.showAndWait();
            }
        } else {
            phone0Field.clear();
            phone0Field.setStyle("-fx-prompt-text-fill: red;");
            phone0Field.setPromptText("Enter in proper 10-digit format!");
        }
    }

    public void signUp (ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp1.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void backToLogin (ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login1.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public void confirmSignUp (ActionEvent event) throws Exception {
        if(phone1Field.getText().length() == 10 && phone1Field.getText().matches("[0-9]{10}")) {
            if(nameField.getText().matches("[a-zA-Z]+")) {
                if((pass1Field.getText().length() >= 8 && pass1Field.getText().length() <= 16) && pass1Field.getText().equals(pass2Field.getText())) {
                    if(syncStatus == false) {
                        syncUsers();
                        syncStatus = true;
                    }
                    if(userList.exists(phone1Field.getText())) {
                        phone1Field.clear();
                        phone1Field.setStyle("-fx-prompt-text-fill: red;");
                        phone1Field.setPromptText("User already exists!");
                    }
                    else {
                        user = new AppUsers(phone1Field.getText(), nameField.getText(), pass1Field.getText());
                        userList.add(user);

                        connect();
                        addNewAppUser(phone1Field.getText(), nameField.getText(), pass1Field.getText());

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Sign Up");
                        alert.setHeaderText(null);
                        alert.setContentText("Signed Up Successfully!");
                        alert.showAndWait();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("login1.fxml"));
                        root = loader.load();
                        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    }
                }
                else {
                    if(!(pass1Field.getText().equals(pass2Field.getText()))) {
                        pass2Field.clear();
                        pass2Field.setStyle("-fx-prompt-text-fill: red;");
                        pass2Field.setPromptText("Both password does not match!");
                    }
                    else {
                        pass1Field.clear();
                        pass1Field.setStyle("-fx-prompt-text-fill: red;");
                        pass1Field.setPromptText("Choose in (8-16) characters!");
                    }
                }
            }
            else {
                nameField.clear();
                nameField.setStyle("-fx-prompt-text-fill: red;");
                nameField.setPromptText("Enter only alphabets!");
            }
        }
        else {
            phone1Field.clear();
            phone1Field.setStyle("-fx-prompt-text-fill: red;");
            phone1Field.setPromptText("Enter in proper 10-digit format!");
        }
    }

    public void resetPassword(ActionEvent event) throws Exception {
        if(syncStatus == false) {
            syncUsers();
            syncStatus = true;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("otp1.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static String generateOTP () {
        String digits = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }

        return otp.toString();
    }

    public void sendOTP (ActionEvent event) throws Exception {
        if(phone2Field.getText().matches("[0-9]{10}") && phone2Field.getText().length() == 10) {
            tempPhone = phone2Field.getText();
            if(userList.exists(tempPhone)) {
                otp = generateOTP();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("OTP");
                alert.setHeaderText(null);
                alert.setContentText("Your One-Time Password (OTP) is: " + otp);
                alert.showAndWait();
            }
            else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("User not found, Please verify phone number!");
                alert.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("login1.fxml"));
                root = loader.load();
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }
        else {
            phone2Field.clear();
            phone2Field.setStyle("-fx-prompt-text-fill: red;");
            phone2Field.setPromptText("Enter in proper 10-digit format!");
        }
    }

    public void verifyOTP (ActionEvent event) throws Exception {
        if(otp == null) {
            otpField.setStyle("-fx-prompt-text-fill: red;");
            otpField.setPromptText("Get OTP first!"); 
        }
        else {
            if(otpField.getText() != null && otp.equals(otpField.getText())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("reset1.fxml"));
                root = loader.load();
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                otpField.clear();
                otpField.setStyle("-fx-prompt-text-fill: red;");
                otpField.setPromptText("Incorrect OTP!");
            }
        }
    }

    public void confirmReset (ActionEvent event) throws Exception {
        if((newPasswordField.getText().length() >= 8 && newPasswordField.getText().length() <= 16) && newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
            connect();
            updatePassword(tempPhone, confirmNewPasswordField.getText());          
            userList.updateListPassword(tempPhone, confirmNewPasswordField.getText());

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Reset Password");
            alert.setHeaderText(null);
            alert.setContentText("Password Changed Successfully!");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login1.fxml"));
            root = loader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else {
            if(!(newPasswordField.getText().equals(confirmNewPasswordField.getText()))) {
                confirmNewPasswordField.clear();
                confirmNewPasswordField.setStyle("-fx-prompt-text-fill: red;");
                confirmNewPasswordField.setPromptText("Both password does not match!");
            }
            else {
                newPasswordField.clear();
                newPasswordField.setStyle("-fx-prompt-text-fill: red;");
                newPasswordField.setPromptText("Choose in (8-16) characters!");
            }
        }
    }
}