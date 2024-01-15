package be.ehb.koalaexpress.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.Locale;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.models.KoalaRoomDB;
import be.ehb.koalaexpress.models.Location;
import be.ehb.koalaexpress.models.OrderLine;
import be.ehb.koalaexpress.models.WinkelMandje;

public class Task_LoadUnfinishedOrder extends AsyncTask<KoalaDataRepository, Object, Object> {
    public KoalaDataRepository repo;

    WinkelMandje mandje;
    public Context c;

    public Task_LoadUnfinishedOrder(Context context) {
        c = context;
    }

    @Override
    protected Object doInBackground(KoalaDataRepository... koalaDataRepositories) {
        repo = koalaDataRepositories[0];

        KoalaRoomDB db = KoalaRoomDB.getInstance(c);

        List<WinkelMandje> m;
        m = db.winkelMandjeDao().getAllBaskets();
        if(m.size() > 0){
            mandje = m.get(0);

            mandje.mOrderLines = db.orderLineDao().getAllOrderLinesForBasket(mandje.mOrderId);
        }
        else {
            mandje = null;
        }

        return mandje;
    }

    @Override
    protected void onPostExecute(Object o){
        super.onPostExecute(o);
        if(mandje != null){
            if(mandje.mPickUpStoreId > 0) {
                //location opgeslagen
                Location vestiging = repo.koalaLocations.getLocationWithId(mandje.mPickUpStoreId);
                repo.mVestiging.setValue(vestiging);
            }
            repo.mWinkelMandje.setValue(mandje);
        }
        repo.mFinishedReadingBasketFromRoomDB = true;
    }
}
