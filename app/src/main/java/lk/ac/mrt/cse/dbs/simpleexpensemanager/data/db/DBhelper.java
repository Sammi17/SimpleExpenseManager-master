package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBhelper extends SQLiteOpenHelper {
    private final static String Database_Name ="170452C";
    private final static int Database_Version = 1;

    public DBhelper(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + DBconstants.Account_Table + "(" + DBconstants.Account_AccNo + " VARCHAR PRIMARY KEY," +
                DBconstants.Account_Bankname + " VARCHAR," + DBconstants.Account_HolderName + " VARCHAR," + DBconstants.Account_Balance + " NUMERIC" + ");");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + DBconstants.Transaction_Table + "(" + DBconstants.Transaction_ID + " INTEGER PRIMARY KEY," +
                DBconstants.Transaction_AccNo + " VARCHAR NOT NULL," + DBconstants.Transaction_Date + " TIMESTAMP NOT NULL," + DBconstants.Transaction_Type + " VARCHAR NOT NULL," +
                DBconstants.Transaction_Amount + " NUMERIC NOT NULL," + "FOREIGN KEY (" + DBconstants.Transaction_AccNo + ") REFERENCES "
                + DBconstants.Account_Table + "(" + DBconstants.Account_AccNo + ")" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String sql1 = "DROP TABLE IF EXISTS " + DBconstants.Transaction_Table;
        database.execSQL(sql1);

        String sql2 = "DROP TABLE IF EXISTS " + DBconstants.Account_Table;
        database.execSQL(sql2);
        onCreate(database);
    }
}
