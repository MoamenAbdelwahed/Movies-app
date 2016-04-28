package com.example.moamen.moviesapp;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    ArrayAdapter moviesAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        final ArrayList<Movies> movies = new ArrayList<Movies>();
        movies.add(new Movies("1"+"__"+"/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"+"__"+"Dummy"+"__"+"dummy"+"__"+"dummy"+"__"+"dummy"));

        moviesAdapter = new MoviesAdapter((AppCompatActivity)getActivity(),movies);

        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(moviesAdapter);

        // AsyncTask
        fetchMovies fetchMovies= new fetchMovies();
        fetchMovies.execute("popular");

        if (view.findViewById(R.id.fragment_container_tablet) != null){
            Bundle Movieobj = new Bundle();
            Movieobj.putSerializable("MovieObj", (Serializable) moviesAdapter.getItem(1));
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(Movieobj);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_tablet,detailsFragment).commit();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle Movieobj = new Bundle();
                Movieobj.putSerializable("MovieObj", (Serializable) moviesAdapter.getItem(position));
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(Movieobj);
              if (getView().findViewById(R.id.fragment_container_tablet) != null) {
                  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailsFragment).addToBackStack("backToMain").commit();
              }
                else{
                  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_tablet, detailsFragment).addToBackStack("backToMainTablet").commit();
              }
            }
        });
    }
    public String[] update(String... params){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesStr;
        Gson moviesJson = null;
        String[] moviesDetails = new String[0];
        try {
            String baseUrl = "https://api.themoviedb.org/3/movie/" + params[0];
            String api = "?api_key=" + BuildConfig.TMDb_API_KEY;
            URL url = new URL(baseUrl.concat(api));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            moviesStr = buffer.toString();
            try {
                JSONObject moviesObject = new JSONObject(moviesStr);
                JSONArray allResults = moviesObject.getJSONArray("results");
                moviesDetails = new String[allResults.length()];
                for (int i = 0; i < allResults.length(); i++) {
                    JSONObject movie = allResults.getJSONObject(i);
                    //poster - title - overview - vote average - release date
                    moviesDetails[i] = movie.getString("id") + "__" + movie.getString("poster_path") + "__" + movie.getString("title") + "__" + movie.getString("vote_average") + "__" + movie.getString("release_date") + "__" + movie.getString("overview");
                    //Log.v("MoviesApp",moviesDetails[i]);
                }

            } catch (JSONException e) {
                Log.e("MoviesApp", "ERROR parsing JSON: " + e);
            }//Log.v("MoviesApp", "Movies info: "+ moviesStr);
            //moviesJson = new Gson();
            //moviesJson.toJson(inputStream);
            //Log.v("MoviesApp","JSON: " + moviesJson);
        }catch (IOException e) {
            Log.e("MoviesApp", "ERROR: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MoviesApp", "ERROR closing stream: ", e);
                }
            }
        }
        return moviesDetails;
    }
    public class fetchMovies extends AsyncTask<String,Void,String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String[] moviesDetails = update(params);
            //return null;
            return moviesDetails;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                moviesAdapter.clear();
                ArrayList<Movies> movies = new ArrayList<Movies>();
                for (String movie: result){
                    moviesAdapter.add(new Movies(movie));
                }
                //moviesAdapter.add(movies);
            }
        }
    }


    // favourites AsyncTask
    public String[] updateFavourites(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesStr;
        Gson moviesJson = null;

        FavouriteContract.FavouriteDbHelper mDbHelper = new FavouriteContract(). new FavouriteDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.query(FavouriteContract.FavouriteMovies.TABLE_NAME, new String[]{FavouriteContract.FavouriteMovies.COLUMN_NAME_MOVIE_ID}, null, null, null, null, null);
        c.moveToFirst();
        String[] moviesDetails = new String[c.getCount()];

        for (int i=0; i < c.getCount(); i++) {
            long favouriteId = c.getLong( c.getColumnIndexOrThrow(FavouriteContract.FavouriteMovies.COLUMN_NAME_MOVIE_ID) );
            c.moveToNext();
            try {
                String baseUrl = "https://api.themoviedb.org/3/movie/" + favouriteId;
                String api = "?api_key=" + BuildConfig.TMDb_API_KEY;
                URL url = new URL(baseUrl.concat(api));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                moviesStr = buffer.toString();
                try {
                    JSONObject moviesObject = new JSONObject(moviesStr);
                    //JSONArray allResults = moviesObject.getJSONArray("results");
                    //moviesDetails = new String[allResults.length()];
                    //for (int i = 0; i < allResults.length(); i++) {
                        //JSONObject movie = allResults.getJSONObject(i);
                        //poster - title - overview - vote average - release date
                        moviesDetails[i] = moviesObject.getString("id") + "__" + moviesObject.getString("poster_path") + "__" + moviesObject.getString("title") + "__" + moviesObject.getString("vote_average") + "__" + moviesObject.getString("release_date") + "__" + moviesObject.getString("overview");
                        //Log.v("MoviesApp",moviesDetails[i]);
                    //}

                } catch (JSONException e) {
                    Log.e("MoviesApp", "ERROR parsing JSON: " + e);
                }
            } catch (IOException e) {
                Log.e("MoviesApp", "ERROR: ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MoviesApp", "ERROR closing stream: ", e);
                    }
                }
            }
        }
        return moviesDetails;
    }
    public class fetchFavourites extends AsyncTask<Void,Void,String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            String[] moviesDetails = updateFavourites();
            return moviesDetails;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                moviesAdapter.clear();
                ArrayList<Movies> movies = new ArrayList<Movies>();
                for (String movie: result){
                    moviesAdapter.add(new Movies(movie));
                }
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View v = inflater.inflate(R.layout.your_fragment_layout, container, false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popular) {
            fetchMovies fetchMovies = new fetchMovies();
            fetchMovies.execute("popular");
        }
        else if (id == R.id.action_rate){
            fetchMovies fetchMovies = new fetchMovies();
            fetchMovies.execute("top_rated");
        }
        else if(id == R.id.action_favourite){
            fetchFavourites fetchFavourites = new fetchFavourites();
            fetchFavourites.execute();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
