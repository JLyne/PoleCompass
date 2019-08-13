package nl.function1.polepass;

/*
 * PoleCompass 2.0.3 - Minecraft Bukkit/Spigot plugin by joppiesaus <job@function1.nl>
 * License: Public Domain. Do whatever you want. I'd love to hear what you're making with my code, though!
 * 
 * TODO:
 * GUI
 * CLICK DIRECTIONS
 * CLICK ENTITY TO FOLLOW?
 * 
 * CHANGELOG:
 * Hopefully finally fixed that stupid bug(your compass will reset no matter what now)
 * /compass reset no longer sets your compass to your bed if you have one
 * Added /compass bed, it points the compass to the bed
 * Added /setplayercompass bed
 */

import java.util.List;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.Metadatable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class Compass extends JavaPlugin implements Listener {
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	private void setMetadata(Metadatable object, String key, Object value, Plugin plugin) {
		object.setMetadata(key, new FixedMetadataValue(plugin, value));
	}
	
	private void removeMetadata(Metadatable object, String key, Plugin plugin) {
		object.removeMetadata(key, plugin);
	}

	private Object getMetadata(Metadatable object, String key, Plugin plugin) {
		List<MetadataValue> values = object.getMetadata(key);  
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == plugin) {
				return value.value();
			}
		}
		return null;
	}
	
	private void setPlayerDirection(Player player, String direction) {
		setMetadata(player,"compassdirection", direction, this);
	}

	private void removePlayerDirection(Player player) {
		removeMetadata(player, "compassdirection", this);
	}
		
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("compass")) {
			
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must be a player to set your own compass!");
				return false;
			}
			
			if (args.length < 1) {
				sender.sendMessage("No arguments specified! View \"compass help\" for help.");
				return false;
			}
			
			Player p = (Player) sender;
			
			switch (args[0].toLowerCase()) {
					
				case "help":
					p.sendMessage(ChatColor.GOLD + "/compass [direction]" + ChatColor.GRAY + 
							" - sets your compass direction to North, West, East or South.");
					p.sendMessage(ChatColor.GOLD + "/compass bed" + ChatColor.GRAY + " - sets your compass to your bed's location");
					p.sendMessage(ChatColor.GOLD + "To make your compass normal again, use /compass reset.");
					p.sendMessage(ChatColor.GOLD + "To modify someone else's compass, use /setplayercompass and then anything listed here.");
					return true;
					
				case "about":
					PluginDescriptionFile pdf = this.getDescription();
					String name = "PoleCompass";
					String des = pdf.getDescription();
					String ver = pdf.getVersion();
					String url = pdf.getWebsite();
					String aut = pdf.getAuthors().get(0);

					p.sendMessage(ChatColor.AQUA + name + " " + ver);
					p.sendMessage(ChatColor.GOLD + des);
					p.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.RESET + aut);
					p.sendMessage(ChatColor.GOLD + "URL: " + ChatColor.RESET + url);

					return true;

				case "n":
				case "north":
					p.sendMessage("Your compass has been set to the North");
					setPlayerDirection(p, "north");
					updateCompass(p);
					return true;
				
				case "e":
				case "east":
					p.sendMessage("Your compass has been set to the East");
					setPlayerDirection(p, "east");
					updateCompass(p);
					return true;
					
				case "s":
				case "south":
					p.sendMessage("Your compass has been set to the South");
					setPlayerDirection(p, "south");
					updateCompass(p);
					return true;
				
				case "w":
				case "west":
					p.sendMessage("Your compass has been set to the West");
					setPlayerDirection(p, "west");
					updateCompass(p);
					return true;
					
				case "bed":
					if (p.getBedSpawnLocation() != null) {
						p.setCompassTarget(p.getBedSpawnLocation());
						p.sendMessage("Your compass has been set to your bed's location");
						return true;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have a bed");
						return false;
					}
				
				case "r":
				case "reset":
				case "default":
					p.setCompassTarget(p.getWorld().getSpawnLocation());
					p.sendMessage("Your compass has been set to the world's spawnpoint");
					removePlayerDirection(p);
					return true;
					
				default:
					p.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not a valid direction.");
					return false;
			}
		} else if (cmd.getName().equalsIgnoreCase("setplayercompass")) {			
						
			if (args.length < 2) {
				sender.sendMessage("Syntax error: Too few arguments!");
				return false;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]); 
			
			if (target == null) {
				sender.sendMessage("Error: Player \"" + args[0] + "\" not found");
				return false;
			}
			
			
			switch (args[1].toLowerCase()) {
			
				case "help":
					// this is only hit when the first argument is a player
					sender.sendMessage("/" + cmd.getName().toLowerCase() + " <player> <compass command>. See /compass help");
					return true;
					
				case "n":
				case "north":
					sender.sendMessage(target.getName() + "'s compass has been set to the North");
					setPlayerDirection(target, "north");
					updateCompass(target);
					return true;
				
				case "e":
				case "east":
					sender.sendMessage(target.getName() + "'s compass has been set to the East");
					setPlayerDirection(target, "east");
					updateCompass(target);
					return true;
					
				case "s":
				case "south":
					sender.sendMessage(target.getName() + "'s compass has been set to the South");
					setPlayerDirection(target, "south");
					updateCompass(target);
					return true;
					
				case "w":
				case "west":
					sender.sendMessage(target.getName() + "'s compass has been set to the West");
					setPlayerDirection(target, "west");
					updateCompass(target);
					return true;
				
				case "bed":
					if (target.getBedSpawnLocation() != null) {
						target.setCompassTarget(target.getBedSpawnLocation());
						sender.sendMessage(target.getName() + "'s compass has been set to your bed's location");
						return true;
					} else {
						sender.sendMessage(target.getName() + " doesn't have a bed");
						return false;
					}
					
				case "r":
				case "reset":
				case "default":
					target.setCompassTarget(target.getWorld().getSpawnLocation());
					sender.sendMessage(target.getName() + "'s compass has been set to the world's spawnpoint");
					removePlayerDirection(target);
					return true;
					
				default:
					sender.sendMessage("Syntax error: \"" + args[1] + "\" is not a valid direction or command");
					return false;
			}
		}
		return false;
	}

	private void updateCompass(Player player) {
		updateCompass(player, player.getLocation());
	}

	private void updateCompass(Player player, Location location) {
		Object data = getMetadata(player, "compassdirection", this);
		WorldBorder border = player.getWorld().getWorldBorder();
		int width = (int) (border.getSize() / 2) - 1;

		if(data == null) {
			return;
		}

		switch((String) data) {
			case "north" :
				location.setZ(border.getCenter().getZ() - width);
				break;
			case "east" :
				location.setX(border.getCenter().getX() + width);
				break;
			case "south" :
				location.setZ(border.getCenter().getZ() + width);
				break;
			case "west" :
				location.setX(border.getCenter().getX() - width);
				break;
			default:
				return;
		}

		player.setCompassTarget(location);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location to = e.getTo().clone();

		updateCompass(player, to);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		Location to = e.getTo().clone();

		updateCompass(player, to);
	}
}
