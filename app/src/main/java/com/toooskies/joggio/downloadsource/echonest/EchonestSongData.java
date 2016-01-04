package com.toooskies.joggio.downloadsource.echonest;

import com.toooskies.joggio.RawSongData;

/**
 * Song data specifically from Echonest.
 * This may be refactored for reuse with any JSON content.
 */
class EchonestSongData extends RawSongData
{
    /**
     * Readable JSON-formatted content.
     */
    public final String jsonContent;

    /**
     * Constructor.
     * @param json The JSON-formatted input data.
     */
    public EchonestSongData(String json) {
        jsonContent = json;
    }
}
