package com.epul.ProjetMobile.tools;

import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.adapter.MonumentAdapter;
import com.epul.ProjetMobile.business.Place;

import java.util.ArrayList;

public class ListManager {
    private final ArrayList<ItemStatus> status = new ArrayList<>();
    private ListView monumentList;
    private ArrayList<Place> way;
    private MonumentAdapter adapter;

    public ListManager(ListView monumentList, ArrayList<Place> way) {
        this.monumentList = monumentList;
        this.way = way;
        this.adapter = new MonumentAdapter(monumentList.getContext(), way, status);
        createListView();
    }

    public void createListView() {
        for (int i = 0; i < way.size(); i++) {
            status.add(ItemStatus.Normal);
        }
        //Swipe Détecteur pour les éléments de la liste
        final GestureDetectorCompat detector = new GestureDetectorCompat(monumentList.getContext(), new SwipeDetector() {
            int item = -1;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    item = monumentList.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()));
                    onSwipeToRight();
                    return false; // Left to right
                }
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    item = monumentList.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()));
                    onSwipeToLeft();
                    return false; // Right to left
                }
                return false;
            }

            @Override
            public void onSwipeToLeft() {
                TranslateAnimation animation;
                final View monument = monumentList.getChildAt(item - monumentList.getFirstVisiblePosition());
                switch (status.get(item)) {
                    case Normal:
                        animation = new TranslateAnimation(100, 0, 0, 0);
                        animation.setDuration(300);
                        monument.findViewById(R.id.item_list).setBackgroundResource(R.color.warning);
                        monument.findViewById(R.id.delete).setVisibility(View.VISIBLE);
                        monument.startAnimation(animation);
                        status.set(item, ItemStatus.Delete);
                        break;
                    case Favorite:
                        animation = new TranslateAnimation(0, -100, 0, 0);
                        animation.setDuration(300);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                monument.findViewById(R.id.star).setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        monument.findViewById(R.id.item_list).setBackgroundResource(R.color.windowBackground);
                        monument.startAnimation(animation);
                        status.set(item, ItemStatus.Normal);
                        break;
                    default:
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSwipeToRight() {
                TranslateAnimation animation;
                final View monument = monumentList.getChildAt(item - monumentList.getFirstVisiblePosition());
                switch (status.get(item)) {
                    case Normal:
                        animation = new TranslateAnimation(-100, 0, 0, 0);
                        animation.setDuration(300);
                        monument.findViewById(R.id.item_list).setBackgroundResource(R.color.favorite);
                        monument.findViewById(R.id.star).setVisibility(View.VISIBLE);
                        monument.startAnimation(animation);
                        status.set(item, ItemStatus.Favorite);
                        break;
                    case Delete:
                        animation = new TranslateAnimation(0, 100, 0, 0);
                        animation.setDuration(300);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                monument.findViewById(R.id.delete).setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        monument.findViewById(R.id.item_list).setBackgroundResource(R.color.windowBackground);
                        monument.startAnimation(animation);
                        status.set(item, ItemStatus.Normal);
                        break;
                    default:
                        break;

                }
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new MonumentAdapter(monumentList.getContext(), way, status);
        monumentList.setAdapter(adapter);
        monumentList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        //Touch détecteur selon le status
        monumentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (status.get(position)) {
                    case Normal:
                        //Goto DetailActivity
                        break;
                    case Delete:
                        removeMonument(way.get(position));
                        break;
                    case Favorite:
                        //Add Monument to favorites
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public boolean removeMonument(Place place) {
        int i = way.indexOf(place);
        if (i >= 0) {
            way.remove(place);
            status.remove(i);
            adapter.notifyDataSetChanged();
            monumentList.setAdapter(adapter);
        }
        return i >= 0;
    }
}
