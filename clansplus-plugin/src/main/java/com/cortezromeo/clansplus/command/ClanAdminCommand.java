package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.clan.skill.plugin.BoostScoreSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.CriticalHitSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.DodgeSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.LifeStealSkill;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.*;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.language.Vietnamese;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.support.DiscordSupport;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClanAdminCommand implements CommandExecutor, TabExecutor {

    public static List<CommandSender> commandConfirmation = new ArrayList<>();
    public static List<CommandSender> transferDataCommandNotifying = new ArrayList<>();

    public ClanAdminCommand() {
        ClansPlus.plugin.getCommand("clanadmin").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp() || !player.hasPermission("clanplus.admin")) {
                MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
                return false;
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                // TODO: RELOAD
                long time = System.currentTimeMillis();

                ClansPlus.plugin.reloadConfig();
                ClanListInventoryFile.reload();
                NoClanInventoryFile.reload();
                ClanMenuInventoryFile.reload();
                MembersMenuInventoryFile.reload();
                AddMemberListInventoryFile.reload();
                MemberListInventoryFile.reload();
                ManageMemberInventoryFile.reload();
                ManageMemberRankInventoryFile.reload();
                AlliesMenuInventoryFile.reload();
                AddAllyListInventoryFile.reload();
                AllyInvitationInventoryFile.reload();
                AllyInivtationConfirmInventoryFile.reload();
                AllyListInventoryFile.reload();
                ManageAllyInventoryFile.reload();
                ViewClanInventoryFile.reload();
                UpgradePluginSkillListInventoryFile.reload();
                UpgradeMenuInventoryFile.reload();
                SkillsMenuInventoryFile.reload();
                EventsMenuInventoryFile.reload();
                ClanSettingsInventoryFile.reload();
                SetIconCustomHeadListInventoryFile.reload();
                SetIconMaterialListInventoryFile.reload();
                SetIconMenuInventoryFile.reload();
                SetPermissionInventoryFile.reload();
                DisbandConfirmationInventoryFile.reload();
                LeaveConfirmationInventoryFile.reload();
                EventsFile.reload();
                SkillsFile.reload();
                UpgradeFile.reload();

                Settings.setupValue();

                Vietnamese.reload();
                Messages.setupValue(Settings.LANGUAGE);

                CriticalHitSkill.registerSkill();
                DodgeSkill.registerSkill();
                LifeStealSkill.registerSkill();
                BoostScoreSkill.registerSkill();

                EventManager.getWarEvent().setupValue();

                if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null)
                    ClansPlus.discordsrv = new DiscordSupport(ClansPlus.plugin);

                sender.sendMessage("Đã reload plugin (" + (System.currentTimeMillis() - time) + "ms)");
                return false;
            }
            if (args[0].equalsIgnoreCase("backup")) {
                sender.sendMessage("Đang tạo backup, vui lòng đợi...");
                Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
                    PluginDataManager.backupAll(null);
                    sender.sendMessage("Backup thành công! File backup sẽ nằm trong folder backup trong folder plugin.");
                    sender.sendMessage("Database type: " + ClansPlus.databaseType.toString().toUpperCase());
                });
                return false;
            }
            if (args[0].equalsIgnoreCase("chatspy")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!ClanManager.getPlayerUsingChatSpy().contains(player)) {
                        ClanManager.getPlayerUsingChatSpy().add(player);
                        player.sendMessage("Đã bật spy bang hội chat thành công.");
                        return false;
                    } else {
                        ClanManager.getPlayerUsingChatSpy().remove(player);
                        player.sendMessage("Đã tắt bang hội chat thành công.");
                        return false;
                    }
                } else {
                    if (ClanManager.isConsoleUsingChatSpy()) {
                        ClanManager.consoleUsingChatSpy = false;
                        sender.sendMessage("Đã tắt spy bang hội chat thành công.");
                        return false;
                    } else {
                        ClanManager.consoleUsingChatSpy = true;
                        sender.sendMessage("Đã bật spy bang hội chat thành công.");
                        return false;
                    }
                }
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                String clanName = args[1];
                if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                    sender.sendMessage("Clan " + clanName + " không tồn tại.");
                    return false;
                } else {
                    ClanDataType clanDataType;
                    try {
                        clanDataType = ClanDataType.valueOf(args[2].toLowerCase());
                    } catch (Exception exception) {
                        sender.sendMessage("Type " + (args[2] == null ? "" : args[2] + " ") + "không hợp lệ.");
                        return false;
                    }

                    IClanData clanData = PluginDataManager.getClanDatabase(clanName);

                    // Score
                    if (clanDataType == ClanDataType.score) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setScore(0);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("give")) {
                                clanData.setScore(clanData.getScore() + value);
                                sender.sendMessage("Đã cho " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getScore());
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                clanData.setScore(clanData.getScore() - value);
                                sender.sendMessage("Đã trừ " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getScore());
                            } else if (args[3].equalsIgnoreCase("set")) {
                                clanData.setScore(value);
                                sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getScore());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value không hợp lệ.");
                            return false;
                        }
                    }

                    // Warpoint
                    if (clanDataType == ClanDataType.warpoint) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setWarPoint(0);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("give")) {
                                clanData.setWarPoint(clanData.getWarPoint() + value);
                                sender.sendMessage("Đã cho " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getWarPoint());
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                clanData.setWarPoint(clanData.getWarPoint() - value);
                                sender.sendMessage("Đã trừ " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getWarPoint());
                            } else if (args[3].equalsIgnoreCase("set")) {
                                clanData.setWarPoint(value);
                                sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getWarPoint());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value không hợp lệ.");
                            return false;
                        }
                    }

                    // Warning
                    if (clanDataType == ClanDataType.warning) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setWarning(0);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("give")) {
                                clanData.setWarning(clanData.getWarning() + value);
                                sender.sendMessage("Đã cho " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getWarning());
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                clanData.setWarning(clanData.getWarning() - value);
                                sender.sendMessage("Đã trừ " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getWarning());
                            } else if (args[3].equalsIgnoreCase("set")) {
                                clanData.setWarning(value);
                                sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getWarning());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value không hợp lệ.");
                            return false;
                        }
                    }

                    // Icon
                    if (clanDataType == ClanDataType.icon) {
                        IconType iconType;
                        try {
                            iconType = IconType.valueOf(args[4].toUpperCase());
                        } catch (Exception exception) {
                            sender.sendMessage("Icon type không hợp lệ!");
                            return false;
                        }
                        try {
                            String iconValue = args[5];
                            if (iconValue == null) {
                                sender.sendMessage("Vui lòng nhập icon value!");
                                return false;
                            }

                            if (iconType == IconType.MATERIAL) {
                                try {
                                    XMaterial xMaterial = XMaterial.valueOf(iconValue);
                                    Material material = xMaterial.get();
                                    if (material == null || material.equals(Material.AIR)) {
                                        sender.sendMessage("Icon value " + iconValue + " không hợp lệ!");
                                        sender.sendMessage("Danh sách materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                                        return false;
                                    }
                                } catch (Exception exception) {
                                    sender.sendMessage("Icon value " + iconValue + " không hợp lệ!");
                                    return false;
                                }
                            }
                            PluginDataManager.getClanDatabase(clanName).setIconType(iconType);
                            PluginDataManager.getClanDatabase(clanName).setIconValue(iconValue);
                            sender.sendMessage("Đã set icon cho clan " + clanName + " thành:");
                            sender.sendMessage("Icon type: " + iconType);
                            sender.sendMessage("Icon value: " + iconValue);
                        } catch (Exception exception) {
                            sender.sendMessage("Vui lòng nhập icon value hợp lệ!");
                            return false;
                        }
                        return false;
                    }

                    // Created date
                    if (clanDataType == ClanDataType.createddate) {
                        try {
                            long value = Long.parseLong(args[4]);
                            if (args[3].equalsIgnoreCase("set")) {
                                clanData.setCreatedDate(value);
                                sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getCreatedDate());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value không hợp lệ.");
                            return false;
                        }
                    }

                    // Custom name
                    if (clanDataType == ClanDataType.customname) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setCustomName(null);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        String value = args[4];
                        if (args[3].equalsIgnoreCase("set")) {
                            clanData.setCustomName(value);
                            sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                            sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getCustomName());
                        }
                    }

                    // Message
                    if (clanDataType == ClanDataType.message) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setMessage(null);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        String value = args[4];
                        if (args[3].equalsIgnoreCase("set")) {
                            clanData.setMessage(value);
                            sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                            sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getMessage());
                        }
                    }

                    // Subject permission
                    if (clanDataType == ClanDataType.subjectpermission) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            HashMap<Subject, Rank> newPermissionDefault = new HashMap<>();
                            for (Subject subject : Subject.values())
                                newPermissionDefault.put(subject, Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(subject));
                            clanData.setSubjectPermission(newPermissionDefault);
                            sender.sendMessage("Đã reset quyền của " + clanName + " thành mặc định giống config.yml");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("set")) {

                            Subject subject;
                            try {
                                subject = Subject.valueOf(args[4].toUpperCase());
                            } catch (Exception exception) {
                                sender.sendMessage("Value không hợp lệ.");
                                return false;
                            }

                            Rank newRank;
                            try {
                                newRank = Rank.valueOf(args[5].toUpperCase());
                            } catch (Exception exception) {
                                sender.sendMessage("Rank không hợp lệ.");
                                return false;
                            }

                            clanData.getSubjectPermission().put(subject, newRank);

                            sender.sendMessage("Đã set subject " + subject + " của " + clanName + " thành " + newRank);
                        }
                    }

                    // Discord channel ID
                    if (clanDataType == ClanDataType.discordchannelid) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setDiscordChannelID(0);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("set")) {
                                clanData.setDiscordChannelID(value);
                                sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getDiscordChannelID());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value không hợp lệ.");
                            return false;
                        }
                    }

                    // Discord join link
                    if (clanDataType == ClanDataType.discordjoinlink) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setDiscordJoinLink(null);
                            sender.sendMessage("Đã reset " + clanName + " " + clanDataType);
                            return false;
                        }

                        String value = args[4];
                        if (args[3].equalsIgnoreCase("set")) {
                            clanData.setDiscordJoinLink(value);
                            sender.sendMessage("Đã set " + clanName + " " + value + " " + clanDataType);
                            sender.sendMessage(clanDataType + " mới của " + clanName + ": " + clanData.getMessage());
                        }
                    }

                    // Members
                    if (clanDataType == ClanDataType.members) {
                        if (args[3].equalsIgnoreCase("add")) {
                            String playerName;
                            try {
                                playerName = args[4];
                                if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
                                    sender.sendMessage(playerName + " không tồn tại trong dữ liệu!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Vui lòng nhập tên người chơi!");
                                return false;
                            }
                            if (clanData.getMembers().contains(playerName)) {
                                sender.sendMessage(playerName + " đã là thành viên trong bang hội này rồi!");
                                return false;
                            }
                            if (ClanManager.isPlayerInClan(playerName)) {
                                sender.sendMessage(playerName + " đang có ở trong một bang hội khác!");
                                sender.sendMessage("Lưu ý: Bạn có thể xóa " + playerName + " này ra khỏi clan hiện tại của người này (" + PluginDataManager.getPlayerDatabase(playerName).getClan() + ") bằng cách xài lệnh setPlayerData trong clanAdmin.");
                                return false;
                            }
                            ClanManager.addPlayerToAClan(playerName, clanName, true);
                            sender.sendMessage("Thêm " + playerName + " vào " + clanName + " thành công!");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("remove")) {
                            String playerName;
                            try {
                                playerName = args[4];
                                if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
                                    sender.sendMessage(playerName + " không tồn tại trong dữ liệu!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Vui lòng nhập tên người chơi!");
                                return false;
                            }
                            if (!clanData.getMembers().contains(playerName)) {
                                sender.sendMessage(playerName + " không phải là thành viên trong bang hội này!");
                                return false;
                            }
                            IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
                            if (playerData.getRank() == Rank.LEADER) {
                                sender.sendMessage(playerName + " không thể bị xóa khỏi bang hội vì người này đang là chủ bang hội!");
                                return false;
                            }
                            PluginDataManager.getClanDatabase(playerData.getClan()).getMembers().remove(playerName);
                            PluginDataManager.clearPlayerDatabase(playerName);
                            sender.sendMessage("Đã xóa " + playerName + " khỏi bang hội " + clanName);
                            return false;
                        }
                    }

                    // Allies
                    if (clanDataType == ClanDataType.allies) {
                        if (args[3].equalsIgnoreCase("add")) {
                            String allyName;
                            try {
                                allyName = args[4];
                                if (!PluginDataManager.getClanDatabase().containsKey(allyName)) {
                                    sender.sendMessage("Clan " + allyName + " không tồn tại trong dữ liệu!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Vui lòng nhập tên clan!");
                                return false;
                            }
                            if (clanData.getAllies().contains(allyName)) {
                                sender.sendMessage(allyName + " đã là đồng minh trong bang hội này rồi!");
                                return false;
                            }
                            PluginDataManager.getClanDatabase(clanName).getAllyInvitation().remove(allyName);
                            PluginDataManager.getClanDatabase(allyName).getAllyInvitation().remove(clanName);
                            PluginDataManager.getClanDatabase(clanName).getAllies().add(allyName);
                            PluginDataManager.getClanDatabase(allyName).getAllies().add(clanName);
                            sender.sendMessage("Thêm " + allyName + " làm đồng minh của " + clanName + " thành công!");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("remove")) {
                            String allyName;
                            try {
                                allyName = args[4];
                                if (!PluginDataManager.getClanDatabase().containsKey(allyName)) {
                                    sender.sendMessage("Clan" + allyName + " không tồn tại trong dữ liệu!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Vui lòng nhập tên clan!");
                                return false;
                            }
                            if (!clanData.getAllies().contains(allyName)) {
                                sender.sendMessage(allyName + " không phải là đồng minh trong bang hội này!");
                                return false;
                            }
                            PluginDataManager.getClanDatabase(clanName).getAllies().remove(allyName);
                            PluginDataManager.getClanDatabase(allyName).getAllies().remove(clanName);
                            sender.sendMessage("Đã xóa clan " + allyName + " khỏi danh sách đồng minh của bang hội " + clanName + ".");
                            return false;
                        }
                    }

                    // save clan data eventually
                    PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("setclanskilldata")) {
                String clanName = args[1];
                if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                    sender.sendMessage("Clan " + clanName + " không tồn tại.");
                    return false;
                } else {
                    int skillID;
                    int skilLevel;

                    try {
                        skillID = Integer.parseInt(args[2]);
                        skilLevel = Integer.parseInt(args[3]);

                        PluginDataManager.getClanDatabase(clanName).getSkillLevel().put(skillID, skilLevel);
                        sender.sendMessage("Đã set skill ID " + skillID + " của bang hôi " + clanName + " lên " + skilLevel + ".");
                        PluginDataManager.saveClanDatabaseToStorage(clanName);
                    } catch (Exception exception) {
                        sender.sendMessage("Skill ID hoặc Skill Level phải là số!");
                        return false;
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("event")) {
                try {
                    if (args[1].equalsIgnoreCase("war")) {
                        if (args[2].equalsIgnoreCase("start")) {
                            sender.sendMessage("Đang bắt đầu sự kiện war...");
                            sender.sendMessage("Lưu ý: Sự kiện sẽ không bắt đầu nếu nó đang diễn ra.");
                            EventManager.getWarEvent().runEvent(false);
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("end")) {
                            sender.sendMessage("Đang kết thúc sự kiện war...");
                            sender.sendMessage("Lưu ý: Sự kiện sẽ không kết thúc nếu nó đang không diễn ra.");
                            EventManager.getWarEvent().endEvent(true, true, true);
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("settime")) {
                            try {
                                int newTimeLeft = Integer.parseInt(args[3]);
                                sender.sendMessage("Đã chỉnh thời gian còn lại của sự kiện war thành " + newTimeLeft);
                                sender.sendMessage("Lưu ý: Thời gian sẽ không thay đổi nếu sự kiện đang không diễn ra.");

                                if (newTimeLeft < 0)
                                    newTimeLeft = 1;

                                EventManager.getWarEvent().setTimeLeft(newTimeLeft);
                                return false;
                            } catch (Exception exception) {
                                sender.sendMessage("Vui lòng nhập số giây hợp lệ!");
                                return false;
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {
                    sender.sendMessage("Vui lòng nhập giá trị hợp lệ!");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("delete")) {
                String clanName = args[1];
                if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                    sender.sendMessage("Clan " + clanName + " không tồn tại.");
                    return false;
                } else {
                    if (PluginDataManager.deleteClanData(clanName)) {
                        sender.sendMessage("Đã xóa clan " + clanName + " thành công.");
                    } else {
                        sender.sendMessage("Gặp lỗi trong quá trình xóa clan (Vui lòng check console)");
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("transferplugindatabasetype")) {
                try {
                    DatabaseType databaseType = DatabaseType.valueOf(args[1].toUpperCase());
                    if (databaseType != ClansPlus.databaseType) {
                        PluginDataManager.transferDatabase(sender, databaseType);
                    } else {
                        sender.sendMessage("Không thể chuyển sang database type đang dùng hiện tại!");
                    }
                    return false;
                } catch (Exception exception) {
                    sender.sendMessage("Database type " + args[1] + " không hợp lệ!");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("backup")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    builder.append(args[i]).append(" ");
                builder.deleteCharAt(builder.length() - 1);

                String fileName = builder.toString();

                List<String> prohibitedCharacters = new ArrayList<>();
                // these characters cannot be used for file name due to windows's limited
                prohibitedCharacters.add("\\");
                prohibitedCharacters.add("/");
                prohibitedCharacters.add(":");
                prohibitedCharacters.add("*");
                prohibitedCharacters.add("?");
                prohibitedCharacters.add("<");
                prohibitedCharacters.add(">");
                prohibitedCharacters.add("|");
                for (String character : prohibitedCharacters) {
                    if (fileName.contains(character)) {
                        sender.sendMessage(fileName + " không được có ký tự: " + character);
                        return false;
                    }
                }

                sender.sendMessage("Đang tạo backup, vui lòng đợi...");
                Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
                    PluginDataManager.backupAll(fileName);
                    sender.sendMessage("Backup thành công! File backup sẽ nằm trong folder backup trong folder plugin.");
                    sender.sendMessage("Database type: " + ClansPlus.databaseType.toString().toUpperCase());
                    sender.sendMessage("File name: " + fileName);
                });
                return false;
            }
            if (args[0].equalsIgnoreCase("setPlayerData")) {
                String playerName;
                try {
                    playerName = args[1];
                    if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
                        sender.sendMessage("Người chơi " + playerName + " không tồn tại trong dữ liệu!");
                        return false;
                    }
                    PlayerDataType playerDataType;
                    try {
                        playerDataType = PlayerDataType.valueOf(args[2].toLowerCase());
                    } catch (Exception exception) {
                        sender.sendMessage("Type " + (args[2] == null ? "" : args[2] + " ") + "không hợp lệ.");
                        return false;
                    }

                    if (playerDataType.equals(PlayerDataType.clanname)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            String clanName;
                            try {
                                clanName = args[4];
                            } catch (Exception exception) {
                                sender.sendMessage("Vui lòng nhập tên clan hợp lệ.");
                                return false;
                            }
                            if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                                sender.sendMessage("Clan " + clanName + " không tồn tại.");
                                return false;
                            }
                            if (ClanManager.isPlayerInClan(playerName)) {
                                if (PluginDataManager.getPlayerDatabase(playerName).getRank() == Rank.LEADER) {
                                    sender.sendMessage(playerName + " đang là chủ bang hội của " + PluginDataManager.getPlayerDatabase(playerName).getClan() + ". Vui lòng xài lệnh reset thay vì set!");
                                    return false;
                                }
                            }
                            ClanManager.addPlayerToAClan(playerName, clanName, true);
                            sender.sendMessage("Đã cho " + playerName + " vào " + clanName + ".");
                            sender.sendMessage("Dữ liệu của " + clanName + " cũng đồng thời được thêm người chơi " + playerName + " vào danh sách thành viên.");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("reset")) {
                            if (!ClanManager.isPlayerInClan(playerName)) {
                                sender.sendMessage(playerName + " không có clan!");
                                return false;
                            }
                            String playerClanName = PluginDataManager.getPlayerDatabase(playerName).getClan();
                            PluginDataManager.clearPlayerDatabase(playerName);
                            sender.sendMessage("Đã loại bỏ " + playerName + " khỏi danh sách thành viên của clan " + playerClanName);
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.rank)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Rank chosenRank;
                            try {
                                chosenRank = Rank.valueOf(args[4].toUpperCase());
                            } catch (Exception exception) {
                                sender.sendMessage("Rank không hợp lệ!");
                                sender.sendMessage("Các rank hiện có:");
                                for (Rank rank : Rank.values())
                                    sender.sendMessage(rank.toString().toUpperCase());
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setRank(chosenRank);
                            sender.sendMessage("Đã set rank của " + playerName + " thành " + chosenRank + ".");
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.joindate)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Long newJoinDate;
                            try {
                                newJoinDate = Long.parseLong(args[4]);
                            } catch (Exception exception) {
                                sender.sendMessage("Số ngày không hợp lệ! Vui lòng nhập milliseconds");
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setJoinDate(newJoinDate);
                            sender.sendMessage("Đã set ngày tham gia của " + playerName + " thành " + newJoinDate + " (" + com.cortezromeo.clansplus.util.StringUtil.dateTimeToDateFormat(newJoinDate));
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.scorecollected)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Long newScoreCollected;
                            try {
                                newScoreCollected = Long.parseLong(args[4]);
                            } catch (Exception exception) {
                                sender.sendMessage("Số ngày không hợp lệ!");
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setScoreCollected(newScoreCollected);
                            sender.sendMessage("Đã set số điểm kiếm được của " + playerName + " thành " + newScoreCollected);
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("reset")) {
                            PluginDataManager.getPlayerDatabase(playerName).setScoreCollected(0);
                            sender.sendMessage("Đã reset số điểm kiểm được của " + playerName + ".");
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.lastactivated)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Long newLastActivated;
                            try {
                                newLastActivated = Long.parseLong(args[4]);
                            } catch (Exception exception) {
                                sender.sendMessage("Số ngày không hợp lệ! Vui lòng nhập milliseconds");
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setLastActivated(newLastActivated);
                            sender.sendMessage("Đã set thời gian hoạt động cuối cùng của " + playerName + " thành " + newLastActivated + " (" + com.cortezromeo.clansplus.util.StringUtil.dateTimeToDateFormat(newLastActivated));
                            return false;
                        }
                    }
                } catch (Exception exception) {
                    sender.sendMessage("Vui lòng nhập tên người chơi!");
                    return false;
                }
                return false;
            }
        }

        sender.sendMessage("ClanPlus (Version: " + ClansPlus.plugin.getDescription().getVersion() + ") - Admin");
        sender.sendMessage("Plugin được làm và phát triển bởi Cortez_Romeo");
        sender.sendMessage("");
        sender.sendMessage("/clanadmin reload");
        sender.sendMessage("/clanadmin chatspy");
        sender.sendMessage("/clanadmin transferPluginDatabaseType <type>");
        sender.sendMessage("/clanadmin backup (custom file name)");
        sender.sendMessage("/clanadmin setClanData <clan name> <type> (give/add/set/remove/reset) (value)");
        sender.sendMessage(ChatColor.AQUA + "Types: score, warpoint, warning, createddate, customname, message, icon, spawnpoint, subjectpermission, discordchannelid, discordjoinlink, members, allies");
        sender.sendMessage("/clanadmin setClanSkillData <clan name> <skill id> <skill level>");
        sender.sendMessage("/clanadmin setPlayerData <player name> (type) (set/reset) (value)");
        sender.sendMessage(ChatColor.AQUA + "Types: clanname, rank, joindate, scorecollected, lastactivated");
        sender.sendMessage("/clanadmin event <event> (start/end/settime) (value)");
        sender.sendMessage("/clanadmin delete <clan name>");
        sender.sendMessage("");
        sender.sendMessage("<>: Cần thiết");
        sender.sendMessage("(): Tùy chọn");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp() || !player.hasPermission("clanplus.admin")) {
                MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
                return null;
            }
        }

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            // general sub command
            commands.add("reload");
            commands.add("transferPluginDatabaseType");
            commands.add("setClanData");
            commands.add("setClanSkillData");
            commands.add("setPlayerData");
            commands.add("event");
            commands.add("backup");
            commands.add("delete");
            commands.add("chatspy");

            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            // check clan info -> list all clan name
            if (args[0].equalsIgnoreCase("setclandata") || args[0].equalsIgnoreCase("setClanSkillData") || args[0].equalsIgnoreCase("delete")) {
                if (!PluginDataManager.getClanDatabase().isEmpty()) {
                    commands.addAll(PluginDataManager.getClanDatabase().keySet());
                }
             }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (!PluginDataManager.getPlayerDatabase().isEmpty())
                    commands.addAll(PluginDataManager.getPlayerDatabase().keySet());
            }
            if (args[0].equalsIgnoreCase("event"))
                commands.add("war");
            if (args[0].equalsIgnoreCase("transferPluginDatabaseType")) {
                if (!transferDataCommandNotifying.contains(sender)) {
                    sender.sendMessage("-----------------------");
                    sender.sendMessage("Lưu ý: Clan database type hiện tại là: " + ClansPlus.databaseType.toString().toUpperCase());
                    sender.sendMessage("Những database type có thể sử dụng:");
                    for (DatabaseType databaseType : DatabaseType.values()) {
                        if (databaseType != ClansPlus.databaseType) {
                            sender.sendMessage("- " + databaseType.toString().toUpperCase());
                        }
                    }
                    sender.sendMessage("Để an toàn dữ liệu trong việc chuyển đổi dữ liệu, vui lòng làm trong lúc máy chủ không có người chơi.");
                    sender.sendMessage("-----------------------");
                    transferDataCommandNotifying.add(sender);
                    Bukkit.getScheduler().runTaskLater(ClansPlus.plugin, () -> {
                        transferDataCommandNotifying.remove(sender);
                    }, 20 * 30);
                }
                for (DatabaseType databaseType : DatabaseType.values()) {
                    commands.add(databaseType.toString().toUpperCase());
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    for (ClanDataType clanDataType : ClanDataType.values()) {
                        commands.add(clanDataType.toString().toLowerCase());
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (PluginDataManager.getPlayerDatabase().containsKey(args[1])) {
                    for (PlayerDataType playerDataType : PlayerDataType.values()) {
                        commands.add(playerDataType.toString().toLowerCase());
                    }
                }
            }
            if (args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("war")) {
                commands.add("start");
                commands.add("end");
                commands.add("settime");
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    String type = args[2];
                    commands.add("set");
                    if (!type.equalsIgnoreCase("members") && !type.equalsIgnoreCase("allies")) {
                        commands.add("reset");
                        if (type.equalsIgnoreCase("createddate"))
                            commands.remove("reset");
                    } else {
                        commands.add("add");
                        commands.add("remove");
                        commands.remove("set");
                    }
                    if (type.equalsIgnoreCase("score")
                            || type.equalsIgnoreCase("warpoint")
                            || type.equalsIgnoreCase("warning")) {
                        commands.add("give");
                        commands.add("remove");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (PluginDataManager.getPlayerDatabase().containsKey(args[1])) {
                    String type = args[2];
                    commands.add("set");
                    if (type.equalsIgnoreCase("scorecollected") || type.equalsIgnoreCase("clanname"))
                        commands.add("reset");
                }
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    if (args[2].equalsIgnoreCase("subjectpermission")) {
                        for (Subject subject : Subject.values()) {
                            commands.add(subject.toString().toUpperCase());
                        }
                    }
                    if (args[2].equalsIgnoreCase("icon")) {
                        for (IconType iconType : IconType.values()) {
                            commands.add(iconType.toString().toUpperCase());
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (PluginDataManager.getPlayerDatabase().containsKey(args[1])) {
                    if (args[2].equalsIgnoreCase("rank") && args[3].equalsIgnoreCase("set")) {
                        for (Rank rank : Rank.values()) {
                            commands.add(rank.toString().toUpperCase());
                        }
                    }
                    if (args[2].equalsIgnoreCase("clanname") && args[3].equalsIgnoreCase("set")) {
                        if (!PluginDataManager.getClanDatabase().isEmpty()) {
                            commands.addAll(PluginDataManager.getClanDatabase().keySet());
                        }
                    }
                }
            }
            StringUtil.copyPartialMatches(args[4], commands, completions);
        } else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    if (args[2].equalsIgnoreCase("subjectpermission")) {
                        for (Rank rank : Rank.values()) {
                            commands.add(rank.toString().toUpperCase());
                        }
                    }
                    if (args[2].equalsIgnoreCase("icon")) {
                        try {
                            IconType iconType = IconType.valueOf(args[4].toUpperCase());
                            if (iconType.equals(IconType.MATERIAL)) {
                                for (Material material : Material.values()) {
                                    if (material == Material.AIR)
                                        continue;
                                    commands.add(material.toString().toUpperCase());
                                }
                            }
                        } catch (Exception exception) {
                            //
                        }
                    }
                }
            }
            StringUtil.copyPartialMatches(args[5], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    private enum ClanDataType {
        score, warpoint, warning, createddate, customname, message, icon, spawnpoint, subjectpermission, discordchannelid, discordjoinlink, members, allies
    }
    private enum PlayerDataType {
        clanname, rank, joindate, scorecollected, lastactivated

    }

}
