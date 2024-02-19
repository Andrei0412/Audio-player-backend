package app.user;

import lombok.Getter;

public abstract class GenericUser {
    @Getter
    private final String username;
    @Getter
    private final int age;
    @Getter
    private final String city;

    public GenericUser(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
    }
}
