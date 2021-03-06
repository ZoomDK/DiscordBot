package com.zoomdk.discordbot;

import com.tjplaysnow.discord.object.Bot;
import com.tjplaysnow.discord.object.CommandSpigotManager;
import com.tjplaysnow.discord.object.ProgramCommand;
import com.tjplaysnow.discord.object.ThreadSpigot;
import com.zoomdk.discordbot.Bot.Commands.linkupdateCommand;
import com.zoomdk.discordbot.Bot.Commands.unlinkCommand;
import com.zoomdk.discordbot.Bot.Events;
import com.zoomdk.discordbot.Bot.data.config;
import com.zoomdk.discordbot.Bot.data.data;
import com.zoomdk.discordbot.Bot.Commands.linkCommand;
import com.zoomdk.discordbot.Bot.mcEvents.Join;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public final class Main extends JavaPlugin {
    public static Bot bot;
    private static File file;
    public String TOKEN;
    public final String PREFIX = "!";
    public void sendMessage(User user, String content) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }
    @Override
    public void onEnable() {
        assert TOKEN != null;

        getServer().getPluginManager().registerEvents(new Join(), this);
        Objects.requireNonNull(getCommand("link")).setExecutor(new linkCommand());
        Objects.requireNonNull(getCommand("unlink")).setExecutor(new unlinkCommand());
        Objects.requireNonNull(getCommand("linkupdate")).setExecutor(new linkupdateCommand());

        data.setup();
        data.save();
        file = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DiscordBot")).getDataFolder(), "config.yml");
        if (!(file.exists())) {
            saveResource("config.yml", false);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        config.setup();
        config.save();
        TOKEN = config.get().getString("discordtoken");
        assert TOKEN != null;
        bot = new Bot(TOKEN, PREFIX);
        bot.setBotThread(new ThreadSpigot(this));
        bot.setConsoleCommandManager(new CommandSpigotManager());
        bot.getBot().addEventListener(new Events());
        bot.addCommand(new ProgramCommand() {
            @Override
            protected boolean run(User user, MessageChannel messageChannel, Guild guild, String s, List<String> list) {
                messageChannel.sendMessage("Dette er en test").complete();
                System.out.println("[DiscordBot] Token virker ikke!");
                return false;
            }
            @Override
            public String getLabel() {
                return "Flex";
            }

            @Override
            public Permission getPermissionNeeded() {
                return Permission.MESSAGE_WRITE;
            }


        });


    }

    @Override
    public void onDisable() {
        bot.logout();
    }
}
