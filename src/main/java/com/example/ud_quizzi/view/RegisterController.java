package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.UserController;
import com.example.ud_quizzi.dao.DatabaseConnection; // Import lớp quản lý kết nối
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Label messageLabel;

    @FXML private ImageView registerForm;

    private UserController userController;

    @FXML
    private void initialize() {
        // Init ChoiceBox
        roleChoiceBox.getItems().addAll("teacher", "student", "admin");
        roleChoiceBox.setValue("teacher");

        // KẾT NỐI DB: Sử dụng DatabaseConnection thay vì hardcode
        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            userController = new UserController(conn);
            System.out.println("Kết nối CSDL thành công!");
        } else {
            messageLabel.setText("Kết nối DB thất bại! Kiểm tra file database.properties");
            System.err.println("Không lấy được kết nối từ DatabaseConnection.");
        }

        // Load hình nền
        try {
            URL url = getClass().getResource("/images/registerImage.png");
            if (url != null) {
                registerForm.setImage(new Image(url.toExternalForm()));
            } else {
                System.out.println("Không tìm thấy registerImage.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        if(userController == null) {
            messageLabel.setText("Lỗi: Chưa kết nối được với cơ sở dữ liệu!");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleChoiceBox.getValue();

        if(username.isEmpty() || password.isEmpty() || fullName.isEmpty()
                || email.isEmpty() || phone.isEmpty()) {
            messageLabel.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        boolean success = userController.registerUser(username, password, fullName, email, phone, role);

        if(success) {
            messageLabel.setText("Đăng ký thành công!");
            messageLabel.setStyle("-fx-text-fill: green;"); // Thêm màu xanh cho thông báo thành công
            clearFields();
        } else {
            boolean exists = userController.usernameExists(username);
            messageLabel.setStyle("-fx-text-fill: red;"); // Màu đỏ cho lỗi
            if(exists) {
                messageLabel.setText("Username đã tồn tại!");
            } else {
                messageLabel.setText("Đăng ký thất bại! Vui lòng thử lại.");
            }
        }
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        roleChoiceBox.setValue("teacher");
    }
}