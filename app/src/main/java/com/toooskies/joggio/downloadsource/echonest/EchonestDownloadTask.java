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
class EchonestDownloadTask extends AsyncTask<SongInfo, Void, ArrayList<SongInfo>>
{

    private final Resources mResources;
    /**
     * Constructor.  Needs resources to function. 
     * @param resources TODO remove resources and use constants.
     */
    public EchonestDownloadTask(Resources resources) {
        mResources = resources;
    }

    /**
     * Downloads the song information we need.
     * @param songs The list of songs that we will attempt to update.  In the new file case,
     *              these should be artist/title only.
     * @return songs, but in an ArrayList.  We will still want to display artist/title, but when
     * data updates we will re-populate.
     */
    @Override
    protected ArrayList<SongInfo> doInBackground(SongInfo... songs) {
        // TODO remove the return of unmodified objects in a different format. This may require
        // better knowledge of Java.
        ArrayList<SongInfo> songList = new ArrayList<>(songs.length);

        // Downloading song data is put on an asynchronous thread instead of the GUI thread.
        for (SongInfo song : songs)
        {
            downloadSongData(song);
            songList.add(song);
        }

        return songList;
    }

    /**
     * Queries the Echonest API to download more information about the song.
     * @param song Container for artist/title lookup information.
     */
    private void downloadSongData(SongInfo song) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Modify artist and title to be compatible with lookup
            String artist = getURLSubstring(song.Artist);
            String title = getURLSubstring(song.Title);

            // Construct the URL for the Echonest query
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
            if (inputStream != null)
            {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() != 0)
                {
                    addRawData(song, buffer.toString());
                }
            }
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

    /**
     * Connects raw data to the original song.
     * @param song A song to which to add the raw data.
     * @param buffer The string data buffer which contains the raw return data.
     */
    private void addRawData(SongInfo song, String buffer) {
        song.RawData = new EchonestSongData(buffer);
    }

    /**
     * Modify text to be part of an URL for lookup.
     * @param text The text to be modified.
     * @return A string.
     */
    private String getURLSubstring(String text) {
        // TODO Add this to some utility class, or find a utility implementation.
        String encodedText = text.toLowerCase();
        try
        {
            encodedText = java.net.URLEncoder.encode(encodedText, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e(mResources.getString(log_tag_song_info_echonest), "String encoding error", e);
        }

        return encodedText;
    }

    /**
     * After the worker thread is complete, notify the main thread (GUI) to update.
     * @param songs The returned list of songs, which should have populated raw data.
     */
    @Override
    protected void onPostExecute(ArrayList<SongInfo> songs) {
        fireSongInfoUpdate(songs);
    }

    /**
     * Listeners for completion of the SongInfo request.
     */
    private ArrayList<ISongInfoListener> mListeners;

    /**
     * Add a listener for the completion of the SongInfo request.
     * @param listener An object to be notified when there is a SongInfo update.
     */
    public void addSongInfoListener(ISongInfoListener listener) {
        if (mListeners == null)
        {
            mListeners = new ArrayList<>();
        }
        if (!mListeners.contains(listener))
        {
            mListeners.add(listener);
        }
    }

    /**
     * Remove listener for the completion of the SongInfo request.
     * @param listener An object to no longer be notified when there is a SongInfo update.
     */
    public void removeSongInfoListener(ISongInfoListener listener) {
        if (mListeners != null && !mListeners.contains(listener))
        {
            mListeners.remove(listener);
        }
    }

    /**
     * Fires a SongInfo update.
     * @param songs A list of potentially updated songs.
     */
    private void fireSongInfoUpdate(ArrayList<SongInfo> songs) {
        if (mListeners != null && !mListeners.isEmpty())
        {
            for (ISongInfoListener listener : mListeners)
            {
                listener.onSongInfoUpdated(songs);
            }
        }
    }
}

