package bizconcierge.dms.instamaterialstudy.ui.activity;

import android.os.Bundle;

import bizconcierge.dms.instamaterialstudy.R;

/**
 * Created by USER on 7/26/2017.
 */

public class CommentsActivity extends BaseDrawerActivity{
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
    }
}
