package vn.edu.usth.flickrapp.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import vn.edu.usth.flickrapp.Model.SlideItem;
import vn.edu.usth.flickrapp.R;

public class SlideAdapter extends PagerAdapter {

    private List<SlideItem> slideItems;

    public SlideAdapter(List<SlideItem> slideItems) {
        this.slideItems = slideItems;
    }

    @Override
    public int getCount() {
        return slideItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(container.getContext(), R.layout.slide_item, null);

        TextView titleTextView = view.findViewById(R.id.textView_Title);
        TextView contentTextView = view.findViewById(R.id.textView_Content);

        SlideItem slideItem = slideItems.get(position);
        titleTextView.setText(slideItem.getTitle());
        contentTextView.setText(slideItem.getContent());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}


