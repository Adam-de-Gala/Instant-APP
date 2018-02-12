package food.instant.instant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static food.instant.instant.HttpRequests.HttpGET;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link user_home_restaurant.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link user_home_restaurant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user_home_restaurant extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Restaurant restaurant;
    private static class RestaurantHandler extends Handler {
        /***************************************************************************************
         *    Title: Stack Overflow Answer to Question about static handlers
         *    Author: Tomasz Niedabylski
         *    Date: July 10, 2012
         *    Availability: https://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
         ***************************************************************************************/
        private final WeakReference<user_home_restaurant> restaurantFragment;
        public RestaurantHandler(user_home_restaurant restaurantFragment) {
            this.restaurantFragment = new WeakReference<user_home_restaurant>(restaurantFragment);
        }
        /*** End Code***/
        @Override
        public void handleMessage(Message msg) {
            user_home_restaurant restaurant = restaurantFragment.get();
            if (restaurant != null) {
                JSONArray response = null;
                try {
                    response = ((JSONObject) msg.obj).getJSONArray("Food");
                    ArrayList<Food[]> foodByCategory = new ArrayList<Food[]>();
                    HashMap<Integer,String> categoryIndicies = new HashMap<Integer,String>();
                    for(int i=0;i<response.length();i++){
                        ((JSONObject)response.get(i)).get("Food_Tags_Main");
                    }
                    restaurant.updateMenuList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @SuppressLint("ValidFragment")
    public user_home_restaurant(Restaurant restaurant) {
        this.restaurant = new Restaurant(restaurant);
    }
    public user_home_restaurant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment user_home_restaurant.
     */
    // TODO: Rename and change types and number of parameters
    public static user_home_restaurant newInstance(String param1, String param2) {
        user_home_restaurant fragment = new user_home_restaurant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public void updateMenuList(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home_restaurant, container, false);
        Button directions = view.findViewById(R.id.directions);
        directions.setText(restaurant.getAddress());
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).swapFragments(new user_home_maps(new LatLng(restaurant.getLatitude(),restaurant.getLongitude())));
            }
        });
        TextView name = view.findViewById(R.id.restaurant_name);
        name.setText(restaurant.getName());
        TextView description = view.findViewById(R.id.description);
        description.setText("This is a description of this restaurant");
        TextView operatingHours = view.findViewById(R.id.restaurant_hours);
        operatingHours.setText("Operating Hours\n" +
                                "\t"+ "Monday"+"\n"+
                                "\t"+"Tuesday"+ "\n"+
                                "\t"+"Wednesday"+"\n"+
                                "\t"+"Thursday"+"\n"+
                                "\t"+"Friday"+"\n"+
                                "\t"+"Saturday"+"\n"+
                                "\t"+"Sunday"+"\n");


        //HttpGET("getRestaurantMenu");

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
