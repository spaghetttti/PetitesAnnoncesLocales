package com.example.petitesannonceslocales.utils;

public class Ad {
    private String id; // Firestore document ID
    private String category;
    private String title;
    private String description;
    private String contactMethod;
    private String imageUri;
    private String userId; // ID of the user who posted the ad
    private  String contactInfo;
    // Empty constructor required for Firestore
    public Ad() {}

    public Ad(String id, String category, String title, String description, String contactMethod, String imageUri, String userId, String contactInfo) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.contactMethod = contactMethod;
        this.imageUri = imageUri;
        this.userId = userId;
        this.contactInfo = contactInfo;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContactMethod() { return contactMethod; }
    public void setContactMethod(String contactMethod) { this.contactMethod = contactMethod; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", contactMethod='" + contactMethod + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", userId='" + userId + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}
