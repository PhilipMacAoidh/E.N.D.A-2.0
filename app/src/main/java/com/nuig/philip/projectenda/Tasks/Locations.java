package com.nuig.philip.projectenda.Tasks;

public class Locations {

    private String name, date, time, wiki, imgURL, info;
    private Double latitude, longitude;

    public Locations (String name, String date, String time, String wiki, String imgURL, Double latitude, Double longitude, String info) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.wiki = wiki;
        this.imgURL = imgURL;
        this.info = info;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getWiki() {
        return wiki;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getInfo() {
        return info;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        String output =
                "Name: "+this.name+
                ", Date: "+this.date+
                ", Wiki: "+this.wiki+
                ", imgUrl: "+this.imgURL+
                ", Extract: "+this.info+
                ", Co-Ordinates: "+this.latitude+
                ", "+this.longitude;
        return output;
    }
}
