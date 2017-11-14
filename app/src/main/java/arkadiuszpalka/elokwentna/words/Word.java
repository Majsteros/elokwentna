package arkadiuszpalka.elokwentna.words;

import android.app.Application;
import android.content.res.Resources;

import arkadiuszpalka.elokwentna.R;

public class Word {
    private String word;
    private String description;

    public Word() {
        this.word = Resources.getSystem().getString(android.R.string.untitled);
        this.description = Resources.getSystem().getString(android.R.string.untitled);
    }

    public Word(String word, String description) {
        this.word = word;
        this.description = description;
    }

    public String getWord() {
        return word;
    }

    public String getDescription() {
        return description;
    }
}