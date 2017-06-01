package com.dms.beinone.application.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dms.beinone.application.views.custom.EmptySupportedRecyclerView;
import com.dms.beinone.application.R;
import com.dms.beinone.application.views.adapters.AfterschoolAdapter;
import com.dms.beinone.application.models.Afterschool;
import com.dms.beinone.application.utils.JSONParser;
import com.dms.beinone.application.managers.RecyclerViewManager;
import com.dms.boxfox.networking.HttpBox;
import com.dms.boxfox.networking.HttpBoxCallback;
import com.dms.boxfox.networking.datamodel.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by BeINone on 2017-01-31.
 */

public class AfterschoolApplyFragment extends Fragment {

    private EmptySupportedRecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_afterschool, container, false);
        init(view);

        return view;
    }

    /**
     * 초기화, RecyclerView 세팅
     *
     * @param rootView 필요한 뷰를 찾을 최상위 뷰
     */
    private void init(View rootView) {
        getActivity().setTitle(R.string.afterschool);

        mRecyclerView = (EmptySupportedRecyclerView) rootView.findViewById(R.id.rv_afterschool);
        View emptyView = rootView.findViewById(R.id.view_afterschool_empty);
        RecyclerViewManager.setupCardRecyclerView(mRecyclerView, getContext(), emptyView);

        try {
            loadAfterschoolItemList();
        } catch (IOException e) {
            System.out.println("IOException in AfterschoolApplyFragment: POST /apply/afterschool/item");
            e.printStackTrace();
        }
    }

    private void loadAfterschoolItemList() throws IOException {
        HttpBox.get(getContext(), "/apply/afterschool/item").push(new HttpBoxCallback() {
            @Override
            public void done(Response response) {
                int code = response.getCode();

                if (code == HttpBox.HTTP_OK) {
                    try {
                        List<Afterschool> afterschools = JSONParser.parseAfterschoolListJSON(response.getJsonObject());
                        mRecyclerView.setAdapter(new AfterschoolAdapter(getContext(), afterschools));
                    } catch (JSONException e) {
                        System.out.println("JSONException in AfterschoolApplyFragment: POST /apply/afterschool/item");
                        e.printStackTrace();
                    }
                } else if (code == HttpBox.HTTP_NO_CONTENT) {
                    Toast.makeText(getContext(), R.string.afterschool_list_empty, Toast.LENGTH_SHORT).show();
                } else if (code == HttpBox.HTTP_INTERNAL_SERVER_ERROR) {
                    Toast.makeText(getContext(), R.string.afterschool_list_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void err(Exception e) {
                System.out.println("Error in AfterschoolApplyFragment: POST /apply/afterschool/item");
                e.printStackTrace();
            }
        });
    }
}
