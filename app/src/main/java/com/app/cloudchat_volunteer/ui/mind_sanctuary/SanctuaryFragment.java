package com.app.cloudchat_volunteer.ui.mind_sanctuary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.app.cloudchat_volunteer.R;

public class SanctuaryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sanctuary, container, false);
        LinearLayout ivTree = view.findViewById(R.id.tree_section);
        // 设置点击事件

        ivTree.setOnClickListener(v -> {
            // 添加动画效果
            animateView(v, () -> {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_sanctuary_to_tree);
            });
        });

        return view;
    }
    private void animateView(View view, Runnable endAction) {
        view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    endAction.run();
                })
                .start();
    }
}

