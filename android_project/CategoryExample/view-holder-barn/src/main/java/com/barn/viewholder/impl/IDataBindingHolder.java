package com.barn.viewholder.impl;

import android.databinding.ViewDataBinding;

public interface IDataBindingHolder {
    void executeBind(ViewDataBinding bindingView, Object data);
    void setViewModel(ViewDataBinding bindingView);
}
