package com.hotel.ui;

import com.hotel.data.DataStore;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsView {

    private ScrollPane scrollRoot;
    private VBox root;

    private Label lblDateRange = new Label("");

    
    private Label lblTotalRev = new Label("₹0");
    private Label lblTotalRevChange = new Label("0%");
    
    private Label lblOccRate = new Label("0%");
    private Label lblOccSubtitle = new Label("0 of 0 rooms filled");
    private Label lblOccChange = new Label("0%");

    private Label lblAvgStay = new Label("0.0");
    private Label lblAvgStayChange = new Label("0%");

    private Label lblAvgRevPerRoom = new Label("₹0");
    private Label lblAvgRevChange = new Label("0%");

    private HBox weeklyChartBox = new HBox(15);
    private StackPane donutChartPane = new StackPane();
    private VBox occupancyBox = new VBox(15);
    private VBox topRoomsBox = new VBox(10);
    private VBox activityBox = new VBox(15);
    private VBox summaryBox = new VBox(10);

    
    private LocalDate startDate = LocalDate.now().minusDays(6);
    private LocalDate endDate = LocalDate.now();

    public ReportsView() {
        root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(5);
        Label title = new Label("Reports & Analytics");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label subtitle = new Label("Hotel performance — occupancy, revenue and booking trends");
        subtitle.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        
        MenuButton exportBtn = new MenuButton("Export Report");
        exportBtn.setStyle("-fx-background-color: #facc15; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        MenuItem exportCsv = new MenuItem("Export as CSV");
        MenuItem exportPdf = new MenuItem("Export as Text (PDF format)");
        exportCsv.setOnAction(e -> exportData("csv"));
        exportPdf.setOnAction(e -> exportData("txt"));
        exportBtn.getItems().addAll(exportCsv, exportPdf);

        header.getChildren().addAll(titleBox, spacer, exportBtn);

       
        HBox periodFilters = new HBox(15);
        periodFilters.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> periodCombo = new ComboBox<>();
        periodCombo.getItems().addAll("Last 7 Days", "Last 30 Days", "This Month", "Custom Range");
        periodCombo.setValue("Last 7 Days");
        periodCombo.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-border-color: #334155; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        DatePicker dpStart = new DatePicker(startDate);
        DatePicker dpEnd = new DatePicker(endDate);
        dpStart.setVisible(false); dpStart.setManaged(false);
        dpEnd.setVisible(false); dpEnd.setManaged(false);

        lblDateRange.setStyle("-fx-text-fill: #facc15; -fx-font-size: 16px; -fx-font-weight: bold;");

        periodCombo.setOnAction(e -> {
            String val = periodCombo.getValue();
            boolean custom = "Custom Range".equals(val);
            dpStart.setVisible(custom); dpStart.setManaged(custom);
            dpEnd.setVisible(custom); dpEnd.setManaged(custom);
            
            if (!custom) {
                endDate = LocalDate.now();
                if ("Last 7 Days".equals(val)) startDate = endDate.minusDays(6);
                else if ("Last 30 Days".equals(val)) startDate = endDate.minusDays(29);
                else if ("This Month".equals(val)) startDate = endDate.withDayOfMonth(1);
                updateReports();
            }
        });

        dpStart.setOnAction(e -> { if(dpStart.isVisible()) { startDate = dpStart.getValue(); updateReports(); } });
        dpEnd.setOnAction(e -> { if(dpEnd.isVisible()) { endDate = dpEnd.getValue(); updateReports(); } });

        periodFilters.getChildren().addAll(periodCombo, dpStart, new Label("to"){{setStyle("-fx-text-fill: #9ca3af;"); setVisible(false); setManaged(false); dpStart.visibleProperty().addListener((o,old,nw)->{setVisible(nw); setManaged(nw);});}}, dpEnd, new Region(){{setPrefWidth(20);}}, lblDateRange);

      
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
            createStatCard("Total Revenue", lblTotalRev, lblTotalRevChange),
            createStatCardWithSubtitle("Occupancy Rate", lblOccRate, lblOccSubtitle, lblOccChange),
            createStatCard("Avg Stay Duration", lblAvgStay, lblAvgStayChange),
            createStatCard("Avg Revenue/Room", lblAvgRevPerRoom, lblAvgRevChange)
        );

       
        HBox chartsRow = new HBox(20);
        weeklyChartBox.setAlignment(Pos.BOTTOM_CENTER);
        weeklyChartBox.setPrefHeight(150);
        VBox weeklyRevCard = createSectionCard("Weekly Revenue", weeklyChartBox);
        HBox.setHgrow(weeklyRevCard, Priority.ALWAYS);
        
        VBox revByRoomCard = createSectionCard("Revenue by Room Type", createDonutContainer());
        HBox.setHgrow(revByRoomCard, Priority.ALWAYS);
        
        chartsRow.getChildren().addAll(weeklyRevCard, revByRoomCard);

        
        HBox bottomRow1 = new HBox(20);
        VBox occCard = createSectionCard("Occupancy Overview", occupancyBox);
        HBox.setHgrow(occCard, Priority.ALWAYS);
        
        VBox topCard = createSectionCard("Top Performing Rooms", topRoomsBox);
        HBox.setHgrow(topCard, Priority.ALWAYS);
        
        bottomRow1.getChildren().addAll(occCard, topCard);

        
        HBox bottomRow2 = new HBox(20);
        VBox actCard = createSectionCard("Recent Activity", activityBox);
        HBox.setHgrow(actCard, Priority.ALWAYS);

        VBox sumCard = createSectionCard("Booking Summary Table", summaryBox);
        HBox.setHgrow(sumCard, Priority.ALWAYS);

        bottomRow2.getChildren().addAll(actCard, sumCard);

        root.getChildren().addAll(header, periodFilters, statsRow, chartsRow, bottomRow1, bottomRow2);

        scrollRoot = new ScrollPane(root);
        scrollRoot.setFitToWidth(true);
        scrollRoot.setStyle("-fx-background: #0f172a; -fx-background-color: #0f172a; -fx-border-color: transparent;");

        
        setupListeners();
        updateReports();
    }

    private void setupListeners() {
        DataStore.getRooms().addListener((ListChangeListener<Room>) c -> updateReports());
        DataStore.getCustomers().addListener((ListChangeListener<Customer>) c -> updateReports());
    }

    private void updateReports() {
        if (startDate == null || endDate == null) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        lblDateRange.setText(startDate.format(dtf) + " – " + endDate.format(dtf));

        calculateTotalRevenue();
        calculateOccupancyRate();
        calculateAvgStay();
        calculateAvgRevenuePerRoom();
        
        renderWeeklyChart();
        renderDonutChart();
        renderOccupancyByRoomType();
        renderTopRooms();
        renderRecentActivity();
        renderBookingSummary();
    }

    
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try { return LocalDate.parse(dateStr); } catch (Exception e) {}
        try { return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yyyy")); } catch (Exception e) {}
        try { return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d/M/yyyy")); } catch (Exception e) {}
        return LocalDate.now();
    }

    private boolean isDateInRange(String dateStr, LocalDate start, LocalDate end) {
        LocalDate date = parseDate(dateStr);
        if (date == null) return false;
        return !date.isBefore(start) && !date.isAfter(end);
    }

    private List<Customer> getFilteredCustomers(LocalDate s, LocalDate e) {
        return DataStore.getCustomers().stream()
                .filter(c -> isDateInRange(c.getCheckInDate(), s, e))
                .collect(Collectors.toList());
    }

    private void updateChangeLabel(Label lbl, double current, double previous) {
        if (previous == 0) {
            if (current == 0) lbl.setText("0%");
            else lbl.setText("+100%");
            lbl.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");
            return;
        }
        double change = ((current - previous) / previous) * 100;
        if (change >= 0) {
            lbl.setText(String.format("+%.1f%%", change));
            lbl.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            lbl.setText(String.format("%.1f%%", change));
            lbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-font-weight: bold;");
        }
    }

  

    private void calculateTotalRevenue() {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate pStart = startDate.minusDays(daysBetween);
        LocalDate pEnd = endDate.minusDays(daysBetween);

        double curr = getFilteredCustomers(startDate, endDate).stream()
                .filter(c -> c.getRoom() != null).mapToDouble(c -> c.getRoom().getPrice() * c.getDays()).sum();
        
        double prev = getFilteredCustomers(pStart, pEnd).stream()
                .filter(c -> c.getRoom() != null).mapToDouble(c -> c.getRoom().getPrice() * c.getDays()).sum();

        lblTotalRev.setText(String.format("₹%,.0f", curr));
        updateChangeLabel(lblTotalRevChange, curr, prev);
    }

    private void calculateOccupancyRate() {
       
        long totalRooms = DataStore.getRooms().size();
        long occupied = DataStore.getRooms().stream().filter(r -> "Occupied".equalsIgnoreCase(r.getStatus())).count();
        long occRate = totalRooms == 0 ? 0 : (occupied * 100) / totalRooms;
        
        lblOccRate.setText(occRate + "%");
        lblOccSubtitle.setText(occupied + " of " + totalRooms + " rooms filled");
        lblOccChange.setText("Live");
        lblOccChange.setStyle("-fx-text-fill: #facc15; -fx-font-size: 12px;");
    }

    private void calculateAvgStay() {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate pStart = startDate.minusDays(daysBetween);
        LocalDate pEnd = endDate.minusDays(daysBetween);

        List<Customer> currC = getFilteredCustomers(startDate, endDate);
        List<Customer> prevC = getFilteredCustomers(pStart, pEnd);

        double curr = currC.isEmpty() ? 0 : currC.stream().mapToInt(Customer::getDays).sum() / (double)currC.size();
        double prev = prevC.isEmpty() ? 0 : prevC.stream().mapToInt(Customer::getDays).sum() / (double)prevC.size();

        lblAvgStay.setText(String.format("%.1f", curr));
        updateChangeLabel(lblAvgStayChange, curr, prev);
    }

    private void calculateAvgRevenuePerRoom() {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate pStart = startDate.minusDays(daysBetween);
        LocalDate pEnd = endDate.minusDays(daysBetween);

        List<Customer> currC = getFilteredCustomers(startDate, endDate);
        List<Customer> prevC = getFilteredCustomers(pStart, pEnd);

        double totalRooms = DataStore.getRooms().size(); 
        if(totalRooms == 0) totalRooms = 1;

        double currRev = currC.stream().filter(c -> c.getRoom() != null).mapToDouble(c -> c.getRoom().getPrice() * c.getDays()).sum();
        double prevRev = prevC.stream().filter(c -> c.getRoom() != null).mapToDouble(c -> c.getRoom().getPrice() * c.getDays()).sum();

        double curr = currRev / totalRooms;
        double prev = prevRev / totalRooms;

        lblAvgRevPerRoom.setText(String.format("₹%,.0f", curr));
        updateChangeLabel(lblAvgRevChange, curr, prev);
    }

    private void renderWeeklyChart() {
        weeklyChartBox.getChildren().clear();
        Map<DayOfWeek, Double> weeklyRev = new EnumMap<>(DayOfWeek.class);
        for(DayOfWeek d : DayOfWeek.values()) weeklyRev.put(d, 0.0);

        for (Customer c : getFilteredCustomers(startDate, endDate)) {
            if(c.getRoom() != null && c.getCheckInDate() != null) {
                LocalDate date = parseDate(c.getCheckInDate());
                if(date != null) {
                    DayOfWeek day = date.getDayOfWeek();
                    weeklyRev.put(day, weeklyRev.get(day) + (c.getRoom().getPrice() * c.getDays()));
                }
            }
        }

        double maxRev = weeklyRev.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        if (maxRev == 0) maxRev = 1;

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        DayOfWeek[] enums = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        
        for (int i = 0; i < days.length; i++) {
            VBox col = new VBox(5);
            col.setAlignment(Pos.BOTTOM_CENTER);
            
            double rev = weeklyRev.get(enums[i]);
            double height = (rev / maxRev) * 120;
            if(height < 5) height = 5; 
            
            Rectangle bar = new Rectangle(30, height, Color.web("#3b82f6"));
            bar.setArcWidth(8);
            bar.setArcHeight(8);
            Tooltip t = new Tooltip(String.format("₹%,.0f", rev));
            Tooltip.install(bar, t);

            Label day = new Label(days[i]);
            day.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
            col.getChildren().addAll(bar, day);
            weeklyChartBox.getChildren().add(col);
        }
    }

    private void renderDonutChart() {
        donutChartPane.getChildren().clear();
        
        double revSingle = 0, revDouble = 0, revDeluxe = 0;
        for (Customer c : getFilteredCustomers(startDate, endDate)) {
            if (c.getRoom() != null && c.getRoom().getType() != null) {
                double val = c.getRoom().getPrice() * c.getDays();
                if (c.getRoom().getType().equalsIgnoreCase("Single")) revSingle += val;
                if (c.getRoom().getType().equalsIgnoreCase("Double")) revDouble += val;
                if (c.getRoom().getType().equalsIgnoreCase("Deluxe")) revDeluxe += val;
            }
        }

        double total = revSingle + revDouble + revDeluxe;
        if (total == 0) total = 1;

        double cir = 2 * Math.PI * 60;
        double singleDash = (revSingle / total) * cir;
        double doubleDash = (revDouble / total) * cir;
        double deluxeDash = (revDeluxe / total) * cir;

        Circle outer1 = createDonutSlice("#3b82f6", 0, singleDash, cir, revSingle);
        Circle outer2 = createDonutSlice("#a855f7", (revSingle/total)*360, doubleDash, cir, revDouble);
        Circle outer3 = createDonutSlice("#facc15", ((revSingle+revDouble)/total)*360, deluxeDash, cir, revDeluxe);
        
        VBox text = new VBox(2);
        text.setAlignment(Pos.CENTER);
        Label totalLbl = new Label(total == 1 && revSingle==0 ? "₹0" : String.format("₹%,.0f", (revSingle+revDouble+revDeluxe)));
        totalLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label sub = new Label("Total");
        sub.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
        text.getChildren().addAll(totalLbl, sub);

        donutChartPane.getChildren().addAll(outer1, outer2, outer3, text);
    }

    private Circle createDonutSlice(String color, double rotation, double length, double circumference, double amount) {
        Circle c = new Circle(60, Color.TRANSPARENT);
        c.setStrokeWidth(14);
        c.setStroke(Color.web(color));
        c.setRotate(-90 + rotation);
        c.getStrokeDashArray().addAll(length, circumference);
        Tooltip.install(c, new Tooltip(String.format("₹%,.0f", amount)));
        return c;
    }

    private void renderOccupancyByRoomType() {
        occupancyBox.getChildren().clear();
        Map<String, Long> totalByType = DataStore.getRooms().stream().collect(Collectors.groupingBy(Room::getType, Collectors.counting()));
        Map<String, Long> occByType = DataStore.getRooms().stream().filter(r -> "Occupied".equalsIgnoreCase(r.getStatus())).collect(Collectors.groupingBy(Room::getType, Collectors.counting()));

        double totSingle = totalByType.getOrDefault("Single", 0L);
        double occSingle = occByType.getOrDefault("Single", 0L);
        double ratioS = totSingle == 0 ? 0 : occSingle / totSingle;

        double totDouble = totalByType.getOrDefault("Double", 0L);
        double occDouble = occByType.getOrDefault("Double", 0L);
        double ratioD = totDouble == 0 ? 0 : occDouble / totDouble;

        double totDeluxe = totalByType.getOrDefault("Deluxe", 0L);
        double occDeluxe = occByType.getOrDefault("Deluxe", 0L);
        double ratioL = totDeluxe == 0 ? 0 : occDeluxe / totDeluxe;

        double tot = DataStore.getRooms().size();
        double occ = DataStore.getRooms().stream().filter(r -> "Occupied".equalsIgnoreCase(r.getStatus())).count();
        double ratioOverall = tot == 0 ? 0 : occ / tot;

        occupancyBox.getChildren().addAll(
            createProgressBar("Single Rooms", ratioS, String.format("%.0f%%", ratioS*100), "#3b82f6"),
            createProgressBar("Double Rooms", ratioD, String.format("%.0f%%", ratioD*100), "#a855f7"),
            createProgressBar("Deluxe Rooms", ratioL, String.format("%.0f%%", ratioL*100), "#facc15"),
            new Separator(),
            createProgressBar("Overall Occupancy", ratioOverall, String.format("%.0f%%", ratioOverall*100), "#22c55e")
        );
    }

    private void renderTopRooms() {
        topRoomsBox.getChildren().clear();
        
        Map<Room, Double> roomRev = new HashMap<>();
        for (Customer c : getFilteredCustomers(startDate, endDate)) {
            if (c.getRoom() != null) {
                roomRev.put(c.getRoom(), roomRev.getOrDefault(c.getRoom(), 0.0) + (c.getRoom().getPrice() * c.getDays()));
            }
        }
        
        List<Map.Entry<Room, Double>> topRooms = roomRev.entrySet().stream()
           .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
           .limit(5)
           .collect(Collectors.toList());

        int rank = 1;
        for (Map.Entry<Room, Double> entry : topRooms) {
            Room r = entry.getKey();
            Double rev = entry.getValue();

            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8));
            row.setStyle("-fx-background-color: #334155; -fx-background-radius: 8;");
            
            Label rankLbl = new Label(rank + "");
            rankLbl.setStyle("-fx-text-fill: #facc15; -fx-font-weight: bold; -fx-min-width: 20; -fx-alignment: center;");
            
            Label rName = new Label("Room " + r.getRoomNumber() + " (" + r.getType() + ")");
            rName.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            
            Label rRev = new Label(String.format("₹%,.0f", rev));
            rRev.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
            
            row.getChildren().addAll(rankLbl, rName, sp, rRev);
            topRoomsBox.getChildren().add(row);
            rank++;
        }

        if(topRoomsBox.getChildren().isEmpty()) {
            Label empty = new Label("No revenue data available in period.");
            empty.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic;");
            topRoomsBox.getChildren().add(empty);
        }
    }

    private void renderRecentActivity() {
        activityBox.getChildren().clear();
        
        List<Customer> allCust = new ArrayList<>(DataStore.getCustomers());
        Collections.reverse(allCust); 
        List<Customer> recent = allCust.stream().limit(10).collect(Collectors.toList());

        String[] colors = {"#facc15", "#22c55e", "#3b82f6", "#ef4444", "#a855f7"};
        int colIdx = 0;

        for (Customer c : recent) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(4, Color.web(colors[colIdx % colors.length]));
            
            String log = c.getName() + " managed in Room " + (c.getRoom() != null ? c.getRoom().getRoomNumber() : "N/A");
            Label l = new Label(log);
            l.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
            row.getChildren().addAll(dot, l);
            activityBox.getChildren().add(row);
            colIdx++;
        }

        if(activityBox.getChildren().isEmpty()) {
            Label empty = new Label("No recent activity.");
            empty.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic;");
            activityBox.getChildren().add(empty);
        }
    }

    private void renderBookingSummary() {
        summaryBox.getChildren().clear();
        
        HBox header = new HBox(10);
        String headStyle = "-fx-text-fill: #9ca3af; -fx-font-weight: bold; -fx-font-size: 13px;";
        Label h1 = new Label("Room Type"); h1.setStyle(headStyle); h1.setPrefWidth(120);
        Label h2 = new Label("Bookings"); h2.setStyle(headStyle); h2.setPrefWidth(80); h2.setAlignment(Pos.CENTER_RIGHT);
        Label h3 = new Label("Avg Nights"); h3.setStyle(headStyle); h3.setPrefWidth(80); h3.setAlignment(Pos.CENTER_RIGHT);
        header.getChildren().addAll(h1, h2, h3);
        summaryBox.getChildren().addAll(header, new Separator());

        String[] types = {"Single", "Double", "Deluxe"};
        List<Customer> filtered = getFilteredCustomers(startDate, endDate);

        for (String type : types) {
            long bookings = filtered.stream().filter(c -> c.getRoom() != null && type.equalsIgnoreCase(c.getRoom().getType())).count();
            double totalDays = filtered.stream().filter(c -> c.getRoom() != null && type.equalsIgnoreCase(c.getRoom().getType())).mapToInt(Customer::getDays).sum();
            double avgNights = bookings == 0 ? 0 : totalDays / bookings;

            HBox row = new HBox(10);
            String rowStyle = "-fx-text-fill: white; -fx-font-size: 14px;";
            Label r1 = new Label(type); r1.setStyle(rowStyle); r1.setPrefWidth(120);
            Label r2 = new Label(String.valueOf(bookings)); r2.setStyle(rowStyle); r2.setPrefWidth(80); r2.setAlignment(Pos.CENTER_RIGHT);
            Label r3 = new Label(String.format("%.1f", avgNights)); r3.setStyle(rowStyle); r3.setPrefWidth(80); r3.setAlignment(Pos.CENTER_RIGHT);
            row.getChildren().addAll(r1, r2, r3);
            summaryBox.getChildren().add(row);
        }
    }

    
    private void exportData(String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        if(type.equals("csv")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("Hotel_Report.csv");
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (PDF Alternative)", "*.txt"));
            fileChooser.setInitialFileName("Hotel_Report.txt");
        }
        
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                if(type.equals("csv")) {
                    writer.println("Metric,Value");
                    writer.println("Total Revenue," + lblTotalRev.getText().replace("₹","").replace(",",""));
                    writer.println("Occupancy Rate," + lblOccRate.getText().replace("%",""));
                    writer.println("Avg Stay Duration," + lblAvgStay.getText());
                    writer.println("Avg Revenue/Room," + lblAvgRevPerRoom.getText().replace("₹","").replace(",",""));
                    writer.println();
                    writer.println("Room Type,Bookings,Avg Nights");
                    String[] types = {"Single", "Double", "Deluxe"};
                    List<Customer> filtered = getFilteredCustomers(startDate, endDate);
                    for (String t : types) {
                        long b = filtered.stream().filter(c -> c.getRoom() != null && t.equalsIgnoreCase(c.getRoom().getType())).count();
                        double d = filtered.stream().filter(c -> c.getRoom() != null && t.equalsIgnoreCase(c.getRoom().getType())).mapToInt(Customer::getDays).sum();
                        double a = b == 0 ? 0 : d / b;
                        writer.println(t + "," + b + "," + String.format("%.1f", a));
                    }
                } else {
                    writer.println("==========================================");
                    writer.println("          GRAND HOTEL REPORT");
                    writer.println("==========================================");
                    writer.println("Period: " + lblDateRange.getText());
                    writer.println();
                    writer.println("KPI SUMMARY");
                    writer.println("Total Revenue: " + lblTotalRev.getText() + " (" + lblTotalRevChange.getText() + ")");
                    writer.println("Occupancy Rate: " + lblOccRate.getText() + " | " + lblOccSubtitle.getText());
                    writer.println("Avg Stay Duration: " + lblAvgStay.getText() + " days");
                    writer.println("Avg Revenue/Room: " + lblAvgRevPerRoom.getText());
                    writer.println();
                    writer.println("BOOKING SUMMARY");
                    String[] types = {"Single", "Double", "Deluxe"};
                    List<Customer> filtered = getFilteredCustomers(startDate, endDate);
                    for (String t : types) {
                        long b = filtered.stream().filter(c -> c.getRoom() != null && t.equalsIgnoreCase(c.getRoom().getType())).count();
                        double d = filtered.stream().filter(c -> c.getRoom() != null && t.equalsIgnoreCase(c.getRoom().getType())).mapToInt(Customer::getDays).sum();
                        double a = b == 0 ? 0 : d / b;
                        writer.println("- " + t + ": " + b + " bookings, " + String.format("%.1f", a) + " avg nights");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private VBox createStatCard(String title, Label valLbl, Label changeLbl) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px;");
        
        HBox valBox = new HBox(10);
        valBox.setAlignment(Pos.BOTTOM_LEFT);
        valLbl.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        valBox.getChildren().addAll(valLbl, changeLbl);

        box.getChildren().addAll(titleLbl, valBox);
        return box;
    }
    
    private VBox createStatCardWithSubtitle(String title, Label valLbl, Label subLbl, Label changeLbl) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px;");
        
        HBox valBox = new HBox(10);
        valBox.setAlignment(Pos.BOTTOM_LEFT);
        valLbl.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        valBox.getChildren().addAll(valLbl, changeLbl);
        
        subLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        box.getChildren().addAll(titleLbl, valBox, subLbl);
        return box;
    }

    private VBox createSectionCard(String title, Node content) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        card.getChildren().addAll(titleLbl, new Separator(), content);
        return card;
    }
    
    private Node createDonutContainer() {
        donutChartPane.setPrefHeight(130);
        
        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
            createLegend("Single", "#3b82f6"),
            createLegend("Double", "#a855f7"),
            createLegend("Deluxe", "#facc15")
        );

        VBox wrap = new VBox(15);
        wrap.setAlignment(Pos.CENTER);
        wrap.getChildren().addAll(donutChartPane, legend);
        return wrap;
    }

    private HBox createLegend(String text, String color) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);
        Circle dot = new Circle(4, Color.web(color));
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 12px;");
        box.getChildren().addAll(dot, l);
        return box;
    }

    private VBox createProgressBar(String label, double ratio, String percent, String color) {
        VBox box = new VBox(5);
        HBox top = new HBox();
        Label l = new Label(label); l.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
        Label p = new Label(percent); p.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        top.getChildren().addAll(l, sp, p);

        StackPane barBg = new StackPane();
        barBg.setStyle("-fx-background-color: #334155; -fx-background-radius: 4;");
        barBg.setPrefHeight(8);
        barBg.setAlignment(Pos.CENTER_LEFT);
        Region bar = new Region();
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        bar.setPrefHeight(8);
        bar.maxWidthProperty().bind(barBg.widthProperty().multiply(ratio));
        barBg.getChildren().add(bar);

        box.getChildren().addAll(top, barBg);
        return box;
    }

    public ScrollPane getView() {
        return scrollRoot;
    }
}
