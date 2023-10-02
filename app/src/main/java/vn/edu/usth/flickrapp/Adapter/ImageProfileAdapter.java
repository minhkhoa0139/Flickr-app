package vn.edu.usth.flickrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.PhotoDetailActivity;
import vn.edu.usth.flickrapp.R;

public class ImageProfileAdapter extends RecyclerView.Adapter<ImageProfileAdapter.ViewHolder> {

    private Context context;
    private List<Image> itemList;
    private User user;

    public ImageProfileAdapter(Context context, List<Image> itemList, User user) {
        this.context = context;
        this.itemList = itemList;
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_image_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image item = itemList.get(position);
        Glide.with(context).load(item.getUri()).into(holder.imageTabProfile);

        holder.imageTabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("imageObject", item);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageTabProfile;

        public ViewHolder(View itemView) {
            super(itemView);
            imageTabProfile = itemView.findViewById(R.id.imageTabProfile);
        }
    }
}
