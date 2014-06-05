package com.truongtvd.danhngon.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.InterstitialAd;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.truongtvd.danhngon.R;
import com.truongtvd.danhngon.model.ItemNewFeed;
import com.truongtvd.danhngon.network.MyVolley;
import com.truongtvd.danhngon.network.NetworkOperator;
import com.truongtvd.danhngon.view.FadeInNetworkImageView;
import com.truongtvd.danhngon.view.OnCloseClickListener;
import com.truongtvd.danhngon.view.OnCommentClickListener;
import com.truongtvd.danhngon.view.OnDownloadClickListener;
import com.truongtvd.danhngon.view.OnLikeClickListener;
import com.truongtvd.danhngon.view.OnSendClickListener;
import com.truongtvd.danhngon.view.OnShareClickListener;

import java.util.ArrayList;


public class DetailAdapter extends PagerAdapter {
    private Context context;
    private ImageLoader imgLoader;
    private DisplayImageOptions options;

    private LayoutInflater inflater;
    private View detailview;
    private ArrayList<ItemNewFeed> listNew = null;
    private ItemNewFeed item;
    private boolean isOpen = false;
    private NetworkOperator operator;
    private ViewHolder viewHolder;
    private ProgressDialog dialog;
    private InterstitialAd interstitial;
    private String MY_AD_UNIT_ID = "ca-app-pub-6063844612770322/7329740094";

    public DetailAdapter(Context context, ArrayList<ItemNewFeed> listNew) {
        this.context = context;
        this.listNew = listNew;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imgLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_default)
                .showImageOnFail(R.drawable.ic_default).cacheInMemory(true)
                .cacheOnDisc(false).displayer(new RoundedBitmapDisplayer(50))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        imgLoader.init(config);
        operator = NetworkOperator.getInstance().init(context);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Sharing...");
        interstitial = new InterstitialAd((Activity) context, MY_AD_UNIT_ID);
        AdRequest request = new AdRequest();
        interstitial.loadAd(request);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listNew.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        // TODO Auto-generated method stub
        return view == (arg1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        viewHolder = new ViewHolder();
        detailview = inflater.inflate(R.layout.layout_detail, container, false);
        item = listNew.get(position);

        viewHolder.btnShare = (RelativeLayout) detailview
                .findViewById(R.id.btnShare);
        viewHolder.btnComment = (RelativeLayout) detailview
                .findViewById(R.id.btnComment);
        viewHolder.btnLike = (RelativeLayout) detailview
                .findViewById(R.id.btnLike);
        viewHolder.tvCountComment = (TextView) detailview
                .findViewById(R.id.tvCountComment);
        viewHolder.tvCountLike = (TextView) detailview
                .findViewById(R.id.tvCountLike);
        viewHolder.comment_detail = (RelativeLayout) detailview
                .findViewById(R.id.comment_detail);
        viewHolder.btnClose = (ImageButton) detailview
                .findViewById(R.id.btnCloseComment);
        viewHolder.load = (ProgressBar) detailview.findViewById(R.id.load);
        viewHolder.lvListComment = (ListView) detailview
                .findViewById(R.id.lvListComment);
        viewHolder.imgMyAvatar = (ImageView) detailview
                .findViewById(R.id.imgMyAvatar);
        viewHolder.edComment = (EditText) detailview
                .findViewById(R.id.edComment);
        viewHolder.btnSend = (Button) detailview.findViewById(R.id.btnSend);
        viewHolder.btnDownload=(RelativeLayout)detailview.findViewById(R.id.btnDownload);
        if (position % 10 == 0) {
            interstitial.show();
        }
        FadeInNetworkImageView imgDetail = (FadeInNetworkImageView) detailview
                .findViewById(R.id.imgDetial);

        imgDetail.setImageUrl(item.getImage(), MyVolley.getImageLoader());

        viewHolder.tvCountComment.setText(context.getString(R.string.comment, item.getComment_count()));
        viewHolder.tvCountLike.setText(context.getString(R.string.like, item.getLike_count()));
        viewHolder.btnShare.setOnClickListener(new OnShareClickListener(
                context, item));
        viewHolder.btnComment.setOnClickListener(new OnCommentClickListener(
                context, viewHolder, item, operator, imgLoader, options));
        viewHolder.btnLike.setOnClickListener(new OnLikeClickListener(context,
                viewHolder, item));
        viewHolder.btnClose.setOnClickListener(new OnCloseClickListener(
                viewHolder));
        viewHolder.btnSend.setOnClickListener(new OnSendClickListener(context,
                viewHolder, item));
        viewHolder.btnDownload.setOnClickListener(new OnDownloadClickListener(context,item));

        ((ViewPager) container).addView(detailview, 0);
        return detailview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }



    public class ViewHolder {
        public RelativeLayout btnShare, btnLike, comment_detail,btnDownload;
        public TextView tvCountLike, tvCountComment;
        public RelativeLayout btnComment;
        public ImageButton btnClose;
        public ProgressBar load;
        public ListView lvListComment;
        public ImageView imgMyAvatar;
        public EditText edComment;
        public Button btnSend;
    }

}
