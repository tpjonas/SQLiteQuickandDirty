package cimdata.android.dez2017.sqlitequickanddirty;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Dies ist auch der Dateiname der SQLite DB. Die Datei-Endung ist beliebig)
    private final String DB_NAME = "database.db";
    private final String TABLE_NAME = "names";
    private final String COLUMN_ID = "_id";
    private final String COLUMN_NAME = "name";

    private SQLiteDatabase database;

    Button saveButton;
    EditText textInput;
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveButton = findViewById(R.id.btn_acmain_save);
        textInput = findViewById(R.id.etxt_acmain_input);
        list = findViewById(R.id.list_acmain_list);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(textInput.getText());

                if (text.equals("")) {
                    Toast.makeText(MainActivity.this, "Wert konnte nicht gespeichert werden.", Toast.LENGTH_SHORT).show();
                } else {
                    saveText(text);
                }

                displayValues();

                // und leeren das eingabefeld
                textInput.setText("");

                // Hier schliessen wir das soft keyboard
                // 1. Zuerst schauen wir, ob eine View im Focus ist
                View focusView = getCurrentFocus();
                if (focusView != null) {
                    // Hier holen wir uns den Input Service
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    // Hier schliessen wir das Keyboard
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
            }
        });


        createMyDatabase();
    }

    // Hier erstellen bzw. wenn sie schon existiert, öffnen wir unsere Datenbank
    private void createMyDatabase() {

        database = openOrCreateDatabase(
                DB_NAME,        // Der Name der Datenbank
                MODE_PRIVATE,   // Der Zugriffsmodus unserer Datenbank. (Wenn man sich nicht in einem "Context" befindet, muss man dem Mode ein Context. Präfix voranstellen.)
                null    // Eine Cursor wird so gut wie niemals verwendet.
        );

        // Hier erzeugen wir unsere Tabelle über ein simples SQL Statement.
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                        "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME + " TEXT " +
                        ")"
        );

    }

    private void saveText(String text) {

        // Hier legen einen Container für die Werte an.
        ContentValues values = new ContentValues();

        // Dann speichern wir die Werte als Key-Value-Paare, wobei der Spaletname der Key ist.
        values.put(COLUMN_NAME, text);

        // Zum Schluss speichern wir die Values in die Datenbank
        // ( !
        // Es gibt auch die Möglichkeit mit database.rawQuery() ein normales SQL-Statement auszuführen. No SQL-Injection safety!)
        // Dafür gibt es vorgefertigte Funkionen
        // ! )
        long lastInsertId = database.insert(
                TABLE_NAME, // Der Name der DB
                null, // Der Spaltenname, der bei einem komplett leeren Datensatz NULL entahlen soll
                values  // Die Werte, die wir speichern wollen
        );

    }

    private void displayValuesToast () {

        // Wenn wir Werte abfragen, bekommen wir einen sogenannten "Cursor" zurück.
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_NAME, // SQL
                null                 // WHERE, LIMIT etc / Prepared statements
        );


        while (cursor.moveToNext()) {

            // Um einen Wert einer Spalte abzufragen brauchen wir den Index der Spalte.
            // Diesen fragen wir mit Hilfe des Namens bei der Datenbank ab.
            // Im Grunde ist es die Nummer der Spalte von links gezählt (Im Ergebnis).
            // Das wir dies allerdings nicht immer wissen, fragen wir sie ab.
            int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
            String name = cursor.getString(columnNameIndex);

            int columnIndex = cursor.getColumnIndex(COLUMN_ID);
            int id = cursor.getInt(columnIndex);

            Toast.makeText(this,"(" + id + ") " + name, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayValues() {

        // Wenn wir Werte abfragen, bekommen wir einen sogenannten "Cursor" zurück.
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME,null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,          // cursor für das Ergebnis
                new String[] {   // FROM: Die Spaltennamen in dem Ergebnis, z.B. { "_id, "name" }
                        COLUMN_NAME, COLUMN_ID
                },
                new int[] {     // TO:   Die Resource IDs (des Layouts), auf die die Spalten gemappt werden, z.B. { R.id.id, R.id.name }
                        android.R.id.text2, android.R.id.text1
                },
                0
        );

        list.setAdapter(adapter);
    }
}
