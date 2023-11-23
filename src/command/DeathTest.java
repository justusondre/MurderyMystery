package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import arena.ArenaSpectator;

public class DeathTest implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}

		Player player = (Player) sender;

		ArenaSpectator arenaSpectator = new ArenaSpectator(player);
		arenaSpectator.makeInvisible();
		arenaSpectator.giveCompass();
		arenaSpectator.giveLeaveBed();

		return true;
	}
}
