package solutions.doubts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.newprofile.ProfileActivity;
import solutions.doubts.activities.newprofile.UserCache;
import solutions.doubts.activities.newprofile.UserObserver;
import solutions.doubts.api.FollowingResource;
import solutions.doubts.api.models.User;
import solutions.doubts.api.query.Query;
import solutions.doubts.core.util.StringUtil;

public class UserListFragment extends Fragment implements UserObserver {
    private static final String TAG = "UserListFragment";
    private User user;

    @Override
    public void onUserChanged(User user) {
        this.user = user;
        if(this.getActivity() != null) {
            updateData();
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.username) TextView username;
        @InjectView(R.id.name) TextView name;
        @InjectView(R.id.image) SimpleDraweeView image;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private class Adapter extends RecyclerView.Adapter<UserViewHolder> implements View.OnClickListener {
        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
            v.setOnClickListener(this);
            return new UserViewHolder(v);
        }

        @Override
        public void onBindViewHolder(UserViewHolder h, int position) {
            User user = mUsers.get(position);
            h.itemView.setTag(user);
            h.name.setText(user.getName());
            h.username.setText("@"+user.getUsername());
            if(user.getImage().getUrl() != null)
                h.image.setImageURI(Uri.parse(user.getImage().getUrl()));
            else
                h.image.setImageURI(Uri.parse(StringUtil.getProfileImageUrl(user)));
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        @Override
        public void onClick(View v) {
            User user = (User)v.getTag();
            if(user != null) {
                Intent profileIntent = new Intent(UserListFragment.this.getActivity(), ProfileActivity.class);

                View userImage = v.findViewById(R.id.image);
                View userName = v.findViewById(R.id.name);
                Pair<View, String> p1 = Pair.create(userImage, "userImage");
                Pair<View, String> p2 = Pair.create(userName, "userName");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(UserListFragment.this.getActivity(), p1, p2);
                UserCache.getInstance().setLastSelectedUser(user);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    getActivity().startActivity(profileIntent, options.toBundle());
                else
                    startActivity(profileIntent);
            }
        }
    }

    private Context mContext;
    private Adapter mAdapter;
    private List<User> mUsers = new ArrayList<>();

    public UserListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = new RecyclerView(getActivity());
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this.getActivity());
        rv.setLayoutManager(lm);
        mAdapter = new Adapter();
        rv.setAdapter(mAdapter);
        if(user != null) {
            updateData();
        }
        return rv;
    }

    private void updateData() {
        Query.with(this.getActivity())
                .remote(FollowingResource.class)
                .resource("users", user.getId(), user.getUsername())
                .resource("following")
                .getAll(null, null, 0, new FutureCallback<Response<FollowingResource>>() {
                    @Override
                    public void onCompleted(Exception e, Response<FollowingResource> result) {
                        if(result == null)
                            return;
                        if(result.getResult().getFollowing() != null) {
                            mUsers = result.getResult().getFollowing();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
