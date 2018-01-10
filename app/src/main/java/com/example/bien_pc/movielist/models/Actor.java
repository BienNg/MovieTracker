package com.example.bien_pc.movielist.models;

/**
 * Created by Bien-PC on 10.01.2018.
 */

public class Actor {
    private int id;

    public Actor(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String name, birth, country, profilePath;


    public String getProfilePath() {
        return profilePath;
    }
    public void setProfilePath(String profilePath) {
        if(!profilePath.equals("null")) {
            this.profilePath = "https://image.tmdb.org/t/p/w500" + profilePath;
        }else{
            this.profilePath = null;
        }
    }
}
