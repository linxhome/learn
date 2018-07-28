package com.example.dai.categoryexample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dai.categoryexample.R
import com.example.dai.categoryexample.fragment.adapter.RecyclerViewAdapter

import com.example.dai.categoryexample.fragment.dummy.DummyContent
import com.example.dai.categoryexample.fragment.dummy.DummyContent.DummyItem

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 */
class NestScrollFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nest_scroll, container, false)
    }
}
