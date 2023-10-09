package me.xflyiwnl.anft.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.Error;
import me.xflyiwnl.anft.object.PlayerNFT;
import me.xflyiwnl.anft.request.PostRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class VerifyCommand implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1)
            return Arrays.asList("signature");

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            new MessageSender(player)
                    .path("not-enough-args")
                    .run();
            return true;
        }

        if (!sender.hasPermission("nft.verify")) {
            new MessageSender(player)
                    .path("no-permission")
                    .run();
            return true;
        }

        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());

        if (playerNFT == null) {
            return true;
        }

        if (playerNFT.isVerified()) {
            new MessageSender(player)
                    .path("already-verified")
                    .run();
            return true;
        }

        verifyCommand(player, playerNFT, args);

        return true;
    }

    public void verifyCommand(Player player, PlayerNFT playerNFT, String[] args) {

        JSONObject json = new JSONObject();
        json.put("username", player.getName());
        json.put("signature", args[0]);

        HttpResponse<String> response = new PostRequest()
                .url(ANFT.getInstance().getWebserver() + "/verify")
                .body(json.toJSONString())
                .send();
        JsonObject result = new Gson().fromJson(response.body(), JsonObject.class);

        if (result.get("isError").getAsBoolean()) {
            Error error = ANFT.getInstance().getError(result.get("errorId").getAsInt());
            new MessageSender(player)
                    .path("response-error")
                    .replace("code", String.valueOf(error.getCode()))
                    .replace("description", error.getDescription())
                    .run();
        } else {
            playerNFT.setVerified(true);
            playerNFT.save();
            new MessageSender(player)
                    .path("verified")
                    .run();
        }

    }

}
