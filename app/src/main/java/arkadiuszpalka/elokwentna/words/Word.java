package arkadiuszpalka.elokwentna.words;

public class Word {
    private String word;
    private String description;
    private boolean addable = true;

    public Word(String word, String description) {
        this.word = word;
        this.description = description;
    }

    public Word(String word, String description, boolean isAddable) {
        this.word = word;
        this.description = description;
        this.addable = isAddable;
    }

    public String getWord() {
        return word;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAddable() {
        return addable;
    }
}

