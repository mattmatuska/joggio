package com.toooskies.joggio;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] songsRaw = {"60", "75", "50" };
        List<String> songs = new ArrayList<String>(Arrays.asList(songsRaw));

        ArrayAdapter<String> song_list_adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.song_textview,
                R.id.song_textview,
                songs);

        ListView view = (ListView)rootView.findViewById(R.id.song_listview);
        view.setAdapter(song_list_adapter);

        return rootView;
    }
}
