package com.mobgen.halo.android.auth.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;


/**
 * User profile identified
 */
@Keep
@JsonObject
public class HaloUserProfile implements Parcelable {

    /**
     * The user id.
     */
    @Nullable
    @JsonField(name = "id")
    String mIdentifiedId;
    /**
     * The email.
     */
    @Nullable
    @JsonField(name = "email")
    String mEmail;
    /**
     * The photo.
     */
    @Nullable
    @JsonField(name = "photoUrl")
    String mPhoto;
    /**
     * Display name with the name and surname all together.
     */
    @Nullable
    @JsonField(name = "displayName")
    String mDisplayName;
    /**
     * The name.
     */
    @Nullable
    @JsonField(name = "name")
    String mName;
    /**
     * The surname.
     */
    @Nullable
    @JsonField(name = "surname")
    String mSurname;

    /**
     * Parsing empty constructor.
     */
    protected HaloUserProfile() {
        //Empty constructor for parsing
    }

    public static final Creator<HaloUserProfile> CREATOR = new Creator<HaloUserProfile>() {
        @Override
        public HaloUserProfile createFromParcel(Parcel source) {
            return new HaloUserProfile(source);
        }

        @Override
        public HaloUserProfile[] newArray(int size) {
            return new HaloUserProfile[size];
        }
    };

    /**
     * The social profile constructor
     */
    public HaloUserProfile(String identifiedId, String displayName, String name, String surname, String photo, String email) {
        this.mIdentifiedId = identifiedId;
        this.mDisplayName = displayName;
        this.mName = name;
        this.mSurname = surname;
        this.mPhoto = photo;
        this.mEmail = email;
    }

    /**
     * Provides the identified id.
     *
     * @return The the identified id.
     */
    @Api(2.1)
    @Nullable
    public String getIdentifiedId() {
        return mIdentifiedId;
    }

    /**
     * Provides the email
     *
     * @return The the email
     */
    @Api(2.1)
    @Nullable
    public String getEmail() {
        return mEmail;
    }

    /**
     * Provides the photo uri
     *
     * @return The the photo uri
     */
    @Api(2.1)
    @Nullable
    public String getPhoto() {
        return mPhoto;
    }

    /**
     * Provides the concatenation of name and surname
     *
     * @return The the name and surname together
     */
    @Api(2.1)
    @Nullable
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Provides the name.
     *
     * @return The name.
     */
    @Api(2.1)
    @Nullable
    public String getName() {
        return mName;
    }

    /**
     * Provides the surname.
     *
     * @return The surname.
     */
    @Api(2.1)
    @Nullable
    public String getSurname() {
        return mSurname;
    }

    /**
     * Parcel for the userProfile.
     *
     * @param in The parcel where we will write the user profile
     */
    protected HaloUserProfile(Parcel in) {
        this.mIdentifiedId = in.readString();
        this.mDisplayName = in.readString();
        this.mName = in.readString();
        this.mSurname = in.readString();
        this.mPhoto = in.readString();
        this.mEmail = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mIdentifiedId);
        dest.writeString(this.mDisplayName);
        dest.writeString(this.mName);
        dest.writeString(this.mSurname);
        dest.writeString(this.mPhoto);
        dest.writeString(this.mEmail);
    }

    @Override
    public String toString() {
        return "HaloUserProfile{" +
                "  mIdentifiedId='" + mIdentifiedId + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mPhoto=" + mPhoto +
                ", mDisplayName='" + mDisplayName + '\'' +
                ", mName='" + mName + '\'' +
                ", mSurname='" + mSurname + '\'' +
                '}';
    }
}
