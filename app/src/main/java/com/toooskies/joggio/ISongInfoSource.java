package com.toooskies.joggio;

import java.util.List;

/**
 * Created by toooskies on 12/30/2015.
 */
public interface ISongInfoSource
{
    List<SongInfo> RequestSongs();

    void ParseRawData(SongInfo Info) throws Exception;

    /**
     * Add a listener to be notified when data changes.
     * @param Listener Listens for SongInfo to change.
     */
    void addSongInfoListener(ISongInfoListener Listener);
}
