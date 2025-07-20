package Exercise35_01;

import java.sql.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Exercise35_01 – Compare batch vs. non‑batch insert performance (1 000 rows). */
public class Exercise35_01 extends Application {

    /* ------------------------------------------------------------------ */
    /*                         JDBC / GUI fields                          */
    /* ------------------------------------------------------------------ */
    private Connection connection;     // current DB connection
    private Statement  statement;      // reused for inserts

    private final TextArea taInfo = new TextArea();          // log area
    private final Label    lbStatus = new Label(" ");        // top‑left

    private final Button btConnectDlg   = new Button("Connect to Database");
    private final Button btBatchUpdate  = new Button("Batch Update");
    private final Button btNonBatch     = new Button("Non‑Batch Update");

    /* ---------- dialog fields ---------- */
    private final ComboBox<String> cboDriver = new ComboBox<>();
    private final ComboBox<String> cboURL    = new ComboBox<>();
    private final TextField        tfUser    = new TextField();
    private final PasswordField    pfPass    = new PasswordField();
    private final Label            lblConn   = new Label("Not connected");

    /* ------------------------------------------------------------------ */
    @Override public void start(Stage stage) {

        /* driver / URL presets */
        cboDriver.getItems().addAll(
            "com.mysql.cj.jdbc.Driver",       // MySQL 8+
            "org.sqlite.JDBC",                // SQLite
            "oracle.jdbc.driver.OracleDriver"
        );
        cboDriver.getSelectionModel().selectFirst();

        cboURL.getItems().addAll(
            "jdbc:mysql://localhost/exercise35_1",
            "jdbc:sqlite:exercise35_1.db"
        );
        cboURL.getSelectionModel().selectFirst();

        /* ----------- main window layout ----------- */
        HBox topBar = new HBox(lbStatus, new Region(), btConnectDlg);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8));
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);

        taInfo.setEditable(false);
        taInfo.setPrefRowCount(8);

        HBox buttonBar = new HBox(15, btBatchUpdate, btNonBatch);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10));

        VBox root = new VBox(5, topBar, taInfo, buttonBar);
        root.setPadding(new Insets(10));

        /* ----------- handlers ----------- */
        btConnectDlg.setOnAction(e -> showConnectDialog());
        btBatchUpdate.setOnAction(e -> doBatchInsert());
        btNonBatch.setOnAction(e -> doNonBatchInsert());

        btBatchUpdate.setDisable(true);
        btNonBatch.setDisable(true);

        stage.setScene(new Scene(root, 460, 260));
        stage.setTitle("Exercise35_01 – Batch vs Non‑Batch");
        stage.show();
    }

    /* ------------------------------------------------------------------ */
    /*                           DB CONNECT DIALOG                        */
    /* ------------------------------------------------------------------ */
    private void showConnectDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(lbStatus.getScene().getWindow());

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(6);
        gp.addRow(0, new Label("JDBC Driver"), cboDriver);
        gp.addRow(1, new Label("Database URL"), cboURL);
        gp.addRow(2, new Label("Username"), tfUser);
        gp.addRow(3, new Label("Password"), pfPass);

        Button btConnect = new Button("Connect to DB");
        Button btClose   = new Button("Close Dialog");
        HBox  hbBtns     = new HBox(10, btConnect, btClose);
        hbBtns.setAlignment(Pos.CENTER);

        VBox box = new VBox(5, lblConn, gp, hbBtns);
        box.setPadding(new Insets(10));

        btConnect.setOnAction(e -> connect());
        btClose.setOnAction(e -> dialog.close());

        dialog.setScene(new Scene(box));
        dialog.setTitle("Connect to DB");
        dialog.show();
    }

    private void connect() {
        try {
            String drv  = cboDriver.getValue();
            String url  = cboURL.getValue();
            String user = tfUser.getText().trim();
            String pass = pfPass.getText().trim();

            Class.forName(drv);
            connection = DriverManager.getConnection(url, user, pass);
            statement  = connection.createStatement();

            ensureTempTable();                   // create table if absent

            lblConn.setText("Connected to " + url);
            lbStatus.setText("Connected");
            btBatchUpdate.setDisable(false);
            btNonBatch.setDisable(false);
        } catch (Exception ex) {
            lblConn.setText("Connect failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void ensureTempTable() throws SQLException {
        String ddl = "create table if not exists Temp(num1 double, num2 double, num3 double)";
        try (Statement s = connection.createStatement()) { s.executeUpdate(ddl); }
    }

    /* ------------------------------------------------------------------ */
    /*                      INSERT PERFORMANCE TESTS                      */
    /* ------------------------------------------------------------------ */
    private void doBatchInsert() {
        runInsert(true);
    }

    private void doNonBatchInsert() {
        runInsert(false);
    }

    private void runInsert(boolean useBatch) {
        if (connection == null) { lbStatus.setText("Not connected"); return; }

        try {
            connection.setAutoCommit(false);   // fair comparison
            long start = System.currentTimeMillis();

            if (useBatch) {
                for (int i = 0; i < 1_000; i++) {
                    double n1 = Math.random(), n2 = Math.random(), n3 = Math.random();
                    statement.addBatch(
                        "insert into Temp values (" + n1 + ", " + n2 + ", " + n3 + ')');
                }
                statement.executeBatch();
            } else {
                for (int i = 0; i < 1_000; i++) {
                    double n1 = Math.random(), n2 = Math.random(), n3 = Math.random();
                    statement.executeUpdate(
                        "insert into Temp values (" + n1 + ", " + n2 + ", " + n3 + ')');
                }
            }
            connection.commit();
            long elapsed = System.currentTimeMillis() - start;

            taInfo.appendText((useBatch ? "Batch" : "Non‑Batch") +
                " update completed – elapsed " + elapsed + " ms\n");
            lbStatus.setText((useBatch ? "Batch" : "Non‑Batch") + " succeeded");
        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException ignore) { }
            taInfo.appendText("Error: " + ex.getMessage() + '\n');
            lbStatus.setText("Error – see log");
            ex.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignore) { }
        }
    }

    public static void main(String[] args) { launch(args); }
}
