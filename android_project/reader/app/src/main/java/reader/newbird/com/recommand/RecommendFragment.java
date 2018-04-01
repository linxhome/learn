package reader.newbird.com.recommand;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import reader.newbird.com.R;
import reader.newbird.com.base.thread.ThreadManager;

/**
 * A fragment representing a list of Items.
 * interface.
 */
public class RecommendFragment extends Fragment {
    private WebView mWebView;

    public RecommendFragment() {

    }

    public static RecommendFragment newInstance() {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        // Set the adapter
        if (view instanceof WebView) {
            mWebView = (WebView) view;
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
        }
        ThreadManager.getInstance().postUI(new Runnable() {
            @Override
            public void run() {
                loadUrl("http://dushu.m.baidu.com");
            }
        });
        return view;
    }

    private void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }


}
