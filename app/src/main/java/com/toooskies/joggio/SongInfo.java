package com.toooskies.joggio;

/**
 * Created by toooskies on 12/30/2015.
 */
public class SongInfo
{
    public String Artist;
    public String Title;
    public Double BPM;

    public RawSongData RawData;

    public SongInfo (String SongArtist, String SongTitle)
    {
        Artist = SongArtist;
        Title = SongTitle;
    }

    public String toString()
    {
        if(BPM != null)
        {
            return Artist + " - " + Title + " - " + BPM + " BPM";
        }
        else
        {
            return Artist + " - " + Title;
        }
    }
}

