package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;
//170452C

import android.content.Context;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBhelper;


public class PersistentExpenseManager extends ExpenseManager {

    private DBhelper dbhelper;

    public PersistentExpenseManager(Context context) {
        dbhelper = new DBhelper(context);
        setup();
    }

    @Override
    public void setup()  {
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(dbhelper);
        setTransactionsDAO(persistentTransactionDAO);
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(dbhelper);
        setAccountsDAO(persistentAccountDAO);

        //dummy data
        //Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        //Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
       // getAccountsDAO().addAccount(dummyAcct1);
       // getAccountsDAO().addAccount(dummyAcct2);

    }
}

