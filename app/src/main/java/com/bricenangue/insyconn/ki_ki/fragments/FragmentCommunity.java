package com.bricenangue.insyconn.ki_ki.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bricenangue.insyconn.ki_ki.CheckTimeStamp;
import com.bricenangue.insyconn.ki_ki.Models.Post;
import com.bricenangue.insyconn.ki_ki.R;
import com.bricenangue.insyconn.ki_ki.UserSharedPreference;
import com.bricenangue.insyconn.ki_ki.activities.CreatePostActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCommunity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private UserSharedPreference userSharedPreference;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private PopupMenu popupMenu;
    public FragmentCommunity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        userSharedPreference=new UserSharedPreference(getContext());
        if (auth!=null){
            user=auth.getCurrentUser();
        }

        imageLoader=ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
               // .showImageForEmptyUri(fallback)
                // .showImageOnFail(fallback)
               // .showImageOnLoading(fallback)
                .build();


    }

    public static FragmentCommunity newInstance() {

        return new FragmentCommunity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_community, container, false);
        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.fragment_community_refresh_layout);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview_fragment_community);
        FloatingActionButton fabButton = (FloatingActionButton) view.findViewById(R.id.fab_community_fragment);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(this);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreatePostActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        //refresh layout here
        refreshLayout.setRefreshing(true);
        final Query reference= FirebaseDatabase.getInstance().getReference().child("Posts");

        FirebaseRecyclerAdapter<Post,PostViewHolder> adapter=
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                        Post.class,
                        R.layout.community_card_view_layout,
                        PostViewHolder.class,
                        reference
                ) {
                    @Override
                    protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, int position) {

                        assert model!=null;
                        if(getItemCount()==0){
                           refreshLayout.setRefreshing(false);
                        }else if (position==getItemCount()-1){
                            refreshLayout.setRefreshing(false);
                        }



                            if (model.getText().length()>160){
                                String title=model.getText().substring(0,158)+"...";
                                viewHolder.text.setText(title);
                            }else {
                                viewHolder.text.setText(model.getText());
                            }

                            if (model.getCreator_name()!=null){
                                viewHolder.username.setText(model.getCreator_name());
                            }else {
                                viewHolder.username.setText(getString(R.string.Unknown_user));
                            }
                            if (model.getPictureUrl()!=null){
                                viewHolder.postPicture.setVisibility(View.VISIBLE);
                                imageLoader.displayImage(model.getPictureUrl(), viewHolder.postPicture,options);
                            }else {
                                viewHolder.postPicture.setVisibility(View.GONE);
                            }

                        if(model.getCreate_pic_URL()!=null){
                            /*
                            Picasso.with(getActivity()).load(model.getCreate_pic_URL()).networkPolicy(NetworkPolicy.OFFLINE)
                                    .fit().centerInside()
                                    .into(viewHolder.userPicture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getActivity()).load(model.getCreate_pic_URL())
                                                    .fit().centerInside().into(viewHolder.userPicture);

                                        }
                                    });

                            */
                            Glide.with(getActivity())
                                    .load(model.getCreate_pic_URL())
                                    .asBitmap()
                                    .fitCenter()
                                    .into(viewHolder.userPicture);

                        }else {
                            viewHolder.userPicture.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
                        }




                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });


                            Date date = new Date(model.getTimeofCreation());
                            DateFormat formatter = new SimpleDateFormat("HH:mm");
                            String dateFormatted = formatter.format(date);

                           final CheckTimeStamp checkTimeStamp= new CheckTimeStamp(getActivity(),model.getTimeofCreation());

                            viewHolder.time.setText(checkTimeStamp.checktime());



                            viewHolder.button_like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//
                                }
                            });

                            viewHolder.button_edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    popupMenu=new PopupMenu(getActivity(),viewHolder.button_edit);
                                    popupMenu.getMenuInflater().inflate(R.menu.menu_community_pop_up,popupMenu.getMenu());
                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if (item.getItemId()==R.id.action_edit_post){
                                                //edit post
                                                return true;
                                            }
                                            if (item.getItemId()==R.id.action_delete_post){

                                                DatabaseReference refPost= FirebaseDatabase.getInstance().getReference()
                                                        .child("Posts")
                                                        .child(model.getPostUniqueId());
                                                refPost.setValue(null);
                                                return true;
                                            }
                                            return false;
                                        }
                                    });
                                    popupMenu.show();

                                }
                            });


                            viewHolder.button_comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {



                                }
                            });

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (!haveNetworkConnection()){
            refreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),getString(R.string.alertDialog_no_internet_connection),Toast.LENGTH_SHORT).show();
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        TextView text, time, username;
        private View view;
        private ImageButton button_like,button_edit,button_comment;
        private ImageView userPicture, postPicture;
        private int MENU_ITEM_VIEW_TYPE =0;
        private int AD_VIEW_TYPE=1;


        public PostViewHolder(View itemView) {
            super(itemView);
            view=itemView;

            postPicture=(ImageView) itemView.findViewById(R.id.fragment_community_card_view_body_picture);
            userPicture=(ImageView) itemView.findViewById(R.id.fragment_community_card_userpicture);

            button_like=(ImageButton) itemView.findViewById(R.id.fragment_community_card_view_footer_btn_like);
            button_edit=(ImageButton) itemView.findViewById(R.id.fragment_community_card_edit);
            button_comment=(ImageButton) itemView.findViewById(R.id.fragment_community_card_view_footer_btn_comment);

            username=(TextView)itemView.findViewById(R.id.fragment_community_card_username);
            text=(TextView) itemView.findViewById(R.id.fragment_community_card_view_body_text);
            time=(TextView) itemView.findViewById(R.id.fragment_community_card_time);

        }
    }
    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder{

        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }

}
