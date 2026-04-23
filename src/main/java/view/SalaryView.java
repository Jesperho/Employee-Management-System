package view;

import dao.EmployeeDao;
import entity.Employee;
import entity.LanguageManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * JavaFX view builders for the Salary module.
 * Contains all popup dialogs and cell factories moved out of SalaryController.
 */
public final class SalaryView {

    private SalaryView() {}

    // ── Cell Factories ─────────────────────────────────────────────────────────

    /**
     * Cell factory for the Salary column — renders "€amount" in green bold text.
     */
    public static Callback<TableColumn<ObservableList<String>, String>,
            TableCell<ObservableList<String>, String>> buildSalaryColorColumn() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("€" + item);
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        };
    }

    /**
     * Cell factory for the Actions column — View and Edit buttons per row.
     *
     * @param employeeDao DAO used by the Edit popup to persist salary changes
     * @param onRefresh   callback to reload the table after a save (typically controller::loadFromDatabase)
     * @param rtlApplier  callback to apply RTL/LTR orientation to a new Scene
     */
    public static Callback<TableColumn<ObservableList<String>, String>,
            TableCell<ObservableList<String>, String>> buildActionsColumn(
            EmployeeDao employeeDao,
            Runnable onRefresh,
            Consumer<Scene> rtlApplier) {

        return col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final HBox box = new HBox(5, viewBtn, editBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showViewPopup(row, rtlApplier);
                });
                editBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showEditPopup(row, employeeDao, onRefresh, rtlApplier);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        };
    }

    // ── Popups ────────────────────────────────────────────────────────────────

    /**
     * Shows the salary detail view popup for a given table row.
     * Row indices: 0=id, 1=name, 2=dept, 3=jobTitle, 4=position, 5=salary, 6=gender.
     */
    public static void showViewPopup(ObservableList<String> row, Consumer<Scene> rtlApplier) {
        LanguageManager lang = LanguageManager.getInstance();
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a3a2a, #1e6b45);" +
                " -fx-background-radius: 15 15 0 0;");

        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #27ae60, #1e8449);");
        String n = row.get(1) != null ? row.get(1) : "?";
        String initials = n.trim().contains(" ")
                ? String.valueOf(n.charAt(0)) + n.trim().split(" ")[1].charAt(0)
                : String.valueOf(n.charAt(0));
        javafx.scene.text.Text avatarTxt = new javafx.scene.text.Text(initials.toUpperCase());
        avatarTxt.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarTxt);

        javafx.scene.text.Text nameText = new javafx.scene.text.Text(row.get(1));
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        javafx.scene.text.Text roleText = new javafx.scene.text.Text(row.get(3) + "  •  " + row.get(2));
        roleText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");

        Label salaryBadge = new Label("€ " + row.get(5));
        salaryBadge.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: #a8f5c0;" +
                " -fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 25;");
        header.getChildren().addAll(avatar, nameText, roleText, salaryBadge);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));

        addSalaryInfoCard(grid, 0, "🪪  " + lang.getTranslatedText("salary.col.id"),        row.get(0), "⚧  " + lang.getTranslatedText("salary.col.gender"),   row.get(6));
        addSalaryInfoCard(grid, 1, "🏢  " + lang.getTranslatedText("salary.col.department"), row.get(2), "📋  " + lang.getTranslatedText("salary.col.jobTitle"),  row.get(3));
        addSalaryInfoCard(grid, 2, "🎯  " + lang.getTranslatedText("salary.col.position"),   row.get(4), "💰  " + lang.getTranslatedText("salary.col.salary"),    "€ " + row.get(5));

        Button closeBtn = new Button("✕  " + lang.getTranslatedText("btn.close"));
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449);" +
                " -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
                " -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popup.close());

        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 24, 30));

        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
        card.getChildren().addAll(header, grid, footer);
        card.setMaxWidth(460);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        rtlApplier.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    /**
     * Shows the edit salary popup and persists changes to the DB on save.
     */
    public static void showEditPopup(ObservableList<String> row, EmployeeDao employeeDao,
                                     Runnable onRefresh, Consumer<Scene> rtlApplier) {
        LanguageManager lang = LanguageManager.getInstance();

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(MessageFormat.format(lang.getTranslatedText("salary.form.editTitleFull"), row.get(1)));

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text(lang.getTranslatedText("salary.form.editTitle"));
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        Text empName = new Text(MessageFormat.format(lang.getTranslatedText("salary.view.empDetail"), row.get(1), row.get(2)));
        empName.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField currentSalaryField = FormBuilder.createFormField(lang.getTranslatedText("salary.form.currentSalary"), 250);
        currentSalaryField.setText("€" + row.get(5));
        currentSalaryField.setEditable(false);
        currentSalaryField.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #e0e3e8;" +
                " -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12;");

        TextField newSalaryField = FormBuilder.createFormField(lang.getTranslatedText("salary.form.newSalary"), 250);

        ComboBox<String> positionCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Manager", "Employee", "Specialist", "Analyst", "Intern", "Lead"));
        positionCombo.setValue(row.get(4));
        positionCombo.setPrefWidth(250);
        positionCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8;" +
                " -fx-border-radius: 8; -fx-background-radius: 8;");

        TextArea reasonField = new TextArea();
        reasonField.setPromptText(lang.getTranslatedText("salary.form.reason"));
        reasonField.setPrefRowCount(2);
        reasonField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8;");

        grid.add(FormBuilder.createLabel(lang.getTranslatedText("salary.form.currentSalary")), 0, 0);
        grid.add(currentSalaryField, 1, 0);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("salary.form.newSalary")), 0, 1);
        grid.add(newSalaryField, 1, 1);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("salary.form.position")), 0, 2);
        grid.add(positionCombo, 1, 2);
        grid.add(FormBuilder.createLabel(lang.getTranslatedText("salary.form.reason")), 0, 3);
        GridPane.setColumnSpan(reasonField, 1);
        grid.add(reasonField, 1, 3);

        Button updateBtn = new Button(lang.getTranslatedText("salary.form.update"));
        updateBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4);" +
                " -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;" +
                " -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button(lang.getTranslatedText("salary.form.cancel"));
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c;" +
                " -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px;" +
                " -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        updateBtn.setOnAction(e -> {
            String newSal = newSalaryField.getText().trim();
            if (newSal.isEmpty()) {
                showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.enterNewSalary"));
                return;
            }
            double salaryValue;
            try {
                salaryValue = Double.parseDouble(newSal);
            } catch (NumberFormatException ex) {
                showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.salaryMustBeNumber"));
                return;
            }
            int empId = Integer.parseInt(row.get(0));
            Employee emp = employeeDao.find(empId);
            if (emp != null) {
                emp.setSalary(salaryValue);
                if (positionCombo.getValue() != null) emp.setRole(positionCombo.getValue());
                employeeDao.update(emp);
            }
            onRefresh.run();
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getChildren().addAll(updateBtn, cancelBtn);

        form.getChildren().addAll(formTitle, empName, grid, buttonBar);

        Scene scene = new Scene(form, 500, 420);
        rtlApplier.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    /**
     * Shows the animated payroll processing popup.
     *
     * @param total         total payroll amount (€)
     * @param empCount      number of employees being paid
     * @param scopeLabel    department name or "All Departments"
     * @param isDeptFilter  true when filtered to a specific department
     * @param rtlApplier    RTL/LTR orientation callback
     */
    public static void showPayrollPopup(int total, int empCount, String scopeLabel,
                                        boolean isDeptFilter, Consumer<Scene> rtlApplier) {
        LanguageManager lang = LanguageManager.getInstance();

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 20, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1f36, #2d3561);" +
                " -fx-background-radius: 15 15 0 0;");

        javafx.scene.text.Text titleTxt = new javafx.scene.text.Text("💵  " + lang.getTranslatedText("action.runPayroll"));
        titleTxt.setStyle("-fx-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label scopeBadge = new Label((isDeptFilter ? "🏢 " : "🌐 ") + scopeLabel);
        scopeBadge.setStyle("-fx-background-color: rgba(255,255,255,0.15);" +
                " -fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 12px;" +
                " -fx-font-weight: bold; -fx-padding: 4 14; -fx-background-radius: 20;");

        javafx.scene.text.Text subTxt = new javafx.scene.text.Text(
                MessageFormat.format(lang.getTranslatedText("salary.payroll.subtitle"), empCount));
        subTxt.setStyle("-fx-fill: rgba(255,255,255,0.65); -fx-font-size: 12px;");
        header.getChildren().addAll(titleTxt, scopeBadge, subTxt);

        Label counterLabel = new Label("€ 0");
        counterLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        counterLabel.setAlignment(Pos.CENTER);
        counterLabel.setMaxWidth(Double.MAX_VALUE);

        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);
        progressBar.setStyle("-fx-accent: #27ae60; -fx-background-color: #e8f5e9;" +
                " -fx-background-radius: 6; -fx-effect: null;");

        Label statusLabel = new Label(lang.getTranslatedText("salary.payroll.initializing"));
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        VBox body = new VBox(16, counterLabel, progressBar, statusLabel);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(28, 35, 10, 35));

        HBox summaryRow = new HBox(12);
        summaryRow.setAlignment(Pos.CENTER);
        summaryRow.setPadding(new Insets(0, 30, 0, 30));
        summaryRow.setVisible(false);
        summaryRow.getChildren().addAll(
                makeSummaryCard("👥 " + lang.getTranslatedText("salary.employeesPaid"),   String.valueOf(empCount),                                       "#dbeafe", "#1d4ed8"),
                makeSummaryCard("💰 " + lang.getTranslatedText("dashboard.totalPayroll"),  "€ " + String.format("%,d", total),                              "#d1fae5", "#065f46"),
                makeSummaryCard("📊 " + lang.getTranslatedText("dashboard.avgSalary"),      "€ " + String.format("%,d", empCount > 0 ? total / empCount : 0),"#fef3c7", "#92400e"),
                makeSummaryCard("🏢 " + lang.getTranslatedText("dashboard.departments"),    isDeptFilter ? scopeLabel : lang.getTranslatedText("salary.filterAllDepts"), "#f3e5f5", "#6a1b9a")
        );

        Button doneBtn = new Button("✓  " + lang.getTranslatedText("btn.close"));
        doneBtn.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449);" +
                " -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;" +
                " -fx-padding: 11 35; -fx-background-radius: 8; -fx-cursor: hand;");
        doneBtn.setVisible(false);
        doneBtn.setOnAction(e -> popup.close());

        HBox footer = new HBox(doneBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 24, 30));

        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 22, 0, 0, 6);");
        card.getChildren().addAll(header, body, summaryRow, footer);
        card.setMaxWidth(480);
        card.setMinWidth(420);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        rtlApplier.accept(scene);
        popup.setScene(scene);
        popup.show();

        // Animated counting sequence
        String[] steps = {
                "Validating employee records...",
                "Calculating base salaries...",
                "Applying deductions & bonuses...",
                "Generating payslips...",
                "Finalising transactions...",
                "✅ Payroll complete!"
        };
        AtomicInteger stepIdx = new AtomicInteger(0);
        int frames = 60;
        Timeline countUp = new Timeline();
        for (int i = 1; i <= frames; i++) {
            final int frame = i;
            countUp.getKeyFrames().add(new KeyFrame(Duration.millis((long) i * 30L), e -> {
                int current = (int) ((frame / (double) frames) * total);
                counterLabel.setText("€ " + String.format("%,d", current));
                progressBar.setProgress(frame / (double) frames);
                int stepFor = Math.min((frame / (frames / steps.length)), steps.length - 1);
                if (stepFor > stepIdx.get()) {
                    stepIdx.set(stepFor);
                    statusLabel.setText(steps[stepFor]);
                }
            }));
        }
        countUp.setOnFinished(e -> {
            counterLabel.setText("€ " + String.format("%,d", total));
            statusLabel.setText(MessageFormat.format(lang.getTranslatedText("salary.payroll.complete"), empCount));
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: bold;");
            summaryRow.setVisible(true);
            doneBtn.setVisible(true);
        });
        countUp.play();
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private static void addSalaryInfoCard(GridPane grid, int row,
                                          String l1, String v1, String l2, String v2) {
        Label lbl1 = new Label(l1);
        lbl1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val1 = new Label(v1);
        val1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c1 = new VBox(3, lbl1, val1);
        c1.setStyle("-fx-background-color: #f0faf4; -fx-background-radius: 8; -fx-padding: 10 14;");
        c1.setMinWidth(160);
        grid.add(c1, 0, row);

        Label lbl2 = new Label(l2);
        lbl2.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val2 = new Label(v2);
        val2.setStyle("-fx-text-fill: " + (l2.contains("Salary") ? "#27ae60" : "#1a1f36") +
                "; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c2 = new VBox(3, lbl2, val2);
        c2.setStyle("-fx-background-color: #f0faf4; -fx-background-radius: 8; -fx-padding: 10 14;");
        c2.setMinWidth(160);
        grid.add(c2, 1, row);
    }

    private static VBox makeSummaryCard(String label, String value, String bg, String fg) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + fg + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + fg + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        VBox box = new VBox(3, lbl, val);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 12 16;");
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
