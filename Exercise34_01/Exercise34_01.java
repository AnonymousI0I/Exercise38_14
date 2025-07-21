package Exercise34_01;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

/**
 * Exercise 34‑01 — JavaFX GUI that lets you VIEW, INSERT and UPDATE records
 * in the table Staff. Uses **SQLite**, so it runs on any machine without
 * installing a database server.  The file <code>staff.db</code> is created in
 * the working directory on first launch.
 */
public class Exercise34_01 extends Application {

    /* ───────────────  DB constants ─────────────── */
    private static final String JDBC_URL = "jdbc:sqlite:staff.db";
    /* ────────────────────────────────────────────── */

    private Connection conn;
    private PreparedStatement psSelect;
    private PreparedStatement psInsert;
    private PreparedStatement psUpdate;

    private final Label lbStatus = new Label("Ready");

    private final TextField tfID        = new TextField();
    private final TextField tfLastName  = new TextField();
    private final TextField tfFirstName = new TextField();
    private final TextField tfMI        = new TextField();
    private final TextField tfAddress   = new TextField();
    private final TextField tfCity      = new TextField();
    private final TextField tfState     = new TextField();
    private final TextField tfPhone     = new TextField();
    private final TextField tfEmail     = new TextField();

    @Override public void start(Stage stage) throws Exception {
        connectDB();                       // open DB and prepare statements
        buildGUI(stage);                   // create & show JavaFX window
    }

    /* -------- connect and create table if absent -------- */
    private void connectDB() throws Exception {
        Class.forName("org.sqlite.JDBC");                  // load driver
        conn = DriverManager.getConnection(JDBC_URL);

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
              "CREATE TABLE IF NOT EXISTS Staff (" +
              " id        CHAR(9) PRIMARY KEY," +
              " lastName  VARCHAR(15)," +
              " firstName VARCHAR(15)," +
              " mi        CHAR(1)," +
              " address   VARCHAR(20)," +
              " city      VARCHAR(20)," +
              " state     CHAR(2)," +
              " telephone CHAR(10)," +
              " email     VARCHAR(40))");
        }

        psSelect = conn.prepareStatement(
            "SELECT lastName, firstName, mi, address, city, state, " +
            "telephone, email FROM Staff WHERE id = ?");
        psInsert = conn.prepareStatement(
            "INSERT INTO Staff VALUES (?,?,?,?,?,?,?,?,?)");
        psUpdate = conn.prepareStatement(
            "UPDATE Staff SET lastName=?, firstName=?, mi=?, address=?, city=?, " +
            "state=?, telephone=?, email=? WHERE id=?");
    }

    /* ----------------  GUI builder  ------------------- */
    private void buildGUI(Stage stage) {
        GridPane g = new GridPane();
        g.setHgap(6); g.setVgap(6); g.setPadding(new Insets(10));

        tfMI.setPrefColumnCount(2);
        tfState.setPrefColumnCount(2);

        int r = 0;
        g.addRow(r++, new Label("ID"), tfID);
        g.addRow(r++, new Label("Last Name"), tfLastName,
                       new Label("First Name"), tfFirstName,
                       new Label("MI"), tfMI);
        g.addRow(r++, new Label("Address"), tfAddress);
        g.addRow(r++, new Label("City"), tfCity,
                       new Label("State"), tfState);
        g.addRow(r++, new Label("Telephone"), tfPhone,
                       new Label("Email"), tfEmail);

        Button btView   = new Button("View");
        Button btInsert = new Button("Insert");
        Button btUpdate = new Button("Update");
        Button btClear  = new Button("Clear");

        btView  .setOnAction(e -> view());
        btInsert.setOnAction(e -> insert());
        btUpdate.setOnAction(e -> update());
        btClear .setOnAction(e -> clear());

        HBox buttons = new HBox(10, btView, btInsert, btUpdate, btClear);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(8, lbStatus, g, buttons);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root));
        stage.setTitle("Exercise34_01 – SQLite");
        stage.show();
    }

    /* ---------------- button actions ------------------ */
    private void view() {
        try {
            psSelect.setString(1, tfID.getText().trim());
            ResultSet rs = psSelect.executeQuery();
            if (rs.next()) {
                lbStatus.setText("Record loaded");
                tfLastName .setText(rs.getString(1));
                tfFirstName.setText(rs.getString(2));
                tfMI       .setText(rs.getString(3));
                tfAddress  .setText(rs.getString(4));
                tfCity     .setText(rs.getString(5));
                tfState    .setText(rs.getString(6));
                tfPhone    .setText(rs.getString(7));
                tfEmail    .setText(rs.getString(8));
            } else {
                lbStatus.setText("Record not found");
                clear();
            }
        } catch (SQLException ex) {
            lbStatus.setText("DB error: " + ex.getMessage());
        }
    }

    private void insert() {
        try {
            psInsert.setString(1, tfID.getText().trim());
            psInsert.setString(2, tfLastName.getText().trim());
            psInsert.setString(3, tfFirstName.getText().trim());
            psInsert.setString(4, tfMI.getText().trim());
            psInsert.setString(5, tfAddress.getText().trim());
            psInsert.setString(6, tfCity.getText().trim());
            psInsert.setString(7, tfState.getText().trim());
            psInsert.setString(8, tfPhone.getText().trim());
            psInsert.setString(9, tfEmail.getText().trim());

            int rows = psInsert.executeUpdate();
            lbStatus.setText(rows == 1 ? "Insert OK" : "Insert failed");
        } catch (SQLException ex) {
            lbStatus.setText("Insert error: " + ex.getMessage());
        }
    }

    private void update() {
        try {
            psUpdate.setString(1, tfLastName.getText().trim());
            psUpdate.setString(2, tfFirstName.getText().trim());
            psUpdate.setString(3, tfMI.getText().trim());
            psUpdate.setString(4, tfAddress.getText().trim());
            psUpdate.setString(5, tfCity.getText().trim());
            psUpdate.setString(6, tfState.getText().trim());
            psUpdate.setString(7, tfPhone.getText().trim());
            psUpdate.setString(8, tfEmail.getText().trim());
            psUpdate.setString(9, tfID.getText().trim());

            int rows = psUpdate.executeUpdate();
            lbStatus.setText(rows == 1 ? "Update OK" : "ID not found");
        } catch (SQLException ex) {
            lbStatus.setText("Update error: " + ex.getMessage());
        }
    }

    private void clear() {
        tfLastName.clear();  tfFirstName.clear(); tfMI.clear();
        tfAddress.clear();   tfCity.clear();      tfState.clear();
        tfPhone.clear();     tfEmail.clear();
    }

    public static void main(String[] args) { launch(args); }
}
