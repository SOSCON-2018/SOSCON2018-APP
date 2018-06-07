package org.openingsource.soscon.view;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.openingsource.soscon.app.MainActivity;
public abstract class BaseFragment extends Fragment {
    protected View mview;
    protected Context mcontext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       mcontext=MainActivity.mainActivity;
       mview=inflater.inflate(GetLayoutId(),null);
       PreGetData();
       InitView();
       return mview;
    }

    protected abstract void InitView();
    public abstract int GetLayoutId();
    protected abstract void PreGetData();
}
