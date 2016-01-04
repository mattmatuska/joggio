package com.toooskies.joggio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.toooskies.joggio.downloadsource.echonest.SongInfoSourceEchonest;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SongListFragment extends Fragment implements ISongInfoListener {
    /**
     * Adapter for songs into song_textview displays.
     */
    private ArrayAdapter<String> mSongListAdapter;

    /**
     * Constructor.
     */
    public SongListFragment() {
    }

    /**
     * Creation of the fragment.
     * @param savedInstanceState TODO learn what this is.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Initialization of the View for the SongFragment.
     * @param inflater Inflates the XML document into an object.
     * @param container TODO learn what this is.
     * @param savedInstanceState TODO learn what this is.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate XML layout.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Pass songs into array adapter for display.
        mSongListAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.song_textview,
                R.id.song_textview,
                RequestSongs());
        ListView view = (ListView)rootView.findViewById(R.id.song_listview);
        view.setAdapter(mSongListAdapter);

        return rootView;
    }

    /**
     * Initialization of the options menu.
     * @param menu TODO learn what this is.
     * @param inflater Inflates XML documents into the menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.songfragment, menu);
    }

    /**
     * Response to the selection of an options menu item.
     * @param item The item selected in the GUI.
     * @return Whether a menu option has processed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            RequestSongs();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get all song data.
     * @return A list of strings representing songs.
     */
    @NonNull
    private ArrayList<String> RequestSongs() {
        ArrayList<SongInfo> songs = new ArrayList<>();

        // Generate song title list. Hardcoding three songs to look up.  Later, look these up
        // from song library somewhere.
        ISongInfoSource songSource = new SongInfoSourceEchonest(getResources());
        songSource.addSongInfoListener(this);
        songs.addAll(songSource.requestSongs());

        return getSongsAsText(songs);
    }

    /**
     * Format SongInfo objects into text strings.
     * @param songs Populated SongInfo objects.
     * @return songs converted into strings.
     */
    @NonNull
    private ArrayList<String> getSongsAsText(ArrayList<SongInfo> songs) {
        ArrayList<String> songTexts = new ArrayList<>();
        for(SongInfo song : songs)
        {
            songTexts.add(song.toString());
        }
        return songTexts;
    }

    /**
     * Declares song information has been updated.
     * @param songs The list of songs from the data source.
     */
    @Override
    public void onSongInfoUpdated(ArrayList<SongInfo> songs) {
        // Doing it this way in order to verify that any data changing
        // inside the songs gets re-populated.
        mSongListAdapter.clear();
        mSongListAdapter.addAll(getSongsAsText(songs));
    }
}
