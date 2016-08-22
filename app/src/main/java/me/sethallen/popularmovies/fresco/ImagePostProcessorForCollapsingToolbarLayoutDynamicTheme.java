package me.sethallen.popularmovies.fresco;

import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;

import com.facebook.imagepipeline.request.BasePostprocessor;

public class ImagePostProcessorForCollapsingToolbarLayoutDynamicTheme extends BasePostprocessor {

    CollapsingToolbarLayout mCollapsingToolbarLayout;

    public ImagePostProcessorForCollapsingToolbarLayoutDynamicTheme(CollapsingToolbarLayout collapsingToolbarLayout)
    {
        mCollapsingToolbarLayout = collapsingToolbarLayout;
    }

    @Override
    public String getName() {
        return "ImagePostProcessorForCollapsingToolbarLayoutDynamicTheme";
    }

    @Override
    public void process(Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                    Palette.Swatch swatch;
                    if ((swatch = palette.getVibrantSwatch()) == null) {
                        if ((swatch = palette.getDarkVibrantSwatch()) == null) {
                            if ((swatch = palette.getLightVibrantSwatch()) == null) {
                                if ((swatch = palette.getMutedSwatch()) == null) {
                                    if ((swatch = palette.getDarkMutedSwatch()) == null) {
                                        if ((swatch = palette.getLightMutedSwatch()) == null) {
                                            // No swatches for the image were found
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    int mutedColor = swatch.getRgb();
                    mCollapsingToolbarLayout.setBackgroundColor(mutedColor);
                    mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(mutedColor));
                    mCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(mutedColor));
                    }
                });
    }
}