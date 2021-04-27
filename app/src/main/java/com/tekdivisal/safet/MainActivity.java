package com.tekdivisal.safet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    private FirebaseAuth mauth;
    private Accessories mainAccessor;
    private Menu menu;
    private MenuItem profilemenuitem, confirm_menuItem, settingmenuitem,
            edit_location_menuItem,  locate_children_menuItem;
    private Dialog password_dialogue;
    private String password_string, parent_code,user_password_;

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

        parent_code = mainAccessor.getString("user_phone_number");



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
        settingmenuitem = menu.findItem(R.id.settings);
        confirm_menuItem = menu.findItem(R.id.confirm_school);
//        edit_location_menuItem = menu.findItem(R.id.edit_location);
        locate_children_menuItem = menu.findItem(R.id.locate_children);

        password_dialogue = new Dialog(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mauth.getCurrentUser() != null){
            if(mainAccessor.getBoolean("hasChoosenSchool")){
                if(mainAccessor.getBoolean("isverified")){
                    confirm_menuItem.setTitle("Undo school confirmation");
                    manager.beginTransaction().replace(R.id.container, new Home()).addToBackStack("home").commit();
                }else{
                    profilemenuitem.setVisible(false);
//                    edit_location_menuItem.setVisible(false);
                    locate_children_menuItem.setVisible(false);
                    settingmenuitem.setVisible(false);
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
        MenuItem notifications = menu.findItem(R.id.notifications);

        if(mainAccessor.getBoolean("isverified")){
            notifications.setOnMenuItemClickListener(item -> {
                startActivity(new Intent(MainActivity.this, Notifications.class));
                return false;
            });
        }else{
            notifications.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.notifications) {
//            startActivity(new Intent(MainActivity.this, Notifications.class));
//            return true;
//        }

        if (id == R.id.messages){
            startActivity(new Intent(MainActivity.this, Messages_Activity.class));
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
//            if(mainAccessor.getBoolean("isPasswordCreated")){
//                Show_password_Dialogue(MainActivity.this);
//            }else{
//                Toast.makeText(MainActivity.this, "Create password", Toast.LENGTH_LONG).show();
//                manager.beginTransaction().replace(R.id.container, new Settings()).commit();
//            }
//                manager.beginTransaction().replace(R.id.container, new Locate_Children()).commit();

            manager.beginTransaction().replace(R.id.container, new Locate_Children()).addToBackStack("locate").commit();

        }
        else if (id == R.id.profile) {
            manager.beginTransaction().replace(R.id.container, new Profile()).addToBackStack("profile").commit();

        }

//        else if (id == R.id.edit_location) {
//            if(mainAccessor.getBoolean("isverified")){
//                manager.beginTransaction().replace(R.id.container, new Edit_Location()).commit();
//            }else{
//                Toast.makeText(MainActivity.this, "Confirm school", Toast.LENGTH_LONG).show();
//            }
//
//
//        }

        else if (id == R.id.settings) {
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
            logout.setNegativeButton("Sign out", (dialog, which) -> {
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
            });

            logout.setPositiveButton("Stay", (dialog, which) -> dialog.cancel());
            logout.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Show_password_Dialogue(final FragmentActivity activity) {
        final TextView cancelpopup,success_message;
        final EditText password_editText;
        final Button done_button;
        final ProgressBar loading;

        password_dialogue.setContentView(R.layout.enter_password_dialogue);
        cancelpopup = (TextView)password_dialogue.findViewById(R.id.cancel);
        password_editText = (EditText) password_dialogue.findViewById(R.id.password_edittext);
        success_message = (TextView)password_dialogue.findViewById(R.id.success_message);
        done_button = (Button)password_dialogue.findViewById(R.id.done_button);
        loading = (ProgressBar) password_dialogue.findViewById(R.id.loading);

        cancelpopup.setOnClickListener(v -> password_dialogue.dismiss());


        done_button.setOnClickListener(v -> {
            if(isNetworkAvailable()){
                password_string = password_editText.getText().toString().trim();
                if(!password_string.equals("")){
                    loading.setVisibility(View.VISIBLE);

//                        Verify_password();
                    try {
                        DatabaseReference get_password = FirebaseDatabase.getInstance().getReference("passwords").child(parent_code);
                        get_password.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot child : dataSnapshot.getChildren()){
                                        if(child.getKey().equals("password")){
                                            user_password_ = child.getValue().toString();
                                        }
                                        else{
//                                              Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    if(user_password_.equals(password_string)){
                                        loading.setVisibility(View.GONE);
                                        success_message.setText("Code Accepted");
                                        success_message.setVisibility(View.VISIBLE);
                                        password_dialogue.dismiss();
                                        manager.beginTransaction().replace(R.id.container, new Locate_Children()).addToBackStack("locate").commit();
                                    }else{
                                        loading.setVisibility(View.GONE);
                                        success_message.setText("Invalid password");
                                        success_message.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        success_message.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainActivity.this,"Cancelled",Toast.LENGTH_LONG).show();

                            }
                        });
                    }catch (NullPointerException e){

                    }
                }else{
                    loading.setVisibility(View.GONE);
                    success_message.setText("Invalid password");
                    success_message.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    success_message.setVisibility(View.VISIBLE);
                }
            }else{
                success_message.setText("No internet connection");
                success_message.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                success_message.setVisibility(View.VISIBLE);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(password_dialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        }
        password_dialogue.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}