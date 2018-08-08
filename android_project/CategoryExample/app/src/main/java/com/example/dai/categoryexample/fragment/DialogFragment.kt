package com.example.dai.categoryexample.fragment

import android.app.AlertDialog
import android.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dai.categoryexample.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DialogFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    internal lateinit var dialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view:View =  inflater.inflate(R.layout.dialog_layout, container, false)

        val builder = AlertDialog.Builder(activity)
        dialog = builder.setTitle("试试").setMessage("打开后消失").create()
        val task = Task()
        view.findViewById(R.id.dialog_button).setOnClickListener(View.OnClickListener {
            if (task.status != AsyncTask.Status.FINISHED) {
                task.execute()
            }
        })

        dialog.dismiss()


        //dialog leak window problem
        val builder1 = AlertDialog.Builder(activity)
        val dialog2 = builder1.setTitle("will leak").setMessage("try to destroy the activity").create()
        val button2 = view.findViewById(R.id.create_button2)
        button2.setOnClickListener {
            dialog2.show()
            postDelayDestroy()
        }
        return view;
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


    private fun postDelayDestroy() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ this@DialogFragment.activity.finish() }, 3000)
    }

    internal inner class Task : AsyncTask<Int, Int, String>() {
        override fun doInBackground(vararg params: Int?): String {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return ""
        }


        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            dialog.hide()
        }
    }
}
