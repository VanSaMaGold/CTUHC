package org.vansama.ctuhc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameStartListener implements Listener {

    public GameStartListener(CTUHC ctuhc) {
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        // 检查事件是否被取消
        if (!event.isCancelled()) {
            // 游戏开始的逻辑
            // 通知其他系统游戏即将开始
            ItemEventListener.onGameStart();// 假设这是处理游戏开始后逻辑的静态方法
        }
    }
}