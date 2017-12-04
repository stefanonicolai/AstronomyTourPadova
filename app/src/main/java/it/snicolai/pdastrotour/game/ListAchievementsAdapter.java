package it.snicolai.pdastrotour.game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import it.snicolai.pdastrotour.R;

public class ListAchievementsAdapter extends RecyclerView.Adapter<ListAchievementsAdapter.ViewHolder> {
    
    static final String TAG = "ListAchievementsAdapter";
    
    private Context context;
    private ArrayList<HashMap<String, String>> arrayListHashMapAchievement;
    
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewAchievementId;
        private TextView textViewAchievementName;
        private ImageView achievementTrophy;
        private LinearLayout listViewAchievements;
        private ImageView achievementStatus;
    
    
        public ViewHolder(View view) {
            super(view);
            // Get all views you need to handle from the cell and save them in the view holder
            textViewAchievementId = (TextView) view.findViewById(R.id.textViewAchievementId);
            textViewAchievementName = (TextView) view.findViewById(R.id.textViewAchievementName);
            achievementTrophy = (ImageView) view.findViewById(R.id.imageViewTrophy);
            listViewAchievements = (LinearLayout) view.findViewById(R.id.linearLayoutTrophies);
        }
    }
    
    public ListAchievementsAdapter(Context context, ArrayList<HashMap<String, String>> arrayListHashMapAchievement) {
        super();
        this.context = context;
        this.arrayListHashMapAchievement = arrayListHashMapAchievement;
    }
    
    // Create new views (invoked by the layout manager)
    @Override
    public ListAchievementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View textView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_achievement, parent, false);
        
        // Set the view's size, margins, paddings and layout parameters...
        return new ViewHolder(textView);
    }
    
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    
        HashMap<String, String> map = arrayListHashMapAchievement.get(position);
        
        // Others strings
        holder.textViewAchievementId.setText(map.get("achievementId"));
        holder.textViewAchievementName.setText(map.get("achievementName"));
    
        // List of Achievements
        ImageView image = null;
        int achievementTotalCompleted = Integer.valueOf(map.get("achievementTotalCompleted"));
        int achievementTotalReached = Integer.valueOf(map.get("achievementTotalReached"));
        int achievementTotalVoid = Integer.valueOf(map.get("achievementTotalVoid"));
    
        for(int i=0; i < achievementTotalCompleted; i++)
        {
            image = new ImageView(this.context);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setMaxHeight(20);
            image.setMaxWidth(20);
            image.setImageDrawable(this.context.getDrawable(R.drawable.ic_star_full));
        
            // Adds the view to the layout
            holder.listViewAchievements.addView(image);
        }
        for(int i=0; i < achievementTotalReached; i++)
        {
            image = new ImageView(this.context);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setMaxHeight(20);
            image.setMaxWidth(20);
            image.setImageDrawable(this.context.getDrawable(R.drawable.ic_star_half));
        
            // Adds the view to the layout
            holder.listViewAchievements.addView(image);
        }
        for(int i=0; i < achievementTotalVoid; i++)
        {
            image = new ImageView(this.context);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setMaxHeight(30);
            image.setMaxWidth(30);
            image.setImageDrawable(this.context.getDrawable(R.drawable.ic_star_empty));
        
            // Adds the view to the layout
            holder.listViewAchievements.addView(image);
        }
    
        // Trophy Image
        holder.achievementTrophy.setImageDrawable(context.getResources().getDrawable(Integer.valueOf(map.get("achievementTrophy"))));
    }
    
    @Override
    public int getItemCount() {
        return arrayListHashMapAchievement.size();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
}



