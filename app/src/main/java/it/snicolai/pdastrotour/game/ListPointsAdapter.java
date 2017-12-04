package it.snicolai.pdastrotour.game;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import it.snicolai.pdastrotour.R;

public class ListPointsAdapter extends RecyclerView.Adapter<ListPointsAdapter.ViewHolder> {
    
    static final String TAG = "ListPointsAdapter";
    
    private Context context;
    private ArrayList<HashMap<String, String>> arrayListHashMapPoint;
    
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPointId;
        private TextView textViewPointName;
        private TextView textViewPointTitle;
        //private TextView textViewPointAddress;
        private ImageView imageViewPointImage;
        private ImageView imageViewStar;
        
        
        public ViewHolder(View view) {
            super(view);
            // Get all views you need to handle from the cell and save them in the view holder
            textViewPointId = (TextView) view.findViewById(R.id.textViewPointId);
            textViewPointName = (TextView) view.findViewById(R.id.textViewPointName);
            textViewPointTitle = (TextView) view.findViewById(R.id.textViewPointTitle);
            //textViewPointAddress = (TextView) view.findViewById(R.id.textViewPointAddress);
            imageViewPointImage = (ImageView) view.findViewById(R.id.imageViewPointImage);
            imageViewStar = (ImageView) view.findViewById(R.id.imageViewStar);
        }
    }
    
    public ListPointsAdapter(Context context, ArrayList<HashMap<String, String>> arrayListHashMapPoint) {
        super();
        this.context = context;
        this.arrayListHashMapPoint = arrayListHashMapPoint;
    }
    
    // Create new views (invoked by the layout manager)
    @Override
    public ListPointsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View textView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_point, parent, false);
        
        // Set the view's size, margins, paddings and layout parameters...
        return new ViewHolder(textView);
    }
    
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListPointsAdapter.ViewHolder holder, int position) {
        
        HashMap<String, String> map = arrayListHashMapPoint.get(position);
        
        // Main Image
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(map.get("pointImage"), "drawable", context.getPackageName());
        //holder.imageViewPointImage.setImageDrawable(res.getDrawable(resourceId));
        Glide.
            with(this.context)
            .load(resourceId)
            .override(210, 140)
            .centerCrop()
            .into(holder.imageViewPointImage);
    
        // Others strings
        holder.textViewPointId.setText(map.get("pointId"));
        holder.textViewPointName.setText(map.get("pointName"));
        //holder.textViewPointAddress.setText(map.get("pointAddress"));
        holder.textViewPointTitle.setText(map.get("pointTitle"));
        //holder.textViewPointReachedTime.setText(Utils.getPrintableDateTime(map.get("pointTitle"), null, ", ")); // Fixing default timestamp
    
        // Star Image
        holder.imageViewStar.setImageDrawable(context.getResources().getDrawable(Integer.valueOf(map.get("pointStar"))));
    }
    
    
    @Override
    public int getItemCount() {
        return arrayListHashMapPoint.size();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
//
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//
//        if (convertView == null) {
//            // Object holding all data of the row we're interested into
//            holder = new ViewHolder();
//
//            // Creating row
//            convertView = inflater.inflate(R.layout.row_list_point, null);
//
//            // Get all views you need to handle from the cell and save them in the view holder
//            holder.textViewPointId = (TextView) convertView.findViewById(R.id.textViewPointId);
//            holder.textViewPointName = (TextView) convertView.findViewById(R.id.textViewPointName);
//            holder.textViewPointTitle = (TextView) convertView.findViewById(R.id.textViewPointTitle);
//            //holder.textViewPointAddress = (TextView) convertView.findViewById(R.id.textViewPointAddress);
//            holder.imageViewPointImage = (ImageView) convertView.findViewById(R.id.imageViewPointImage);
//            holder.imageViewStar = (ImageView) convertView.findViewById(R.id.imageViewStar);
//
//            // save the view holder on the cell view to get it back latter
//            convertView.setTag(holder);
//
//        } else {
//            // the getTag returns the viewHolder object set as a tag to the view
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        //get the record of the array and set in the view of the record
//        map = arrayListHashMapPoint.get(position);
//        holder.ref = position;
//
//
//        // -----------------------------------------------------------------------------------------
//        // Setting up datas for each row
//        // -----------------------------------------------------------------------------------------
//
//
//        // Main Image
//        Resources res = context.getResources();
//        int resourceId = res.getIdentifier(map.get("pointImage"), "drawable", context.getPackageName());
//        //holder.imageViewPointImage.setImageDrawable(res.getDrawable(resourceId));
//        Picasso.
//            with(this.context)
//            .load(resourceId)
//            .resize(210, 140)
//            .into(holder.imageViewPointImage);
//
//        // Others strings
//        holder.textViewPointId.setText(map.get("pointId"));
//        holder.textViewPointName.setText(map.get("pointName"));
//        //holder.textViewPointAddress.setText(map.get("pointAddress"));
//        holder.textViewPointTitle.setText(map.get("pointTitle"));
//        //holder.textViewPointReachedTime.setText(Utils.getPrintableDateTime(map.get("pointTitle"), null, ", ")); // Fixing default timestamp
//
//        // Star Image
//        holder.imageViewStar.setImageDrawable(context.getResources().getDrawable(Integer.valueOf(map.get("pointStar"))));
//
//        return convertView;
//    }
//
}



