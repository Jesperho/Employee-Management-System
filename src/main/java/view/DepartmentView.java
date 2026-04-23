package view;

import dao.ChangeDeptDao;
import dao.CompanyDao;
import dao.DepartmentDao;
import dao.DepartmentTranslationDao;
import dao.EmployeeDao;
import entity.ChangeDept;
import entity.Company;
import entity.Department;
import entity.Employee;
import entity.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class DepartmentView {

    public static Callback<TableColumn<ObservableList<String>, String>, TableCell<ObservableList<String>, String>> buildDeptActionsColumn(
            Consumer<ObservableList<String>> onEdit, Consumer<ObservableList<String>> onDelete) {
        return col -> new TableCell<>() {
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox box = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> onEdit.accept(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> onDelete.accept(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    LanguageManager lang = LanguageManager.getInstance();
                    editBtn.setText(lang.getTranslatedText("btn.edit"));
                    deleteBtn.setText(lang.getTranslatedText("btn.delete"));
                    setGraphic(box);
                }
            }
        };
    }

    public static Callback<TableColumn<ObservableList<String>, String>, TableCell<ObservableList<String>, String>> buildEmpActionsColumn(
            List<Employee> currentDeptEmployees, Consumer<Employee> onView, Consumer<Employee> onEdit, Consumer<Employee> onDelete) {
        return col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Del");
            private final HBox box = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < currentDeptEmployees.size()) onView.accept(currentDeptEmployees.get(idx));
                });
                editBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < currentDeptEmployees.size()) onEdit.accept(currentDeptEmployees.get(idx));
                });
                deleteBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < currentDeptEmployees.size()) onDelete.accept(currentDeptEmployees.get(idx));
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        };
    }

    public static void showEmpViewPopup(Employee emp, Consumer<Scene> applyRTL) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setTitle("Employee Details");

        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1f36, #2d3561); -fx-background-radius: 15 15 0 0;");

        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #6c63ff, #4834d4);");
        String initials = emp.getFullName() != null && !emp.getFullName().isEmpty()
                ? (emp.getFullName().trim().contains(" ")
                ? String.valueOf(emp.getFullName().charAt(0)) + emp.getFullName().trim().split(" ")[1].charAt(0)
                : String.valueOf(emp.getFullName().charAt(0)))
                : "?";
        Text avatarText = new Text(initials.toUpperCase());
        avatarText.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarText);

        Text nameText = new Text(emp.getFullName() != null ? emp.getFullName() : "N/A");
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        String deptName = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "N/A";
        Text roleText = new Text((emp.getPosition() != null ? emp.getPosition() : "Employee") + "  •  " + deptName);
        roleText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");
        header.getChildren().addAll(avatar, nameText, roleText);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));

        String salary = emp.getSalary() != null ? "€" + String.format("%,.0f", emp.getSalary()) : "N/A";
        addInfoRow(grid, 0, "🪪  Employee ID", String.valueOf(emp.getId()), "📅  Start Date", emp.getStartDate() != null ? emp.getStartDate().toString() : "N/A");
        addInfoRow(grid, 1, "⚧  Gender", emp.getGender() != null ? emp.getGender() : "N/A", "📋  Contract", emp.getContractType() != null ? emp.getContractType() : "N/A");
        addInfoRow(grid, 2, "💰  Salary", salary, "🏢  Department", deptName);

        Button closeBtn = new Button("✕  Close");
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
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        var css = DepartmentView.class.getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void addInfoRow(GridPane grid, int row, String label1, String val1, String label2, String val2) {
        Label l1 = new Label(label1);
        l1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label v1 = new Label(val1);
        v1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox col1 = new VBox(3, l1, v1);
        col1.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 8; -fx-padding: 10 14;");
        col1.setMinWidth(160);
        grid.add(col1, 0, row);

        if (!label2.isEmpty()) {
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

    public static void showEmpEditPopup(Employee emp, EmployeeDao employeeDao, DepartmentDao departmentDao, Runnable onSuccess, Consumer<Scene> applyRTL) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        LanguageManager dlang = LanguageManager.getInstance();
        popup.setTitle(dlang.getTranslatedText("emp.form.editTitle"));

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text(dlang.getTranslatedText("emp.form.editTitle"));
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField nameField = FormBuilder.createFormField(dlang.getTranslatedText("emp.form.name"));
        nameField.setText(emp.getFullName() != null ? emp.getFullName() : "");

        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList(
                dlang.getTranslatedText("gender1"), dlang.getTranslatedText("gender2"), dlang.getTranslatedText("gender3")));
        genderCombo.setValue(emp.getGender());
        genderCombo.setPrefWidth(200);
        genderCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        List<Department> departments = departmentDao.findAll();
        ComboBox<Department> deptCombo = new ComboBox<>(FXCollections.observableArrayList(departments));
        deptCombo.setPrefWidth(200);
        deptCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");
        deptCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d.getDepartmentName());
            }
        });
        deptCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d.getDepartmentName());
            }
        });
        if (emp.getDepartment() != null) {
            departments.stream().filter(d -> d.getId() == emp.getDepartment().getId())
                    .findFirst().ifPresent(deptCombo::setValue);
        }

        TextField jobTitleField = FormBuilder.createFormField(dlang.getTranslatedText("emp.form.jobTitle"));
        jobTitleField.setText(emp.getPosition() != null ? emp.getPosition() : "");

        DatePicker startDate = new DatePicker(emp.getStartDate());
        startDate.setPrefWidth(200);

        ComboBox<String> contractCombo = new ComboBox<>(
                FXCollections.observableArrayList(dlang.getTranslatedText("contract1"), dlang.getTranslatedText("contract2"), dlang.getTranslatedText("contract3"), dlang.getTranslatedText("contract4")));
        contractCombo.setValue(emp.getContractType());
        contractCombo.setPrefWidth(200);
        contractCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField salaryField = FormBuilder.createFormField(dlang.getTranslatedText("emp.form.salary"));
        if (emp.getSalary() != null) salaryField.setText(String.valueOf(emp.getSalary()));

        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.name")),      0, 0); grid.add(nameField,    1, 0);
        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.gender")),    2, 0); grid.add(genderCombo,  3, 0);
        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.department")),0, 1); grid.add(deptCombo,    1, 1);
        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.jobTitle")),  2, 1); grid.add(jobTitleField,3, 1);
        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.startDate")), 0, 3); grid.add(startDate,    1, 3);
        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.contract")),  2, 3); grid.add(contractCombo,3, 3);
        grid.add(FormBuilder.createLabel(dlang.getTranslatedText("emp.form.salary")),    0, 4); grid.add(salaryField,  1, 4);

        Button updateBtn = new Button(dlang.getTranslatedText("btn.update"));
        updateBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button(dlang.getTranslatedText("btn.cancel"));
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        updateBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) { showAlert("Validation Error", "Name is required."); return; }
            Double salary = null;
            if (!salaryField.getText().isEmpty()) {
                try { salary = Double.parseDouble(salaryField.getText()); }
                catch (NumberFormatException ex) { showAlert("Validation Error", "Salary must be a valid number."); return; }
            }
            emp.setFullName(nameField.getText());
            emp.setGender(genderCombo.getValue());
            emp.setPosition(jobTitleField.getText());
            emp.setStartDate(startDate.getValue());
            emp.setContractType(contractCombo.getValue());
            emp.setSalary(salary);
            emp.setDepartment(deptCombo.getValue());
            employeeDao.update(emp);
            
            if (onSuccess != null) onSuccess.run();
            popup.close();
        });
        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getChildren().addAll(updateBtn, cancelBtn);

        form.getChildren().addAll(formTitle, grid, buttonBar);
        Scene scene = new Scene(form, 720, 490);
        var css = DepartmentView.class.getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    public static void showDeptFormPopup(String title, ObservableList<String> existingData, 
            DepartmentDao departmentDao, CompanyDao companyDao, 
            DepartmentTranslationDao departmentTranslationDao, ChangeDeptDao changeDeptDao, 
            EmployeeDao employeeDao, Runnable onSuccess, Consumer<Scene> applyRTL) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(title);

        Scene scene;
        var css = DepartmentView.class.getResource("/css/management.css");

        if (existingData != null) {
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            Tab infoTab = new Tab("Department Info");
            infoTab.setContent(buildDeptForm(popup, existingData, departmentDao, companyDao, departmentTranslationDao, changeDeptDao, onSuccess));

            Tab empTab = new Tab("Employees");
            int deptId = Integer.parseInt(existingData.get(0));
            empTab.setContent(buildEditPopupEmployeesTab(deptId, existingData.get(1), employeeDao));

            tabPane.getTabs().addAll(infoTab, empTab);
            scene = new Scene(tabPane, 900, 520);
        } else {
            scene = new Scene(buildDeptForm(popup, null, departmentDao, companyDao, departmentTranslationDao, changeDeptDao, onSuccess), 700, 380);
        }

        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static VBox buildDeptForm(Stage popup, ObservableList<String> existingData, 
            DepartmentDao departmentDao, CompanyDao companyDao, 
            DepartmentTranslationDao departmentTranslationDao, ChangeDeptDao changeDeptDao, 
            Runnable onSuccess) {
        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        LanguageManager lang = LanguageManager.getInstance();
        String formTitleText = existingData == null
                ? lang.getTranslatedText("dept.addBtn")
                : java.text.MessageFormat.format(lang.getTranslatedText("dept.form.editTitleFull"), existingData.get(1));
        Text formTitle = new Text(formTitleText);
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        ComboBox<String> nameField = new ComboBox<>(FXCollections.observableArrayList(
                lang.getTranslatedText("dept.operations"), lang.getTranslatedText("dept.production"), lang.getTranslatedText("dept.research"),
                lang.getTranslatedText("dept.sales"), lang.getTranslatedText("dept.marketing"), lang.getTranslatedText("dept.customerService"),
                lang.getTranslatedText("dept.hr"), lang.getTranslatedText("dept.finance"), lang.getTranslatedText("dept.legal"),
                lang.getTranslatedText("dept.it"), lang.getTranslatedText("dept.data"), lang.getTranslatedText("dept.executive"),
                lang.getTranslatedText("dept.strategy"), lang.getTranslatedText("dept.logistics"), lang.getTranslatedText("dept.quality"),
                lang.getTranslatedText("dept.assurance"), lang.getTranslatedText("dept.pr")
        ));

        nameField.setPrefWidth(200);
        nameField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField capacityField = FormBuilder.createFormField(lang.getTranslatedText("dept.col.capacity"));
        DatePicker creationDate = new DatePicker();
        creationDate.setPromptText(lang.getTranslatedText("dept.col.created"));
        creationDate.setPrefWidth(250);

        grid.add(FormBuilder.createLabel(lang.getTranslatedText("dept.col.name")), 0, 0); grid.add(nameField, 1, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("dept.col.capacity")), 2, 0); grid.add(capacityField, 3, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("dept.col.created")), 0, 1);  grid.add(creationDate, 1, 1);

        if (existingData != null) {
            nameField.setValue(existingData.get(1));
            capacityField.setText(existingData.get(2));
            if (!"N/A".equals(existingData.get(4))) {
                try {
                    creationDate.setValue(LocalDate.parse(existingData.get(4)));
                } catch (Exception ignored) {}
            }
        }

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button(existingData == null ? lang.getTranslatedText("btn.submit") : lang.getTranslatedText("btn.update"));
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button(lang.getTranslatedText("btn.cancel"));
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (nameField.getValue() == null || nameField.getValue().isEmpty()) {
                showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.deptNameRequired"));
                return;
            }
            int capacity = 0;
            if (!capacityField.getText().isEmpty()) {
                try {
                    capacity = Integer.parseInt(capacityField.getText());
                } catch (NumberFormatException ex) {
                    showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.capacityMustBeNumber"));
                    return;
                }
            }

            if (existingData == null) {
                List<Company> companies = companyDao.findAll();
                Company company = companies.isEmpty() ? null : companies.get(0);
                Department newDept = new Department(capacity, nameField.getValue(), "", creationDate.getValue(), company);
                departmentDao.create(newDept);
                departmentTranslationDao.submitCreateDepartmentTranslation(nameField.getValue(), newDept);

                LocalDate date = LocalDate.now();
                app.UserSession s = app.UserSession.getInstance();
                String deptName = newDept.getDepartmentName();
                String deptKey = lang.getKey(deptName);

                String en = java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.added", "en"), lang.getTranslatedTexts(deptKey, "en"));
                String fa = java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.added", "fa"), lang.getTranslatedTexts(deptKey, "fa"));
                String ar = java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.added", "ar"), lang.getTranslatedTexts(deptKey, "ar"));
                String es = java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.added", "es"), lang.getTranslatedTexts(deptKey, "es"));

                ChangeDept newChangeDept = new ChangeDept(date, en, fa, ar, es, s.getAdmin(), newDept);
                changeDeptDao.create(newChangeDept);
            } else {
                int id = Integer.parseInt(existingData.get(0));
                Department dept = departmentDao.find(id);
                if (dept != null) {
                    dept.setDepartmentName(nameField.getValue());
                    dept.setCapacity(capacity);
                    dept.setCreationDate(creationDate.getValue());

                    departmentDao.update(dept);
                    departmentTranslationDao.deleteByDepartment(dept);
                    departmentTranslationDao.submitCreateDepartmentTranslation(nameField.getValue(), dept);

                    LocalDate date = LocalDate.now();
                    app.UserSession s = app.UserSession.getInstance();
                    String deptName = dept.getDepartmentName();
                    String deptKey = lang.getKey(deptName);

                    changeDeptDao.create(new ChangeDept(
                            date,
                            java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.edited", "en"), lang.getTranslatedTexts(deptKey, "en")),
                            java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.edited", "fa"), lang.getTranslatedTexts(deptKey, "fa")),
                            java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.edited", "ar"), lang.getTranslatedTexts(deptKey, "ar")),
                            java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.edited", "es"), lang.getTranslatedTexts(deptKey, "es")),
                            s.getAdmin(), dept
                    ));
                }
            }

            if (onSuccess != null) onSuccess.run();
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());
        buttonBar.getChildren().addAll(submitBtn, cancelBtn);
        form.getChildren().addAll(formTitle, grid, buttonBar);
        return form;
    }

    private static VBox buildEditPopupEmployeesTab(int deptId, String deptName, EmployeeDao employeeDao) {
        VBox container = new VBox(12);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white;");

        List<Employee> employees = employeeDao.findByDepartment(deptId);

        Text header = new Text("Employees in " + deptName + " (" + employees.size() + ")");
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        TableView<ObservableList<String>> table = new TableView<>();
        table.getStyleClass().add("management-table");
        VBox.setVgrow(table, Priority.ALWAYS);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (Employee emp : employees) {
            data.add(FXCollections.observableArrayList(
                    String.valueOf(emp.getId()),
                    emp.getFullName() != null ? emp.getFullName() : "N/A",
                    emp.getPosition() != null ? emp.getPosition() : "N/A",
                    emp.getStartDate() != null ? emp.getStartDate().toString() : "N/A",
                    emp.getContractType() != null ? emp.getContractType() : "N/A",
                    emp.getGender() != null ? emp.getGender() : "N/A"
            ));
        }

        TableColumn<ObservableList<String>, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        cId.setPrefWidth(55);

        TableColumn<ObservableList<String>, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        cName.setPrefWidth(200);

        TableColumn<ObservableList<String>, String> cTitle = new TableColumn<>("Job Title");
        cTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        cTitle.setPrefWidth(170);

        TableColumn<ObservableList<String>, String> cStart = new TableColumn<>("Start Date");
        cStart.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        cStart.setPrefWidth(110);

        TableColumn<ObservableList<String>, String> cContract = new TableColumn<>("Contract");
        cContract.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        cContract.setPrefWidth(110);

        TableColumn<ObservableList<String>, String> cGender = new TableColumn<>("Gender");
        cGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
        cGender.setPrefWidth(90);

        table.getColumns().addAll(cId, cName, cTitle, cStart, cContract, cGender);
        table.setItems(data);

        if (employees.isEmpty()) {
            table.setPlaceholder(new Label("No employees in this department."));
        }

        container.getChildren().addAll(header, table);
        return container;
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}
