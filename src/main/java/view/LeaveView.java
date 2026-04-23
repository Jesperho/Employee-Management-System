package view;

import dao.ChangeLeaveDao;
import dao.EmployeeDao;
import dao.LeaveDao;
import dao.LeaveTranslationDao;
import entity.ChangeLeave;
import entity.Employee;
import entity.LanguageManager;
import entity.Leave;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

public class LeaveView {

    public static Callback<TableColumn<Leave, String>, TableCell<Leave, String>> buildActionsColumn(
            Consumer<Leave> onView, Consumer<Leave> onEdit, Consumer<Leave> onDelete) {
        return col -> new TableCell<>() {
            private final Button editBtn = new Button();
            private final Button viewBtn = new Button();
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

    public static void showViewPopup(Leave leave, LeaveTranslationDao leaveTranslationDao, Consumer<Scene> applyRTL) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        LanguageManager lang = LanguageManager.getInstance();
        String langCode = lang.getAbreviation(lang.getLanguage());

        String type = getOrDefault(leave.getTranslatedType(langCode));
        String[] colors = getLeaveColors(leave.getType());
        String gradFrom = colors[0], gradTo = colors[1], accent = colors[2], lightBg = colors[3];
        String empName = leave.getEmployee() != null ? leave.getEmployee().getFullName() : "?";

        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, " + gradFrom + ", " + gradTo + "); -fx-background-radius: 15 15 0 0;");

        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: rgba(255,255,255,0.2);");

        String initials = empName.trim().contains(" ")
                ? String.valueOf(empName.charAt(0)) + empName.trim().split(" ")[1].charAt(0)
                : String.valueOf(empName.charAt(0));
        Text avatarTxt = new Text(initials.toUpperCase());
        avatarTxt.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarTxt);

        Text nameText = new Text(empName);
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label typeBadge = new Label(getLeaveEmoji(leave.getType()) + "  " + type);
        typeBadge.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 16; -fx-background-radius: 20;");

        String durationText = leave.getTranslatedDuration(langCode);
        Label durBadge = new Label("🗓  " + durationText);
        durBadge.setStyle("-fx-background-color: rgba(0,0,0,0.15); -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 16; -fx-background-radius: 20;");

        HBox badges = new HBox(10, typeBadge, durBadge);
        badges.setAlignment(Pos.CENTER);
        header.getChildren().addAll(avatar, nameText, badges);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));

        addLeaveInfoCard(grid, 0, "🪪  " + lang.getTranslatedText("leave.col.id"), String.valueOf(leaveTranslationDao.formatNumber(leave.getId(), lang.getLanguage())), "📋  " + lang.getTranslatedText("leave.col.type"), type, lightBg, accent);
        addLeaveInfoCard(grid, 1, "📅  " + lang.getTranslatedText("leave.col.startDate"), getOrDefault(leave.getTranslatedStartDate(langCode)), "📅  " + lang.getTranslatedText("leave.col.endDate"), getOrDefault(leave.getTranslatedEndDate(langCode)), lightBg, accent);
        addLeaveInfoCard(grid, 2, "⏱  " + lang.getTranslatedText("leave.col.duration"), getOrDefault(leave.getTranslatedDuration(langCode)), "📝  " + lang.getTranslatedText("leave.col.reason"), leave.getReason() == null || leave.getReason().isEmpty() ? "—" : leave.getReason(), lightBg, accent);

        Button closeBtn = new Button("✕  " + lang.getTranslatedText("btn.close"));
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, " + gradFrom + ", " + gradTo + "); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
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
        var css = LeaveView.class.getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static String[] getLeaveColors(String type) {
        if (type == null) return new String[]{"#1a1f36", "#2d3561", "#6c63ff", "#f0f0ff"};
        return switch (type.toLowerCase()) {
            case "sick", "sick leave"    -> new String[]{"#7b1111", "#c0392b", "#e74c3c", "#fdf0f0"};
            case "vacation"              -> new String[]{"#0a3d62", "#1565c0", "#2196f3", "#e3f2fd"};
            case "maternity", "paternity"-> new String[]{"#4a0a77", "#7b1fa2", "#9c27b0", "#f3e5f5"};
            case "personal"              -> new String[]{"#1a4a1a", "#2e7d32", "#388e3c", "#e8f5e9"};
            default                      -> new String[]{"#1a1f36", "#2d3561", "#6c63ff", "#f0f0ff"};
        };
    }

    private static String getLeaveEmoji(String type) {
        if (type == null) return "📋";
        return switch (type.toLowerCase()) {
            case "sick", "sick leave" -> "🤒";
            case "vacation"           -> "🏖";
            case "maternity"          -> "👶";
            case "personal"           -> "🏠";
            default                   -> "📋";
        };
    }

    private static void addLeaveInfoCard(GridPane grid, int row, String l1, String v1, String l2, String v2, String bg, String accent) {
        Label lbl1 = new Label(l1);
        lbl1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val1 = new Label(v1);
        val1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c1 = new VBox(3, lbl1, val1);
        c1.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; -fx-padding: 10 14; -fx-border-color: " + accent + "; -fx-border-width: 0 0 0 3;");
        c1.setMinWidth(160);
        grid.add(c1, 0, row);

        Label lbl2 = new Label(l2);
        lbl2.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val2 = new Label(v2);
        val2.setWrapText(true);
        val2.setMaxWidth(160);
        val2.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c2 = new VBox(3, lbl2, val2);
        c2.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; -fx-padding: 10 14; -fx-border-color: " + accent + "; -fx-border-width: 0 0 0 3;");
        c2.setMinWidth(160);
        grid.add(c2, 1, row);
    }

    public static void showLeaveFormPopup(String title, Leave existingData,
                                          EmployeeDao employeeDao, LeaveDao leaveDao,
                                          LeaveTranslationDao leaveTranslationDao, ChangeLeaveDao changeLeaveDao,
                                          Runnable onSuccess, Consumer<Scene> applyRTL) {
        LanguageManager lang = LanguageManager.getInstance();
        boolean editing = existingData != null;
        String popupTitle = (title != null && !title.isBlank()) ? title : (editing ? lang.getTranslatedText("leave.form.editTitle") : lang.getTranslatedText("leave.form.addTitle"));
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(popupTitle);

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text(popupTitle);
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(12);

        List<Employee> employees = employeeDao.findAll();
        ComboBox<Employee> empCombo = new ComboBox<>(FXCollections.observableArrayList(employees));
        empCombo.setPromptText(lang.getTranslatedText("leave.form.employee"));
        empCombo.setPrefWidth(250);
        empCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");
        empCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Employee e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getFullName() + " (ID: " + e.getId() + ")");
            }
        });
        empCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Employee e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getFullName() + " (ID: " + e.getId() + ")");
            }
        });

        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                lang.getTranslatedText("leave.sick2"), lang.getTranslatedText("leave.vacation"), lang.getTranslatedText("leave.maternity"),
                lang.getTranslatedText("leave.personal"), lang.getTranslatedText("leave.exam"), lang.getTranslatedText("leave.paternity"), lang.getTranslatedText("leave.unpaid")
        ));
        typeCombo.setPromptText(lang.getTranslatedText("leave.form.type"));
        typeCombo.setPrefWidth(250);
        typeCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText(lang.getTranslatedText("leave.form.startDate"));
        startDatePicker.setPrefWidth(250);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText(lang.getTranslatedText("leave.form.endDate"));
        endDatePicker.setPrefWidth(250);

        TextField durationField = FormBuilder.createFormField(lang.getTranslatedText("leave.form.duration"));
        durationField.setPrefWidth(250);
        durationField.setEditable(false);

        startDatePicker.valueProperty().addListener((obs, o, n) -> calcDuration(startDatePicker, endDatePicker, durationField));
        endDatePicker.valueProperty().addListener((obs, o, n) -> calcDuration(startDatePicker, endDatePicker, durationField));

        grid.add(FormBuilder.createLabel(lang.getTranslatedText("leave.form.employee")), 0, 0);    grid.add(empCombo, 1, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("leave.form.type")), 2, 0);        grid.add(typeCombo, 3, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("leave.form.startDate")), 0, 1);   grid.add(startDatePicker, 1, 1);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("leave.form.endDate")), 2, 1);     grid.add(endDatePicker, 3, 1);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("leave.form.duration")), 0, 2);    grid.add(durationField, 1, 2);

        if (existingData != null) {
            if (existingData.getEmployee() != null) {
                for (Employee emp : employees) {
                    if (emp.getId() == existingData.getEmployee().getId()) {
                        empCombo.setValue(emp);
                        break;
                    }
                }
            }
            typeCombo.setValue(existingData.getType());
            startDatePicker.setValue(existingData.getStartDate());
            endDatePicker.setValue(existingData.getEndDate());
            calcDuration(startDatePicker, endDatePicker, durationField);
            empCombo.setDisable(true);
        }

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button(editing ? lang.getTranslatedText("leave.form.update") : lang.getTranslatedText("leave.form.submit"));
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button resetBtn = new Button(lang.getTranslatedText("leave.form.reset"));
        resetBtn.setStyle("-fx-background-color: white; -fx-text-fill: #7f8c8d; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-padding: 12 25; -fx-cursor: hand;");

        Button cancelBtn = new Button(lang.getTranslatedText("leave.form.cancel"));
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (existingData == null && empCombo.getValue() == null) {
                showAlert(lang.getTranslatedText("alert.validationError"), "Please select an employee."); return;
            }
            if (typeCombo.getValue() == null) {
                showAlert(lang.getTranslatedText("alert.validationError"), "Please select a leave type."); return;
            }
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showAlert(lang.getTranslatedText("alert.validationError"), "Please select both start and end dates."); return;
            }
            if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
                showAlert(lang.getTranslatedText("alert.validationError"), "End date cannot be before start date."); return;
            }

            if (existingData == null) {
                Leave newLeave = new Leave(
                        startDatePicker.getValue(), endDatePicker.getValue(), typeCombo.getValue(),
                        "", empCombo.getValue()
                );
                leaveDao.create(newLeave);
                leaveTranslationDao.submitCreateLeaveTranslation(typeCombo.getValue(), newLeave);

                String en = "New leave was added!";
                String es = "¡Nueva licencia añadida!";
                String fa = "مرخصی جدید اضافه شد!";
                String ar = "تمت إضافة إجازة جديدة!";

                ChangeLeave newChangeLeave = new ChangeLeave(
                        LocalDate.now(),
                        en, fa, ar, es,
                        app.UserSession.getInstance().getAdmin(),
                        newLeave
                );
                changeLeaveDao.create(newChangeLeave);

            } else {
                existingData.setType(typeCombo.getValue());
                existingData.setStartDate(startDatePicker.getValue());
                existingData.setEndDate(endDatePicker.getValue());

                leaveDao.update(existingData);

                leaveTranslationDao.deleteByLeave(existingData);
                leaveTranslationDao.submitCreateLeaveTranslation(typeCombo.getValue(), existingData);

                String en = "Leave was edited!";
                String es = "¡La licencia fue editada!";
                String fa = "مرخصی ویرایش شد!";
                String ar = "تم تعديل الإجازة!";

                ChangeLeave newChangeLeave = new ChangeLeave(
                        LocalDate.now(),
                        en, fa, ar, es,
                        app.UserSession.getInstance().getAdmin(),
                        existingData
                );
                changeLeaveDao.create(newChangeLeave);
            }

            if (onSuccess != null) onSuccess.run();
            popup.close();
        });

        resetBtn.setOnAction(e -> {
            if(existingData == null) empCombo.setValue(null);
            typeCombo.setValue(null); startDatePicker.setValue(null);
            endDatePicker.setValue(null); durationField.clear();
        });

        cancelBtn.setOnAction(e -> popup.close());
        buttonBar.getChildren().addAll(submitBtn, resetBtn, cancelBtn);

        form.getChildren().addAll(formTitle, grid, buttonBar);

        Scene scene = new Scene(form, 720, 450);
        var css = LeaveView.class.getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        if (applyRTL != null) applyRTL.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void calcDuration(DatePicker start, DatePicker end, TextField duration) {
        if (start.getValue() != null && end.getValue() != null) {
            long days = ChronoUnit.DAYS.between(start.getValue(), end.getValue());
            duration.setText(String.valueOf(Math.max(days, 0)));
        }
    }

    private static String getOrDefault(String value) {
        return value == null ? "N/A" : value;
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}
