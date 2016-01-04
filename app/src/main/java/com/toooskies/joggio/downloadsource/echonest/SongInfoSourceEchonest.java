package com.toooskies.joggio.downloadsource.echonest;

import android.content.res.Resources;
import android.util.Log;

import com.toooskies.joggio.ISongInfoListener;
import com.toooskies.joggio.ISongInfoSource;
import com.toooskies.joggio.SongInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toooskies on 12/30/2015.
 */
public class SongInfoSourceEchonest implements ISongInfoSource, ISongInfoListener
{
    Resources mResources;
    ArrayList<SongInfo> mSongs;

    /**
     * Constructor.
     * @param resources
     *
     */
    public SongInfoSourceEchonest(Resources resources)
    {
        mResources = resources;
        mSongs = new ArrayList<SongInfo>();
        mSongs.add(new SongInfo("Bon Jovi", "Wanted Dead or Alive"));
        mSongs.add(new SongInfo("Muse", "Stockholm Syndrome"));
        mSongs.add(new SongInfo("Mumford and Sons", "The Wolf"));
    }

    /**
     * Gets available song information.
     * @return
     */
    @Override
    public List<SongInfo> RequestSongs()
    {
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
    public void GetSongInfo(SongInfo Info)
    {
        EchonestDownloadTask downloadTask = new EchonestDownloadTask(mResources);
        downloadTask.addSongInfoListener(this);
        downloadTask.execute(Info);
    }

    /**
     * Parses the JSON content into the object.
     * @param Song
     */
    public void ParseRawData(SongInfo Song)
    throws Exception
    {
        if (Song.RawData instanceof EchonestSongData)
        {
            String json = ((EchonestSongData)Song.RawData).JSONContent;
            // Navigate JSON tree data.
            JSONObject searchResult = new JSONObject(json);
            JSONObject response = searchResult.getJSONObject("response");
            JSONArray songs = response.getJSONArray("songs");
            if (songs != null)
            {
                JSONObject song = songs.getJSONObject(0);
                JSONObject audio_summary = song.getJSONObject("audio_summary");
                Song.BPM = audio_summary.getDouble("tempo");
            }

            // Reset the JSON field, we don't need it anymore.
            Song.RawData = null;
        }
    }

    // ISongInfoListener implementation

    @Override
    public void onSongInfoUpdated(ArrayList<SongInfo> Songs)
    {
        for (SongInfo song : Songs)
        {
            try
            {
                ParseRawData(song);
            }
            catch (Exception e)
            {
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

