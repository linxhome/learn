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

class RecyclerViewAdapter(
        private val mValues: List<DummyItem>)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_two_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.mContentView.text = item.content
        holder.mButton.setOnClickListener { Toast.makeText(it.context,"show click",Toast.LENGTH_LONG).show()}
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content
        val mButton:Button = mView.button

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}