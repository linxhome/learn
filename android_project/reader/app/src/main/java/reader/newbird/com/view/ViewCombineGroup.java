package reader.newbird.com.view;

import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by new bird on 2018/5/17.
 * Comment: 控制一组view的显示和隐藏，同时可以与其他view建立附属关系
 */
public class ViewCombineGroup {

    private List<View> mGroups;

    //存在树依赖关系的view group，父show 子不动,父hide 子全部 hide;兄show 弟hide;兄hide 弟不动；
    private List<ViewCombineGroup> mChildren;
    private ViewCombineGroup mParent;

    public static ViewCombineGroup createGroup(View... views) {
        ViewCombineGroup group = new ViewCombineGroup();
        group.mGroups = new ArrayList<>();
        if (views != null) {
            Collections.addAll(group.mGroups, views);
        }
        return group;
    }

    public void recycle() {
        if (mChildren != null) {
            mChildren.clear();
        }
        mParent = null;
        if (mGroups != null) {
            mGroups.clear();
        }
    }


    public void addChild(ViewCombineGroup child) {
        if (child == null) {
            return;
        }
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.add(child);
        child.mParent = this;
    }

    //父show 子不动;兄show 弟hide;子show的前提是父是show
    private void show() {
        if (mParent != null) {
            if (!mParent.isShow()) {
                return;
            }
        }

        for (View view : mGroups) {
            view.setVisibility(View.VISIBLE);
        }
        if (mParent != null) {
            List<ViewCombineGroup> brothers = mParent.mChildren;
            if (brothers != null) {
                for (ViewCombineGroup brother : brothers) {
                    if (brother != this) {
                        brother.hide();
                    }
                }
            }
        }
    }

    //父hide 子全部 hide;兄hide 弟不动
    public void hide() {
        for (View view : mGroups) {
            view.setVisibility(View.GONE);
        }
        if (mChildren != null) {
            for (ViewCombineGroup child : mChildren) {
                child.hide();
            }
        }
    }

    private boolean isShow() {
        if (mGroups != null && mGroups.size() > 0) {
            return mGroups.get(0).getVisibility() == View.VISIBLE;
        }
        return false;
    }

    public void toggleShow() {
        if (isShow()) {
            hide();
        } else {
            show();
        }
    }


}
