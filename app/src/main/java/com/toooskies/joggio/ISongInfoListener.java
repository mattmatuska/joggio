package com.toooskies.joggio;

import java.util.ArrayList;

/**
 *  Interface to listen for updates.
 */
public interface ISongInfoListener
{
    /**
     * Handle a completion of updated SongInfo.
     * @param songs Potentially updated SongInfo.
     */
    void onSongInfoUpdated(ArrayList<SongInfo> songs);
}
