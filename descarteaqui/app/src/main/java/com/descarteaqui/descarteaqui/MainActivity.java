package com.descarteaqui.descarteaqui;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.descarteaqui.descarteaqui.activities.AccountsActivity;
import com.descarteaqui.descarteaqui.controllers.App;
import com.descarteaqui.descarteaqui.controllers.UserController;
import com.descarteaqui.descarteaqui.fragments.MapsFragment;
import com.descarteaqui.descarteaqui.fragments.PetitionsFragment;
import com.descarteaqui.descarteaqui.fragments.TipFragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements  GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    private GoogleSignInAccount userInfo;
    private Profile facebookProfile;
    private TextView email;
    private ImageView photo;
    private TextView name;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));

        navigationView.setNavigationItemSelectedListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                callbackManager = CallbackManager.Factory.create();
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                String email = "" ;
                                try {
                                    email = object.optString("email");
                                    UserController.setCurrentUser(email);
                                }catch(Exception e){}
                                
                                refreshFacebookInformation(email);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,link");
                request.setParameters(parameters);
                request.executeAsync();

            }
        }, 100);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            //Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            View header = navigationView.getHeaderView(0);
            photo = (ImageView)header.findViewById(R.id.imageView);
            email = (TextView)header.findViewById(R.id.textView);
            name = (TextView)header.findViewById(R.id.nameView);

            UserController.setCurrentUser(result.getSignInAccount().getEmail());
            email.setText(result.getSignInAccount().getEmail());
            name.setText(result.getSignInAccount().getDisplayName());
            photo.setImageURI(result.getSignInAccount().getPhotoUrl());
            Picasso.with(this).load(result.getSignInAccount().getPhotoUrl())
                    .resize(115, 115)
                    .into(photo);
        }

      if (App.getInstance() != null && App.getInstance().getUserGoogleInfo() != null) {
                userInfo = App.getInstance().getUserGoogleInfo();
                //refreshScreenInformation();
            } else if (App.getInstance() != null && App.getInstance().getFacebookProfile() != null) {
                facebookProfile = App.getInstance().getFacebookProfile();
                //refreshFacebookInformation();
            }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private void refreshFacebookInformation(String emailUser){
        View header=navigationView.getHeaderView(0);
        // Bitmap bmp = BitmapFactory.decodeStream(userInfo.getPhotoUrl());
        photo = (ImageView)header.findViewById(R.id.imageView);
        email = (TextView)header.findViewById(R.id.textView);
        name = (TextView)header.findViewById(R.id.nameView);

        if(Profile.getCurrentProfile() != null) {
            name.setText("Bem vindo, " + Profile.getCurrentProfile().getName());
             email.setText(UserController.getCurrentUser());
             photo.setImageURI(Profile.getCurrentProfile().getProfilePictureUri(120, 120));

            Picasso.with(this).load(Profile.getCurrentProfile().getProfilePictureUri(120, 120))
                .resize(120, 120)
                .into(photo);
        }
    }

    private void refreshScreenInformation()  {
        View header=navigationView.getHeaderView(0);
       // Bitmap bmp = BitmapFactory.decodeStream(userInfo.getPhotoUrl());
        photo = (ImageView)header.findViewById(R.id.imageView);
        email = (TextView)header.findViewById(R.id.textView);
        name = (TextView)header.findViewById(R.id.nameView);

        email.setText(userInfo.getEmail());
        name.setText(userInfo.getDisplayName());
        photo.setImageURI(userInfo.getPhotoUrl());

        Picasso.with(this).load(userInfo.getPhotoUrl())
                .resize(120, 120)
                .into(photo);

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = null;

        int id = item.getItemId();

        switch (id){
            case R.id.nav_map:
                fragment = new MapsFragment();
                break;
            case R.id.nav_tip:
                fragment = new TipFragment();
                break;
            case R.id.nav_petitions:
                if (!UserController.isUserLogged()) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("É preciso estar logado para ter acesso ao menu de Petições.\n\nVocê deseja efetuar o login?");

                    alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            Intent intent = new Intent(getApplicationContext(), AccountsActivity.class);
                            startActivity(intent);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    fragment = new PetitionsFragment();
                }

                break;
            case R.id.nav_accounts:
                Intent intent = new Intent(this, AccountsActivity.class);
                startActivity(intent);
                break;
        }


        if (fragment != null) {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
