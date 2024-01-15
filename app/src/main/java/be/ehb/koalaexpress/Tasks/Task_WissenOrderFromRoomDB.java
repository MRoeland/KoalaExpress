package be.ehb.koalaexpress.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.KoalaRoomDB;

public class Task_WissenOrderFromRoomDB extends AsyncTask<KoalaDataRepository, Object, Object> {
    public KoalaDataRepository repo;

    public Context c;

    public Task_WissenOrderFromRoomDB(Context context) {
        c = context;
    }

    @Override
    protected Object doInBackground(KoalaDataRepository... koalaDataRepositories) {
        repo = koalaDataRepositories[0];

        KoalaRoomDB db = KoalaRoomDB.getInstance(c);

        db.orderLineDao().deleteAllOrderLines();
        db.winkelMandjeDao().deleteAllBaskets();

        return null;
    }
}
