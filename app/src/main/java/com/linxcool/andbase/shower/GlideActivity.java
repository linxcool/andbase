package com.linxcool.andbase.shower;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.linxcool.andbase.BaseActivity;
import com.linxcool.andbase.demo.R;

/**
 * Example for Glide
 * <pre>
 *  <a href="https://github.com/bumptech/glide">https://github.com/bumptech/glide</a>
 *  <a href="https://github.com/wasabeef/glide-transformations">https://github.com/wasabeef/glide-transformations</a>
 * </pre>
 * Created by huchanghai on 2017/3/15.
 */
public class GlideActivity extends BaseActivity implements Shower {

    @Override
    public String getName() {
        return "Glide";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_blank);
        enableHomeback();
        setTitle(getName());

        ImageView iv = new ImageView(this);
        addView(iv);

        Glide.with(this)
                .load("http://sjbz.fd.zol-img.com.cn/t_s480x800c/g5/M00/0F/09/ChMkJlfJQV-ILMZgAAGGYPOGsPsAAU7cQP-K8oAAYZ4504.jpg")
                .centerCrop()
                .crossFade()
                .into(iv);
    }
}
