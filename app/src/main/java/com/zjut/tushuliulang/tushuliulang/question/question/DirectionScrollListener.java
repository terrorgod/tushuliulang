/*
 * Copyright (c) 2014 SBG Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjut.tushuliulang.tushuliulang.question.question;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by Stéphane on 09/07/2014.
 */
class DirectionScrollListener implements AbsListView.OnScrollListener {

    private static final int DIRECTION_CHANGE_THRESHOLD = 1;
    private final FloatingActionButton mFloatingActionButton;
    private int mPrevPosition;
    private int mPrevTop;
    private boolean mUpdated;
    //是否是最后一行
    boolean isLastRow = false;

    DirectionScrollListener(FloatingActionButton floatingActionButton) {
        mFloatingActionButton = floatingActionButton;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final View topChild = view.getChildAt(0);
        int firstViewTop = 0;
        if (topChild != null) {
            firstViewTop = topChild.getTop();
        }
        boolean goingDown;
        boolean changed = true;
        /*if (mPrevPosition == firstVisibleItem) {
            final int topDelta = mPrevTop - firstViewTop;
            goingDown = firstViewTop < mPrevTop;
            changed = Math.abs(topDelta) > DIRECTION_CHANGE_THRESHOLD;
        } */

        //默认不可见，在到最后一行时显示
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            isLastRow = true;
            mFloatingActionButton.setVisibility(View.VISIBLE);
        }

        //3个大于小于符合一起控制button上滑消失还是下滑消失，现在状态是上滑消失
        if (mPrevPosition == firstVisibleItem) {
            final int topDelta = mPrevTop - firstViewTop;
            goingDown = firstViewTop > mPrevTop;
            changed = Math.abs(topDelta) < DIRECTION_CHANGE_THRESHOLD;

        } else {
            goingDown = firstVisibleItem < mPrevPosition;
        }


        if (changed && mUpdated) {
            mFloatingActionButton.hide(goingDown);
        }


        mPrevPosition = firstVisibleItem;
        mPrevTop = firstViewTop;
        mUpdated = true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //如果不是最后一行停止则不显示button
        if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            mFloatingActionButton.setVisibility(View.VISIBLE);
            isLastRow = false;
        }else {
            mFloatingActionButton.setVisibility(View.INVISIBLE);
        }
    }
}