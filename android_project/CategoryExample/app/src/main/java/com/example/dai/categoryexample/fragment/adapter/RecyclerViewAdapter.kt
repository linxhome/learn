package com.example.dai.categoryexample.fragment.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.dai.categoryexample.R

import com.example.dai.categoryexample.fragment.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.item_two_text.view.*

open class RecyclerViewAdapter(
        private val mValues: List<DummyItem>)
    : RecyclerLoadMoreAdapter() {

    override fun doGetItemViewType(position: Int): Int {
        return 1
    }

    override fun doGetItemCount(): Int {
        return mValues.size
    }

    override fun doOnBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mValues[position]
        if (holder is ViewHolder) {
            holder.mIdView.text = item.id
            holder.mContentView.text = item.content
            holder.mButton.setOnClickListener { Toast.makeText(it.context, "show click", Toast.LENGTH_LONG).show() }
        }
    }

    override fun doOnCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_two_text, parent, false)
        return ViewHolder(view)//To change body of created functions use File | Settings | File Templates.
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.findViewById(R.id.item_number)
        val mContentView: TextView = mView.findViewById(R.id.content)
        val mButton: Button = mView.findViewById(R.id.button)

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
