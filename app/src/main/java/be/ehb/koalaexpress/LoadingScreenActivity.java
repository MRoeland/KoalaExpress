package be.ehb.koalaexpress;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import be.ehb.koalaexpress.Tasks.Task_OnlineCheck;

public class LoadingScreenActivity extends AppCompatActivity {

    public final String TAG = "SplashActivity";
    public KoalaDataRepository repo;
    boolean checkpermissionsiscompleted;
    int loadingTimer = 0;
    private Handler h;
    private Runnable r;

    public enum StappenInSplash {stepOnlineCheck, stepInitialseerRepo, stepCopieerLokaalInRoomDB, stepLeesMandjeUitRoomDB ,stepSplashKlaar};
    public StappenInSplash mStatusSplashScreen;

    public ProgressBar mProgressBar;
    public TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkpermissionsiscompleted = false;

        setContentView(R.layout.activity_loadingscreen);

        mProgressBar = findViewById(R.id.splashprogress);
        mInfoText = findViewById(R.id.splash_infotxt);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mProgressBar.setVisibility(View.VISIBLE);

        repo = KoalaDataRepository.getInstance();

        //Step1: start de eerste taak om te zien of App online is
        mStatusSplashScreen = StappenInSplash.stepOnlineCheck;
        Task_OnlineCheck onlineCheckTask = new Task_OnlineCheck();
        onlineCheckTask.execute(repo);
        mInfoText.setText("Check Online Status...");
    }

    @Override
    protected void onStart() {
        startLoadingScreenTimerNow();
        super.onStart();
    }

    public void startLoadingScreenTimerNow() {
        // Maak een timer die elke seconde de status checkt van de workflow

        // Elke seconde gaat er een timer af die door dit hele process loopt
        // Op elke check wordt er een functie opgeroepen, meestal om data op te halen van de db of juist weg te schrijven naar de RoomDb
        // Eens deze check gebeurt is wordt er een bijgehouden boolean op true gezet (vb. repo.mReadyInitDB)
        // Ook wordt de waarde van onze enum in de net gebeurde stap veranderdt naar de volgende stap waardoor hij bij de volgende check van alle ifs op de volgende komt

        Timer mijnTimerInActivity =new Timer();
        mijnTimerInActivity.schedule(new TimerTask() {
            @Override
            public void run() {

                //Elke if is een check of ik naar de volgende stap mag

                loadingTimer += 1;
                Log.i("Splash", "Check of laden klaar is...");
                // stap 1: doe een online check van de serverconnectie, gebeurd bij starten in oncreateview

                // stap 2: starten indien servercheck klaar is

                if(mStatusSplashScreen == StappenInSplash.stepOnlineCheck && repo.mServerStatusGechecked == true) {
                    // online beslissing is gemaakt, volgende stap initialiseer repo
                    mStatusSplashScreen = StappenInSplash.stepInitialseerRepo;
                    mInfoText.setText("Intialiseren van de data repository...");
                    if(repo.mServerIsOnline) {
                        // online, haal producten op via api calls
                        repo.InitialiseBijStart();
                    }
                    else {
                        //inlezen producten via lokale room databank

                        repo.LoadRepoFromRoomDB(getApplicationContext());
                    }
                }
                else if(mStatusSplashScreen == StappenInSplash.stepInitialseerRepo && repo.IsReady()) {
                    // stap 3: indien online, opslaan van alle opgehaalde gegevens in de roomDB lokaal

                    if(repo.mServerIsOnline) {
                        mStatusSplashScreen = StappenInSplash.stepCopieerLokaalInRoomDB;
                        mInfoText.setText("Opslaan in lokale Room DB.");
                        //opslaan in room database en ophalen images voor lokaal opslaan
                        repo.SaveRepoInRoomDB(getApplicationContext());

                    }
                    else{
                        repo.LoadOrderFromRoomDB(getApplicationContext());
                        mStatusSplashScreen = StappenInSplash.stepLeesMandjeUitRoomDB;
                    }
                }
                else if(mStatusSplashScreen == StappenInSplash.stepCopieerLokaalInRoomDB && repo.mReadyInitDB == true) {
                    mStatusSplashScreen = StappenInSplash.stepLeesMandjeUitRoomDB;
                    mInfoText.setText("Ophalen order uit Room DB.");
                    // stap 4: haal de data van de vorige onafgeronde order op uit de roomDB

                    repo.LoadOrderFromRoomDB(getApplicationContext());
                }
                else if(mStatusSplashScreen == StappenInSplash.stepLeesMandjeUitRoomDB && repo.mFinishedReadingBasketFromRoomDB == true){

                    mStatusSplashScreen = StappenInSplash.stepSplashKlaar;
                    mInfoText.setText("Klaar.");
                }
                else if (mStatusSplashScreen == StappenInSplash.stepSplashKlaar) {
                    // stap 5: repository is volledig geinitialiseerd, kan nu naar main activity

                    Log.i("Splash", "Repo is klaar, nu navigeren naar main activity");
                    this.cancel();
                    Intent switchActivityIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(switchActivityIntent);
                }
                mInfoText.setText(mInfoText.getText()+".");
            }
        }, 500, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgressBar.setVisibility(View.GONE);
    }

    public void onBackPressed() {
        // this method is used to finish the activity
        // when user enters the correct password
        super.onBackPressed();
        this.finishAffinity();
    }
}