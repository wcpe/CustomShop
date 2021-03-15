package com.killercraft.jimy.Utils.nms.v1_9_R2;

import net.minecraft.server.v1_9_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.server.v1_9_R2.ChatClickable.EnumClickAction.RUN_COMMAND;
import static net.minecraft.server.v1_9_R2.ChatHoverable.EnumHoverAction.SHOW_TEXT;


public class ChatMessageSender1_9 {

    private static final Pattern FORMAT = Pattern.compile("((?<![&|\\u00A7])[&|\\u00A7][0-9a-fk-or])+");


    private static ChatModifier parseStyle(String text) {
        ChatModifier style = new ChatModifier();
        int length = text.length();
        for (int i = 1; i < length; i += 2) {
            switch (text.charAt(i)) {
                case '0':
                    style.setColor(EnumChatFormat.BLACK);
                    break;
                case '1':
                    style.setColor(EnumChatFormat.DARK_BLUE);
                    break;
                case '2':
                    style.setColor(EnumChatFormat.DARK_GREEN);
                    break;
                case '3':
                    style.setColor(EnumChatFormat.DARK_AQUA);
                    break;
                case '4':
                    style.setColor(EnumChatFormat.DARK_RED);
                    break;
                case '5':
                    style.setColor(EnumChatFormat.DARK_PURPLE);
                    break;
                case '6':
                    style.setColor(EnumChatFormat.GOLD);
                    break;
                case '7':
                    style.setColor(EnumChatFormat.GRAY);
                    break;
                case '8':
                    style.setColor(EnumChatFormat.DARK_GRAY);
                    break;
                case '9':
                    style.setColor(EnumChatFormat.BLUE);
                    break;
                case 'a':
                    style.setColor(EnumChatFormat.GREEN);
                    break;
                case 'b':
                    style.setColor(EnumChatFormat.AQUA);
                    break;
                case 'c':
                    style.setColor(EnumChatFormat.RED);
                    break;
                case 'd':
                    style.setColor(EnumChatFormat.LIGHT_PURPLE);
                    break;
                case 'e':
                    style.setColor(EnumChatFormat.YELLOW);
                    break;
                case 'f':
                    style.setColor(EnumChatFormat.WHITE);
                    break;
                case 'k':
                    style.setRandom(true);
                    break;
                case 'l':
                    style.setBold(true);
                    break;
                case 'm':
                    style.setStrikethrough(true);
                    break;
                case 'n':
                    style.setUnderline(true);
                    break;
                case 'o':
                    style.setItalic(true);
                    break;
                default:
                    style = new ChatModifier();
            }
        }
        return style;
    }

    public static void sendMessage(Player player, IChatBaseComponent... siblings) {
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        IChatBaseComponent text = new ChatComponentText(ChatColor.RED+"");
        for (IChatBaseComponent component : siblings) text.addSibling(component);
        handle.sendMessage(text);
    }

    public static void sendHandleMessage(Player handler,String title,String shopName,String endText,String hover) {
        String a = ChatColor.COLOR_CHAR+"";
        sendMessage(handler,
                format(title),
                format(shopName, RUN_COMMAND, "/cshop edit "+shopName.replace(a,"&"), SHOW_TEXT, hover),
                format(endText)
        );
    }


    public static IChatBaseComponent format(String text) {
        return format(text, null, null, null, null);
    }

    public static IChatBaseComponent format(String text, ChatClickable.EnumClickAction ca, String cv, ChatHoverable.EnumHoverAction ha, String hv) {
        Matcher matcher = FORMAT.matcher(text);
        IChatBaseComponent component = new ChatComponentText("");
        int head = 0;
        ChatModifier style = new ChatModifier();
        while (matcher.find()) {
            component.addSibling(new ChatComponentText(text.substring(head, matcher.start()).replaceAll("&&", "&")).setChatModifier(style));
            style = parseStyle(matcher.group());
            head = matcher.end();
        }
        component.addSibling(new ChatComponentText(text.substring(head).replaceAll("&&", "&")).setChatModifier(style));
        if (ca != null && cv != null) {
            component.getChatModifier().setChatClickable(new ChatClickable(ca, cv));
        }
        if (ha != null && hv != null) {
            component.getChatModifier().setChatHoverable(new ChatHoverable(ha, format(hv)));
        }
        return component;
    }

}
