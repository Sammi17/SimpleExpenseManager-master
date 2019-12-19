package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
//import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBconstants;

public class PersistentTransactionDAO implements TransactionDAO{
    private SQLiteOpenHelper dbhelper;

    private static SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy",
            Locale.getDefault());

    public PersistentTransactionDAO(SQLiteOpenHelper dbhelper){
        this.dbhelper = dbhelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        if (accountNo == null) return;
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBconstants.Transaction_Date, date_format.format(date));
        values.put(DBconstants.Transaction_AccNo, accountNo);
        values.put(DBconstants.Transaction_Type, expenseType.toString());
        values.put(DBconstants.Transaction_Amount, amount);
        sqdb.insert(DBconstants.Transaction_Table, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();
        Cursor cur_point = sqdb.rawQuery("SELECT * FROM " + DBconstants.Transaction_Table +
                " ORDER BY " + DBconstants.Transaction_ID + " DESC ", null);
        ArrayList<Transaction> alltransactions = new ArrayList<>();
        for (cur_point.moveToFirst(); !cur_point.isAfterLast(); cur_point.moveToNext()) {
            try {
                String expTypeStr = cur_point.getString(2);
                ExpenseType exptype = ExpenseType.EXPENSE;
                if (expTypeStr.equals(DBconstants.Type_Income)) {
                    exptype = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(date_format.parse(cur_point.getString(3)),
                        cur_point.getString(1), exptype, cur_point.getDouble(4));
                alltransactions.add(transaction);
            } catch (ParseException ignored) {
            }
        }
        cur_point.close();
        return alltransactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cur_point = database.rawQuery("SELECT * FROM " + DBconstants.Transaction_Table +
                " ORDER BY " + DBconstants.Transaction_ID + " DESC " +
                " LIMIT ?;", new String[]{Integer.toString(limit)});
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cur_point.moveToFirst(); !cur_point.isAfterLast(); cur_point.moveToNext()) {
            try {
                String expTypeStr = cur_point.getString(2);
                ExpenseType exptype = ExpenseType.EXPENSE;
                if (expTypeStr.equals(DBconstants.Type_Income)) {
                    exptype = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(date_format.parse(cur_point.getString(3)),
                        cur_point.getString(1), exptype, cur_point.getDouble(4));
                transactions.add(transaction);
            } catch (ParseException ignored) {
            }
        }
        cur_point.close();
        return transactions;
    }
}
