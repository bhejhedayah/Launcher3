package com.android.launcher3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends Activity {
    TextView txtTime, txtDate;
    Calendar c;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleTimeFormat;

    PackageManager packageManager;
    public static List<AppInfo> apps;
    GridView grdView;
    ListView listView;
    RecyclerView recyclerView;
    public static ArrayAdapter<AppInfo> adapter;

    LinearLayout containAppDrawer;

    RelativeLayout ContainerHome;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("hh:mm:ss.SSS a");
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDate = (TextView) findViewById(R.id.txtDate);

        containAppDrawer = (LinearLayout) findViewById(R.id.containAppDrawer);
        ContainerHome = (RelativeLayout) findViewById(R.id.ContainerHome);
        HideAppDrawer(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        c = Calendar.getInstance();
                        txtDate.setText(simpleDateFormat.format(c.getTime()));
                        txtTime.setText(simpleTimeFormat.format(c.getTime()));
                    }

                });

            }
        }, 0, 10);
        apps = null;
        adapter = null;
        loadApps();
        loadListView();
        addGridListeners();
    }

    public void addGridListeners() {
        try {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = packageManager.getLaunchIntentForPackage(apps.get(i).name.toString());
                    HomeActivity.this.startActivity(intent);
                }
            });
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " Grid", Toast.LENGTH_LONG).show();
            Log.e("Error Grid", ex.getMessage().toString() + " Grid");
        }

    }


    private void loadListView() {

        try {
            listView = (ListView) findViewById(R.id.grd_allApps);
//            listView.setFastScrollEnabled (true);
            listView.setDivider (null);
            if (adapter == null) {
                adapter = new ArrayAdapter<AppInfo>(this, R.layout.grd_items, apps) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        ViewHolderItem viewHolder = null;

                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(
                                    R.layout.grd_items, parent, false
                            );
                            viewHolder = new ViewHolderItem();
                            viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_icon);
                            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name);
                            viewHolder.label = (TextView) convertView.findViewById(R.id.txt_label);

                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolderItem) convertView.getTag();
                        }

                        AppInfo appInfo = apps.get(position);

                        if (appInfo != null) {
                            viewHolder.icon.setImageDrawable(appInfo.icon);
                            viewHolder.label.setText(appInfo.label);
                            viewHolder.name.setText(appInfo.name);
                        }
                        return convertView;

                    }

                    final class ViewHolderItem {
                        ImageView icon;
                        TextView label;
                        TextView name;
                    }
                };
            }

            listView.setAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " loadListView", Toast.LENGTH_LONG).show();
            Log.e("Error loadListView", ex.getMessage().toString() + " loadListView");
        }

    }

    private void loadApps() {
        try {
            if (packageManager == null)
                packageManager = getPackageManager();
            if (apps == null) {
                apps = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                Collections.sort (availableApps, new Comparator<ResolveInfo> () {
                    @Override
                    public int compare(ResolveInfo o1, ResolveInfo o2) {
                        return o1.loadLabel(packageManager).toString().toLowerCase()
                                .compareTo(o2.loadLabel(packageManager).toString().toLowerCase());
                    }
                });
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    apps.add(appinfo);

                }
            }

        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage().toString() + " loadApps");
        }

    }

    public void showApps(View v) {
        // Intent i = new Intent(HomeActivity.this, GetApps.class);
        //startActivity(i);
        HideAppDrawer(true);
    }

    public void HideAppDrawer(Boolean visibility) {
        if (visibility) {
            containAppDrawer.setVisibility(View.VISIBLE);
            ContainerHome.setVisibility(View.GONE);
        } else {
            containAppDrawer.setVisibility(View.GONE);
            ContainerHome.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onBackPressed() {
        HideAppDrawer(false);
    }


}
