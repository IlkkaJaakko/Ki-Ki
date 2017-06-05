package com.bricenangue.insyconn.ki_ki;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCommunity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private UserSharedPreference userSharedPreference;

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
                //start activity create a post

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
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

                            if(model.getPictureURL()!=null){

                                viewHolder.postPicture.setVisibility(View.VISIBLE);
                                Picasso.with(getContext()).load(model.getPictureURL()).networkPolicy(NetworkPolicy.OFFLINE)
                                        .fit().centerInside()
                                        .into(viewHolder.postPicture, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                                refreshLayout.setRefreshing(false);
                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(getContext()).load(model.getPictureURL())
                                                        .fit().centerInside().into(viewHolder.postPicture);
                                                refreshLayout.setRefreshing(false);
                                            }
                                        });


                            }else {

                                viewHolder.postPicture.setVisibility(View.GONE);

                            }

                        if(model.getCreator_pic_URL()!=null){
                            Picasso.with(getContext()).load(model.getCreator_pic_URL()).networkPolicy(NetworkPolicy.OFFLINE)
                                    .fit().centerInside()
                                    .into(viewHolder.userPicture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getContext()).load(model.getCreator_pic_URL())
                                                    .fit().centerInside().into(viewHolder.userPicture);

                                        }
                                    });

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

                           // CheckTimeStamp checkTimeStamp= new CheckTimeStamp(getActivity(),model.getPrivateContent().getTimeofCreation());

                           // viewHolder.time.setText(checkTimeStamp.checktime());



                            viewHolder.button_like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//
                                }
                            });

                            viewHolder.button_edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


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

}
