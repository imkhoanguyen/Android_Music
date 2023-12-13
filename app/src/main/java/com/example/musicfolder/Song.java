    package com.example.musicfolder;

    import android.os.Parcel;
    import android.os.Parcelable;

    public class Song implements Parcelable {
        private String title;
        private int resourceId; // Resource ID for the raw mp3 file
        private int albumCoverResourceId;
        public Song(String title, int resourceId) {
            this.title = title;
            this.resourceId = resourceId;
        }
        public Song(String title, int resourceId, int albumCoverResourceId) {
            this.title = title;
            this.resourceId = resourceId;
            this.albumCoverResourceId=albumCoverResourceId;
        }

        public String getTitle() {
            return title;
        }

        public int getResourceId() {
            return resourceId;
        }

        // Parcelable implementation to pass Song objects through intent
        protected Song(Parcel in) {
            title = in.readString();
            resourceId = in.readInt();
        }

        public static final Creator<Song> CREATOR = new Creator<Song>() {
            @Override
            public Song createFromParcel(Parcel in) {
                return new Song(in);
            }

            @Override
            public Song[] newArray(int size) {
                return new Song[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeInt(resourceId);
        }
        public int getAlbumCoverResourceId() {
            return albumCoverResourceId;
        }

        public void setAlbumCoverResourceId(int albumCoverResourceId) {
            this.albumCoverResourceId = albumCoverResourceId;
        }
    }
