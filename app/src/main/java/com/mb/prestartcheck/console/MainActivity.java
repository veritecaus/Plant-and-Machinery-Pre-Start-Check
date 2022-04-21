package com.mb.prestartcheck.console;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppExecutorService;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionTypes;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Roles;
import com.mb.prestartcheck.Sections;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.WorkerCreateDailyLog;
import com.mb.prestartcheck.WorkerEmailBypass;
import com.mb.prestartcheck.WorkerEmailResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements DialogFragmentListener {


    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the thread pool to 10.
        AppExecutorService.getInstance().setExecutorService(Executors.newFixedThreadPool(10));

        if (App.getInstance().getSettings().size() == 0) {
            //Once off initalization.
            App.getInstance().getSettings().readFromAppPreferences(this);
        }
        //Ensure that  the daily log is opened  for writing.
        AppLog.getInstance().writeDailyTextFile("**MainActivity onCreate.");

        Sections.getInstance().addObserver(this);
        Roles.getInstance().addObserver(this);
        Operators.getInstance().addObserver(this);
        Supervisors.getInstance().addObserver(this);
        QuestionTypes.getInstance().addObserver(this);


        initApp();

        setContentView(R.layout.activity_main);

        this.navigationView = this.findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_main_nav_left);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_section_cover,
                R.id.nav_question_ack,
                R.id.nav_question_options,
                R.id.nav_question_yes_no,
                R.id.nav_question_multiple_choice,
                R.id.nav_summary,
                R.id.nav_interlock_device,
                R.id.nav_logout,
                R.id.nav_login_supervisor,
                R.id.nav_risk_notice)
                .setOpenableLayout(drawer)
                //.setDrawerLayout(drawer)
                .build();

        setAppVersion();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        AppLog.getInstance().writeDailyTextFile(AppLog.getInstance().setFileName() + ",,,MainActiviity,,, "+
//                (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())));
     }

    @Override
    protected void onStart() {
        super.onStart();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(this.navigationView, navController);

    }

    @Override
    protected void onDestroy() {
        AppLog.getInstance().print("MainActivity onDestroy called.");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        AppLog.getInstance().print("MainActivity OnPause.");
        super.onPause();
    }

    @Override
    protected void onResume() {
        AppLog.getInstance().print("MainActivity resuming.");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onPositive(DialogFragment dialog, Fragment fragment) {

        if (dialog.getTag() == FragmentQuestion.DIALOG_TAG_SUPERVISOR_PIN) {
            if (fragment != null && FragmentQuestion.class.isAssignableFrom(fragment.getClass()))
                ((FragmentQuestion) fragment).operateMachine();
        } else if (dialog.getTag() == FragmentQuestion.DIALOG_TAG_MACHINE_OP) {
            if (fragment != null && FragmentQuestion.class.isAssignableFrom(fragment.getClass()))
                ((FragmentQuestion) fragment).operateMachine();
        } else if (dialog.getTag() == FragmentQuestion.DIALOG_TAG_SUPERVISOR_LOGON) {
            if (fragment != null && FragmentQuestion.class.isAssignableFrom(fragment.getClass()))
                ((FragmentQuestion) fragment).askForSupervisorLogin();

        } else if (dialog.getTag() == FragmentQuestionOptions.DIALOG_TAG_CUSTOM_RESPONSE) {
            if (fragment != null && FragmentQuestionOptions.class.isAssignableFrom(fragment.getClass())) {
                ((FragmentQuestionOptions) fragment).setOperatorResponse();
            }

        } else if (dialog.getTag() == FragmentQuestionAdminHelper.DIALOG_TAG_PERMISSION_NOTICE) {
            if (fragment != null && FragmentQuestionAdminYesNo.class.isAssignableFrom(fragment.getClass())) {
                ((FragmentQuestionAdminYesNo) fragment).askForPermission();
            } else if (fragment != null && FragmentQuestionAdminMul.class.isAssignableFrom(fragment.getClass())) {
                ((FragmentQuestionAdminMul) fragment).askForPermission();
            } else if (fragment != null && FragmentQuestionAdminOptions.class.isAssignableFrom(fragment.getClass())) {
                ((FragmentQuestionAdminOptions) fragment).askForPermission();
            } else if (fragment != null && FragmentQuestionAdminAck.class.isAssignableFrom(fragment.getClass())) {
                ((FragmentQuestionAdminAck) fragment).askForPermission();
            }
        }

    }

    @Override
    public void onNegative(DialogFragment dialog, Fragment fragment) {
        if (dialog.getTag() == FragmentQuestion.DIALOG_TAG_MACHINE_OP) {
            if (fragment != null && FragmentQuestion.class.isAssignableFrom(fragment.getClass()))
                ((FragmentQuestion) fragment).dismissedDialog();
        }

    }


    public void secureNavigation() {
        boolean hasAdminAcess = Session.getInstance().isSupervisorLoggedIn();
        boolean isLoggedIn = Session.getInstance().getUser() != null;

        final Menu menu = this.navigationView.getMenu();

        menu.findItem(R.id.drawer_menu_item_home).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_settings).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_settings_email_report_admin).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_settings_interlock_device).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_question_admin_start).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_sections).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_report).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_setting_users).setVisible(hasAdminAcess);
        menu.findItem(R.id.nav_interlock_device).setVisible(hasAdminAcess);
        menu.findItem(R.id.drawer_menu_item_logout).setVisible(isLoggedIn);


    }

    public void drawer_menu_item_logout_click_handler(MenuItem view) {
        Log.i(App.TAG, "drawer_menu_item_logout_click_handler called.");
        //When the current screen is the logout screen.

        try {

            Fragment fragment = getCurrentDisplayedFragment();
            if (fragment != null && FragmentLogout.class.isAssignableFrom(fragment.getClass())) {
                Session.getInstance().stopInterlockDeviceMonitoring();
            } else if (fragment != null && FragmentQuestion.class.isAssignableFrom(fragment.getClass())) {
                Question question = Questioner.getInstance().getQuestionerState().getCurrentQuestion();

                if (question != null && question.getAllowMachineOperation()) {
                    AppLog.getInstance().print("Logout from drawer occured while the machine was operating.");
                    ((FragmentQuestion) fragment).dismissMachineLockTimeout();
                }

            }

            AppLog.getInstance().print("Drawer logout opened the interlock device.");
            ProxyInterlockDevice.openInterlockDevice();

            Session.getInstance().logout(getApplication().getApplicationContext(), new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            drawer.closeDrawers();

                            final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                            navController.navigate(R.id.nav_home);

                        }
                    });
                }
            });

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    public void drawer_menu_item_home_click_handler(MenuItem view) {
        try {
            Questioner.getInstance().restart(true);
            drawer.closeDrawers();

            final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
            navController.navigate(R.id.nav_home);


        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }


    private void setAppVersion() {
        try {
            String strVersion = String.format("Version %s", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

            TextView textViewVersion = this.navigationView.getHeaderView(0).findViewById(R.id.textViewDrawerHeaderVersion);
            textViewVersion.setText(strVersion);
        } catch (PackageManager.NameNotFoundException ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }


    private void initApp() {
        try {

            AppLog.getInstance().print("MainActivity::initApp entered.  Starting emailing services and connect to interlock device if settings changed.");

            boolean emailResp = Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_RESPONSES));
            if (emailResp) {
                String freq = App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FREQUENCY);
                AppLog.getInstance().print("MainAcitivity is enqueing worker for emailing reponses %s.", freq);
                WorkerEmailResponse.startPeriodic(getApplicationContext(), freq);
            } else {
                AppLog.getInstance().print("MainAcitivity stopping worker from emailing reponses.");
                WorkerEmailResponse.cancelPeriodic(getApplicationContext());
            }

            boolean emailBypasses = Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_BYPASSES));
            if (emailBypasses) {
                String freq = App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FREQUENCY);
                AppLog.getInstance().print("MainActivity is enqueing worker for emailing bypasses %s.", freq);
                WorkerEmailBypass.startPeriodic(getApplicationContext(), freq);
            } else {
                AppLog.getInstance().print("MainAcitivity stopping worker from emailing bypasses.");
                WorkerEmailBypass.cancelPeriodic(getApplicationContext());
            }

            //Make a connection to the interlockdevice.
            if (Machine.getInstance().isSettingValid()) {
                String addr = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS);
                String nodeNo = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_NODE_ADDRESS);
                Integer iNodeNo = Integer.parseInt(nodeNo);

                if (!Machine.getInstance().hasComms()) {
                    ProxyInterlockDevice.connectAndInitializeInterlockDevice(addr, nodeNo);
                } else {
                    boolean hasChanged = Machine.getInstance().getCurrentFINNodeNumber() != iNodeNo ||
                            Machine.getInstance().getCurrentInterlockDeviceAddress().compareToIgnoreCase(addr) != 0;
                    if (hasChanged) {
                        AppLog.getInstance().print("Interlock device settings changed. Reconnecting.");
                        ProxyInterlockDevice.connectAndInitializeInterlockDevice(addr, nodeNo);
                    }

                }

            }

             WorkerCreateDailyLog.startPeriodic(getApplication().getApplicationContext());

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    @Nullable
    public Fragment getCurrentDisplayedFragment() {
        if (getSupportFragmentManager() != null) {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

            if (navHostFragment != null && navHostFragment.getChildFragmentManager().getFragments().size() > 0) {
                return navHostFragment.getChildFragmentManager().getFragments().get(0);
            }
        }

        return null;
    }


}