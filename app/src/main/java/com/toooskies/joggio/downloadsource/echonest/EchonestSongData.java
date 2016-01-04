package com.toooskies.joggio.downloadsource.echonest;

import com.toooskies.joggio.RawSongData;

class EchonestSongData extends RawSongData
{
    public final String JSONContent;

    public EchonestSongData(String JSON)
    {
        JSONContent = JSON;
    }
}
