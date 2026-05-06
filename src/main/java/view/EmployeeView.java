package view;

import dao.ChangeEmpDao;
import dao.CompanyDao;
import dao.DepartmentDao;
import dao.EmployeeDao;
import dao.EmployeeTranslationDao;
import entity.ChangeEmp;
import entity.Company;
import entity.Department;
import entity.Employee;
import entity.LanguageManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class EmployeeView {

    public static Callback<TableColumn<Employee, String>, TableCell<Employee, String>> buildActionsColumn(
            Consumer<Employee> onView, Consumer<Employee> onEdit, Consumer<Employee> onDelete) {
        return col -> new TableCell<>() {
            private final Button viewBtn = new Button();
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox box = new HBox(4, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");

                for (Button b : new Button[]{viewBtn, editBtn, deleteBtn}) {
                    b.setMinWidth(42);
                    b.setPrefWidth(42);
                    b.setMaxWidth(52);
                }
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> onView.accept(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e -> onEdit.accept(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        onDelete.accept(getTableView().getItems().get(idx));
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    LanguageManager lang = LanguageManager.getInstance();
                    viewBtn.setText(lang.getTranslatedText("btn.view"));
                    editBtn.setText(lang.getTranslatedText("btn.edit"));
                    deleteBtn.setText(lang.getTranslatedText("btn.delete"));
                    setGraphic(box);
                }
            }
        };
    }

    public static void showViewPopup(Employee emp, EmployeeTranslationDao employeeTranslationDao, Consumer<Scene> applyRTL) {
        LanguageManager lang = LanguageManager.getInstance();
        String langCode = lang.getAbreviation(lang.getLanguage());

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // Header gradient banner
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1f36, #2d3561); -fx-background-radius: 15 15 0 0;");

        // Avatar initials circle
        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #6c63ff, #4834d4);");
        String name = emp.getFullName() != null ? emp.getFullName() : "?";
        String initials = name.trim().contains(" ")
                ? String.valueOf(name.charAt(0)) + name.trim().split(" ")[1].charAt(0)
                : String.valueOf(name.charAt(0));
        Text avatarTxt = new Text(initials.toUpperCase());
        avatarTxt.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarTxt);

        Text nameText = new Text(emp.getFullName() != null ? emp.getFullName() : "N/A");
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Text roleText = new Text(
                getOrDefault(emp.getTranslatedPosition(langCode)) + "  •  " + emp.getTranslatedDepartment(langCode));
        roleText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");
        header.getChildren().addAll(avatar, nameText, roleText);

        // Info grid
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));

        addInfoCard(grid, 0, "🪪  " + lang.getTranslatedText("emp.col.id"), String.valueOf(employeeTranslationDao.formatNumber(emp.getId(), lang.getLanguage())), "📅  " + lang.getTranslatedText("emp.col.startDate"), getOrDefault(emp.getTranslatedStartDate(langCode)));
        addInfoCard(grid, 1, "⚧  " + lang.getTranslatedText("emp.col.gender"), getOrDefault(emp.getTranslatedGender(langCode)), "📋  " + lang.getTranslatedText("emp.col.contract"), getOrDefault(emp.getTranslatedContract(langCode)));
        addInfoCard(grid, 2, "💰  " + lang.getTranslatedText("emp.form.salary"), getOrDefault(emp.getTranslatedSalary(langCode)), "🏢  " + lang.getTranslatedText("emp.col.department"), emp.getTranslatedDepartment(langCode));

        Button closeBtn = new Button("✕  " + lang.getTranslatedText("btn.close"));
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popup.close());
        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 24, 30));

        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
        card.getChildren().addAll(header, grid, footer);
        card.setMaxWidth(460);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        var css = EmployeeView.class.getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void addInfoCard(GridPane grid, int row, String label1, String val1, String label2, String val2) {
        Label l1 = new Label(label1);
        l1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label v1 = new Label(val1);
        v1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox col1 = new VBox(3, l1, v1);
        col1.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 8; -fx-padding: 10 14;");
        col1.setMinWidth(160);
        grid.add(col1, 0, row);
        if (label2 != null && !label2.isEmpty()) {
            Label l2 = new Label(label2);
            l2.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
            Label v2 = new Label(val2);
            v2.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
            VBox col2 = new VBox(3, l2, v2);
            col2.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 8; -fx-padding: 10 14;");
            col2.setMinWidth(160);
            grid.add(col2, 1, row);
        } else {
            GridPane.setColumnSpan(col1, 2);
        }
    }

    public static void showEmployeeFormPopup(String title, Employee existingEmployee,
                                             DepartmentDao departmentDao, EmployeeDao employeeDao,
                                             EmployeeTranslationDao employeeTranslationDao,
                                             CompanyDao companyDao, ChangeEmpDao changeEmpDao,
                                             Runnable onSuccess, Consumer<Scene> applyRTL) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(title);

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text(title);
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        LanguageManager lang = LanguageManager.getInstance();

        TextField nameField = FormBuilder.createFormField(lang.getTranslatedText("emp.form.name"));

        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList(lang.getTranslatedText("gender1"), lang.getTranslatedText("gender2"), lang.getTranslatedText("gender3")));
        genderCombo.setPromptText(lang.getTranslatedText("emp.form.selectGender"));
        genderCombo.setPrefWidth(200);
        genderCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        List<Department> departments = departmentDao.findAll();
        ComboBox<Department> deptCombo = new ComboBox<>(FXCollections.observableArrayList(departments));
        deptCombo.setPromptText(lang.getTranslatedText("emp.form.selectDept"));
        deptCombo.setPrefWidth(200);
        deptCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");
        deptCombo.setCellFactory(lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setText(null);
                } else {
                    setText(d.getDepartmentName());
                }
            }
        });
        deptCombo.setButtonCell(new ListCell<Department>() {
            @Override
            protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setText(null);
                } else {
                    setText(d.getDepartmentName());
                }
            }
        });

        TextField jobTitleField = FormBuilder.createFormField(lang.getTranslatedText("emp.form.jobTitle"));

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText(lang.getTranslatedText("emp.form.startDate"));
        startDatePicker.setPrefWidth(200);

        ComboBox<String> contractCombo = new ComboBox<>(FXCollections.observableArrayList(lang.getTranslatedText("contract1"), lang.getTranslatedText("contract2"), lang.getTranslatedText("contract3"), lang.getTranslatedText("contract4")));
        contractCombo.setPromptText(lang.getTranslatedText("emp.form.contractType"));
        contractCombo.setPrefWidth(200);
        contractCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField salaryField = FormBuilder.createFormField(lang.getTranslatedText("emp.form.salary"));

        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.name")), 0, 0);       grid.add(nameField, 1, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.gender")), 2, 0);     grid.add(genderCombo, 3, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.department")), 0, 1); grid.add(deptCombo, 1, 1);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.jobTitle")), 2, 1);   grid.add(jobTitleField, 3, 1);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.startDate")), 0, 3); grid.add(startDatePicker, 1, 3);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.contract")), 2, 3);  grid.add(contractCombo, 3, 3);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("emp.form.salary")), 0, 4);    grid.add(salaryField, 1, 4);

        if (existingEmployee != null) {
            nameField.setText(existingEmployee.getFullName());
            genderCombo.setValue(existingEmployee.getGender());
            jobTitleField.setText(existingEmployee.getPosition());
            startDatePicker.setValue(existingEmployee.getStartDate());
            contractCombo.setValue(existingEmployee.getContractType());
            if (existingEmployee.getSalary() != null) {
                salaryField.setText(String.valueOf(existingEmployee.getSalary()));
            }
            if (existingEmployee.getDepartment() != null) {
                for (Department d : departments) {
                    if (d.getId() == existingEmployee.getDepartment().getId()) {
                        deptCombo.setValue(d);
                        break;
                    }
                }
            }
        }

        Button submitBtn = new Button(existingEmployee == null ? lang.getTranslatedText("emp.form.submit") : lang.getTranslatedText("emp.form.update"));
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button(lang.getTranslatedText("emp.form.cancel"));
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.nameRequired"));
                return;
            }

            Double salary = null;
            if (!salaryField.getText().isEmpty()) {
                try {
                    salary = Double.parseDouble(salaryField.getText());
                } catch (NumberFormatException ex) {
                    showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.salaryMustBeNumber"));
                    return;
                }
            }

            if (existingEmployee == null) {
                List<Company> companies = companyDao.findAll();
                Company company = companies.isEmpty() ? null : companies.get(0);

                Employee newEmp = new Employee(
                        nameField.getText(),
                        genderCombo.getValue(),
                        startDatePicker.getValue(),
                        salary,
                        company,
                        deptCombo.getValue(),
                        null,
                        contractCombo.getValue(),
                        jobTitleField.getText()
                );

                employeeDao.create(newEmp);
                employeeTranslationDao.submitCreateEmployeeTranslation(genderCombo.getValue(), contractCombo.getValue(), jobTitleField, newEmp);

                LocalDate date = LocalDate.now();
                app.UserSession s = app.UserSession.getInstance();

                String en = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.added", "en"), newEmp.getFullName());
                String es = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.added", "es"), newEmp.getFullName());
                String fa = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.added", "fa"), newEmp.getFullName());
                String ar = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.added", "ar"), newEmp.getFullName());

                ChangeEmp newChangeEmp = new ChangeEmp(date, en, fa, ar, es, s.getAdmin(), newEmp);
                changeEmpDao.create(newChangeEmp);
            } else {
                existingEmployee.setFullName(nameField.getText());
                existingEmployee.setStartDate(startDatePicker.getValue());
                existingEmployee.setSalary(salary);
                existingEmployee.setDepartment(deptCombo.getValue());
                existingEmployee.setGender(genderCombo.getValue());
                existingEmployee.setPosition(jobTitleField.getText());
                existingEmployee.setContractType(contractCombo.getValue());

                employeeDao.update(existingEmployee);

                employeeTranslationDao.deleteByEmployee(existingEmployee);
                employeeTranslationDao.submitCreateEmployeeTranslation(genderCombo.getValue(), contractCombo.getValue(), jobTitleField, existingEmployee);

                LocalDate date = LocalDate.now();
                app.UserSession s = app.UserSession.getInstance();

                String en = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.edited", "en"), existingEmployee.getFullName());
                String es = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.edited", "es"), existingEmployee.getFullName());
                String fa = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.edited", "fa"), existingEmployee.getFullName());
                String ar = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.edited", "ar"), existingEmployee.getFullName());

                ChangeEmp newChangeEmp = new ChangeEmp(date, en, fa, ar, es, s.getAdmin(), existingEmployee);
                changeEmpDao.create(newChangeEmp);
            }

            if (onSuccess != null) onSuccess.run();
            popup.close();
            utils.Toast.showSuccess(lang.getTranslatedText("alert.savedSuccessfully") != null && !lang.getTranslatedText("alert.savedSuccessfully").startsWith("!") 
                ? lang.getTranslatedText("alert.savedSuccessfully") : "Saved successfully", null);
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getChildren().addAll(submitBtn, cancelBtn);

        form.getChildren().addAll(formTitle, grid, buttonBar);

        Scene scene = new Scene(form, 700, 480);
        var css = EmployeeView.class.getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static String getOrDefault(String value) {
        return value == null ? "N/A" : value;
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
