package net.sarangnamu.common.ui.list

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2017. 12. 4.. <p/>
 */

/**
 * ```xml
    <net.sarangnamu.common.ui.list.LockListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/white"
        android:drawSelectorOnTop="false" />

 * ```
 * ```kotlin
    list.listener = {
        // TODO
    }
 * ```
 */
open class LockListView : ListView {
    protected var isLockScroll = false
    var listener: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.initLayout()
    }

    open fun initLayout() {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (isLockScroll) {
            when (ev.action) {
                MotionEvent.ACTION_UP -> listener?.invoke()
            }

            return false
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isLockScroll) {
            return false
        }

        return super.onTouchEvent(ev)
    }

    fun lock() {
        synchronized(this, { isLockScroll = !isLockScroll })
    }
}

/**
 * ```xml
 * <net.sarangnamu.common.ui.list.AniBtnListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/white"
        android:drawSelectorOnTop="false" />

        <!-- row xml -->
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
 * ```
 *
 * ```kotlin
 * list.slidingMargin(124)
 * list.buttonLayoutId = R.id.btnLayout
 * list.rowId = R.id.row
 * ```
 */
open class AniBtnListView: LockListView {
    private val log = LoggerFactory.getLogger(AniBtnListView::class.java)

    @IdRes var rowId = 0
    @IdRes var buttonLayoutId = 0

    var slidingMargin = 0
    var isShowMenu = false
    var currentView: View? = null

    constructor(context: Context) : super(context) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.initLayout()
    }

    fun slidingMargin(dp: Float) {
        slidingMargin = (dp * context.resources.displayMetrics.density).toInt() - 1
    }

    override fun initLayout() {
        listener = {
            currentView?.let { toggle(it) }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            selector = resources.getDrawable(android.R.color.transparent, null)
        } else {
            selector = resources.getDrawable(android.R.color.transparent)
        }
    }

    fun toggle(view: View) {
        if (rowId == 0 || buttonLayoutId == 0) {
            log.error("ERROR: rowId == 0 || buttonLayoutId == 0")

            return
        }

        var endX = 0
        val moveX = slidingMargin
        var tempView: View? = null

        if (isShowMenu) {
            currentView?.let {
                tempView = it.parent as View
                currentView = null
            }
        } else {
            endX = moveX * -1
            tempView = view.parent as View
            currentView = view
        }

        isShowMenu = !isShowMenu
        lock()

        tempView?.let {
            val row: ViewGroup = it.findViewById(rowId)
            val btn: ViewGroup = it.findViewById(buttonLayoutId)

            btn.animate().translationX(endX.toFloat()).start()
            row.animate().translationX(endX.toFloat()).setListener(object: Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) { view.isClickable = false }
                override fun onAnimationEnd(animation: Animator) {
                    animation.removeAllListeners()
                    view.isClickable = true
                }
            }).start()
        }
    }

    fun hide() {
        currentView?.let {
            if (isShowMenu) {
                toggle(it)
            }
        }
    }
}