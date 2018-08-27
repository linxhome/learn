package com.example.dai.categoryexample.fragment.viewholder;

import android.databinding.ViewDataBinding;

import com.barn.viewholder.annotation.BindItemView;
import com.barn.viewholder.impl.IDataBindingHolder;
import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.databinding.ActivityDataBindingBinding;

@BindItemView(view_id = R.layout.activity_data_binding)
public class TypeThreeViewHolder implements IDataBindingHolder {

    @Override
    public void executeBind(ViewDataBinding bindingView, Object data) {
        ActivityDataBindingBinding binding = (ActivityDataBindingBinding) bindingView;

        binding.textview1.setText("Hello databinding");
    }

    @Override
    public void setViewModel(ViewDataBinding bindingView) {
        ActivityDataBindingBinding binding = (ActivityDataBindingBinding) bindingView;
        //todo set your own view model or do nothing
        binding.setData(null);
    }
}
