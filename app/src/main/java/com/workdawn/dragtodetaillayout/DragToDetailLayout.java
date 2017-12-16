package com.workdawn.dragtodetaillayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;

/**
 * 一个向上或者向右拖拽查看更多详情的自定义控件，支持2个以上子页面，支持竖向和横向拖拉
 * 用于实现类似淘宝、京东等电商app拖动查看详情需求
 * 有如下两种使用方法
 *  1.<DragToDetailLayout
        app:introLayout="@layout/product_intro"
        app:detailLayout="@layout/product_detail"/>
 这种使用方法只能设置两个页面，introLayout标识第一个页面布局，detailLayout标识第二个页面布局
    2.<DragToDetailLayout>
        ....
      <DragToDetailLayout/>
 方法2的优先级更高，也就是说如果在容器节点下面手动添加了子节点，那么就会忽略属性上配置的两个页面
 * Created by Administrator on 2017/12/12.
 * email:2008miaowe@163.com
 * @author workdawn
 */
public class DragToDetailLayout extends LinearLayout {

    private final float DEFAULT_DRAG_DAMP = 0.7f;
    private final int DEFAULT_ANI_DURATION = 250;
    private final float DEFAULT_REBOUND_PERCENT = 0.2f;

    /**头部介绍页面布局资源id*/
    private int mIntroLayoutResId;
    /**底部详情页面布局资源id*/
    private int mDetailLayoutResId;
    private Context mContext;
    private LayoutInflater mInflater;
    /**布局集合*/
    private SparseArray<View> mViews;
    private EnterDetailLayoutListener mEnterDetailLayoutListener;
    private DragScrollListener mDragScrollListener;
    private OnDragListener mOnDragListener;
    private VelocityTracker mVelocityTracker;
    private float mTouchSlop;
    private int mMaxFlingVelocity;
    private ScrollerCompat mScrollerCompat;
    private float initX, initY;
    /**当前活动目标*/
    private View currentTargetView;
    /**当前活动目标序号，从1开始*/
    private int currentTargetViewIndex = 1;
    /**拦截有效的时候记录的X,Y坐标，用户处理滚动*/
    private float mScrollInterceptX, mScrollInterceptY;
    private UseType mUseType = UseType.DEFAULT;

    private int mDuration = DEFAULT_ANI_DURATION;
    private float mDragDamp = DEFAULT_DRAG_DAMP;
    private float mReboundPercent = DEFAULT_REBOUND_PERCENT;

    private enum UseType{
        DEFAULT,
        CUSTOMIZE
    }

    public DragToDetailLayout(Context context) {
        this(context, null);
    }

    public DragToDetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragToDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        mMaxFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mScrollerCompat = ScrollerCompat.create(context, new DecelerateInterpolator());
        mViews = new SparseArray<>();
        initAttr(context, attrs, defStyleAttr);
    }

    /**
     * 处理属性
     * @param context 上下文对象
     * @param attrs 属性
     */
    private void initAttr(Context context, AttributeSet attrs, int def){
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.DragToDetailLayout, def, 0);
            mDragDamp = a.getFloat(R.styleable.DragToDetailLayout_dragDamp, DEFAULT_DRAG_DAMP);
            mDuration = a.getInt(R.styleable.DragToDetailLayout_reboundDuration, DEFAULT_ANI_DURATION);
            mReboundPercent = a.getFloat(R.styleable.DragToDetailLayout_reboundPercent, DEFAULT_REBOUND_PERCENT);
            mIntroLayoutResId = a.getResourceId(R.styleable.DragToDetailLayout_introLayout, 0);
            mDetailLayoutResId = a.getResourceId(R.styleable.DragToDetailLayout_detailLayout, 0);

            if(mDragDamp >= 1){
                mDragDamp = 1.0f;
            } else {
                mDragDamp = 1.0f - mDragDamp;
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(a != null){
                a.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(mInflater == null){
            mInflater =  LayoutInflater.from(mContext);
        }
        int childCount = getChildCount();
        if(childCount == 0){
            if(mIntroLayoutResId == 0 || mDetailLayoutResId == 0){
                throw new RuntimeException("Unable to find explicit attr introLayout or detailLayout ; have you declared it in your layout xml ?");
            } else {
                mUseType = UseType.DEFAULT;
                View inV = mInflater.inflate(mIntroLayoutResId, null);
                mViews.put(mIntroLayoutResId, inV);
                addView(inV);
                currentTargetView = inV;
                View deV = mInflater.inflate(mDetailLayoutResId, null);
                mViews.put(mDetailLayoutResId, deV);
                addView(deV);
            }
        } else {
            if(childCount < 2){
                throw new RuntimeException("DragToDetailLayout's child must more than 1");
            } else {
                mUseType = UseType.CUSTOMIZE;
                for(int i = 0; i < childCount; i++){
                    mViews.put(i + 1, getChildAt(i));
                }
                currentTargetView = getChildAt(0);
            }
        }
    }

    private void setInternalScrollChangedListener(View view){
        if(mDragScrollListener == null) return;
        if(view instanceof ScrollView){
            ScrollView scrollView = (ScrollView) view;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        mDragScrollListener.onScrollChanged(v, scrollY, scrollX);
                    }
                });
            } else {
                if(scrollView instanceof CanListenerScrollView){
                    ((CanListenerScrollView) scrollView).setOnScrollChangeListener(new CanListenerScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChanged(View v, int x, int y) {
                            mDragScrollListener.onScrollChanged(v, y, x);
                        }
                    });
                }
            }
        } else if(view instanceof NestedScrollView){
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                nestedScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        mDragScrollListener.onScrollChanged(v, scrollY, scrollX);
                    }
                });
            } else {
                if(nestedScrollView instanceof CanListenerNestScrollView){
                    ((CanListenerNestScrollView) nestedScrollView).setOnScrollChangeListener(new CanListenerNestScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChanged(View v, int x, int y) {
                            mDragScrollListener.onScrollChanged(v, y, x);
                        }
                    });
                }
            }
        } else if(view instanceof HorizontalScrollView){
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) view;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                horizontalScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        mDragScrollListener.onScrollChanged(v, scrollY, scrollX);
                    }
                });
            } else {
                if(view instanceof CanListenerHorizontalScrollView){
                    ((CanListenerHorizontalScrollView) view).setOnScrollChangeListener(new CanListenerHorizontalScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChanged(View v, int x, int y) {
                            mDragScrollListener.onScrollChanged(v, y, x);
                        }
                    });
                }
            }
        } else if(view instanceof AbsListView){
            AbsListView absListView = (AbsListView) view;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                absListView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        mDragScrollListener.onScrollChanged(v, scrollY, scrollX);
                    }
                });
            } else {
                mDragScrollListener.onScrollChanged(view, 0, 0);
            }
        } else if(view instanceof RecyclerView){
            RecyclerView recyclerView = (RecyclerView) view;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                recyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        mDragScrollListener.onScrollChanged(v, scrollY, scrollX);
                    }
                });
            } else {
                mDragScrollListener.onScrollChanged(recyclerView, 0, 0);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean isIntercept = false;
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initX = event.getX();
                initY = event.getY();
                isIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isIntercept = checkIntercept(event);
                if(isIntercept){
                    mScrollInterceptY = event.getY();
                    mScrollInterceptX = event.getX();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                if(!mScrollerCompat.isFinished()){
                    mScrollerCompat.abortAnimation();
                    isIntercept = true;
                }
                break;
        }
        return isIntercept;
    }

    private boolean checkIntercept(MotionEvent event){
        int orientation = getOrientation();
        float currentTmpX = event.getX();
        float currentTmpY = event.getY();
        float distanceY = currentTmpY - initY;
        float distanceX = currentTmpX - initX;
        findCurrentTargetView();
        int direction;
        if(orientation == LinearLayout.VERTICAL){
            direction = (int) distanceY;
            return !((currentTargetViewIndex == 1 && !canScrollVertically(currentTargetView, -1, event))
                    || (currentTargetViewIndex == mViews.size() && !canScrollVertically(currentTargetView, 1, event)))
                    && Math.abs(distanceY) > mTouchSlop && Math.abs(distanceY) > Math.abs(distanceX)
                    && !canScrollVertically(currentTargetView, -direction, event);
        } else {
            direction = (int) distanceX;
            return !((currentTargetViewIndex == 1 && !canScrollHorizontally(currentTargetView, -1, event))
                    || (currentTargetViewIndex == mViews.size() && !canScrollHorizontally(currentTargetView, 1, event)))
                    && Math.abs(distanceX) > mTouchSlop && Math.abs(distanceX) > Math.abs(distanceY)
                    && !canScrollHorizontally(currentTargetView, -direction, event);
        }
    }

    /**
     * 寻找当前目前页面
     * @return 目标页面
     */
    private View findCurrentTargetView(){
        return currentTargetView;
    }

    /**
     * 判断控件是否可以在水平方向滚动
     * @param target 目标控件
     * @param direction 方向
     * @param event 事件
     * @return 能否滚动
     */
    private boolean canScrollHorizontally(View target, int direction, MotionEvent event){
        if(currentTargetView == null) return false;
        if(target instanceof ViewPager){
            //控件为viewpager的时候，是否可以滚动依据是否是最后一页
            ViewPager viewPager = (ViewPager) target;
            int currentPagerIndex = viewPager.getCurrentItem();
            int pagerCount = viewPager.getChildCount();
            if(currentPagerIndex == pagerCount - 1){
                return true;
            }
        } else if(target instanceof HorizontalScrollView){
            return ViewCompat.canScrollHorizontally(target, direction);
        } else if(target instanceof RecyclerView){
            //如果是RecyclerView只允许LayoutManager布局方向为HORIZONTAL
            RecyclerView recyclerView = (RecyclerView) target;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int layoutOrientation = OrientationHelper.VERTICAL;
            if(layoutManager instanceof LinearLayoutManager){
                layoutOrientation = ((LinearLayoutManager) layoutManager).getOrientation();
            } else if(layoutManager instanceof StaggeredGridLayoutManager){
                layoutOrientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            }
            return layoutOrientation !=1 && ViewCompat.canScrollHorizontally(target, direction);
        } else {
            if((target instanceof ViewGroup) && ((ViewGroup) target).getChildCount() > 0){
                ViewGroup vg = (ViewGroup) target;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View v = vg.getChildAt(i);
                    if(checkTouchRange(v, event)){
                        return canScrollHorizontally(vg.getChildAt(i), direction, event);
                    }
                }
            } else {
                return ViewCompat.canScrollHorizontally(target, direction);
            }
        }
        return false;
    }

    /**
     * 判断控件是否可以在垂直方向滚动
     * @param target 控件
     * @param direction 滑动方向，正值为向上，负值为向下
     * @param event 事件
     * @return 能否滚动
     */
    private boolean canScrollVertically(View target, int direction, MotionEvent event){
        if(currentTargetView == null) return false;
        if(target instanceof ViewPager){
            ViewPager viewPager = (ViewPager) target;
            View currentPagerView = null;
            if(viewPager.getAdapter() instanceof DragFragmentPagerAdapter){
                currentPagerView = ((DragFragmentPagerAdapter) viewPager.getAdapter()).getCurrentView();
            } else if(viewPager.getAdapter() instanceof DragFragmentStatePagerAdapter){
                currentPagerView = ((DragFragmentStatePagerAdapter) viewPager.getAdapter()).getCurrentView();
            }
            return currentPagerView != null && canScrollVertically(currentPagerView, direction, event);
        } else if ((target instanceof AbsListView) ||
                (target instanceof RecyclerView) ||
                (target instanceof ScrollView) ||
                (target instanceof NestedScrollView) ||
                (target instanceof WebView)){
            return ViewCompat.canScrollVertically(target, direction);
        } else {
            if((target instanceof ViewGroup) && ((ViewGroup) target).getChildCount() > 0){
                ViewGroup vg = (ViewGroup) target;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View v = vg.getChildAt(i);
                    if(checkTouchRange(v, event)){
                        return canScrollVertically(v, direction, event);
                    }
                }
            } else {
                return ViewCompat.canScrollVertically(target, direction);
            }
        }
        return false;
    }

    private boolean checkTouchRange(View v, MotionEvent event){
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int xRange = v.getRight() - v.getLeft();
        int yRange = v.getBottom() - v.getTop();
        int xMax = location[0] + xRange;
        int yMax = location[1] + yRange;
        return rawX >= location[0] && rawX <= xMax && rawY >= location[1] && rawY <= yMax;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        final int actionMasked = event.getActionMasked();
        mVelocityTracker.addMovement(event);
        switch (actionMasked){
            case MotionEvent.ACTION_DOWN:
                initX = event.getX();
                initY = event.getY();
                if(!mScrollerCompat.isFinished()){
                    mScrollerCompat.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                scroll(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                finishScroll();
                resetVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 拦截事件后进行滚动
     * @param event 事件
     */
    private void scroll(MotionEvent event){
        int orientation = getOrientation();
        int scrollToX = 0;
        int scrollToY = 0;
        float curX = event.getX();
        float curY = event.getY();
        if(mViews.size() > 0){
            switch (orientation) {
                case LinearLayout.HORIZONTAL:
                    int accumulateViewWidth = accumulateViewWidth();
                    if(currentTargetViewIndex== 1){
                        if(curX >= mScrollInterceptX){
                            mScrollInterceptX = curX;
                        }
                        scrollToX = (int) ((mScrollInterceptX - curX) > 0 ? mScrollInterceptX - curX : 0);
                    } else if(currentTargetViewIndex == mViews.size()) {
                        if(curX <= mScrollInterceptX){
                            mScrollInterceptX = curX;
                        }
                        scrollToX = (int) ((accumulateViewWidth - (int)((curX - mScrollInterceptX) > 0 ? (curX - mScrollInterceptX) * mDragDamp : 0)) / mDragDamp);
                    } else {
                        if(!ViewCompat.canScrollHorizontally(currentTargetView, -1)){
                            //向左
                            if(curX <= mScrollInterceptX){
                                mScrollInterceptX = curX;
                            }
                            scrollToX = (int) ((accumulateViewWidth - (int)((curX - mScrollInterceptX) > 0 ? (curX - mScrollInterceptX) * mDragDamp : 0)) / mDragDamp);
                        } else {
                            //向右
                            if (curX >= mScrollInterceptX) {
                                mScrollInterceptX = curX;
                            }
                            scrollToX = (int) ((accumulateViewWidth + (int)((mScrollInterceptX - curX) > 0 ? (mScrollInterceptX - curX) * mDragDamp : 0)) / mDragDamp);
                        }
                    }
                    break;
                case LinearLayout.VERTICAL:
                    int accumulateHeight = accumulateViewHeight();
                    if(currentTargetViewIndex == 1){
                        //结尾处跳跃和边界拖动处理
                        if (curY >= mScrollInterceptY) {
                            mScrollInterceptY = curY;
                        }
                        scrollToY = (int)((mScrollInterceptY - curY) > 0 ? (mScrollInterceptY - curY) : 0);
                    } else if(currentTargetViewIndex == mViews.size()){

                        if(curY <= mScrollInterceptY){
                            mScrollInterceptY = curY;
                        }
                        scrollToY = (int) ((accumulateHeight - (int)((curY - mScrollInterceptY) > 0 ? (curY - mScrollInterceptY) * mDragDamp : 0)) / mDragDamp);
                    } else {
                        if(!ViewCompat.canScrollVertically(currentTargetView, -1)){
                            //向下
                            if(curY <= mScrollInterceptY){
                                mScrollInterceptY = curY;
                            }
                            scrollToY = (int) ((accumulateHeight - (int)((curY - mScrollInterceptY) > 0 ? (curY - mScrollInterceptY) * mDragDamp : 0)) / mDragDamp);
                        } else {
                            //向上
                            if (curY >= mScrollInterceptY) {
                                mScrollInterceptY = curY;
                            }
                            scrollToY = (int) ((accumulateHeight + (int)((mScrollInterceptY - curY) > 0 ? (mScrollInterceptY - curY) * mDragDamp : 0)) / mDragDamp);
                        }
                    }
                    break;
            }
            scrollTo((int)(scrollToX * mDragDamp), (int)(scrollToY * mDragDamp));
            if(mOnDragListener != null){
                mOnDragListener.onDrag(currentTargetView, (int)(scrollToY * mDragDamp), (int)(scrollToX * mDragDamp));
            }
        }
    }

    /**
     * 累加当前页面前面所有页面总高度
     * @return 累加高度
     */
    private int accumulateViewHeight(){
        int accumulateHeight = 0;
        if(currentTargetViewIndex > 1){
            switch (mUseType) {
                case DEFAULT:
                    View lastView = mViews.get(mIntroLayoutResId);
                    accumulateHeight = lastView.getMeasuredHeight();
                    break;
                case CUSTOMIZE:
                    int tempCurrentIndex = currentTargetViewIndex;
                    for (int i = tempCurrentIndex - 1; i > 0; i --){
                        View tempView = mViews.get(i);
                        accumulateHeight = tempView.getMeasuredHeight() + accumulateHeight;
                    }
                    break;
            }
        }
        return accumulateHeight;
    }

    /**
     * 累加当前页面前面所有页面总宽度
     * @return 累加宽度
     */
    private int accumulateViewWidth(){
        int accumulateViewWidth = 0;
        if(currentTargetViewIndex > 1){
            switch (mUseType){
                case DEFAULT:
                    View lastView = mViews.get(mIntroLayoutResId);
                    accumulateViewWidth = lastView.getMeasuredWidth();
                    break;
                case CUSTOMIZE:
                    int tempCurrentIndex = currentTargetViewIndex;
                    for (int i = tempCurrentIndex - 1; i > 0; i --){
                        View tempView = mViews.get(i);
                        accumulateViewWidth = tempView.getMeasuredWidth() + accumulateViewWidth;
                    }
                    break;
            }
        }
        return accumulateViewWidth;
    }

    /**
     * 抬起手指的后续滚动处理
     */
    private void finishScroll(){
        int startX = 0;
        int startY = 0;
        int dx = 0;
        int dy = 0;
        mVelocityTracker.computeCurrentVelocity(1000);
        int orientation = getOrientation();
        switch (orientation) {
            case HORIZONTAL:
                startX = getScrollX();
                float xVelocity = mVelocityTracker.getXVelocity();
                int currentTargetViewWidth = currentTargetView.getMeasuredWidth();
                float baseWidth = currentTargetView.getMeasuredWidth() * mReboundPercent;
                int accumulateWidth = accumulateViewWidth();
                int differenceWidth = startX - accumulateWidth;
                if(Math.abs(xVelocity) >= mMaxFlingVelocity){
                    if(xVelocity > 0){
                        if(startX <= currentTargetViewWidth){
                            dx = -startX;
                        } else {
                            dx = -startX + currentTargetViewWidth;
                        }
                        changeCurrentTargetIndexAndView(false);
                    } else {
                        dx = (accumulateWidth + currentTargetViewWidth) - startX;
                        changeCurrentTargetIndexAndView(true);
                    }
                } else {
                    if(Math.abs(differenceWidth) >= baseWidth){
                        if(differenceWidth < 0){
                            dx = -(currentTargetViewWidth - Math.abs(differenceWidth));
                            changeCurrentTargetIndexAndView(false);
                        } else {
                            dx = currentTargetViewWidth - Math.abs(differenceWidth);
                            changeCurrentTargetIndexAndView(true);
                        }
                    } else {
                        dx = -differenceWidth;
                    }
                }
                break;
            case VERTICAL:
                startY = getScrollY();
                float yVelocity = mVelocityTracker.getYVelocity();
                int currentTargetViewHeight = currentTargetView.getMeasuredHeight();
                float baseHeight = currentTargetViewHeight * mReboundPercent;
                int accumulateHeight = accumulateViewHeight();
                int difference = startY - accumulateHeight;
                if(Math.abs(yVelocity) >= mMaxFlingVelocity){
                    if(yVelocity > 0){
                        //上一页
                        if(startY <= currentTargetViewHeight){
                            dy = -startY;
                        } else {
                            dy = -startY + currentTargetViewHeight;
                        }
                        changeCurrentTargetIndexAndView(false);
                    } else {
                        //下一页
                        dy = (accumulateHeight + currentTargetViewHeight) - startY;
                        changeCurrentTargetIndexAndView(true);
                    }
                } else {
                    if(Math.abs(difference) >= baseHeight){
                        //跳转到另外一个页面
                        if(difference < 0){
                            //上一页
                            dy = -(currentTargetViewHeight - Math.abs(difference));
                            changeCurrentTargetIndexAndView(false);
                        } else {
                            //下一页
                            dy = currentTargetViewHeight - Math.abs(difference);
                            changeCurrentTargetIndexAndView(true);
                        }
                    } else {
                        //回弹
                        dy = -difference;
                    }
                }
                break;
        }
        mScrollerCompat.startScroll(startX, startY, dx, dy, mDuration);
        invalidate();
    }

    /**
     * 改变当前目标view
     * @param isNext 区别是向前改变还是向后改变, 向前false， 向后true
     */
    private void changeCurrentTargetIndexAndView(boolean isNext){
        int viewId = 0;
        if(isNext){
            currentTargetViewIndex += 1;
            switch (mUseType){
                case DEFAULT:
                    currentTargetView = mViews.get(mDetailLayoutResId);
                    viewId = mDetailLayoutResId;
                    break;
                case CUSTOMIZE:
                    currentTargetView = mViews.get(currentTargetViewIndex);
                    viewId = currentTargetViewIndex;
                    break;
            }
        } else {
            currentTargetViewIndex -= 1;
            switch (mUseType){
                case DEFAULT:
                    currentTargetView = mViews.get(mIntroLayoutResId);
                    viewId = mIntroLayoutResId;
                    break;
                case CUSTOMIZE:
                    currentTargetView = mViews.get(currentTargetViewIndex);
                    viewId = currentTargetViewIndex;
                    break;
            }
        }
        if(mEnterDetailLayoutListener != null){
            mEnterDetailLayoutListener.onEnter(viewId);
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void resetVelocityTracker(){
        if(mVelocityTracker != null){
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScrollerCompat.computeScrollOffset()){
            int orientation = getOrientation();
            switch (orientation){
                case LinearLayout.HORIZONTAL:
                    scrollTo(mScrollerCompat.getCurrX(), 0);
                    break;
                case LinearLayout.VERTICAL:
                    scrollTo(0, mScrollerCompat.getCurrY());
                    break;
            }
            postInvalidate();
        }
    }

    /**
     * 获取指定id对应的view
     * @param id 对于第一种情况指的是资源id，对于第二种情况对于的是子布局在父布局中的层级序号
     * @return 返回获取到的view，如果没有找到返回null
     */
    public View getTargetView(int id){
        if(mViews.size() > 0){
            return mViews.get(id);
        }
        return null;
    }

    /**
     * 设置将要显示的页面
     * @param index 页面序号，从1开始
     */
    public void setSelectionItem(int index){
        if(index == currentTargetViewIndex || index > mViews.size() || index < 1) return;
        jumpToSelectedItem(index);
    }

    private void jumpToSelectedItem(int index){
        int startX = getScrollX();
        int startY = getScrollY();
        int dx = 0;
        int dy = 0;
        int orientation = getOrientation();
        int viewId = 0;
        switch (mUseType) {
            case DEFAULT:
                if(orientation == VERTICAL){
                    dy = currentTargetView.getMeasuredHeight();
                } else {
                    dx = currentTargetView.getMeasuredWidth();
                }
                if(index > currentTargetViewIndex){
                    currentTargetView = mViews.get(mDetailLayoutResId);
                    viewId = mDetailLayoutResId;
                } else {
                    currentTargetView = mViews.get(mIntroLayoutResId);
                    viewId = mIntroLayoutResId;
                }
                break;
            case CUSTOMIZE:
                if(index > currentTargetViewIndex){
                    for(int i = currentTargetViewIndex; i < index; i++){
                        View tempView = mViews.get(i);
                        if(orientation == VERTICAL){
                            dy = tempView.getMeasuredHeight() + dy;
                        }else {
                            dx = tempView.getMeasuredWidth() + dx;
                        }
                    }
                } else {
                    for(int i = currentTargetViewIndex; i > index; i--){
                        View tempView = mViews.get(i);
                        if(orientation == VERTICAL){
                            dy = tempView.getMeasuredHeight() + dy;
                        }else {
                            dx = tempView.getMeasuredWidth() + dx;
                        }
                    }
                }
                currentTargetView = mViews.get(index);
                viewId = index;
                break;
        }

        if(index < currentTargetViewIndex){
            dy = -dy;
            dx = -dx;
        } else {
            fullEnd(index);
        }

        currentTargetViewIndex = index;
        mScrollerCompat.startScroll(startX, startY, dx, dy, mDuration);
        postInvalidate();
        if(mEnterDetailLayoutListener != null){
            mEnterDetailLayoutListener.onEnter(viewId);
        }
    }

    private void fullEnd(int index){
        switch (mUseType) {
            case DEFAULT:
                View v = mViews.get(mIntroLayoutResId);
                processEnd(v);
                break;
            case CUSTOMIZE:
                for(int i = currentTargetViewIndex; i < index; i++){
                    View tempV = mViews.get(i);
                    processEnd(tempV);
                }
                break;
        }
    }

    /**
     * 处理控件滑动到终点
     * @param v 待处理控件
     */
    private void processEnd(View v){
        if(v instanceof ScrollView){
            ((ScrollView) v).fullScroll(ScrollView.FOCUS_DOWN);
        } else if(v instanceof NestedScrollView){
            ((NestedScrollView) v).fullScroll(NestedScrollView.FOCUS_DOWN);
        }else if(v instanceof HorizontalScrollView){
            ((HorizontalScrollView) v).fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }else if(v instanceof AbsListView){
            AbsListView absListView = (AbsListView) v;
            ListAdapter adapter = absListView.getAdapter();
            if(adapter != null && absListView.getLastVisiblePosition() < adapter.getCount() - 1){
                ((AbsListView) v).setSelection(adapter.getCount() - 1);
            }
        } else if(v instanceof RecyclerView){
            RecyclerView recyclerView = (RecyclerView) v;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int itemCount = layoutManager.getItemCount();
            if(layoutManager instanceof LinearLayoutManager){
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(itemCount - 1, 0);
            } else {
                layoutManager.scrollToPosition(itemCount - 1);
            }
        } else if(v instanceof ViewPager){
            ViewPager viewPager = (ViewPager) v;
            View currentPagerView = null;
            if(viewPager.getAdapter() instanceof DragFragmentPagerAdapter){
                currentPagerView = ((DragFragmentPagerAdapter) viewPager.getAdapter()).getCurrentView();
            } else if(viewPager.getAdapter() instanceof DragFragmentStatePagerAdapter){
                currentPagerView = ((DragFragmentStatePagerAdapter) viewPager.getAdapter()).getCurrentView();
            }
            if(currentPagerView != null){
                processEnd(currentPagerView);
            }
        }else if(v instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) v;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);
                if((childView instanceof ScrollView) || (childView instanceof ScrollingView) || (childView instanceof AbsListView)){
                    processEnd(childView);
                }
            }
        }
    }

    public void setOnEnterDetailLayoutListener(EnterDetailLayoutListener enterDetailLayoutListener){
        this.mEnterDetailLayoutListener = enterDetailLayoutListener;
    }

    public void setOnDragScrollListener(DragScrollListener dragScrollListener){
        this.mDragScrollListener = dragScrollListener;
        setInternalScrollChangedListener(currentTargetView);
    }

    public void setOnDragListener(OnDragListener onDragListener){
        this.mOnDragListener = onDragListener;
    }

    /**
     * 进入某个详情layout的监听，可以通过该监听来实现特定页面的数据懒加载功能
     */
    public interface EnterDetailLayoutListener{
        /**
         * 当进入某个detailLayout的时候回调
         * @param id layout的id或者layout在DragToDetailLayout中的层级序号
         */
        void onEnter(int id);
    }

    /**
     * 一个简便滚动监听(推荐通过{@link DragToDetailLayout#getTargetView(int)} 获取到相关页面自行监听)
     * 该监听器仅监听第一页的滚动情况（ScrollView, ListView, RecyclerView, HorizontalScrollView, NestedScrollView）
     * 其中ListView,RecyclerView只在Android版本为M以上才监听，需要全版本监听的可以自行重写onScrollChanged进行处理
     * ScrollView,HorizontalScrollView,NestedScrollView如需全版本监听则需要继承{@link CanListenerScrollView},
     * {@link CanListenerHorizontalScrollView}, {@link CanListenerNestScrollView}等对应父类
     * 可通过{@link DragToDetailLayout#getTargetView(int)}方法获取到相关页面view进行监听
     *
     */
    public interface DragScrollListener{
        /**
         * 当布局滚动的时候回调
         * @param v 当前滚动的view
         * @param distanceY 垂直方向滑动距离
         * @param distanceX 水平方向滑动距离
         */
        void onScrollChanged(View v, float distanceY, float distanceX);
    }

    /**
     * 拖拽监听
     * 通过设置该监听器可以对拖拽过程进行监听来实现特定需求
     */
    public interface OnDragListener{
        /**
         * 拖拽监听回调方法
         * @param dragView 当前拖拽控件
         * @param distanceY 垂直拖拽距离
         * @param distanceX 水平拖拽距离
         */
        void onDrag(View dragView, float distanceY, float distanceX);
    }
}
