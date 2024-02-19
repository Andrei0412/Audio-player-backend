package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import lombok.Getter;

import java.util.ArrayList;

public final class Album extends AudioCollection {
    @Getter
    private final ArrayList<Song> songs;
    @Getter
    private final String description;
    @Getter
    private final int releaseYear;
    @Getter
    private Integer followers;

    public Album(final String name, final String owner,
                 final String description, final Integer releaseYear) {
        super(name, owner);
        songs = new ArrayList<>();
        this.description = description;
        this.releaseYear = releaseYear;
        followers = 0;
    }

    @Override
    public int getNumberOfTracks() {
        return songs.size();
    }

    @Override
    public AudioFile getTrackByIndex(final int index) {
        return songs.get(index);
    }

    /**
     * Matches description boolean.
     *
     * @param descriptionFilter the description
     * @return the boolean
     */
    public boolean matchesDescription(final String descriptionFilter) {
        return getDescription().toLowerCase().startsWith(descriptionFilter.toLowerCase());
    }

    public int getCombinedLikes() {
        return songs.stream().mapToInt(Song::getLikes).sum();
    }

    @Override
    public String toString() {
        return getName();
    }
}
