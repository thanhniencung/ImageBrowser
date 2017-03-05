package imagebrowser.kiennguyen.com.imagebrowser;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kiennguyen.imagebrowser.ImageDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private AppCompatActivity context;
    private List<String> imageList = new ArrayList<>();

    public ImageAdapter(AppCompatActivity context) {
        this.context = context;
    }

    public void setData(List<String> imageList) {
        this.imageList.addAll(imageList);
        notifyDataSetChanged();
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.adapter_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        holder.bind(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;

        public ImageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void bind(String image) {
            Picasso.with(context)
                    .load(image)
                    .into(imageView);
        }

        @Override
        public void onClick(View v) {
            ImageDialogFragment.showImages(context.getSupportFragmentManager(), getAdapterPosition(), imageView, imageList);
        }
    }
}
