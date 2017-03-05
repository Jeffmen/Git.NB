package com.example.gitnb.module.custom;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import com.example.gitnb.R;

/**
 * Created by Rain on 16/4/17.
 */
public class ShareActionProvider extends ActionProvider {
    private ContextWrapper mContextWrapper;
    private PopupMenu mPopupMenu;

    public ShareActionProvider(Context context) {
        super(context);
        mContextWrapper = (ContextWrapper)context;
    }

    @Override
    public View onCreateActionView() {
        // Inflate the action view to be shown on the action bar.
        LayoutInflater layoutInflater = LayoutInflater.from(mContextWrapper);
        View view = layoutInflater.inflate(R.layout.share_action_provider_view, null);
        ImageView popupView = (ImageView)view.findViewById(R.id.popup_view);
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        return view;
    }

    private void showPopup(View v) {
        mPopupMenu = new PopupMenu(mContextWrapper, v);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }

        });
        MenuInflater inflater = mPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.repos_detail_menu, mPopupMenu.getMenu());
        mPopupMenu.show();
    }
}
