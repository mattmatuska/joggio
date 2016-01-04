package com.toooskies.joggio.downloadsource.echonest;

import com.toooskies.joggio.RawSongData;

public class EchonestSongData extends RawSongData
{
    public String JSONContent;

    public EchonestSongData(String JSON)
    {
        JSONContent = JSON;
    }
}
