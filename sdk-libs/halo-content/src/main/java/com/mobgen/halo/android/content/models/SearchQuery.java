package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Options for the request of general content.
 */
@Keep
@JsonObject
public class SearchQuery implements Parcelable {

    static {
        LoganSquare.registerTypeConverter(SearchExpression.class, new SearchExpression.SearchExpressionTypeConverter());
    }

    /**
     * Constant for the total matching type in segmentation.
     */
    @Keep
    @Api(2.0)
    public static final String TOTAL_MATCH = "total";
    /**
     * Constant for the partial matching type in segmentation.
     */
    @Keep
    @Api(2.0)
    public static final String PARTIAL_MATCH = "partial";

    /**
     * The middleware ids for the search.
     */
    @Nullable
    @JsonField(name = "moduleIds")
    List<String> mModuleIds;
    /**
     * The instance ids that will be brought.
     */
    @Nullable
    @JsonField(name = "instanceIds")
    List<String> mInstanceIds;

    /**
     * The instace ids with relation by field
     */
    @Nullable
    @JsonField(name = "relatedTo")
    List<Relationship> mRelationships;

    /**
     * The field names that will be brought.
     */
    @Nullable
    @JsonField(name = "fields")
    List<String> mFieldNames;

    /**
     * The names of the fields that will be populated.
     */
    @Nullable
    @JsonField(name = "include")
    List<String> mPopulateNames;

    /**
     * The pagination item.
     */
    @Nullable
    @JsonField(name = "pagination")
    PaginationCriteria mPagination;

    /**
     * The tags list for the general content service.
     */
    @Nullable
    @JsonField(name = "segmentTags")
    List<HaloSegmentationTag> mTags;

    /**
     * The search query for the general content service.
     */
    @Nullable
    @JsonField(name = "searchValues")
    SearchExpression mSearch;

    /**
     * The meta search query for the general content service.
     */
    @Nullable
    @JsonField(name = "metaSearch")
    SearchExpression mMetaSearch;

    /**
     * Locale field to set the locale of the fields brought in
     * the values of an instance.
     */
    @Nullable
    @JsonField(name = "locale")
    String mLocale;

    /**
     * The unique module name you can use to search for content.
     */
    @Nullable
    @JsonField(name = "moduleName")
    String mModuleName;

    /**
     * The matching mode
     */
    @Nullable
    @JsonField(name = "segmentMode")
    @SegmentMode
    String mSegmentMode;

    /**
     * The list of sort elements to search.
     */
    @Nullable
    @JsonField(name = "sort")
    String mSearchSort;

    /**
     * Tells if should be segmented using the user.
     */
    private transient boolean mSegmentWithDevice;

    /**
     * The ttl time the content is available.
     */
    private transient long mTtl = TimeUnit.DAYS.toMillis(2);

    /**
     * Tag the search to a name so it can be fetched always with this name.
     * For searches which date changes, it is really important to set it.
     */
    private transient String mSearchTag;

    /**
     * Tells the content api if the call should be cached during this time in seconds.
     */
    private int mCacheTime;

    /**
     * The creator for parcelables.
     */
    public static final Creator<SearchQuery> CREATOR = new Creator<SearchQuery>() {
        @Override
        public SearchQuery createFromParcel(Parcel source) {
            return new SearchQuery(source);
        }

        @Override
        public SearchQuery[] newArray(int size) {
            return new SearchQuery[size];
        }
    };

    /**
     * Parsing empty constructor.
     */
    protected SearchQuery() {
        //Empty constructor for parsing
    }

    @SuppressWarnings("ResourceType")
    protected SearchQuery(Parcel in) {
        this.mModuleIds = in.createStringArrayList();
        this.mInstanceIds = in.createStringArrayList();
        this.mFieldNames = in.createStringArrayList();
        this.mPopulateNames = in.createStringArrayList();
        this.mPagination = in.readParcelable(PaginationCriteria.class.getClassLoader());
        this.mTags = in.createTypedArrayList(HaloSegmentationTag.CREATOR);
        this.mSearch = in.readParcelable(SearchExpression.class.getClassLoader());
        this.mMetaSearch = in.readParcelable(SearchExpression.class.getClassLoader());
        this.mLocale = in.readString();
        this.mSegmentWithDevice = in.readByte() != 0;
        this.mTtl = in.readLong();
        this.mModuleName = in.readString();
        this.mSegmentMode = in.readString();
        this.mSearchTag = in.readString();
        this.mRelationships = in.createTypedArrayList(Relationship.CREATOR);
        this.mCacheTime = in.readInt();
        this.mSearchSort = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mModuleIds);
        dest.writeStringList(this.mInstanceIds);
        dest.writeStringList(this.mFieldNames);
        dest.writeStringList(this.mPopulateNames);
        dest.writeParcelable(this.mPagination, flags);
        dest.writeTypedList(mTags);
        dest.writeParcelable(this.mSearch, flags);
        dest.writeParcelable(this.mMetaSearch, flags);
        dest.writeString(this.mLocale);
        dest.writeByte(mSegmentWithDevice ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mTtl);
        dest.writeString(this.mModuleName);
        dest.writeString(this.mSegmentMode);
        dest.writeString(this.mSearchTag);
        dest.writeTypedList(mRelationships);
        dest.writeInt(this.mCacheTime);
        dest.writeString(this.mSearchSort);
    }

    /**
     * private constructor to fromCursor search options.
     *
     * @param builder The builder used to fromCursor the original object.
     */
    protected SearchQuery(@NonNull Builder builder) {
        mModuleIds = builder.mModuleIds;
        mInstanceIds = builder.mInstanceIds;
        mFieldNames = builder.mFieldNames;
        mPopulateNames = builder.mPopulateNames;
        mTags = builder.mTags;
        mSearch = builder.mSearch;
        mMetaSearch = builder.mMetaSearch;
        mPagination = builder.mPagination;
        mLocale = builder.mLocale;
        mSegmentWithDevice = builder.mSegmentWithDevice;
        if (builder.mTtl != null) {
            mTtl = builder.mTtl;
        }
        mModuleName = builder.mModuleName;
        mSegmentMode = builder.mSegmentMode;
        mSearchTag = builder.mSearchTag;
        mRelationships = builder.mRelationships;
        mCacheTime = builder.mCacheTime;
        mSearchSort = builder.mSearchSort;
    }

    /**
     * Creates a new builder based on the current options.
     *
     * @return The new builder created.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * Provides the middleware ids.
     *
     * @return The middleware ids.
     */
    @Api(2.0)
    @Nullable
    public List<String> getModuleIds() {
        return mModuleIds;
    }

    /**
     * Provides the instance ids.
     *
     * @return The instance ids.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public List<String> getInstanceIds() {
        return mInstanceIds;
    }

    /**
     * Provides the field names to be brought.
     *
     * @return The field names.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public List<String> getFieldNames() {
        return mFieldNames;
    }

    /**
     * Provides the populate names.
     *
     * @return The populate names.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public List<String> getPopulateNames() {
        return mPopulateNames;
    }

    /**
     * Provides the pagination.
     *
     * @return The pagination criteria.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public PaginationCriteria getPagination() {
        return mPagination;
    }

    /**
     * Provides the built in locale.
     *
     * @return The locale.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public String getLocale() {
        return mLocale;
    }

    /**
     * Sets the locale on the options.
     *
     * @param locale The locale.
     */
    @Keep
    @Api(2.0)
    public void setLocale(@HaloLocale.LocaleDefinition String locale) {
        mLocale = locale;
    }

    /**
     * The module name that is used to search.
     *
     * @return The module name that is used to search.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public String getModuleName() {
        return mModuleName;
    }

    /**
     * Sets the current device as part of the options. This user set will take all its tags and use them to
     * search for content.
     *
     * @param device The device to be used for the search.
     */
    @Keep
    @Api(2.0)
    public void setDevice(@Nullable Device device) {
        if (device != null) {
            mTags = HaloContentHelper.addToList(mTags, device.getTags().toArray(new HaloSegmentationTag[device.getTags().size()]));
            if (mSegmentMode == null) {
                mSegmentMode = PARTIAL_MATCH;
            }
        }
    }

    /**
     * Creates the hash of the options.
     *
     * @param parser The parser.
     * @return The hash created.
     * @throws HaloStorageGeneralException The general content storage error produced.
     * @throws HaloParsingException        Error parsing the current item.
     */
    @Keep
    @Api(2.0)
    @NonNull
    @SuppressWarnings("unchecked")
    public String createHash(@NonNull Parser.Factory parser) throws HaloStorageGeneralException, HaloParsingException {
        try {
            String tagForSearch = !TextUtils.isEmpty(mSearchTag) ? mSearchTag : serializerFrom(parser).convert(this);
            return HaloUtils.sha1(tagForSearch);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new HaloStorageGeneralException("Error while creating the search options hash.", e);
        }
    }

    /**
     * Tells if the options are considered as paginated.
     *
     * @return True if paginated, false otherwise.
     */
    @Keep
    @Api(2.0)
    public boolean isPaginated() {
        return mPagination == null || !mPagination.isSkipped();
    }

    /**
     * Provides the tags of the options.
     *
     * @return The tags.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public List<HaloSegmentationTag> getTags() {
        return mTags;
    }

    /**
     * Provides the search query.
     *
     * @return The search query.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public SearchExpression getSearch() {
        return mSearch;
    }

    /**
     * Provides the meta search query.
     *
     * @return The meta search query.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public SearchExpression getMetaSearch() {
        return mMetaSearch;
    }

    /**
     * Provides the segment mode.
     *
     * @return The segment mode.
     */
    @Keep
    @Api(2.0)
    @Nullable
    @SegmentMode
    public String getSegmentMode() {
        return mSegmentMode;
    }


    /**
     * Provides the sort query to perfom
     *
     * @return The sort query.
     */
    @Keep
    @Api(2.4)
    public String getSort() {
        return mSearchSort;
    }

    /**
     * Provides the ttl time for the cache of instances.
     *
     * @return The ttl time for the instances.
     */
    @Keep
    @Api(2.0)
    public long getTTL() {
        return mTtl;
    }


    /**
     * Checks if the search is segmented with the device.
     *
     * @return The segmentation based on the builder.
     */
    @Keep
    @Api(2.0)
    public boolean isSegmentedWithDevice() {
        return mSegmentWithDevice;
    }

    /**
     * Creates a new builder.
     *
     * @return The builder created.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Provides the serializer for the search options.
     *
     * @param parser The parser factory.
     * @return The parser.
     */
    @Keep
    @Api(2.0)
    @SuppressWarnings("unchecked")
    public Parser<SearchQuery, String> serializerFrom(Parser.Factory parser) {
        return (Parser<SearchQuery, String>) parser.serialize(SearchQuery.class);
    }

    /**
     * Provides the time in seconds to cache the request.
     *
     * @return The time in seconds to cache request.
     */
    @Keep
    @Api(2.33)
    @NonNull
    public int serverCache() {
        return mCacheTime;
    }

    /**
     * The builder for the options item.
     */
    @Keep
    public static class Builder implements IBuilder<SearchQuery> {

        /**
         * The middleware ids for the search.
         */
        private List<String> mModuleIds;

        /**
         * The instance ids that will be brought.
         */
        private List<String> mInstanceIds;

        /**
         * The field names that will be brought.
         */
        private List<String> mFieldNames;

        /**
         * The names of the fields that will be populated.
         */
        private List<String> mPopulateNames;

        /**
         * The tags list for the general content service.
         */
        private List<HaloSegmentationTag> mTags;

        /**
         * The ttl time the content is available.
         */
        private Long mTtl;

        /**
         * The search query for the general content service.
         */
        private SearchExpression mSearch;

        /**
         * The meta search query for the general content service.
         */
        @Nullable
        private SearchExpression mMetaSearch;

        /**
         * The pagination item for general content instances.
         */
        @Nullable
        private PaginationCriteria mPagination;

        /**
         * Provides the locale.
         */
        @Nullable
        private String mLocale;

        /**
         * The module name option.
         */
        @Nullable
        private String mModuleName;

        /**
         * The tag for searching.
         */
        @Nullable
        private String mSearchTag;

        /**
         * The segmentation mode.
         */
        @Nullable
        @SegmentMode
        private String mSegmentMode;

        /**
         * The instace ids with relation by field
         */
        @Nullable
        List<Relationship> mRelationships;

        /**
         * The sort elements.
         */
        @Nullable
        String mSearchSort;

        /**
         * Tells the content api if the call should be segmented using the current device.
         * The action options.
         */
        private boolean mSegmentWithDevice;

        /**
         * Tells the content api if the call should be cached during this time in seconds.
         */
        private int mCacheTime;

        /**
         * Default builder creation.
         */
        protected Builder() {
        }

        /**
         * Copy constructor to get a new builder from the given options.
         *
         * @param currentOptions The options to construct a new builder.
         */
        protected Builder(@NonNull SearchQuery currentOptions) {
            if (currentOptions.mInstanceIds != null) {
                mInstanceIds = new ArrayList<>(currentOptions.mInstanceIds);
            }
            if (currentOptions.mModuleIds != null) {
                mModuleIds = new ArrayList<>(currentOptions.mModuleIds);
            }
            if (currentOptions.mFieldNames != null) {
                mFieldNames = new ArrayList<>(currentOptions.mFieldNames);
            }
            if (currentOptions.mPopulateNames != null) {
                mPopulateNames = new ArrayList<>(currentOptions.mPopulateNames);
            }
            if (currentOptions.mTags != null) {
                mTags = new ArrayList<>(currentOptions.mTags);
            }
            if (currentOptions.mPagination != null) {
                mPagination = new PaginationCriteria(currentOptions.mPagination);
            }
            if (currentOptions.mRelationships != null) {
                mRelationships = new ArrayList<>(currentOptions.mRelationships);
            }
            mSearch = currentOptions.mSearch;
            mMetaSearch = currentOptions.mMetaSearch;
            mTtl = currentOptions.mTtl;
            mLocale = currentOptions.mLocale;
            mModuleName = currentOptions.mModuleName;
            mSegmentWithDevice = currentOptions.mSegmentWithDevice;
            mSegmentMode = currentOptions.mSegmentMode;
            mSearchTag = currentOptions.mSearchTag;
            mSearchSort = currentOptions.mSearchSort;
            mCacheTime = 0;
        }

        /**
         * Adds the middleware ids for the search.
         *
         * @param ids The ids.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder moduleIds(@Nullable String... ids) {
            mModuleIds = HaloContentHelper.addToList(mModuleIds, ids);
            return this;
        }

        /**
         * The instance ids to bring.
         *
         * @param ids The ids of the instances.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder instanceIds(@Nullable String... ids) {
            mInstanceIds = HaloContentHelper.addToList(mInstanceIds, ids);
            return this;
        }

        /**
         * The relationships to filter
         *
         * @param relationships The relationship to filter
         * @return The current builder.
         */
        @Keep
        @Api(2.22)
        @NonNull
        public Builder relatedInstances(@NonNull Relationship... relationships) {
            mRelationships = HaloContentHelper.addToList(mRelationships, relationships);
            return this;
        }

        /**
         * The relationship to filter
         *
         * @param relationship The relationship to filter
         * @return The current builder.
         */
        @Keep
        @Api(2.22)
        @NonNull
        public Builder addRelatedInstances(@NonNull Relationship relationship) {
            if (mRelationships != null) {
                mRelationships.add(relationship);
            } else {
                mRelationships = new ArrayList<>();
                mRelationships.add(relationship);
            }
            return this;
        }

        /**
         * All related instances
         *
         * @param fieldName The fieldname
         * @return The current builder.
         */
        @Keep
        @Api(2.22)
        @NonNull
        public Builder allRelatedInstances(@NonNull String fieldName) {
            mRelationships = HaloContentHelper.addToList(mRelationships, new Relationship[]{Relationship.createForAll(fieldName)});
            return this;
        }


        /**
         * The sort of the instances
         *
         * @param searchSorts The relationship to filter
         * @return The current builder.
         */
        @Keep
        @Api(2.4)
        @NonNull
        public Builder sort(@NonNull SearchSort... searchSorts) {
            mSearchSort = addSortElements(mSearchSort, searchSorts);
            return this;
        }

        /**
         * Adds something to the given list or creates it returning as a result.
         *
         * @param currentSort  The list of items.
         * @param items The items.
         * @return The list returned or created.
         */
        @NonNull
        private String addSortElements(@Nullable String currentSort, @Nullable SearchSort[] items) {
            List<String> finalList = new ArrayList<>();
            if (items != null && items.length > 0) {
                for(int i = 0; i < items.length; i ++){
                    finalList.add(items[i].getSortQuery());
                }
            }
            if(currentSort == null) {
                return TextUtils.join(",", finalList);
            } else {
                return mSearchSort + "," + TextUtils.join(",", finalList);
            }
        }

        /**
         * Discriminate those fields you don't intent to be retrieved.
         *
         * @param fields The fields to discriminate.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder pickFields(@Nullable String... fields) {
            //Add values previous to the field name
            if (fields != null) {
                String[] finalFields = new String[fields.length];
                int i = 0;
                for (String field : fields) {
                    finalFields[i] = "values." + field;
                    i++;
                }
                mFieldNames = HaloContentHelper.addToList(mFieldNames, finalFields);
            }
            return this;
        }

        /**
         * Adds some tags to the search query.
         *
         * @param tags The tags to add.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder tags(@Nullable HaloSegmentationTag... tags) {
            mTags = HaloContentHelper.addToList(mTags, tags);
            return this;
        }

        /**
         * The segment mode. This mode specifies how you can segment the content.
         *
         * @param segmentMode The segment mode.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder segmentMode(@SegmentMode @NonNull String segmentMode) {
            mSegmentMode = segmentMode;
            return this;
        }

        /**
         * Sets the current device as part of the options. This device set will take all its tags and use them to
         * search for content.
         *
         * @return The builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder segmentWithDevice() {
            mSegmentWithDevice = true;
            return this;
        }

        /**
         * Populates all the items form the referenced fields.
         *
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder populateAll() {
            mPopulateNames = HaloContentHelper.addToList(mPopulateNames, new String[]{"all"});
            return this;
        }

        /**
         * Allows us to populate different fields. Population means that you are bringing the
         * instance of referenced items.
         *
         * @param fields The field that will be populated.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder populate(@Nullable String... fields) {
            mPopulateNames = HaloContentHelper.addToList(mPopulateNames, fields);
            return this;
        }


        /**
         * Adds a tag for the search so the
         *
         * @param searchTag The search tag.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder searchTag(@NonNull String searchTag) {
            mSearchTag = searchTag;
            return this;
        }

        /**
         * Begins the search process.
         *
         * @return The search group.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public SearchSyntax beginSearch() {
            return new SearchSyntax(this, new SearchSyntax.BuildSearchListener() {
                @Override
                public void onBuild(@Nullable SearchExpression criteria) {
                    mSearch = criteria;
                }
            });
        }

        /**
         * Starts a meta search process.
         *
         * @return The meta search group.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public SearchSyntax beginMetaSearch() {
            return new SearchSyntax(this, new SearchSyntax.BuildSearchListener() {
                @Override
                public void onBuild(@Nullable SearchExpression criteria) {
                    mMetaSearch = criteria;
                }
            });
        }

        /**
         * The ttl for the items that will be stored in the local device when cached for local content
         * access.
         *
         * @param unit The unit of the time.
         * @param time The time that it will be stored in the unit provided.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder ttl(@NonNull TimeUnit unit, long time) {
            AssertionUtils.notNull(unit, "unit == null");
            mTtl = unit.toMillis(time);
            return this;
        }

        /**
         * The pagination item to add a page file and an limit for the items to retrieve.
         *
         * @param page  The page that will be retrieved. Starts in 1.
         * @param limit The limit.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder pagination(int page, int limit) {
            if (page <= 0 || limit <= 0) {
                throw new HaloConfigurationException("page and limit for pagination should be greater than 0 to configure it.");
            }

            if (mPagination != null) {
                mPagination.setPage(page);
                mPagination.setLimit(limit);
            } else {
                mPagination = new PaginationCriteria(page, limit);
            }
            return this;
        }

        /**
         * Skips the pagination bringing only one page with all the data. This overrides the default
         * pagination params when active.
         *
         * @param active True to activate the one page, false otherwise.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder onePage(boolean active) {
            if (mPagination != null) {
                mPagination.setSkip(active);
            } else {
                mPagination = new PaginationCriteria(active);
            }
            return this;
        }

        /**
         * Enables the locale bringing search for the fields that can be localized.
         *
         * @param locale The locale.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder locale(@Nullable @HaloLocale.LocaleDefinition String locale) {
            mLocale = locale;
            return this;
        }

        /**
         * The module name builder method to make searches by module name.
         *
         * @param moduleName The module name.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder moduleName(@Nullable String moduleName) {
            mModuleName = moduleName;
            return this;
        }

        /**
         * Set server cache in seconds when cache will expire.
         *
         * @param timeInSeconds The time when cache will expire in seconds
         * @return The current builder.
         */
        @Keep
        @Api(2.33)
        @NonNull
        public Builder serverCache(int timeInSeconds) {
            mCacheTime = timeInSeconds;
            return this;
        }

        @Keep
        @NonNull
        @Api(2.0)
        @Override
        public SearchQuery build() {
            return new SearchQuery(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * The segmentation mode types definition.
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TOTAL_MATCH, PARTIAL_MATCH})
    public @interface SegmentMode {
    }
}
