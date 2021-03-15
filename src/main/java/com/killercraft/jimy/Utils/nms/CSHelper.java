package com.killercraft.jimy.Utils.nms;

import com.killercraft.jimy.Utils.nms.v1_10_R1.ChatMessageSender1_10;
import com.killercraft.jimy.Utils.nms.v1_11_R1.ChatMessageSender1_11;
import com.killercraft.jimy.Utils.nms.v1_12_R1.ChatMessageSender1_12;
import com.killercraft.jimy.Utils.nms.v1_13_R2.ChatMessageSender1_13;
import com.killercraft.jimy.Utils.nms.v1_14_R1.ChatMessageSender1_14;
import com.killercraft.jimy.Utils.nms.v1_15_R1.ChatMessageSender1_15;
import com.killercraft.jimy.Utils.nms.v1_9_R2.ChatMessageSender1_9;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CSHelper {

    private CSHelper() {

    }

    public static void sendChat(Player handler,String title,String shopName,String endText,String hover) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        if(version.equals("v1_9_R2")){
            ChatMessageSender1_9.sendHandleMessage(handler,title,shopName,endText,hover);
        }else if(version.equals("v1_10_R1")){
            ChatMessageSender1_10.sendHandleMessage(handler,title,shopName,endText,hover);
        }else if(version.equals("v1_11_R1")){
            ChatMessageSender1_11.sendHandleMessage(handler,title,shopName,endText,hover);
        }else if(version.equals("v1_12_R1")){
            ChatMessageSender1_12.sendHandleMessage(handler,title,shopName,endText,hover);
        }else if(version.equals("v1_13_R2")){
            ChatMessageSender1_13.sendHandleMessage(handler,title,shopName,endText,hover);
        }else if(version.equals("v1_14_R1")){
            ChatMessageSender1_14.sendHandleMessage(handler,title,shopName,endText,hover);
        }else if(version.equals("v1_15_R1")){
            ChatMessageSender1_15.sendHandleMessage(handler,title,shopName,endText,hover);
        }
    }
}
