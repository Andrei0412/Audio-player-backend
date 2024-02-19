package app;

import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import app.utils.Constants;
import app.utils.Enums;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The type Admin.
 */
public final class Admin {
    private static Admin instance = null;
    @Getter
    private List<User> users = new ArrayList<>();
    @Getter
    private List<Artist> artists = new ArrayList<>();
    @Getter
    private List<Host> hosts = new ArrayList<>();
    private List<Song> songs = new ArrayList<>();
    @Getter
    private List<Podcast> podcasts = new ArrayList<>();
    private int timestamp = 0;

    private Admin() {
    }

    /**
     * Gets the admin instance.
     *
     * @return instance
     */
    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public void setUsers(final List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    /**
     * Adds user.
     *
     * @param userInput the user input
     */
    public String addUser(final UserInput userInput, final String type) {
        String message = "";
        if (users.stream().anyMatch(user ->
                user.getUsername().equals(userInput.getUsername()))
                || artists.stream().anyMatch(artist ->
                    artist.getUsername().equals(userInput.getUsername()))
                || hosts.stream().anyMatch(host ->
                    host.getUsername().equals(userInput.getUsername()))) {
            return "The username %s is already taken.".formatted(userInput.getUsername());
        }

        if (type.equals("user")) {
            users.add(new User(userInput.getUsername(), userInput.getAge(),
                    userInput.getCity()));
        }

        if (type.equals("artist")) {
            artists.add(new Artist(userInput.getUsername(), userInput.getAge(),
                    userInput.getCity()));
        }

        if (type.equals("host")) {
            hosts.add(new Host(userInput.getUsername(), userInput.getAge(),
                    userInput.getCity()));
        }

        return "The username %s has been added successfully.".formatted(userInput.getUsername());
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public void setSongs(final List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }

    /**
     * Adds song.
     *
     * @param song the sing to be added.
     */
    public void addSongs(final Song song) {
        songs.add(song);
    }

    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public void setPodcasts(final List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Adds a new podcast to the list.
     */
    public void addPodcast(final Podcast podcast) {
        podcasts.add(podcast);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            playlists.addAll(user.getPlaylists());
        }
        return playlists;
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public List<Album> getAlbums() {
        List<Album> albums = new ArrayList<>();
        for (Artist artist : artists) {
            albums.addAll(artist.getAlbums());
        }
        return albums;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gets artist.
     *
     * @param username the username
     * @return the artist
     */
    public Artist getArtist(final String username) {
        for (Artist artist : artists) {
            if (artist.getUsername().equals(username)) {
                return artist;
            }
        }
        return null;
    }

    /**
     * Gets host.
     *
     * @param username the username
     * @return the host
     */
    public Host getHost(final String username) {
        for (Host host : hosts) {
            if (host.getUsername().equals(username)) {
                return host;
            }
        }
        return null;
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            if (user.getConnectionStatus() == Enums.ConnectionStatus.OFFLINE) {
                return;
            } else {
                user.simulateTime(elapsed);
            }
        }
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= Constants.LIMIT) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 albums.
     *
     * @return the top 5 albums
     */
    public List<String> getTop5Albums() {
        return getAlbums().stream()
                .sorted(Comparator
                        .comparingInt(Album::getCombinedLikes)
                        .reversed()
                        .thenComparing(Comparator.comparing(Album::getName)))
                .limit(Constants.LIMIT)
                .map(Album::getName)
                .toList();
    }

    /**
     * Gets top 5 artists.
     *
     * @return the top 5 artists
     */
    public List<String> getTop5Artists() {
        HashMap<String, Integer> artistLikes = new HashMap<>();
        for (Artist artist : artists) {
            int combinedLikes = 0;
            for (Album album : artist.getAlbums()) {
                combinedLikes += album.getCombinedLikes();
            }
            artistLikes.put(artist.getUsername(), combinedLikes);
        }

        return artistLikes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(Constants.LIMIT)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= Constants.LIMIT) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Reset.
     */
    public void reset() {
        users = new ArrayList<>();
        artists = new ArrayList<>();
        hosts = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        timestamp = 0;
    }

    /**
     * Gets online users.
     *
     * @return the all online users
     */
    public ArrayList<String> getOnlineUsers() {
        ArrayList<String> results = new ArrayList<>();
        for (User user : users) {
            if (user.getConnectionStatus() == Enums.ConnectionStatus.ONLINE) {
                results.add(user.getUsername());
            }
        }
        return results;
    }

    /**
     * Gets all users.
     *
     * @return the all types of users
     */
    public ArrayList<String> getAllUsers() {
        ArrayList<String> results = new ArrayList<>();
        for (User user : users) {
            results.add(user.getUsername());
        }

        for (Artist artist : artists) {
            results.add(artist.getUsername());
        }

        for (Host host : hosts) {
            results.add(host.getUsername());
        }

        return results;
    }

    /**
     * Deletes an user.
     *
     * @param username the user to be deleted
     * @return command status
     */
    public String deleteUser(final String username) {
        //Get the users that play sth.
        List<User> playingUsers = new ArrayList<>();
        for (User allUsers : users) {
            if (allUsers.getPlayer().getSource() != null) {
                playingUsers.add(allUsers);
            }
        }

        if (users.stream()
                .anyMatch(user -> user.getUsername().equals(username))) {
            User user = Admin.getInstance().getUser(username);
            ArrayList<Playlist> userPlaylists = user.getPlaylists();

            for (User auxiliaryUser : playingUsers) {
                if (auxiliaryUser.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.PLAYLIST)) {
                    for (Playlist playlist : user.getPlaylists()) {
                        if (auxiliaryUser.getPlayer().getSource().getAudioCollection().getName()
                                .equals(playlist.getName())) {
                            return "%s can't be deleted.".formatted(username);
                        }
                    }
                }
            }

            //if no one listens to a playlist owned by the user, we remove him.
            for (Song song : user.getLikedSongs()) {
                song.dislike();
            }

            for (Playlist playlist : user.getFollowedPlaylists()) {
                playlist.decreaseFollowers();
            }

            for (User user2 : Admin.getInstance().getUsers()) {
                user2.getFollowedPlaylists().removeIf(playlist
                        -> playlist.getOwner().equals(username));
            }

            Admin.getInstance().getPlaylists().removeIf(playlist
                    -> playlist.getOwner().equals(username));
            Admin.getInstance().getUsers().removeIf(user1 -> user1.getUsername().equals(username));
            return "%s was successfully deleted.".formatted(username);
        }

        //If user to be deleted is an artist :D
        if (Admin.getInstance().getArtists().stream()
                .anyMatch(artist -> artist.getUsername().equals(username))) {

            for (User auxiliaryUser : playingUsers) {
                if (auxiliaryUser.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.ALBUM)) {
                    //Modded.
                    if (auxiliaryUser.getPlayer().getSource()
                            .getAudioCollection().getOwner().equals(username)) {
                        return "%s can't be deleted.".formatted(username);
                    }
                }

                if (auxiliaryUser.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.PLAYLIST)) {
                    for (Song song : ((Playlist) auxiliaryUser.getPlayer().getSource()
                            .getAudioCollection()).getSongs()) {
                        if (song.getArtist().equals(username)) {
                            return "%s can't be deleted.".formatted(username);
                        }
                    }
                }

                if (auxiliaryUser.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.LIBRARY)) {
                    if (((Song) auxiliaryUser.getPlayer().getSource().getAudioFile()).getArtist()
                            .equals(username)) {
                        return "%s can't be deleted.".formatted(username);
                    }
                }

                if (auxiliaryUser.getCreatorName().equals(username)) {
                    return "%s can't be deleted.".formatted(username);
                }
            }

            //Check if a user is on artist's page
            for (User user : users) {
                if (user.getCreatorName().equals(username)) {
                    return "%s can't be deleted.".formatted(username);
                }
            }

            //If no one listens to song/Album we can remove him.
            songs.removeIf(song -> song.getArtist().equals(username));
            artists.removeIf(artist -> artist.getUsername().equals(username));

            //Update stuff for users :D
            for (User user : users) {
                user.getLikedSongs().removeIf(song -> song.getArtist().equals(username));
                for (Playlist playlist : user.getPlaylists()) {
                    playlist.getSongs().removeIf(song -> song.getArtist().equals(username));
                }
            }
            return "%s was successfully deleted.".formatted(username);
        }

        //If user to be deleted is a host :D
        if (Admin.getInstance().getHosts().stream()
                .anyMatch(host -> host.getUsername().equals(username))) {
            for (User auxiliaryUser : playingUsers) {
                if (auxiliaryUser.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.PODCAST)) {
                    if (auxiliaryUser.getPlayer().getSource()
                            .getAudioCollection().getOwner().equals(username)) {
                        return "%s can't be deleted.".formatted(username);
                    }
                }
            }

            //Check if a user is on host's page
            for (User user : users) {
                if (user.getCreatorName().equals(username)) {
                    return "%s can't be deleted.".formatted(username);
                }
            }

            Admin.getInstance().getHosts().removeIf(host -> host.getUsername().equals(username));
            return "%s was successfully deleted.".formatted(username);
        }

        return "Error";
    }

    /**
     * Checks if the user exists.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean doesExist(final String username) {
        return Admin.getInstance().users.stream()
                .anyMatch(user -> user.getUsername().equals(username))
                || Admin.getInstance().artists.stream()
                .anyMatch(artist -> artist.getUsername().equals(username))
                || Admin.getInstance().hosts.stream()
                .anyMatch(host -> host.getUsername().equals(username));
    }

    /**
     * Checks if the user is normal.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean isUser(final String username) {
        return !(Admin.getInstance().artists.stream()
                .anyMatch(artist -> artist.getUsername().equals(username))
                || Admin.getInstance().hosts.stream()
                .anyMatch(host -> host.getUsername().equals(username)));
    }

    /**
     * Checks if the user is artist.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean isArtist(final String username) {
        return !(Admin.getInstance().users.stream()
                .anyMatch(user -> user.getUsername().equals(username))
                || Admin.getInstance().hosts.stream()
                .anyMatch(host -> host.getUsername().equals(username)));
    }

    /**
     * Checks if the user is host.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean isHost(final String username) {
        return !(Admin.getInstance().users.stream()
                .anyMatch(user -> user.getUsername().equals(username))
                || Admin.getInstance().artists.stream()
                .anyMatch(artist -> artist.getUsername().equals(username)));
    }
}
