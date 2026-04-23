package view;

import dao.ChangeDeptDao;
import dao.ChangeEmpDao;
import dao.ChangeLeaveDao;
import entity.ChangeDept;
import entity.ChangeEmp;
import entity.ChangeLeave;
import entity.LanguageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.function.Consumer;

public class DashboardView {

    public static HBox createActivityItem(String icon, String iconStyle, String title, String description, String time) {
        HBox item = new HBox(12);
        item.getStyleClass().add("activity-item");
        item.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().addAll("activity-icon-container", iconStyle);
        Text iconText = new Text(icon);
        iconText.getStyleClass().add("activity-icon");
        iconContainer.getChildren().add(iconText);

        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Text titleText = new Text(title);
        titleText.getStyleClass().add("activity-title");
        Text descText = new Text(description);
        descText.getStyleClass().add("activity-description");
        textBox.getChildren().addAll(titleText, descText);

        Text timeText = new Text(time);
        timeText.getStyleClass().add("activity-time");

        item.getChildren().addAll(iconContainer, textBox, timeText);
        return item;
    }

    public static void showViewMorePopup(ChangeEmpDao changeEmpDao, ChangeLeaveDao changeLeaveDao, ChangeDeptDao changeDeptDao, Consumer<Scene> applyRTLToScene) {
        LanguageManager lang = LanguageManager.getInstance();
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // ── Header ──────────────────────────────────────────────────────────
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(22, 25, 18, 25));
        header.setSpacing(12);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1a1f36, #2d3561);" +
                "-fx-background-radius: 15 15 0 0;");

        StackPane iconWrap = new StackPane();
        javafx.scene.shape.Circle iconCircle = new javafx.scene.shape.Circle(20);
        iconCircle.setStyle("-fx-fill: rgba(108,99,255,0.35);");
        Label iconLbl = new Label("🕐");
        iconLbl.setStyle("-fx-font-size: 16px;");
        iconWrap.getChildren().addAll(iconCircle, iconLbl);

        VBox titleBox = new VBox(2);
        Label titleLbl = new Label(lang.getTranslatedText("dashboard.recentActivities"));
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titleBox.getChildren().addAll(titleLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;" +
                "-fx-background-radius: 6; -fx-font-size: 14px; -fx-cursor: hand;" +
                "-fx-padding: 4 10;");
        closeBtn.setOnAction(e -> popup.close());

        header.getChildren().addAll(iconWrap, titleBox, spacer, closeBtn);

        // ── Build activity entries ────────────────────────────────────────
        VBox activityList = new VBox(0);
        activityList.setStyle("-fx-background-color: white;");

        app.UserSession s = app.UserSession.getInstance();

        List<ChangeEmp> changesEmps = changeEmpDao.findAll();
        changesEmps.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        int empLimit = changesEmps.size();

        int rowNum = 0;

        for (int i = 0; i < empLimit; i++) {
            ChangeEmp emp = changesEmps.get(i);
            activityList.getChildren().add(
                    buildActivityRow(rowNum++,
                            "👤", "#e8f4ff", "#1565c0",
                            emp.getMessage(lang.getCurrentLanguageCode()),
                            (lang.getTranslatedText("madeby") + " " + s.getFullName()), "", "", "#1565c0", "#dbeafe"));
        }

        // Last 2 leave records added (sorted by ID descending)
        List<ChangeLeave> leaves = changeLeaveDao.findAll();
        leaves.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

        int leaveLimit = leaves.size();
        for (int i = 0; i < leaveLimit; i++) {
            ChangeLeave leave = leaves.get(i);
            activityList.getChildren().add(
                    buildActivityRow(rowNum++,
                            "📅", "#e8f4ff", "#1565c0",
                            leave.getMessage(lang.getCurrentLanguageCode()),
                            (lang.getTranslatedText("madeby") + " " + s.getFullName()), "", "", "#1565c0", "#dbeafe"));
        }

        List<ChangeDept> changeDepts = changeDeptDao.findAll();
        changeDepts.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        int changeDeptsLimit = changeDepts.size();
        for (int i = 0; i < changeDeptsLimit; i++) {
            ChangeDept changeDept = changeDepts.get(i);
            activityList.getChildren().add(
                    buildActivityRow(rowNum++,
                            "+", "#e8f4ff", "#1565c0",
                            changeDept.getMessage(lang.getCurrentLanguageCode()),
                            (lang.getTranslatedText("madeby") + " " + s.getFullName()), "", "", "#1565c0", "#dbeafe"));
        }

        if (activityList.getChildren().isEmpty()) {
            Label empty = new Label("No activity found.");
            empty.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 30;");
            activityList.getChildren().add(empty);
        }

        // ── Scroll pane wraps list ────────────────────────────────────────
        ScrollPane scroll = new ScrollPane(activityList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        scroll.setPrefHeight(460);

        // ── Footer ───────────────────────────────────────────────────────
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(14, 25, 16, 25));
        footer.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 0 0 15 15;" +
                "-fx-border-color: #e8eaf0; -fx-border-width: 1 0 0 0;");


        Button doneBtn = new Button(lang.getTranslatedText("btn.close"));
        doneBtn.setStyle("-fx-background-color: linear-gradient(to right,#6c63ff,#5a52d4);" +
                "-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13px;" +
                "-fx-padding:9 28;-fx-background-radius:8;-fx-cursor:hand;");
        doneBtn.setOnAction(e -> popup.close());
        footer.getChildren().addAll(doneBtn);

        // ── Assemble card ─────────────────────────────────────────────────
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.28),24,0,0,6);");
        card.getChildren().addAll(header, scroll, footer);
        card.setMaxWidth(600);
        card.setMinWidth(560);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(12));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        if (applyRTLToScene != null) applyRTLToScene.accept(scene);
        popup.setScene(scene);
        popup.showAndWait();
    }

    public static HBox buildActivityRow(int index, String emoji, String iconBg, String iconFg,
                                  String title, String detail, String date,
                                  String badgeText, String badgeFg, String badgeBg) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 22, 14, 22));
        row.setStyle("-fx-background-color: " + (index % 2 == 0 ? "white" : "#fafbff") + ";" +
                "-fx-border-color: #f0f2f8; -fx-border-width: 0 0 1 0;");

        // Icon bubble
        StackPane bubble = new StackPane();
        bubble.setMinSize(42, 42);
        bubble.setMaxSize(42, 42);
        javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(21);
        bg.setStyle("-fx-fill: " + iconBg + ";");
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size: 16px;");
        bubble.getChildren().addAll(bg, emojiLbl);

        // Text section
        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Badge
        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeFg + ";" +
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8;" +
                "-fx-background-radius: 10;");
        titleRow.getChildren().addAll(titleLbl, badge);

        Label detailLbl = new Label(detail);
        detailLbl.setStyle("-fx-text-fill: #5f6c7b; -fx-font-size: 12px;");
        detailLbl.setWrapText(true);
        detailLbl.setMaxWidth(350);
        textBox.getChildren().addAll(titleRow, detailLbl);

        // Date on the right
        Label dateLbl = new Label("📅 " + date);
        dateLbl.setStyle("-fx-text-fill: #9ba8b7; -fx-font-size: 11px;");
        dateLbl.setMinWidth(95);
        dateLbl.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(bubble, textBox, dateLbl);
        return row;
    }

    public static String[] getActivityLeaveColor(String type) {
        if (type == null) return new String[]{"#f0f0ff", "#6c63ff", "#ede9ff"};
        return switch (type.toLowerCase()) {
            case "sick"      -> new String[]{"#fff0f0", "#c0392b", "#fde8e8"};
            case "vacation"  -> new String[]{"#e3f2fd", "#1565c0", "#dbeafe"};
            case "maternity" -> new String[]{"#f3e5f5", "#7b1fa2", "#ede7f6"};
            case "personal"  -> new String[]{"#e8f5e9", "#2e7d32", "#dcfce7"};
            case "exam"      -> new String[]{"#fff8e1", "#f57f17", "#fef9c3"};
            default          -> new String[]{"#f0f0ff", "#6c63ff", "#ede9ff"};
        };
    }

    public static String getActivityLeaveEmoji(String type) {
        if (type == null) return "📋";
        return switch (type.toLowerCase()) {
            case "sick"      -> "🤒";
            case "vacation"  -> "🏖";
            case "maternity" -> "👶";
            case "personal"  -> "🏠";
            case "exam"      -> "📚";
            default          -> "📋";
        };
    }
}
