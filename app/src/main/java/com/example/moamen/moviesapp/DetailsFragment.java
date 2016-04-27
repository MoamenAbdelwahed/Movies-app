package com.example.moamen.moviesapp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    Movies currentMovie;
    TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;

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
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ArrayList<Trailers> trailers = new ArrayList();

        trailers.add(new Trailers("as__as"));

        trailersAdapter = new TrailersAdapter((AppCompatActivity)getActivity(),trailers);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(trailersAdapter);
        fetchTrailers fetchTrailer = new fetchTrailers();
        fetchTrailer.execute();

        final ArrayList<Reviews> reviews = new ArrayList();
        reviews.add(new Reviews("as__as"));
        reviewsAdapter = new ReviewsAdapter((AppCompatActivity)getActivity(),reviews);
        ListView listView2 = (ListView) view.findViewById(R.id.listView2);
        listView2.setAdapter(reviewsAdapter);
        fetchReviews fetchReviews = new fetchReviews();
        fetchReviews.execute();

        // make ListView Scrollable
        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listView2.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // Favourites
        Button favouriteButton = (Button) view.findViewById(R.id.favourite_button);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouriteContract.FavouriteDbHelper mDbHelper = new FavouriteContract().new FavouriteDbHelper(getContext());
                SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
                Cursor c = dbRead.query(FavouriteContract.FavouriteMovies.TABLE_NAME,new String[]{FavouriteContract.FavouriteMovies._ID}, FavouriteContract.FavouriteMovies.COLUMN_NAME_MOVIE_ID  + "=?",new String[]{currentMovie.getId()},null,null,null);
                c.moveToFirst();
                if(c.getCount() == 0) {
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FavouriteContract.FavouriteMovies.COLUMN_NAME_MOVIE_ID, currentMovie.getId());

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(
                            FavouriteContract.FavouriteMovies.TABLE_NAME,
                            FavouriteContract.FavouriteMovies.COLUMN_NAME_MOVIE_ID,
                            values);
                }
                else {
                    Toast.makeText(getContext(),"Already in your favourite list",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, null);
        currentMovie = (Movies) getArguments().getSerializable("MovieObj");

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + currentMovie.getPoster()).into((ImageView) view.findViewById(R.id.posterView));

        ((TextView) view.findViewById(R.id.titleView)).setText(currentMovie.getTitle() + "\n\n" + currentMovie.getReleaseDate() + "\n\n" + currentMovie.getVoteAverage());

        ((TextView) view.findViewById(R.id.overviewView)).setText(currentMovie.getOverview() + '\n');
    return view;
    }

    public String[] updateReviews(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsStr;
        Gson reviewsJson = null;
        String[] reviewsDetails = new String[0];
        try {
            String baseUrl = "https://api.themoviedb.org/3/movie/" + currentMovie.getId() + "/reviews";
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
            reviewsStr = buffer.toString();
            try {
                JSONObject reviewsObject = new JSONObject(reviewsStr);
                JSONArray allResults = reviewsObject.getJSONArray("results");
                reviewsDetails = new String[allResults.length()];
                for (int i = 0; i < allResults.length(); i++) {
                    JSONObject review = allResults.getJSONObject(i);
                    reviewsDetails[i] = review.getString("author") + "__" + review.getString("content");
                }

            } catch (JSONException e) {
                Log.e("MoviesApp", "ERROR parsing review JSON: " + e);
            }
        }catch (IOException e) {
            Log.e("MoviesApp", "ERROR review: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MoviesApp", "ERROR closing review stream: ", e);
                }
            }
        }
        return reviewsDetails;
    }
    public class fetchReviews extends AsyncTask<Void,Void,String[]>{
        @Override
        protected String[] doInBackground(Void... params) {
            String[] reviewsDetails = updateReviews();
            return reviewsDetails;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                reviewsAdapter.clear();
                ArrayList<Reviews> reviews = new ArrayList<Reviews>();
                for (String review: result){
                    reviewsAdapter.add(new Reviews(review));
                }
            }
        }
    }

    // TRAILERS FUNCTIONS
    public String[] updateTrailers(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailersStr;
        Gson trailersJson = null;
        String[] trailersDetails = new String[0];
        try {
            String baseUrl = "https://api.themoviedb.org/3/movie/" + currentMovie.getId() + "/videos";
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
            trailersStr = buffer.toString();
            try {
                JSONObject trailersObject = new JSONObject(trailersStr);
                JSONArray allResults = trailersObject.getJSONArray("results");
                trailersDetails = new String[allResults.length()];
                for (int i = 0; i < allResults.length(); i++) {
                    JSONObject trailer = allResults.getJSONObject(i);
                    trailersDetails[i] = trailer.getString("name") + "__" + trailer.getString("key");
                }

            } catch (JSONException e) {
                Log.e("MoviesApp", "ERROR parsing trailer JSON: " + e);
            }
        }catch (IOException e) {
            Log.e("MoviesApp", "ERROR trailer: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MoviesApp", "ERROR closing trailer stream: ", e);
                }
            }
        }
        return trailersDetails;
    }
    public class fetchTrailers extends AsyncTask<Void,Void,String[]>{
        @Override
        protected String[] doInBackground(Void... params) {
            String[] trailersDetails = updateTrailers();
            return trailersDetails;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                trailersAdapter.clear();
                ArrayList<Trailers> trailers = new ArrayList<Trailers>();
                for (String trailer: result){
                    trailersAdapter.add(new Trailers(trailer));
                }
            }
        }
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
