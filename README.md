DragToDetail
=
一个用以实现拖拉查看更多内容的自定义控件（可以实现如电商应用详情页面，拖拉查看商品详情描述），继承自LinearLayout
-----------

特性
=
1.支持大于两个子页面<br>
2.支持竖向拖拉，支持横向拖拉<br>
3.支持弹性拖拉
4.支持常见的ListView、ScrollView、HorizontalScrollView、NestedScrollView、RecyclerView、ViewPager、WebView等控件的组合使用（详情看下面效果图）<br>
5.支持页面进入监听、支持滚动监听（仅监听第一个页面，详细请看使用说明 4）、支持拖拽监听<br>
6.支持跳转到指定页面<br>
7.更多特性<br>
<br>

1.效果图1
<br>
<br>
![DragToDetail](https://github.com/workdawn/DragToDetail/blob/master/gif/1.gif)

---------

2.效果图2
<br>
<br>
![DragToDetail](https://github.com/workdawn/DragToDetail/blob/master/gif/4.gif)

---------

3.效果图3
<br>
<br>
![DragToDetail](https://github.com/workdawn/DragToDetail/blob/master/gif/2.gif)

---------

4.效果图4
<br>
<br>
![DragToDetail](https://github.com/workdawn/DragToDetail/blob/master/gif/3.gif)

------
<br>

使用说明
-------
1.相关属性
```
        <declare-styleable name="DragToDetailLayout">
           <!-- 拖拽阻尼系数 -->
           <attr name="dragDamp" format="float"/>
           <!-- 上部或左部介绍页面布局-->
           <attr name="introLayout" format="reference"/>
           <!-- 下部或右部详情页面布局-->
           <attr name="detailLayout" format="reference"/>
           <!-- 回弹滚动持续时间 单位：毫秒 -->
           <attr name="reboundDuration" format="integer"/>
           <!-- 回弹临界比率，用于确定拖动多少距离跳转到下一页面 -->
           <attr name="reboundPercent" format="float"/>
       </declare-styleable>
```
（1.1）.dragDamp：拖拽阻尼系数，表示拖拉布局的阻力大小（0.0f - 1.0f）之间，越小阻力越小，说明越容易拖拽<br>
<br>
（1.2）.introLayout：以方法一（详情请看使用DragToDetail的两种方法）使用控件时候的第一页布局文件引用，它表示一个layout文件<br>
<br>
（1.3）.detailLayout：以方法一（详情请看使用DragToDetail的两种方法）使用控件时候的第二页布局文件引用，它表示一个layout文件<br>
<br>
（1.4）.reboundDuration：拖动放手后布局得回弹或者跳转到下一页的持续时间，单位毫秒<br>
<br>
（1.5）.reboundPercent：跳转到下一页所需要的拖动临界距离百分比，值越大说明需要拖动更多距离才能出发跳转下一页<br>
<br>
<br>
2.两种使用方法
<br>
<br>
（2.1）.通过introLayout、detailLayout属性来配置相关页面，这种方式只支持两个页面，优先级比使用自定义节点要低（意味着如果同时配置了这两个页面属性和自定义布局子节点，那么控件会忽略这两个布局属性），这两个布局属性只有都配置才有效，只配置其中一个的话控件会认为没有该属性
```
        <com.workdawn.dragtodetaillayout.DragToDetailLayout
           android:layout_width="match_parent"
           android:layout_height="wrap_content"
           android:orientation="vertical"
           app:detailLayout="@layout/detail_layout"
           app:introLayout="@layout/intro_layout" />           
```
（2.2）.通过自定义子布局节点，跟使用普通的LinearLayout一样
```
        <com.workdawn.dragtodetaillayout.DragToDetailLayout
           android:id="@+id/dd_test"
           android:layout_width="match_parent"
           android:layout_height="wrap_content"
           android:orientation="vertical">
   
           <include layout="@layout/intro_layout" />
   
           <include layout="@layout/middle_layout" />
   
           <include layout="@layout/detail_layout" />
   
       </com.workdawn.dragtodetaillayout.DragToDetailLayout>
```
重要说明：
=
说明（1）.因为该自定义控件继承自LinearLayout，所以控件的拖拽方向也同样跟随LinearLayout，也就是说如果你需要一个垂直的拖拉效果那么应该设置DragToDetail的 `android:orientation="horizontal"` 同时内部的子布局也要设置为垂直方向排列（详细使用方法可以下载demo查看）<br>
<br>
说明（2）.如果需要一个水平方向的拖拉效果那么同理需要设置布局的方向为 `android:orientation="horizontal"` ，同时内部的布局方向也为 `android:orientation="horizontal"` ，当前水平方向的拖拽支持HorizontalScrollView、RecyclerView（布局方向水平）、ViewPager等的组合，其他垂直方向特性的控件如：ScrollView、ListView不支持<br>
<br>
说明（3）.如果组合中有ViewPager + Fragment的话，要想成功的拖拽那么ViewPager中的Fragment适配器必须继承自这里给出的DragFragmentPagerAdapter或者DragFragmentStatePagerAdapter（主要是为了能获取到当前Fragment里面的控件）,ScrollViewAndViewPager页面演示的就是这种情况

<br>

3.布局进入监听<br>
如果想监听进入某个页面得事件可以通过设置EnterDetailLayoutListener监听器来实现
```
        dragToDetailLayout.setOnEnterDetailLayoutListener(new DragToDetailLayout.EnterDetailLayoutListener() {
            @Override
            public void onEnter(int id) {
                Toast.makeText(DragListenerActivity.this, "进入第 " + id + "页", Toast.LENGTH_LONG).show();
            }
        });
```
通过该功能可以实现类似于懒加载的需求，当进入页面得时候才初始化相关布局控件，加载网络数据等等（可以看demo中的ScrollViewAndViewPager页面）
<br>

4.页面滚动监听<br>
控件默认提供了对第一个页面的滚动监听，如需要监听其余页面可以通过控件提供的getTargetView(int id)方法获取到你想要监听滑动的控件来自行添加监听

```
        dragToDetailLayout.setOnDragScrollListener(new DragToDetailLayout.DragScrollListener() {
            @Override
            public void onScrollChanged(View v, float distanceY, float distanceX) {
                tv_scr_distance.setText("垂直方向移动距离 = " + distanceY);
            }
        });
```
有关滑动监听重要说明（因为View的OnScrollChangeListener监听器是在Android的M版本后才加入的所以如果想进行相关全版本的滚动监听那么）
=
（1）.用CanListenerScrollView代替ScrollView
<br>
<br>
（2）.用CanListenerHorizontalScrollView代替HorizontalScrollView
<br>
<br>
（3）.用CanListenerNestScrollView代替NestScrollView
<br>

5.拖拽监听<br>
通过设置 `OnDragListener` 可以监听布局得拖拽过程
```
        dragToDetailLayout.setOnDragListener(new DragToDetailLayout.OnDragListener() {
            @Override
            public void onDrag(View dragView, float distanceY, float distanceX) {
                tv_scr_distance.setText("垂直方向拖拽距离 = " + distanceY);
            }
        });
```
<br>

6.跳转到特定页面，详细请查看SelectItemActivity

```
dragToDetailLayout.setSelectionItem(index);
```