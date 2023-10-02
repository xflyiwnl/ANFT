package me.xflyiwnl.anft.command;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.gui.NFTGUI;
import me.xflyiwnl.anft.object.BufferedNFT;
import me.xflyiwnl.anft.object.Error;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import me.xflyiwnl.anft.request.GetRequest;
import me.xflyiwnl.anft.request.PostRequest;
import me.xflyiwnl.anft.util.NFTUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ANFTCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("nft.anft")) {
            new MessageSender(player)
                    .path("no-permission")
                    .run();
            return true;
        }

        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());

        if (args.length == 0) {
            new MessageSender(player)
                    .path("not-enough-args")
                    .run();
            return true;
        }

        nftCommand(player, playerNFT, args);

        return true;
    }

    public void nftCommand(Player player, PlayerNFT playerNFT, String[] args) {

        if (!playerNFT.isVerified()) {
            new MessageSender(player)
                    .path("not-verified")
                    .run();
            return;
        }

        new MessageSender(player)
                .path("nft-loading")
                .run();

        HttpResponse<String> response = new PostRequest()
                .url(ANFT.getInstance().getWebserver() + "/nft/" + args[0])
                .send();
        JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);

        if (json.get("isError").getAsBoolean()) {
            Error error = ANFT.getInstance().getError(json.get("errorId").getAsInt());
            new MessageSender(player)
                    .path("response-error")
                    .replace("code", String.valueOf(error.getCode()))
                    .replace("description", error.getDescription())
                    .run();
            return;
        }

        List<NFT> nfts = new ArrayList<NFT>();
        List<BufferedNFT> buffered = new ArrayList<BufferedNFT>();
        for (JsonElement element : json.get("nfts").getAsJsonArray()) {
            JsonObject obj = element.getAsJsonObject();
            String id = NFTUtil.generateId(obj.get("address").getAsString(), obj.get("tokenId").getAsInt());
            NFT nft = ANFT.getInstance().getNFT(id);
            if (nft != null) {
                nfts.add(nft);
                continue;
            }
            BufferedNFT bnft = new BufferedNFT(
                    obj.get("address").getAsString(),
                    obj.get("tokenId").getAsInt(),
                    obj.get("title").getAsString(),
                    obj.get("description").getAsString(),
                    obj.get("imageURL").getAsString()
            );
            buffered.add(bnft);
        }

        if (nfts.isEmpty() && buffered.isEmpty()) {
            new MessageSender(player)
                    .path("empty-nfts")
                    .run();
            return;
        }

        NFTGUI.showGUI(player, nfts, buffered);

    }

}
