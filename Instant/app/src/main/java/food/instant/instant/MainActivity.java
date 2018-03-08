package food.instant.instant;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

import java.io.IOException;
import java.net.URI;

import static food.instant.instant.HttpRequests.HttpGET;
import static food.instant.instant.HttpRequests.HttpPostRestaurant;

public class MainActivity extends AppCompatActivity implements user_home_maps.OnFragmentInteractionListener, user_home_orders.OnFragmentInteractionListener, user_home.OnFragmentInteractionListener,user_home_restaurant.OnFragmentInteractionListener, user_home_search.OnFragmentInteractionListener, admin_home.OnFragmentInteractionListener, vendor_analytics.OnFragmentInteractionListener, vendor_edit_menu.OnFragmentInteractionListener, vendor_home.OnFragmentInteractionListener, vendor_orders.OnFragmentInteractionListener, vendor_restaurant_details.OnFragmentInteractionListener, OrderAddNew.OnFragmentInteractionListener, OrderViewAll.OnFragmentInteractionListener, VendorCompletedOrdersFragment.OnFragmentInteractionListener, VendorPendingOrdersFragment.OnFragmentInteractionListener {

    private Context c;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c = this;

        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
       // HttpPostRestaurant("http://proj-309-sd-4.cs.iastate.edu/demo/addRestaurant","");
        //HttpGET("getRestaurants");
        //This loads the starting fragment
        chooseStartingFragment(c);
        //Starting Fragment loaded

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final NavigationView mNavigationView = findViewById(R.id.nav_view);

        //helper method to hide login/logout buttons
        chooseNavDrawer(mNavigationView, c);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean close = true;
                switch (item.getItemId())
                {
                    case(R.id.nav_login):
                        Intent loginIntent = new Intent(c, LoginActivity.class);
                        c.startActivity(loginIntent);
                        close = false;
                        break;
                    case(R.id.nav_logout):
                        SaveSharedPreference.logout(c);
                        chooseNavDrawer(mNavigationView, c);
                        close = false;
                        break;
                    case(R.id.nav_map):
                        swapFragments(new user_home_maps());
                        break;
                    case(R.id.nav_search):
                        swapFragments(new user_home_search());
                        break;
                    case(R.id.nav_orders_user):
                        swapFragments(new user_home_orders());
                        break;
                    case(R.id.nav_add_order):
                        swapFragments(new OrderAddNew());
                        break;
                    case(R.id.nav_view_orders):
                        swapFragments(new OrderViewAll());
                        break;
                    case(R.id.nav_account):
                        break;
                    case(R.id.nav_home_admin):
                        swapFragments(new admin_home());
                        break;
                    case(R.id.nav_vendor_analytics):
                        swapFragments(new vendor_analytics());
                        break;
                    case(R.id.nav_vendor_edit_menu):
                        swapFragments(new vendor_edit_menu());
                        break;
                    case(R.id.nav_home_vendor):
                        swapFragments(new vendor_home());
                        break;
                    case(R.id.nav_vendor_orders):
                        swapFragments(new vendor_orders());
                        break;
                    case(R.id.nav_vendor_restaurant_details):
                        swapFragments(new vendor_restaurant_details());
                        break;
                    default:
                        Log.d(TAG, "#########Default case hit on the navigation drawer switch!#########");
                }
                if(close) {
                    mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        final NavigationView mNavigationView = findViewById(R.id.nav_view);
        chooseNavDrawer(mNavigationView, c);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chooseStartingFragment(Context cxt)
    {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();

        //if a vendor is logged in
        if(SaveSharedPreference.isLoggedIn(cxt) && SaveSharedPreference.getType(cxt).equals("Vendor"))
        {
            transaction.add(R.id.content_frame, new vendor_home());
        }
        //if an admin is logged in
        else if(SaveSharedPreference.isLoggedIn(cxt) && SaveSharedPreference.getType(cxt).equals("Admin"))
        {
            transaction.add(R.id.content_frame, new admin_home());
        }
        //else user is logged out or a customer is logged in
        else
        {
            transaction.add(R.id.content_frame, new user_home());
        }
        transaction.commit();
    }

    private void chooseNavDrawer(NavigationView mNavigationView, Context cxt)
    {
        TextView welcomeMsg = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_message);
        //if customer is logged in
        if(SaveSharedPreference.isLoggedIn(cxt) && SaveSharedPreference.getType(cxt).equals("Customer"))
        {
            loginDefault(mNavigationView, cxt);
        }
        //if a vendor is logged in
        else if(SaveSharedPreference.isLoggedIn(cxt) && SaveSharedPreference.getType(cxt).equals("Vendor"))
        {
            loginVendor(mNavigationView, cxt);
            swapFragments(new vendor_home());
        }
        //if an admin is logged in
        else if(SaveSharedPreference.isLoggedIn(cxt) && SaveSharedPreference.getType(cxt).equals("Admin"))
        {
            loginAdmin(mNavigationView, cxt);
            swapFragments(new admin_home());
        }
        //else user is logged out
        else
        {
            logoutDefault(mNavigationView);
        }
    }

    public void loginAdmin(NavigationView mNavigationView, Context cxt)
    {
        logoutDefault(mNavigationView);

        mNavigationView.getMenu().findItem(R.id.nav_home_admin).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_home_admin).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_logout).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_home_user).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_home_user).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_search).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_search).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_map).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_map).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_login).setEnabled(false);

        ((TextView)mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_message)).setText("Welcome, " + SaveSharedPreference.getFirstName(cxt) + " " + SaveSharedPreference.getLastName(cxt));
    }

    public void loginVendor(NavigationView mNavigationView, Context cxt)
    {
        logoutDefault(mNavigationView);

        mNavigationView.getMenu().findItem(R.id.nav_home_vendor).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_home_vendor).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_logout).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_restaurant_details).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_restaurant_details).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_edit_menu).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_edit_menu).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_orders).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_orders).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_analytics).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_analytics).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_home_user).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_home_user).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_search).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_search).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_map).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_map).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_login).setEnabled(false);

        ((TextView)mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_message)).setText("Welcome, " + SaveSharedPreference.getFirstName(cxt) + " " + SaveSharedPreference.getLastName(cxt));
    }

    public void loginDefault(NavigationView mNavigationView, Context cxt)
    {
        logoutDefault(mNavigationView);

        mNavigationView.getMenu().findItem(R.id.nav_account).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_account).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_orders_user).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_orders_user).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_login).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_home_user).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_home_user).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_logout).setEnabled(true);

        ((TextView)mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_message)).setText("Welcome, " + SaveSharedPreference.getFirstName(cxt) + " " + SaveSharedPreference.getLastName(cxt));
    }

    public void logoutDefault(NavigationView mNavigationView)
    {
        //clear user items
        mNavigationView.getMenu().findItem(R.id.nav_account).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_account).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_orders_user).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_orders_user).setEnabled(false);

        //clear vendor items
        mNavigationView.getMenu().findItem(R.id.nav_home_vendor).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_home_vendor).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_restaurant_details).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_restaurant_details).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_edit_menu).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_edit_menu).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_orders).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_orders).setEnabled(false);

        mNavigationView.getMenu().findItem(R.id.nav_vendor_analytics).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_vendor_analytics).setEnabled(false);

        //clear admin items
        mNavigationView.getMenu().findItem(R.id.nav_home_admin).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_home_admin).setEnabled(false);

        //clear items common to any logged in account
        mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_logout).setEnabled(false);

        //re-show items that are viewable by default
        mNavigationView.getMenu().findItem(R.id.nav_home_user).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_home_user).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_search).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_search).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_map).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_map).setEnabled(true);

        mNavigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_login).setEnabled(true);

        ((TextView)mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_message)).setText("Welcome");
        swapFragments(new user_home());
    }

    public void swapFragments(Fragment obj)
    {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_frame, obj)
                    .addToBackStack(null)
                    .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        {}
    }
}
