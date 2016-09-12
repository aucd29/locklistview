/*
 * AniBtnListView.java
 * Copyright 2013 Burke Choi All rights reserved.
 *             http://www.sarangnamu.net
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
package net.sarangnamu.common.ui.list;

import net.sarangnamu.common.DimTool;
import net.sarangnamu.common.ani.AnimatorEndListener;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * {@code
    - xml
    <net.sarangnamu.common.ui.list.AniBtnListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/white"
        android:drawSelectorOnTop="false" />

    - row xml
    <RelativeLayout
        android:id="@+id/row"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="5dp" >

        <TextView
            android:id="@+id/emsNum"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentTop="true"
            android:textColor="@android:color/black"
            android:textSize="17sp"
            android:textStyle="bold" />
    </RelativeLayout>

    <LinearLayout
        android:id="@+id/btnLayout"
        android:layout_width="124dp"
        android:layout_height="62dp"
        android:layout_alignParentRight="true"
        android:layout_marginRight="-124dp"
        android:orientation="horizontal" >

        <TextView
            android:id="@+id/detail"
            style="@style/btnLayout"
            android:layout_width="62dp"
            android:layout_height="match_parent"
            android:background="#acacac"
            android:text="@string/detail"
            android:textColor="#ffffff" />

        <TextView
            android:id="@+id/delete"
            style="@style/btnLayout"
            android:layout_width="62dp"
            android:layout_height="match_parent"
            android:background="#ed594e"
            android:text="@string/delete"
            android:textColor="#ffffff" />
    </LinearLayout>

    - code
    private static final int SLIDING_MARGIN = 130;

    AniBtnListView list = (AniBtnListView) getListView();
    list.setSlidingMargin(SLIDING_MARGIN);
    list.setBtnLayoutId(R.id.btnLayout);
    list.setRowId(R.id.row);

    - row
    holder.row.setOnClickListener(MainActivity.this);

    - click
    ((AniBtnListView) getListView()).toggleMenu(v);
 * }
 * </pre>
 *
 * @author <a href="mailto:aucd29@gmail.com">Burke Choi</a>
 */
public class AniBtnListView extends LockListView {
    private static final Logger mLog = LoggerFactory.getLogger(AniBtnListView.class);

    protected int mSlidingMargin = 0, mRowId, mBtnLayoutId;
    protected View mCurrView;
    protected boolean mIsShowMenu;

    public AniBtnListView(Context context) {
        super(context);
    }

    public AniBtnListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AniBtnListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initLayout() {
        setOnTouchListener(() -> {
            if (mCurrView != null) {
                toggleMenu(mCurrView);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSelector(getResources().getDrawable(android.R.color.transparent, null));
        } else {
            setSelector(getResources().getDrawable(android.R.color.transparent));
        }
    }

    protected int dpToPixelInt(int dp) {
        return DimTool.dpToPixelInt(getContext(), dp);
    }

    public void setSlidingMargin(int dp) {
        mSlidingMargin = dpToPixelInt(dp) - 1;
    }

    public void setBtnLayoutId(int id) {
        mBtnLayoutId = id;
    }

    public void setRowId(int id) {
        mRowId = id;
    }

    public void toggleMenu(final View view) {
        if (mRowId == 0 || mBtnLayoutId == 0) {
            mLog.error("mRowId == 0 || mBtnLayoutId == 0");
            return ;
        }

        final int endX;
        final int moveX = mSlidingMargin;
        View tempView;

        if (mIsShowMenu) {
            endX = 0;
            tempView = (View) mCurrView.getParent();
            mCurrView = null;
        } else {
            endX = moveX * -1;
            tempView = (View) view.getParent();
            mCurrView = view;
        }

        mIsShowMenu = !mIsShowMenu;
        setLock();

        final ViewGroup row       = (ViewGroup) tempView.findViewById(mRowId);
        final ViewGroup btnLayout = (ViewGroup) tempView.findViewById(mBtnLayoutId);

        ObjectAnimator.ofFloat(btnLayout, "translationX", endX).start();
        final ObjectAnimator objAni = ObjectAnimator.ofFloat(row, "translationX", endX);
        objAni.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                objAni.removeAllListeners();
                view.setClickable(true);
            }
        });
        objAni.start();
    }

    public boolean isShowMenu() {
        return mIsShowMenu;
    }

    public void hideMenu() {
        if (mIsShowMenu && mCurrView != null) {
            toggleMenu(mCurrView);
        }
    }
}
