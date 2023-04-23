package ru.sbp.security;

import ru.sbp.objects.db.User;

public class JwtEntityFactory {

    public static JwtEntity create(User user){
        return new JwtEntity(user.getId(), user.getUsername(), user.getPassword());
    }

}
