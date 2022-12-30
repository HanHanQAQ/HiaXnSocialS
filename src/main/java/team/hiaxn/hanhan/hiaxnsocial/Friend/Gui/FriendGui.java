package team.hiaxn.hanhan.hiaxnsocial.Friend.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;
import team.hiaxn.hanhan.hiaxnsocial.PlayerProfile.PlayerProfile;
import team.hiaxn.hanhan.hiaxnsocial.Utils.HeadUtil;
import team.hiaxn.hanhan.hiaxnsocial.Utils.InventoryUtil;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class FriendGui implements Listener {
    private int pages = 1;//总共的page数量
    private int page = 1;//当前page页面
    private ArrayList<String> nextPage = new ArrayList<>(); //如过存在下一页 将第一页存储的之后的存在这个List里面
    private Player ownPlayer;
    private InventoryUtil friendGui;
    public FriendGui() {}
    public FriendGui(Player targetPlayer) {
        this.ownPlayer = targetPlayer;
        this.friendGui = new InventoryUtil("§0好友",6 * 9);
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 2);
        ItemMeta glassItemMeta = glass.getItemMeta();
        glassItemMeta.setDisplayName(" ");
        glass.setItemMeta(glassItemMeta);
        for (int i = 9; i < 17; i++) {
            friendGui.setItem(i,glass);
        }
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
        SkullMeta m = (SkullMeta)playerHead.getItemMeta();
        m.setDisplayName("§a好友");
        m.setLore(Arrays.asList("§7浏览你" + HiaXnSocial.serverName + "好友的资料，并与你的在线好友进行互动！"));
        m.setOwner(ownPlayer.getName());
        playerHead.setItemMeta(m);
        friendGui.setItem(2,playerHead);

        ItemStack friend = HeadUtil.getSkull("ewogICJ0aW1lc3RhbXAiIDogMTU5OTI4OTk4MzAzNiwKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83M2Y5YTQyODEyYzNlZThlYmFjOGYzOThhNzA0Mzk1MmE3ZmE2ZDZhYzRhOTQxYjg4NzQ0MDFjMDc0MzM3ODg0IgogICAgfQogIH0KfQ");
        ItemMeta fmeta = friend.getItemMeta();
        fmeta.setDisplayName("§a好友");
        fmeta.setLore(Arrays.asList("§7浏览你" + HiaXnSocial.serverName + "好友的资料，并与你的在线好友进行互动！"));
        friend.setItemMeta(fmeta);
        friendGui.setItem(3,friend);

        ItemStack party = HeadUtil.getSkull("ewogICJ0aW1lc3RhbXAiIDogMTU5OTI4OTA2MzgwMywKICAicHJvZmlsZUlkIiA6ICI3MjI2Mjg2NzYyZWY0YjZlODRlMzc2Y2JkYWNhZjU1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJicmFuZG9uZDI2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFmNmNlYzQ1NmMzYjdjYmI5OTViMjIzNTY1Mzc3M2M0MjUzMzA4MGFhZDg1ZjZmMmYyY2E0MTE4NWNkYjIyYzEiCiAgICB9CiAgfQp9");
        ItemMeta pmeta = party.getItemMeta();
        pmeta.setDisplayName("§a组队");
        pmeta.setLore(Arrays.asList("§7创建队伍和其他玩家一起游戏！"));
        party.setItemMeta(pmeta);
        friendGui.setItem(4,party);

        ItemStack guild = HeadUtil.getSkull("ewogICJ0aW1lc3RhbXAiIDogMTU5OTI5MDAyMzQwMCwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83NWUwNWZjYzQ0ZGMyODMzZGRiZGJiMjRlZDIwNWMzMGQ4N2JiZTc2MGFiNzJjNjY3MmJmZjJjZmJiOTk3NTE0IgogICAgfQogIH0KfQ");
        ItemMeta gmeta = guild.getItemMeta();
        gmeta.setDisplayName("§a公会");
        gmeta.setLore(Arrays.asList("§7与其他" + HiaXnSocial.serverName + "玩家一起创","§7立公会，征服各类游戏模","§7式，为Geek奖励一起努力。"));
        guild.setItemMeta(gmeta);
        friendGui.setItem(5,guild);

        ItemStack recent = HeadUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM5Y2FjZGQ4NjkwM2U3ZDQ3ZDFiZTY4Y2ZkZDFiNmY5ZDU1YmUwM2FmODJjODgwZmVjMTc0NzI4OTdjNzAzNiJ9fX0");
        ItemMeta rmeta = recent.getItemMeta();
        rmeta.setDisplayName("§a近期活跃玩家");
        rmeta.setLore(Arrays.asList("§7查看最近和你一起游戏的玩家。"));
        recent.setItemMeta(rmeta);
        friendGui.setItem(6,recent);
    }
    public void open() throws SQLException{
                  //第一次打开的时候创建一次
                try {
                    PlayerProfile ownProfileFrist = new PlayerProfile(ownPlayer.getUniqueId());
                Bukkit.broadcastMessage("§e[DEBUG] 成功实例化ownProfileFrist");
                try {
                    if (ownProfileFrist.getResultSet().getString("friends").equals("friends:null")) { //无好友
                        Bukkit.broadcastMessage("§e[DEBUG] 获取到玩家的Friends为: " + ownProfileFrist.getResultSet().getString("friends"));
                        ItemStack noFriends = new ItemStack(Material.GLASS_BOTTLE);
                        ItemMeta nfMeta = noFriends.getItemMeta();
                        nfMeta.setDisplayName("§c你还木有好友╮(￣▽￣'')╭");
                        nfMeta.setLore(Arrays.asList("§7你可以点击上方的“添加好","§7友”或§b/friend add","§b玩家名","§7来添加好友！"));
                        noFriends.setItemMeta(nfMeta);
                        friendGui.setItem(31,noFriends);
                        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 2);
                        ItemMeta glassItemMeta = glass.getItemMeta();
                        glassItemMeta.setDisplayName(" ");
                        glass.setItemMeta(glassItemMeta);
                        friendGui.setItem(17,glass);
                    } else { //有好友

                        String[] friendNames = ownProfileFrist.getPlayerFriendsArray();
                        //页数设置
                        while (true) {
                            if (friendNames.length > pages * 27 && friendNames.length < (pages+1) * 27) {
                                break;
                            } else {
                                pages++;
                            }
                        }
                        ArrayList<String> onlinePlayers = new ArrayList<>(); //存储在线玩家列表
                        ArrayList<String> offlinePlayers = new ArrayList<>(); //用于在列表计算时将在线玩家排列到最前 之后筛选出来的

                        for (String name : friendNames) { //分出在线与离线玩家
                            String onlineselect = "SELECT * FROM player_data WHERE name = ?"; //SQL 查询
                            PreparedStatement onlinePlayersPs = connection.prepareStatement(onlineselect);
                            onlinePlayersPs.setString(1,name);
                            ResultSet onlinePRs = onlinePlayersPs.executeQuery(); //查询结果集
                            onlinePRs.next();
                            if (onlinePRs.getString("status").equals("online")) { //设置在线玩家设置List
                                onlinePlayers.add(name);
                            } else {
                                offlinePlayers.add(name);
                            }
                        }

                        int slot = 27; //限定栏位于27槽开始
                        for (String name : onlinePlayers) { //构建页面
                            if (slot == 53) { //如果槽位是53 就加入到下一页的List内
                                nextPage.add(name);
                            } else {
                                ItemStack friendHead = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                                SkullMeta m = (SkullMeta)friendHead.getItemMeta();
                                m.setOwner(name);
                                friendHead.setItemMeta(m);
                                friendGui.setItem(slot,friendHead);
                                slot++;
                            }
                        }
                        for (String name : offlinePlayers) { //构建页面
                            if (slot == 53) { //如果槽位是53 就加入到下一页的List内
                                nextPage.add(name);
                            } else {
                                ItemStack friendHead = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                                SkullMeta m = (SkullMeta)friendHead.getItemMeta();
                                m.setOwner(name);
                                friendHead.setItemMeta(m);
                                friendGui.setItem(slot,friendHead);
                                slot++;
                            }
                        }

                        //翻页器按钮设置
                        if (pages > 1) {
                            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 2);
                            ItemMeta glassItemMeta = glass.getItemMeta();
                            glassItemMeta.setDisplayName(" ");
                            glass.setItemMeta(glassItemMeta);
                            friendGui.setItem(17,glass);
                        } else {
                            ItemStack arrow = new ItemStack(Material.ARROW);
                            ItemMeta arrowItemMeta = arrow.getItemMeta();
                            arrowItemMeta.setDisplayName("§a下一页");
                            arrowItemMeta.setLore(Arrays.asList("§e左键点击§7以前往下一页","§e右键点击§7以前往最后一页","§7第" + page + "页，共" + pages + "页"));
                            arrow.setItemMeta(arrowItemMeta);
                            friendGui.setItem(17,arrow);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        friendGui.open(ownPlayer);
    }
    @EventHandler
    public void onClickNext(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§0好友")) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§a下一页")) {
                page++;
                int slot = 27; //限定栏位于27槽开始
                for (String name : nextPage) { //构建页面
                    if (slot == 53) { //如果槽位是53 就加入到下一页的List内
                        nextPage.add(name);
                    } else {
                        ItemStack friendHead = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                        SkullMeta m = (SkullMeta)friendHead.getItemMeta();
                        m.setOwner(name);
                        friendHead.setItemMeta(m);
                        friendGui.setItem(slot,friendHead);
                        slot++;
                    }
                }
                if (pages > 1) {
                    ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 2);
                    ItemMeta glassItemMeta = glass.getItemMeta();
                    glassItemMeta.setDisplayName(" ");
                    glass.setItemMeta(glassItemMeta);
                    friendGui.setItem(17,glass);
                } else {
                    ItemStack arrow = new ItemStack(Material.ARROW);
                    ItemMeta arrowItemMeta = arrow.getItemMeta();
                    arrowItemMeta.setDisplayName("§a下一页");
                    arrowItemMeta.setLore(Arrays.asList("§e左键点击§7以前往下一页","§e右键点击§7以前往最后一页","§7第" + page + "页，共" + pages + "页"));
                    arrow.setItemMeta(arrowItemMeta);
                    friendGui.setItem(17,arrow);
                }
                friendGui.open(ownPlayer);
            }
        }
    }
}
