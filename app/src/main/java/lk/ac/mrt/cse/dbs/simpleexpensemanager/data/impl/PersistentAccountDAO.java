package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
//170452C

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBconstants;


public class PersistentAccountDAO implements AccountDAO {
    private SQLiteOpenHelper dbhelper;

    public PersistentAccountDAO(SQLiteOpenHelper dbhelper){
        this.dbhelper = dbhelper;
    }


    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();
        Cursor cur_point = sqdb.rawQuery("SELECT " + DBconstants.Account_AccNo + " FROM "
                + DBconstants.Account_Table, null);
        ArrayList<String> accNumbers = new ArrayList<>();
        for (cur_point.moveToFirst(); !cur_point.isAfterLast(); cur_point.moveToNext()) {
            accNumbers.add(cur_point.getString(0));
        }
        cur_point.close();
        return accNumbers;

    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();
        Cursor cur_point = sqdb.rawQuery("SELECT * FROM " + DBconstants.Account_Table, null);
        ArrayList<Account> accounts = new ArrayList<>();
        for (cur_point.moveToFirst(); !cur_point.isAfterLast(); cur_point.moveToNext()) {
            Account account = new Account(cur_point.getString(0), cur_point.getString(1),
                    cur_point.getString(2), cur_point.getDouble(3));
            accounts.add(account);
        }
        cur_point.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();

        Cursor cur_point = sqdb.rawQuery("SELECT * FROM " + DBconstants.Account_Table + " WHERE " + DBconstants.Account_AccNo + "=?;", new String[]{accountNo});
        Account account;
        if (cur_point.moveToFirst()) {
            account = new Account(cur_point.getString(0), cur_point.getString(1),
                    cur_point.getString(2), cur_point.getDouble(3));
        } else {
            throw new InvalidAccountException("Account " + accountNo + " is invalid");
        }
        cur_point.close();
        return account;

    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBconstants.Account_AccNo, account.getAccountNo());
        values.put(DBconstants.Account_Bankname, account.getBankName());
        values.put(DBconstants.Account_HolderName, account.getAccountHolderName());
        values.put(DBconstants.Account_Balance, account.getBalance());

        sqdb.insert(DBconstants.Account_Table, null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        Cursor cur_point = sqdb.rawQuery("SELECT * FROM " + DBconstants.Account_Table + " WHERE " + DBconstants.Account_AccNo + "=?;", new String[]{accountNo});
        if (cur_point.moveToFirst()) {
            sqdb.delete(DBconstants.Account_Table, DBconstants.Account_AccNo + " = ?", new String[]{accountNo});
        } else {
            throw new InvalidAccountException("Account " + accountNo + " is invalid");
        }
        cur_point.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        if (accountNo == null) throw new InvalidAccountException("Account was not selected");

        sqdb.beginTransaction();
        Account account = getAccount(accountNo);

        if (account != null) {
            double newAmount;
            if (expenseType == ExpenseType.EXPENSE) {
                newAmount = account.getBalance() - amount;
            } else if (expenseType == ExpenseType.INCOME) {
                newAmount = account.getBalance() + amount;
            } else {
                throw new InvalidAccountException("Unknown Expense Type");
            }

            if (newAmount < 0){
                throw  new InvalidAccountException("Insufficient balance. (" + account.getBalance() + " in the account)");
            }

            sqdb.execSQL("UPDATE " + DBconstants.Account_Table + " SET "
                            + DBconstants.Account_Balance + " = ? WHERE " +
                            DBconstants.Account_AccNo + " = ?",
                    new String[]{Double.toString(newAmount), accountNo});
            sqdb.endTransaction();
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
    }
}

