package com.tekdivisal.safet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    private FirebaseAuth mauth;
    private Accessories mainAccessor;
    private Menu menu;
    private MenuItem profilemenuitem, confirm_menuItem, edit_location_menuItem,
    locate_children_menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        manager = getSupportFragmentManager();
        mauth = FirebaseAuth.getInstance();
        mainAccessor = new Accessories(MainActivity.this);


        //
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // getting the menu from the navigation item;
        menu = navigationView.getMenu();
//        getting the menuitem that i want to change
        profilemenuitem = menu.findItem(R.id.profile);
        confirm_menuItem = menu.findItem(R.id.confirm_school);
        edit_location_menuItem = menu.findItem(R.id.edit_location);
        locate_children_menuItem = menu.findItem(R.id.locate_children);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mauth.getCurrentUser() != null){
            if(mainAccessor.getBoolean("hasChoosenSchool")){
                if(mainAccessor.getBoolean("isverified")){
                    confirm_menuItem.setTitle("Undo school confirmation");
                    manager.beginTransaction().replace(R.id.container, new Home()).commit();
                }else{
                    profilemenuitem.setVisible(false);
//                    edit_location_menuItem.setVisible(false);
                    locate_children_menuItem.setVisible(false);
                    manager.beginTransaction().replace(R.id.container, new Home()).commit();
                }
            }else{
                startActivity(new Intent(MainActivity.this,Select_School.class));
            }

        }else{
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notifications) {
            startActivity(new Intent(MainActivity.this, Notifications.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            manager.beginTransaction().replace(R.id.container, new Home()).commit();
        }

        else if (id == R.id.locate_children) {
                manager.beginTransaction().replace(R.id.container, new Locate_Children()).commit();
        }
        else if (id == R.id.profile) {
            manager.beginTransaction().replace(R.id.container, new Profile()).commit();

        } else if (id == R.id.edit_location) {
            if(mainAccessor.getBoolean("isverified")){
                manager.beginTransaction().replace(R.id.container, new Edit_Location()).commit();
            }else{
                Toast.makeText(MainActivity.this, "Confirm school", Toast.LENGTH_LONG).show();
            }


        } else if (id == R.id.settings) {
            manager.beginTransaction().replace(R.id.container, new Settings()).commit();

        } else if (id == R.id.about_school) {
            manager.beginTransaction().replace(R.id.container, new Contact_school()).commit();
        }

        else if(id == R.id.confirm_school){
            if(mainAccessor.getBoolean("isverified")){
                Toast.makeText(MainActivity.this, "Unconfirmed", Toast.LENGTH_LONG).show();
                mainAccessor.put("isverified", false);
                confirm_menuItem.setTitle("Confirm school");
                            Intent restart = new Intent(MainActivity.this, MainActivity.class);
                            restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(restart);
            }else{
                startActivity(new Intent(MainActivity.this, Verify_School.class));
            }
        }

        else if(id == R.id.change_school){
            mainAccessor.put("hasChoosenSchool",false);
            mainAccessor.put("isverified", false);
            Intent chage_number = new Intent(MainActivity.this, Select_School.class);
            chage_number.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(chage_number);
        }

        else if (id == R.id.logout) {
            final AlertDialog.Builder logout = new AlertDialog.Builder(MainActivity.this, R.style.Myalert);
            logout.setTitle("Signing Out?");
            logout.setMessage("Leaving us? Please reconsider.");
            logout.setNegativeButton("Sign out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                        logout here
                    if(isNetworkAvailable()){
                        FirebaseAuth.getInstance().signOut();
                        mainAccessor.put("isverified", false);
                        mainAccessor.put("hasChoosenSchool",false);
                        mainAccessor.clearStore();
                        startActivity(new Intent(MainActivity.this,Login.class));
                    }else{
                        Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                    }
                }
            });

            logout.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            logout.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
