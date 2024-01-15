package be.ehb.koalaexpress;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import be.ehb.koalaexpress.Tasks.Task_WissenOrderFromRoomDB;
import be.ehb.koalaexpress.databinding.ActivityMainBinding;
import be.ehb.koalaexpress.models.KoalaRoomDB;
import be.ehb.koalaexpress.models.WinkelMandje;
import be.ehb.koalaexpress.ui.Dialogs.InfoDialog;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public KoalaDataRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hiding ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = KoalaDataRepository.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_winkel, R.id.navigation_winkelmandje, R.id.navigation_vestingen)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (getIntent().getExtras() != null) {
            // Er is een param meegegeven bij het opstarten
            String intentExtra = getIntent().getStringExtra("JumpToFragment");
            if (intentExtra != null && (intentExtra.equals("WinkelMandjeSucces") ||  intentExtra.equals("WinkelMandjeAnnuleren"))) {
                // info, eerst geprobeer met navController.navigate(R.id.action_navigation_Winkel_To_Mandje);
                // maar kan dan niet meer naar eerste item in navcontroller springen
                // werkt enkel als de startdestination van navgraph aanpast. (met dank aan mijn vriend chatgpt :3 )
                /* "By programmatically changing the start destination of the navigation graph, you effectively modified the entry point
                   of the navigation flow, ensuring that the desired fragment (R.id.navigation_winkelmandje in this case) becomes the initial
                   destination. This method helps in bypassing any conflicts that might have arisen when trying to navigate directly to a specific
                   fragment after the initial setup."*/
                NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.main_navigation);
                navGraph.setStartDestination(R.id.navigation_winkelmandje);
                navController.setGraph(navGraph);

                if(intentExtra.equals("WinkelMandjeSucces")) {
                    WinkelMandje mandje = repo.mWinkelMandje.getValue();
                    String infotxt = "Uw bestelling met nummer " + mandje.mOrderId +  " is genoteerd.\n\n" +
                            "De vestiging gaat meteen aan de slag. \nBedankt voor het vertrouwen.\n\nUw betalingsreferentie is : " + repo.mWinkelMandje.getValue().mPayPalPaymentId;
                    InfoDialog dlg = new InfoDialog(infotxt, R.color.KoalaDarkGreen, R.color.KoalaYellowButton );
                    dlg.show(getSupportFragmentManager(), "InfoDialog");
                    //dialog completion tonen en winkelmandje leeg

                    repo.ClearWinkelMandjeOnCheckout();
                    repo.PaypalAccessToken = ""; // -> bij nieuw winkelmandje nieuwe token aanvragen

                    // wis laatste order uit lokale room DB
                    Task_WissenOrderFromRoomDB dbtask = new Task_WissenOrderFromRoomDB(getApplicationContext());
                    dbtask.execute(repo);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        KoalaDataRepository.getInstance().SaveOrderToRoomDB(getApplicationContext());

        super.onPause();
    }

}