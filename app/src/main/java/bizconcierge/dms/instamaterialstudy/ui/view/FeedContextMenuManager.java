package bizconcierge.dms.instamaterialstudy.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import bizconcierge.dms.instamaterialstudy.ui.utils.Utils;

/**
 * Created by user on 8/19/2017.
 * <p>
 * <p>
 * Only one context menu should be visible on the screen
 * Context menu should appear after we click on “more” button and disappear after we click this button again
 * Context menu should appear right above the clicked button
 * Context menu should disappear when we start scrolling feed
 */

public class FeedContextMenuManager extends RecyclerView.OnScrollListener implements View.OnAttachStateChangeListener {
    private static FeedContextMenuManager instance;

    private FeedContextMenu contextMenuView;
    private boolean isContextMenuShowing;
    private boolean isContextMenuDismissing;

    //    Only one context menu should be visible on the screen
    public static FeedContextMenuManager getInstance() {
        if (instance == null) {
            instance = new FeedContextMenuManager();
        }
        return instance;
    }

    // For showing and hiding view we’ll pretty simple method:
    public void toggleContextMenuFromView(View openingView, int feedItem, FeedContextMenu.OnFeedContextMenuItemClickListener listener) {
        if (contextMenuView == null) {
            showContextMenuFromView(openingView, feedItem, listener);
        } else {
            hideContextMenu();
        }
    }

    //  hiding view while users scroll the feed.
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (contextMenuView != null) {
            hideContextMenu();
            contextMenuView.setTranslationY(contextMenuView.getTranslationY() - dy);
        }
    }

    // prevent from clicking twice
    public void hideContextMenu() {
        if (!isContextMenuDismissing) {
            isContextMenuDismissing = true;
            performDismissAnimation();
        }
    }

    private void performDismissAnimation() {
        contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
        contextMenuView.setPivotY(contextMenuView.getHeight());
        contextMenuView.animate()
                .scaleX(0.1f).scaleY(0.1f)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (contextMenuView != null) {
                            contextMenuView.dismiss();
                        }
                        isContextMenuDismissing = false;
                    }
                });
    }

    private void showContextMenuFromView(final View openingView, int feedItem, FeedContextMenu.OnFeedContextMenuItemClickListener listener) {
        if (!isContextMenuShowing) {
            isContextMenuShowing = true;
            contextMenuView = new FeedContextMenu(openingView.getContext());
            contextMenuView.bindToItem(feedItem);
            contextMenuView.addOnAttachStateChangeListener(this);
            contextMenuView.setOnFeedMenuItemClickListener(listener);

            ViewGroup view = ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content));
            ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(contextMenuView);

            // We have to do this in onPreDraw() callback to make sure that our view is layed out.
            contextMenuView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //  Only then we can use getWidth() or getHeight() methods on our view
                    contextMenuView.getViewTreeObserver().removeOnPreDrawListener(this);
                    setupContextMenuInitialPosition(openingView);
                    performShowAnimation();
                    return false;
                }
            });
        }
    }

    private void setupContextMenuInitialPosition(View openingView) {
        final int[] openingViewLocation = new int[2];
        openingView.getLocationOnScreen(openingViewLocation);
        int additionalBottomMargin = Utils.dpToPx(16);
        contextMenuView.setTranslationX(openingViewLocation[0] - contextMenuView.getWidth() / 3);
        contextMenuView.setTranslationY(openingViewLocation[1] - contextMenuView.getHeight() - additionalBottomMargin);
    }

    private void performShowAnimation() {
        contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
        contextMenuView.setPivotY(contextMenuView.getHeight());
        contextMenuView.setScaleX(0.1f);
        contextMenuView.setScaleY(0.1f);
        contextMenuView.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(150)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isContextMenuShowing = false;
                    }
                });
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        contextMenuView = null;
    }

}
