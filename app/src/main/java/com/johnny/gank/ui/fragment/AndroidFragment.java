package com.johnny.gank.ui.fragment;
/*
 * Copyright (C) 2016 Johnny Shieh Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.johnny.gank.action.ActionType;
import com.johnny.gank.action.AndroidActionCreator;
import com.johnny.gank.data.ui.GankNormalItem;
import com.johnny.gank.di.component.AndroidFragmentComponent;
import com.johnny.gank.rxflux.Dispatcher;
import com.johnny.gank.rxflux.StoreObserver;
import com.johnny.gank.stat.StatName;
import com.johnny.gank.store.StoreChange;
import com.johnny.gank.ui.activity.MainActivity;
import com.johnny.gank.ui.activity.WebviewActivity;
import com.johnny.gank.ui.adapter.CategoryGankAdapter;
import com.johnny.gank.ui.widget.LoadMoreView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class AndroidFragment extends CategoryGankFragment implements StoreObserver<StoreChange.AndroidStore>{

    public static final String TAG = AndroidFragment.class.getSimpleName();

    @Inject
    com.johnny.gank.store.AndroidStore mStore;
    @Inject AndroidActionCreator mActionCreator;

    protected AndroidFragmentComponent mComponent;

    public static AndroidFragment newInstance() {
        return new AndroidFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initInjector() {
        mComponent = ((MainActivity)getActivity()).getMainActivityComponent().androidFragmentComponent();
        mComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View contentView = createView(inflater, container);
        mAdapter.setOnItemClickListener(new CategoryGankAdapter.OnItemClickListener() {
            @Override
            public void onClickNormalItem(View view, GankNormalItem normalItem) {
                WebviewActivity.openUrl(getActivity(), normalItem.url, normalItem.desc);
            }
        });

        initInjector();
        Dispatcher.get().register(mStore, ActionType.GET_ANDROID_LIST);
        mStore.addObserver(this);
        return contentView;
    }

    @Override
    public void onDestroyView() {
        mStore.unRegister();
        super.onDestroyView();
    }

    @Override
    protected void refreshList() {
        mActionCreator.getAndroidList(1);
    }

    @Override
    protected void loadMore() {
        vLoadMore.setStatus(LoadMoreView.STATUS_LOADING);
        mActionCreator.getAndroidList(mAdapter.getCurPage() + 1);
    }

    @Override
    protected String getStatPageName() {
        return StatName.PAGE_ANDROID;
    }

    @Override
    public void onChange(StoreChange.AndroidStore androidStore) {
        if(1 == mStore.getPage()) {
            vRefreshLayout.setRefreshing(false);
        }
        mAdapter.updateData(mStore.getPage(), mStore.getGankList());
        mLoadingMore = false;
        vLoadMore.setStatus(LoadMoreView.STATUS_INIT);
    }

    @Override
    public void onError(StoreChange.AndroidStore androidStore) {
        vRefreshLayout.setRefreshing(false);
        mLoadingMore = false;
        vLoadMore.setStatus(LoadMoreView.STATUS_FAIL);
    }
}
