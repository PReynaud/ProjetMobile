package com.epul.ProjetMobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Dimitri on 24/12/2015.
 * @Version 1.0
 */
public class ListActivity extends Activity {
    private ListView monumentList;
    private ArrayList<Place> places;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monument_list);
        this.monumentList = (ListView) findViewById(R.id.list);
        this.places = getIntent().getParcelableArrayListExtra(MainActivity.wayResource);
        createListView();

        //Animations
        ImageView imageView = (ImageView) findViewById(R.id.expand_icon);
        findViewById(R.id.monumentList).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up));
        imageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_180));

        //Détecteur pour la barre du haut
        final GestureDetectorCompat detector = new GestureDetectorCompat(this, new SwipeDetector() {
            @Override
            public void onTouch() {
                animeAndQuit();
            }

            @Override
            public void onSwipeToDown() {
                animeAndQuit();
            }
        });
        findViewById(R.id.list_top).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
    }

    private void animeAndQuit() {
        RotateAnimation rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rotateAnimation.setDuration(500);

        findViewById(R.id.monumentList).startAnimation(animation);
        findViewById(R.id.expand_icon).startAnimation(rotateAnimation);
    }

    private void createListView() {
        if (!places.isEmpty()) {
            monumentList.setAdapter(new SimpleAdapter(this.getBaseContext(),
                    getPlaces(),
                    R.layout.monument,
                    new String[]{"name", "address"},
                    new int[]{R.id.monumentName, R.id.monumentAddress}));
            monumentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removeMonument(places.get(position));
                    return false;
                }
            });
            monumentList.invalidateViews();
        }
    }

    public boolean removeMonument(Place place) {
        boolean wasRemoved = places.remove(place);
        if (wasRemoved) {
            monumentList.setAdapter(new SimpleAdapter(this.getBaseContext(),
                    getPlaces(),
                    R.layout.monument,
                    new String[]{"name", "address"},
                    new int[]{R.id.monumentName, R.id.monumentAddress}));
            monumentList.invalidateViews();
        }
        return wasRemoved;
    }

    public List<Map<String, String>> getPlaces() {
        List<Map<String, String>> result = new ArrayList<>();
        for (Place place : places) {
            Map<String, String> map = new HashMap<>();
            map.put("name", place.getName());
            map.put("address", place.getVicinity());
            result.add(map);
        }
        return result;
    }
}
