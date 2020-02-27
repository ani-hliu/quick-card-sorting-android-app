package com.example.qsort.UxResearcher;

public class Projects {
    public Projects(String project_name, String project_image, String project_id) {
        this.project_name = project_name;
        this.project_image = project_image;
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    private String project_name = "";

    public String getProject_image() {
        return project_image;
    }

    public void setProject_image(String project_image) {
        this.project_image = project_image;
    }

    private String project_image = "";

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    private String project_id = "";

}
