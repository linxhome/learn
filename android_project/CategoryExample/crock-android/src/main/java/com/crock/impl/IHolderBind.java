package com.crock.impl;

import android.view.View;

public interface IHolderBind<T> {
    void executeBind(View rootView, T data);
}
