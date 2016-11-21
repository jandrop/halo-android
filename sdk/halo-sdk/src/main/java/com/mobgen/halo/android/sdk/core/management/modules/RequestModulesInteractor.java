package com.mobgen.halo.android.sdk.core.management.modules;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProvider;

import java.util.List;

/**
 * Data provider for each of the data modes.
 */
public class RequestModulesInteractor implements SelectorProvider<List<HaloModule>, Cursor> {

    /**
     * The modules repository.
     */
    private ModulesRepository mModulesRepository;

    /**
     * The modules repository.
     * @param modulesRepository The modules repository.
     */
    public RequestModulesInteractor(@NonNull ModulesRepository modulesRepository) {
        mModulesRepository = modulesRepository;
    }

    @NonNull
    @Override
    public HaloResultV2<List<HaloModule>> fromNetwork() throws HaloNetException {
        return mModulesRepository.getModulesFromNetwork();
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromStorage() throws HaloStorageException {
        return mModulesRepository.getCachedModules();
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromNetworkStorage() throws HaloNetException, HaloStorageException {
        return mModulesRepository.getModules();
    }
}