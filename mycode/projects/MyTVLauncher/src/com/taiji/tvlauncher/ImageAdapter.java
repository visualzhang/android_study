package com.taiji.tvlauncher;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
    /*myContext为上下文*/
    private Context context;
    private int focus_pos;
    int     text_focus_color=Color.rgb(255, 255, 255);
    int     text_unfocus_color=Color.rgb(120, 120, 120);
    int     text_size;
    private Integer[] image_focus;
    private Integer[] image_unfocus;
    private Integer[] text_id;
    int default_focus;
    // 这是图片资源ID的数组
    static class ViewHolder {        
        ImageView image;
        TextView text;
    }
    /*构造方法*/
    public ImageAdapter(Context context,Integer[] imageFocus,Integer[] imageUnfocus,
    		Integer[] textId, int textSize) {
    	// TODO Auto-generated constructor stub
        this.context = context;
        image_focus = imageFocus;
        image_unfocus = imageUnfocus;
        text_id = textId;
        text_size = textSize;
        /*传入一个Context，本例中传入的是GridViewTest */
    }
    /* 设置默认选择的item */
    public void setDefaultFocus(int pos){
    	default_focus = pos;
    	focus_pos = pos;
    }
    /*返回资源ID数组长度*/
    @Override
    public int getCount() {
    	// TODO Auto-generated method stub
        return image_focus.length;
    }
    /*得到Item*/
    @Override
    public Object getItem(int position) {
    	// TODO Auto-generated method stub
        return position;
    }

    public void notifyDataSetChanged(int pos){
    	focus_pos = pos;
    	notifyDataSetChanged();
    }
    /*获取Items的ID*/
    @Override
    public long getItemId(int position) {
    	// TODO Auto-generated method stub
        return position;
    }
    /*获取要显示的View对象*/
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	// TODO Auto-generated method stub
		ViewHolder vHolder = null;  /*解决内存泄露问题*/
        //如果convertView对象为空则创建新对象，不为空则复用  
		if (convertView == null) {  
			convertView = View.inflate(context, R.layout.menu_item, null);
        	
            RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.item_relative);
            
            vHolder = new ViewHolder();
            vHolder.image = (ImageView) rl.findViewById(R.id.itemImage);

            vHolder.text = (TextView) rl.findViewById(R.id.itemText);
            convertView.setTag(vHolder);
		}
        else{
        	vHolder = (ViewHolder)convertView.getTag();
        }
		vHolder.image.setImageResource(image_focus[position]);
		vHolder.image.setScaleType(ScaleType.CENTER);
		
        vHolder.text.setText(text_id[position]);
        vHolder.text.setTextSize(text_size);
        
        //image.setScaleType(ScaleType.CENTER_CROP);
        //image.setAdjustViewBounds(true);
        //image.setBackground(getResources().getDrawable(R.drawable.item_focus_frame));
        if(focus_pos != position){
        	vHolder.image.setImageResource(image_unfocus[position]);
        	vHolder.text.setTextColor(text_unfocus_color);
        }
        else {
        	vHolder.image.setImageResource(image_focus[position]);
        	vHolder.text.setTextColor(text_focus_color);
			//image.setBackground(getResources().getDrawable(R.drawable.frame_focus));
			//image.setBackground(getResources().getDrawable(R.drawable.item_focus_frame));
		}
		return convertView;
    }
}