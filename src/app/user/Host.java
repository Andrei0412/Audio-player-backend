package app.user;

import app.Admin;
import app.audio.Collections.Podcast;
import app.audio.Collections.PodcastOutput;
import app.audio.Files.Episode;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Host extends GenericUser {
    @Getter
    private final ArrayList<Podcast> podcasts;
    @Getter
    private ArrayList<Announcement> announcements;

    public Host(final String username, final int age, final String city) {
        super(username, age, city);
        podcasts = new ArrayList<>();
        announcements = new ArrayList<>();
    }

    /**
     * Add new album in host's current podcasts.
     *
     * @param inputPodcast the podcast to be added
     * @return success status
     */
    public String addPodcast(final Podcast inputPodcast) {
        if (podcasts.stream().anyMatch(podcast -> podcast.getName()
                .equals(inputPodcast.getName()))) {
            return "%s has another podcast with the same name.".formatted(getUsername());
        }

        if (inputPodcast.getEpisodes().stream()
                .map(Episode::getName)
                .anyMatch(name -> inputPodcast.getEpisodes().stream()
                        .filter(episode -> episode.getName().equals(name))
                        .count() > 1)) {
            return "%s has the same episode in this podcast.".formatted(getUsername());
        }

        Podcast podcast = new Podcast(inputPodcast.getName(), inputPodcast.getOwner(),
                inputPodcast.getEpisodes());

        podcasts.add(podcast);
        Admin.getInstance().addPodcast(podcast);
        return "%s has added new podcast successfully.".formatted(getUsername());
    }

    /**
     * Add new announcement in host's list.
     *
     * @param name the announcement name
     * @param description the announcement description
     * @return success status
     */
    public String addAnnouncement(final String name, final String description) {
        if (announcements.stream().anyMatch(announcement
                -> announcement.getName().equals(name))) {
            return "%s has already added an announcement with this name.".formatted(getUsername());
        }

        announcements.add(new Announcement(name, description));
        return "%s has successfully added new announcement.".formatted(getUsername());
    }

    /**
     * Removes an announcement from host's list.
     *
     * @param title the title of the announcement to be deleted
     * @return success status
     */
    public String removeAnnouncement(final String title) {
        if (announcements.stream().noneMatch(announcement
                -> announcement.getName().equals(title))) {
            return "%s has no announcement with the given name.".formatted(getUsername());
        }

        announcements.removeIf(announcement -> announcement.getName().equals(title));
        return "%s has successfully deleted the announcement.".formatted(getUsername());
    }

    /**
     * Show podcasts array list.
     *
     * @return the array list
     */
    public ArrayList<PodcastOutput> showPodcasts() {
        ArrayList<PodcastOutput> podcastOutputs = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            podcastOutputs.add(new PodcastOutput(podcast));
        }

        return podcastOutputs;
    }

    /**
     * Removes a podcast from host's list.
     *
     * @param title the podcast title.
     * @return success status
     */
    public String removePodcast(final String title) {
        if (podcasts.stream().noneMatch(podcast -> podcast.getName().equals(title))) {
            return "%s doesn't have a podcast with the given name.".formatted(getUsername());
        }

        //Get the users that are playing sth.
        List<User> playingUsers = new ArrayList<>();
        for (User allUsers : Admin.getInstance().getUsers()) {
            if (allUsers.getPlayer().getSource() != null) {
                playingUsers.add(allUsers);
            }
        }

        //Check if someone is listening to that podcast.
        for (User auxiliaryUser : playingUsers) {
            if (auxiliaryUser.getPlayer().getSource().getType()
                    .equals(Enums.PlayerSourceType.PODCAST)) {
                if (auxiliaryUser.getPlayer().getSource()
                        .getAudioCollection().getName().equals(title)) {
                    return "%s can't delete this podcast.".formatted(getUsername());
                }
            }
        }

        podcasts.removeIf(podcast -> podcast.getName().equals(title));
        return "%s deleted the podcast successfully.".formatted(getUsername());
    }
}
