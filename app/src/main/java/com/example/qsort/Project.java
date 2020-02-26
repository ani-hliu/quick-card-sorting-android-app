package com.example.qsort;

public class Project {

    String name, id,pictureUri;
    int noParticipants;
    String categories, labels;

    public Project(String name, String id, int noParticipants,String pictureUri, String categories, String labels){
        this.name = name;
        this.id = id;
        this.noParticipants = noParticipants;
        this.pictureUri = pictureUri;
        this.categories = categories;
        this.labels = categories;

    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getNoParticipants() {
        return noParticipants;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public String getCategories() {
        return categories;
    }

    public String getLabels() {
        return labels;
    }
}

