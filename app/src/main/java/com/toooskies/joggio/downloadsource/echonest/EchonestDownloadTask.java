package com.toooskies.joggio.downloadsource.echonest;

import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.toooskies.joggio.ISongInfoListener;
import com.toooskies.joggio.SongInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.toooskies.joggio.R.string.echonest_api_song_search_artist_param;
import static com.toooskies.joggio.R.string.echonest_api_song_search_base_url;
import static com.toooskies.joggio.R.string.echonest_api_song_search_bucket_param;
import static com.toooskies.joggio.R.string.echonest_api_song_search_bucket_value;
import static com.toooskies.joggio.R.string.echonest_api_song_search_title_param;
import static com.toooskies.joggio.R.string.log_tag_song_info_echonest;

/**
 * Created by toooskies on 1/2/2016.
 */
public class EchonestDownloadTask extends AsyncTask<SongInfo, Void, ArrayList<SongInfo>>
{

    private Resources mResources;
    /**
     * Constructor.  Needs resources to function. 
     * @param resources
     */
    public EchonestDownloadTask(Resources resources)
    {
        mResources = resources;
    }
    
   
    /**
     * Downloads the song information we need.
     * @param params
     * @return
     */
    @Override
    protected ArrayList<SongInfo> doInBackground(SongInfo... params)
    {
        ArrayList<SongInfo> songs = new ArrayList<SongInfo>(params.length);

        for (int i = 0; i < params.length; i++)
        {
            DownloadSongData(params[i]);
            songs.add(params[i]);
        }

        return songs;
    }

    private void DownloadSongData(SongInfo Song)
    {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Modify artist and title to be compatible with lookup
            String artist = GetURLSubstring(Song.Artist);
            String title = GetURLSubstring(Song.Title);

            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri builtUri = Uri.parse(mResources.getString(echonest_api_song_search_base_url)).buildUpon()
                    .appendQueryParameter(mResources.getString(echonest_api_song_search_title_param), title)
                    .appendQueryParameter(mResources.getString(echonest_api_song_search_artist_param), artist)
                    .appendQueryParameter(mResources.getString(echonest_api_song_search_bucket_param),
                            mResources.getString(echonest_api_song_search_bucket_value))
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
            }
            AddRawData(Song, buffer.toString());
        } catch (IOException e) {
            Log.e(mResources.getString(log_tag_song_info_echonest), "Error ", e);
            // If the code didn't successfully get the song data, there's no point in attempting
            // to parse it.
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException e) {
                    Log.e(mResources.getString(log_tag_song_info_echonest), "Error closing stream", e);
                }
            }
        }
    }

    private void AddRawData(SongInfo Song, String Buffer)
    {
        Song.RawData = new EchonestSongData(Buffer);
    }

    /**
     * Modify text to be part of an URL.
     * @param Text The text to be modified.
     * @return A string.
     */
    private String GetURLSubstring(String Text)
    {
        String encoded_text = Text.toLowerCase();
        try
        {
            encoded_text = java.net.URLEncoder.encode(encoded_text, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e(mResources.getString(log_tag_song_info_echonest), "String encoding error", e);
        }

        return encoded_text;
    }

    @Override
    protected void onPostExecute(ArrayList<SongInfo> Songs)
    {
        fireSongInfoUpdate(Songs);
    }

    // Our collection of classes that are subscribed as listeners
    protected ArrayList<ISongInfoListener> mListeners;

    // Method for listener classes to register themselves
    public void addSongInfoListener(ISongInfoListener listener)
    {
        if (mListeners == null)
        {
            mListeners = new ArrayList<>();
        }
        if (!mListeners.contains(listener))
        {
            mListeners.add(listener);
        }
    }

    // "fires" the event
    protected void fireSongInfoUpdate(ArrayList<SongInfo> Songs)
    {
        if (mListeners != null && !mListeners.isEmpty())
        {
            for (ISongInfoListener l : mListeners)
            {
                l.onSongInfoUpdated(Songs);
            }
        }
    }
}

