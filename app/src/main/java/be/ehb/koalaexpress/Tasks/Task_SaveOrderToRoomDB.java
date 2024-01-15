package be.ehb.koalaexpress.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.dao.WinkelMandjeDao;
import be.ehb.koalaexpress.models.KoalaRoomDB;

public class Task_SaveOrderToRoomDB extends AsyncTask<KoalaDataRepository, Object, Object> {
    public KoalaDataRepository repo;

    public Context c;

    public Task_SaveOrderToRoomDB(Context context) {
        c = context;
    }

    @Override
    protected Object doInBackground(KoalaDataRepository... koalaDataRepositories) {
        repo = koalaDataRepositories[0];

        KoalaRoomDB db = KoalaRoomDB.getInstance(c);

        db.orderLineDao().deleteAllOrderLines();
        db.winkelMandjeDao().deleteAllBaskets();

        repo.mWinkelMandje.getValue().SetOrderId(1); // ordernummer komt terug na finaal opslaan naar nas koala db, mag hier altijd 1 zijn

        db.winkelMandjeDao().insertBasket(repo.mWinkelMandje.getValue());
        db.orderLineDao().insertMultipleOrderLines(repo.mWinkelMandje.getValue().mOrderLines);

        return null;
    }
}
