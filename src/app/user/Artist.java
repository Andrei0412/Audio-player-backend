package app.user;

import app.Admin;
import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.utils.Constants;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class Artist extends GenericUser {
    @Getter
    private final ArrayList<Album> albums;
    @Getter
    private final ArrayList<Event> events;
    @Getter
    private final ArrayList<Merch> merchandise;

    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        albums = new ArrayList<>();
        events = new ArrayList<>();
        merchandise = new ArrayList<>();
    }

    /**
     * Add new album in artist's current albums.
     *
     * @param inputAlbum the album to be added
     * @return success status
     */
    public String addAlbum(final Album inputAlbum) {
        if (albums.stream().anyMatch(album -> album.getName().equals(inputAlbum.getName()))) {
            return "%s has another album with the same name.".formatted(getUsername());
        }

        if (inputAlbum.getSongs().stream()
                .map(Song::getName)
                .anyMatch(name -> inputAlbum.getSongs().stream()
                        .filter(song -> song.getName().equals(name))
                        .count() > 1)) {
            return "%s has the same song at least twice in this album.".formatted(getUsername());
        }

        Album album = new Album(inputAlbum.getName(), getUsername(),
                inputAlbum.getDescription(), inputAlbum.getReleaseYear());
        album.getSongs().addAll(inputAlbum.getSongs());

        albums.add(album);
        for (Song song : inputAlbum.getSongs()) {
            Admin.getInstance().addSongs(song);
        }
        return "%s has added new album successfully.".formatted(getUsername());
    }

    /**
     * Add new event in artist's current events.
     *
     * @param name the event name
     * @param description the event description
     * @param date the event date
     * @return success status
     */
    public String addEvent(final String name, final String description, final String date) {
        if (events.stream().anyMatch(event -> event.getName().equals(name))) {
            return "%s has another event with the same name.".formatted(getUsername());
        }

        int day = Integer.parseInt(date.substring(Constants.DAY_LOWER_BOUND,
                Constants.DAY_UPPER_BOUND));
        int month = Integer.parseInt(date.substring(Constants.MONTH_LOWER_BOUND,
                Constants.MONTH_UPPER_BOUND));
        int year = Integer.parseInt(date.substring(Constants.YEAR_LOWER_INDEX,
                Constants.YEAR_UPPER_INDEX));

        if (day < Constants.FIRST_DAY || day > Constants.LAST_DAY) {
            return "Event for %s does not have a valid date.".formatted(getUsername());
        }

        if (month < Constants.FIRST_MONTH || month > Constants.LAST_MONTH) {
            return "Event for %s does not have a valid date.".formatted(getUsername());
        }

        if (year < Constants.YEAR_LOWER_BOUND || year > Constants.YEAR_UPPER_BOUND) {
            return "Event for %s does not have a valid date.".formatted(getUsername());
        }

        if (month == Constants.APRIL || month == Constants.JUNE
                || month == Constants.SEPTEMBER || month == Constants.NOVEMBER) {
            if (day > Constants.THIRTY_BOUND) {
                return "Event for %s does not have a valid date.".formatted(getUsername());
            }
        }

        if (month == Constants.FEBRUARY && day > Constants.TWENTY_NINE_BOUND) {
            return "Event for %s does not have a valid date.".formatted(getUsername());
        }

        events.add(new Event(name, description, date));
        return "%s has added new event successfully.".formatted(getUsername());
    }

    /**
     * Add new merch in artist's current merchandise list.
     *
     * @param name the merch name
     * @param description the merch description
     * @param price the merch price
     * @return success status
     */
    public String addMerch(final String name, final String description,
                            final Integer price) {
        if (merchandise.stream().anyMatch(merch -> merch.getName().equals(name))) {
            return "%s has merchandise with the same name.".formatted(getUsername());
        }

        if (price < 1) {
            return "Price for merchandise can not be negative.";
        }

        merchandise.add(new Merch(name, description, price));
        return "%s has added new merchandise successfully.".formatted(getUsername());
    }

    /**
     * Removes an album from artist's list.
     *
     * @param title the album title.
     * @return success status
     */
    public String removeAlbum(final String title) {
        if (albums.stream().noneMatch(album -> album.getName().equals(title))) {
            return "%s doesn't have an album with the given name.".formatted(getUsername());
        }

        //Get the users that are playing sth.
        List<User> playingUsers = new ArrayList<>();
        for (User allUsers : Admin.getInstance().getUsers()) {
            if (allUsers.getPlayer().getSource() != null) {
                playingUsers.add(allUsers);
            }
        }

        for (User auxiliaryUser : playingUsers) {
            //Check if user plays an album and that album name is equal to album to delete.
            if (auxiliaryUser.getPlayer().getSource().getType()
                    .equals(Enums.PlayerSourceType.ALBUM)) {
                if (auxiliaryUser.getPlayer().getSource()
                        .getAudioCollection().getName().equals(title)) {
                    return "%s can't delete this album.".formatted(getUsername());
                }
            }

            //Check if user plays a playlist that has a song from album.
            if (auxiliaryUser.getPlayer().getSource().getType()
                    .equals(Enums.PlayerSourceType.PLAYLIST)) {
                for (Song song : ((Playlist) auxiliaryUser.getPlayer().getSource()
                        .getAudioCollection()).getSongs()) {
                    if (song.getAlbum().equals(title)) {
                        return "%s can't delete this album.".formatted(getUsername());
                    }
                }
            }

            //check if user plays a song from the album.
            if (auxiliaryUser.getPlayer().getSource().getType()
                    .equals(Enums.PlayerSourceType.LIBRARY)) {
                if (((Song) auxiliaryUser.getPlayer().getSource().getAudioFile()).getAlbum()
                        .equals(title)) {
                    return "%s can't delete this album.".formatted(getUsername());
                }
            }
        }

        albums.removeIf(album -> album.getName().equals(title));
        return "%s deleted the album successfully.".formatted(getUsername());
    }

    /**
     * Remove an event from artist's event list.
     *
     * @param name the event name
     * @return success status
     */
    public String removeEvent(final String name) {
        if (events.stream().noneMatch(event -> event.getName().equals(name))) {
            return "%s doesn't have an event with the given name.".formatted(getUsername());
        }

        events.removeIf(event -> event.getName().equals(name));
        return "%s deleted the event successfully.".formatted(getUsername());
    }
}
