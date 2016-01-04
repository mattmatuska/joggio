package com.toooskies.joggio;

import java.util.List;

/**
 * Created by toooskies on 12/30/2015.
 */
public interface ISongInfoSource
{
    /**
     * Request all available song information.
     * @return
     */
    List<SongInfo> requestSongs();

    /**
     * Add a listener for the completion of the SongInfo request.
     * @param listener An object to be notified when there is a SongInfo update.
     */
    void addSongInfoListener(ISongInfoListener listener);
    /**
     * Remove listener for the completion of the SongInfo request.
     * @param listener An object to no longer be notified when there is a SongInfo update.
     */
    void removeSongInfoListener(ISongInfoListener listener);
}
