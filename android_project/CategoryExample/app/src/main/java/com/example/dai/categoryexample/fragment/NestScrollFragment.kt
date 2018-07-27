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
 * [NestScrollFragment.OnListFragmentInteractionListener] interface.
 */
class NestScrollFragment : Fragment() {

    private var columnCount = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nest_scroll, container, false)

        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = RecyclerViewAdapter(DummyContent.ITEMS)
            }
        }
        return view
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: DummyItem?)
    }

}
