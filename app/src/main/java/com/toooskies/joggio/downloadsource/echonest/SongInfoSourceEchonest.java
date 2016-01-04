package com.toooskies.joggio.downloadsource.echonest;

import android.content.res.Resources;
import android.util.Log;

import com.toooskies.joggio.ISongInfoListener;
import com.toooskies.joggio.ISongInfoSource;
import com.toooskies.joggio.SongInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by toooskies on 12/30/2015.
 */
public class SongInfoSourceEchonest implements ISongInfoSource, ISongInfoListener
{
    /**
     *
     */
    private final Resources mResources;
    private ArrayList<SongInfo> mSongs;

    /**
     * Constructor.
     * @param resources
     * TODO remove resources and use constants.
     */
    public SongInfoSourceEchonest(Resources resources) {
        mResources = resources;
        mSongs = new ArrayList<>();
        // TODO delete this default collection.
        mSongs.add(new SongInfo("Bon Jovi", "Wanted Dead or Alive"));
        mSongs.add(new SongInfo("Muse", "Stockholm Syndrome"));
        mSongs.add(new SongInfo("Mumford and Sons", "The Wolf"));
    }

    /**
     * Retrieves existing song info and queries more.
     * TODO Separate request of current info and update of new info.
     * @return All available song information this source currently has.
     */
    @Override
    public ArrayList<SongInfo> requestSongs() {
        for (SongInfo song : mSongs)
        {
            GetSongInfo(song);
        }

        return mSongs;
    }

    /**
     * Recover information about a specific song and artist.
     * @param Info Our existing information for a song.
     */
    private void GetSongInfo(SongInfo Info) {
        EchonestDownloadTask downloadTask = new EchonestDownloadTask(mResources);
        downloadTask.addSongInfoListener(this);
        downloadTask.execute(Info);
    }

    /**
     * Parses the JSON content into the object.
     * @param Song The internal data object containing song information (including parsed data).
     * @throws JSONException A problem with changing raw data into JSON content.
     * TODO extract into a parser class which runs on an asynchronous thread.
     */
    private void ParseRawData(SongInfo Song) throws JSONException {
        if (Song.RawData instanceof EchonestSongData)
        {
            String json = ((EchonestSongData)Song.RawData).jsonContent;
            // Navigate JSON tree data.
            JSONObject searchResult = new JSONObject(json);
            JSONObject response = searchResult.getJSONObject("response");
            JSONArray songs = response.getJSONArray("songs");
            if (songs != null)
            {
                JSONObject song = songs.getJSONObject(0);
                JSONObject audio_summary = song.getJSONObject("audio_summary");
                Song.Bpm = audio_summary.getDouble("tempo");
            }

            // Clear the data field, we don't need it anymore.
            Song.RawData = null;
        }
    }

    // ISongInfoListener implementation

    /**
     * Handle a completion of updated SongInfo.
     * @param songs Potentially updated SongInfo.
     */
    @Override
    public void onSongInfoUpdated(ArrayList<SongInfo> songs)
    {
        for (SongInfo song : songs)
        {
            try
            {
                ParseRawData(song);
            }
            catch (JSONException e)
            {
                // TODO redo this error log statement once constants get moved out of XML or
                // TODO this gets refactored into part of a parser class.
                Log.e(this.getClass().getName(), e.getClass().getName());
            }

            if (!mSongs.contains(song))
            {
                mSongs.add(song);
            }
        }
        fireSongInfoUpdate(mSongs);
    }


    // ISongInfoSource implementation

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

