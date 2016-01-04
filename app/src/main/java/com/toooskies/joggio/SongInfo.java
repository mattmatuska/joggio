package com.toooskies.joggio;

/**
 * Created by toooskies on 12/30/2015.
 */
public class SongInfo
{
    // TODO Separate SongInfoRequest (Artist/Title/RawData) from stored data.
    // TODO For the current state of the app, double duty is OK.
    /**
     * Recording artist associated with the song.
     */
    public String Artist;
    /**
     * Title of the song.
     */
    public String Title;
    /**
     * Tempo of the song in beats per minute.
     */
    public Double Bpm;

    /**
     * Data object.
     */
    public RawSongData RawData;

    /**
     * Constructor.
     * @param songArtist The recording artist associated with the song.
     * @param songTitle The title of the song.
     */
    public SongInfo (String songArtist, String songTitle) {
        Artist = songArtist;
        Title = songTitle;
    }

    /**
     * String conversion for SongInfo content.
     * @return A formatted string.
     */
    public String toString() {
        if(Bpm != null)
        {
            return Artist + " - " + Title + " - " + Bpm + " Bpm";
        }
        else
        {
            return Artist + " - " + Title;
        }
    }
}

