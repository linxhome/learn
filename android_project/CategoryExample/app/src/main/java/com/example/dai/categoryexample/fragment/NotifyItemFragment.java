package com.example.dai.categoryexample.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.fragment.adapter.RecyclerViewAdapter;
import com.example.dai.categoryexample.fragment.dummy.DummyContent;
import com.example.dai.categoryexample.fragment.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NotifyItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotifyItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifyitem_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(DummyContent.INSTANCE.getITEMS());
        mRecyclerView.setAdapter(adapter);

        adapter.setLoadingEnable(true);
        TextView loadingView = new TextView(getContext());
        loadingView.setText("Loading");
        loadingView.setTextColor(getResources().getColor(R.color.black,null));
        adapter.setLoadingView(loadingView);

        view.findViewById(R.id.y_btn).setOnClickListener(v -> {
            mRecyclerView.setTranslationY(100);
        });

        view.findViewById(R.id.top_btn).setOnClickListener(v -> {
            int mTop = mRecyclerView.getTop();
            mRecyclerView.setTop(mTop + 100);
        });

        view.findViewById(R.id.reset_btn).setOnClickListener(v -> {
            mRecyclerView.setTranslationY(0);
            //mRecyclerView.setTop(mTop);
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.sendEmptyMessage(44);


        handler.postDelayed(() -> {
            if (NotifyItemFragment.this.isVisible()) {
                Toast.makeText(getActivity(), "start to add data ", Toast.LENGTH_SHORT);
                RecyclerViewAdapter adapter = (RecyclerViewAdapter) mRecyclerView.getAdapter();
                int count = adapter.getItemCount();
                DummyContent.INSTANCE.getITEMS().add(new DummyItem("1", "lesson 1", "detail"));
                DummyContent.INSTANCE.getITEMS().add(new DummyItem("2", "lesson 2", "detail"));
                adapter.notifyItemInserted(count);
            }

        }, 3000);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressWarnings("unused")
    public static NotifyItemFragment newInstance(int columnCount) {
        NotifyItemFragment fragment = new NotifyItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
